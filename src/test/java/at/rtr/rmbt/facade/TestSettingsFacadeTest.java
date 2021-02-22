package at.rtr.rmbt.facade;

import at.rtr.rmbt.TestUtils;
import at.rtr.rmbt.config.RollBackService;
import at.rtr.rmbt.model.LoopModeSettings;
import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.enums.ClientType;
import at.rtr.rmbt.enums.ServerType;
import at.rtr.rmbt.enums.TestPlatform;
import at.rtr.rmbt.enums.TestStatus;
import at.rtr.rmbt.properties.ApplicationProperties;
import at.rtr.rmbt.request.TestSettingsRequest;
import at.rtr.rmbt.response.TestSettingsResponse;
import at.rtr.rmbt.service.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static at.rtr.rmbt.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class TestSettingsFacadeTest {

    private static final TestSettingsRequest.LoopModeInfo loopModeInfo = new TestSettingsRequest.LoopModeInfo(
            DEFAULT_UID,
            DEFAULT_UUID.toString(),
            DEFAULT_CLIENT_UUID.toString(),
            1,
            2,
            3,
            4,
            -1,
            null
    );

    private static final TestSettingsRequest testSettingsRequest = TestSettingsRequest.builder()
            .platform(TestPlatform.ANDROID)
            .softwareVersionCode(1)
            .ndt(false)
            .previousTestStatus(null)
            .testCounter(-1)
            .softwareRevision("1")
            .softwareVersion("1")
            .userServerSelection(true)
            .preferredServer(DEFAULT_UUID.toString())
            .numberOfThreads(5)
            .protocolVersion(TestSettingsRequest.ProtocolVersion.IPV4)
            .location(null)
            .time(System.currentTimeMillis())
            .timezone("Europe/Bratislava")
            .serverType(ServerType.RMBT)
            .testSetVersion("1.2")
            .clientType(ClientType.DESKTOP)
            .uuid(DEFAULT_CLIENT_UUID.toString())
            .language("en")
            .userLoopMode(true)
            .loopModeInfo(loopModeInfo)
            .capabilities(null)
            .androidPermissionStatus(null)
            .build();

    private static final TestServer testServer = TestServer.builder()
            .uid(11L)
            .uuid(DEFAULT_UUID)
            .serverTypes(Set.of(ServerType.RMBT))
            .active(true)
            .country("Ukraine")
            .key("test_server_key")
            .portSsl(23)
            .port(22)
            .encrypted(true)
            .build();

    private static final at.rtr.rmbt.model.ClientType clientType = new at.rtr.rmbt.model.ClientType(10L, ClientType.DESKTOP);

    private static final RtrClient client = new RtrClient(
            2L,
            UUID.randomUUID(),
            clientType,
            ZONED_DATE_TIME,
            null,
            null,
            true,
            ZONED_DATE_TIME,
            false,
            1L,
            ZONED_DATE_TIME,
            ZONED_DATE_TIME
    );

    private final LoopModeSettingsService loopModeSettingsService = mock(LoopModeSettingsService.class);
    private final ClientTypeService clientTypeService = mock(ClientTypeService.class);
    private final ClientService clientService = mock(ClientService.class);
    private final TestServerService testServerService = mock(TestServerService.class);
    private final TestService testService = mock(TestService.class);
    private final MessageSource messageSource = mock(MessageSource.class);
    private final ApplicationProperties applicationProperties = new ApplicationProperties(
            new ApplicationProperties.LanguageProperties(Set.of("en", "de"), "en"),
            Set.of("RMBT", "RMBTjs", "Open-RMBT", "RMBTws", "HW-PROBE"),
            "1.2",
            1,
            2,
            3
    );
    private final RollBackService rollBackService = mock(RollBackService.class);

    private final TestSettingsFacade facade = new TestSettingsFacade(
            loopModeSettingsService,
            clientTypeService,
            clientService,
            testServerService,
            testService,
            applicationProperties,
            messageSource,
            TestUtils.mapper,
            rollBackService
    );

    private MockHttpServletRequest request;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        request.setRemoteAddr("185.38.216.246");

    }

    @Test
    public void updateTestSettings() {
        ArgumentCaptor<at.rtr.rmbt.model.Test> testArgumentCaptor = ArgumentCaptor.forClass(at.rtr.rmbt.model.Test.class);

        when(loopModeSettingsService.save(any())).then(new Answer<>() {
            private int counter = 0;

            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                counter++;
                LoopModeSettings argument = invocationOnMock.getArgument(0);

                assertNotNull(argument.getLoopUuid());
                assertEquals(loopModeInfo.getTestCounter(), argument.getTestCounter());
                assertEquals(loopModeInfo.getMaxDelay(), argument.getMaxDelay());
                assertEquals(loopModeInfo.getMaxTests(), argument.getMaxTests());
                assertEquals(loopModeInfo.getMaxMovement(), argument.getMaxMovement());

                if (counter <= 1) {
                    assertEquals(loopModeInfo.getTestUuid(), argument.getTestUuid().toString());
                    client.setUuid(argument.getClientUuid());
                    when(clientService.getClientByUUID(argument.getClientUuid())).thenReturn(client);
                } else {
                    assertNotEquals(loopModeInfo.getTestUuid(), argument.getTestUuid().toString());
                }

                return argument;
            }
        });
        when(clientTypeService.findByClientType(ClientType.DESKTOP)).thenReturn(Optional.of(clientType));
        when(testServerService.findActiveByServerTypeInAndCountry(any(), eq(testServer.getCountry()))).thenReturn(testServer);
        when(testServerService.findByUuidAndActive(DEFAULT_UUID, true)).thenReturn(Optional.of(testServer));
        when(testService.save(any())).thenAnswer(a -> {
            at.rtr.rmbt.model.Test argument = a.getArgument(0);

            return argument.toBuilder().uid(23L).build();
        });

        TestSettingsResponse result = facade.updateTestSettings(testSettingsRequest, request);

        verify(loopModeSettingsService, times(2)).save(any());
        verify(testService, times(2)).save(testArgumentCaptor.capture());

        at.rtr.rmbt.model.Test firstTestResult = testArgumentCaptor.getAllValues().get(0);
        at.rtr.rmbt.model.Test secondTestResult = testArgumentCaptor.getAllValues().get(1);

        assertTestResult(request, testSettingsRequest, testServer, client, firstTestResult);
        assertTestResult(request, testSettingsRequest, testServer, client, secondTestResult);
        assertNull(firstTestResult.getUid());
        assertEquals(23L, secondTestResult.getUid());

        assertEquals(result.getTestToken(), secondTestResult.getToken());
        assertEquals(result.getOpenTestUuid().substring(1), secondTestResult.getOpenTestUuid().toString());
        assertEquals(result.getTestUuid(), secondTestResult.getUuid().toString());
        assertEquals(result.getTestId(), secondTestResult.getUid());
        assertEquals(result.getTestServerPort(), testServer.getPortSsl());
        assertEquals(result.getTestDuration(), applicationProperties.getDuration().toString());
        assertEquals(result.getTestNumberOfThreads(), secondTestResult.getNumberOfThreads().toString());
        assertEquals(result.getTestNumberOfPings(), applicationProperties.getPings().toString());
        assertEquals(result.getClientRemoteIp(), request.getRemoteAddr());
    }

    @Mock
    private at.rtr.rmbt.model.Test test;
    @Mock
    private LoopModeSettings loopModeSettings;

    @Test
    public void updateTestSettings_encryptedFalse_usedPortField() {
        mockStubToTestEncryption();
        testServer.setEncrypted(false);
        testServer.setPortSsl(123);
        testServer.setPort(321);
        TestSettingsResponse result = facade.updateTestSettings(testSettingsRequest, request);
        assertEquals(321, result.getTestServerPort());
        assertFalse(result.getTestServerEncryption());
    }

    @Test
    public void updateTestSettings_encryptedTrue_usedSSLPortField() {
        mockStubToTestEncryption();
        testServer.setEncrypted(true);
        testServer.setPortSsl(123);
        testServer.setPort(321);
        TestSettingsResponse result = facade.updateTestSettings(testSettingsRequest, request);
        assertEquals(123, result.getTestServerPort());
        assertTrue(result.getTestServerEncryption());
    }

    @Test
    public void updateTestSettings_existInServerTypes_expectFromResponse() {
        mockStubToTestEncryption();
        testServer.setServerTypes(Set.of(ServerType.RMBT));
        TestSettingsResponse result = facade.updateTestSettings(testSettingsRequest, request);
        assertEquals(ServerType.RMBT, result.getTestServerType());
    }

    @Test
    public void updateTestSettings_notExistInServerTypes_expectFromTypesSet() {
        mockStubToTestEncryption();
        testServer.setServerTypes(Set.of(ServerType.HW_PROBE));
        TestSettingsResponse result = facade.updateTestSettings(testSettingsRequest, request);
        assertEquals(ServerType.HW_PROBE, result.getTestServerType());
    }

    @Test
    public void updateTestSettings_emptyServerTypeListNotPossibleCase_expectNull() {
        mockStubToTestEncryption();
        testServer.setServerTypes(Collections.emptySet());
        when(messageSource.getMessage(eq("ERROR_TEST_SERVER"), eq(null), eq(Locale.ENGLISH))).thenReturn(DEFAULT_TEXT);
        TestSettingsResponse response = facade.updateTestSettings(testSettingsRequest, request);
        assertEquals(DEFAULT_TEXT, response.getErrorList().get(0));
    }

    private void mockStubToTestEncryption() {
        when(clientTypeService.findByClientType(ClientType.DESKTOP)).thenReturn(Optional.of(clientType));
        when(testServerService.findActiveByServerTypeInAndCountry(any(), eq(testServer.getCountry()))).thenReturn(testServer);
        when(testServerService.findByUuidAndActive(DEFAULT_UUID, true)).thenReturn(Optional.of(testServer));
        when(testService.save(any())).thenReturn(test);
        when(clientService.getClientByUUID(any())).thenReturn(client);
        when(loopModeSettingsService.save(any())).thenReturn(loopModeSettings);
        when(loopModeSettings.getLoopUuid()).thenReturn(DEFAULT_UUID);
    }

    private void assertTestResult(MockHttpServletRequest request, TestSettingsRequest testSettingsRequest, TestServer testServer, RtrClient client, at.rtr.rmbt.model.Test testResult) {
        assertEquals(client.getUid(), testResult.getClient().getUid());
        assertEquals(testSettingsRequest.getServerType(), testResult.getClientName());
        assertEquals(testSettingsRequest.getTestSetVersion(), testResult.getClientVersion());
        assertEquals(testSettingsRequest.getSoftwareVersion(), testResult.getClientSoftwareVersion());
        assertEquals(request.getRemoteAddr(), testResult.getClientPublicIp());
        assertEquals(testServer.getUid(), testResult.getServerId());
        assertEquals(testServer.getPortSsl(), testResult.getServerPort());
        assertEquals(applicationProperties.getDuration(), testResult.getDuration());
        assertEquals(testSettingsRequest.getNumberOfThreads(), testResult.getNumberOfThreads());
        assertEquals(TestStatus.STARTED, testResult.getStatus());
        assertEquals(testSettingsRequest.isNdt(), testResult.getRunNdt());
        assertEquals(testSettingsRequest.getSoftwareRevision(), testResult.getSoftwareRevision());
        assertTrue(testResult.getUseSsl());
    }

}
