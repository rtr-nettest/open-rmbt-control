package at.rtr.rmbt.utils;

import at.rtr.rmbt.constant.Constants;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests the GeoTools-free geometry transforms: EPSG:4326 stores the raw lon/lat, EPSG:3857 / 900913
 * are the (identical) web-mercator projection, and the projection round-trips back to WGS84.
 */
class GeometryUtilsTest {

    private static final double LON = 15.401262012505189;
    private static final double LAT = 46.99121587668888;
    private static final double EARTH_RADIUS = 6378137.0;
    private static final double EPS = 1e-6;

    @Test
    void epsg4326_storesRawLonLatWithSrid() {
        Point p = GeometryUtils.getPointEPSG4326FromLongitudeAndLatitude(LON, LAT);
        assertEquals(Constants.SRID4326, p.getSRID());
        assertEquals(LON, p.getX(), EPS);
        assertEquals(LAT, p.getY(), EPS);
    }

    @Test
    void epsg3857_originMapsToZero() {
        Point p = GeometryUtils.getPointEPSG3857FromLongitudeAndLatitude(0.0, 0.0);
        assertEquals(Constants.SRID3857, p.getSRID());
        assertEquals(0.0, p.getX(), EPS);
        assertEquals(0.0, p.getY(), EPS);
    }

    @Test
    void epsg900913_equalsEpsg3857_sameProjection() {
        Point m3857 = GeometryUtils.getPointEPSG3857FromLongitudeAndLatitude(LON, LAT);
        Point m900913 = GeometryUtils.getPointEPSG900913FromLongitudeAndLatitude(LON, LAT);
        assertEquals(Constants.SRID900913, m900913.getSRID());
        assertEquals(m3857.getX(), m900913.getX(), EPS);
        assertEquals(m3857.getY(), m900913.getY(), EPS);
    }

    @Test
    void epsg3857_roundTripsBackToWgs84() {
        Point p = GeometryUtils.getPointEPSG3857FromLongitudeAndLatitude(LON, LAT);
        // inverse web-mercator must recover the original WGS84 coordinates
        double lon = Math.toDegrees(p.getX() / EARTH_RADIUS);
        double lat = Math.toDegrees(2 * Math.atan(Math.exp(p.getY() / EARTH_RADIUS)) - Math.PI / 2);
        assertEquals(LON, lon, EPS);
        assertEquals(LAT, lat, EPS);
    }

    @Test
    void nullCoordinates_returnNull() {
        assertNull(GeometryUtils.getPointEPSG4326FromLongitudeAndLatitude(null, LAT));
        assertNull(GeometryUtils.getPointEPSG3857FromLongitudeAndLatitude(LON, null));
        assertNull(GeometryUtils.getPointEPSG900913FromLongitudeAndLatitude(null, null));
    }
}
