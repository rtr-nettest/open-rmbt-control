-- EXTENSIONS --
CREATE EXTENSION IF NOT EXISTS hstore WITH SCHEMA public;
CREATE EXTENSION IF NOT EXISTS pgstattuple WITH SCHEMA public;
CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;
CREATE EXTENSION IF NOT EXISTS postgis_raster WITH SCHEMA public;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;

-- TYPES --
CREATE TYPE public.mobiletech AS ENUM (
    'unknown',
    '2G',
    '3G',
    '4G',
    'mixed'
    );
CREATE TYPE public.qostest AS ENUM (
    'website',
    'http_proxy',
    'non_transparent_proxy',
    'dns',
    'tcp',
    'udp',
    'traceroute',
    'voip',
    'traceroute_masked'
    );

CREATE TYPE public.cov_geo_location_assignment_type AS
(
    location public.geometry,
    accuracy numeric
);

-- FUNCTIONS --
CREATE FUNCTION public.within(public.geometry, public.geometry) RETURNS boolean
    LANGUAGE sql
    IMMUTABLE STRICT
AS
$_$
SELECT ST_Within($1, $2)
$_$;

CREATE FUNCTION public.rmbt_set_provider_from_as(_test_id bigint) RETURNS character varying
    LANGUAGE plpgsql
AS
$$
DECLARE
    _asn           bigint;
    _rdns          character varying;
    _provider_id   integer;
    _provider_name character varying;
BEGIN

    SELECT ap.provider_id,
           p.shortname
    FROM test t
             JOIN as2provider ap
                  ON t.public_ip_asn = ap.asn
                      AND
                     (ap.dns_part IS NULL OR
                      t.public_ip_rdns ILIKE ap.dns_part /*Case insensitive regexp, DJ per #235:*/ OR
                      t.public_ip_rdns ~* ap.dns_part)
             JOIN provider p
                  ON p.uid = ap.provider_id
    WHERE t.uid = _test_id
    ORDER BY dns_part IS NOT NULL DESC
    LIMIT 1
    INTO _provider_id, _provider_name;

    IF _provider_id IS NOT NULL THEN
        UPDATE test
        SET provider_id = _provider_id
        WHERE uid = _test_id;
        RETURN _provider_name;
    ELSE
        RETURN NULL;
    END IF;
END;
$$;

CREATE FUNCTION public.rmbt_get_next_test_slot(_test_id bigint) RETURNS integer
    LANGUAGE plpgsql
AS
$$
DECLARE
    _slot      integer;
    _count     integer;
    _server_id integer;
BEGIN
    SELECT server_id
    FROM test
    WHERE uid = _test_id
    INTO _server_id;
    _slot := EXTRACT(EPOCH FROM NOW())::int - 2;
    _count := 100;
    WHILE _count >= 5
        LOOP
            _slot := _slot + 1;
            SELECT COUNT(uid)
            FROM test
            WHERE test_slot = _slot
              AND server_id = _server_id
            INTO _count;
        END LOOP;
    UPDATE test
    SET test_slot = _slot
    WHERE uid = _test_id;
    RETURN _slot;
END;
$$;

CREATE FUNCTION public._final_median(anyarray) RETURNS double precision
    LANGUAGE sql
    IMMUTABLE
AS
$_$
WITH q AS
         (
             SELECT val
             FROM unnest($1) val
             WHERE VAL IS NOT NULL
             ORDER BY 1
         ),
     cnt AS
         (
             SELECT COUNT(*) AS c
             FROM q
         )
SELECT AVG(val) ::float8
FROM (
         SELECT val
         FROM q
         LIMIT 2 - MOD((SELECT c FROM cnt), 2) OFFSET GREATEST(CEIL((SELECT c FROM cnt) / 2.0) - 1, 0)
     ) q2;
$_$;

CREATE AGGREGATE public.median(anyelement) (
    SFUNC = array_append,
    STYPE = anyarray,
    INITCOND = '{}',
    FINALFUNC = public._final_median
    );

CREATE FUNCTION public.bdmpolyfromtext(text, integer) RETURNS public.geometry
    LANGUAGE plpgsql
    IMMUTABLE STRICT
AS
$_$
DECLARE
    geomtext alias for $1;
    srid alias for $2;
    mline geometry;
    geom  geometry;
BEGIN
    mline := ST_MultiLineStringFromText(geomtext, srid);

    IF mline IS NULL
    THEN
        RAISE EXCEPTION 'Input is not a MultiLinestring';
    END IF;

    geom := ST_Multi(ST_BuildArea(mline));

    RETURN geom;
END;
$_$;

CREATE FUNCTION public.interpolate_radio_signal_location(in_open_test_uuid uuid)
    RETURNS TABLE
            (
                out_last_signal_uuid       uuid,
                out_last_radio_signal_uuid uuid,
                out_last_geo_location_uuid uuid,
                out_open_test_uuid         uuid,
                out_interpolated_location  public.geometry,
                out_time                   timestamp with time zone
            )
    LANGUAGE plpgsql
AS
$$
    -- USAGE:
-- # for data migration:
--   insert into radio_signal_location (last_signal_uuid, last_radio_signal_uuid, last_geo_location_uuid, open_test_uuid, interpolated_location, time) select (interpolate_radio_signal_location (open_test_uuid)).* from test where open_test_uuid = '0583309a-1c36-4048-90d1-a777be8ef4fd' order by uid asc;
-- # with single open_test_uuid (e.g. in trigger or server code):
--   insert into radio_signal_location (last_signal_uuid, last_radio_signal_uuid, last_geo_location_uuid, open_test_uuid, interpolated_location, time) select (interpolate_radio_signal_location ('0583309a-1c36-4048-90d1-a777be8ef4fd')).* ;
-- # show only (e.g for debugging):
--   select (interpolate_radio_signal_location ('0583309a-1c36-4048-90d1-a777be8ef4fd')).*;
-- # generate test data:
--   select * from (
--   (select * from interpolate_radio_signal_location ('0583309a-1c36-4048-90d1-a777be8ef4fd') left join signal on signal_uuid=out_last_signal_uuid left join radio_signal on radio_signal_uuid = out_last_radio_signal_uuid left join geo_location on geo_location_uuid= out_last_geo_location_uuid)
--   union
--   (select * from interpolate_radio_signal_location ('d1fe403a-14b5-41a7-946a-251b1a5f49ce') left join signal on signal_uuid=out_last_signal_uuid left join radio_signal on radio_signal_uuid = out_last_radio_signal_uuid left join geo_location on geo_location_uuid= out_last_geo_location_uuid)
--   ) as foo order by out_open_test_uuid, out_time;
declare
    signal_rows_found       bool;
    test_time               timestamptz;
    MAX_INACCURACY constant double precision := 2000.0;
    MIN_TIME_NS    constant bigint           := -1 * 60 * 1000000000::bigint; -- -1 minute  in nanoseconds
    MAX_TIME_NS    constant bigint           := 2 * 60 * 1000000000::bigint; --  2 minutes in nanoseconds
BEGIN
    --raise notice '%',  in_open_test_uuid; --debug, can be commented out
    SELECT test.time INTO test_time FROM test WHERE open_test_uuid = in_open_test_uuid;
    if not found then
        return; -- no timestamp found for this open_test_uuid, return nothing and exit
    end if;

    CREATE TEMP TABLE if not exists temp_radio_signal_location
    (
        -- inputs:
        signal_uuid            uuid,
        radio_signal_uuid      uuid,
        geo_location_uuid      uuid,
        open_test_uuid         uuid,
        time_ns                bigint,      -- this is also debug or auxillary output for. e.g. sorting purposes - equals to a coalesce of all time_ns from geo_location, signal and radio_signal tables
        signal_strength        int,         -- a coalesce of 2G/3G/4G/WLAN signal strengths
        location               geometry,
        -- intermediate internal results:
        time_ns_a              bigint,      --time of first interpolation point
        location_a             geometry,    --and its location
        time_ns_b              bigint,      --time of last interpolation point
        location_b             geometry,    --and its location
        -- outputs:
        last_signal_uuid       uuid,        --last non-null signal_uuid
        last_radio_signal_uuid uuid,        --last non-null radio_signal_uuid
        last_geo_location_uuid uuid,        --last non-null geo_location_uuid
        interpolated_location  geometry,    --points from geo_location and interpolated points for signal rows
        last_signal_strength   int,         --last non-null signal strength
        "time"                 timestamptz, --timestamp with accuracy of microseconds, equals to test.time + time_ns
        -- internal:
        uid                    bigserial
    ) on commit drop;

    truncate table temp_radio_signal_location;

    insert into temp_radio_signal_location (/*signal_uuid=NULL, radio_signal_uuid=NULL,*/ geo_location_uuid,
                                                                                          open_test_uuid, time_ns,
                                                                                          location/*, signal_strength*/)
    select geo_location_uuid, geo_location.open_test_uuid, time_ns, location
    from geo_location
    where geo_location.open_test_uuid = in_open_test_uuid
      and location is not null
      and accuracy <= MAX_INACCURACY -- consider only data with good accuracy
      AND time_ns BETWEEN MIN_TIME_NS AND MAX_TIME_NS; -- consider only plausible time_ns values
    if not found then
        return; -- no location data found for this open_test_uuid, return nothing and exit
    end if;

    insert into temp_radio_signal_location (signal_uuid, /*radio_signal_uuid=NULL, geo_location_uuid=NULL,*/
                                            open_test_uuid, time_ns /*,location=NULL*/, signal_strength)
    select signal_uuid,
           signal.open_test_uuid,
           time_ns,
           lte_rsrp -- temporary only 4G, should be coalesce - TODO tbd
    from signal
    where signal.open_test_uuid = in_open_test_uuid
      AND time_ns BETWEEN MIN_TIME_NS AND MAX_TIME_NS; -- consider only plausible time_ns values
    signal_rows_found := found;

    insert into temp_radio_signal_location (/*signal_uuid=NULL,*/ radio_signal_uuid, /*geo_location_uuid=NULL,*/
                                                                  open_test_uuid, time_ns /*,location=NULL*/,
                                                                  signal_strength)
    select radio_signal_uuid,
           radio_signal.open_test_uuid,
           time_ns,
           lte_rsrp -- temporary only 4G, should be coalesce - TODO tbd
    from radio_signal
             join radio_cell
                  on radio_signal.cell_uuid = radio_cell.uuid and registered and active --for active cells only and the active SIM in case of dual SIMs
    where radio_signal.open_test_uuid = in_open_test_uuid
      AND time_ns BETWEEN MIN_TIME_NS AND MAX_TIME_NS; -- consider only plausible time_ns values
    if (not signal_rows_found and not /*radio_signal_rows_*/found) then -- or (signal_rows_found and /*radio_signal_rows_*/found) == additionally both signal and radio_signal found - would be more restrictive
        return; -- no signal data found, return nothing and exit
    end if;

-- do first the time ascending handling
    declare -- default values are NULL
        cursor_asc cursor for select * from temp_radio_signal_location order by time_ns asc for update;
        row               record;
        last_oldsig_uuid  uuid;
        last_sig_uuid     uuid;
        last_geo_uuid     uuid;
        last_time_ns_a    bigint;
        last_location_a   geometry;
        last_sig_strength int;
    begin
        for row in cursor_asc
            LOOP
                <<get_missing_values_asc>>
                BEGIN
                    if row.signal_uuid is not null and row.signal_uuid is distinct from last_oldsig_uuid then
                        last_oldsig_uuid := row.signal_uuid;
                    end if;
                    if row.radio_signal_uuid is not null and row.radio_signal_uuid is distinct from last_sig_uuid then
                        last_sig_uuid := row.radio_signal_uuid;
                    end if;
                    if row.geo_location_uuid is not null and row.geo_location_uuid is distinct from last_geo_uuid then
                        last_geo_uuid := row.geo_location_uuid;
                    end if;
                    if row.time_ns is not null and row.time_ns is distinct from last_time_ns_a and
                       row.geo_location_uuid is not null then
                        last_time_ns_a := row.time_ns;
                    end if;
                    if row.location is not null and row.location is distinct from last_location_a then
                        last_location_a := row.location;
                    end if;
                    if row.signal_strength is not null and row.signal_strength is distinct from last_sig_strength then
                        last_sig_strength := row.signal_strength;
                    end if;
                END get_missing_values_asc;
                <<populate_missing_values_asc>>
                BEGIN
                    if last_oldsig_uuid is not null then -- assume last old signal
                        update temp_radio_signal_location
                        set last_signal_uuid = last_oldsig_uuid
                        where current of cursor_asc;
                    end if;
                    if last_sig_uuid is not null then -- assume last signal
                        update temp_radio_signal_location
                        set last_radio_signal_uuid = last_sig_uuid
                        where current of cursor_asc;
                    end if;
                    if last_geo_uuid is not null then -- assume last location
                        update temp_radio_signal_location
                        set last_geo_location_uuid = last_geo_uuid
                        where current of cursor_asc;
                    end if;
                    if last_time_ns_a is not null then -- fill last time_ns_a
                        update temp_radio_signal_location set time_ns_a = last_time_ns_a where current of cursor_asc;
                    end if;
                    if last_location_a is not null then -- assume last location
                        update temp_radio_signal_location set location_a = last_location_a where current of cursor_asc;
                    end if;
                    if last_sig_strength is not null then -- assume last signal_strength
                        update temp_radio_signal_location
                        set last_signal_strength = last_sig_strength
                        where current of cursor_asc;
                    end if;
                    update temp_radio_signal_location
                    set time = test_time + ((ROW.time_ns / 1000.0) * INTERVAL '1 microsecond')
                    where current of cursor_asc; -- timestamp has accuracy of microseconds
                END populate_missing_values_asc;
            END LOOP;
    end;

