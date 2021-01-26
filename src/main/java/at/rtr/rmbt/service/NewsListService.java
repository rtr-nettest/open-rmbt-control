package at.rtr.rmbt.service;

import at.rtr.rmbt.request.NewsParametersRequest;
import at.rtr.rmbt.request.NewsRequest;
import at.rtr.rmbt.response.NewsListResponse;

public interface NewsListService {
    NewsListResponse getAllNews(NewsParametersRequest newsParametersRequest);

    void createNews(NewsRequest newsRequest);
}
