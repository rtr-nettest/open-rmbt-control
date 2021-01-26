package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.News;
import at.rtr.rmbt.request.NewsRequest;
import at.rtr.rmbt.response.NewsResponse;

public interface NewsMapper {

    NewsResponse newsToNewsResponse(News news, String language);

    News newsRequestToNews(NewsRequest newsRequest);
}