-- secondly do the time descending handling
    declare -- default values are NULL
        cursor_desc cursor for select * from temp_radio_signal_location order by time_ns desc for update;
        row                record;
        last_time_ns_b     bigint;
        last_location_b    geometry;
        interpolated_line  geometry;
        interpolated_point geometry;
        fraction           double precision;
    begin
        for row in cursor_desc
            LOOP
                <<get_missing_values_desc>>
                BEGIN
                    if row.time_ns is not null and row.time_ns is distinct from last_time_ns_b and
                       row.geo_location_uuid is not null then
                        last_time_ns_b := row.time_ns;
                    end if;
                    if row.location is not null and row.location is distinct from last_location_b then
                        last_location_b := row.location;
                    end if;
                END get_missing_values_desc;
                <<populate_missing_values_desc>>
                BEGIN
                    if last_time_ns_b is not null then -- fill next time_ns_b
                        update temp_radio_signal_location set time_ns_b = last_time_ns_b where current of cursor_desc;
                    end if;
                    if last_location_b is not null then -- assume next location
                        update temp_radio_signal_location set location_b = last_location_b where current of cursor_desc;
                    end if;
                END populate_missing_values_desc;
            END LOOP;
    end;

-- do the interpolation
    declare -- default values are NULL
        cursor_asc cursor for select * from temp_radio_signal_location order by time_ns asc for update;
        row                record;
        interpolated_line  geometry;
        interpolated_point geometry;
        fraction           double precision;
    begin
        for row in cursor_asc
            LOOP
                <<populate_interpolated_values>>
                BEGIN
                    if row.location_a is not null then
                        if row.location_b is not null then
                            interpolated_line := ST_makeline(row.location_a, row.location_b);
                            if row.time_ns_a <> row.time_ns_b then
                                fraction := (row.time_ns - row.time_ns_a)::double precision /
                                            (row.time_ns_b - row.time_ns_a)::double precision;
                            else
                                fraction := 0; -- it is one point only
                            end if;
                            interpolated_point := ST_lineinterpolatepoint(interpolated_line, fraction);
                            update temp_radio_signal_location
                            set interpolated_location = interpolated_point /*, provider = 'interpolated'*/
                            where current of cursor_asc;
                        else --row.location_b is null, take the last row.location_a
                            update temp_radio_signal_location
                            set interpolated_location = row.location_a /*, provider = 'last_position'*/
                            where current of cursor_asc;
                        end if;
                    end if;
                END populate_interpolated_values;
            END LOOP;
    end;
    return query select last_signal_uuid,
                        last_radio_signal_uuid,
                        last_geo_location_uuid,
                        open_test_uuid,
                        interpolated_location,
                        "time"
                 from temp_radio_signal_location
                 where (((last_signal_uuid IS NOT NULL) AND (last_radio_signal_uuid IS NULL)) OR
                        ((last_signal_uuid IS NULL) AND (last_radio_signal_uuid IS NOT NULL))) --only return rows with either signal or radio signal according to constraint xor_signals_not_null
                   and (((last_geo_location_uuid IS NOT NULL) AND
                         (interpolated_location IS NOT NULL)))                                 -- and with location according to constraint location_not_null_for_uuid
                   AND "time" IS NOT NULL                                                      -- and with a timestamp
                 order by time_ns asc;
END;
$$;

CREATE FUNCTION public.trigger_test() RETURNS trigger
    LANGUAGE plpgsql
AS
$$
DECLARE
    _country_location      varchar;
    _tmp_uuid              uuid;
    _tmp_uid               integer;
    _tmp_time              timestamp;
    _mcc_sim               VARCHAR;
    _mcc_net               VARCHAR;
    -- limit for accurate location (differs from map where 2000m and 10000m are thresholds)
    _min_accuracy CONSTANT integer := 3000;
    _tmp_location          geometry;

