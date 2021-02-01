package at.rtr.rmbt.facade;

import at.rtr.rmbt.exception.TestServerNotFoundException;
import at.rtr.rmbt.model.*;
import at.rtr.rmbt.model.enums.ServerType;
import at.rtr.rmbt.model.enums.TestStatus;
import at.rtr.rmbt.properties.ApplicationProperties;
import at.rtr.rmbt.request.TestSettingsRequest;
import at.rtr.rmbt.response.ErrorResponse;
import at.rtr.rmbt.response.TestSettingsResponse;
import at.rtr.rmbt.service.*;
import at.rtr.rmbt.utils.GeoIpHelper;
import at.rtr.rmbt.utils.HelperFunctions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vdurmont.semver4j.Requirement;
import com.vdurmont.semver4j.Semver;
import com.vdurmont.semver4j.SemverException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

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

    @Autowired
    public TestSettingsFacade(LoopModeSettingsService loopModeSettingsService, ClientTypeService clientTypeService, ClientService clientService, TestServerService testServerService, TestService testService, ApplicationProperties applicationProperties, MessageSource messageSource, ObjectMapper objectMapper) {
        this.loopModeSettingsService = loopModeSettingsService;
        this.clientTypeService = clientTypeService;
        this.clientService = clientService;
        this.testServerService = testServerService;
        this.testService = testService;
        this.applicationProperties = applicationProperties;
        this.messageSource = messageSource;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public TestSettingsResponse updateTestSettings(TestSettingsRequest testSettingsRequest, HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        Long asn;
        String asName = null;
        String asCountry = null;
        String clientIpAddress = request.getRemoteAddr();
        InetAddress clientAddress = null;
        try {
            clientAddress = InetAddress.getByAddress(clientIpAddress.getBytes());
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
            loopModeInfo.setClientUuid(settingUuid);

            //if no loop mode uuid is set - generate one
            if (loopModeInfo.getLoopUuid() == null)
                loopModeInfo.setLoopUuid(UUID.randomUUID().toString());

            //old clients expect a "text_counter"
            if (loopModeInfo.getTestCounter() == null)
                loopModeInfo.setTestCounter(loopModeInfo.getTextCounter());

            LoopModeSettings loopModeSettings = toLoopModeSettings(loopModeInfo);

            loopModeSettings = loopModeSettingsService.save(loopModeSettings);

            ClientType clientType = null;
            if (testSettingsRequest.getClientType() != null) {
                Optional<ClientType> clientTypeOptional = clientTypeService.findByClientType(testSettingsRequest.getClientType());

                if (clientTypeOptional.isPresent())
                    clientType = clientTypeOptional.get();
                else
                    errorResponse.getError().add(getErrorMessageAndRollback("ERROR_DB_GET_CLIENTTYPE", locale));
            }

            Semver version = new Semver(adjustOldVersion(testSettingsRequest.getTestSetVersion()), Semver.SemverType.NPM);
            Requirement requirement = Requirement.buildNPM(applicationProperties.getVersion());

            if (!version.satisfies(requirement)) {
                throw new SemverException("requirement not satisfied");
            }

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
                    boolean testServerEncryption = true; // encryption is mandatory
                    final Boolean ipv6;

                    List<ServerType> serverTypes;

                    if (testSettingsRequest.getCapabilities().getRmbtHttp()) {
                        serverTypes = List.of(ServerType.RMBThttp, ServerType.RMBT);
                    } else if (ServerType.RMBTws.equals(testSettingsRequest.getServerType())) {
                        serverTypes = List.of(ServerType.RMBThttp, ServerType.RMBTws);
                    } else if (ServerType.HW_PROBE.equals(testSettingsRequest.getServerType())) {
                        serverTypes = List.of(ServerType.RMBT);
                        testServerEncryption = false;
                    } else {
                        serverTypes = List.of(ServerType.RMBT);
                    }

                    ipv6 = isIpV6(testSettingsRequest.getProtocolVersion(), clientAddress, errorResponse, locale);

                    Integer numberOfThreads = getNumberOfThreadsOrDefault(testSettingsRequest.getNumberOfThreads());
                    TestServer testServer = null;

                    if (testSettingsRequest.getUserServerSelection()) {
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

                    builder.testServerAddress(getServerAddress(ipv6, testServer))
                        .testServerPort(testServerEncryption ? testServer.getPortSsl() : testServer.getPort())
                        .testServerName(testServer.getName() + " (" + testServer.getCity() + ")")
                        .testServerEncryption(testServerEncryption)
                        .testServerType(testServer.getServerType())
                        .testDuration(String.valueOf(applicationProperties.getDuration()))
                        .testNumberOfThreads(String.valueOf(numberOfThreads))
                        .testNumberOfPings(String.valueOf(applicationProperties.getPings()))
                        .clientRemoteIp(clientIpAddress);

                    String resultUrl;
                    if (request.getHeader("X-Real-URL") == null) {
                        resultUrl = request.getHeader("X-Real-URL");
                    } else {
                        resultUrl = request.getRequestURI();
                    }

                    builder.resultUrl(resultUrl + RESULT_URL);
                    builder.resultQosUrl(resultUrl + RESULT_QOS_URL);


                    if (errorResponse.getError().isEmpty()) {
                        Test test = getTest(testSettingsRequest, clientIpAddress, asn, asName, asCountry, clientAddress, language, timeZoneId, client, testUuid, testOpenUuid, testServerEncryption, numberOfThreads, testServer, geoIpCountry);

                        test = testService.save(test);
                        loopModeSettings.setTestUuid(testUuid);
                        loopModeSettings = loopModeSettingsService.save(loopModeSettings);

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
                                .loopUuid(loopModeSettings.getLoopUuid().toString())
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


    private Test getTest(TestSettingsRequest testSettingsRequest, String clientIpdAddress, Long asn, String asName, String asCountry, InetAddress clientAddress, String language, String timeZoneId, RtrClient client, UUID testUuid, UUID testOpenUuid, boolean testServerEncryption, Integer numberOfThreads, TestServer testServer, String geoIpCountry) {
        Test test = new Test();

        test.setUuid(testUuid);
        test.setOpenTestUuid(testOpenUuid);
        test.setClientId(client.getUid());
        test.setClientName(testSettingsRequest.getServerType());
        test.setClientVersion(testSettingsRequest.getTestSetVersion());
        test.setClientSoftwareVersion(testSettingsRequest.getSoftwareVersion());
        test.setClientLanguage(language);
        test.setClientPublicIp(clientIpdAddress);
        test.setClientPublicIpAnonymized(HelperFunctions.anonymizeIp(clientAddress));
        test.setCountryGeoip(geoIpCountry);
        test.setServerId(testServer.getUid());
        test.setServerPort(testServer.getPort());
        test.setUseSsl(testServerEncryption);
        test.setTimezone(timeZoneId);
        test.setClientTime(ZonedDateTime.ofInstant(Instant.ofEpochSecond(testSettingsRequest.getTime()), ZoneId.of(timeZoneId)));
        test.setDuration(applicationProperties.getDuration());
        test.setNumberOfThreads(numberOfThreads);
        test.setStatus(TestStatus.STARTED);
        test.setSoftwareRevision(testSettingsRequest.getSoftwareRevision());
        test.setClientTestCounter(testSettingsRequest.getTestCounter() == -1 ? null : testSettingsRequest.getTestCounter().longValue());
        test.setClientPreviousTestStatus(testSettingsRequest.getPreviousTestStatus());
        test.setPublicIpAsn(asn);
        test.setPublicIpAsName(asName);
        test.setCountryAsn(asCountry);
        String reverseDns = HelperFunctions.reverseDNSLookup(clientAddress);
        if (StringUtils.isNotBlank(reverseDns))
            test.setPublicIpRdns(reverseDns.replaceFirst("\\.$", ""));
        test.setRunNdt(testSettingsRequest.getNdt());
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

    private String adjustOldVersion(String version) {
        return version.length() == 3 ? version + ".0" : version; //adjust old versions
    }

    private LoopModeSettings toLoopModeSettings(TestSettingsRequest.LoopModeInfo loopModeInfo) {
        var loopModeSettings = new LoopModeSettings();
        loopModeSettings.setClientUuid(UUID.fromString(loopModeInfo.getClientUuid()));
        loopModeSettings.setLoopUuid(UUID.fromString(loopModeInfo.getLoopUuid()));
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
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return getErrorMessage(key, locale);
    }

    private String getErrorMessage(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }
}
