package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.mapper.FencesMapper;
import at.rtr.rmbt.model.Fences;
import at.rtr.rmbt.repository.FencesRepository;
import at.rtr.rmbt.request.FencesRequest;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link FencesServiceImpl}: each request is mapped, the per-test fence counter is
 * advanced, every fence is stamped with {@code testTime + offsetMs}, and the batch is saved.
 */
@ExtendWith(MockitoExtension.class)
class FencesServiceImplTest {

    @Mock
    private FencesMapper fencesMapper;
    @Mock
    private FencesRepository fencesRepository;
    @InjectMocks
    private FencesServiceImpl fencesService;

    @Captor
    private ArgumentCaptor<List<Fences>> fencesCaptor;

    @Test
    void processFencesRequests_countsStampsFenceTimeAndSaves() {
        final ZonedDateTime testTime = ZonedDateTime.parse("2024-01-01T00:00:00Z");
        final at.rtr.rmbt.model.Test test = new at.rtr.rmbt.model.Test();
        test.setTime(testTime);

        final FencesRequest req1 = org.mockito.Mockito.mock(FencesRequest.class);
        when(req1.getOffsetMs()).thenReturn(1000L);
        final FencesRequest req2 = org.mockito.Mockito.mock(FencesRequest.class);
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
    void processFencesRequests_emptyCollection_savesNothingAndCountIsZero() {
        final at.rtr.rmbt.model.Test test = new at.rtr.rmbt.model.Test();

        fencesService.processFencesRequests(List.of(), test);

        assertEquals(0L, test.getFencesCount());
        verify(fencesRepository).saveAll(fencesCaptor.capture());
        assertTrue(fencesCaptor.getValue().isEmpty());
    }
}
