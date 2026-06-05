package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.mapper.FencesMapper;
import at.rtr.rmbt.model.Fences;
import at.rtr.rmbt.model.GeoLocation;
import at.rtr.rmbt.repository.FencesRepository;
import at.rtr.rmbt.request.FencesRequest;
import at.rtr.rmbt.request.SimpleLocationRequest;
import at.rtr.rmbt.service.GeoLocationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link FencesServiceImpl}: each request is mapped, the per-test fence counter is
 * advanced, every fence is stamped with {@code testTime + offsetMs}, a geo_location is created per
 * fence (the first one also assigned to the test) and referenced via geo_location_uuid, and the
 * batch is saved.
 */
@ExtendWith(MockitoExtension.class)
class FencesServiceImplTest {

    @Mock
    private FencesMapper fencesMapper;
    @Mock
    private FencesRepository fencesRepository;
    @Mock
    private GeoLocationService geoLocationService;
    @InjectMocks
    private FencesServiceImpl fencesService;

    @Captor
    private ArgumentCaptor<List<Fences>> fencesCaptor;

    @Test
    void processFencesRequests_countsStampsFenceTimeAndSaves() {
        final ZonedDateTime testTime = ZonedDateTime.parse("2024-01-01T00:00:00Z");
        final at.rtr.rmbt.model.Test test = new at.rtr.rmbt.model.Test();
        test.setTime(testTime);

        final FencesRequest req1 = mock(FencesRequest.class);
        when(req1.getOffsetMs()).thenReturn(1000L);
        final FencesRequest req2 = mock(FencesRequest.class);
        when(req2.getOffsetMs()).thenReturn(2000L);

        final Fences fence1 = Fences.builder().build();
        final Fences fence2 = Fences.builder().build();
        when(fencesMapper.fencesRequestToFences(req1, test)).thenReturn(fence1);
        when(fencesMapper.fencesRequestToFences(req2, test)).thenReturn(fence2);

        fencesService.processFencesRequests(List.of(req1, req2), test);

        // counter advanced once per fence
        assertEquals(2L, test.getFencesCount());
        // service assigns the 0-based fence id from its own counter
        assertEquals(0L, fence1.getFenceId());
        assertEquals(1L, fence2.getFenceId());
        // fenceTime = test time + per-fence offset
        assertEquals(testTime.plus(1000, ChronoUnit.MILLIS), fence1.getFenceTime());
        assertEquals(testTime.plus(2000, ChronoUnit.MILLIS), fence2.getFenceTime());
        // both mapped fences are saved, in order
        verify(fencesRepository).saveAll(fencesCaptor.capture());
        assertEquals(2, fencesCaptor.getValue().size());
        assertSame(fence1, fencesCaptor.getValue().get(0));
        assertSame(fence2, fencesCaptor.getValue().get(1));
    }

    @Test
    void processFencesRequests_createsGeoLocationPerFence_firstAssignedToTestAndReferencedByUuid() {
        final ZonedDateTime testTime = ZonedDateTime.parse("2024-01-01T00:00:00Z");
        final at.rtr.rmbt.model.Test test = new at.rtr.rmbt.model.Test();
        test.setTime(testTime);

        final SimpleLocationRequest loc1 = mock(SimpleLocationRequest.class);
        when(loc1.getLatitude()).thenReturn(48.2);
        when(loc1.getLongitude()).thenReturn(16.3);
        final SimpleLocationRequest loc2 = mock(SimpleLocationRequest.class);
        when(loc2.getLatitude()).thenReturn(48.25);
        when(loc2.getLongitude()).thenReturn(16.35);

        final FencesRequest req1 = mock(FencesRequest.class);
        when(req1.getOffsetMs()).thenReturn(0L);
        when(req1.getLocation()).thenReturn(loc1);
        when(req1.getAccuracy()).thenReturn(9.5);
        when(req1.getProvider()).thenReturn("gps");
        final FencesRequest req2 = mock(FencesRequest.class);
        when(req2.getOffsetMs()).thenReturn(5000L);
        when(req2.getLocation()).thenReturn(loc2);
        when(req2.getAccuracy()).thenReturn(7.0);
        when(req2.getProvider()).thenReturn("network");

        final Fences fence1 = Fences.builder().build();
        final Fences fence2 = Fences.builder().build();
        when(fencesMapper.fencesRequestToFences(req1, test)).thenReturn(fence1);
        when(fencesMapper.fencesRequestToFences(req2, test)).thenReturn(fence2);

        final GeoLocation geo1 = mock(GeoLocation.class);
        final GeoLocation geo2 = mock(GeoLocation.class);
        when(geoLocationService.createAndAssignGeoLocation(test, 48.2, 16.3, 9.5, "gps", testTime.plus(0, ChronoUnit.MILLIS)))
                .thenReturn(geo1);
        when(geoLocationService.createGeoLocation(test, 48.25, 16.35, 7.0, "network", testTime.plus(5000, ChronoUnit.MILLIS)))
                .thenReturn(geo2);

        fencesService.processFencesRequests(List.of(req1, req2), test);

        // first fence's geo_location is created AND assigned to the test; the rest are only created
        verify(geoLocationService).createAndAssignGeoLocation(test, 48.2, 16.3, 9.5, "gps", testTime.plus(0, ChronoUnit.MILLIS));
        verify(geoLocationService).createGeoLocation(test, 48.25, 16.35, 7.0, "network", testTime.plus(5000, ChronoUnit.MILLIS));
        // each fence references its own geo_location
        assertSame(geo1, fence1.getGeoLocation());
        assertSame(geo2, fence2.getGeoLocation());
    }

    @Test
    void processFencesRequests_emptyCollection_savesNothingAndCountIsZero() {
        final at.rtr.rmbt.model.Test test = new at.rtr.rmbt.model.Test();

        fencesService.processFencesRequests(List.of(), test);

        assertEquals(0L, test.getFencesCount());
        verify(fencesRepository).saveAll(fencesCaptor.capture());
        assertTrue(fencesCaptor.getValue().isEmpty());
    }
}
