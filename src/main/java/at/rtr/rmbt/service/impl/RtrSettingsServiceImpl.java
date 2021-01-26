package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.model.Settings;
import at.rtr.rmbt.request.*;
import at.rtr.rmbt.response.*;
import at.rtr.rmbt.service.*;
import at.rtr.rmbt.utils.LongUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import at.rtr.rmbt.exception.NotSupportedClientVersionException;
import at.rtr.rmbt.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static at.rtr.rmbt.constant.Config.SETTINGS_KEYS;
import static at.rtr.rmbt.constant.Config.SUPPORTED_CLIENT_NAMES;

@Service
@RequiredArgsConstructor
public class RtrSettingsServiceImpl implements RtrSettingsService {

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
    public SettingsResponse getSettings(RtrSettingsRequest request) {
        var lang = request.getLanguage();
        if (!SUPPORTED_CLIENT_NAMES.contains(request.getName())) {
            throw new NotSupportedClientVersionException();
        }
        var now = ZonedDateTime.now(clock);
        Map<String, String> settings = getSettingsURLMapByLanguageAndKeys(lang, SETTINGS_KEYS);
        var clientType = clientTypeService.findByClientType(request.getType());

        boolean isTermAndConditionAccepted = isTermAndConditionAccepted(request);

        var client = clientService.getClientByUUID(request.getUuid());
        if (Objects.nonNull(client)) {
            if (client.getTermsAndConditionsAcceptedVersion() < request.getTermsAndConditionsAcceptedVersion()) {
                client.setTermsAndConditionsAcceptedVersion(request.getTermsAndConditionsAcceptedVersion());
                client.setTermsAndConditionsAcceptedTimestamp(now);
            }
            client.setLastSeen(now);

        } else if (isTermAndConditionAccepted) {
            client = new RtrClient();
            client.setUuid(uuidGenerator.generateUUID());
            client.setClientType(clientType.orElse(null));
            client.setTermsAndConditionsAccepted(isTermAndConditionAccepted);
            client.setTermsAndConditionsAcceptedVersion(request.getTermsAndConditionsAcceptedVersion());
            client.setTime(now);
            client.setLastSeen(now);
        }
        var savedClient = clientService.saveClient(client);

        var setting = SettingResponse.builder()
                .termAndConditionsResponse(getTermAndConditionResponse(request.getPlatform(), request.getSoftwareVersionName(), settings))
                .uuid(savedClient.getUuid())
                .qosTestTypeDescResponse(qosTestTypeDescService.getAll(lang))
                .urls(getUrlsResponse(settings))
                .history(getHistoryResponse(savedClient.getUid()))
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

    @Override
    public void createSettings(AdminSettingsRequest adminSettingsRequest) {
        Map<String, Settings> settingsActual = getSettingsMapByLanguageAndKeys(adminSettingsRequest.getLanguage(), SETTINGS_KEYS);
        Map<String, String> settingsNew = getNewSettingsMap(adminSettingsRequest.getSettings());
        List<Settings> updatedSettings = new ArrayList<>();
        settingsNew.entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .forEach(entry -> updateOrCreateSettings(adminSettingsRequest.getLanguage(), settingsActual, updatedSettings, entry));

        settingsRepository.saveAll(updatedSettings);
    }

    private void updateOrCreateSettings(String language, Map<String, Settings> settingsActual, List<Settings> updatedSettings, Map.Entry<String, String> entry) {
        Optional.ofNullable(settingsActual.get(entry.getKey()))
                .ifPresentOrElse(x -> {
                            x.setValue(entry.getValue());
                            updatedSettings.add(x);
                        },
                        () -> {
                            var newSetting = new Settings();
                            newSetting.setKey(entry.getKey());
                            newSetting.setValue(entry.getValue());
                            newSetting.setLang(language);
                            updatedSettings.add(newSetting);
                        });
    }

    private Map<String, String> getNewSettingsMap(AdminSettingsBodyRequest adminSettingsBodyRequest) {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.convertValue(adminSettingsBodyRequest, Map.class);
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

    private boolean isTermAndConditionAccepted(RtrSettingsRequest rtrSettingsRequest) {
        if (rtrSettingsRequest.getTermsAndConditionsAcceptedVersion() > 0) {
            return true;
        } else {
            return rtrSettingsRequest.isTermsAndConditionsAccepted();
        }
    }

    private Map<String, String> getSettingsURLMapByLanguageAndKeys(String lang, List<String> keys) {
        return settingsRepository.findAllByLangOrLangIsNullAndKeyIn(lang, keys).stream()
                .collect(Collectors.toMap(Settings::getKey, Settings::getValue, (val1, val2) -> val1));
    }

    private Map<String, Settings> getSettingsMapByLanguageAndKeys(String lang, List<String> keys) {
        return settingsRepository.findAllByLangOrLangIsNullAndKeyIn(lang, keys).stream()
                .collect(Collectors.toMap(Settings::getKey, Function.identity(), (val1, val2) -> val1));
    }
}
