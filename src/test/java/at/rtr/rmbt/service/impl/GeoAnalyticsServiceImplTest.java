package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.dto.TestDistance;
import at.rtr.rmbt.repository.GeoAnalyticsRepository;
import at.rtr.rmbt.service.GeoAnalyticsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.apache.commons.lang3.math.NumberUtils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class GeoAnalyticsServiceImplTest {
    private GeoAnalyticsService geoAnalyticsService;

    @MockBean
    private GeoAnalyticsRepository geoAnalyticsRepository;


    @Before
    public void setUp() {
        geoAnalyticsService = new GeoAnalyticsServiceImpl(geoAnalyticsRepository);
    }

    @Test
    public void getTestDistance_whenMaxAccuracyMoreThanDistance_expectZeroDistance() {
        TestDistance testDistance = TestDistance.builder()
                .totalDistance(TestConstants.DEFAULT_TEST_DISTANCE_TOTAL_DISTANCE_SECOND)
                .maxAccuracy(TestConstants.DEFAULT_TEST_DISTANCE_MAX_ACCURACY_SECOND)
                .build();
        when(geoAnalyticsRepository.getTestDistance(TestConstants.DEFAULT_TEST_OPEN_TEST_UUID)).thenReturn(testDistance);

        var response = geoAnalyticsService.getTestDistance(TestConstants.DEFAULT_TEST_OPEN_TEST_UUID);

        assertEquals(NumberUtils.DOUBLE_ZERO, response.getTotalDistance());
        assertEquals(TestConstants.DEFAULT_TEST_DISTANCE_MAX_ACCURACY_SECOND, response.getMaxAccuracy());
    }

    @Test
    public void getTestDistance_whenMaxAccuracyLessThanDistance_expectTestDistance() {
        TestDistance testDistance = TestDistance.builder()
                .totalDistance(TestConstants.DEFAULT_TEST_DISTANCE_TOTAL_DISTANCE_FIRST)
                .maxAccuracy(TestConstants.DEFAULT_TEST_DISTANCE_MAX_ACCURACY_FIRST)
                .build();
        when(geoAnalyticsRepository.getTestDistance(TestConstants.DEFAULT_TEST_OPEN_TEST_UUID)).thenReturn(testDistance);

        var response = geoAnalyticsService.getTestDistance(TestConstants.DEFAULT_TEST_OPEN_TEST_UUID);

        assertEquals(TestConstants.DEFAULT_TEST_DISTANCE_TOTAL_DISTANCE_FIRST, response.getTotalDistance());
        assertEquals(TestConstants.DEFAULT_TEST_DISTANCE_MAX_ACCURACY_FIRST, response.getMaxAccuracy());
    }
}