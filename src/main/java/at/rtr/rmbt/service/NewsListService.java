package at.rtr.rmbt.service;

import at.rtr.rmbt.model.News;
import at.rtr.rmbt.request.NewsParametersRequest;
import at.rtr.rmbt.request.NewsRequest;
import at.rtr.rmbt.response.NewsListItemResponse;
import at.rtr.rmbt.response.NewsListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * News list service interface.
 */
public interface NewsListService {
    NewsListResponse getAllNews(NewsParametersRequest newsParametersRequest);

    /**
     * Create news.
     *
     * @param newsRequest the News request
     */
    void createNews(NewsRequest newsRequest);

    /**
     * Update news.
     *
     * @param uid the Uid
     * @param newsRequest the News request
     * @return the result
     */
    News updateNews(Long uid, NewsRequest newsRequest);

    /**
     * List news.
     *
     * @param pageable the Pageable
     * @return the result
     */
    Page<NewsListItemResponse> listNews(Pageable pageable);

    NewsListItemResponse getNews(Long newsId);
}
