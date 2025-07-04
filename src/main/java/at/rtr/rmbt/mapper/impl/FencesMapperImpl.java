package at.rtr.rmbt.mapper.impl;


import at.rtr.rmbt.mapper.FencesMapper;
import at.rtr.rmbt.model.Fences;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.FencesRequest;
import at.rtr.rmbt.utils.GeometryUtils;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Service;

@Service
public class FencesMapperImpl implements FencesMapper {

    @Override
    public Fences fencesRequestToFences(Fences fr, Test test) {
        return null;
    }

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
                .technologyId(fr.getTechnologyId())
                .offsetMs(fr.getOffsetMs())
                .durationMs(fr.getDurationMs())
                .geom4326(geom4326)
                .build();
    }

    /*
    CREATE TABLE public.fences (
	uid serial4 NOT NULL,
	open_test_uuid uuid NOT NULL,
	fence_id int4 NOT NULL,
	technology_id int4 NULL,
	technology varchar(50) NULL,
	offset_ms int4 NOT NULL,
	duration_ms int4 NOT NULL,
	radius int4 NULL,
	geom4326 public.geometry(point, 4326) NULL,
	CONSTRAINT fences_open_test_uuid_fence_id_idx UNIQUE (open_test_uuid, fence_id),
	CONSTRAINT fences_pkey PRIMARY KEY (uid),
	CONSTRAINT fences_open_test_uuid_fkey FOREIGN KEY (open_test_uuid) REFERENCES public.test(open_test_uuid) ON DELETE CASCADE
);
CREATE INDEX fences_open_test_uuid_idx ON public.fences USING btree (open_test_uuid);
CREATE INDEX geom4326_idx ON public.fences USING gist (geom4326);
     */

}
