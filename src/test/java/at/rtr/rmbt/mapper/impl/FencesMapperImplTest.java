package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.model.Fences;
import at.rtr.rmbt.request.FencesRequest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link FencesMapperImpl}: the non-location request fields are copied onto the
 * entity. The centre location is no longer stored on the fence (it lives in geo_location and is
 * referenced via geo_location_uuid), and fenceId/fenceTime/geoLocationUuid are assigned by the
 * service, not the mapper.
 */
class FencesMapperImplTest {

    private final FencesMapperImpl mapper = new FencesMapperImpl();

    @Test
    void fencesRequestToFences_mapsNonLocationFields() {
        final UUID openTestUuid = UUID.randomUUID();

        final FencesRequest request = mock(FencesRequest.class);
        when(request.getTechnology()).thenReturn("LTE");
        when(request.getTechnologyId()).thenReturn(13L);
        when(request.getRadius()).thenReturn(20.0);

        final at.rtr.rmbt.model.Test test = mock(at.rtr.rmbt.model.Test.class);
        when(test.getOpenTestUuid()).thenReturn(openTestUuid);

        final Fences fences = mapper.fencesRequestToFences(request, test);

        assertEquals(openTestUuid, fences.getOpenTestUUID());
        assertEquals("LTE", fences.getTechnology());
        assertEquals(Long.valueOf(13), fences.getTechnologyId());
        assertEquals(20.0, fences.getRadius());
        // location/geo reference and counters are assigned by the service, not the mapper
        assertNull(fences.getFenceId());
        assertNull(fences.getGeoLocation());
    }
}
