package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.mapper.GeoLocationMapper;
import at.rtr.rmbt.request.GeoLocationRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.shaded.org.apache.commons.lang.math.NumberUtils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class GeoLocationMapperImplTest {
    private GeoLocationMapper geoLocationMapper;

    @MockBean
    private UUIDGenerator uuidGenerator;

    @Mock
    private GeoLocationRequest geoLocationRequest;
    @Mock
    private at.rtr.rmbt.model.Test test;

    @Before
    public void setUp() {
        geoLocationMapper = new GeoLocationMapperImpl(uuidGenerator);
    }

    @Test
    public void geoLocationRequestAndTestToGeoLocation_whenCommonData_expectGeoLocation() {
        when(test.getTimezone()).thenReturn(TestConstants.DEFAULT_TIMEZONE);
        when(test.getOpenTestUuid()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(geoLocationRequest.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_FIRST);
        when(geoLocationRequest.getGeoLong()).thenReturn(TestConstants.DEFAULT_LONGITUDE);
        when(geoLocationRequest.getGeoLat()).thenReturn(TestConstants.DEFAULT_LATITUDE);
        when(geoLocationRequest.getAltitude()).thenReturn(TestConstants.DEFAULT_ALTITUDE);
        when(geoLocationRequest.getBearing()).thenReturn(TestConstants.DEFAULT_BEARING);
        when(geoLocationRequest.getMockLocation()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(geoLocationRequest.getProvider()).thenReturn(TestConstants.DEFAULT_PROVIDER);
        when(geoLocationRequest.getSpeed()).thenReturn(TestConstants.DEFAULT_SPEED);
        when(geoLocationRequest.getTimeNs()).thenReturn(TestConstants.DEFAULT_TIME);
        when(geoLocationRequest.getTstamp()).thenReturn(TestConstants.DEFAULT_TIME_INSTANT);
        when(uuidGenerator.generateUUID()).thenReturn(TestConstants.DEFAULT_GEO_LOCATION_UUID);

        var response = geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequest, test);

        assertEquals(TestConstants.DEFAULT_ACCURACY_FIRST, response.getAccuracy());
        assertEquals(TestConstants.DEFAULT_LATITUDE, response.getGeoLat());
        assertEquals(TestConstants.DEFAULT_LONGITUDE, response.getGeoLong());
        assertEquals(TestConstants.DEFAULT_ALTITUDE, response.getAltitude());
        assertEquals(TestConstants.DEFAULT_BEARING, response.getBearing());
        assertEquals(TestConstants.DEFAULT_FLAG_TRUE, response.getMockLocation());
        assertEquals(TestConstants.DEFAULT_PROVIDER, response.getProvider());
        assertEquals(TestConstants.DEFAULT_SPEED, response.getSpeed());
        assertEquals(TestConstants.DEFAULT_TIME, response.getTimeNs());
        assertEquals(TestConstants.DEFAULT_ZONED_DATE_TIME, response.getTime());
        assertEquals(TestConstants.DEFAULT_TEST_UUID, response.getOpenTestUUID());
        assertEquals(TestConstants.DEFAULT_GEO_LOCATION_UUID, response.getGeoLocationUUID());
        assertEquals(TestConstants.DEFAULT_LOCATION, response.getLocation());
        assertEquals(test, response.getTest());
    }

    @Test
    public void buildNewGeoLocation_whenCommonData_expectGeoLocation() {
        when(test.getOpenTestUuid()).thenReturn(TestConstants.DEFAULT_TEST_UUID);
        when(uuidGenerator.generateUUID()).thenReturn(TestConstants.DEFAULT_GEO_LOCATION_UUID);

        var response = geoLocationMapper.buildNewGeoLocation(test, TestConstants.DEFAULT_LATITUDE, TestConstants.DEFAULT_LONGITUDE, TestConstants.DEFAULT_ACCURACY_FIRST, Config.GEO_PROVIDER_GEOCODER);

        assertEquals(TestConstants.DEFAULT_ACCURACY_FIRST, response.getAccuracy());
        assertEquals(TestConstants.DEFAULT_LATITUDE, response.getGeoLat());
        assertEquals(TestConstants.DEFAULT_LONGITUDE, response.getGeoLong());
        assertEquals(Config.GEO_PROVIDER_GEOCODER, response.getProvider());
        assertEquals(NumberUtils.LONG_ZERO, response.getTimeNs());
        assertEquals(TestConstants.DEFAULT_TEST_UUID, response.getOpenTestUUID());
        assertEquals(TestConstants.DEFAULT_GEO_LOCATION_UUID, response.getGeoLocationUUID());
        assertEquals(TestConstants.DEFAULT_LOCATION, response.getLocation());
        assertEquals(test, response.getTest());
    }
}
