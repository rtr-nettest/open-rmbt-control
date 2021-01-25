package com.rtr.nettest.service.impl;

import com.google.common.net.InetAddresses;
import com.rtr.nettest.exception.ClientNotFoundException;
import com.rtr.nettest.model.Test;
import com.rtr.nettest.model.enums.TestStatus;
import com.rtr.nettest.repository.ClientRepository;
import com.rtr.nettest.repository.RTRProviderRepository;
import com.rtr.nettest.repository.TestRepository;
import com.rtr.nettest.request.SignalRequest;
import com.rtr.nettest.response.SignalResponse;
import com.rtr.nettest.service.SignalService;
import com.rtr.nettest.utils.HelperFunctions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static com.rtr.nettest.constant.HeaderConstants.URL;
import static com.rtr.nettest.constant.URIConstants.SIGNAL_RESULT;

@Service
@RequiredArgsConstructor
public class SignalServiceImpl implements SignalService {

    private final TestRepository testRepository;
    private final RTRProviderRepository providerRepository;
    private final UUIDGenerator uuidGenerator;
    private final ClientRepository clientRepository;

    @Override
    public SignalResponse registerSignal(SignalRequest signalRequest, HttpServletRequest httpServletRequest) {
        var ip = httpServletRequest.getRemoteAddr();

        var uuid = uuidGenerator.generateUUID();
        var openTestUUID = uuidGenerator.generateUUID();

        var client = clientRepository.findByUuid(signalRequest.getUuid())
                .orElseThrow(ClientNotFoundException::new);

        var clientAddress = InetAddresses.forString(ip);
        var clientIpString = InetAddresses.toAddrString(clientAddress);

        var asInformation = HelperFunctions.getASInformationForSignalRequest(clientAddress);

        var test = Test.builder()
                .uuid(uuid)
                .openTestUuid(openTestUUID)
                .clientId(client.getUid())
                .clientPublicIp(clientIpString)
                .clientPublicIpAnonymized(HelperFunctions.anonymizeIp(clientAddress))
                .timezone(signalRequest.getTimezone())
                .clientTime(getClientTime(signalRequest))
                .publicIpAsn(asInformation.getNumber())
                .publicIpAsName(asInformation.getName())
                .countryAsn(asInformation.getCountry())
                .publicIpRdns(HelperFunctions.getReverseDNS(clientAddress))
                .status(TestStatus.STARTED)
                .lastSequenceNumber(-1)
                .build();

        var savedTest = testRepository.save(test);

        return SignalResponse.builder()
                .provider(providerRepository.getProviderNameByTestId(savedTest.getUid()))
                .clientRemoteIp(ip)
                .resultUrl(getResultUrl(httpServletRequest))
                .testUUID(savedTest.getUuid())
                .build();
    }

    private ZonedDateTime getClientTime(SignalRequest signalRequest) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(signalRequest.getTime()),
                ZoneId.of(signalRequest.getTimezone()));
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
