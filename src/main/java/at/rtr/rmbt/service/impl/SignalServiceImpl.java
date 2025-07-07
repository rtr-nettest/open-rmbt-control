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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static at.rtr.rmbt.constant.Constants.NETWORK_TYPE_WLAN;
import static at.rtr.rmbt.constant.HeaderConstants.URL;
import static at.rtr.rmbt.constant.URIConstants.SIGNAL_RESULT;

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


    @Override
    public Page<SignalMeasurementResponse> getSignalsHistory(Pageable pageable) {
        return testRepository.findAllByRadioCellIsNotEmptyAndNetworkTypeNotIn(pageable, Collections.singletonList(NETWORK_TYPE_WLAN))
                .map(signalMapper::signalToSignalMeasurementResponse);
    }

    @Override
    public SignalSettingsResponse processSignalRequest(SignalRegisterRequest signalRegisterRequest, HttpServletRequest httpServletRequest, Map<String, String> headers) {
        var ip = HeaderExtrudeUtil.getIpFromNgNixHeader(httpServletRequest, headers);

        var uuid = uuidGenerator.generateUUID();
        var openTestUUID = uuidGenerator.generateUUID();

        var client = clientRepository.findByUuid(signalRegisterRequest.getUuid())
                .orElseThrow(() -> new ClientNotFoundException(String.format(ErrorMessage.CLIENT_NOT_FOUND, signalRegisterRequest.getUuid())));

        var clientAddress = InetAddresses.forString(ip);
        var clientIpString = InetAddresses.toAddrString(clientAddress);

        var asInformation = HelperFunctions.getASInformationForSignalRequest(clientAddress);

        var test = Test.builder()
                .uuid(uuid)
                .openTestUuid(openTestUUID)
                .client(client)
                .clientPublicIp(clientIpString)
                .clientPublicIpAnonymized(HelperFunctions.anonymizeIp(clientAddress))
                .timezone(signalRegisterRequest.getTimezone())
                .clientTime(getClientTimeFromSignalRequest(signalRegisterRequest))
                .time(getClientTimeFromSignalRequest(signalRegisterRequest))
                .publicIpAsn(asInformation.getNumber())
                .publicIpAsName(asInformation.getName())
                .countryAsn(asInformation.getCountry())
                .publicIpRdns(HelperFunctions.getReverseDNS(clientAddress))
                .status(TestStatus.SIGNAL_STARTED)
                .lastSequenceNumber(-1)
                .useSsl(false)//TODO hardcode because of database constraint
                .measurementType(signalRegisterRequest.getMeasurementType())
                .build();

        var savedTest = testRepository.saveAndFlush(test);

        return SignalSettingsResponse.builder()
                .provider(providerRepository.getProviderNameByTestId(savedTest.getUid()))
                .clientRemoteIp(ip)
                .resultUrl(getSignalResultUrl(httpServletRequest))
                .testUUID(savedTest.getUuid())
                .build();
    }

    @Override
    public CoverageSettingsResponse processCoverageRequest(CoverageRegisterRequest coverageRegisterRequest, HttpServletRequest httpServletRequest, Map<String, String> headers) {
        var ip = HeaderExtrudeUtil.getIpFromNgNixHeader(httpServletRequest, headers);

        var uuid = uuidGenerator.generateUUID();
        var openTestUUID = uuidGenerator.generateUUID();

        var client = clientRepository.findByUuid(coverageRegisterRequest.getClientUuid())
                .orElseThrow(() -> new ClientNotFoundException(String.format(ErrorMessage.CLIENT_NOT_FOUND, coverageRegisterRequest.getClientUuid())));

        var clientAddress = InetAddresses.forString(ip);
        var clientIpString = InetAddresses.toAddrString(clientAddress);

        var asInformation = HelperFunctions.getASInformationForSignalRequest(clientAddress);

        TestStatus regStatus = TestStatus.COVERAGE_STARTED;
        Boolean supportSignal = coverageRegisterRequest.getSignal();
        if (Boolean.TRUE.equals(supportSignal)) {
            regStatus = TestStatus.SIGNAL_STARTED;
        }

        var test = Test.builder()
                .uuid(uuid)
                .openTestUuid(openTestUUID)
                .client(client)
                .clientPublicIp(clientIpString)
                .clientPublicIpAnonymized(HelperFunctions.anonymizeIp(clientAddress))
                .timezone(coverageRegisterRequest.getTimezone())
                .clientTime(getClientTimeFromSignalRequest(coverageRegisterRequest))
                .time(getClientTimeFromSignalRequest(coverageRegisterRequest))
                .publicIpAsn(asInformation.getNumber())
                .publicIpAsName(asInformation.getName())
                .countryAsn(asInformation.getCountry())
                .publicIpRdns(HelperFunctions.getReverseDNS(clientAddress))
                .status(regStatus)
                .lastSequenceNumber(-1)
                .useSsl(false) // hardcode because of database constraint
                .measurementType(coverageRegisterRequest.getMeasurementType())
                .clientLanguage(coverageRegisterRequest.getClientLanguage())
                .softwareRevision(coverageRegisterRequest.getSoftwareRevision())
                .model(coverageRegisterRequest.getModel())
                .osVersion(coverageRegisterRequest.getOsVersion())
                .clientName(ServerType.valueOf(coverageRegisterRequest.getClient_name()))
                .clientSoftwareVersion(coverageRegisterRequest.getClientSoftwareVersion())
                .device(coverageRegisterRequest.getDevice())
                .platform(TestPlatform.valueOf(coverageRegisterRequest.getPlatform().toUpperCase()))
                .build();
                // version (0.3), softwareVersionCode (11), type (MOBILE), name (RMBT), client (RMBT)

        var savedTest = testRepository.saveAndFlush(test);

        // TODO: for debugging a dummy secret is hardcoded
        // Later a specific test server needs to be defined (host, port)
        final String sharedSecret = "topsecret";

        // TODO: Hard coded URLs, later to be defined by pingServer table
        final String hostname_v4 = "udpv4.netztest.at";
        final String hostname_v6 = "udpv6.netztest.at";
        final String port = String.valueOf(444);

        // TODO Add code that takes the server settings from test_server HERE
        //final List<TestServerResponseForSettings> serverUdpResponseList = testServerService.getServersUdp();



        String hostname;
        final boolean isV4Client = inetAddressIsv4(clientAddress);
        // Integer equal to IP protocol version
        final int protocolVersion = isV4Client ? 4 : 6;

        if (isV4Client) {
            hostname= hostname_v4;
        }
        else {
            hostname = hostname_v6;
        }

        return CoverageSettingsResponse.builder()
                .provider(providerRepository.getProviderNameByTestId(savedTest.getUid()))
                .clientRemoteIp(ip)
                .testUUID(savedTest.getUuid())
                .pingToken(generatePingToken(sharedSecret, clientAddress))
                .pingHost(hostname)
                .pingPort(port)
                .ipVersion(protocolVersion)
                .build();
    }

    @Override
    @Transactional
    public SignalResultResponse processSignalResult(SignalResultRequest signalResultRequest) {
        log.info("SignalResultRequest = " + signalResultRequest);
        UUID testUuid = getTestUUID(signalResultRequest);

        Long sequenceNumber = signalResultRequest.getSequenceNumber();

        RtrClient client = clientRepository.findByUuid(signalResultRequest.getClientUUID())
                .orElseThrow(() -> new ClientNotFoundException(String.format(ErrorMessage.CLIENT_NOT_FOUND, signalResultRequest.getClientUUID())));

        Test updatedTest = testRepository.findByUuidAndStatusesInLocked(testUuid, Config.SIGNAL_RESULT_STATUSES)
                .orElseGet(() -> getEmptyGeneratedTest(signalResultRequest, client));
        updatedTest.setStatus(TestStatus.SIGNAL);

        if (Objects.isNull(sequenceNumber) || sequenceNumber <= updatedTest.getLastSequenceNumber()) {
            throw new InvalidSequenceException();
        }
        updatedTest.setLastSequenceNumber(sequenceNumber.intValue());

        testMapper.updateTestWithSignalResultRequest(signalResultRequest, updatedTest);

        updateIpAddress(signalResultRequest.getTestIpLocal(), updatedTest);

        processGeoLocation(signalResultRequest.getGeoLocations(), updatedTest);

        processRadioInfo(signalResultRequest.getRadioInfo(), updatedTest);

        log.info("Updated test before save = " + updatedTest);
        testMapper.updateTestLocation(updatedTest);
        testRepository.saveAndFlush(updatedTest);

        UUID uuidToReturn = updatedTest.getUuid();
        if (updatedTest.getTimestamp().plusMinutes(Constants.SIGNAL_CHANGE_UUID_AFTER_MIN)
                .compareTo(Instant.now().atZone(updatedTest.getTimestamp().getZone())) < 0) {
            log.info("updating signal uuid after " + Constants.SIGNAL_CHANGE_UUID_AFTER_MIN + " minutes");
            uuidToReturn = UUID.randomUUID();
        }

        return SignalResultResponse.builder()
                .testUUID(uuidToReturn)
                .build();
    }


    @Override
    @Transactional
    public CoverageResultResponse processCoverageResult(CoverageResultRequest coverageResultRequest) {
        log.info("CoverageResultRequest = " + coverageResultRequest);
        UUID testUuid = getTestUUID(coverageResultRequest);


        RtrClient client = clientRepository.findByUuid(coverageResultRequest.getClientUUID())
                .orElseThrow(() -> new ClientNotFoundException(String.format(ErrorMessage.CLIENT_NOT_FOUND, coverageResultRequest.getClientUUID())));


        Test updatedTest = testRepository.findByUuidAndStatusesInLocked(testUuid, Config.COVERAGE_RESULT_STATUSES)
                .orElseGet(() -> getEmptyGeneratedTest(coverageResultRequest, client));
        updatedTest.setStatus(TestStatus.COVERAGE);


        testMapper.updateTestWithCoverageResultRequest(coverageResultRequest, updatedTest);

        updateIpAddress(coverageResultRequest.getTestIpLocal(), updatedTest);


        log.info("Updated test before save = " + updatedTest);
        testMapper.updateTestLocation(updatedTest);
        testRepository.saveAndFlush(updatedTest);

        processFences(coverageResultRequest.getFences(), updatedTest);

        //TODO: UUID is no longer changed by backend
        UUID uuidToReturn = updatedTest.getUuid();

        if (updatedTest.getTimestamp().plusMinutes(Constants.SIGNAL_CHANGE_UUID_AFTER_MIN)
                .compareTo(Instant.now().atZone(updatedTest.getTimestamp().getZone())) < 0) {
            log.info("updating signal uuid after " + Constants.SIGNAL_CHANGE_UUID_AFTER_MIN + " minutes");
            uuidToReturn = UUID.randomUUID();
        }

        return CoverageResultResponse.builder()
                // TODO no uuid as result
                .testUUID(uuidToReturn)
                .build();

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

    private void processFences(List<FencesRequest>  fences, Test updatedTest) {
        if (Objects.nonNull(fences)) {
            fencesService.processFencesRequests(fences, updatedTest);
        }
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

            InetAddress ipPublicAddress = InetAddresses.forString(updatedTest.getClientPublicIp());
            updatedTest.setNatType(HelperFunctions.getNatType(ipLocalAddress, ipPublicAddress));
        }
    }

    private UUID getTestUUID(SignalResultRequest signalResultRequest) {
        if (Objects.nonNull(signalResultRequest.getTestUUID())) {
            return signalResultRequest.getTestUUID();
        } else {
            if (signalResultRequest.getSequenceNumber() != 0) {
                throw new InvalidSequenceException();
            }
            return uuidGenerator.generateUUID();
        }
    }

    private UUID getTestUUID(CoverageResultRequest coverageResultRequest) {
        if (Objects.nonNull(coverageResultRequest.getTestUUID())) {
            return coverageResultRequest.getTestUUID();
        } else {
            if (coverageResultRequest.getSequenceNumber() != 0) {
                throw new InvalidSequenceException();
            }
            return uuidGenerator.generateUUID();
        }
    }

    private Test getEmptyGeneratedTest(SignalResultRequest signalResultRequest, RtrClient client) {
        Test newTest = Test.builder()
                .uuid(uuidGenerator.generateUUID())
                .openTestUuid(uuidGenerator.generateUUID())
                .time(getClientTimeFromSignalResultRequest(signalResultRequest))
                .timezone(signalResultRequest.getTimezone())
                .client(client)
                .useSsl(false)
                .lastSequenceNumber(-1)
                .build();

        return testRepository.saveAndFlush(newTest);
    }

    private Test getEmptyGeneratedTest(CoverageResultRequest coverageResultRequest, RtrClient client) {
        Test newTest = Test.builder()
                .uuid(uuidGenerator.generateUUID())
                .openTestUuid(uuidGenerator.generateUUID())
                .time(getClientTimeFromCoverageResultRequest(coverageResultRequest))
                .timezone(coverageResultRequest.getTimezone())
                .client(client)
                .useSsl(false)
                .lastSequenceNumber(-1)
                .build();

        return testRepository.saveAndFlush(newTest);
    }

    private ZonedDateTime getClientTimeFromSignalResultRequest(SignalResultRequest signalResultRequest) {
        return TimeUtils.getZonedDateTimeFromMillisAndTimezone(Math.round(signalResultRequest.getTimeNanos() / 1e6), signalResultRequest.getTimezone());
    }

    private ZonedDateTime getClientTimeFromCoverageResultRequest(CoverageResultRequest coverageResultRequest) {
        if (coverageResultRequest.getTimezone() == null)
            return null;
        else {
            return TimeUtils.getZonedDateTimeFromMillisAndTimezone(Math.round(coverageResultRequest.getTimeNanos() / 1e6), coverageResultRequest.getTimezone());
        }
    }


    private ZonedDateTime getClientTimeFromSignalRequest(SignalRegisterRequest signalRegisterRequest) {
        return TimeUtils.getZonedDateTimeFromMillisAndTimezone(signalRegisterRequest.getTime(), signalRegisterRequest.getTimezone());
    }

    private ZonedDateTime getClientTimeFromSignalRequest(CoverageRegisterRequest signalRegisterRequest) {
        return TimeUtils.getZonedDateTimeFromMillisAndTimezone(signalRegisterRequest.getTime(), signalRegisterRequest.getTimezone());
    }

    private String getSignalResultUrl(HttpServletRequest req) {
        return Optional.ofNullable(req.getHeader(URL))
                .map(url -> String.join(URL, SIGNAL_RESULT))
                .orElse(getDefaultResultUrl(req));
    }

    private String getDefaultResultUrl(HttpServletRequest req) {
        return String.format("%s://%s:%s%s", req.getScheme(), req.getServerName(), req.getServerPort(), req.getRequestURI())
                .replace("Request", "Result");
    }

    // Utility: Hex encoding for debug prints
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

        if (ip instanceof Inet4Address) {
            return true;
        }
        return false;
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
        // TODO: Use correct logging, not print (or remove code)
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(nowSeconds, 0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("Current time: " + dateTime.format(formatter));
        System.out.println("time hex: " + bytesToHex(timeBytes));

        // HMAC-SHA256 with seed as key (take first 8 bytes of digest)
        // in src/main/java/at/rtr/rmbt/facade/TestSettingsFacade.java
        // the secret is taken from the testserver as
        // testServer.getKey().getBytes()

        // First hmac - general check (seed, time) with length 8 bytes
        final byte[] packetHashTime = HelperFunctions.calculateSha256HMAC(sharedSecret.getBytes(), timeBytes);
        final byte[] packetHashTime8 = firstNBytes(packetHashTime, 8);
        System.out.println("hmac (in hex): " + bytesToHex(packetHashTime8));

        // Second hmac - check for source IP
        final byte[] packetHashIp = HelperFunctions.calculateSha256HMAC(sharedSecret.getBytes(), timeBytes, ipBytes);
        final byte[] packetHashIp4 = firstNBytes(packetHashIp, 4);

        // Construct final token
        ByteBuffer dataBuffer = ByteBuffer.allocate(16);
        dataBuffer.put(timeBytes);  // 4 bytes
        dataBuffer.put(packetHashTime8); // 8 bytes
        dataBuffer.put(packetHashIp4); // 4 bytes
        byte[] token = dataBuffer.array();

        // Print results
        System.out.println("Original token (in hex): " + bytesToHex(token));
        String b64Token = Base64.getEncoder().encodeToString(token);
        System.out.println("Token (Base64): " + b64Token);

        return b64Token;

    }
}
