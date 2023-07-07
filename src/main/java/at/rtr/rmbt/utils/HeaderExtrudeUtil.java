package at.rtr.rmbt.utils;

import is.tagomor.woothee.Classifier;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static at.rtr.rmbt.constant.HeaderConstants.*;

@UtilityClass
public class HeaderExtrudeUtil {

    public String getIpFromNgNixHeader(HttpServletRequest request, Map<String, String> headers) {
        return Optional.ofNullable(headers).orElse(Collections.emptyMap()).entrySet().stream()
                .filter(x -> StringUtils.equalsAnyIgnoreCase(x.getKey(), IP))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(request.getLocalAddr());
    }

    public Map<String, String> getHeadersWithoutIpAndUrl(Map<String, String> headers) {
        return headers.entrySet().stream()
                .filter(x -> !StringUtils.equalsAnyIgnoreCase(x.getKey(), IP, URL, HOST))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public String getUserAgent(HttpServletRequest request, Map<String, String> headers) {
        return headers.entrySet().stream()
                .filter(x -> StringUtils.equalsIgnoreCase(x.getKey(), USER_AGENT))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(request.getRemoteAddr());
    }

    public List<String> getLanguagesFromRequest(HttpServletRequest request) {
        return Collections.list(request.getLocales()).stream()
                .map(Locale::toString)
                .collect(Collectors.toList());
    }

    public Map<String, String> getUserAgentFromHeader(HttpServletRequest request) {
        return Classifier.parse(request.getHeader(USER_AGENT));
    }

    public String getUrlFromNgNixHeader(HttpServletRequest request, Map<String, String> headers) {
        return headers.entrySet().stream()
                .filter(x -> StringUtils.equalsAnyIgnoreCase(x.getKey(), URL))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(request.getRequestURL().toString());
    }
}
