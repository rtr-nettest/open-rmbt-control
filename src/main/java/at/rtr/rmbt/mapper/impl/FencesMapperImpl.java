package at.rtr.rmbt.mapper.impl;


import at.rtr.rmbt.mapper.FencesMapper;
import at.rtr.rmbt.model.Fences;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.FencesRequest;
import at.rtr.rmbt.utils.GeometryUtils;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Service;

/**
 * Fences mapper impl class.
 */
@Service
public class FencesMapperImpl implements FencesMapper {

    /**
     * Fences request to fences.
     *
     * @param fr the Fr
     * @param test the Test
     * @return the result
     */
    @Override
    public Fences fencesRequestToFences(Fences fr, Test test) {
        return null;
    }

       /**
        * Fences request to fences.
        *
        * @param fr the Fr
        * @param test the Test
        * @return the result
        */
    @Override
       public Fences fencesRequestToFences(FencesRequest fr, Test test) {

        // extract lat/long from request, create Geom
        double lat = fr.getLocation().getLatitude();
        double lon = fr.getLocation().getLongitude();
        // Double longitude, Double latitude
        Geometry geom4326 = GeometryUtils.getPointEPSG4326FromLongitudeAndLatitude(lon,lat);

        return Fences.builder()
                .openTestUUID(test.getOpenTestUuid())
                .fenceId(test.getFencesCount())
                .fenceId(test.getFencesCount())
                .technology(fr.getTechnology())
                .avgPingMs(fr.getAvgPingMs())
                .technologyId(fr.getTechnologyId())
                .offsetMs(fr.getOffsetMs())
                .durationMs(fr.getDurationMs())
                .radius(fr.getRadius())
                .geom4326(geom4326)
                .signal(fr.getSignal())
                .build();
    }

}
