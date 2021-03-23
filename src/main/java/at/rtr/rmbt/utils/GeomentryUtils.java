package at.rtr.rmbt.utils;

import lombok.experimental.UtilityClass;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

import java.util.Objects;

@UtilityClass
public class GeomentryUtils {

    public Geometry getGeometryFromLongitudeAndLatitude(Double longitude, Double latitude) {
        if (Objects.nonNull(latitude) && Objects.nonNull(longitude)) {
            return new GeometryFactory(new PrecisionModel(), 900913).createPoint(new Coordinate(longitude, latitude));
        } else {
            return null;
        }
    }
}
