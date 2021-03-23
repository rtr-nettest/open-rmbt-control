package at.rtr.rmbt.utils;

import at.rtr.rmbt.constant.Constants;
import lombok.experimental.UtilityClass;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

import java.util.Objects;

@UtilityClass
public class GeometryUtils {

    public Geometry getGeometryFromLongitudeAndLatitude(Double longitude, Double latitude) {
        if (Objects.nonNull(latitude) && Objects.nonNull(longitude)) {
            return new GeometryFactory(new PrecisionModel(), Constants.SRID).createPoint(new Coordinate(longitude, latitude));
        } else {
            return null;
        }
    }
}
