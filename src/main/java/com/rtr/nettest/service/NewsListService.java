package com.rtr.nettest.service;

import com.rtr.nettest.request.NewsParametersRequest;
import com.rtr.nettest.request.NewsRequest;
import com.rtr.nettest.response.NewsListResponse;

public interface NewsListService {
    NewsListResponse getAllNews(NewsParametersRequest newsParametersRequest);

    void createNews(NewsRequest newsRequest);
}
