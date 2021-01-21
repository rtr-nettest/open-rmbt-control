package com.rtr.nettest.service.impl;

import com.google.common.base.Strings;
import com.rtr.nettest.exception.NotSupportedClientVersionException;
import com.rtr.nettest.model.Client;
import com.rtr.nettest.model.Settings;
import com.rtr.nettest.repository.SettingsRepository;
import com.rtr.nettest.request.CapabilitiesRequest;
import com.rtr.nettest.request.RMBTHttpRequest;
import com.rtr.nettest.request.SettingsRequest;
import com.rtr.nettest.response.*;
import com.rtr.nettest.service.*;
import com.rtr.nettest.utils.LongUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.rtr.nettest.constant.Config.SETTINGS_KEYS;
import static com.rtr.nettest.constant.Config.SUPPORTED_CLIENT_NAMES;

@Service
@RequiredArgsConstructor
public class SettingsServiceImpl implements SettingsService {

    @Value("${git.branch}")
    private String branch;

    @Value("${git.commit.id.describe}")
    private String describe;

    private final ClientTypeService clientTypeService;
    private final ClientService clientService;
    private final SettingsRepository settingsRepository;
    private final QoSTestTypeDescService qosTestTypeDescService;
    private final TestService testService;
    private final TestServerService testServerService;
    private final UUIDGenerator uuidGenerator;
    private final Clock clock;

    @Override
    public SettingsResponse getSettings(SettingsRequest request, String clientIpRaw) {
        var lang = request.getLanguage();
        if (!SUPPORTED_CLIENT_NAMES.contains(request.getName())) {
            throw new NotSupportedClientVersionException();
        }
        var now = OffsetDateTime.now(clock);
        Map<String, String> settings = getSettingsByLanguageAndKeys(lang, SETTINGS_KEYS);
        var clientType = clientTypeService.getClientTypeByName(request.getType());

        boolean isTermAndConditionAccepted = isTermAndConditionAccepted(request);

        var client = clientService.getClientByUUID(request.getUuid());
        if (Objects.nonNull(client)) {
            if (client.getTermAndConditionsVersion() < request.getTermsAndConditionsAcceptedVersion()) {
                client.setTermAndConditionsVersion(request.getTermsAndConditionsAcceptedVersion());
                client.setTermAndConditionsVersionAcceptedTimestamp(now);
            }
            client.setLastSeen(now);

        } else if (isTermAndConditionAccepted) {
            client = new Client();
            client.setUuid(uuidGenerator.generateUUID());
            client.setClientType(clientType);
            client.setTermAndConditionsAccepted(isTermAndConditionAccepted);
            client.setTermAndConditionsVersion(request.getTermsAndConditionsAcceptedVersion());
            client.setTime(now);
            client.setLastSeen(now);
        }
        var savedClient = clientService.saveClient(client);

        var setting = SettingResponse.builder()
                .termAndConditionsResponse(getTermAndConditionResponse(request.getPlatform(), request.getSoftwareVersionName(), settings))
                .uuid(savedClient.getUuid())
                .qosTestTypeDescResponse(qosTestTypeDescService.getAll(lang))
                .urls(getUrlsResponse(settings))
                .history(getHistoryResponse(savedClient.getId()))
                .servers(getServers(request.getCapabilities()))
                .serverWSResponseList(testServerService.getServersWs())
                .serverQoSResponseList(testServerService.getServersQos())
                .mapServerResponse(getMapServerResponse(settings))
                .versions(getVersionResponse())
                .build();

        return SettingsResponse.builder()
                .settings(List.of(setting))
                .build();
    }

    private VersionResponse getVersionResponse() {
        return VersionResponse.builder()
                .controlServerVersion(String.format("%s_%s", branch, describe))
                .build();
    }

    private TermAndConditionsResponse getTermAndConditionResponse(String platform, String softwareVersionName, Map<String, String> settings) {
        String tcUrl = null;
        String tcNdtUrl = null;
        Long tcVersion = null;
        if (Objects.nonNull(platform) && platform.equalsIgnoreCase("android")) {
            tcUrl = settings.get("tc_url_android");
            tcNdtUrl = settings.get("tc_ndt_url_android");
            tcVersion = LongUtils.parseLong(settings.get("tc_version_android"));

            if (Objects.nonNull(tcUrl) && Objects.nonNull(softwareVersionName) && softwareVersionName.startsWith("4.")) {
                String newUrl = settings.get("tc_url_android_v4");
                if (!Strings.isNullOrEmpty(newUrl)) {
                    tcUrl = newUrl;
                }
            }
        } else if (Objects.nonNull(platform) && platform.equalsIgnoreCase("ios")) {
            tcUrl = settings.get("tc_url_ios");
            tcVersion = LongUtils.parseLong(settings.get("tc_version_ios"));
        }
        if (Objects.isNull(tcVersion)) {
            tcVersion = LongUtils.parseLong(settings.get("tc_version"));
        }
        if (Objects.isNull(tcUrl)) {
            tcUrl = settings.get("tc_url");
        }

        return TermAndConditionsResponse.builder()
                .version(tcVersion)
                .ndtUrl(tcNdtUrl)
                .url(tcUrl)
                .build();
    }

    private MapServerResponse getMapServerResponse(Map<String, String> settings) {
        return MapServerResponse.builder()
                .host(settings.get("host_map_server"))
                .ssl(Boolean.parseBoolean(settings.get("ssl_map_server")))
                .port(LongUtils.parseLong(settings.get("port_map_server")))
                .build();
    }

    private List<TestServerResponse> getServers(CapabilitiesRequest capabilitiesRequest) {
        var isRmbtHttp = Optional.ofNullable(capabilitiesRequest)
                .map(CapabilitiesRequest::getRmbtHttpRequest)
                .map(RMBTHttpRequest::isRmbtHttp)
                .orElse(false);
        if (isRmbtHttp) {
            return testServerService.getServersHttp();
        } else {
            return testServerService.getServers();
        }
    }

    private UrlsResponse getUrlsResponse(Map<String, String> settings) {
        return UrlsResponse.builder()
                .openDataPrefix(settings.get("url_open_data_prefix"))
                .urlShare(settings.get("url_share"))
                .statistics(settings.get("url_statistics"))
                .controlIPV4Only(settings.get("control_ipv4_only"))
                .controlIPV6Only(settings.get("control_ipv6_only"))
                .urlIPV4Check(settings.get("url_ipv4_check"))
                .urlIPV6Check(settings.get("url_ipv6_check"))
                .urlMapServer(settings.get("url_map_server"))
                .build();
    }

    private HistoryResponse getHistoryResponse(Long clientId) {
        var devices = getSyncGroupDeviceList(clientId);
        var networks = getGroupName(clientId);
        return HistoryResponse.builder()
                .devices(devices)
                .networks(networks)
                .build();
    }

    private List<String> getGroupName(Long clientId) {
        return testService.getGroupNameByClientId(clientId);
    }

    private List<String> getSyncGroupDeviceList(Long clientId) {
        return testService.getDeviceHistory(clientId);
    }

    private boolean isTermAndConditionAccepted(SettingsRequest settingsRequest) {
        if (settingsRequest.getTermsAndConditionsAcceptedVersion() > 0) {
            return true;
        } else {
            return settingsRequest.isTermsAndConditionsAccepted();
        }
    }

    private Map<String, String> getSettingsByLanguageAndKeys(String lang, List<String> keys) {
        return settingsRepository.findAllByLangOrLangIsNullAndKeyIn(lang, keys).stream()
                .collect(Collectors.toMap(Settings::getKey, Settings::getValue, (val1, val2) -> val1));
    }
}
