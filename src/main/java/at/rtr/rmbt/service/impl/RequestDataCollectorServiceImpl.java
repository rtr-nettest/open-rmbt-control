package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.enums.TestStatus;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.IpRequest;
import at.rtr.rmbt.response.DataCollectorResponse;
import at.rtr.rmbt.response.IpResponse;
import at.rtr.rmbt.service.RequestDataCollectorService;
import at.rtr.rmbt.utils.GeoIpHelper;
import at.rtr.rmbt.utils.HeaderExtrudeUtil;
import at.rtr.rmbt.utils.HelperFunctions;
import at.rtr.rmbt.repository.TestRepository;
import com.google.common.net.InetAddresses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestDataCollectorServiceImpl implements RequestDataCollectorService {

    private final TestRepository testRepository;

    @Override
    public DataCollectorResponse getDataCollectorResponse(HttpServletRequest request, Map<String, String> headers) {
        Map<String, String> userAgent = HeaderExtrudeUtil.getUserAgentFromHeader(request);
        String ip = HeaderExtrudeUtil.getIpFromNgNixHeader(request, headers);

        return DataCollectorResponse.builder()
                .ip(ip)
                .countryGeoIp(GeoIpHelper.lookupCountry(InetAddresses.forString(ip)))
                .port(request.getRemotePort())
                .product(userAgent.get("name").replace("Internet Explorer", "IE"))
                .version(userAgent.get("version"))
                .category((userAgent.get("category")))
                .os(userAgent.get("os"))
                .agent(HeaderExtrudeUtil.getUserAgent(request, headers))
                .url(HeaderExtrudeUtil.getUrlFromNgNixHeader(request, headers))
                .headers(HeaderExtrudeUtil.getHeadersWithoutIpAndUrl(headers))
                .languages(HeaderExtrudeUtil.getLanguagesFromRequest(request))
                .build();
    }

    @Override
    public IpResponse getIpVersion(@RequestBody IpRequest ipRequest, HttpServletRequest httpServletRequest, @RequestHeader Map<String, String> headers) {
        String clientIpRaw = HeaderExtrudeUtil.getIpFromNgNixHeader(httpServletRequest, headers);
        InetAddress clientAddress = InetAddresses.forString(clientIpRaw);
        String ipVersion = getVersion(clientAddress);
        String natType;
        InetAddress localAddress = null;

        // if clientIpLocal is provided, the address must be valid, else throw exception
        if (ipRequest != null && StringUtils.isNotBlank(ipRequest.getClientIpLocal())) {

            try {
                localAddress = InetAddresses.forString(ipRequest.getClientIpLocal());

            } catch (Exception e) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "client_ip_local must be valid IP address"
                );
            }
        }
        natType = HelperFunctions.getNatType(localAddress, clientAddress);
        if (ipRequest != null && StringUtils.isNotBlank(ipRequest.getTestUUID())) {
            UUID requestUUID = UUID.fromString(ipRequest.getTestUUID());
            Test test = testRepository.findByUuidOrOpenTestUuid(requestUUID)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid test_uuid"));

            //verify test status
            if (test.getStatus() != TestStatus.STARTED) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Test has invalid status"
                );
            }
            if (Objects.equals(ipVersion, Constants.INET_4_IP_VERSION)) {
                test.setNatTypeV4(natType);
            } else {
                test.setNatTypeV6(natType);
            }
            testRepository.save(test);
        }


        return IpResponse.builder()
                .ip(clientIpRaw)
                .version(ipVersion)
                .natType(natType)
                .build();
    }

    private String getVersion(InetAddress clientAddress) {
        if (clientAddress instanceof Inet4Address) {
            return Constants.INET_4_IP_VERSION;
        } else {
            return Constants.INET_6_IP_VERSION;
        }
    }
}
