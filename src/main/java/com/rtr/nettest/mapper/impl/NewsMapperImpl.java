package com.rtr.nettest.mapper.impl;

import com.rtr.nettest.model.News;
import com.rtr.nettest.mapper.NewsMapper;
import com.rtr.nettest.response.NewsResponse;
import org.springframework.stereotype.Service;

@Service
public class NewsMapperImpl implements NewsMapper {

    @Override
    public NewsResponse newsToNewsResponse(News news, String language) {
        var response = NewsResponse.builder()
                .uid(news.getId());
        if (language.equals("de")) {
            response.title(news.getTitleDe());
            response.text(news.getTextDe());
        } else {
            response.title(news.getTitleEn());
            response.text(news.getTextEn());
        }
        return response.build();
    }
}
