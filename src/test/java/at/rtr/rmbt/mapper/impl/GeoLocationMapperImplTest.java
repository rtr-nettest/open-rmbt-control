package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.mapper.GeoLocationMapper;
import at.rtr.rmbt.request.GeoLocationRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class GeoLocationMapperImplTest {
    private GeoLocationMapper geoLocationMapper;

    @MockBean
    private UUIDGenerator uuidGenerator;

    @Mock
    private GeoLocationRequest geoLocationRequest;

    @Before
    public void setUp() {
        geoLocationMapper = new GeoLocationMapperImpl(uuidGenerator);
    }

    @Test
    public void geoLocationRequestAndTestToGeoLocation_whenCommonData_expectGeoLocation() {
        when(geoLocationRequest.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_FIRST);
        when(geoLocationRequest.getGeoLong()).thenReturn(TestConstants.DEFAULT_LONGITUDE);
        when(geoLocationRequest.getGeoLat()).thenReturn(TestConstants.DEFAULT_LATITUDE);
        when(geoLocationRequest.getAltitude()).thenReturn(TestConstants.DEFAULT_ALTITUDE);
        when(geoLocationRequest.getBearing()).thenReturn(TestConstants.DEFAULT_BEARING);
        when(geoLocationRequest.isMockLocation()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(geoLocationRequest.getProvider()).thenReturn(TestConstants.DEFAULT_PROVIDER);
        when(geoLocationRequest.getSpeed()).thenReturn(TestConstants.DEFAULT_SPEED);
        when(geoLocationRequest.getTimeNs()).thenReturn(TestConstants.DEFAULT_TIME);
        when(uuidGenerator.generateUUID()).thenReturn(TestConstants.DEFAULT_GEO_LOCATION_UUID);

        var response = geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequest);

        assertEquals(TestConstants.DEFAULT_ACCURACY_FIRST, response.getAccuracy());
        assertEquals(TestConstants.DEFAULT_LATITUDE, response.getGeoLat());
        assertEquals(TestConstants.DEFAULT_LONGITUDE, response.getGeoLong());
        assertEquals(TestConstants.DEFAULT_ALTITUDE, response.getAltitude());
        assertEquals(TestConstants.DEFAULT_BEARING, response.getBearing());
        assertEquals(TestConstants.DEFAULT_FLAG_TRUE, response.isMockLocation());
        assertEquals(TestConstants.DEFAULT_PROVIDER, response.getProvider());
        assertEquals(TestConstants.DEFAULT_SPEED, response.getSpeed());
        assertEquals(TestConstants.DEFAULT_TIME, response.getTimeNs());
        assertEquals(TestConstants.DEFAULT_GEO_LOCATION_UUID, response.getGeoLocationUUID());
    }
}
