package com.rtr.nettest.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NewsResponse {

    private final Long uid;

    private final String title;

    private final String text;
}