BEGIN
    -- calc logarithmic speed downlink
    IF ((TG_OP = 'INSERT' OR NEW.speed_download IS DISTINCT FROM OLD.speed_download) AND NEW.speed_download > 0) THEN
        NEW.speed_download_log = (log(NEW.speed_download::double precision / 10)) / 4;
    END IF;
    -- calc logarithmic speed uplink
    IF ((TG_OP = 'INSERT' OR NEW.speed_upload IS DISTINCT FROM OLD.speed_upload) AND NEW.speed_upload > 0) THEN
        NEW.speed_upload_log = (log(NEW.speed_upload::double precision / 10)) / 4;
    END IF;
    -- calc logarithmic ping
    -- ping_shortest is obsolete
    IF ((TG_OP = 'INSERT' OR NEW.ping_shortest IS DISTINCT FROM OLD.ping_shortest) AND NEW.ping_shortest > 0) THEN
        NEW.ping_shortest_log = (log(NEW.ping_shortest::double precision / 1000000)) / 3;
        -- ping_median (from ping table)
        SELECT INTO NEW.ping_median floor(median(coalesce(value_server, value))) FROM ping WHERE NEW.uid = test_id;
        NEW.ping_median_log = (log(NEW.ping_median::double precision / 1000000)) / 3;
        IF (NEW.ping_median IS NULL) THEN
            NEW.ping_median = NEW.ping_shortest;
        END IF;
    END IF;

    IF ((NEW.location IS NOT NULL) AND (NEW.geo_location_uuid IS NOT NULL)) THEN
        UPDATE test_location
        SET geo_location_uuid = NEW.geo_location_uuid,
            location          = NEW.location,
            geo_lat           = NEW.geo_lat,
            geo_long          = NEW.geo_long,
            geo_accuracy      = NEW.geo_accuracy,
            geo_provider      = NEW.geo_provider
        WHERE open_test_uuid = NEW.open_test_uuid;
        IF NOT FOUND THEN
            INSERT INTO test_location (geo_location_uuid, open_test_uuid, location, geo_lat,
                                       geo_long, geo_accuracy, geo_provider)
            VALUES (NEW.geo_location_uuid, NEW.open_test_uuid, NEW.location, NEW.geo_lat,
                    NEW.geo_long, NEW.geo_accuracy, NEW.geo_provider);
        END IF;
    END IF;

    select into _country_location country_location from test_location tl where tl.open_test_uuid = NEW.open_test_uuid;
    -- end of location post processing
    -- set roaming_type /mobile_provider_id
    IF (TG_OP = 'INSERT'
        OR NEW.network_sim_operator IS DISTINCT FROM OLD.network_sim_operator
        OR NEW.network_operator IS DISTINCT FROM OLD.network_operator
        OR NEW.time IS DISTINCT FROM OLD.time
        ) THEN

        IF (NEW.network_sim_operator IS NULL OR NEW.network_operator IS NULL) THEN
            NEW.roaming_type = NULL;
        ELSE
            IF (NEW.network_sim_operator = NEW.network_operator) THEN
                NEW.roaming_type = 0; -- no roaming
            ELSE
                _mcc_sim := split_part(NEW.network_sim_operator, '-', 1);
                _mcc_net := split_part(NEW.network_operator, '-', 1);
                -- TODO not correct for India - #1050 (old)
                IF (_mcc_sim = _mcc_net) THEN
                    NEW.roaming_type = 1; -- national roaming
                ELSE
                    NEW.roaming_type = 2; -- international roaming
                END IF;
            END IF;
        END IF;

        -- set mobile_provider_id
        -- do not set if outside Austria
        IF ((NEW.roaming_type IS NULL AND _country_location IS DISTINCT FROM 'AT') OR
            NEW.roaming_type IS NOT DISTINCT FROM 2) THEN -- not for foreign networks #659
            NEW.mobile_provider_id = NULL;
        ELSE
            SELECT INTO NEW.mobile_provider_id provider_id
            FROM mccmnc2provider
            WHERE mcc_mnc_sim = NEW.network_sim_operator
              AND (valid_from IS NULL OR valid_from <= NEW.time)
              AND (valid_to IS NULL OR valid_to >= NEW.time)
              AND (mcc_mnc_network IS NULL OR mcc_mnc_network = NEW.network_operator)
            ORDER BY mcc_mnc_network NULLS LAST
            LIMIT 1;
        END IF;
    END IF;
    -- end of network_sim_operator

    -- set mobile_provider_id (again?)
    IF ((TG_OP = 'UPDATE' AND OLD.STATUS = 'STARTED' AND NEW.STATUS = 'FINISHED')
        AND (NEW.time > (now() - INTERVAL '5 minutes'))) THEN -- update only new entries, skip old entries
        IF (NEW.network_operator is not NULL) THEN
            SELECT INTO NEW.mobile_network_id COALESCE(n.mapped_uid, n.uid)
            FROM mccmnc2name n
            WHERE NEW.network_operator = n.mccmnc
              AND (n.valid_from is null OR n.valid_from <= NEW.time)
              AND (n.valid_to is null or n.valid_to >= NEW.time)
              AND use_for_network = TRUE
            ORDER BY n.uid NULLS LAST
            LIMIT 1;
        END IF;

        -- set network_sim_operator
        IF (NEW.network_sim_operator is not NULL) THEN
            SELECT INTO NEW.mobile_sim_id COALESCE(n.mapped_uid, n.uid)
            FROM mccmnc2name n
            WHERE NEW.network_sim_operator = n.mccmnc
              AND (n.valid_from is null OR n.valid_from <= NEW.time)
              AND (n.valid_to is null or n.valid_to >= NEW.time)
              AND (NEW.network_sim_operator = n.mcc_mnc_network_mapping OR n.mcc_mnc_network_mapping is NULL)
              AND use_for_sim = TRUE
            ORDER BY n.uid NULLS LAST
            LIMIT 1;
        END IF;

    END IF;
    -- end of mobile_provider_id (again?)

    -- ignore automated tests from CLI
    IF ((TG_OP = 'UPDATE') AND (NEW.time > (now() - INTERVAL '5 minutes')) AND NEW.network_type = 97/*CLI*/ AND
        NEW.deleted = FALSE) THEN
        NEW.deleted = TRUE;
        NEW.comment = 'Exclude CLI per #211';
    END IF;

    -- ignore location of Samsung Galaxy Note 3
    -- TODO: should be done before post-processing of location, obsolete
    IF ((TG_OP = 'UPDATE' AND OLD.STATUS = 'STARTED' AND NEW.STATUS = 'FINISHED')
        AND (NEW.time > (now() - INTERVAL '5 minutes'))
        AND NEW.model = 'SM-N9005'
        AND NEW.geo_provider = 'network') THEN
        NEW.geo_accuracy = 99999;
    END IF;

    -- plausibility check on distance from previous test
    IF ((TG_OP = 'UPDATE' AND OLD.STATUS = 'STARTED' AND NEW.STATUS = 'FINISHED')
        AND (NEW.time > (now() - INTERVAL '5 minutes'))
        AND NEW.geo_accuracy is not null
        AND NEW.geo_accuracy <= 10000) THEN

        SELECT INTO _tmp_uid uid
        FROM test
        WHERE client_id = NEW.client_id
          AND time < NEW.time -- #668 allow only past tests
          AND (NEW.time - INTERVAL '24 hours' < time)
          AND geo_accuracy is not null
          AND geo_accuracy <= 10000
        ORDER BY uid DESC
        LIMIT 1;

        IF _tmp_uid is not null THEN
            SELECT INTO NEW.dist_prev ST_Distance(ST_Transform(t.location, 4326)::geography,
                                                  ST_Transform(NEW.location, 4326)::geography) -- #668 improve geo precision for the calculation of the distance (in meters) to a previous test
            FROM test t
            WHERE uid = _tmp_uid;
            IF NEW.dist_prev is not null THEN
                SELECT INTO _tmp_time time
                FROM test t
                WHERE uid = _tmp_uid;
                NEW.speed_prev = NEW.dist_prev / GREATEST(0.000001, EXTRACT(EPOCH FROM (NEW.time - _tmp_time))) *
                                 3.6; -- #668 speed in km/h and don't allow division by zero
            END IF;
        END IF;
    END IF;
    -- end of plausibility check

    -- set network_type
    IF ((NEW.network_type > 0) AND (NEW.time > (now() - INTERVAL '5 minutes'))) THEN
        SELECT INTO NEW.network_group_name group_name
        FROM network_type
        WHERE uid = NEW.network_type
        LIMIT 1;
        SELECT INTO NEW.network_group_type type
        FROM network_type
        WHERE uid = NEW.network_type
        LIMIT 1;
    END IF;

    -- set open_uuid
    -- #759 Finalisation loop mode
    IF (TG_OP = 'UPDATE' AND OLD.status = 'STARTED' AND NEW.status = 'FINISHED')
        AND (NEW.time > (now() - INTERVAL '5 minutes')) -- update only new entries, skip old entries
    THEN
        _tmp_uuid = NULL;
        _tmp_location = NULL;
        SELECT open_uuid, location
        INTO _tmp_uuid, _tmp_location
        FROM test -- find the open_uuid and location
        WHERE (NEW.client_id = client_id)                                      -- of the current client
          AND (NEW.time > time)                                                -- thereby skipping the current entry (was: OLD.uid != uid)
          AND status = 'FINISHED'                                              -- of successful tests
          AND (NEW.time - time) < '4 hours'::INTERVAL                          -- within last 4 hours
          AND (NEW.time::DATE = time::DATE)                                    -- on the same day
          AND (NEW.network_group_type IS NOT DISTINCT FROM network_group_type) -- of the same technology (i.e. MOBILE, WLAN, LAN, CLI, NULL) - was: network_group_name
          AND (NEW.public_ip_asn IS NOT DISTINCT FROM public_ip_asn)           -- and of the same operator (including NULL)
        ORDER BY time DESC
        LIMIT 1; -- get only the latest test

        IF
                (_tmp_uuid IS NULL) -- previous query doesn't return any test
                OR -- OR
                (NEW.location IS NOT NULL AND _tmp_location IS NOT NULL
                    AND ST_Distance(ST_Transform(NEW.location, 4326),
                                    ST_Transform(_tmp_location, 4326)::geography)
                     >= 100) -- the distance to the last test >= 100m
        THEN
            _tmp_uuid = uuid_generate_v4(); --generate new open_uuid
        END IF;
        NEW.open_uuid = _tmp_uuid;
    END IF;
    --end of set open_uuid

    -- plausibility check on movement during test
    IF (TG_OP = 'UPDATE' AND OLD.STATUS = 'STARTED' AND NEW.STATUS = 'FINISHED') THEN
        NEW.timestamp = now();

        SELECT INTO NEW.location_max_distance round(
                                                      ST_Distance( -- #668 improve geo precision for the calculation of the diagonal length (in meters) of the bounding box of one test
                                                              ST_SetSRID(ST_MakePoint(
                                                                                 ST_XMin(ST_Extent(ST_Transform(location, 4326))),
                                                                                 ST_YMin(ST_Extent(ST_Transform(location, 4326)))),
                                                                         4326)::geography,
                                                              ST_SetSRID(ST_MakePoint(
                                                                                 ST_XMax(ST_Extent(ST_Transform(location, 4326))),
                                                                                 ST_YMax(ST_Extent(ST_Transform(location, 4326)))),
                                                                         4326)::geography)
                                                  )
        FROM geo_location
        WHERE test_id = NEW.uid;
    END IF;

    -- plausibility check - Austrian networks outside Austria are not allowed #272
    IF ((NEW.time > (now() - INTERVAL '5 minutes')) -- update only new entries, skip old entries
        AND (
            (NEW.network_operator ILIKE '232%') -- test with Austrian mobile network operator
            )
        AND ST_Distance(
                    ST_Transform(NEW.location, 4326), -- location of the test
                    ST_Transform((select geom from ne_10m_admin_0_countries where sovereignt ilike 'Austria'),
                                 4326)::geography -- Austria shape
                ) > 35000 -- location is more than 35 km outside of the Austria shape
        ) -- if
    THEN
        NEW.status = 'UPDATE ERROR'; NEW.comment = 'Automatic update error due to invalid location per #272';
    END IF;

    -- ignore provider_id if location outside Austria
    IF ((NEW.time > (now() - INTERVAL '5 minutes')) -- update only new entries, skip old entries
        AND NEW.network_type in (97, 98, 99, 106, 107) -- CLI, LAN, WLAN, Ethernet, Bluetooth
        AND (
            (NEW.provider_id IS NOT NULL) -- Austrian operator
            )
        AND ST_Distance(
                    ST_Transform(NEW.location, 4326), -- location of the test
                    ST_Transform((select geom from ne_10m_admin_0_countries where sovereignt ilike 'Austria'),
                                 4326)::geography -- Austria shape
                ) > 3000 -- location is outside of the Austria shape with a tolerance of +3 km
        ) -- if
    THEN
        NEW.provider_id = NULL;
        NEW.comment = concat(
                'No provider_id outside of Austria for e.g. VPNs, HotSpots, manual location/geocoder etc. per #664; ',
                NEW.comment, NULLIF(OLD.comment, NEW.comment));
    END IF;

    -- ignore tests with model name 'unknown'
    -- TODO justified/relevant?
    IF ((NEW.time > (now() - INTERVAL '5 minutes')) -- update only new entries, skip old entries
        AND (NEW.model = 'unknown') -- model is 'unknown'
        )
    THEN
        NEW.status = 'UPDATE ERROR'; NEW.comment = 'Automatic update error due to unknown model per #356';
    END IF;

    -- implement test pinning (tests excluded from statistics)
    IF ((TG_OP = 'UPDATE' AND OLD.STATUS = 'STARTED' AND NEW.STATUS = 'FINISHED')
        AND (NEW.time > (now() - INTERVAL '5 minutes'))) -- update only new entries, skip old entries
    THEN -- Returns the uid of a previous similar test, otherwise -1. Also IF similar_test_uid = -1 then pinned = TRUE ELSE pinned=FALSE. Column similar_test_uid has a default value NULL, meaning the evaluation for similar test(s) wasn't performed yet.
        SELECT INTO NEW.similar_test_uid uid
        FROM test
        WHERE (similar_test_uid = -1 OR similar_test_uid IS NULL) -- consider only unsimilar or not yet evaluated tests
          AND NEW.open_uuid = open_uuid                           -- with the same open_uuid
          AND NEW.time > time
          AND (NEW.time - time) < '4 hours'::INTERVAL             -- in the last 4 hours
          AND NEW.public_ip_asn = public_ip_asn                   -- from the same network based on AS
          AND NEW.network_type = network_type                     -- of the same network_type
          AND CASE
                  WHEN (NEW.location IS NOT NULL AND NEW.geo_accuracy IS NOT NULL AND NEW.geo_accuracy < 2000
                      AND location IS NOT NULL AND geo_accuracy IS NOT NULL AND geo_accuracy < 2000)
                      THEN ST_Distance(
                                   ST_Transform(NEW.location, 4326),
                                   ST_Transform(location, 4326)::geography
                               ) < GREATEST(100, NEW.geo_accuracy) -- either within a radius of 100 m
                  ELSE TRUE -- or if no or inaccurate location, only other criteria count
            END
        ORDER BY time DESC -- consider the last, most previous test
        LIMIT 1;
        IF NEW.similar_test_uid IS NULL -- no similar test found
        then
            NEW.similar_test_uid = -1; -- indicate that we have searched for a similar test but nothing found
            NEW.pinned = TRUE; -- and set the pinned for the statistics
        ELSE
            NEW.pinned = FALSE; -- else in similar_test_uid the uid of a previous test is stored so the test shouldn't go into the statistics
        END IF;
    END IF;
    -- end test pinning

    --populate radio_signal_location for location and signal interpolation
    IF (TG_OP = 'UPDATE' AND OLD.STATUS = 'STARTED' AND NEW.STATUS = 'FINISHED') then
        insert into radio_signal_location (last_signal_uuid, last_radio_signal_uuid, last_geo_location_uuid,
                                           open_test_uuid, interpolated_location, "time")
        select (interpolate_radio_signal_location(new.open_test_uuid)).*;
    end if; --location and signal interpolation
    RETURN NEW;
END;
$$;

CREATE FUNCTION public.trigger_test_location() RETURNS trigger
    LANGUAGE plpgsql
AS
$$
DECLARE

    _min_accuracy CONSTANT integer := 2000;
BEGIN
    -- post process if location is updated
    IF (TG_OP = 'INSERT' OR NEW.location IS DISTINCT FROM OLD.location) THEN
        -- ignore if location is not accurate
        IF (NEW.location IS NULL OR NEW.geo_accuracy > _min_accuracy) THEN
        ELSE
            -- add dsr id (Austrian dauersiedlungsraum)
            SELECT dsr.id::INTEGER
            INTO NEW.settlement_type
            FROM dsr
            WHERE within(st_transform(NEW.location, 31287), dsr.geom)
            LIMIT 1;

            -- add Austrian streets and railway (FRC 1,2,3,4,20,21)
            select q1.link_id,
                   linknet_names.link_name,
                   round(ST_DistanceSphere(q1.geom, ST_Transform(NEW.location, 4326))) link_distance,
                   q1.frc,
                   q1.edge_id
            into NEW.link_id, NEW.link_name, NEW.link_distance, NEW.frc, NEW.edge_id
            from (SELECT linknet.link_id,
                         linknet.geom,
                         linknet.frc,
                         linknet.edge_id
                  FROM linknet
                       -- optimize search by using boundary box on geometry
                       -- bbox=ST_Expand(geom,0.01);
                  WHERE ST_Transform(NEW.location, 4326) && linknet.bbox

                  ORDER BY ST_Distance(linknet.geom,
                                       ST_Transform(NEW.location,
                                                    4326)) ASC
                  LIMIT 1) as q1
                     LEFT JOIN linknet_names ON q1.link_id = linknet_names.link_id
            WHERE ST_DistanceSphere(q1.geom,
                                    ST_Transform(NEW.location, 4326)) <=
                  10.0
              -- only if accuracy 10m or better
              AND NEW.geo_accuracy < 10.0
              -- only if GPS available
              AND (NEW.geo_provider = '' OR -- iOS (up to now)
                   NEW.geo_provider IS NULL OR -- iOS (planned)
                   NEW.geo_provider = 'gps');

            -- add BEV gkz (community identifier) and kg_nr (settlement identifier)
            BEGIN
                SELECT bev.gkz::INTEGER,
                       bev.kg_nr_int
                INTO NEW.gkz_bev, NEW.kg_nr_bev
                FROM bev_vgd bev
                WHERE st_transform(NEW.location, 31287) && bev.bbox
                  AND within(st_transform(NEW.location, 31287), bev.geom)
                LIMIT 1;
            EXCEPTION
                WHEN undefined_table THEN
                    -- just return NULL, but ignore missing database
                    RAISE NOTICE '%', SQLERRM;
            END;

            -- add SA gkz (community identifier)
            BEGIN
                SELECT sa.id::INTEGER
                INTO NEW.gkz_sa
                FROM statistik_austria_gem sa
                WHERE st_transform(NEW.location, 31287) && sa.bbox
                  AND within(st_transform(NEW.location, 31287), sa.geom)
                LIMIT 1;
            EXCEPTION
                WHEN undefined_table THEN
                    -- just return NULL, but ignore missing database
                    RAISE NOTICE '%', SQLERRM;
            END;
            -- add land cover id
            SELECT clc.code_12::INTEGER
            INTO NEW.land_cover
            FROM clc12_all_oesterreich clc
                 -- use boundary box to increase performance
                 -- bbox=ST_EXPAND(geom,0.01)
            WHERE st_transform(NEW.location, 3035) && clc.bbox
              AND within(st_transform(NEW.location, 3035), clc.geom)
            LIMIT 1;
            -- add country code (country_location)
            IF (NEW.gkz_bev IS NOT NULL) THEN -- #659(mod): Austrian communities are more accurate/up-to-date for AT than ne_50_admin_0_countries
                NEW.country_location = 'AT';
            ELSE
                SELECT INTO NEW.country_location iso_a2
                FROM ne_10m_admin_0_countries
                WHERE NEW.location && geom
                  AND Within(NEW.location, geom)
                  AND char_length(iso_a2) = 2
                  AND iso_a2 IS DISTINCT FROM 'AT' -- #659: because ne_50_admin_0_countries is inaccurate, do not allow to return 'AT'
                LIMIT 1;
            END IF;
            -- add altitude level from digital terrain model (DTM) #1203
            SELECT INTO NEW.dtm_level ST_Value(rast,
                                               (ST_Transform(ST_SetSRID(ST_MakePoint(NEW.geo_long, NEW.geo_lat), 4326),
                                                             31287)))
            FROM dhm
            WHERE st_intersects(rast, (ST_Transform(ST_SetSRID(ST_MakePoint(NEW.geo_long, NEW.geo_lat), 4326), 31287)));
        END IF;
    END IF;
    -- end of location post processing
    RETURN NEW;
