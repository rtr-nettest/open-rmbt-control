package at.rtr.rmbt.utils;

import at.rtr.rmbt.constant.Constants;
import lombok.experimental.UtilityClass;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

@UtilityClass
public class GeometryUtils {

    public Point getPointFromLongitudeAndLatitude(Double longitude, Double latitude) {
        try {
            CoordinateReferenceSystem sourceCRS = CRS.parseWKT(Constants.WKT_EPSG_4326);
            CoordinateReferenceSystem targetCRS = CRS.parseWKT(Constants.WKT_EPSG_900913);
            MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), Constants.SRID);
            Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
            return (Point) JTS.transform(point, transform);
        } catch (TransformException | FactoryException e) {
            e.printStackTrace();
        }
        return null;
    }
}
