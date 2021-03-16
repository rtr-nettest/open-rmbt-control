package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.response.DataCollectorResponse;
import at.rtr.rmbt.service.RequestDataCollectorService;
import at.rtr.rmbt.utils.HeaderExtrudeUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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
}