END;
$$;

-- TABLES --

CREATE TABLE public.client_type
(
    uid  integer PRIMARY KEY NOT NULL,
    name character varying(200)
);

CREATE TABLE public.sync_group
(
    uid    integer PRIMARY KEY      NOT NULL,
    tstamp timestamp with time zone NOT NULL
);

CREATE TABLE public.provider
(
    uid        integer PRIMARY KEY NOT NULL,
    name       character varying(200),
    mcc_mnc    character varying(10),
    shortname  character varying(100),
    map_filter boolean             NOT NULL
);

CREATE TABLE public.client
(
    uid                                     bigint PRIMARY KEY    NOT NULL,
    uuid                                    uuid UNIQUE           NOT NULL,
    client_type_id                          integer,
    "time"                                  timestamp with time zone,
    sync_group_id                           integer,
    sync_code                               character varying(12) UNIQUE,
    terms_and_conditions_accepted           boolean DEFAULT false NOT NULL,
    sync_code_timestamp                     timestamp with time zone,
    blacklisted                             boolean DEFAULT false NOT NULL,
    terms_and_conditions_accepted_version   integer,
    last_seen                               timestamp with time zone,
    terms_and_conditions_accepted_timestamp timestamp with time zone,
    CONSTRAINT client_client_type_id_fkey FOREIGN KEY (client_type_id) REFERENCES public.client_type (uid),
    CONSTRAINT client_sync_group_id FOREIGN KEY (sync_group_id) REFERENCES public.sync_group (uid)
);

CREATE TABLE public.test_server
(
    uid              integer PRIMARY KEY                                          NOT NULL,
    name             character varying(200),
    web_address      character varying(500),
    port             integer,
    port_ssl         integer,
    city             character varying,
    country          character varying,
    geo_lat          double precision,
    geo_long         double precision,
    location         public.geometry(Point, 900913),
    web_address_ipv4 character varying(200),
    web_address_ipv6 character varying(200),
    server_type      character varying(10),
    priority         integer             DEFAULT 0                                NOT NULL,
    weight           integer             DEFAULT 1                                NOT NULL,
    active           boolean             DEFAULT true                             NOT NULL,
    uuid             uuid                DEFAULT public.uuid_generate_v4() UNIQUE NOT NULL,
    key              character varying,
    selectable       boolean             DEFAULT false                            NOT NULL,
    countries        character varying[] DEFAULT '{dev}':: character varying[]    NOT NULL,
    node             character varying,
    CONSTRAINT enforce_dims_location CHECK ((public.st_ndims(location) = 2)),
    CONSTRAINT enforce_geotype_location CHECK (((public.geometrytype(location) = 'POINT'::text) OR (location IS NULL))),
    CONSTRAINT enforce_srid_location CHECK ((public.st_srid(location) = 900913))
);

CREATE TABLE public.as2provider
(
    uid         integer PRIMARY KEY NOT NULL,
    asn         bigint,
    dns_part    character varying(200),
    provider_id integer,
    CONSTRAINT as2provider_provider_id_fkey FOREIGN KEY (provider_id) REFERENCES public.provider (uid)
);

CREATE TABLE public.news
(
    uid                       integer PRIMARY KEY      NOT NULL,
    "time"                    timestamp with time zone NOT NULL,
    title_en                  text,
    title_de                  text,
    text_en                   text,
    text_de                   text,
    active                    boolean DEFAULT false    NOT NULL,
    force                     boolean DEFAULT false    NOT NULL,
    plattform                 text,
    max_software_version_code integer,
    min_software_version_code integer,
    uuid                      uuid
);

CREATE TABLE public.test
(
    uid                         bigint PRIMARY KEY                        NOT NULL,
    uuid                        uuid UNIQUE,
    client_id                   bigint,
    client_version              character varying(10),
    client_name                 character varying,
    client_language             character varying(10),
    token                       character varying(500),
    server_id                   integer,
    port                        integer,
    use_ssl                     boolean                     DEFAULT false NOT NULL,
    "time"                      timestamp with time zone,
    speed_upload                integer,
    speed_download              integer,
    ping_shortest               bigint,
    encryption                  character varying(50),
    client_public_ip            character varying(100),
    plattform                   character varying(200),
    os_version                  character varying(100),
    api_level                   character varying(10),
    device                      character varying(200),
    model                       character varying(200),
    product                     character varying(200),
    phone_type                  integer,
    data_state                  integer,
    network_country             character varying(10),
    network_operator            character varying(10),
    network_operator_name       character varying(200),
    network_sim_country         character varying(10),
    network_sim_operator        character varying(10),
    network_sim_operator_name   character varying(200),
    wifi_ssid                   character varying(200),
    wifi_bssid                  character varying(200),
    wifi_network_id             character varying(200),
    duration                    integer,
    num_threads                 integer,
    status                      character varying(100),
    timezone                    character varying(200),
    bytes_download              bigint,
    bytes_upload                bigint,
    nsec_download               bigint,
    nsec_upload                 bigint,
    server_ip                   character varying(100),
    client_software_version     character varying(100),
    geo_lat                     double precision,
    geo_long                    double precision,
    network_type                integer,
    location                    public.geometry,
    signal_strength             integer,
    software_revision           character varying(200),
    client_test_counter         bigint,
    nat_type                    character varying(200),
    client_previous_test_status character varying(200),
    public_ip_asn               bigint,
    speed_upload_log            double precision,
    speed_download_log          double precision,
    total_bytes_download        bigint,
    total_bytes_upload          bigint,
    wifi_link_speed             integer,
    public_ip_rdns              character varying(200),
    public_ip_as_name           character varying(200),
    test_slot                   integer,
    provider_id                 integer,
    network_is_roaming          boolean,
    ping_shortest_log           double precision,
    run_ndt                     boolean,
    num_threads_requested       integer,
    client_public_ip_anonymized character varying(100),
    zip_code                    integer,
    geo_provider                character varying(200),
    geo_accuracy                double precision,
    deleted                     boolean                     DEFAULT false NOT NULL,
    comment                     text,
    open_uuid                   uuid,
    client_time                 timestamp with time zone,
    zip_code_geo                integer,
    mobile_provider_id          integer,
    roaming_type                integer,
    open_test_uuid              uuid UNIQUE,
    country_asn                 character(2),
    country_location            character(2),
    test_if_bytes_download      bigint,
    test_if_bytes_upload        bigint,
    implausible                 boolean                     DEFAULT false NOT NULL,
    testdl_if_bytes_download    bigint,
    testdl_if_bytes_upload      bigint,
    testul_if_bytes_download    bigint,
    testul_if_bytes_upload      bigint,
    country_geoip               character(2),
    location_max_distance       integer,
    location_max_distance_gps   integer,
    network_group_name          character varying(200),
    network_group_type          character varying(200),
    time_dl_ns                  bigint,
    time_ul_ns                  bigint,
    num_threads_ul              integer,
    "timestamp"                 timestamp without time zone DEFAULT now(),
    source_ip                   character varying(50),
    lte_rsrp                    integer,
    lte_rsrq                    integer,
    mobile_network_id           integer,
    mobile_sim_id               integer,
    dist_prev                   double precision,
    speed_prev                  double precision,
    tag                         character varying(512),
    ping_median                 bigint,
    ping_median_log             double precision,
    source_ip_anonymized        character varying(50),
    client_ip_local             character varying(50),
    client_ip_local_anonymized  character varying(50),
    client_ip_local_type        character varying(50),
    hidden_code                 character varying(8),
    origin                      uuid,
    developer_code              character varying(8),
    dual_sim                    boolean,
    gkz_obsolete                integer,
    android_permissions         json,
    dual_sim_detection_method   character varying(50),
    pinned                      boolean                     DEFAULT true  NOT NULL,
    similar_test_uid            bigint,
    user_server_selection       boolean,
    radio_band                  smallint,
    sim_count                   smallint,
    time_qos_ns                 bigint,
    test_nsec_qos               bigint,
    channel_number              integer,
    gkz_bev_obsolete            integer,
    gkz_sa_obsolete             integer,
    kg_nr_bev                   integer,
    land_cover_obsolete         integer,
    cell_location_id            integer,
    cell_area_code              integer,
    link_distance_obsolete      integer,
    link_id_obsolete            integer,
    settlement_type_obsolete    integer,
    link_name_obsolete          character varying,
    frc_obsolete                smallint,
    edge_id_obsolete            numeric,
    geo_location_uuid           uuid,
    last_client_status          character varying(50),
    last_qos_status             character varying(50),
    test_error_cause            character varying,
    last_sequence_number        integer,
    submission_retry_count      integer,
    CONSTRAINT enforce_dims_location CHECK ((public.st_ndims(location) = 2)),
    CONSTRAINT enforce_geotype_location CHECK (((public.geometrytype(location) = 'POINT'::text) OR (location IS NULL))),
    CONSTRAINT enforce_srid_location CHECK ((public.st_srid(location) = 900913)),
    CONSTRAINT test_speed_download_noneg CHECK ((speed_download >= 0)),
    CONSTRAINT test_speed_upload_noneg CHECK ((speed_upload >= 0)),
    CONSTRAINT settlement_type_check CHECK (((settlement_type_obsolete > 0) AND (settlement_type_obsolete < 4))) NOT VALID,
    CONSTRAINT test_client_id_fkey FOREIGN KEY (client_id) REFERENCES public.client (uid) ON DELETE CASCADE,
    CONSTRAINT test_mobile_provider_id_fkey FOREIGN KEY (mobile_provider_id) REFERENCES public.provider (uid),
    CONSTRAINT test_provider_fkey FOREIGN KEY (provider_id) REFERENCES public.provider (uid),
    CONSTRAINT test_test_server_id_fkey FOREIGN KEY (server_id) REFERENCES public.test_server (uid)
);

CREATE TABLE public.linknet_names
(
    link_id   integer PRIMARY KEY NOT NULL,
    link_name character varying
);

CREATE TABLE public.linknet
(
    gid        integer PRIMARY KEY NOT NULL,
    link_id    bigint,
    name1      character varying(254),
    name2      character varying(254),
    from_node  bigint,
    to_node    bigint,
    speedcar_t smallint,
    speedcar_b smallint,
    speedtru_t smallint,
    speedtru_b smallint,
    vmax_car_t smallint,
    vmax_car_b smallint,
    vmax_tru_t smallint,
    vmax_tru_b smallint,
    access_tow integer,
    access_bkw integer,
    length     double precision,
    frc        smallint,
    cap_tow    bigint,
    cap_bkw    bigint,
    lanes_tow  double precision,
    lanes_bkw  double precision,
    formofway  smallint,
    brunnel    smallint,
    maxheight  double precision,
    maxwidth   double precision,
    maxpress   double precision,
    abuttercar smallint,
    abuttertru smallint,
    urban      integer,
    width      double precision,
    int_level  double precision,
    toll       smallint,
    baustatus  smallint,
    subnet_id  integer,
    oneway_car smallint,
    oneway_bk  smallint,
    oneway_bus smallint,
    edge_id    numeric,
    edgecat    character varying(3),
    regcode    character varying(31),
    sustainer  character varying(19),
    dbcon      smallint,
    geom       public.geometry(MultiLineString, 4326),
    bbox       public.geometry
);

