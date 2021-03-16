package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.dto.TestDistance;
import at.rtr.rmbt.repository.GeoAnalyticsRepository;
import at.rtr.rmbt.service.GeoAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GeoAnalyticsServiceImpl implements GeoAnalyticsService {

    private final GeoAnalyticsRepository geoAnalyticsRepository;

    @Override
    public TestDistance getTestDistance(UUID openTestUUID) {
        TestDistance testDistance = geoAnalyticsRepository.getTestDistance(openTestUUID);
        if (Objects.nonNull(testDistance) && testDistance.getTotalDistance() < testDistance.getMaxAccuracy()) {
            testDistance.setTotalDistance(NumberUtils.DOUBLE_ZERO);
        }
        return testDistance;
    }
}
