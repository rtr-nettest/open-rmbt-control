package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.SignalMapper;
import at.rtr.rmbt.model.LoopModeSettings;
import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.model.enums.NetworkGroupName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static at.rtr.rmbt.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class SignalMapperImplTest {

    @Mock
    private RtrClient client;
    @Mock
    private LoopModeSettings loopModeSettings;

    private SignalMapper mapper;

    @Before
    public void setUp() {
        mapper = new SignalMapperImpl();
    }

    @Test
    public void signalToSignalMeasurementResponse_correctParameters_expectResponse() {
        var test = buildTest(NetworkGroupName.G2, null);
        var actual = mapper.signalToSignalMeasurementResponse(test);
        assertEquals(5, actual.getDuration());
        assertEquals(DEFAULT_ZONED_DATE_TIME, actual.getStartDateTime());
        assertEquals(DEFAULT_LOCATION, actual.getLocation());
        assertEquals(NetworkGroupName.G2.getLabelEn(), actual.getTechnology());
        assertEquals("Regular", actual.getTestType());
        assertEquals(DEFAULT_CLIENT_UUID, actual.getUserUuid());
        assertEquals(DEFAULT_UUID, actual.getTestUuid());
    }

    @Test
    public void signalToSignalMeasurementResponse_correctParametersWithNull_expectResponse() {
        var test = buildTest(null, loopModeSettings);
        var actual = mapper.signalToSignalMeasurementResponse(test);
        assertEquals(5, actual.getDuration());
        assertEquals(DEFAULT_ZONED_DATE_TIME, actual.getStartDateTime());
        assertEquals(DEFAULT_LOCATION, actual.getLocation());
        assertNull(actual.getTechnology());
        assertEquals("Loop", actual.getTestType());
        assertEquals(DEFAULT_CLIENT_UUID, actual.getUserUuid());
        assertEquals(DEFAULT_UUID, actual.getTestUuid());
    }

    private at.rtr.rmbt.model.Test buildTest(NetworkGroupName ngn, LoopModeSettings lms) {
        when(client.getUuid()).thenReturn(DEFAULT_CLIENT_UUID);
        return at.rtr.rmbt.model.Test.builder()
                .duration(5)
                .time(DEFAULT_ZONED_DATE_TIME)
                .location(DEFAULT_LOCATION)
                .uuid(DEFAULT_UUID)
                .networkGroupName(ngn)
                .loopModeSettings(lms)
                .client(client)
                .build();
    }

}
