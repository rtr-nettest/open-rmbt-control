package at.rtr.rmbt.facade;

import at.rtr.rmbt.config.RollBackService;
import at.rtr.rmbt.enums.ServerType;
import at.rtr.rmbt.enums.TestStatus;
import at.rtr.rmbt.exception.TestServerNotFoundException;
import at.rtr.rmbt.model.ClientType;
import at.rtr.rmbt.model.LoopModeSettings;
import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.model.ServerTypeDetails;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.properties.ApplicationProperties;
import at.rtr.rmbt.request.TestSettingsRequest;
import at.rtr.rmbt.response.ErrorResponse;
import at.rtr.rmbt.response.TestSettingsResponse;
import at.rtr.rmbt.service.ClientService;
import at.rtr.rmbt.service.ClientTypeService;
import at.rtr.rmbt.service.LoopModeSettingsService;
import at.rtr.rmbt.service.TestServerService;
import at.rtr.rmbt.service.TestService;
import at.rtr.rmbt.utils.GeoIpHelper;
import at.rtr.rmbt.utils.HeaderExtrudeUtil;
import at.rtr.rmbt.utils.HelperFunctions;
import at.rtr.rmbt.utils.ValidateUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vdurmont.semver4j.SemverException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import static at.rtr.rmbt.constant.URIConstants.RESULT_QOS_URL;
import static at.rtr.rmbt.constant.URIConstants.RESULT_URL;

@Service
public class TestSettingsFacade {
    private static final Logger logger = LoggerFactory.getLogger(TestSettingsFacade.class);

    private static final Set<String> TIMEZONES = Set.of(TimeZone.getAvailableIDs());

    private final LoopModeSettingsService loopModeSettingsService;

    private final ClientTypeService clientTypeService;

    private final ClientService clientService;

    private final TestServerService testServerService;

    private final TestService testService;

    private final ApplicationProperties applicationProperties;

    private final MessageSource messageSource;

    private final ObjectMapper objectMapper;

    private final RollBackService rollBackService;

    @Value("${application-version.server-url}")
    private String applicationServerUrl;

    @Autowired
    public TestSettingsFacade(LoopModeSettingsService loopModeSettingsService, ClientTypeService clientTypeService,
                              ClientService clientService, TestServerService testServerService, TestService testService,
                              ApplicationProperties applicationProperties, MessageSource messageSource, ObjectMapper objectMapper,
                              RollBackService rollBackService) {
        this.loopModeSettingsService = loopModeSettingsService;
        this.clientTypeService = clientTypeService;
        this.clientService = clientService;
        this.testServerService = testServerService;
        this.testService = testService;
        this.applicationProperties = applicationProperties;
        this.messageSource = messageSource;
        this.objectMapper = objectMapper;
        this.rollBackService = rollBackService;
    }

