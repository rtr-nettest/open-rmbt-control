package at.rtr.rmbt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * News list response class.
 */
@Getter
@RequiredArgsConstructor
public class NewsListResponse {

    @JsonProperty(value = "news")
    private final List<NewsResponse> news;
}
