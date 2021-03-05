package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.constant.Config;
import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.constant.ErrorMessage;
import at.rtr.rmbt.enums.TestStatus;
import at.rtr.rmbt.exception.ClientNotFoundException;
import at.rtr.rmbt.exception.InvalidSequenceException;
import at.rtr.rmbt.mapper.*;
import at.rtr.rmbt.model.*;
import at.rtr.rmbt.repository.*;
import at.rtr.rmbt.request.GeoLocationRequest;
import at.rtr.rmbt.request.SignalRequest;
import at.rtr.rmbt.request.SignalResultRequest;
import at.rtr.rmbt.response.*;
import at.rtr.rmbt.service.SignalService;
import at.rtr.rmbt.utils.BandCalculationUtil;
import at.rtr.rmbt.utils.FormatUtils;
import at.rtr.rmbt.utils.HelperFunctions;
import at.rtr.rmbt.utils.TimeUtils;
import com.google.common.net.InetAddresses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.net.InetAddress;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
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
    private final RadioCellRepository radioCellRepository;
    private final RadioSignalRepository radioSignalRepository;
    private final GeoLocationRepository geoLocationRepository;
    private final GeoLocationMapper geoLocationMapper;
    private final RadioCellMapper radioCellMapper;
    private final RadioSignalMapper radioSignalMapper;
    private final TestMapper testMapper;

    @Override
    public Page<SignalMeasurementResponse> getSignalsHistory(Pageable pageable) {
        return testRepository.findAllByStatusIn(List.of(TestStatus.SIGNAL_STARTED, TestStatus.SIGNAL), pageable)
                .map(signalMapper::signalToSignalMeasurementResponse);
    }

    @Override
    public SignalSettingsResponse registerSignal(SignalRequest signalRequest, HttpServletRequest httpServletRequest) {
        var ip = httpServletRequest.getRemoteAddr();

        var uuid = uuidGenerator.generateUUID();
        var openTestUUID = uuidGenerator.generateUUID();

        var client = clientRepository.findByUuid(signalRequest.getUuid())
                .orElseThrow(() -> new ClientNotFoundException(String.format(ErrorMessage.CLIENT_NOT_FOUND, signalRequest.getUuid())));

        var clientAddress = InetAddresses.forString(ip);
        var clientIpString = InetAddresses.toAddrString(clientAddress);

        var asInformation = HelperFunctions.getASInformationForSignalRequest(clientAddress);

        var test = Test.builder()
                .uuid(uuid)
                .openTestUuid(openTestUUID)
                .client(client)
                .clientPublicIp(clientIpString)
                .clientPublicIpAnonymized(HelperFunctions.anonymizeIp(clientAddress))
                .timezone(signalRequest.getTimezone())
                .clientTime(getClientTimeFromSignalRequest(signalRequest))
                .time(getClientTimeFromSignalRequest(signalRequest))
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

        List<GeoLocation> newGeoLocation = updateGeoLocation(signalResultRequest, updatedTest);

        testRepository.save(updatedTest);

        geoLocationRepository.saveAll(newGeoLocation);

        processRadioInfo(signalResultRequest, updatedTest);

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
        Set<Long> locationsIds = radioCellUUIDs.values().stream()
                .map(RadioCell::getLocationId)
                .collect(Collectors.toSet());

        Map<Long, GeoLocation> geoLocationIdsMap = geoLocationRepository.findAllById(locationsIds).stream()
                .collect(Collectors.toMap(GeoLocation::getId, Function.identity()));

        return SignalDetailsResponse.builder()
                .signalStrength(radioSignalRepository.findAllByCellUUIDIn(radioCellUUIDs.keySet()).stream()
                        .map(signal -> {
                            var signalStrengthResponseBuilder = SignalStrengthResponse.builder()
                                    .time(TimeUtils.getDiffInSecondsFromTwoZonedDateTime(test.getTime(), signal.getTime()))
                                    .signalStrength(getSignalStrength(signal));
                            setRadioCellInfo(radioCellUUIDs, signal, signalStrengthResponseBuilder);
                            setGeoLocationInformation(radioCellUUIDs, geoLocationIdsMap, signal, signalStrengthResponseBuilder);
                            return signalStrengthResponseBuilder.build();
                        })
                        .collect(Collectors.toList()))
                .testResponse(testMapper.testToTestResponse(test))
                .build();

    }

    private void setRadioCellInfo(Map<UUID, RadioCell> radioCellUUIDs, RadioSignal signal, SignalStrengthResponse.SignalStrengthResponseBuilder builder) {
        Optional.ofNullable(radioCellUUIDs.get(signal.getCellUUID()))
                .ifPresent(radioCell -> builder
                        .technology(radioCell.getTechnology())
                        .ci(radioCell.getAreaCode())
                        .tac(radioCell.getLocationId())
                        .pci(radioCell.getPrimaryScramblingCode())
                        .earfcn(radioCell.getChannelNumber())
                        .band(getFiBand(radioCell))
                        .frequency(getFiFrequency(radioCell))
                );
    }

    private void setGeoLocationInformation(Map<UUID, RadioCell> radioCellUUIDs, Map<Long, GeoLocation> geoLocationIdsMap, RadioSignal signal, SignalStrengthResponse.SignalStrengthResponseBuilder builder) {
        Optional.ofNullable(radioCellUUIDs.get(signal.getCellUUID()))
                .map(RadioCell::getLocationId)
                .map(geoLocationIdsMap::get)
                .ifPresent(geoLocation -> builder
                        .accuracy(FormatUtils.format(Constants.SIGNAL_STRENGTH_ACCURACY_TEMPLATE, geoLocation.getAccuracy()))
                        .speed(FormatUtils.format(Constants.SIGNAL_STRENGTH_SPEED_TEMPLATE, geoLocation.getSpeed()))
                        .altitude(FormatUtils.format(Constants.SIGNAL_STRENGTH_ALTITUDE_TEMPLATE, geoLocation.getAltitude()))
                        .bearing(FormatUtils.format(Constants.SIGNAL_STRENGTH_BEARING_TEMPLATE, geoLocation.getBearing()))
                        .location(geoLocation.getLocation())
                );
    }

    private String getSignalStrength(RadioSignal signal) {
        return Stream.of(FormatUtils.format(Constants.SIGNAL_STRENGTH_DBM_TEMPLATE, signal.getSignalStrength()),
                FormatUtils.format(Constants.SIGNAL_STRENGTH_TIMING_ADVANCE_TEMPLATE, signal.getTimingAdvance()),
                FormatUtils.format(Constants.SIGNAL_STRENGTH_RSQR_TEMPLATE, signal.getLteRSRQ()))
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
            List<RadioCell> radioCells = signalResultRequest.getRadioInfo().getCells().stream()
                    .map(rcq -> {
                        RadioCell radioCell = radioCellMapper.radioCellRequestToRadioCell(rcq);
                        radioCell.setTest(updatedTest);
                        return radioCell;
                    })
                    .collect(Collectors.toList());
            radioCellRepository.saveAll(radioCells);

            List<RadioSignal> radioSignals = signalResultRequest.getRadioInfo().getSignals().stream()
                    .map(rsr -> {
                        RadioSignal radioSignal = radioSignalMapper.radioSignalRequestToRadioSignal(rsr);
                        radioSignal.setOpenTestUUID(updatedTest.getOpenTestUuid());
                        return radioSignal;
                    })
                    .collect(Collectors.toList());
            radioSignalRepository.saveAll(radioSignals);
        }
    }

    private List<GeoLocation> updateGeoLocation(SignalResultRequest signalResultRequest, Test updatedTest) {
        List<GeoLocation> actualGeoLocation = new ArrayList<>();
        if (Objects.nonNull(signalResultRequest.getGeoLocations())) {
            Double minAccuracy = Double.MAX_VALUE;
            GeoLocation firstAccuratePosition = null;

            for (GeoLocationRequest geoDataItem : signalResultRequest.getGeoLocations()) {
                if (Objects.nonNull(geoDataItem.getTstamp()) && Objects.nonNull(geoDataItem.getGeoLat()) && Objects.nonNull(geoDataItem.getGeoLong())) {
                    GeoLocation geoLoc = geoLocationMapper.geoLocationRequestToGeoLocation(geoDataItem);
                    geoLoc.setOpenTestUUID(updatedTest.getOpenTestUuid());
                    geoLoc.setTime(getClientTimeFromMillisAndTimezone(geoDataItem.getTstamp(), updatedTest.getTimezone()));
                    geoLoc.setLocation(new GeometryFactory(new PrecisionModel(), Constants.SRID)
                            .createPoint(new Coordinate(geoLoc.getGeoLong(), geoLoc.getGeoLat())));
//                    geoloc.testId(test.getUid()); there are foreign  two key in table geo_location test_id and open_test_uuid

                    // ignore all timestamps older than 20s
                    if (geoDataItem.getTimeNs() > -20000000000L) {
                        if (geoDataItem.getAccuracy() < minAccuracy) {
                            minAccuracy = geoDataItem.getAccuracy();
                            firstAccuratePosition = geoLoc;
                        }
                        actualGeoLocation.add(geoLoc);
                    }
                }
            }

            if (Objects.nonNull(firstAccuratePosition)) {
                updateTestGeo(updatedTest, firstAccuratePosition);
            }
        }
        return actualGeoLocation;
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

    private void updateTestGeo(Test updatedTest, GeoLocation firstAccuratePosition) {
        updatedTest.setGeoLocationUuid(firstAccuratePosition.getGeoLocationUUID());
        updatedTest.setGeoAccuracy(firstAccuratePosition.getAccuracy());
        updatedTest.setLongitude(firstAccuratePosition.getGeoLong());
        updatedTest.setLatitude(firstAccuratePosition.getGeoLat());
        updatedTest.setGeoProvider(firstAccuratePosition.getProvider());
    }

    private Test getEmptyGeneratedTest(SignalResultRequest signalResultRequest, RtrClient client) {
        return Test.builder()
                .uuid(uuidGenerator.generateUUID())
                .openTestUuid(uuidGenerator.generateUUID())
                .time(getClientTimeFromSignalResultRequest(signalResultRequest))
                .timezone(signalResultRequest.getTimezone())
                .client(client)
                .useSsl(false)
                .lastSequenceNumber(-1)
                .build();
    }

    private ZonedDateTime getClientTimeFromSignalResultRequest(SignalResultRequest signalResultRequest) {
        return getClientTimeFromMillisAndTimezone(Math.round(signalResultRequest.getTimeNanos() / 1e6), signalResultRequest.getTimezone());
    }

    private ZonedDateTime getClientTimeFromSignalRequest(SignalRequest signalRequest) {
        return getClientTimeFromMillisAndTimezone(signalRequest.getTime(), signalRequest.getTimezone());
    }

    private ZonedDateTime getClientTimeFromMillisAndTimezone(Long millies, String timezone) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(millies), ZoneId.of(timezone));
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