CREATE TABLE public.clc12_all_oesterreich
(
    gid        integer PRIMARY KEY NOT NULL,
    code_12    character varying(3),
    id         character varying(18),
    remark     character varying(20),
    area_ha    numeric,
    shape_leng numeric,
    shape_area numeric,
    geom       public.geometry(MultiPolygonZM, 3035),
    bbox       public.geometry
);

CREATE TABLE public.test_location
(
    uid               bigint PRIMARY KEY NOT NULL,
    open_test_uuid    uuid UNIQUE        NOT NULL,
    geo_location_uuid uuid               NOT NULL,
    location          public.geometry    NOT NULL,
    geo_long          double precision,
    geo_lat           double precision,
    geo_accuracy      double precision,
    geo_provider      character varying,
    kg_nr_bev         integer,
    gkz_bev           integer,
    gkz_sa            integer,
    land_cover        integer,
    settlement_type   integer,
    link_id           integer,
    link_name         character varying,
    link_distance     integer,
    frc               smallint,
    edge_id           numeric,
    country_location  character(2),
    dtm_level         integer,
    CONSTRAINT enforce_dims_location2 CHECK ((public.st_ndims(location) = 2)),
    CONSTRAINT enforce_geotype_location2 CHECK ((public.geometrytype(location) = 'POINT'::text)),
    CONSTRAINT enforce_srid_location2 CHECK ((public.st_srid(location) = 900913)),
    CONSTRAINT settlement_type_check2 CHECK (((settlement_type > 0) AND (settlement_type < 4))),
    CONSTRAINT test_location_open_test_uuid_fkey FOREIGN KEY (open_test_uuid) REFERENCES public.test (open_test_uuid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE public.mccmnc2provider
(
    uid             integer PRIMARY KEY NOT NULL,
    mcc_mnc_sim     character varying(10),
    provider_id     integer             NOT NULL,
    mcc_mnc_network character varying(10),
    valid_from      date,
    valid_to        date,
    CONSTRAINT mccmnc2provider_provider_id_fkey FOREIGN KEY (provider_id) REFERENCES public.provider (uid)
);

CREATE TABLE public.dsr
(
    gid  integer PRIMARY KEY NOT NULL,
    id   numeric,
    name character varying(40),
    geom public.geometry(MultiPolygon, 31287)
);

CREATE TABLE public.dhm
(
    rid  integer PRIMARY KEY NOT NULL,
    rast public.raster
);

CREATE TABLE public.ping
(
    uid            bigint PRIMARY KEY NOT NULL,
    test_id        bigint,
    value          bigint,
    value_server   bigint,
    time_ns        bigint,
    open_test_uuid uuid,
    CONSTRAINT ping_test_id_fkey FOREIGN KEY (test_id) REFERENCES public.test (uid) ON DELETE CASCADE
);

CREATE TABLE public.test_loopmode
(
    uid          integer PRIMARY KEY NOT NULL,
    test_uuid    uuid UNIQUE,
    client_uuid  uuid,
    max_movement integer,
    max_delay    integer,
    max_tests    integer,
    test_counter integer,
    loop_uuid    uuid,
    CONSTRAINT test_loopmode_test_client_uuid_fkey FOREIGN KEY (client_uuid) REFERENCES public.client (uuid),
    CONSTRAINT test_loopmode_test_uuid_fkey FOREIGN KEY (test_uuid) REFERENCES public.test (uuid)
);


CREATE TABLE public.ne_10m_admin_0_countries
(
    gid        integer PRIMARY KEY NOT NULL,
    scalerank  smallint,
    featurecla character varying(30),
    labelrank  double precision,
    sovereignt character varying(32),
    sov_a3     character varying(3),
    adm0_dif   double precision,
    level      double precision,
    type       character varying(17),
    admin      character varying(36),
    adm0_a3    character varying(3),
    geou_dif   double precision,
    geounit    character varying(36),
    gu_a3      character varying(3),
    su_dif     double precision,
    subunit    character varying(36),
    su_a3      character varying(3),
    brk_diff   double precision,
    name       character varying(36),
    name_long  character varying(36),
    brk_a3     character varying(3),
    brk_name   character varying(36),
    brk_group  character varying(30),
    abbrev     character varying(13),
    postal     character varying(4),
    formal_en  character varying(52),
    formal_fr  character varying(35),
    name_ciawf character varying(45),
    note_adm0  character varying(22),
    note_brk   character varying(164),
    name_sort  character varying(36),
    name_alt   character varying(38),
    mapcolor7  double precision,
    mapcolor8  double precision,
    mapcolor9  double precision,
    mapcolor13 double precision,
    pop_est    double precision,
    pop_rank   double precision,
    gdp_md_est double precision,
    pop_year   double precision,
    lastcensus double precision,
    gdp_year   double precision,
    economy    character varying(26),
    income_grp character varying(23),
    wikipedia  double precision,
    fips_10_   character varying(3),
    iso_a2     character varying(5),
    iso_a3     character varying(3),
    iso_a3_eh  character varying(3),
    iso_n3     character varying(3),
    un_a3      character varying(4),
    wb_a2      character varying(3),
    wb_a3      character varying(3),
    woe_id     double precision,
    woe_id_eh  double precision,
    woe_note   character varying(190),
    adm0_a3_is character varying(3),
    adm0_a3_us character varying(3),
    adm0_a3_un double precision,
    adm0_a3_wb double precision,
    continent  character varying(23),
    region_un  character varying(23),
    subregion  character varying(25),
    region_wb  character varying(26),
    name_len   double precision,
    long_len   double precision,
    abbrev_len double precision,
    tiny       double precision,
    homepart   double precision,
    min_zoom   double precision,
    min_label  double precision,
    max_label  double precision,
    geom       public.geometry(MultiPolygon, 900913)
);

-- CREATE INDEXES --
---- PING ----
CREATE SEQUENCE public.ping_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.ping_uid_seq OWNED BY public.ping.uid;
ALTER TABLE ONLY public.ping
    ALTER COLUMN uid SET DEFAULT nextval('public.ping_uid_seq'::regclass);
SELECT pg_catalog.setval('public.ping_uid_seq', 50, true);
CREATE INDEX open_test_uuid_ping_idx ON public.ping USING btree (open_test_uuid);
CREATE INDEX ping_test_id_key ON public.ping USING btree (test_id);

---- NEWS ----
CREATE SEQUENCE public.news_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.news_uid_seq OWNED BY public.news.uid;
ALTER TABLE ONLY public.news
    ALTER COLUMN uid SET DEFAULT nextval('public.news_uid_seq'::regclass);
CREATE INDEX news_time_idx ON public.news USING btree ("time");

---- CLIENT TYPE ----
CREATE SEQUENCE public.client_type_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.client_type_uid_seq OWNED BY public.client_type.uid;
ALTER TABLE ONLY public.client_type
    ALTER COLUMN uid SET DEFAULT nextval('public.client_type_uid_seq'::regclass);
SELECT pg_catalog.setval('public.client_type_uid_seq', 2, true);

---- AS 2 PROVIDER ----
CREATE SEQUENCE public.as2provider_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.as2provider_uid_seq OWNED BY public.as2provider.uid;
ALTER TABLE ONLY public.as2provider
    ALTER COLUMN uid SET DEFAULT nextval('public.as2provider_uid_seq'::regclass);
SELECT pg_catalog.setval('public.client_type_uid_seq', 7, true);
CREATE INDEX as2provider_provider_id_idx ON public.as2provider USING btree (provider_id);

---- SYNC GROUP ----
CREATE SEQUENCE public.sync_group_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.sync_group_uid_seq OWNED BY public.sync_group.uid;
ALTER TABLE ONLY public.sync_group
    ALTER COLUMN uid SET DEFAULT nextval('public.sync_group_uid_seq'::regclass);

---- PROVIDER ----
CREATE SEQUENCE public.provider_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.provider_uid_seq OWNED BY public.provider.uid;
ALTER TABLE ONLY public.provider
    ALTER COLUMN uid SET DEFAULT nextval('public.provider_uid_seq'::regclass);
SELECT pg_catalog.setval('public.provider_uid_seq', 2, true);
CREATE INDEX provider_mcc_mnc_idx ON public.provider USING btree (mcc_mnc);

---- CLIENT ----
CREATE SEQUENCE public.client_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.client_uid_seq OWNED BY public.client.uid;
ALTER TABLE ONLY public.client
    ALTER COLUMN uid SET DEFAULT nextval('public.client_uid_seq'::regclass);
SELECT pg_catalog.setval('public.client_uid_seq', 4, true);
CREATE INDEX client_client_type_id_idx ON public.client USING btree (client_type_id);
CREATE INDEX client_sync_group_id_idx ON public.client USING btree (sync_group_id);

---- TEST SERVER ----
CREATE SEQUENCE public.test_server_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.test_server_uid_seq OWNED BY public.test_server.uid;
ALTER TABLE ONLY public.test_server
    ALTER COLUMN uid SET DEFAULT nextval('public.test_server_uid_seq'::regclass);
SELECT pg_catalog.setval('public.test_server_uid_seq', 3, true);

---- TEST ----
CREATE SEQUENCE public.test_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.test_uid_seq OWNED BY public.test.uid;
ALTER TABLE ONLY public.test
    ALTER COLUMN uid SET DEFAULT nextval('public.test_uid_seq'::regclass);
SELECT pg_catalog.setval('public.test_uid_seq', 6, true);
CREATE INDEX download_idx ON public.test USING btree (bytes_download, network_type);
CREATE INDEX location_idx ON public.test USING gist (location);
CREATE INDEX test_client_id_idx ON public.test USING btree (client_id);
CREATE INDEX test_deleted_idx ON public.test USING btree (deleted);
CREATE INDEX test_developer_code_idx ON public.test USING btree (developer_code);
CREATE INDEX test_device_idx ON public.test USING btree (device);
CREATE INDEX test_geo_accuracy_idx ON public.test USING btree (geo_accuracy);
CREATE INDEX test_gkz_bev_idx ON public.test USING btree (gkz_bev_obsolete);
CREATE INDEX test_gkz_idx ON public.test USING btree (gkz_obsolete);
CREATE INDEX test_gkz_sa_idx ON public.test USING btree (gkz_sa_obsolete);
CREATE INDEX test_idx ON public.test USING btree (((network_type <> ALL (ARRAY [0, 99]))));
CREATE INDEX test_kg_nr_bev_idx ON public.test USING btree (kg_nr_bev);
CREATE INDEX test_land_cover_idx ON public.test USING btree (land_cover_obsolete);
CREATE INDEX test_mobile_network_id_idx ON public.test USING btree (mobile_network_id);
CREATE INDEX test_mobile_provider_id_idx ON public.test USING btree (mobile_provider_id);
CREATE INDEX test_network_operator_idx ON public.test USING btree (network_operator);
CREATE INDEX test_network_type_idx ON public.test USING btree (network_type);
CREATE INDEX test_open_test_uuid_idx ON public.test USING btree (open_test_uuid);
CREATE INDEX test_open_uuid_idx ON public.test USING btree (open_uuid);
CREATE INDEX test_ping_median_log_idx ON public.test USING btree (ping_median_log);
CREATE INDEX test_ping_shortest_log_idx ON public.test USING btree (ping_shortest_log);
CREATE INDEX test_pinned_idx ON public.test USING btree (pinned);
CREATE INDEX test_pinned_implausible_deleted_idx ON public.test USING btree (pinned, implausible, deleted);
CREATE INDEX test_provider_id_idx ON public.test USING btree (provider_id);
CREATE INDEX test_similar_test_uid_idx ON public.test USING btree (similar_test_uid);
CREATE INDEX test_speed_download_log_idx ON public.test USING btree (speed_download_log);
CREATE INDEX test_speed_upload_log_idx ON public.test USING btree (speed_upload_log);
CREATE INDEX test_status_finished2_idx ON public.test USING btree ((((NOT deleted) AND (NOT implausible) AND
                                                                     ((status)::text = 'FINISHED'::text))),
                                                                   network_type);
CREATE INDEX test_status_finished_idx ON public.test USING btree ((((deleted = false) AND ((status)::text = 'FINISHED'::text))),
                                                                  network_type);
CREATE INDEX test_status_idx ON public.test USING btree (status);
CREATE INDEX test_test_slot_idx ON public.test USING btree (test_slot);
CREATE INDEX test_time_export ON public.test USING btree (date_part('month'::text, timezone('UTC'::text, "time")),
                                                          date_part('year'::text, timezone('UTC'::text, "time")));
CREATE INDEX test_time_idx ON public.test USING btree ("time");
CREATE INDEX test_zip_code_idx ON public.test USING btree (zip_code);
CREATE TRIGGER trigger_test
    BEFORE INSERT OR UPDATE
    ON public.test
    FOR EACH ROW
EXECUTE FUNCTION public.trigger_test();

---- LINKNET NAMES ----
CREATE UNIQUE INDEX linknet_names_link_id_uindex ON public.linknet_names USING btree (link_id);
CREATE INDEX linknet_names_link_name_index ON public.linknet_names USING btree (link_name);

---- LINKNET ----
CREATE SEQUENCE public.linknet_gid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.linknet_gid_seq OWNED BY public.linknet.gid;
ALTER TABLE ONLY public.linknet
    ALTER COLUMN gid SET DEFAULT nextval('public.linknet_gid_seq'::regclass);
CREATE INDEX linknet_bbox_gix ON public.linknet USING gist (bbox);
CREATE INDEX linknet_gix ON public.linknet USING gist (geom);

---- CLC12 ALL OESTERREICH ----
CREATE SEQUENCE public.clc12_all_oesterreich_gid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.clc12_all_oesterreich_gid_seq OWNED BY public.clc12_all_oesterreich.gid;
ALTER TABLE ONLY public.clc12_all_oesterreich
    ALTER COLUMN gid SET DEFAULT nextval('public.clc12_all_oesterreich_gid_seq'::regclass);
CREATE INDEX clc12_all_oesterreich_bbox_gix ON public.clc12_all_oesterreich USING gist (bbox);
CREATE INDEX clc12_gix ON public.clc12_all_oesterreich USING gist (geom);

---- DSR ----
CREATE SEQUENCE public.dsr_gid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.dsr_gid_seq OWNED BY public.dsr.gid;
ALTER TABLE ONLY public.dsr
    ALTER COLUMN gid SET DEFAULT nextval('public.dsr_gid_seq'::regclass);

---- DHR ----
CREATE SEQUENCE public.dhm_rid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.dhm_rid_seq OWNED BY public.dhm.rid;
ALTER TABLE ONLY public.dhm
    ALTER COLUMN rid SET DEFAULT nextval('public.dhm_rid_seq'::regclass);
CREATE INDEX dhm_st_convexhull_idx ON public.dhm USING gist (public.st_convexhull(rast));

---- MCCMNC2PROVIDER ----
CREATE SEQUENCE public.mccmnc2provider_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.mccmnc2provider_uid_seq OWNED BY public.mccmnc2provider.uid;
ALTER TABLE ONLY public.mccmnc2provider
    ALTER COLUMN uid SET DEFAULT nextval('public.mccmnc2provider_uid_seq'::regclass);
SELECT pg_catalog.setval('public.mccmnc2provider_uid_seq', 6, true);
CREATE INDEX mccmnc2provider_mcc_mnc_idx ON public.mccmnc2provider USING btree (mcc_mnc_sim, mcc_mnc_network);
CREATE INDEX mccmnc2provider_provider_id ON public.mccmnc2provider USING btree (provider_id);

---- TEST LOCATIONS ----
CREATE SEQUENCE public.test_location_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.test_location_uid_seq OWNED BY public.test_location.uid;
ALTER TABLE ONLY public.test_location
    ALTER COLUMN uid SET DEFAULT nextval('public.test_location_uid_seq'::regclass);
CREATE INDEX test_location_geo_accuracy_idx ON public.test_location USING btree (geo_accuracy);
CREATE INDEX test_location_gkz_bev_idx ON public.test_location USING btree (gkz_bev);
CREATE INDEX test_location_gkz_sa_idx ON public.test_location USING btree (gkz_sa);
CREATE INDEX test_location_kg_nv_bev_idx ON public.test_location USING btree (kg_nr_bev);
CREATE INDEX test_location_land_cover_idx ON public.test_location USING btree (land_cover);
CREATE INDEX test_location_link_name_idx ON public.test_location USING btree (link_name);
CREATE INDEX test_location_location_gix ON public.test_location USING gist (location);
CREATE INDEX test_location_open_test_uuid_idx ON public.test_location USING btree (open_test_uuid);
CREATE INDEX test_location_settlement_type_idx ON public.test_location USING btree (settlement_type);
CREATE TRIGGER trigger_test_location2
    BEFORE INSERT OR UPDATE
    ON public.test_location
    FOR EACH ROW
EXECUTE FUNCTION public.trigger_test_location();


---- TEST LOOPMODE ----
CREATE SEQUENCE public.test_loopmode_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.test_loopmode_uid_seq OWNED BY public.test_loopmode.uid;
ALTER TABLE ONLY public.test_loopmode
    ALTER COLUMN uid SET DEFAULT nextval('public.test_loopmode_uid_seq'::regclass);

---- NE 10M ADMIN 0 COUNTRIES ----
CREATE SEQUENCE public.ne_10m_admin_0_countries_gid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.ne_10m_admin_0_countries_gid_seq OWNED BY public.ne_10m_admin_0_countries.gid;
ALTER TABLE ONLY public.ne_10m_admin_0_countries
    ALTER COLUMN gid SET DEFAULT nextval('public.ne_10m_admin_0_countries_gid_seq'::regclass);
CREATE INDEX ne_10m_admin_0_countries_iso_a2_idx ON public.ne_10m_admin_0_countries USING btree (iso_a2);
CREATE INDEX ne_10m_admin_0_countries_iso_geom_gist ON public.ne_10m_admin_0_countries USING gist (geom);

-- INSERT DATA --

INSERT INTO public.provider
VALUES (1, 'A1 Telekom Austria AG', '232-01', 'A1 TA', true),
       (2, 'Hutchison Drei Austria GmbH', '232-10', 'Hutchison Drei', true);

INSERT INTO public.client_type (uid, name)
VALUES (1, 'DESKTOP'),
       (2, 'MOBILE');

INSERT INTO public.client (uid, uuid, client_type_id, "time", sync_group_id, sync_code, terms_and_conditions_accepted,
                           sync_code_timestamp, blacklisted, terms_and_conditions_accepted_version, last_seen,
                           terms_and_conditions_accepted_timestamp)
VALUES (1, '13d8667a-ddbc-40c9-95c9-93933087ec30', 1, '2020-12-17 17:14:24.197+00', NULL, NULL, true, NULL, false, 6,
        '2020-12-17 18:11:45.472304+00', '2020-12-17 17:14:24.201251+00'),
       (3, 'bf16bc3f-cf9d-4e61-9552-8c341ab65b4e', 1, '2020-12-22 09:55:29.777+00', NULL, NULL, true, NULL, false, 6,
        '2020-12-22 09:56:22.188571+00', '2020-12-22 09:55:29.777922+00'),
       (2, 'c94e7c39-8774-4210-8be9-2411c5da9ff7', 2, '2020-12-17 18:52:09.83+00', NULL, NULL, true, NULL, false, 6,
        '2020-12-23 22:47:28.71415+00', '2020-12-17 18:52:09.831605+00'),
       (4, '69a7a3a3-cec2-42c3-8dfe-83f665dddb84', 1, '2021-01-15 15:40:30.059+00', NULL, NULL, true, NULL, false, 6,
        '2021-01-15 15:41:12.953452+00', '2021-01-15 15:40:30.064048+00');

INSERT INTO public.test_server (uid, name, web_address, port, port_ssl, city, country, geo_lat, geo_long, location,
                                web_address_ipv4, web_address_ipv6, server_type, priority, weight, active, uuid, key,
                                selectable, countries, node)
VALUES (1, 'OpenRMBTws Server', NULL, NULL, 443, 'Vienna', 'AT', 48.269755, 16.410913,
        '010100002031BF0D00DD5C867A26E03B41B6FC3597AA775741', 'test-rmbtwsv4.netztest.at', 'test-rmbtwsv6.netztest.at',
        'RMBThttp', 1, 1, true, 'ccc9107b-3d34-493f-8afc-6af8b6a66b6f',
        'ZzRPpSPvbvasaGy8JB26MhuD9Bh0MjMVn8Mv86s52y9mMFiZCinHCSuBN7vKWEua5fAP3kgu7E03pPr5kPVGpHWDI6ctpPhMImlP', true,
        '{any}', 'VIE'),
       (2, 'OpenRMBT Server', NULL, NULL, 443, 'Vienna', 'AT', 48.269755, 16.410913,
        '010100002031BF0D00DD5C867A26E03B41B6FC3597AA775741', 'test-rmbtv4.netztest.at', 'test-rmbtv6.netztest.at',
        'RMBT', 1, 1, true, 'ccc9107b-3d34-493f-8afc-6af8b6a66b6e',
        'ZzRPpSPvbvasaGy8JB26MhuD9Bh0MjMVn8Mv86s52y9mMFiZCinHCSuBN7vKWEua5fAP3kgu7E03pPr5kPVGpHWDI6ctpPhMImlP', true,
        '{any}', 'VIE'),
       (3, 'QOS Server', NULL, NULL, 443, 'Vienna', 'AT', 48.269755, 16.410913,
        '010100002031BF0D00DD5C867A26E03B41B6FC3597AA775741', 'test-qosv4.netztest.at', 'test-qosv6.netztest.at', 'QoS',
        0, 1, true, '27ba66e8-d6fc-4fca-890a-19e3b4aed6bf',
        'ZzRPpSPvbvasaGy8JB26MhuD9Bh0MjMVn8Mv86s52y9mMFiZCinHCSuBN7vKWEua5fAP3kgu7E03pPr5kPVGpHWDI6ctpPhMImlP', true,
        '{any}', 'VIE');

INSERT INTO public.as2provider (uid, asn, dns_part, provider_id)
VALUES (1, 8447, NULL, 1),
       (2, 8447, NULL, 2),
       (3, 25255, NULL, 1),
       (4, 8447, NULL, 2),
       (5, 25255, NULL, 1),
       (6, 8447, NULL, 2),
       (7, 8447, NULL, 1);

INSERT INTO public.test (uid, uuid, client_id, client_version, client_name, client_language, token, server_id, port,
                         use_ssl, "time", speed_upload, speed_download, ping_shortest, encryption, client_public_ip,
                         plattform, os_version, api_level, device, model, product, phone_type, data_state,
                         network_country, network_operator, network_operator_name, network_sim_country,
                         network_sim_operator, network_sim_operator_name, wifi_ssid, wifi_bssid, wifi_network_id,
                         duration, num_threads, status, timezone, bytes_download, bytes_upload, nsec_download,
                         nsec_upload, server_ip, client_software_version, geo_lat, geo_long, network_type, location,
                         signal_strength, software_revision, client_test_counter, nat_type, client_previous_test_status,
                         public_ip_asn, speed_upload_log, speed_download_log, total_bytes_download, total_bytes_upload,
                         wifi_link_speed, public_ip_rdns, public_ip_as_name, test_slot, provider_id, network_is_roaming,
                         ping_shortest_log, run_ndt, num_threads_requested, client_public_ip_anonymized, zip_code,
                         geo_provider, geo_accuracy, deleted, comment, open_uuid, client_time, zip_code_geo,
                         mobile_provider_id, roaming_type, open_test_uuid, country_asn, country_location,
                         test_if_bytes_download, test_if_bytes_upload, implausible, testdl_if_bytes_download,
                         testdl_if_bytes_upload, testul_if_bytes_download, testul_if_bytes_upload, country_geoip,
                         location_max_distance, location_max_distance_gps, network_group_name, network_group_type,
                         time_dl_ns, time_ul_ns, num_threads_ul, "timestamp", source_ip, lte_rsrp, lte_rsrq,
                         mobile_network_id, mobile_sim_id, dist_prev, speed_prev, tag, ping_median, ping_median_log,
                         source_ip_anonymized, client_ip_local, client_ip_local_anonymized, client_ip_local_type,
                         hidden_code, origin, developer_code, dual_sim, gkz_obsolete, android_permissions,
                         dual_sim_detection_method, pinned, similar_test_uid, user_server_selection, radio_band,
                         sim_count, time_qos_ns, test_nsec_qos, channel_number, gkz_bev_obsolete, gkz_sa_obsolete,
                         kg_nr_bev, land_cover_obsolete, cell_location_id, cell_area_code, link_distance_obsolete,
                         link_id_obsolete, settlement_type_obsolete, link_name_obsolete, frc_obsolete, edge_id_obsolete,
                         geo_location_uuid, last_client_status, last_qos_status, test_error_cause, last_sequence_number,
                         submission_retry_count)
VALUES (1, '48ddf306-3f55-4e28-a657-22dd7790921d', 1, '1.2.1', 'RMBTws', 'de',
        '5bd11dd8-992a-4429-b1e0-e93da81e5118_1608228705_AWA/AXEwxzUoNYi//7ZE9aq6Jzc=', 1, 443, true,
        '2020-12-17 18:11:45.91873+00', 32262, 44440, 27030687, NULL, '46.75.40.46', NULL, NULL, NULL, NULL, 'Firefox',
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0', NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, 7, 3, 'FINISHED', 'Europe/Budapest', 42701388, 28508160, 7687000000,
        7069123214, NULL, '0.9.0', 48.900102499999996, 15.167007900000002, 98,
        '010100002031BF0D00992BA6983FC33941F29825F641DF5741', NULL, NULL, NULL, NULL, NULL, 8447, 0.8771728217151907,
        0.9119435125672075, NULL, NULL, NULL, '046075040046.atmpu0010.highway.a1.net',
        'TELEKOM-AT A1 Telekom Austria AG', 1608228705, 1, NULL, 0.4772856945585657, NULL, 3, '46.75.40', NULL,
        'Browser', 20, false, NULL, '489e7f8a-0a64-4826-9694-86c59f0b2b46', '2020-12-17 18:11:45.481+00', NULL, NULL,
        NULL, '5bd11dd8-992a-4429-b1e0-e93da81e5118', 'AT', NULL, NULL, NULL, false, NULL, NULL, NULL, NULL, NULL, 0,
        NULL, 'LAN', 'LAN', NULL, NULL, 1, '2020-12-17 18:12:11.838761', '46.75.40.46', NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, 47444527, 0.5587287070925939, '46.75.40', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, true, -1, false, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, '5ccd4f52-3bd5-477a-82c4-a8b3fce047b0', NULL, NULL, NULL, NULL, NULL),

       (2, '5a9be491-66c3-4c34-ba85-8f889e1ea1fb', 2, '1.2.1', 'RMBT', 'en',
        '3ff240d0-61dc-4db7-b0bb-c51a57a64bf8_1608297589_vjtVjmelsrcO0tVZ+tso3rBdN3Q=', 1, 443, true,
        '2020-12-18 13:19:49.893792+00', 3072, 3382, 32987607, NULL, '92.248.36.185', 'Android', '11(6966805)', '30',
        'bramble', 'Pixel 4a (5G)', 'bramble', 1, 2, 'at', '232-03', 'yesss!', 'at', '232-12', 'yesss!', NULL, NULL,
        NULL, 7, 3, 'FINISHED', 'Europe/Vienna', 2981446, 2733210, 7052189244, 7117572247, '213.208.152.3', '4.1.19',
        48.89870633, 15.16635747, 103, '010100002031BF0D00E0EFD430F7C239410ADB07DB06DF5741', -83,
        'fix/rtr_release_fixes_''4ce8bda9''', 395, 'no_nat_ipv4', 'END', 8447, 0.6218553028398686, 0.6322934008154307,
        3388272, 6890962, NULL, '092248036185.atmpu0038.highway.a1.net', 'TELEKOM-AT A1 Telekom Austria AG', 1608297589,
        1, false, 0.5061169373087923, NULL, 3, '92.248.36', NULL, 'gps', 3.7900925, false, NULL,
        '70e8b161-fbf6-46fc-9742-decf269c090b', '2020-12-18 13:19:46.379+00', NULL, 1, 1,
        '3ff240d0-61dc-4db7-b0bb-c51a57a64bf8', 'AT', NULL, 4238055, 4052673, false, 3714000, 69113, 95708, 3714101,
        NULL, 50, NULL, '2G/4G', 'MOBILE', 6043386384, 17768509895, NULL, '2020-12-18 13:20:28.303603', '92.248.36.185',
        NULL, NULL, 3217, 3235, 69.44637647, 5.577625849033263, NULL, 36862220, 0.5221938289117971, '92.248.36',
        '92.248.36.185', '92.248.36', 'public_ipv4', NULL, NULL, NULL, true, NULL, '[
         {
           "permission": "android.permission.ACCESS_FINE_LOCATION",
           "status": true
         },
         {
           "permission": "android.permission.ACCESS_BACKGROUND_LOCATION",
           "status": true
         },
         {
           "permission": "android.permission.ACCESS_COARSE_LOCATION",
           "status": true
         },
         {
           "permission": "android.permission.READ_PHONE_STATE",
           "status": true
         }
       ]', NULL, true, -1, true, 8, 2, NULL, NULL, 4, NULL, NULL, NULL, NULL, 1295, 12030, NULL, NULL, NULL, NULL, NULL,
        NULL, 'af713895-eaef-4b78-b512-8c212fb8e497', 'UP', 'QOS_END', NULL, NULL, 0),

       (3, '56d4d641-7e17-43ca-8b00-56ddf51d43f4', 2, '1.2.1', 'RMBT', 'en',
        '985cf645-5f64-4ef8-b565-6136862d9532_1608231149_yqmnMMgasZgzS8JuWI1lcFNqrCY=', 1, 443, true,
        '2020-12-17 18:52:30.328172+00', 942, 10686, 23871565, NULL, '77.116.193.187', 'Android', '11(6886588)', '30',
        'bramble', 'Pixel 4a (5G)', 'bramble', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '17Sept11-B529s',
        'ac:07:5f:ba:12:cf', '3', 7, 3, 'FINISHED', 'Europe/Vienna', 9355775, 866669, 7003956687, 7359768498,
        '213.208.152.3', '4.1.19', 48.9000215, 15.1670788, 99, '010100002031BF0D009373247D47C33941F3664A883EDF5741',
        -67, 'fix/rtr_release_fixes_''4ce8bda9''', 380, 'nat_local_to_public_ipv4', 'END', 25255, 0.49351272569821936,
        0.7572037924617218, 13551642, 3122842, 78, '77.116.193.187.wireless.dyn.drei.com', 'H3G-AUSTRIA-AS', 1608231149,
        2, NULL, 0.4592936306420276, NULL, 3, '77.116.193', NULL, 'network', 14.443, false, NULL,
        'd868677f-5dcb-4a2d-8d79-43e713cae1a3', '2020-12-17 18:52:29.136+00', NULL, NULL, NULL,
        '985cf645-5f64-4ef8-b565-6136862d9532', 'AT', NULL, 14263109, 2522331, false, 9810064, 152607, 50746, 1355624,
        NULL, 0, NULL, 'WLAN', 'WLAN', 6578304301, 17990601844, NULL, '2020-12-17 18:53:02.426916', '77.116.193.187',
        NULL, NULL, NULL, NULL, NULL, NULL, NULL, 43917748, 0.5475466874672571, '77.116.193', '192.168.8.129',
        '192.168.8', 'site_local_ipv4', NULL, NULL, NULL, NULL, NULL, '[
         {
           "permission": "android.permission.ACCESS_FINE_LOCATION",
           "status": true
         },
         {
           "permission": "android.permission.ACCESS_COARSE_LOCATION",
           "status": true
         },
         {
           "permission": "android.permission.ACCESS_BACKGROUND_LOCATION",
           "status": true
         },
         {
           "permission": "android.permission.READ_PHONE_STATE",
           "status": true
         }
       ]', NULL, true, -1, true, NULL, NULL, NULL, NULL, 2457, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, '2fe554fb-5741-42af-a774-d5afb8c854a6', 'UP', 'QOS_END', NULL, NULL, 0),

       (4, '3e41deec-78e6-40a6-b8fb-da70433bc70d', 2, '1.2.1', 'RMBT', 'en',
        '87ccf1dc-e830-4ebe-9bbe-1d8437c38438_1608297628_BY0chAfIXQGI8VqvG5RogLR/CXA=', 2, 443, true,
        '2020-12-18 13:20:28.938978+00', 8706, 9404, 34652503, NULL, '92.248.36.185', 'Android', '11(6966805)', '30',
        'bramble', 'Pixel 4a (5G)', 'bramble', 1, 2, 'at', '232-03', 'yesss!', 'at', '232-12', 'yesss!', NULL, NULL,
        NULL, 7, 3, 'FINISHED', 'Europe/Vienna', 8298902, 7791741, 7059930964, 7160105363, '213.208.152.4', '4.1.19',
        48.8982937, 15.16608128, 103, '010100002031BF0D00C1FA0672D8C23941CA873263F5DE5741', -83,
        'fix/rtr_release_fixes_''4ce8bda9''', 396, 'no_nat_ipv4', 'END', 8447, 0.7349546657053448, 0.7433281551132255,
        9906522, 16716984, NULL, '092248036185.atmpu0038.highway.a1.net', 'TELEKOM-AT A1 Telekom Austria AG',
        1608297628, 1, false, 0.5132448699314115, NULL, 3, '92.248.36', NULL, 'gps', 3.7900925, false, NULL,
        '70e8b161-fbf6-46fc-9742-decf269c090b', '2020-12-18 13:20:25.515+00', NULL, 1, 1,
        '87ccf1dc-e830-4ebe-9bbe-1d8437c38438', 'AT', NULL, 11452109, 11319570, false, 9579662, 78280, 185371, 8812812,
        NULL, 50, NULL, '2G/4G', 'MOBILE', 5175070620, 15895871063, NULL, '2020-12-18 13:21:04.527575', '92.248.36.185',
        NULL, NULL, 3217, 3235, 50.15729541, 4.62454612140918, NULL, 39607019, 0.5325907189014966, '92.248.36',
        '92.248.36.185', '92.248.36', 'public_ipv4', NULL, NULL, NULL, true, NULL, '[
         {
           "permission": "android.permission.ACCESS_BACKGROUND_LOCATION",
           "status": true
         },
         {
           "permission": "android.permission.ACCESS_COARSE_LOCATION",
           "status": true
         },
         {
           "permission": "android.permission.ACCESS_FINE_LOCATION",
           "status": true
         },
         {
           "permission": "android.permission.READ_PHONE_STATE",
           "status": true
         }
       ]', NULL, false, 23, true, 8, 2, NULL, NULL, 4, NULL, NULL, NULL, NULL, 1295, 12030, NULL, NULL, NULL, NULL,
        NULL, NULL, '581b264d-ad3c-457f-9e0c-f1f92ec6048b', 'UP', 'QOS_END', NULL, NULL, 0),

       (5, '377d5df8-21b5-47de-b898-1fc9add0319e', 2, '1.2.1', 'RMBT', 'en',
        '58fd2387-1404-4485-8f1a-d9cfbc945f3e_1608297884_KjxiqkNp4WIo5V6T6WXZpReTKVw=', 2, 443, true,
        '2020-12-18 13:24:45.417947+00', 4567, 16513, 30071826, NULL, '92.248.36.185', 'Android', '11(6966805)', '30',
        'bramble', 'Pixel 4a (5G)', 'bramble', 1, 2, 'at', '232-03', 'yesss!', 'at', '232-12', 'yesss!', NULL, NULL,
        NULL, 7, 3, 'FINISHED', 'Europe/Vienna', 14458613, 4036087, 7004758458, 7070289817, '213.208.152.4', '4.1.19',
        48.89591297, 15.16377769, 103, '010100002031BF0D0037D4CD02D8C13941293C039B90DE5741', -87,
        'fix/rtr_release_fixes_''4ce8bda9''', 403, 'no_nat_ipv4', 'END', 8447, 0.6649077529017502, 0.8044564952249631,
        20772390, 9893102, NULL, '092248036185.atmpu0038.highway.a1.net', 'TELEKOM-AT A1 Telekom Austria AG',
        1608297884, 1, false, 0.49271993327379865, NULL, 3, '92.248.36', NULL, 'gps', 3.7900925, false, NULL,
        '70e8b161-fbf6-46fc-9742-decf269c090b', '2020-12-18 13:24:41.977+00', NULL, 1, 1,
        '58fd2387-1404-4485-8f1a-d9cfbc945f3e', 'AT', NULL, 23016068, 6173115, false, 16305958, 141024, 115483, 4760367,
        NULL, 0, NULL, '2G/4G', 'MOBILE', 4646957495, 15526753630, NULL, '2020-12-18 13:25:23.63583', '92.248.36.185',
        NULL, NULL, 3217, 3235, 23.19250728, 2.280010294164191, NULL, 27956880, 0.48216290077478696, '92.248.36',
        '92.248.36.185', '92.248.36', 'public_ipv4', NULL, NULL, NULL, true, NULL, '[
         {
           "permission": "android.permission.ACCESS_COARSE_LOCATION",
           "status": true
         },
         {
           "permission": "android.permission.ACCESS_FINE_LOCATION",
           "status": true
         },
         {
           "permission": "android.permission.READ_PHONE_STATE",
           "status": true
         },
         {
           "permission": "android.permission.ACCESS_BACKGROUND_LOCATION",
           "status": true
         }
       ]', NULL, true, -1, true, 8, 2, NULL, NULL, 4, NULL, NULL, NULL, NULL, 1295, 12030, NULL, NULL, NULL, NULL, NULL,
        NULL, '971af114-ce9f-45f8-9f0e-021863a3f685', 'UP', 'QOS_TEST_RUNNING', NULL, NULL, 0),

       (6, '41ab60bd-becf-45c8-abbc-0e85b59d65ca', 2, '1.2.1', 'RMBT', 'en',
        '1b5e1c0c-9c7b-4bca-a9c4-793991b0f6b4_1608235353_CgJyTgMounQk9zR2NtgzzZzLUr4=', 2, 443, true,
        '2020-12-17 20:02:33.502374+00', 11975, 5546, 25274951, NULL, '46.75.40.46', 'Android', '11(6886588)', '30',
        'bramble', 'Pixel 4a (5G)', 'bramble', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ZTE_5GCPE_AD13_2.4G',
        '60:14:66:d8:ad:13', '65', 7, 3, 'ERROR', 'Europe/Vienna', 4853542, 9033268, 7001466739, 6034570338,
        '213.208.152.4', '4.1.19', 48.9000169, 15.1670786, 99, '010100002031BF0D005B5D717747C3394197E66F563EDF5741',
        -83, 'fix/rtr_release_fixes_''4ce8bda9''', 381, 'nat_local_to_public_ipv4', 'END', 8447, 0.7695688805216502,
        0.6859949663104608, 5086565, 16061408, 1, '046075040046.atmpu0010.highway.a1.net',
        'TELEKOM-AT A1 Telekom Austria AG', 1608235353, 1, NULL, 0.4675634407688502, NULL, 3, '46.75.40', NULL,
        'network', 13.218, false, NULL, NULL, '2020-12-17 20:02:25.636+00', NULL, NULL, NULL,
        '1b5e1c0c-9c7b-4bca-a9c4-793991b0f6b4', 'AT', NULL, 6074929, 15204788, false, 5593483, 131438, 201253, 11778922,
        NULL, NULL, NULL, 'WLAN', 'WLAN', 5603645194, 19929779173, NULL, '2020-12-17 20:02:33.502374', '46.75.40.46',
        NULL, NULL, NULL, NULL, NULL, NULL, NULL, 40238424, 0.5348803208556933, '46.75.40', '192.168.0.7', '192.168.0',
        'site_local_ipv4', NULL, NULL, NULL, NULL, NULL, '[
         {
           "permission": "android.permission.ACCESS_FINE_LOCATION",
           "status": true
         },
         {
           "permission": "android.permission.ACCESS_COARSE_LOCATION",
           "status": true
         },
         {
           "permission": "android.permission.ACCESS_BACKGROUND_LOCATION",
           "status": true
         },
         {
           "permission": "android.permission.READ_PHONE_STATE",
           "status": true
         }
       ]', NULL, true, NULL, true, NULL, NULL, NULL, NULL, 2437, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, '929ede40-95ae-478f-9f99-e42a81e3fe77', 'SPEEDTEST_END', 'QOS_TEST_RUNNING', 'at.rtr.rmbt.util.IllegalNetworkChangeException: Illegal network change during the test
	at at.specure.measurement.MeasurementService$testListener$1.onError(MeasurementService.kt:266)
	at at.specure.test.TestControllerImpl.handleError(TestControllerImpl.kt:461)
	at at.specure.test.TestControllerImpl.checkIllegalNetworkChange(TestControllerImpl.kt:447)
	at at.specure.test.TestControllerImpl.access$checkIllegalNetworkChange(TestControllerImpl.kt:48)
	at at.specure.test.TestControllerImpl$start$1.invokeSuspend(TestControllerImpl.kt:266)
	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:56)
	at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:571)
	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.executeTask(CoroutineScheduler.kt:738)
	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:678)
	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:665)
