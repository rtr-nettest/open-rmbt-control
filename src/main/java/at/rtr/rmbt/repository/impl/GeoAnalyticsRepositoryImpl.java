package at.rtr.rmbt.repository.impl;

import at.rtr.rmbt.dto.TestDistance;
import at.rtr.rmbt.repository.GeoAnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class GeoAnalyticsRepositoryImpl implements GeoAnalyticsRepository {

    private final JdbcTemplate jdbcTemplate;
    private final static String GET_DISTANCE_FROM_GPS_LOCATION = "select max(g.accuracy) max_accuracy,st_lengthSpheroid(st_transform(st_makeline(g.location " +
            "order by g.time_ns),4326),'SPHEROID[\"WGS 84\",6378137,298.257223563]') as distance " +
            "from geo_location as g  where g.open_test_uuid= '%s' and (g.provider='gps' or g.provider='' or g.provider is null)\n" +
            "group by g.open_test_uuid;";

    @Override
    public TestDistance getTestDistance(UUID openTestUUID) {
        return jdbcTemplate.query(String.format(GET_DISTANCE_FROM_GPS_LOCATION, openTestUUID.toString()), (rs) -> {
            if (rs.next()) {
                return TestDistance.builder()
                        .totalDistance(rs.getDouble("distance"))
                        .maxAccuracy(rs.getDouble("max_accuracy"))
                        .build();
            }
            return null;
        });
    }
}
