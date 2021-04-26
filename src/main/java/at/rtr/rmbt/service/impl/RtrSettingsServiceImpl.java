package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.exception.NotSupportedClientVersionException;
import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.model.Settings;
import at.rtr.rmbt.properties.ApplicationProperties;
import at.rtr.rmbt.repository.SettingsRepository;
import at.rtr.rmbt.request.AdminSettingsBodyRequest;
import at.rtr.rmbt.request.AdminSettingsRequest;
import at.rtr.rmbt.request.CapabilitiesRequest;
import at.rtr.rmbt.request.RtrSettingsRequest;
import at.rtr.rmbt.request.settings.admin.update.AdminUpdateSettingsRequest;
import at.rtr.rmbt.response.*;
import at.rtr.rmbt.response.settings.admin.update.*;
import at.rtr.rmbt.service.*;
import at.rtr.rmbt.utils.LongUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final QosTestTypeDescService qosTestTypeDescService;
    private final TestService testService;
    private final TestServerService testServerService;
    private final UUIDGenerator uuidGenerator;
    private final Clock clock;
    private final ApplicationProperties applicationProperties;

    @Override
    public SettingsResponse getSettings(RtrSettingsRequest request) {
        var lang = request.getLanguage();
        if (!applicationProperties.getClientNames().contains(request.getName())) {
            throw new NotSupportedClientVersionException();
        }
        var now = ZonedDateTime.now(clock);
        Map<String, String> settings = getSettingsURLMapByLanguageAndKeys(lang, Config.SETTINGS_KEYS);
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
                .serverQosResponseList(testServerService.getServersQos())
                .mapServerResponse(getMapServerResponse(settings))
                .versions(getVersionResponse())
                .build();

        return SettingsResponse.builder()
                .settings(List.of(setting))
                .build();
    }

    @Override
    public void createSettings(AdminSettingsRequest adminSettingsRequest) {
        Map<String, Settings> settingsActual = getSettingsMapByLanguageAndKeys(adminSettingsRequest.getLanguage(), Config.SETTINGS_KEYS);
        Map<String, String> settingsNew = getNewSettingsMap(adminSettingsRequest.getSettings());
        List<Settings> updatedSettings = new ArrayList<>();
        settingsNew.entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(l -> Pair.of(l.getKey(), String.valueOf(l.getValue())))
                .forEach(entry -> updateOrCreateSettings(adminSettingsRequest.getLanguage(), settingsActual, updatedSettings, entry));

        settingsRepository.saveAll(updatedSettings);
    }

    @Override
    public AdminSettingsResponse getAllSettings() {
        Map<String, String> actualSettings = getAdminSettingsMap();

        return AdminSettingsResponse.builder()
                .adminSettingsSignalTestResponse(getAdminSettingSignalTestResponse(actualSettings))
                .adminTestResponse(getAdminTestResponse(actualSettings))
                .mapServerResponse(getAdminSettingsMapServerResponse(actualSettings))
                .termAndConditionsResponse(getAdminTermAndConditionResponse(actualSettings))
                .urls(getAdminSettingsUrlsResponse(actualSettings))
                .versions(getAdminSettingsVersionResponse())
                .build();
    }

    @Override
    public void updateSettings(AdminUpdateSettingsRequest adminUpdateSettingsRequest) {
        Map<String, Settings> settingsActual = getSettingsMapByLanguageAndKeys("en", Config.ADMIN_SETTINGS_KEYS);
        List<Settings> updateSettings = new ArrayList<>();

        Optional.ofNullable(adminUpdateSettingsRequest.getAdminUpdateSettingsTermsAndConditionsRequest())
                .ifPresent(tcRequest -> {
                    updateSettings(settingsActual, Config.TERM_AND_CONDITION_URL_KEY, tcRequest.getTcUrl(), updateSettings);
                    updateSettings(settingsActual, Config.TERM_AND_CONDITION_URL_IOS_KEY, tcRequest.getTcUrlIOS(), updateSettings);
                    updateSettings(settingsActual, Config.TERM_AND_CONDITION_URL_ANDROID_KEY, tcRequest.getTcUrlAndroid(), updateSettings);
                    updateSettings(settingsActual, Config.TERM_AND_CONDITION_VERSION_KEY, tcRequest.getTcVersion(), updateSettings);
                    updateSettings(settingsActual, Config.TERM_AND_CONDITION_VERSION_IOS_KEY, tcRequest.getTcVersionIOS(), updateSettings);
                    updateSettings(settingsActual, Config.TERM_AND_CONDITION_VERSION_ANDROID_KEY, tcRequest.getTcVersionAndroid(), updateSettings);
                    updateSettings(settingsActual, Config.TERM_AND_CONDITION_NDT_URL_KEY, tcRequest.getTcNdtUrlAndroid(), updateSettings);
                });

        Optional.ofNullable(adminUpdateSettingsRequest.getAdminUpdateSettingsUrlsRequest())
                .ifPresent(urlsRequest -> {
                    updateSettings(settingsActual, Config.URL_OPEN_DATA_PREFIX_KEY, urlsRequest.getOpenDataPrefix(), updateSettings);
                    updateSettings(settingsActual, Config.URL_SHARE_KEY, urlsRequest.getUrlShare(), updateSettings);
                    updateSettings(settingsActual, Config.URL_STATISTIC_KEY, urlsRequest.getStatistics(), updateSettings);
                    updateSettings(settingsActual, Config.URL_CONTROL_IPV4_ONLY_KEY, urlsRequest.getControlIpV4Only(), updateSettings);
                    updateSettings(settingsActual, Config.URL_CONTROL_IPV6_ONLY_KEY, urlsRequest.getControlIpV6Only(), updateSettings);
                    updateSettings(settingsActual, Config.URL_IPV4_CHECK_KEY, urlsRequest.getUrlIpV4Check(), updateSettings);
                    updateSettings(settingsActual, Config.URL_IPV6_CHECK_KEY, urlsRequest.getUrlIpV6Check(), updateSettings);
                    updateSettings(settingsActual, Config.URL_MAP_SERVER_KEY, urlsRequest.getUrlMapServer(), updateSettings);

                });

        Optional.ofNullable(adminUpdateSettingsRequest.getAdminUpdateSettingsMapServerRequest())
                .ifPresent(mapServerRequest -> {
                    updateSettings(settingsActual, Config.MAP_SERVER_HOST_KEY, mapServerRequest.getHost(), updateSettings);
                    updateSettings(settingsActual, Config.MAP_SERVER_SSL_KEY, mapServerRequest.getSsl(), updateSettings);
                    updateSettings(settingsActual, Config.MAP_SERVER_PORT_KEY, mapServerRequest.getPort(), updateSettings);
                });

        Optional.ofNullable(adminUpdateSettingsRequest.getAdminUpdateSettingsTestRequest())
                .ifPresent(testRequest -> {
                    updateSettings(settingsActual, Config.TEST_RESULT_URL_KEY, testRequest.getResultUrl(), updateSettings);
                    updateSettings(settingsActual, Config.TEST_RESULT_QOS_URL_KEY, testRequest.getResultQosUrl(), updateSettings);
                    updateSettings(settingsActual, Config.TEST_DURATION_KEY, testRequest.getTestDuration(), updateSettings);
                    updateSettings(settingsActual, Config.TEST_NUM_THREADS_KEY, testRequest.getTestNumThreads(), updateSettings);
                    updateSettings(settingsActual, Config.TEST_NUM_PINGS_KEY, testRequest.getTestNumPings(), updateSettings);
                });

        Optional.ofNullable(adminUpdateSettingsRequest.getAdminUpdateSettingsSignalTestRequest())
                .ifPresent(signalTestRequest -> {
                    updateSettings(settingsActual, Config.SIGNAL_RESULT_URL_KEY, signalTestRequest.getResultUrl(), updateSettings);
                });

        settingsRepository.saveAll(updateSettings);
    }

    private void updateSettings(Map<String, Settings> settingsActual, String key, String value, List<Settings> updatedSettings) {
        Optional.ofNullable(settingsActual.get(key))
                .ifPresentOrElse(x -> {
                            x.setValue(value);
                            updatedSettings.add(x);
                        },
                        () -> {
                            var newSetting = new Settings();
                            newSetting.setKey(key);
                            newSetting.setValue(value);
                            newSetting.setLang("en");
                            updatedSettings.add(newSetting);
                        });
    }

    private Map<String, String> getAdminSettingsMap() {
        return settingsRepository.findAllByLangOrLangIsNullAndKeyIn("en", Config.ADMIN_SETTINGS_KEYS).stream()
                .collect(Collectors.toMap(Settings::getKey,
                        Settings::getValue,
                        (x1, x2) -> x1));
    }

    private AdminSettingsTermAndConditionsResponse getAdminTermAndConditionResponse(Map<String, String> actualSettings) {
        return AdminSettingsTermAndConditionsResponse.builder()
                .tcUrl(actualSettings.get(Config.TERM_AND_CONDITION_URL_KEY))
                .tcUrlIOS(actualSettings.get(Config.TERM_AND_CONDITION_URL_IOS_KEY))
                .tcUrlAndroid(actualSettings.get(Config.TERM_AND_CONDITION_URL_ANDROID_KEY))
                .tcVersion(actualSettings.get(Config.TERM_AND_CONDITION_VERSION_KEY))
                .tcVersionIOS(actualSettings.get(Config.TERM_AND_CONDITION_VERSION_IOS_KEY))
                .tcVersionAndroid(actualSettings.get(Config.TERM_AND_CONDITION_VERSION_ANDROID_KEY))
                .tcNdtUrlAndroid(actualSettings.get(Config.TERM_AND_CONDITION_NDT_URL_KEY))
                .build();
    }

    private AdminSettingsTestResponse getAdminTestResponse(Map<String, String> actualSettings) {
        return AdminSettingsTestResponse.builder()
                .resultQosUrl(actualSettings.get(Config.TEST_RESULT_QOS_URL_KEY))
                .resultUrl(actualSettings.get(Config.TEST_RESULT_URL_KEY))
                .testDuration(actualSettings.get(Config.TEST_DURATION_KEY))
                .testNumPings(actualSettings.get(Config.TEST_NUM_PINGS_KEY))
                .testNumThreads(actualSettings.get(Config.TEST_NUM_THREADS_KEY))
                .build();
    }

    private AdminSettingsSignalTestResponse getAdminSettingSignalTestResponse(Map<String, String> actualSettings) {
        return AdminSettingsSignalTestResponse.builder()
                .resultUrl(actualSettings.get(Config.SIGNAL_RESULT_URL_KEY))
                .build();
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
                .controlServerVersion(String.format(Constants.VERSION_TEMPLATE, branch, describe))
                .build();
    }

    private AdminSettingsVersionResponse getAdminSettingsVersionResponse() {
        return AdminSettingsVersionResponse.builder()
                .controlServerVersion(String.format(Constants.VERSION_TEMPLATE, branch, describe))
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
                .host(settings.get(Config.MAP_SERVER_HOST_KEY))
                .ssl(Boolean.parseBoolean(settings.get(Config.MAP_SERVER_SSL_KEY)))
                .port(LongUtils.parseLong(settings.get(Config.MAP_SERVER_PORT_KEY)))
                .build();
    }

    private AdminSettingsMapServerResponse getAdminSettingsMapServerResponse(Map<String, String> settings) {
        return AdminSettingsMapServerResponse.builder()
                .host(settings.get(Config.MAP_SERVER_HOST_KEY))
                .ssl(settings.get(Config.MAP_SERVER_SSL_KEY))
                .port(settings.get(Config.MAP_SERVER_PORT_KEY))
                .build();
    }

    private List<TestServerResponseForSettings> getServers(CapabilitiesRequest capabilitiesRequest) {
        var isRmbtHttp = Optional.ofNullable(capabilitiesRequest)
                .map(CapabilitiesRequest::isRmbtHttp)
                .orElse(false);
        if (isRmbtHttp) {
            return testServerService.getServersHttp();
        } else {
            return testServerService.getServers();
        }
    }

    private UrlsResponse getUrlsResponse(Map<String, String> settings) {
        return UrlsResponse.builder()
                .openDataPrefix(settings.get(Config.URL_OPEN_DATA_PREFIX_KEY))
                .urlShare(settings.get(Config.URL_SHARE_KEY))
                .statistics(settings.get(Config.URL_STATISTIC_KEY))
                .controlIPV4Only(settings.get(Config.URL_CONTROL_IPV4_ONLY_KEY))
                .controlIPV6Only(settings.get(Config.URL_CONTROL_IPV6_ONLY_KEY))
                .urlIPV4Check(settings.get(Config.URL_IPV4_CHECK_KEY))
                .urlIPV6Check(settings.get(Config.URL_IPV6_CHECK_KEY))
                .urlMapServer(settings.get(Config.URL_MAP_SERVER_KEY))
                .build();
    }

    private AdminSettingsUrlsResponse getAdminSettingsUrlsResponse(Map<String, String> settings) {
        return AdminSettingsUrlsResponse.builder()
                .openDataPrefix(settings.get(Config.URL_OPEN_DATA_PREFIX_KEY))
                .urlShare(settings.get(Config.URL_SHARE_KEY))
                .statistics(settings.get(Config.URL_STATISTIC_KEY))
                .controlIpV4Only(settings.get(Config.URL_CONTROL_IPV4_ONLY_KEY))
                .controlIpV6Only(settings.get(Config.URL_CONTROL_IPV6_ONLY_KEY))
                .urlIpV4Check(settings.get(Config.URL_IPV4_CHECK_KEY))
                .urlIpV6Check(settings.get(Config.URL_IPV6_CHECK_KEY))
                .urlMapServer(settings.get(Config.URL_MAP_SERVER_KEY))
                .build();
    }

    private SettingsHistoryResponse getHistoryResponse(Long clientId) {
        List<RtrClient> rtrClients = clientService.listSyncedClientsByClientUid(clientId);
        List<Long> clientIds = rtrClients.stream().map(RtrClient::getUid).collect(Collectors.toList());
        var devices = testService.getDeviceHistory(clientIds);
        var networks = testService.getGroupNameByClientIds(clientIds);
        return SettingsHistoryResponse.builder()
            .devices(devices)
            .networks(networks)
            .build();
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
