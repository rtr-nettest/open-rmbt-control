package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.response.DataCollectorResponse;
import at.rtr.rmbt.response.IpResponse;
import at.rtr.rmbt.service.RequestDataCollectorService;
import at.rtr.rmbt.utils.HeaderExtrudeUtil;
import com.google.common.net.InetAddresses;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Map;

@Service
public class RequestDataCollectorServiceImpl implements RequestDataCollectorService {

    @Override
    public DataCollectorResponse getDataCollectorResponse(HttpServletRequest request, Map<String, String> headers) {
        Map<String, String> userAgent = HeaderExtrudeUtil.getUserAgentFromHeader(request);
        String ip = HeaderExtrudeUtil.getIpFromNgNixHeader(request, headers);

        return DataCollectorResponse.builder()
                .ip(ip)
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
    public IpResponse getIpVersion(HttpServletRequest request, Map<String, String> headers) {
        String clientIpRaw = HeaderExtrudeUtil.getIpFromNgNixHeader(request, headers);
        InetAddress clientAddress = InetAddresses.forString(clientIpRaw);
        return IpResponse.builder()
                .ip(clientIpRaw)
                .version(getVersion(clientAddress))
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
