package at.rtr.rmbt.service;

import at.rtr.rmbt.model.News;
import at.rtr.rmbt.request.NewsParametersRequest;
import at.rtr.rmbt.request.NewsRequest;
import at.rtr.rmbt.response.NewsListItemResponse;
import at.rtr.rmbt.response.NewsListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewsListService {
    NewsListResponse getAllNews(NewsParametersRequest newsParametersRequest);

    void createNews(NewsRequest newsRequest);

    News updateNews(Long uid, NewsRequest newsRequest);

    Page<NewsListItemResponse> listNews(Pageable pageable);

    NewsListItemResponse getNews(Long newsId);
}
