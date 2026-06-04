package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.model.Fences;
import at.rtr.rmbt.request.FencesRequest;
import at.rtr.rmbt.request.SimpleLocationRequest;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link FencesMapperImpl}: the request fields are copied onto the entity, the
 * fence id comes from the running per-test counter, and the geometry is built in EPSG:4326.
 */
class FencesMapperImplTest {

    private static final double LON = 15.401262012505189;
    private static final double LAT = 46.99121587668888;

    private final FencesMapperImpl mapper = new FencesMapperImpl();

    @Test
    void fencesRequestToFences_mapsFieldsAndBuildsGeometry() {
        final UUID openTestUuid = UUID.randomUUID();

        final SimpleLocationRequest location = mock(SimpleLocationRequest.class);
        when(location.getLatitude()).thenReturn(LAT);
        when(location.getLongitude()).thenReturn(LON);

        final FencesRequest request = mock(FencesRequest.class);
        when(request.getLocation()).thenReturn(location);
        when(request.getTechnology()).thenReturn("LTE");
        when(request.getTechnologyId()).thenReturn(13L);
        when(request.getRadius()).thenReturn(20.0);
        when(request.getAccuracy()).thenReturn(13.014);
        when(request.getProvider()).thenReturn("gps");

        final at.rtr.rmbt.model.Test test = mock(at.rtr.rmbt.model.Test.class);
        when(test.getOpenTestUuid()).thenReturn(openTestUuid);

        final Fences fences = mapper.fencesRequestToFences(request, test);

        // fenceId is assigned by the service, not the mapper
        assertNull(fences.getFenceId());
        assertEquals(openTestUuid, fences.getOpenTestUUID());
        assertEquals("LTE", fences.getTechnology());
        assertEquals(Long.valueOf(13), fences.getTechnologyId());
        assertEquals(13.014, fences.getAccuracy());
        assertEquals("gps", fences.getProvider());

        final Geometry geom = fences.getGeom4326();
        assertNotNull(geom);
        assertEquals(4326, geom.getSRID());
        assertEquals(LON, geom.getCoordinate().x, 1e-9);
        assertEquals(LAT, geom.getCoordinate().y, 1e-9);
    }
}
