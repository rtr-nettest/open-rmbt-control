ALTER TABLE public.test_server_types
    ADD COLUMN IF not exists uid serial NOT null;
ALTER TABLE public.test_server_types
    ADD COLUMN IF not exists  port int4 null;
ALTER TABLE public.test_server_types
    ADD COLUMN IF not exists port_ssl int4 null;
ALTER TABLE public.test_server_types
    ADD COLUMN IF not exists "encrypted" bool NOT NULL DEFAULT false;

update test_server_types tst
set
    uid = subquery.uid,
    test_server_uid  = subquery.test_server_uid,
    server_type  = subquery.server_type,
    port = subquery.port,
    port_ssl = subquery.port_ssl,
    "encrypted" = subquery."encrypted"
from(select row_number() OVER(ORDER BY test_server_uid) as uid,
            tst.test_server_uid as test_server_uid,
            tst.server_type as server_type,
            ts.port as port,
            ts.port_ssl  as port_ssl,
            ts."encrypted" as "encrypted"
    from public.test_server_types tst
    inner join public.test_server ts  on ts.uid  = tst.test_server_uid)
    AS subquery
where tst.test_server_uid  = subquery.test_server_uid
  and tst.server_type  = subquery.server_type;

ALTER TABLE test_server DROP COLUMN "encrypted";