package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.mapper.GeoLocationMapper;
import at.rtr.rmbt.model.GeoLocation;
import at.rtr.rmbt.repository.GeoLocationRepository;
import at.rtr.rmbt.request.GeoLocationRequest;
import at.rtr.rmbt.request.ResultUpdateRequest;
import at.rtr.rmbt.service.GeoLocationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class GeoLocationServiceImplTest {
    private GeoLocationService geoLocationService;

    @MockBean
    private GeoLocationMapper geoLocationMapper;
    @MockBean
    private GeoLocationRepository geoLocationRepository;

    @Mock
    private GeoLocationRequest geoLocationRequestFirst;
    @Mock
    private GeoLocationRequest geoLocationRequestSecond;
    @Mock
    private GeoLocation geoLocationFirst;
    @Mock
    private GeoLocation geoLocationSecond;
    @Mock
    private at.rtr.rmbt.model.Test test;
    @Mock
    private ResultUpdateRequest resultUpdateRequest;

    @Before
    public void setUp() {
        geoLocationService = new GeoLocationServiceImpl(geoLocationMapper, geoLocationRepository);
    }

    @Test
    public void updateGeoLocation_whenCommonRequest_expectGeoLocationSavedAndTestModified() {
        var requests = List.of(geoLocationRequestFirst, geoLocationRequestSecond);
        when(geoLocationRepository.saveAndFlush(geoLocationFirst)).thenReturn(geoLocationFirst);
        when(geoLocationRepository.saveAndFlush(geoLocationSecond)).thenReturn(geoLocationSecond);
        when(geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequestFirst, test)).thenReturn(geoLocationFirst);
        when(geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequestSecond, test)).thenReturn(geoLocationSecond);
        when(geoLocationFirst.getId()).thenReturn(TestConstants.DEFAULT_UID);
        when(geoLocationFirst.getTimeNs()).thenReturn(TestConstants.DEFAULT_TIME_NS);
        when(geoLocationFirst.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_FIRST);
        when(geoLocationFirst.getGeoLocationUUID()).thenReturn(TestConstants.DEFAULT_GEO_LOCATION_UUID);
        when(geoLocationFirst.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_FIRST);
        when(geoLocationFirst.getGeoLong()).thenReturn(TestConstants.DEFAULT_LONGITUDE);
        when(geoLocationFirst.getGeoLat()).thenReturn(TestConstants.DEFAULT_LATITUDE);
        when(geoLocationFirst.getProvider()).thenReturn(TestConstants.DEFAULT_PROVIDER);
        when(geoLocationSecond.getTimeNs()).thenReturn(TestConstants.DEFAULT_TIME_NS);
        when(geoLocationSecond.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_SECOND);
        when(geoLocationSecond.getId()).thenReturn(TestConstants.DEFAULT_UID);

        geoLocationService.processGeoLocationRequests(requests, test);

        verify(geoLocationRepository).saveAndFlush(geoLocationFirst);
        verify(geoLocationRepository).saveAndFlush(geoLocationSecond);
        verify(test).setGeoLocationUuid(TestConstants.DEFAULT_GEO_LOCATION_UUID);
        verify(test).setGeoProvider(TestConstants.DEFAULT_PROVIDER);
        verify(test).setGeoAccuracy(TestConstants.DEFAULT_ACCURACY_FIRST);
        verify(test).setLongitude(TestConstants.DEFAULT_LONGITUDE);
        verify(test).setLatitude(TestConstants.DEFAULT_LATITUDE);
    }

    @Test
    public void updateGeoLocation_whenCommonData_expectGeoLocationUpdated() {
        when(resultUpdateRequest.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_FIRST);
        when(resultUpdateRequest.getGeoLat()).thenReturn(TestConstants.DEFAULT_LATITUDE);
        when(resultUpdateRequest.getGeoLong()).thenReturn(TestConstants.DEFAULT_LONGITUDE);
        when(resultUpdateRequest.getProvider()).thenReturn(Config.GEO_PROVIDER_GEOCODER);
        when(geoLocationMapper.buildNewGeoLocation(test, TestConstants.DEFAULT_LATITUDE, TestConstants.DEFAULT_LONGITUDE, TestConstants.DEFAULT_ACCURACY_FIRST, Config.GEO_PROVIDER_GEOCODER)).thenReturn(geoLocationFirst);
        when(geoLocationFirst.getGeoLocationUUID()).thenReturn(TestConstants.DEFAULT_GEO_LOCATION_UUID);
        when(geoLocationFirst.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_FIRST);
        when(geoLocationFirst.getGeoLong()).thenReturn(TestConstants.DEFAULT_LONGITUDE);
        when(geoLocationFirst.getGeoLat()).thenReturn(TestConstants.DEFAULT_LATITUDE);
        when(geoLocationFirst.getProvider()).thenReturn(TestConstants.DEFAULT_PROVIDER);

        geoLocationService.updateGeoLocation(test, resultUpdateRequest);

        verify(geoLocationRepository).save(geoLocationFirst);
        verify(test).setGeoLocationUuid(TestConstants.DEFAULT_GEO_LOCATION_UUID);
        verify(test).setGeoProvider(TestConstants.DEFAULT_PROVIDER);
        verify(test).setGeoAccuracy(TestConstants.DEFAULT_ACCURACY_FIRST);
        verify(test).setLongitude(TestConstants.DEFAULT_LONGITUDE);
        verify(test).setLatitude(TestConstants.DEFAULT_LATITUDE);
    }
}
