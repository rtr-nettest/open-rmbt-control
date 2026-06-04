package at.rtr.rmbt.utils;

import at.rtr.rmbt.constant.Constants;
import lombok.experimental.UtilityClass;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

/**
 * Builds the geometry columns stored on a {@code test} row from a client's WGS84 longitude/latitude.
 *
 * <p>Web Mercator (EPSG:3857, and its legacy-numbered twin EPSG:900913 "Google Mercator") is a
 * closed-form spherical transform, so it is computed directly here with plain JTS - no external
 * CRS/transform library. This is numerically equivalent to PostGIS
 * {@code ST_Transform(geom4326, 3857)} (PROJ uses the same spherical pseudo-Mercator), and it keeps
 * the project free of GeoTools (whose referencing factories also spawned background threads that had
 * to be shut down explicitly).
 *
 * <p><b>Alternative:</b> these columns could instead be derived entirely in the database as Postgres
 * generated columns, which would let this Java code be dropped:
 * <pre>{@code
 * geom4326 geometry GENERATED ALWAYS AS (ST_SetSRID(ST_MakePoint(geo_long, geo_lat), 4326)) STORED
 * geom3857 geometry GENERATED ALWAYS AS (ST_Transform(ST_SetSRID(ST_MakePoint(geo_long, geo_lat), 4326), 3857)) STORED
 * }</pre>
 * That migration is <i>not</i> simply "delete this class": a STORED generated column is computed
 * <i>after</i> BEFORE triggers run, so it is NULL inside {@code trigger_test()} - which reads
 * {@code NEW.geom4326} for the {@code test_location} upsert, {@code dist_prev}, boundary checks, etc.
 * The trigger would first have to compute the point itself (e.g.
 * {@code ST_SetSRID(ST_MakePoint(NEW.geo_long, NEW.geo_lat), 4326)}) before generated columns are safe.
 */
@UtilityClass
public class GeometryUtils {

    /** WGS84 semi-major axis (m); the sphere radius used by EPSG:3857 / EPSG:900913 web mercator. */
    private static final double WEB_MERCATOR_RADIUS = 6378137.0;

    /** Point in EPSG:900913 ("Google Mercator") - same projection as 3857, kept for the legacy column. */
    public Point getPointEPSG900913FromLongitudeAndLatitude(Double longitude, Double latitude) {
        return webMercatorPoint(longitude, latitude, Constants.SRID900913);
    }

    /**
     * Returns a Point in EPSG:4326 from longitude/latitude. This is not a projection - the stored
     * coordinates are the WGS84 lon/lat themselves.
     *
     * @param longitude the east-west position in degrees (x)
     * @param latitude  the north-south position in degrees (y)
     */
    public Point getPointEPSG4326FromLongitudeAndLatitude(Double longitude, Double latitude) {
        if (longitude == null || latitude == null) {
            return null;
        }
        return point(longitude, latitude, Constants.SRID4326);
    }

    /** Point in EPSG:3857 (web mercator). */
    public Point getPointEPSG3857FromLongitudeAndLatitude(Double longitude, Double latitude) {
        return webMercatorPoint(longitude, latitude, Constants.SRID3857);
    }

    private static Point webMercatorPoint(final Double longitude, final Double latitude, final int srid) {
        if (longitude == null || latitude == null) {
            return null;
        }
        final double x = Math.toRadians(longitude) * WEB_MERCATOR_RADIUS;
        final double y = Math.log(Math.tan(Math.PI / 4.0 + Math.toRadians(latitude) / 2.0)) * WEB_MERCATOR_RADIUS;
        return point(x, y, srid);
    }

    private static Point point(final double x, final double y, final int srid) {
        final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), srid);
        return geometryFactory.createPoint(new Coordinate(x, y));
    }
}
