package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.exception.SyncException;
import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.model.SyncGroup;
import at.rtr.rmbt.properties.ApplicationProperties;
import at.rtr.rmbt.repository.ClientRepository;
import at.rtr.rmbt.repository.SyncGroupRepository;
import at.rtr.rmbt.request.SyncRequest;
import at.rtr.rmbt.response.SyncItemResponse;
import at.rtr.rmbt.service.ClientService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class RtrClientServiceImplTest {
    private ClientService clientService;

    @MockBean
    private ClientRepository clientRepository;
    @MockBean
    private SyncGroupRepository syncGroupRepository;

    private MessageSource messageSource;
    private final ApplicationProperties applicationProperties = new ApplicationProperties(
            new ApplicationProperties.LanguageProperties(Set.of("en", "de"), "en"),
            Set.of("RMBT", "RMBTjs", "Open-RMBT", "RMBTws", "HW-PROBE"),
            "1.2",
            1,
            2,
            3,
            10000,
            2000
    );

    @Mock
    private RtrClient rtrClient;
    @Mock
    private RtrClient savedRtrClient;
    @Mock
    private SyncRequest syncRequest;
    @Mock
    private RtrClient clientBySyncCode;
    @Mock
    private RtrClient clientByClientUUID;
    @Mock
    private SyncGroup savedSyncGroup;

    @Before
    public void setUp() {
        Locale.setDefault(Locale.ENGLISH);
        ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        reloadableResourceBundleMessageSource.setBasename("classpath:SystemMessages");
        reloadableResourceBundleMessageSource.setDefaultEncoding("UTF-8");
        messageSource = reloadableResourceBundleMessageSource;
        clientService = new ClientServiceImpl(clientRepository, applicationProperties, messageSource, syncGroupRepository);
    }

    @Test
    public void getClientByUUID_whenClientExist_expectClient() {
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(rtrClient));

        var response = clientService.getClientByUUID(TestConstants.DEFAULT_CLIENT_UUID);

        assertEquals(rtrClient, response);
    }

    @Test
    public void getClientByUUID_whenClientNotExist_expectNull() {
        var response = clientService.getClientByUUID(TestConstants.DEFAULT_CLIENT_UUID);

        assertNull(response);
    }

    @Test
    public void saveClient_whenCommonData_expectSaved() {
        when(clientRepository.save(rtrClient)).thenReturn(savedRtrClient);

        var response = clientService.saveClient(rtrClient);

        assertEquals(savedRtrClient, response);
    }

    @Test
    public void sync_whenSyncCodeIsBlank_expectSyncResponse() {
        when(syncRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(syncRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(clientRepository.getSyncCode(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(TestConstants.DEFAULT_SYNC_CODE));
        var expectedResponseItem = SyncItemResponse.builder()
                .syncCode(TestConstants.DEFAULT_SYNC_CODE.toLowerCase(Locale.US))
                .build();

        var response = clientService.sync(syncRequest);

        assertEquals(List.of(expectedResponseItem), response.getSync());
    }

    @Test
    public void sync_whenSyncCodeIsNotBlankAndClientBySyncCodeNotFound_expectSyncResponse() {
        when(syncRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(syncRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(syncRequest.getSyncCode()).thenReturn(TestConstants.DEFAULT_SYNC_CODE);
        when(clientRepository.getClientBySyncCode(TestConstants.DEFAULT_SYNC_CODE.toUpperCase(Locale.US))).thenReturn(Optional.empty());
        when(clientRepository.getRtrClientByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(clientByClientUUID));
        when(clientByClientUUID.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_CLIENT_UUID);
        var expectedResponseItem = SyncItemResponse.builder()
                .msgTitle(TestConstants.DEFAULT_SYNC_WRONG_CODE_MSG_TITLE)
                .msgText(TestConstants.DEFAULT_SYNC_WRONG_CODE_MSG_TEXT)
                .build();

        var response = clientService.sync(syncRequest);

        assertEquals(List.of(expectedResponseItem), response.getSync());
    }

    @Test
    public void sync_whenSyncCodeIsNotBlankAndClientByUUIDNotFound_expectSyncResponse() {
        when(syncRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(syncRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(syncRequest.getSyncCode()).thenReturn(TestConstants.DEFAULT_SYNC_CODE);
        when(clientRepository.getClientBySyncCode(TestConstants.DEFAULT_SYNC_CODE.toUpperCase(Locale.US))).thenReturn(Optional.of(clientBySyncCode));
        when(clientBySyncCode.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_SYNC_CODE);
        when(clientRepository.getRtrClientByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.empty());
        var expectedResponseItem = SyncItemResponse.builder()
                .msgTitle(TestConstants.DEFAULT_SYNC_UNKNOWN_CLIENT_MSG_TITLE)
                .msgText(TestConstants.DEFAULT_SYNC_UNKNOWN_CLIENT_MSG_TEXT)
                .build();

        var response = clientService.sync(syncRequest);

        assertEquals(List.of(expectedResponseItem), response.getSync());
    }

    @Test
    public void sync_whenSyncCodeIsNotBlankAndClientsHaveNotSyncGroup_expectSyncResponse() {
        when(syncGroupRepository.save(any())).thenReturn(savedSyncGroup);
        when(savedSyncGroup.getUid()).thenReturn(TestConstants.DEFAULT_SYNC_GROUP_UID);
        when(syncRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(syncRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(syncRequest.getSyncCode()).thenReturn(TestConstants.DEFAULT_SYNC_CODE);
        when(clientRepository.getClientBySyncCode(TestConstants.DEFAULT_SYNC_CODE.toUpperCase(Locale.US))).thenReturn(Optional.of(clientBySyncCode));
        when(clientBySyncCode.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_SYNC_CODE);
        when(clientRepository.getRtrClientByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(clientByClientUUID));
        when(clientByClientUUID.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_CLIENT_UUID);
        when(clientRepository.updateSyncGroupIdByTwoUids(TestConstants.DEFAULT_SYNC_GROUP_UID, TestConstants.DEFAULT_CLIENT_UID_BY_SYNC_CODE, TestConstants.DEFAULT_CLIENT_UID_BY_CLIENT_UUID)).thenReturn(1);
        var expectedResponseItem = SyncItemResponse.builder()
                .success(true)
                .msgText(TestConstants.DEFAULT_SYNC_SUCCESSFUL_MSG_TEXT)
                .msgTitle(TestConstants.DEFAULT_SYNC_SUCCESSFUL_MSG_TITLE)
                .build();

        var response = clientService.sync(syncRequest);

        assertEquals(List.of(expectedResponseItem), response.getSync());
    }

    @Test(expected = SyncException.class)
    public void sync_whenSyncCodeIsNotBlankAndClientsHaveNotSyncGroup_expectException() {
        when(syncGroupRepository.save(any())).thenReturn(savedSyncGroup);
        when(savedSyncGroup.getUid()).thenReturn(TestConstants.DEFAULT_SYNC_GROUP_UID);
        when(syncRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(syncRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(syncRequest.getSyncCode()).thenReturn(TestConstants.DEFAULT_SYNC_CODE);
        when(clientRepository.getClientBySyncCode(TestConstants.DEFAULT_SYNC_CODE.toUpperCase(Locale.US))).thenReturn(Optional.of(clientBySyncCode));
        when(clientBySyncCode.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_SYNC_CODE);
        when(clientBySyncCode.getSyncGroupId()).thenReturn(0);
        when(clientRepository.getRtrClientByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(clientByClientUUID));
        when(clientByClientUUID.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_CLIENT_UUID);
        when(clientByClientUUID.getSyncGroupId()).thenReturn(0);
        when(clientRepository.updateSyncGroupIdByTwoUids(TestConstants.DEFAULT_SYNC_GROUP_UID, TestConstants.DEFAULT_CLIENT_UID_BY_SYNC_CODE, TestConstants.DEFAULT_CLIENT_UID_BY_CLIENT_UUID)).thenReturn(0);

        clientService.sync(syncRequest);
    }

    @Test
    public void sync_whenSyncCodeIsNotBlankAndClientBySyncCodeHasSyncGroupAndClientByClientUUIDHasNotSyncGroup_expectSyncResponse() {
        when(syncRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(syncRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(syncRequest.getSyncCode()).thenReturn(TestConstants.DEFAULT_SYNC_CODE);
        when(clientRepository.getClientBySyncCode(TestConstants.DEFAULT_SYNC_CODE.toUpperCase(Locale.US))).thenReturn(Optional.of(clientBySyncCode));
        when(clientBySyncCode.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_SYNC_CODE);
        when(clientBySyncCode.getSyncGroupId()).thenReturn(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_SYNC_CODE);
        when(clientRepository.getRtrClientByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(clientByClientUUID));
        when(clientByClientUUID.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_CLIENT_UUID);
        when(clientByClientUUID.getSyncGroupId()).thenReturn(0);
        when(clientRepository.updateSyncGroupIdByUid(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_SYNC_CODE, TestConstants.DEFAULT_CLIENT_UID_BY_CLIENT_UUID)).thenReturn(1);
        var expectedResponseItem = SyncItemResponse.builder()
                .success(true)
                .msgText(TestConstants.DEFAULT_SYNC_SUCCESSFUL_MSG_TEXT)
                .msgTitle(TestConstants.DEFAULT_SYNC_SUCCESSFUL_MSG_TITLE)
                .build();

        var response = clientService.sync(syncRequest);

        assertEquals(List.of(expectedResponseItem), response.getSync());
    }

    @Test(expected = SyncException.class)
    public void sync_whenSyncCodeIsNotBlankAndClientBySyncCodeHasSyncGroupAndClientByClientUUIDHasNotSyncGroup_expectException() {
        when(syncRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(syncRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(syncRequest.getSyncCode()).thenReturn(TestConstants.DEFAULT_SYNC_CODE);
        when(clientRepository.getClientBySyncCode(TestConstants.DEFAULT_SYNC_CODE.toUpperCase(Locale.US))).thenReturn(Optional.of(clientBySyncCode));
        when(clientBySyncCode.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_SYNC_CODE);
        when(clientBySyncCode.getSyncGroupId()).thenReturn(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_SYNC_CODE);
        when(clientRepository.getRtrClientByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(clientByClientUUID));
        when(clientByClientUUID.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_CLIENT_UUID);
        when(clientByClientUUID.getSyncGroupId()).thenReturn(0);
        when(clientRepository.updateSyncGroupIdByUid(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_SYNC_CODE, TestConstants.DEFAULT_CLIENT_UID_BY_CLIENT_UUID)).thenReturn(0);

        clientService.sync(syncRequest);
    }

    @Test
    public void sync_whenSyncCodeIsNotBlankAndClientBySyncCodeHasNotSyncGroupAndClientByClientUUIDHasSyncGroup_expectSyncResponse() {
        when(syncRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(syncRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(syncRequest.getSyncCode()).thenReturn(TestConstants.DEFAULT_SYNC_CODE);
        when(clientRepository.getClientBySyncCode(TestConstants.DEFAULT_SYNC_CODE.toUpperCase(Locale.US))).thenReturn(Optional.of(clientBySyncCode));
        when(clientBySyncCode.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_SYNC_CODE);
        when(clientBySyncCode.getSyncGroupId()).thenReturn(0);
        when(clientRepository.getRtrClientByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(clientByClientUUID));
        when(clientByClientUUID.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_CLIENT_UUID);
        when(clientByClientUUID.getSyncGroupId()).thenReturn(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_UUID);
        when(clientRepository.updateSyncGroupIdByUid(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_UUID, TestConstants.DEFAULT_CLIENT_UID_BY_SYNC_CODE)).thenReturn(1);
        var expectedResponseItem = SyncItemResponse.builder()
                .success(true)
                .msgText(TestConstants.DEFAULT_SYNC_SUCCESSFUL_MSG_TEXT)
                .msgTitle(TestConstants.DEFAULT_SYNC_SUCCESSFUL_MSG_TITLE)
                .build();

        var response = clientService.sync(syncRequest);

        assertEquals(List.of(expectedResponseItem), response.getSync());
    }

    @Test(expected = SyncException.class)
    public void sync_whenSyncCodeIsNotBlankAndClientBySyncCodeHasNotSyncGroupAndClientByClientUUIDHasSyncGroup_expectException() {
        when(syncRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(syncRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(syncRequest.getSyncCode()).thenReturn(TestConstants.DEFAULT_SYNC_CODE);
        when(clientRepository.getClientBySyncCode(TestConstants.DEFAULT_SYNC_CODE.toUpperCase(Locale.US))).thenReturn(Optional.of(clientBySyncCode));
        when(clientBySyncCode.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_SYNC_CODE);
        when(clientBySyncCode.getSyncGroupId()).thenReturn(0);
        when(clientRepository.getRtrClientByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(clientByClientUUID));
        when(clientByClientUUID.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_CLIENT_UUID);
        when(clientByClientUUID.getSyncGroupId()).thenReturn(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_UUID);
        when(savedSyncGroup.getUid()).thenReturn(TestConstants.DEFAULT_SYNC_GROUP_UID);
        when(clientRepository.updateSyncGroupIdByUid(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_UUID, TestConstants.DEFAULT_CLIENT_UID_BY_CLIENT_UUID)).thenReturn(0);

        clientService.sync(syncRequest);
    }

    @Test
    public void sync_whenSyncCodeIsNotBlankAndClientBySyncCodeHasSyncGroupAndClientByClientUUIDHasSyncGroup_expectSyncResponse() {
        when(syncRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(syncRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(syncRequest.getSyncCode()).thenReturn(TestConstants.DEFAULT_SYNC_CODE);
        when(clientRepository.getClientBySyncCode(TestConstants.DEFAULT_SYNC_CODE.toUpperCase(Locale.US))).thenReturn(Optional.of(clientBySyncCode));
        when(clientBySyncCode.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_SYNC_CODE);
        when(clientBySyncCode.getSyncGroupId()).thenReturn(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_SYNC_CODE);
        when(clientRepository.getRtrClientByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(clientByClientUUID));
        when(clientByClientUUID.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_CLIENT_UUID);
        when(clientByClientUUID.getSyncGroupId()).thenReturn(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_UUID);
        when(clientRepository.updateSyncGroupIdBySyncGroupId(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_SYNC_CODE, TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_UUID)).thenReturn(1);
        when(syncGroupRepository.deleteBySyncGroupId(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_UUID)).thenReturn(1);
        var expectedResponseItem = SyncItemResponse.builder()
                .success(true)
                .msgText(TestConstants.DEFAULT_SYNC_SUCCESSFUL_MSG_TEXT)
                .msgTitle(TestConstants.DEFAULT_SYNC_SUCCESSFUL_MSG_TITLE)
                .build();

        var response = clientService.sync(syncRequest);

        assertEquals(List.of(expectedResponseItem), response.getSync());
    }

    @Test(expected = SyncException.class)
    public void sync_whenSyncCodeIsNotBlankAndClientBySyncCodeHasSyncGroupAndClientByClientUUIDHasSyncGroup_expectException() {
        when(syncRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(syncRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(syncRequest.getSyncCode()).thenReturn(TestConstants.DEFAULT_SYNC_CODE);
        when(clientRepository.getClientBySyncCode(TestConstants.DEFAULT_SYNC_CODE.toUpperCase(Locale.US))).thenReturn(Optional.of(clientBySyncCode));
        when(clientBySyncCode.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_SYNC_CODE);
        when(clientBySyncCode.getSyncGroupId()).thenReturn(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_SYNC_CODE);
        when(clientRepository.getRtrClientByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(clientByClientUUID));
        when(clientByClientUUID.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_CLIENT_UUID);
        when(clientByClientUUID.getSyncGroupId()).thenReturn(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_UUID);
        when(clientRepository.updateSyncGroupIdBySyncGroupId(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_SYNC_CODE, TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_UUID)).thenReturn(0);

        clientService.sync(syncRequest);
    }

    @Test(expected = SyncException.class)
    public void sync_whenSyncCodeIsNotBlankAndClientBySyncCodeHasSyncGroupAndClientByClientUUIDHasSyncGroup_expectExceptionWhenDelete() {
        when(syncRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(syncRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(syncRequest.getSyncCode()).thenReturn(TestConstants.DEFAULT_SYNC_CODE);
        when(clientRepository.getClientBySyncCode(TestConstants.DEFAULT_SYNC_CODE.toUpperCase(Locale.US))).thenReturn(Optional.of(clientBySyncCode));
        when(clientBySyncCode.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_SYNC_CODE);
        when(clientBySyncCode.getSyncGroupId()).thenReturn(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_SYNC_CODE);
        when(clientRepository.getRtrClientByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(clientByClientUUID));
        when(clientByClientUUID.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_CLIENT_UUID);
        when(clientByClientUUID.getSyncGroupId()).thenReturn(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_UUID);
        when(clientRepository.updateSyncGroupIdBySyncGroupId(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_SYNC_CODE, TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_UUID)).thenReturn(1);
        when(syncGroupRepository.deleteBySyncGroupId(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_UUID)).thenReturn(0);

        clientService.sync(syncRequest);
    }

    @Test
    public void sync_whenSyncCodeIsNotBlankAndClientBySyncCodeAndClientByClientUUIDHaveEqualSyncGroup_expectSyncResponse() {
        when(syncRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(syncRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(syncRequest.getSyncCode()).thenReturn(TestConstants.DEFAULT_SYNC_CODE);
        when(clientRepository.getClientBySyncCode(TestConstants.DEFAULT_SYNC_CODE.toUpperCase(Locale.US))).thenReturn(Optional.of(clientBySyncCode));
        when(clientBySyncCode.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_SYNC_CODE);
        when(clientBySyncCode.getSyncGroupId()).thenReturn(TestConstants.DEFAULT_SYNC_GROUP_UID);
        when(clientRepository.getRtrClientByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(clientByClientUUID));
        when(clientByClientUUID.getUid()).thenReturn(TestConstants.DEFAULT_CLIENT_UID_BY_CLIENT_UUID);
        when(clientByClientUUID.getSyncGroupId()).thenReturn(TestConstants.DEFAULT_SYNC_GROUP_UID);
        var expectedResponseItem = SyncItemResponse.builder()
                .success(false)
                .msgText(TestConstants.DEFAULT_SYNC_ALREADY_SYNCHRONIZED_MSG_TEXT)
                .msgTitle(TestConstants.DEFAULT_SYNC_ALREADY_SYNCHRONIZED_MSG_TITLE)
                .build();

        var response = clientService.sync(syncRequest);

        assertEquals(List.of(expectedResponseItem), response.getSync());
    }

    @Test
    public void sync_whenSyncCodeIsNotBlankAndClientBySyncCodeAndClientByClientUUIDIsSaveDevice_expectSyncResponse() {
        when(syncRequest.getUuid()).thenReturn(TestConstants.DEFAULT_CLIENT_UUID);
        when(syncRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(syncRequest.getSyncCode()).thenReturn(TestConstants.DEFAULT_SYNC_CODE);
        when(clientRepository.getClientBySyncCode(TestConstants.DEFAULT_SYNC_CODE.toUpperCase(Locale.US))).thenReturn(Optional.of(clientBySyncCode));
        when(clientBySyncCode.getUid()).thenReturn(TestConstants.DEFAULT_UID);
        when(clientBySyncCode.getSyncGroupId()).thenReturn(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_SYNC_CODE);
        when(clientRepository.getRtrClientByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(clientByClientUUID));
        when(clientByClientUUID.getUid()).thenReturn(TestConstants.DEFAULT_UID);
        when(clientByClientUUID.getSyncGroupId()).thenReturn(TestConstants.DEFAULT_CLIENT_SYNC_GROUP_UID_BY_UUID);
        var expectedResponseItem = SyncItemResponse.builder()
                .success(false)
                .msgText(TestConstants.DEFAULT_SYNC_SAME_DEVICE_MSG_TEXT)
                .msgTitle(TestConstants.DEFAULT_SYNC_SAME_DEVICE_MSG_TITLE)
                .build();

        var response = clientService.sync(syncRequest);

        assertEquals(List.of(expectedResponseItem), response.getSync());
    }
}
