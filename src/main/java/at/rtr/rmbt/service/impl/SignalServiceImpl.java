package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.constant.ErrorMessage;
import at.rtr.rmbt.enums.ServerType;
import at.rtr.rmbt.enums.TestPlatform;
import at.rtr.rmbt.enums.TestStatus;
import at.rtr.rmbt.exception.ClientNotFoundException;
import at.rtr.rmbt.exception.InvalidSequenceException;
import at.rtr.rmbt.exception.TestNotFoundException;
import at.rtr.rmbt.mapper.SignalMapper;
import at.rtr.rmbt.mapper.TestMapper;
import at.rtr.rmbt.model.*;
import at.rtr.rmbt.repository.*;
import at.rtr.rmbt.request.*;
import at.rtr.rmbt.response.*;
import at.rtr.rmbt.service.*;
import at.rtr.rmbt.utils.*;
import com.google.common.net.InetAddresses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.servlet.http.HttpServletRequest;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static at.rtr.rmbt.constant.Constants.NETWORK_TYPE_WLAN;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignalServiceImpl implements SignalService {

    private final TestRepository testRepository;
    private final ProviderRepository providerRepository;
    private final UUIDGenerator uuidGenerator;
    private final ClientRepository clientRepository;
    private final SignalMapper signalMapper;
    private final RadioSignalRepository radioSignalRepository;
    private final GeoLocationRepository geoLocationRepository;
    private final TestMapper testMapper;
    private final GeoLocationService geoLocationService;
    private final RadioCellService radioCellService;
    private final RadioSignalService radioSignalService;
    private final SignalRepository signalRepository;
    private final FencesService fencesService;
    private final TestServerService testServerService;
    private final SettingsRepository settingsRepository;
    private final LoopModeSettingsService loopModeSettingsService;
    private final CellLocationService cellLocationService;

    /** Fallback duration (ms) for a signal measurement / session when no DB setting is present. */
    private static final long DEFAULT_MAX_SIGNAL_MEASUREMENT_MS = 14400000L; // 4 hours
    /** Fallback UDP port for the signal measurement test server when the server has none configured. */
    private static final String DEFAULT_UDP_PORT = "444";


    @Override
    public Page<SignalMeasurementResponse> getSignalsHistory(Pageable pageable) {
        return testRepository.findAllByRadioCellIsNotEmptyAndNetworkTypeNotIn(pageable, Collections.singletonList(NETWORK_TYPE_WLAN))
                .map(signalMapper::signalToSignalMeasurementResponse);
    }

    @Override
    @Transactional
    public SignalMeasurementSettingsResponse processSignalMeasurementRequest(SignalMeasurementRegisterRequest signalMeasurementRegisterRequest, HttpServletRequest httpServletRequest, Map<String, String> headers) {
        var ip = HeaderExtrudeUtil.getIpFromNgNixHeader(httpServletRequest, headers);

        var uuid = uuidGenerator.generateUUID();
        var openTestUUID = uuidGenerator.generateUUID();

        // Check if client exists
        var client = findClientOrThrow(signalMeasurementRegisterRequest.getClientUuid());

        var clientAddress = InetAddresses.forString(ip);
        var clientIpString = InetAddresses.toAddrString(clientAddress);

        var asInformation = HelperFunctions.getASInformationForSignalRequest(clientAddress);


        // check if client submitted a loopUuid
        var loopUuid = signalMeasurementRegisterRequest.getLoopUuid();
        int loopTestCounter;

        if (loopUuid == null) {
            loopUuid = uuidGenerator.generateUUID();
            loopTestCounter = 1;
        }
        else {
            // check if loop uuid exists and if it belongs to the client
            if (!loopModeSettingsService.existsByLoopUuidAndClientUuid(loopUuid, client.getUuid())) {
                throw new TestNotFoundException(
                        String.format(ErrorMessage.TEST_WITH_LOOP_UUID_NOT_FOUND, loopUuid)
                );
            }
            // get latest counter
            loopTestCounter = loopModeSettingsService.findMaxTestCounterByLoopUuid(loopUuid).orElse(0);
            // increase counter by one
            loopTestCounter = loopTestCounter + 1;
        }

        // create new loop record
        LoopModeSettings loopModeSettings = toLoopModeSettings(loopUuid, uuid, client.getUuid(), loopTestCounter);


        TestStatus regStatus = Boolean.TRUE.equals(signalMeasurementRegisterRequest.getSignal())
                ? TestStatus.SIGNAL_STARTED
                : TestStatus.COVERAGE_STARTED;

        // get geoIP country, used for selecting the UDP server
        String countryIp = GeoIpHelper.lookupCountry(clientAddress);
        var clientTime = getClientTimeFromSignalRequest(signalMeasurementRegisterRequest);

        var test = Test.builder()
                .uuid(uuid)
                .openTestUuid(openTestUUID)
                .client(client)
                .clientPublicIp(clientIpString)
                .clientPublicIpAnonymized(HelperFunctions.anonymizeIp(clientAddress))
                .timezone(signalMeasurementRegisterRequest.getTimezone())
                .clientTime(clientTime)
                .time(clientTime)
                .publicIpAsn(asInformation.getNumber())
                .publicIpAsName(asInformation.getName())
                .countryAsn(asInformation.getCountry())
                .countryGeoip(countryIp)
                .publicIpRdns(HelperFunctions.reverseDNSLookup(clientAddress))
                .status(regStatus)
                .lastSequenceNumber(-1)
                .useSsl(false) // hardcode because of database constraint
                .measurementType(signalMeasurementRegisterRequest.getMeasurementType())
                .clientLanguage(signalMeasurementRegisterRequest.getClientLanguage())
                .softwareRevision(signalMeasurementRegisterRequest.getSoftwareRevision())
                .model(signalMeasurementRegisterRequest.getModel())
                .osVersion(signalMeasurementRegisterRequest.getOsVersion())
                .clientName(ServerType.valueOf(signalMeasurementRegisterRequest.getClient_name()))
                .clientSoftwareVersion(signalMeasurementRegisterRequest.getClientSoftwareVersion())
                .device(signalMeasurementRegisterRequest.getDevice())
                .platform(TestPlatform.valueOf(signalMeasurementRegisterRequest.getPlatform().toUpperCase()))
                .build();
                // version (0.3), softwareVersionCode (11), type (MOBILE), name (RMBT), client (RMBT)

        var savedTest = testRepository.saveAndFlush(test);
        loopModeSettingsService.save(loopModeSettings);

        // Max duration (ms) for a single signal measurement and for the whole session, with fallback.
        long maxSignalMeasurementSeconds = getLongSettingOrDefault("max_coverage_measurement_seconds", DEFAULT_MAX_SIGNAL_MEASUREMENT_MS);
        long maxSignalMeasurementSessionSeconds = getLongSettingOrDefault("max_coverage_session_seconds", DEFAULT_MAX_SIGNAL_MEASUREMENT_MS);

        log.info("UDP-Country = {}", countryIp);

        // Select the UDP test server by geoIP country.
        TestServer rmbtUdpServer = testServerService.findActiveByServerTypeInAndCountry(List.of(ServerType.RMBTudp), countryIp, null);

        final String sharedSecret = rmbtUdpServer.getKey();
        final boolean isV4Client = inetAddressIsv4(clientAddress);
        final int protocolVersion = isV4Client ? 4 : 6; // IP protocol version
        final String hostname = isV4Client ? rmbtUdpServer.getWebAddressIpV4() : rmbtUdpServer.getWebAddressIpV6();
        final String port = rmbtUdpServer.getPort() != null ? rmbtUdpServer.getPort().toString() : DEFAULT_UDP_PORT;

        return SignalMeasurementSettingsResponse.builder()
                .provider(providerRepository.getProviderNameByTestId(savedTest.getUid()))
                .clientRemoteIp(ip)
                .testUUID(savedTest.getUuid())
                .pingToken(generatePingToken(sharedSecret, clientAddress))
                .pingHost(hostname)
                .pingPort(port)
                .ipVersion(protocolVersion)
                .maxSignalMeasurementSeconds(maxSignalMeasurementSeconds)
                .maxSignalMeasurementSessionSeconds(maxSignalMeasurementSessionSeconds)
                .loopUUID(loopUuid)
                .loopTestCounter(loopTestCounter)
                .build();
    }

    private LoopModeSettings toLoopModeSettings(UUID loopUUID,UUID testUUID, UUID clientUUID, int testCounter) {
        var loopModeSettings = new LoopModeSettings();
        loopModeSettings.setLoopUuid(loopUUID);
        loopModeSettings.setClientUuid(clientUUID);
        loopModeSettings.setTestUuid(testUUID);
        loopModeSettings.setTestCounter(testCounter);
        loopModeSettings.setMaxDelay(null);
        loopModeSettings.setMaxMovement(null);
        loopModeSettings.setMaxTests(null);
        loopModeSettings.setCertMode(null);
        return loopModeSettings;
    }


    @Override
    @Transactional
    public void processSignalMeasurementResult(SignalMeasurementResultRequest signalMeasurementResultRequest,
                                      HttpServletRequest httpServletRequest,
                                      Map<String, String> headers) {
        log.info("SignalMeasurementResultRequest = {}", signalMeasurementResultRequest);
        UUID testUuid = getTestUUID(signalMeasurementResultRequest);

        // Check if client uuid exists
        findClientOrThrow(signalMeasurementResultRequest.getClientUUID());

        // Try to find test in correct started state or throw exception
        Test updatedTest = testRepository.findByUuidAndStatusesInLocked(testUuid, Config.SIGNAL_MEASUREMENT_RESULT_STATUSES)
                .orElseThrow(() -> new TestNotFoundException(String.format(ErrorMessage.STARTED_TEST_NOT_FOUND, testUuid)));
        updatedTest.setStatus(TestStatus.COVERAGE);

        testMapper.updateTestWithSignalMeasurementResultRequest(signalMeasurementResultRequest, updatedTest);

        // write android permission statuses into test.android_permissions (jsonb)
        Optional.ofNullable(signalMeasurementResultRequest.getPermissionStatuses())
                .ifPresent(updatedTest::setAndroidPermissions);

        // IP address as reported by the client for test.client_ip_local
        updateIpAddress(signalMeasurementResultRequest.getTestIpLocal(), updatedTest);

        // IP address as seen by the server for test.source_ip
        setSourceIp(httpServletRequest, headers, updatedTest);

        // If the public IP seen at the result submission (source_ip) differs from the one captured at the registration
        // (client_public_ip), the client's network changed between register and result.
        processClientPublicIpChanged(updatedTest);

        // A fence's ping proves the client's IP is correct (the ping server only answers a
        // reachable source IP); log whether any fence in this submission carried a ping.
        if (!fencesContainPing(signalMeasurementResultRequest.getFences())) {
            nullIClientiPInfos(updatedTest);
        }

        // cellLocations
        processCellLocation(signalMeasurementResultRequest.getCellLocations(), updatedTest);

        // geoLocations
        processGeoLocation(signalMeasurementResultRequest.getGeoLocations(), updatedTest);

        // radioInfo (cells, signals)
        processRadioInfo(signalMeasurementResultRequest.getRadioInfo(), updatedTest);

        // If at least one fence is present, the test location is defined by the first
        // (oldest, index 0) fence rather than by the geoLocations. The geoLocations are
        // still stored above; only the test's representative position is overridden.
        applyFenceLocation(signalMeasurementResultRequest.getFences(), updatedTest);

        log.info("Updated test before save = {}", updatedTest);
        testMapper.updateTestLocation(updatedTest);
        testRepository.saveAndFlush(updatedTest);

        processFences(signalMeasurementResultRequest.getFences(), updatedTest);
    }


    @Override
    public SignalDetailsResponse getSignalStrength(UUID testUUID) {
        Test test = testRepository.findByUuid(testUUID)
                .orElseThrow();
        Map<UUID, RadioCell> radioCellUUIDs = test.getRadioCell().stream()
                .collect(Collectors.toMap(RadioCell::getUuid, Function.identity()));

        List<GeoLocation> geoLocations = geoLocationRepository.findAllByTestOrderByTimeAsc(test);
        List<RadioSignal> radioSignals = radioSignalRepository.findAllByCellUUIDInOrderByTimeAsc(radioCellUUIDs.keySet());
        log.info("Info signal number:" + radioSignals.size());
        log.info("Info location number:" + geoLocations.size());
        return SignalDetailsResponse.builder()
                .signalStrength(radioSignals.stream()
                        .map(signal -> {
                            var signalStrengthResponseBuilder = SignalStrengthResponse.builder()
                                    .time(TimeUtils.formatToSeconds(signal.getTimeNs()))
                                    .signalStrength(getSignalStrength(signal));
                            setRadioCellInfo(radioCellUUIDs, signal, signalStrengthResponseBuilder);
                            return signalStrengthResponseBuilder.build();
                        })
                        .collect(Collectors.toList()))
                .testResponse(testMapper.testToTestResponse(test))
                .signalLocation(toLocationResponse(geoLocations))
                .build();
    }

    private void setRadioCellInfo(Map<UUID, RadioCell> radioCellUUIDs, RadioSignal signal, SignalStrengthResponse.SignalStrengthResponseBuilder builder) {
        Optional.ofNullable(radioCellUUIDs.get(signal.getCellUUID()))
                .ifPresent(radioCell -> builder
                        .technology(HelperFunctions.getNetworkTypeName(signal.getNetworkTypeId()))
                        .ci(radioCell.getAreaCode())
                        .tac(radioCell.getLocationId())
                        .pci(radioCell.getPrimaryScramblingCode())
                        .earfcn(radioCell.getChannelNumber())
                        .band(getFiBand(radioCell))
                        .frequency(getFiFrequency(radioCell))
                );
    }

    private List<SignalLocationResponse> toLocationResponse(List<GeoLocation> geoLocations) {
        return geoLocations.stream()
                .map(geoLocation -> SignalLocationResponse.builder()
                        .accuracy(FormatUtils.format(Constants.SIGNAL_STRENGTH_ACCURACY_TEMPLATE, geoLocation.getAccuracy()))
                        .speed(FormatUtils.format(Constants.SIGNAL_STRENGTH_SPEED_TEMPLATE, geoLocation.getSpeed()))
                        .altitude(FormatUtils.format(Constants.SIGNAL_STRENGTH_ALTITUDE_TEMPLATE, geoLocation.getAltitude()))
                        .bearing(FormatUtils.format(Constants.SIGNAL_STRENGTH_BEARING_TEMPLATE, geoLocation.getBearing()))
                        .location(geoLocation.getLocation())
                        .time(TimeUtils.formatToSeconds(geoLocation.getTimeNs()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void processSignalRequests(Collection<SignalRequest> signalRequests, Test test) {
        int minSignalStrength = Integer.MAX_VALUE; //measured as RSSI (GSM,UMTS,Wifi)
        int minLteRsrp = Integer.MAX_VALUE; //signal strength measured as RSRP
        int minLteRsrq = Integer.MAX_VALUE; //signal quality of LTE measured as RSRQ
        int minLinkSpeed = Integer.MAX_VALUE;
        List<Signal> newSignals = new ArrayList<>();

        for (SignalRequest signalDataItem : signalRequests) {
            Signal newSignal = signalMapper.signalRequestToSignal(signalDataItem, test);
            newSignals.add(newSignal);
            if (test.getNetworkType() == 99) // wlan
            {
                if (newSignal.getWifiRSSI() != null && newSignal.getWifiRSSI() < minSignalStrength) {
                    minSignalStrength = newSignal.getWifiRSSI();
                }
            } else if (newSignal.getSignalStrength() != null && newSignal.getSignalStrength() < minSignalStrength) {
                minSignalStrength = newSignal.getSignalStrength();
            }

            if (newSignal.getLteRSRP() != null && newSignal.getLteRSRP() < minLteRsrp) {
                minLteRsrp = newSignal.getLteRSRP();
            }

            if (newSignal.getLteRSRP() != null && (newSignal.getLteRSRQ() < minLteRsrq && !(Math.abs(newSignal.getLteRSRQ()) > 19.5 || Math.abs(newSignal.getLteRSRQ()) < 3.0))) {
                minLteRsrq = newSignal.getLteRSRQ();
            }

            if (newSignal.getWifiLinkSpeed() != null && newSignal.getWifiLinkSpeed() < minLinkSpeed) {
                minLinkSpeed = newSignal.getWifiLinkSpeed();
            }
        }

        // set rssi value (typically GSM,UMTS, but also old LTE-phones)
        if (minSignalStrength < 0) { // 0 dBm is out of range
            test.setSignalStrength(minSignalStrength);
        }
        // set rsrp value (typically LTE)
        if (minLteRsrp < 0) { // 0 dBm is out of range
            test.setLteRsrp(minLteRsrp);
        }
        // set rsrq value (LTE)
        if (minLteRsrp < 0) {
            test.setLteRsrq(minLteRsrq);
        }

        if (minLinkSpeed != Integer.MAX_VALUE) {
            test.setWifiLinkSpeed(minLinkSpeed);
        }
        signalRepository.saveAll(newSignals);
    }

    private void processGeoLocation(List<GeoLocationRequest>  geoLocations, Test updatedTest) {
        if (Objects.nonNull(geoLocations)) {
            geoLocationService.processGeoLocationRequests(geoLocations, updatedTest);
        }
    }

    private void processCellLocation(List<CellLocationRequest>  cellLocations, Test updatedTest) {
        if (Objects.nonNull(cellLocations)) {
            cellLocationService.saveCellLocationRequests(cellLocations, updatedTest);
        }
    }

    private void processFences(List<FencesRequest>  fences, Test updatedTest) {
        if (Objects.nonNull(fences)) {
            fencesService.processFencesRequests(fences, updatedTest);
        }
    }

    /**
     * Returns {@code true} if at least one fence reported a ping value ({@code avg_ping_ms}).
     * <p>
     * A returned ping proves the client's IP is correct: the ping server only answers a
     * reachable source address, so a non-null ping in any fence confirms the IP. The result
     * is {@code false} when the submission contains no fence, or when every fence is missing
     * a ping value. The outcome is logged.
     */
    static boolean fencesContainPing(List<FencesRequest> fences) {
        boolean ipConfirmedByPing = fences != null && fences.stream()
                .map(FencesRequest::getAvgPingMs)
                .anyMatch(Objects::nonNull);
        log.info("coverageResult fences confirm client IP via ping = {}", ipConfirmedByPing);
        return ipConfirmedByPing;
    }

    /**
     * Defines the test's representative location from the first (oldest, index 0) fence, if any
     * fence is present. Since a fence has no client {@code geo_location} of its own, a single
     * geo_location row with a server-generated UUID is created from the fence center and assigned
     * to the test (which also sets lat/long, accuracy and provider); the derived geometries are
     * computed afterwards by {@code updateTestLocation}. Accuracy and provider are taken from the
     * fence's nested location as-is (NULL when the client did not supply them — no default is
     * invented). When no fence is present the location set from the geoLocations is left untouched.
     */
    private void applyFenceLocation(List<FencesRequest> fences, Test updatedTest) {
        if (fences == null || fences.isEmpty()) {
            return;
        }
        FencesRequest firstFence = fences.get(0);
        SimpleLocationRequest location = firstFence.getLocation();
        if (location == null || location.getLatitude() == null || location.getLongitude() == null) {
            return;
        }
        // Derive the geo_location timestamp from the fence/test time: test time + fence offset
        // (same derivation FencesServiceImpl uses for fence_time).
        final ZonedDateTime fenceTime = updatedTest.getTime() == null
                ? null
                : updatedTest.getTime().plus(Objects.requireNonNullElse(firstFence.getOffsetMs(), 0L), ChronoUnit.MILLIS);
        geoLocationService.createAndAssignGeoLocation(
                updatedTest, location.getLatitude(), location.getLongitude(),
                location.getAccuracy(), location.getProvider(), fenceTime);
    }

    private String getSignalStrength(RadioSignal signal) {
        return Stream.of(FormatUtils.format(Constants.SIGNAL_STRENGTH_DBM_TEMPLATE, signal.getSignalStrength() != null
                                ? signal.getSignalStrength() : signal.getLteRSRP()),
                        FormatUtils.format(Constants.SIGNAL_STRENGTH_TIMING_ADVANCE_TEMPLATE, signal.getTimingAdvance()),
                        FormatUtils.format(Constants.SIGNAL_STRENGTH_RSRQ_TEMPLATE, signal.getLteRSRQ()))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(Constants.SIGNAL_STRENGTH_DELIMITER));

    }

    private Integer getFiBand(RadioCell radioCell) {
        return Optional.ofNullable(BandCalculationUtil.getFrequencyInformationFromChannelNumberAndTechnology(radioCell.getChannelNumber(), radioCell.getTechnology()))
                .map(BandCalculationUtil.FrequencyInformation::getBand)
                .orElse(null);
    }

    private Double getFiFrequency(RadioCell radioCell) {
        return Optional.ofNullable(BandCalculationUtil.getFrequencyInformationFromChannelNumberAndTechnology(radioCell.getChannelNumber(), radioCell.getTechnology()))
                .map(BandCalculationUtil.FrequencyInformation::getFrequencyDL)
                .orElse(null);
    }

    private void processRadioInfo(RadioInfoRequest radioInfo, Test updatedTest) {
        if (Objects.nonNull(radioInfo)) {
            if (!CollectionUtils.isEmpty(radioInfo.getCells())) {
                radioCellService.processRadioCellRequests(radioInfo.getCells(), updatedTest);
            }
            if (!CollectionUtils.isEmpty(radioInfo.getSignals())) {
                radioSignalService.saveRadioSignalRequests(radioInfo, updatedTest);
            }
        }
    }

    private void updateIpAddress(String ipLocal, Test updatedTest) {
        if (Objects.nonNull(ipLocal)) {
            InetAddress ipLocalAddress = InetAddresses.forString(ipLocal);
            updatedTest.setClientIpLocal(InetAddresses.toAddrString(ipLocalAddress));
            updatedTest.setClientIpLocalAnonymized(HelperFunctions.anonymizeIp(ipLocalAddress));
            updatedTest.setClientIpLocalType(HelperFunctions.IpType(ipLocalAddress));

            // clientPublicIp might be null, avoid NullPointerException
            String clientPublicIp = updatedTest.getClientPublicIp();
            if (clientPublicIp != null) {
                InetAddress ipPublicAddress = InetAddresses.forString(clientPublicIp);
                updatedTest.setNatType(HelperFunctions.getNatType(ipLocalAddress, ipPublicAddress));
            }
        }
    }

    // TODO: Refactor with/in HeaderExtrudeUtil
    // - ResultServiceImpl.setSourceIp(...)
    // - SignalServiceImpl.setSourceIp(...)
    // - SignalServiceImpl.processSignalMeasurementRequest(...) (clientPublicIp part)
    private void setSourceIp(HttpServletRequest httpServletRequest, Map<String, String> headers, Test test) {
        InetAddress sourceAddress = InetAddresses.forString(
                HeaderExtrudeUtil.getIpFromNgNixHeader(httpServletRequest, headers));
        test.setSourceIp(InetAddresses.toAddrString(sourceAddress));
        test.setSourceIpAnonymized(HelperFunctions.anonymizeIp(sourceAddress));
    }

    /**
     * Processes a public IP changed between register and result.
     * (Currently logging event only)
     */
    private void processClientPublicIpChanged(Test test) {
        final String clientPublicIp = test.getClientPublicIp();
        final String sourceIp = test.getSourceIp();
        if (clientPublicIp == null || sourceIp == null || clientPublicIp.equals(sourceIp)) {
            return;
        }
        log.info("source_ip ({}) differs from client_public_ip ({}) at test {}",
                sourceIp, clientPublicIp, test.getUuid());
    }

    private void nullIClientiPInfos(Test test) {
        test.setClientPublicIp(null);
        test.setClientPublicIpAnonymized(null);
        test.setPublicIpRdns(null);
        test.setCountryGeoip(null);
        test.setPublicIpAsName(null);
        test.setCountryAsn(null);
        test.setPublicIpAsn(null);
    }

    private RtrClient findClientOrThrow(UUID clientUuid) {
        return clientRepository.findByUuid(clientUuid)
                .orElseThrow(() -> new ClientNotFoundException(String.format(ErrorMessage.CLIENT_NOT_FOUND, clientUuid)));
    }

    /** Reads a numeric {@code settings} value (language-agnostic) by key, falling back to a default. */
    long getLongSettingOrDefault(final String key, final long defaultValue) {
        return settingsRepository.findFirstByKeyAndLangIsNullOrKeyAndLangOrderByLang(key, key, null)
                .map(Settings::getValue)
                .map(Long::parseLong)
                .orElse(defaultValue);
    }

    private UUID getTestUUID(SignalMeasurementResultRequest signalMeasurementResultRequest) {
        if (Objects.nonNull(signalMeasurementResultRequest.getTestUUID())) {
            return signalMeasurementResultRequest.getTestUUID();
        } else {
            if (signalMeasurementResultRequest.getSequenceNumber() != 0) {
                throw new InvalidSequenceException();
            }
            return uuidGenerator.generateUUID();
        }
    }

    private ZonedDateTime getClientTimeFromSignalRequest(SignalMeasurementRegisterRequest signalRegisterRequest) {
        return TimeUtils.getZonedDateTimeFromMillisAndTimezone(signalRegisterRequest.getTime(), signalRegisterRequest.getTimezone());
    }

    // Utility: Hex encoding for debug prints
    @SuppressWarnings("unused")
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Utility: Take first N bytes of a byte array
    private static byte[] firstNBytes(byte[] input, int n) {
        byte[] out = new byte[n];
        System.arraycopy(input, 0, out, 0, n);
        return out;
    }

    // Utility to distinguish between IPv4 and IPv6 addresses
    private static boolean inetAddressIsv4(InetAddress ip) {

        return ip instanceof Inet4Address;
    }

        private static String generatePingToken(String sharedSecret, InetAddress ip)  {
        // Reference code:
        // src/main/java/at/rtr/rmbt/facade/TestSettingsFacade.java

        byte[] ipBytes;

        if (ip instanceof Inet4Address) {
            // Convert IPv4 to IPv4-mapped IPv6 (128-bit)
            byte[] ipv4Bytes = ip.getAddress();
            ipBytes = new byte[16];
            Arrays.fill(ipBytes, 0, 10, (byte) 0);
            ipBytes[10] = (byte) 0xff;
            ipBytes[11] = (byte) 0xff;
            System.arraycopy(ipv4Bytes, 0, ipBytes, 12, 4);
        } else if (ip instanceof Inet6Address) {
            ipBytes = ip.getAddress();
        } else {
            throw new IllegalArgumentException("Invalid IP address type");
        }

        // process current time
        // Compute 32-bit current time (just like Python: int(time.time()) & 0xFFFFFFFF)
        long nowSeconds = Instant.now().getEpochSecond() & 0xFFFFFFFFL;
        int currentTime32 = (int) nowSeconds; // 32-bit truncated
        // 4-byte array (big-endian) for the "struct.pack('>I', current_time)"
        ByteBuffer timeBuffer = ByteBuffer.allocate(4);
        timeBuffer.order(ByteOrder.BIG_ENDIAN);
        timeBuffer.putInt(currentTime32);

        // 8-byte array (big-endian) for the HMAC calculations
        // Python uses current_time as a 32-bit number, then extends to 8 bytes in big-endian
        long extendedTime = currentTime32 & 0xFFFFFFFFL; // ensure no sign extension
        ByteBuffer timeBuffer8 = ByteBuffer.allocate(8);
        timeBuffer8.order(ByteOrder.BIG_ENDIAN);
        timeBuffer8.putLong(extendedTime);
        byte[] timeBytes = Arrays.copyOfRange(timeBuffer8.array(), 4, 8);
        // byte[] timeBytes = Arrays.copyOfRange(timeBuffer8.array(), 1, 8);

        // Print current time for debugging (similar to Python)
        // LocalDateTime dateTime = LocalDateTime.ofEpochSecond(nowSeconds, 0, ZoneOffset.UTC);
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // System.out.println("Current time: " + dateTime.format(formatter));
        // System.out.println("time hex: " + bytesToHex(timeBytes));

        // HMAC-SHA256 with seed as key (take first 8 bytes of digest)
        // in src/main/java/at/rtr/rmbt/facade/TestSettingsFacade.java
        // the secret is taken from the testserver as
        // testServer.getKey().getBytes()

        // First hmac - general check (seed, time) with length 8 bytes
        final byte[] packetHashTime = HelperFunctions.calculateSha256HMAC(sharedSecret.getBytes(), timeBytes);
        final byte[] packetHashTime8 = firstNBytes(packetHashTime, 8);
        // System.out.println("hmac (in hex): " + bytesToHex(packetHashTime8));

        // Second hmac - check for source IP
        final byte[] packetHashIp = HelperFunctions.calculateSha256HMAC(sharedSecret.getBytes(), timeBytes, ipBytes);
        final byte[] packetHashIp4 = firstNBytes(packetHashIp, 4);

        // Construct final token
        ByteBuffer dataBuffer = ByteBuffer.allocate(16);
        dataBuffer.put(timeBytes);  // 4 bytes
        dataBuffer.put(packetHashTime8); // 8 bytes
        dataBuffer.put(packetHashIp4); // 4 bytes
        byte[] token = dataBuffer.array();
        return Base64.getEncoder().encodeToString(token);
    }
}
