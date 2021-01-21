package com.rtr.nettest.mapper;

import com.rtr.nettest.model.News;
import com.rtr.nettest.response.NewsResponse;

public interface NewsMapper {

    NewsResponse newsToNewsResponse(News news, String language);
}
