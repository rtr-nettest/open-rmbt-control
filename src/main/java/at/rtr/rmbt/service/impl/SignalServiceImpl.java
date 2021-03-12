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
import at.rtr.rmbt.model.GeoLocation;
import at.rtr.rmbt.model.RadioCell;
import at.rtr.rmbt.model.RadioSignal;
import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.model.Signal;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.repository.ClientRepository;
import at.rtr.rmbt.repository.GeoLocationRepository;
import at.rtr.rmbt.repository.ProviderRepository;
import at.rtr.rmbt.repository.RadioSignalRepository;
import at.rtr.rmbt.repository.SignalRepository;
import at.rtr.rmbt.repository.TestRepository;
import at.rtr.rmbt.request.SignalRegisterRequest;
import at.rtr.rmbt.request.SignalRequest;
import at.rtr.rmbt.request.SignalResultRequest;
import at.rtr.rmbt.response.SignalDetailsResponse;
import at.rtr.rmbt.response.SignalLocationResponse;
import at.rtr.rmbt.response.SignalMeasurementResponse;
import at.rtr.rmbt.response.SignalResultResponse;
import at.rtr.rmbt.response.SignalSettingsResponse;
import at.rtr.rmbt.response.SignalStrengthResponse;
import at.rtr.rmbt.service.GeoLocationService;
import at.rtr.rmbt.service.RadioCellService;
import at.rtr.rmbt.service.RadioSignalService;
import at.rtr.rmbt.service.SignalService;
import at.rtr.rmbt.utils.BandCalculationUtil;
import at.rtr.rmbt.utils.FormatUtils;
import at.rtr.rmbt.utils.HelperFunctions;
import at.rtr.rmbt.utils.TimeUtils;
import com.google.common.net.InetAddresses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.net.InetAddress;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static at.rtr.rmbt.constant.HeaderConstants.URL;
import static at.rtr.rmbt.constant.URIConstants.SIGNAL_RESULT;

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
        return testRepository.findAllByStatusIn(List.of(TestStatus.SIGNAL_STARTED, TestStatus.SIGNAL), pageable)
                .map(signalMapper::signalToSignalMeasurementResponse);
    }

    @Override
    public SignalSettingsResponse registerSignal(SignalRegisterRequest signalRegisterRequest, HttpServletRequest httpServletRequest) {
        var ip = httpServletRequest.getRemoteAddr();

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
                .build();

        var savedTest = testRepository.save(test);

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
        UUID testUuid = getTestUUID(signalResultRequest);

        Long sequenceNumber = signalResultRequest.getSequenceNumber();

        RtrClient client = clientRepository.findByUuid(signalResultRequest.getClientUUID())
                .orElseThrow(() -> new ClientNotFoundException(String.format(ErrorMessage.CLIENT_NOT_FOUND, signalResultRequest.getClientUUID())));

        Test updatedTest = testRepository.findByUuidAndStatusesIn(testUuid, Config.SIGNAL_RESULT_STATUSES)
                .orElseGet(() -> getEmptyGeneratedTest(signalResultRequest, client));
        updatedTest.setStatus(TestStatus.SIGNAL);

        if (sequenceNumber <= updatedTest.getLastSequenceNumber()) {
            throw new InvalidSequenceException();
        }
        updatedTest.setLastSequenceNumber(sequenceNumber.intValue());

        updateIpAddress(signalResultRequest, updatedTest);

        processGeoLocation(signalResultRequest, updatedTest);

        processRadioInfo(signalResultRequest, updatedTest);

        testRepository.save(updatedTest);

        return SignalResultResponse.builder()
                .testUUID(updatedTest.getUuid())
                .build();
    }

    @Override
    public SignalDetailsResponse getSignalStrength(UUID testUUID) {
        Test test = testRepository.findByUuidAndStatusesIn(testUUID, Config.SIGNAL_RESULT_STATUSES)
                .orElseThrow();
        Map<UUID, RadioCell> radioCellUUIDs = test.getRadioCell().stream()
                .collect(Collectors.toMap(RadioCell::getUuid, Function.identity()));

        List<GeoLocation> geoLocations = geoLocationRepository.findAllByTestOrderByTimeAsc(test);

        return SignalDetailsResponse.builder()
                .signalStrength(radioSignalRepository.findAllByCellUUIDInOrderByTimeAsc(radioCellUUIDs.keySet()).stream()
                        .map(signal -> {
                            var signalStrengthResponseBuilder = SignalStrengthResponse.builder()
                                    .time(TimeUtils.getDiffInSecondsFromTwoZonedDateTime(test.getTime(), signal.getTime()))
                                    .signalStrength(getSignalStrength(signal));
                            setRadioCellInfo(radioCellUUIDs, signal, signalStrengthResponseBuilder);

                            return signalStrengthResponseBuilder.build();
                        })
                        .collect(Collectors.toList()))
                .testResponse(testMapper.testToTestResponse(test))
                .signalLocation(toLocationResponse(geoLocations, test))
                .build();

    }

    private void setRadioCellInfo(Map<UUID, RadioCell> radioCellUUIDs, RadioSignal signal, SignalStrengthResponse.SignalStrengthResponseBuilder builder) {
        Optional.ofNullable(radioCellUUIDs.get(signal.getCellUUID()))
                .ifPresent(radioCell -> builder
                        .technology(radioCell.getTechnology().getLabelEn())
                        .ci(radioCell.getAreaCode())
                        .tac(radioCell.getLocationId())
                        .pci(radioCell.getPrimaryScramblingCode())
                        .earfcn(radioCell.getChannelNumber())
                        .band(getFiBand(radioCell))
                        .frequency(getFiFrequency(radioCell))
                );
    }

    private List<SignalLocationResponse> toLocationResponse(List<GeoLocation> geoLocations, Test test) {
        return geoLocations.stream()
                .map(geoLocation -> SignalLocationResponse.builder()
                        .accuracy(FormatUtils.format(Constants.SIGNAL_STRENGTH_ACCURACY_TEMPLATE, geoLocation.getAccuracy()))
                        .speed(FormatUtils.format(Constants.SIGNAL_STRENGTH_SPEED_TEMPLATE, geoLocation.getSpeed()))
                        .altitude(FormatUtils.format(Constants.SIGNAL_STRENGTH_ALTITUDE_TEMPLATE, geoLocation.getAltitude()))
                        .bearing(FormatUtils.format(Constants.SIGNAL_STRENGTH_BEARING_TEMPLATE, geoLocation.getBearing()))
                        .location(geoLocation.getLocation())
                        .time(TimeUtils.getDiffInSecondsFromTwoZonedDateTime(test.getTime(), geoLocation.getTime()))
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
                if (newSignal.getWifiRSSI() < minSignalStrength) {
                    minSignalStrength = newSignal.getWifiRSSI();
                }
            } else if (newSignal.getSignalStrength() < minSignalStrength) {
                minSignalStrength = newSignal.getSignalStrength();
            }

            if (newSignal.getLteRSRP() < minLteRsrp) {
                minLteRsrp = newSignal.getLteRSRP();
            }

            if (newSignal.getLteRSRQ() < minLteRsrq && !(Math.abs(newSignal.getLteRSRQ()) > 19.5 || Math.abs(newSignal.getLteRSRQ()) < 3.0)) {
                minLteRsrq = newSignal.getLteRSRQ();
            }

            if (newSignal.getWifiLinkSpeed() < minLinkSpeed) {
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
        return Optional.ofNullable(BandCalculationUtil.getFrequencyInformationFromRadioCell(radioCell))
                .map(BandCalculationUtil.FrequencyInformation::getBand)
                .orElse(null);
    }

    private Double getFiFrequency(RadioCell radioCell) {
        return Optional.ofNullable(BandCalculationUtil.getFrequencyInformationFromRadioCell(radioCell))
                .map(BandCalculationUtil.FrequencyInformation::getFrequencyDL)
                .orElse(null);
    }

    private void processRadioInfo(SignalResultRequest signalResultRequest, Test updatedTest) {
        if (Objects.nonNull(signalResultRequest.getRadioInfo())) {
            radioCellService.processRadioCellRequests(signalResultRequest.getRadioInfo().getCells(), updatedTest);
            radioSignalService.saveRadioSignalRequests(signalResultRequest.getRadioInfo().getSignals(), updatedTest);
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

        return testRepository.save(newTest);
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
