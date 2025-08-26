package at.rtr.rmbt.utils;

import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.model.GeoLocation;
import lombok.experimental.UtilityClass;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.*;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

@UtilityClass
public class GeometryUtils {

    public Point getPointEPSG900913FromLongitudeAndLatitude(Double longitude, Double latitude) {
        try {
            CoordinateReferenceSystem sourceCRS = CRS.parseWKT(Constants.WKT_EPSG_4326);
            CoordinateReferenceSystem targetCRS = CRS.parseWKT(Constants.WKT_EPSG_900913);
            MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), Constants.SRID900913);
            Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
            return (Point) JTS.transform(point, transform);
        } catch (TransformException | FactoryException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Returns a Point object in EPSG:4326 coordinate reference system from given longitude and latitude.
     *
     * <p>
     * Longitude (x) is the east-west position in degrees.
     * Latitude (y) is the north-south position in degrees.
     * </p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * Point point = getPointEPSG4326FromLongitudeAndLatitude(15.401262012505189, 46.99121587668888);
     * }</pre>
     *
     * @param longitude the east-west position in degrees
     * @param latitude the north-south position in degrees
     * @return a Point object representing the specified longitude and latitude
     */
    public Point getPointEPSG4326FromLongitudeAndLatitude(Double longitude, Double latitude) {
            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), Constants.SRID4326);
            Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
            return point;
    }
    public Point getPointEPSG3857FromLongitudeAndLatitude(Double longitude, Double latitude) {
        try {
            CoordinateReferenceSystem sourceCRS = CRS.parseWKT(Constants.WKT_EPSG_4326);
            CoordinateReferenceSystem targetCRS = CRS.parseWKT(Constants.WKT_EPSG_3857);
            MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), Constants.SRID3857);
            Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
            return (Point) JTS.transform(point, transform);
        } catch (TransformException | FactoryException e) {
            e.printStackTrace();
        }
        return null;
    }

}
