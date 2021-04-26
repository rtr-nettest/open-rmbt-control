package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.exception.SyncException;
import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.model.SyncGroup;
import at.rtr.rmbt.properties.ApplicationProperties;
import at.rtr.rmbt.repository.ClientRepository;
import at.rtr.rmbt.repository.SyncGroupRepository;
import at.rtr.rmbt.request.SyncRequest;
import at.rtr.rmbt.response.SyncItemResponse;
import at.rtr.rmbt.response.SyncResponse;
import at.rtr.rmbt.service.ClientService;
import at.rtr.rmbt.utils.MessageUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    @Qualifier(ClientRepository.NAME)
    private final ClientRepository clientRepository;
    private final ApplicationProperties applicationProperties;
    private final MessageSource messageSource;
    private final SyncGroupRepository syncGroupRepository;

    @Override
    public RtrClient getClientByUUID(UUID uuid) {
        return clientRepository.findByUuid(uuid)
                .orElse(null);
    }

    @Override
    public RtrClient saveClient(RtrClient rtrClient) {
        return clientRepository.save(rtrClient);
    }

    @Override
    @Transactional
    public SyncResponse sync(SyncRequest syncRequest) {
        Locale locale = MessageUtils.getLocaleFormLanguage(syncRequest.getLanguage(), applicationProperties.getLanguage());
        List<SyncItemResponse> syncItemResponses = new ArrayList<>();
        if (Objects.nonNull(syncRequest.getUuid())) {
            SyncItemResponse syncItemResponse = getSyncItemResponse(syncRequest, locale);
            syncItemResponses.add(syncItemResponse);
        }
        return SyncResponse.builder()
                .sync(syncItemResponses)
                .build();
    }

    @Override
    public List<RtrClient> listSyncedClientsByClientUid(Long clientId) {
        return clientRepository.listSyncedClientsByClientUid(clientId);
    }

    private SyncItemResponse getSyncItemResponse(SyncRequest syncRequest, Locale locale) {
        if (StringUtils.isBlank(syncRequest.getSyncCode())) {
            String syncCode = clientRepository.getSyncCode(syncRequest.getUuid())
                    .orElseThrow(() -> new SyncException(messageSource.getMessage("ERROR_DB_GET_SYNC_SQL", null, locale)));
            return SyncItemResponse.builder()
                    .syncCode(syncCode.toLowerCase(Locale.US))
                    .build();
        } else {
            SyncMessageDto dto = new SyncMessageDto(syncRequest.getUuid(), syncRequest.getSyncCode(), locale)
                    .getClientUidAndValidateSyncGroup();
            if (!dto.isError()) {
                updateSyncGroupId(locale, dto.getClientSyncGroupBySyncCode(), dto.getClientUidBySyncCode(), dto.getClientSyncGroupByUUID(), dto.getClientUidByUUID());
            }
            return SyncItemResponse.builder()
                    .msgTitle(dto.getMsgTitle())
                    .msgText(dto.getMsgText())
                    .success(!dto.isError())
                    .build();
        }
    }

    private void updateSyncGroupId(Locale locale, int clientSyncGroupBySyncCode, long clientUidBySyncCode, int clientSyncGroupByUUID, long clientUidByUUID) {
        if (clientSyncGroupBySyncCode == 0 && clientSyncGroupByUUID == 0) {
            SyncGroup newSyncGroup = SyncGroup.builder()
                    .timestamp(OffsetDateTime.now())
                    .build();
            SyncGroup savedSyncGroup = syncGroupRepository.save(newSyncGroup);
            int affectedRows = clientRepository.updateSyncGroupIdByTwoUids(savedSyncGroup.getUid(), clientUidBySyncCode, clientUidByUUID);
            if (affectedRows == 0) {
                throw new SyncException(messageSource.getMessage("ERROR_DB_UPDATE_SYNC_GROUP", null, locale));
            }
        } else if (clientSyncGroupBySyncCode == 0 && clientSyncGroupByUUID > 0) {
            int affectedRows = clientRepository.updateSyncGroupIdByUid(clientSyncGroupByUUID, clientUidBySyncCode);
            if (affectedRows == 0) {
                throw new SyncException(messageSource.getMessage("ERROR_DB_UPDATE_SYNC_GROUP", null, locale));
            }
        } else if (clientSyncGroupBySyncCode > 0 && clientSyncGroupByUUID == 0) {
            int affectedRows = clientRepository.updateSyncGroupIdByUid(clientSyncGroupBySyncCode, clientUidByUUID);
            if (affectedRows == 0) {
                throw new SyncException(messageSource.getMessage("ERROR_DB_UPDATE_SYNC_GROUP", null, locale));
            }
        } else if (clientSyncGroupBySyncCode > 0 && clientSyncGroupByUUID > 0) {
            int affectedRows = clientRepository.updateSyncGroupIdBySyncGroupId(clientSyncGroupBySyncCode, clientSyncGroupByUUID);
            if (affectedRows == 0) {
                throw new SyncException(messageSource.getMessage("ERROR_DB_UPDATE_SYNC_GROUP", null, locale));
            } else {
                affectedRows = syncGroupRepository.deleteBySyncGroupId(clientSyncGroupByUUID);
                if (affectedRows == 0) {
                    throw new SyncException(messageSource.getMessage("ERROR_DB_DELETE_SYNC_GROUP", null, locale));
                }
            }
        }
    }

    @Getter
    private class SyncMessageDto {
        private final UUID clientUUID;
        private final Locale locale;
        private final String syncCode;
        private int clientSyncGroupBySyncCode;
        private long clientUidBySyncCode;
        private int clientSyncGroupByUUID;
        private long clientUidByUUID;
        private String msgTitle;
        private String msgText;
        private boolean error;

        public SyncMessageDto(UUID clientUUID, String syncCode, Locale locale) {
            this.clientUUID = clientUUID;
            this.syncCode = syncCode.toUpperCase(Locale.US);
            this.clientSyncGroupBySyncCode = 0;
            this.clientUidBySyncCode = 0;
            this.clientSyncGroupByUUID = 0;
            this.clientUidByUUID = 0;
            this.msgTitle = messageSource.getMessage("SYNC_SUCCESS_TITLE", null, locale);
            this.msgText = messageSource.getMessage("SYNC_SUCCESS_TEXT", null, locale);
            this.error = false;
            this.locale = locale;
        }

        public SyncMessageDto getClientUidAndValidateSyncGroup() {
            getClientBySyncCode(locale);
            getClientByClientUUID(locale);
            validateSyncGroup(locale);
            return this;
        }

        private void getClientByClientUUID(Locale locale) {
            Optional<RtrClient> client2 = clientRepository.getRtrClientByUuid(clientUUID);
            if (client2.isPresent()) {
                clientSyncGroupByUUID = ObjectUtils.defaultIfNull(client2.get().getSyncGroupId(), NumberUtils.INTEGER_ZERO);
                clientUidByUUID = client2.get().getUid();
            } else {
                msgTitle = messageSource.getMessage("SYNC_UUID_TITLE", null, locale);
                msgText = messageSource.getMessage("SYNC_UUID_TEXT", null, locale);
                error = true;
            }
        }

        private void getClientBySyncCode(Locale locale) {
            Optional<RtrClient> client = clientRepository.getClientBySyncCode(syncCode);
            if (client.isPresent()) {
                clientSyncGroupBySyncCode = ObjectUtils.defaultIfNull(client.get().getSyncGroupId(), NumberUtils.INTEGER_ZERO);
                clientUidBySyncCode = client.get().getUid();
            } else {
                msgTitle = messageSource.getMessage("SYNC_CODE_TITLE", null, locale);
                msgText = messageSource.getMessage("SYNC_CODE_TEXT", null, locale);
                error = true;
            }
        }

        private void validateSyncGroup(Locale locale) {
            if (clientSyncGroupBySyncCode > 0 && clientSyncGroupBySyncCode == clientSyncGroupByUUID) {
                msgTitle = messageSource.getMessage("SYNC_GROUP_TITLE", null, locale);
                msgText = messageSource.getMessage("SYNC_GROUP_TEXT", null, locale);
                error = true;
            }

            if (clientUidBySyncCode > 0 && clientUidBySyncCode == clientUidByUUID) {
                msgTitle = messageSource.getMessage("SYNC_CLIENT_TITLE", null, locale);
                msgText = messageSource.getMessage("SYNC_CLIENT_TEXT", null, locale);
                error = true;
            }
        }
    }
}
