package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.News;
import at.rtr.rmbt.model.NewsView;
import at.rtr.rmbt.request.NewsRequest;
import at.rtr.rmbt.response.NewsListItemResponse;
import at.rtr.rmbt.response.NewsResponse;

/**
 * News mapper interface.
 */
public interface NewsMapper {

    /**
     * News to news response.
     *
     * @param news the News
     * @return the result
     */
    NewsResponse newsToNewsResponse(News news);

    /**
     * News request to news.
     *
     * @param newsRequest the News request
     * @return the result
     */
    News newsRequestToNews(NewsRequest newsRequest);

    /**
     * Update news by news request.
     *
     * @param news the News
     * @param newsRequest the News request
     * @return the result
     */
    News updateNewsByNewsRequest(News news, NewsRequest newsRequest);

    /**
     * News view to news list item.
     *
     * @param newsView the News view
     * @return the result
     */
    NewsListItemResponse newsViewToNewsListItem(NewsView newsView);
}
