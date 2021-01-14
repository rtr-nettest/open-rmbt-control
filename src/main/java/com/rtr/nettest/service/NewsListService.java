package com.rtr.nettest.service;

import com.rtr.nettest.request.NewsParametersRequest;
import com.rtr.nettest.response.NewsListResponse;
import com.rtr.nettest.response.NewsResponse;

import java.util.List;

public interface NewsListService {
    NewsListResponse getAllNews(NewsParametersRequest newsParametersRequest);
}