', NULL, 0);

INSERT INTO public.mccmnc2provider (uid, mcc_mnc_sim, provider_id, mcc_mnc_network, valid_from, valid_to)
VALUES (1, '232-01', 1, NULL, NULL, NULL),
       (2, '232-02', 1, NULL, NULL, NULL),
       (3, '232-03', 2, NULL, NULL, NULL),
       (4, '232-07', 2, NULL, NULL, NULL),
       (5, '232-09', 1, NULL, NULL, NULL),
       (6, '232-11', 1, NULL, NULL, NULL);

INSERT INTO public.ping (uid, test_id, value, value_server, time_ns, open_test_uuid)
VALUES (1, 1, 76000000, 91935281, 6748000000, '5bd11dd8-992a-4429-b1e0-e93da81e5118'),
       (2, 1, 34000000, 50386654, 7015000000, '5bd11dd8-992a-4429-b1e0-e93da81e5118'),
       (3, 1, 118000000, 117220109, 7216000000, '5bd11dd8-992a-4429-b1e0-e93da81e5118'),
       (4, 1, 81000000, 48955757, 7522000000, '5bd11dd8-992a-4429-b1e0-e93da81e5118'),
       (5, 1, 33000000, 27030687, 7784000000, '5bd11dd8-992a-4429-b1e0-e93da81e5118'),
       (6, 1, 71000000, 63149971, 7946000000, '5bd11dd8-992a-4429-b1e0-e93da81e5118'),
       (7, 1, 38000000, 38973636, 8136000000, '5bd11dd8-992a-4429-b1e0-e93da81e5118'),
       (8, 1, 31000000, 29839485, 8353000000, '5bd11dd8-992a-4429-b1e0-e93da81e5118'),
       (9, 1, 47000000, 36984434, 8508000000, '5bd11dd8-992a-4429-b1e0-e93da81e5118'),
       (10, 1, 41000000, 45933297, 8663000000, '5bd11dd8-992a-4429-b1e0-e93da81e5118'),
       (11, 2, 32876201, 51525374, 4634869993, '3ff240d0-61dc-4db7-b0bb-c51a57a64bf8'),
       (12, 2, 47845161, 45512819, 4817883970, '3ff240d0-61dc-4db7-b0bb-c51a57a64bf8'),
       (13, 2, 44209848, 138129818, 5026812949, '3ff240d0-61dc-4db7-b0bb-c51a57a64bf8'),
       (14, 2, 34147816, 37340711, 5296348861, '3ff240d0-61dc-4db7-b0bb-c51a57a64bf8'),
       (15, 2, 60813444, 79595256, 5479599088, '3ff240d0-61dc-4db7-b0bb-c51a57a64bf8'),
       (16, 2, 23871565, 25258149, 5775339638, '3ff240d0-61dc-4db7-b0bb-c51a57a64bf8'),
       (17, 2, 33909222, 25157860, 5925819757, '3ff240d0-61dc-4db7-b0bb-c51a57a64bf8'),
       (18, 2, 36715577, 42322678, 6082754096, '3ff240d0-61dc-4db7-b0bb-c51a57a64bf8'),
       (19, 2, 49024536, 50655146, 6243851508, '3ff240d0-61dc-4db7-b0bb-c51a57a64bf8'),
       (20, 2, 50160787, 34361676, 6411119233, '3ff240d0-61dc-4db7-b0bb-c51a57a64bf8'),
       (21, 3, 35376201, 27233737, 3959108311, '985cf645-5f64-4ef8-b565-6136862d9532'),
       (22, 3, 160569339, 59993133, 4033836808, '985cf645-5f64-4ef8-b565-6136862d9532'),
       (23, 3, 43386515, 40186260, 4345088870, '985cf645-5f64-4ef8-b565-6136862d9532'),
       (24, 3, 56883131, 59893569, 4507353730, '985cf645-5f64-4ef8-b565-6136862d9532'),
       (25, 3, 65547090, 50593563, 4670069840, '985cf645-5f64-4ef8-b565-6136862d9532'),
       (26, 3, 32715108, 40290589, 4859947255, '985cf645-5f64-4ef8-b565-6136862d9532'),
       (27, 3, 43562660, 57900666, 5007467842, '985cf645-5f64-4ef8-b565-6136862d9532'),
       (28, 3, 31417294, 30074272, 5197899945, '985cf645-5f64-4ef8-b565-6136862d9532'),
       (29, 3, 25274951, 24908413, 5340795636, '985cf645-5f64-4ef8-b565-6136862d9532'),
       (30, 3, 29242399, 23845724, 5476366639, '985cf645-5f64-4ef8-b565-6136862d9532'),
       (31, 4, 35376201, 27233737, 3959108311, '87ccf1dc-e830-4ebe-9bbe-1d8437c38438'),
       (32, 4, 160569339, 59993133, 4033836808, '87ccf1dc-e830-4ebe-9bbe-1d8437c38438'),
       (33, 4, 43386515, 40186260, 4345088870, '87ccf1dc-e830-4ebe-9bbe-1d8437c38438'),
       (34, 4, 56883131, 59893569, 4507353730, '87ccf1dc-e830-4ebe-9bbe-1d8437c38438'),
       (35, 4, 65547090, 50593563, 4670069840, '87ccf1dc-e830-4ebe-9bbe-1d8437c38438'),
       (36, 4, 32715108, 40290589, 4859947255, '87ccf1dc-e830-4ebe-9bbe-1d8437c38438'),
       (37, 4, 43562660, 57900666, 5007467842, '87ccf1dc-e830-4ebe-9bbe-1d8437c38438'),
       (38, 4, 31417294, 30074272, 5197899945, '87ccf1dc-e830-4ebe-9bbe-1d8437c38438'),
       (39, 4, 25274951, 24908413, 5340795636, '87ccf1dc-e830-4ebe-9bbe-1d8437c38438'),
       (40, 4, 29242399, 23845724, 5476366639, '87ccf1dc-e830-4ebe-9bbe-1d8437c38438'),
       (41, 5, 35376201, 27233737, 3959108311, '58fd2387-1404-4485-8f1a-d9cfbc945f3e'),
       (42, 5, 160569339, 59993133, 4033836808, '58fd2387-1404-4485-8f1a-d9cfbc945f3e'),
       (43, 5, 43386515, 40186260, 4345088870, '58fd2387-1404-4485-8f1a-d9cfbc945f3e'),
       (44, 5, 56883131, 59893569, 4507353730, '58fd2387-1404-4485-8f1a-d9cfbc945f3e'),
       (45, 5, 65547090, 50593563, 4670069840, '58fd2387-1404-4485-8f1a-d9cfbc945f3e'),
       (46, 5, 32715108, 40290589, 4859947255, '58fd2387-1404-4485-8f1a-d9cfbc945f3e'),
       (47, 5, 43562660, 57900666, 5007467842, '58fd2387-1404-4485-8f1a-d9cfbc945f3e'),
       (48, 5, 31417294, 30074272, 5197899945, '58fd2387-1404-4485-8f1a-d9cfbc945f3e'),
       (49, 5, 25274951, 24908413, 5340795636, '58fd2387-1404-4485-8f1a-d9cfbc945f3e'),
       (50, 5, 29242399, 23845724, 5476366639, '58fd2387-1404-4485-8f1a-d9cfbc945f3e');