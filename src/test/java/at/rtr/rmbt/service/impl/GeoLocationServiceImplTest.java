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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class GeoLocationServiceImplTest {
    private GeoLocationService geoLocationService;

    @MockitoBean
    private GeoLocationMapper geoLocationMapper;
    @MockitoBean
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

        verify(geoLocationRepository).saveAll(List.of(geoLocationFirst, geoLocationSecond));
        verify(test).setGeoLocationUuid(TestConstants.DEFAULT_GEO_LOCATION_UUID);
        verify(test).setGeoProvider(TestConstants.DEFAULT_PROVIDER);
        verify(test).setGeoAccuracy(TestConstants.DEFAULT_ACCURACY_FIRST);
        verify(test).setLongitude(TestConstants.DEFAULT_LONGITUDE);
        verify(test).setLatitude(TestConstants.DEFAULT_LATITUDE);
    }

    @Test
    public void processGeoLocationRequests_whenFirstPositionWithinThreshold_expectFirstUsedOverMoreAccurateLater() {
        var requests = List.of(geoLocationRequestFirst, geoLocationRequestSecond);
        stubValidRequest(geoLocationRequestFirst);
        stubValidRequest(geoLocationRequestSecond);
        when(geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequestFirst, test)).thenReturn(geoLocationFirst);
        when(geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequestSecond, test)).thenReturn(geoLocationSecond);
        // First position is already accurate enough (within the +/- threshold) ...
        when(geoLocationFirst.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_WITHIN_THRESHOLD);
        when(geoLocationFirst.getGeoLocationUUID()).thenReturn(TestConstants.DEFAULT_GEO_LOCATION_UUID);
        when(geoLocationFirst.getGeoLong()).thenReturn(TestConstants.DEFAULT_LONGITUDE);
        when(geoLocationFirst.getGeoLat()).thenReturn(TestConstants.DEFAULT_LATITUDE);
        when(geoLocationFirst.getProvider()).thenReturn(TestConstants.DEFAULT_PROVIDER);
        // ... even though the second position has a better accuracy value.
        when(geoLocationSecond.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_BEST);

        geoLocationService.processGeoLocationRequests(requests, test);

        verify(geoLocationRepository).saveAll(List.of(geoLocationFirst, geoLocationSecond));
        verify(test).setGeoLocationUuid(TestConstants.DEFAULT_GEO_LOCATION_UUID);
        verify(test).setGeoProvider(TestConstants.DEFAULT_PROVIDER);
        verify(test).setGeoAccuracy(TestConstants.DEFAULT_ACCURACY_WITHIN_THRESHOLD);
        verify(test).setLongitude(TestConstants.DEFAULT_LONGITUDE);
        verify(test).setLatitude(TestConstants.DEFAULT_LATITUDE);
    }

    @Test
    public void processGeoLocationRequests_whenNoPositionWithinThreshold_expectBestAccuracyUsed() {
        var requests = List.of(geoLocationRequestFirst, geoLocationRequestSecond);
        stubValidRequest(geoLocationRequestFirst);
        stubValidRequest(geoLocationRequestSecond);
        when(geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequestFirst, test)).thenReturn(geoLocationFirst);
        when(geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequestSecond, test)).thenReturn(geoLocationSecond);
        // Neither position is within the threshold, so the more accurate (second) one wins.
        when(geoLocationFirst.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_SECOND);
        when(geoLocationSecond.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_FIRST);
        when(geoLocationSecond.getGeoLocationUUID()).thenReturn(TestConstants.DEFAULT_GEO_LOCATION_UUID);
        when(geoLocationSecond.getGeoLong()).thenReturn(TestConstants.DEFAULT_LONGITUDE_SECOND);
        when(geoLocationSecond.getGeoLat()).thenReturn(TestConstants.DEFAULT_LATITUDE_SECOND);
        when(geoLocationSecond.getProvider()).thenReturn(TestConstants.DEFAULT_PROVIDER);

        geoLocationService.processGeoLocationRequests(requests, test);

        verify(test).setGeoAccuracy(TestConstants.DEFAULT_ACCURACY_FIRST);
        verify(test).setLongitude(TestConstants.DEFAULT_LONGITUDE_SECOND);
        verify(test).setLatitude(TestConstants.DEFAULT_LATITUDE_SECOND);
    }

    private void stubValidRequest(GeoLocationRequest request) {
        stubValidRequest(request, TestConstants.DEFAULT_TIME_NS); // positive -> in-test position
    }

    private void stubValidRequest(GeoLocationRequest request, Long timeNs) {
        when(request.getTstamp()).thenReturn(TestConstants.DEFAULT_TIME_NS);
        when(request.getGeoLat()).thenReturn(TestConstants.DEFAULT_LATITUDE);
        when(request.getGeoLong()).thenReturn(TestConstants.DEFAULT_LONGITUDE);
        when(request.getTimeNs()).thenReturn(timeNs);
    }

    @Test
    public void processGeoLocationRequests_whenTimeNsZero_expectPositionUsed() {
        // A position at exactly time_ns == 0 is "at test start" and must be treated as valid.
        var requests = List.of(geoLocationRequestFirst);
        stubValidRequest(geoLocationRequestFirst, 0L);
        when(geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequestFirst, test)).thenReturn(geoLocationFirst);
        when(geoLocationFirst.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_WITHIN_THRESHOLD);
        when(geoLocationFirst.getGeoLocationUUID()).thenReturn(TestConstants.DEFAULT_GEO_LOCATION_UUID);
        when(geoLocationFirst.getGeoLong()).thenReturn(TestConstants.DEFAULT_LONGITUDE);
        when(geoLocationFirst.getGeoLat()).thenReturn(TestConstants.DEFAULT_LATITUDE);
        when(geoLocationFirst.getProvider()).thenReturn(TestConstants.DEFAULT_PROVIDER);

        geoLocationService.processGeoLocationRequests(requests, test);

        verify(test).setGeoLocationUuid(TestConstants.DEFAULT_GEO_LOCATION_UUID);
        verify(test).setGeoAccuracy(TestConstants.DEFAULT_ACCURACY_WITHIN_THRESHOLD);
    }

    @Test
    public void processGeoLocationRequests_whenBestAccuracyIsPreStart_expectPreStartExcluded() {
        // The most accurate position is before test start; it must NOT become the reference.
        var requests = List.of(geoLocationRequestFirst, geoLocationRequestSecond);
        stubValidRequest(geoLocationRequestFirst, -1_000_000_000L); // pre-start (-1 s), best accuracy
        stubValidRequest(geoLocationRequestSecond, TestConstants.DEFAULT_TIME_NS); // in-test, worse accuracy
        when(geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequestFirst, test)).thenReturn(geoLocationFirst);
        when(geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequestSecond, test)).thenReturn(geoLocationSecond);
        when(geoLocationFirst.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_BEST);   // 2.0, but pre-start
        when(geoLocationSecond.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_SECOND); // 20.0, in-test
        when(geoLocationSecond.getGeoLocationUUID()).thenReturn(TestConstants.DEFAULT_GEO_LOCATION_UUID);
        when(geoLocationSecond.getGeoLong()).thenReturn(TestConstants.DEFAULT_LONGITUDE_SECOND);
        when(geoLocationSecond.getGeoLat()).thenReturn(TestConstants.DEFAULT_LATITUDE_SECOND);
        when(geoLocationSecond.getProvider()).thenReturn(TestConstants.DEFAULT_PROVIDER);

        geoLocationService.processGeoLocationRequests(requests, test);

        // in-test second wins despite worse accuracy
        verify(test).setGeoAccuracy(TestConstants.DEFAULT_ACCURACY_SECOND);
        verify(test).setLatitude(TestConstants.DEFAULT_LATITUDE_SECOND);
    }

    @Test
    public void processGeoLocationRequests_whenNullTimeNs_expectNoExceptionAndNotSelected() {
        // A null time_ns must not cause an NPE and must not be selected as reference.
        var requests = List.of(geoLocationRequestFirst);
        stubValidRequest(geoLocationRequestFirst, null);
        when(geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequestFirst, test)).thenReturn(geoLocationFirst);
        when(geoLocationFirst.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_WITHIN_THRESHOLD);

        geoLocationService.processGeoLocationRequests(requests, test);

        verify(geoLocationRepository).saveAll(List.of(geoLocationFirst)); // still stored
        verify(test, never()).setGeoLocationUuid(any());                  // but never used as reference
    }

    @Test
    public void processGeoLocationRequests_whenOnlyPreStartWithinWindow_expectNewestSelectedNotMostAccurate() {
        // No in-test position exists. Fallback: among positions within the 10 s pre-start window,
        // the NEWEST is taken (by time), NOT the most accurate.
        var requests = List.of(geoLocationRequestFirst, geoLocationRequestSecond);
        stubValidRequest(geoLocationRequestFirst, -5_000_000_000L);  // -5 s, most accurate
        stubValidRequest(geoLocationRequestSecond, -1_000_000_000L); // -1 s (newest), least accurate
        when(geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequestFirst, test)).thenReturn(geoLocationFirst);
        when(geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequestSecond, test)).thenReturn(geoLocationSecond);
        when(geoLocationFirst.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_BEST);    // 2.0
        when(geoLocationSecond.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_SECOND); // 20.0, but newer
        when(geoLocationSecond.getGeoLocationUUID()).thenReturn(TestConstants.DEFAULT_GEO_LOCATION_UUID);
        when(geoLocationSecond.getGeoLong()).thenReturn(TestConstants.DEFAULT_LONGITUDE_SECOND);
        when(geoLocationSecond.getGeoLat()).thenReturn(TestConstants.DEFAULT_LATITUDE_SECOND);
        when(geoLocationSecond.getProvider()).thenReturn(TestConstants.DEFAULT_PROVIDER);

        geoLocationService.processGeoLocationRequests(requests, test);

        // newest pre-start (second) selected, not the more accurate first
        verify(test).setGeoAccuracy(TestConstants.DEFAULT_ACCURACY_SECOND);
        verify(test).setLatitude(TestConstants.DEFAULT_LATITUDE_SECOND);
    }

    @Test
    public void processGeoLocationRequests_whenInTestButAccuracyOverLimit_expectIgnored() {
        // In-test position but accuracy == 10000 m (not < limit) -> ignored, no reference set.
        var requests = List.of(geoLocationRequestFirst);
        stubValidRequest(geoLocationRequestFirst, TestConstants.DEFAULT_TIME_NS);
        when(geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequestFirst, test)).thenReturn(geoLocationFirst);
        when(geoLocationFirst.getAccuracy()).thenReturn(10000.0);

        geoLocationService.processGeoLocationRequests(requests, test);

        verify(geoLocationRepository).saveAll(List.of(geoLocationFirst)); // stored
        verify(test, never()).setGeoLocationUuid(any());                  // but not a reference
    }

    @Test
    public void processGeoLocationRequests_whenPreStartAccuracyOverLimit_expectIgnored() {
        // Only position is pre-start within the window but accuracy is worse than the limit -> ignored.
        var requests = List.of(geoLocationRequestFirst);
        stubValidRequest(geoLocationRequestFirst, -1_000_000_000L);
        when(geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequestFirst, test)).thenReturn(geoLocationFirst);
        when(geoLocationFirst.getAccuracy()).thenReturn(15000.0);

        geoLocationService.processGeoLocationRequests(requests, test);

        verify(geoLocationRepository).saveAll(List.of(geoLocationFirst)); // stored
        verify(test, never()).setGeoLocationUuid(any());                  // but not a reference
    }

    @Test
    public void processGeoLocationRequests_whenPreStartOutsideWindow_expectNoReference() {
        // The only position is more than 10 s before start -> outside the fallback window -> no reference set.
        var requests = List.of(geoLocationRequestFirst);
        stubValidRequest(geoLocationRequestFirst, -20_000_000_000L); // -20 s, outside the -10 s window
        when(geoLocationMapper.geoLocationRequestToGeoLocation(geoLocationRequestFirst, test)).thenReturn(geoLocationFirst);
        when(geoLocationFirst.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_BEST);

        geoLocationService.processGeoLocationRequests(requests, test);

        verify(geoLocationRepository).saveAll(List.of(geoLocationFirst)); // stored
        verify(test, never()).setGeoLocationUuid(any());                  // but never used as reference
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

    @Test
    public void createAndAssignGeoLocation_whenCommonData_expectGeoLocationSavedWithTimeAndTestModified() {
        when(geoLocationMapper.buildNewGeoLocation(test, TestConstants.DEFAULT_LATITUDE, TestConstants.DEFAULT_LONGITUDE, TestConstants.DEFAULT_ACCURACY_FIRST, Config.GEO_PROVIDER_GPS)).thenReturn(geoLocationFirst);
        when(geoLocationFirst.getGeoLocationUUID()).thenReturn(TestConstants.DEFAULT_GEO_LOCATION_UUID);
        when(geoLocationFirst.getAccuracy()).thenReturn(TestConstants.DEFAULT_ACCURACY_FIRST);
        when(geoLocationFirst.getGeoLong()).thenReturn(TestConstants.DEFAULT_LONGITUDE);
        when(geoLocationFirst.getGeoLat()).thenReturn(TestConstants.DEFAULT_LATITUDE);
        when(geoLocationFirst.getProvider()).thenReturn(TestConstants.DEFAULT_PROVIDER);

        geoLocationService.createAndAssignGeoLocation(test, TestConstants.DEFAULT_LATITUDE, TestConstants.DEFAULT_LONGITUDE, TestConstants.DEFAULT_ACCURACY_FIRST, Config.GEO_PROVIDER_GPS, TestConstants.DEFAULT_ZONED_DATE_TIME);

        verify(geoLocationFirst).setTime(TestConstants.DEFAULT_ZONED_DATE_TIME);
        verify(geoLocationRepository).save(geoLocationFirst);
        verify(test).setGeoLocationUuid(TestConstants.DEFAULT_GEO_LOCATION_UUID);
        verify(test).setGeoProvider(TestConstants.DEFAULT_PROVIDER);
        verify(test).setGeoAccuracy(TestConstants.DEFAULT_ACCURACY_FIRST);
        verify(test).setLongitude(TestConstants.DEFAULT_LONGITUDE);
        verify(test).setLatitude(TestConstants.DEFAULT_LATITUDE);
    }
}