    @Transactional
    public TestSettingsResponse updateTestSettings(TestSettingsRequest testSettingsRequest, HttpServletRequest request, Map<String, String> headers) {
        long startTime = System.currentTimeMillis();
        Long asn;
        String asName = null;
        String asCountry = null;
        String clientIpAddress = HeaderExtrudeUtil.getIpFromNgNixHeader(request, headers);
        InetAddress clientAddress = null;
        try {
            clientAddress = InetAddress.getByName(clientIpAddress);
        } catch (UnknownHostException ignored) {
        }
        TestSettingsResponse.TestSettingsResponseBuilder builder = TestSettingsResponse.builder();
        ErrorResponse errorResponse = new ErrorResponse();
        String language = getLanguageIfSupportedOrDefault(testSettingsRequest.getLanguage());
        Locale locale = Locale.forLanguageTag(language);

        asn = HelperFunctions.getASN(clientAddress);
        if (asn != null) {
            asName = HelperFunctions.getASName(asn);
            asCountry = HelperFunctions.getAScountry(asn);
        }

        try {
            final String settingUuid = testSettingsRequest.getUuid();
            UUID uuid = null;

            if (StringUtils.isNotBlank(settingUuid)) {
                if (settingUuid.startsWith("U") && settingUuid.length() > 1)
                    uuid = UUID.fromString(settingUuid.substring(1));
                else uuid = UUID.fromString(settingUuid);
            }

            TestSettingsRequest.LoopModeInfo loopModeInfo = testSettingsRequest.getLoopModeInfo();

            LoopModeSettings loopModeSettings = null;
            if (loopModeInfo != null) {
                loopModeSettings = toLoopModeSettings(loopModeInfo);
                loopModeSettings.setClientUuid(uuid);

                //if no loop mode uuid is set - generate one
                if (loopModeInfo.getLoopUuid() == null)
                    loopModeSettings.setLoopUuid(UUID.randomUUID());
                else
                    loopModeSettings.setLoopUuid(UUID.fromString(loopModeInfo.getLoopUuid()));

                //old clients expect a "text_counter"
                if (loopModeSettings.getTestCounter() == null)
                    loopModeSettings.setTestCounter(loopModeInfo.getTextCounter());

                loopModeSettings = loopModeSettingsService.save(loopModeSettings);
            }

            ClientType clientType = null;
            if (testSettingsRequest.getClientType() != null) {
                Optional<ClientType> clientTypeOptional = clientTypeService.findByClientType(testSettingsRequest.getClientType());

                if (clientTypeOptional.isPresent())
                    clientType = clientTypeOptional.get();
                else
                    errorResponse.getError().add(getErrorMessageAndRollback("ERROR_DB_GET_CLIENTTYPE", locale));
            }

            ValidateUtils.validateClientVersion(applicationProperties.getVersion(), testSettingsRequest.getTestSetVersion());

            if (applicationProperties.getClientNames().contains(testSettingsRequest.getServerType().getLabel()) && clientType != null) {
                if (!TIMEZONES.contains(testSettingsRequest.getTimezone()))
                    errorResponse.getError().add(getErrorMessage("ERROR_TIMEZONE", locale));

                String timeZoneId = testSettingsRequest.getTimezone();
                if (StringUtils.isBlank(timeZoneId))
                    timeZoneId = HelperFunctions.getTimeZoneId();

                RtrClient client = getClientOrAddError(uuid, errorResponse, locale);

                if (client != null) {
                    final UUID testUuid = UUID.randomUUID();
                    final UUID testOpenUuid = UUID.randomUUID();
                    final Boolean ipv6;

                    List<ServerType> serverTypes;

                    if (testSettingsRequest.getCapabilities() != null && testSettingsRequest.getCapabilities().isRmbtHttp()) {
                        serverTypes = List.of(ServerType.RMBThttp, ServerType.RMBT);
                    } else if (ServerType.RMBTws.equals(testSettingsRequest.getServerType())) {
                        serverTypes = List.of(ServerType.RMBThttp, ServerType.RMBTws);
                    } else if (ServerType.HW_PROBE.equals(testSettingsRequest.getServerType())) {
                        serverTypes = List.of(ServerType.RMBT);
                    } else {
                        serverTypes = List.of(ServerType.RMBT);
                    }

                    ipv6 = isIpV6(testSettingsRequest.getProtocolVersion(), clientAddress, errorResponse, locale);

                    Integer numberOfThreads = getNumberOfThreadsOrDefault(testSettingsRequest.getNumberOfThreads());
                    TestServer testServer = null;

                    if (testSettingsRequest.isUserServerSelection()) {
                        final String preferServer = testSettingsRequest.getPreferredServer();
                        if (StringUtils.isNotBlank(preferServer))
                            testServer = testServerService.findByUuidAndActive(UUID.fromString(preferServer), true)
                                    .orElse(null);
                    }

                    String geoIpCountry = GeoIpHelper.lookupCountry(clientAddress);
                    if (testServer == null)
                        testServer = testServerService.findActiveByServerTypeInAndCountry(serverTypes, StringUtils.isNotBlank(asCountry) ? asCountry : geoIpCountry);

                    if (testServer == null)
                        throw new TestServerNotFoundException();
                    ServerTypeDetails serverTypeDetails = getServerTypeDetails(testSettingsRequest, testServer);
                    builder.testServerAddress(getServerAddress(ipv6, testServer))
                            .testServerPort(serverTypeDetails.isEncrypted() ? serverTypeDetails.getPortSsl() : serverTypeDetails.getPort())
                            .testServerName(testServer.getName() + " (" + testServer.getCity() + ")")
                            .testServerEncryption(serverTypeDetails.isEncrypted())
                            .testServerType(serverTypeDetails.getServerType())
                            .testDuration(String.valueOf(applicationProperties.getDuration()))
                            .testNumberOfThreads(String.valueOf(numberOfThreads))
                            .testNumberOfPings(String.valueOf(applicationProperties.getPings()))
                            .clientRemoteIp(clientIpAddress);

                    String resultUrl = applicationServerUrl;
//                    if (request.getHeader(HeaderConstants.URL) != null) {
//                        resultUrl = request.getHeader(HeaderConstants.URL);
//                    } else {
//                        resultUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
//                                .build()
//                                .toString();
//                    }
                    builder.resultUrl(resultUrl + RESULT_URL);
                    builder.resultQosUrl(resultUrl + RESULT_QOS_URL);


                    if (errorResponse.getError().isEmpty()) {
                        Test test = getTest(testSettingsRequest, clientIpAddress, asn, asName, asCountry, clientAddress, language, timeZoneId, client, testUuid, testOpenUuid, numberOfThreads, testServer, geoIpCountry);

                        test = testService.save(test);

                        if (loopModeSettings != null) {
                            loopModeSettings.setTestUuid(testUuid);
                            loopModeSettings = loopModeSettingsService.save(loopModeSettings);
                            builder.loopUuid(loopModeSettings.getLoopUuid().toString());
                        }

                        builder.provider(testService.getRmbtSetProviderFromAs(test.getUid()));

                        Integer testSlot = testService.getRmbtNextTestSlot(test.getUid());

                        if (testSlot < 0) {
                            errorResponse.getError().add(getErrorMessageAndRollback("ERROR_DB_STORE_GENERAL", locale));
                        } else {
                            final String data = testOpenUuid + "_" + testSlot;
                            final String hmac = HelperFunctions.calculateHMAC(testServer.getKey().getBytes(), data);

                            if (StringUtils.isBlank(hmac))
                                errorResponse.getError().add(getErrorMessage("ERROR_TEST_TOKEN", locale));

                            final String token = data + "_" + hmac;

                            test.setToken(token);
                            test = testService.save(test);

                            int waitTime = testSlot - (int) (System.currentTimeMillis() / 1000);
                            builder.testToken(token)
                                    .testUuid(testUuid.toString())
                                    .openTestUuid("O" + testOpenUuid)
                                    .testId(test.getUid())
                                    .testWait(Math.max(waitTime, 0));
                        }

                    }
                } else {
                    errorResponse.getError().add(getErrorMessageAndRollback("ERROR_CLIENT_UUID", locale));
                }
            } else {
                errorResponse.getError().add(getErrorMessageAndRollback("ERROR_CLIENT_VERSION", locale));
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            errorResponse.getError().add(getErrorMessageAndRollback("ERROR_DB_CONNECTION", locale));
        } catch (SemverException e) {
            errorResponse.getError().add(getErrorMessageAndRollback("ERROR_CLIENT_VERSION", locale));
        } catch (TestServerNotFoundException e) {
            errorResponse.getError().add(getErrorMessageAndRollback("ERROR_TEST_SERVER", locale));
        }

        TestSettingsResponse response = builder.errorList(errorResponse.getError()).build();

        logger.info(messageSource.getMessage("NEW_REQUEST_SUCCESS", null, Locale.forLanguageTag("en")), clientIpAddress, System.currentTimeMillis() - startTime);
        try {
            logger.info(objectMapper.writeValueAsString(response));
        } catch (JsonProcessingException ignored) {
        }
        return response;
    }

    private ServerTypeDetails getServerTypeDetails(TestSettingsRequest testSettingsRequest, TestServer testServer) {
        return testServer.getServerTypeDetails().stream().anyMatch(x -> x.getServerType().equals(testSettingsRequest.getServerType())) ?
                testServer.getServerTypeDetails().stream().filter(x -> x.getServerType().equals(testSettingsRequest.getServerType())).findFirst().orElseThrow(TestServerNotFoundException::new) :
                testServer.getServerTypeDetails().stream().findFirst().orElseThrow(TestServerNotFoundException::new);
    }

    private Test getTest(TestSettingsRequest testSettingsRequest, String clientIpdAddress, Long asn, String asName, String asCountry, InetAddress clientAddress, String language, String timeZoneId, RtrClient client, UUID testUuid, UUID testOpenUuid, Integer numberOfThreads, TestServer testServer, String geoIpCountry) {
        ServerTypeDetails serverTypeDetails = getServerTypeDetails(testSettingsRequest, testServer);

        Test test = new Test();

        test.setUuid(testUuid);
        test.setOpenTestUuid(testOpenUuid);
        test.setClient(client);
        test.setClientName(testSettingsRequest.getServerType());
        test.setClientVersion(testSettingsRequest.getTestSetVersion());
        test.setClientSoftwareVersion(testSettingsRequest.getSoftwareVersion());
        test.setClientLanguage(language);
        test.setClientPublicIp(clientIpdAddress);
        test.setClientPublicIpAnonymized(HelperFunctions.anonymizeIp(clientAddress));
        test.setCountryGeoip(geoIpCountry);
        test.setTestServer(testServer);
        test.setServerPort(serverTypeDetails.isEncrypted() ? testServer.getPortSsl() : testServer.getPort());
        test.setUseSsl(serverTypeDetails.isEncrypted());
        test.setTimezone(timeZoneId);
        test.setClientTime(ZonedDateTime.ofInstant(Instant.ofEpochSecond(testSettingsRequest.getTime()), ZoneId.of(timeZoneId)));
        test.setDuration(applicationProperties.getDuration());
        test.setNumberOfThreads(numberOfThreads);
        test.setStatus(TestStatus.STARTED);
        test.setSoftwareRevision(testSettingsRequest.getSoftwareRevision());
        test.setClientPreviousTestStatus(testSettingsRequest.getPreviousTestStatus());
        test.setPublicIpAsn(asn);
        test.setPublicIpAsName(asName);
        test.setCountryAsn(asCountry);
        test.setClientTestCounter(
                testSettingsRequest.getTestCounter() == null || testSettingsRequest.getTestCounter() == -1 ?
                        null :
                        testSettingsRequest.getTestCounter().longValue()
        );
        String reverseDns = HelperFunctions.reverseDNSLookup(clientAddress);
        if (StringUtils.isNotBlank(reverseDns))
            test.setPublicIpRdns(reverseDns.replaceFirst("\\.$", ""));
        test.setRunNdt(testSettingsRequest.isNdt());
        test.setMeasurementType(testSettingsRequest.getMeasurementType());
        return test;
    }

    private String getServerAddress(Boolean ipV6, TestServer testServer) {
        if (ipV6 == null)
            return testServer.getWebAddress();
        else if (ipV6)
            return testServer.getWebAddressIpV6();
        else
            return testServer.getWebAddressIpV4();
    }

    private Boolean isIpV6(TestSettingsRequest.ProtocolVersion protocolVersion, InetAddress clientAddress, ErrorResponse errorResponse, Locale locale) {
        if (protocolVersion != null) {
            //allow clients to explicitly request a specific version in the test request
            if (TestSettingsRequest.ProtocolVersion.IPV6.equals(protocolVersion)) {
                return true;
            } else if (TestSettingsRequest.ProtocolVersion.IPV4.equals(protocolVersion)) {
                return false;
            } else {
                errorResponse.getError().add(getErrorMessage("ERROR_IP_STRING", locale));
                return null;
            }
        } else {
            if (clientAddress instanceof Inet6Address) {
                return true;
            } else if (clientAddress instanceof Inet4Address) {
                return false;
            } else {
                return null; // should never happen, unless ipv > 6 is available
            }
        }
    }

    private RtrClient getClientOrAddError(UUID uuid, ErrorResponse errorResponse, Locale locale) {
        RtrClient client = null;
        if (uuid != null) {
            client = clientService.getClientByUUID(uuid);

            if (client == null)
                errorResponse.getError().add(getErrorMessageAndRollback("ERROR_DB_GET_CLIENT", locale));
        }
        return client;
    }

    private LoopModeSettings toLoopModeSettings(TestSettingsRequest.LoopModeInfo loopModeInfo) {
        var loopModeSettings = new LoopModeSettings();
        if (loopModeInfo.getClientUuid() != null)
            loopModeSettings.setClientUuid(UUID.fromString(loopModeInfo.getClientUuid()));
        if (loopModeInfo.getTestUuid() != null)
            loopModeSettings.setTestUuid(UUID.fromString(loopModeInfo.getTestUuid()));
        loopModeSettings.setMaxDelay(loopModeInfo.getMaxDelay());
        loopModeSettings.setMaxMovement(loopModeInfo.getMaxMovement());
        loopModeSettings.setMaxTests(loopModeInfo.getMaxTests());
        loopModeSettings.setTestCounter(loopModeInfo.getTestCounter());
        return loopModeSettings;
    }

    private Integer getNumberOfThreadsOrDefault(Integer numberOfThreads) {
        //allow clients to explicitly request a certain num_threads
        return numberOfThreads != null && numberOfThreads > 0 ? numberOfThreads : applicationProperties.getThreads();
    }

    private String getLanguageIfSupportedOrDefault(String language) {
        return applicationProperties.getLanguage().getSupportedLanguages().contains(language.toLowerCase()) ? language : applicationProperties.getLanguage().getDefaultLanguage();
    }

    private String getErrorMessageAndRollback(String key, Locale locale) {
        rollBackService.setRollBackOnly();
        return getErrorMessage(key, locale);
    }

    private String getErrorMessage(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }
}
