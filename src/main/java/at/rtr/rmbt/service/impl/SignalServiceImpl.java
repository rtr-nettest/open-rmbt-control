package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.constant.ErrorMessage;
import at.rtr.rmbt.enums.TestStatus;
import at.rtr.rmbt.exception.ClientNotFoundException;
import at.rtr.rmbt.exception.InvalidSequenceException;
import at.rtr.rmbt.mapper.SignalMapper;
import at.rtr.rmbt.mapper.TestMapper;
import at.rtr.rmbt.model.*;
import at.rtr.rmbt.repository.*;
import at.rtr.rmbt.request.SignalRegisterRequest;
import at.rtr.rmbt.request.SignalRequest;
import at.rtr.rmbt.request.SignalResultRequest;
import at.rtr.rmbt.response.*;
import at.rtr.rmbt.service.GeoLocationService;
import at.rtr.rmbt.service.RadioCellService;
import at.rtr.rmbt.service.RadioSignalService;
import at.rtr.rmbt.service.SignalService;
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
import java.net.InetAddress;
import java.time.Instant;
import java.time.ZonedDateTime;
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


    @Override
    public Page<SignalMeasurementResponse> getSignalsHistory(Pageable pageable) {
        return testRepository.findAllByRadioCellIsNotEmptyAndNetworkTypeNotIn(pageable, Collections.singletonList(NETWORK_TYPE_WLAN))
                .map(signalMapper::signalToSignalMeasurementResponse);
    }

    @Override
    public SignalSettingsResponse registerSignal(SignalRegisterRequest signalRegisterRequest, HttpServletRequest httpServletRequest, Map<String, String> headers) {
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
                .resultUrl(getResultUrl(httpServletRequest))
                .testUUID(savedTest.getUuid())
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

        updateIpAddress(signalResultRequest, updatedTest);

        processGeoLocation(signalResultRequest, updatedTest);

        processRadioInfo(signalResultRequest, updatedTest);

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

    private void processGeoLocation(SignalResultRequest signalResultRequest, Test updatedTest) {
        if (Objects.nonNull(signalResultRequest.getGeoLocations())) {
            geoLocationService.processGeoLocationRequests(signalResultRequest.getGeoLocations(), updatedTest);
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

    private void processRadioInfo(SignalResultRequest signalResultRequest, Test updatedTest) {
        if (Objects.nonNull(signalResultRequest.getRadioInfo())) {
            if (!CollectionUtils.isEmpty(signalResultRequest.getRadioInfo().getCells())) {
                radioCellService.processRadioCellRequests(signalResultRequest.getRadioInfo().getCells(), updatedTest);
            }
            if (!CollectionUtils.isEmpty(signalResultRequest.getRadioInfo().getSignals())) {
                radioSignalService.saveRadioSignalRequests(signalResultRequest.getRadioInfo(), updatedTest);
            }
        }
    }

    private void updateIpAddress(SignalResultRequest signalResultRequest, Test updatedTest) {
        if (Objects.nonNull(signalResultRequest.getTestIpLocal())) {
            InetAddress ipLocalAddress = InetAddresses.forString(signalResultRequest.getTestIpLocal());
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

    private ZonedDateTime getClientTimeFromSignalResultRequest(SignalResultRequest signalResultRequest) {
        return TimeUtils.getZonedDateTimeFromMillisAndTimezone(Math.round(signalResultRequest.getTimeNanos() / 1e6), signalResultRequest.getTimezone());
    }

    private ZonedDateTime getClientTimeFromSignalRequest(SignalRegisterRequest signalRegisterRequest) {
        return TimeUtils.getZonedDateTimeFromMillisAndTimezone(signalRegisterRequest.getTime(), signalRegisterRequest.getTimezone());
    }

    private String getResultUrl(HttpServletRequest req) {
        return Optional.ofNullable(req.getHeader(URL))
                .map(url -> String.join(URL, SIGNAL_RESULT))
                .orElse(getDefaultResultUrl(req));
    }

    private String getDefaultResultUrl(HttpServletRequest req) {
        return String.format("%s://%s:%s%s", req.getScheme(), req.getServerName(), req.getServerPort(), req.getRequestURI())
                .replace("Request", "Result");
    }
}
