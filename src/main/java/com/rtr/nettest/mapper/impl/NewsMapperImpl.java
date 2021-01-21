package com.rtr.nettest.mapper.impl;

import com.rtr.nettest.mapper.NewsMapper;
import com.rtr.nettest.model.News;
import com.rtr.nettest.request.NewsRequest;
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

    @Override
    public News newsRequestToNews(NewsRequest newsRequest) {
        var news = News.builder()
                .minSoftwareVersionCode(newsRequest.getMinSoftwareVersion())
                .maxSoftwareVersionCode(newsRequest.getMaxSoftwareVersion())
                .active(newsRequest.isActive())
                .force(newsRequest.isForce())
                .platform(newsRequest.getPlatform())
                .uuid(newsRequest.getUuid());
        if (newsRequest.getLanguage().equals("de")) {
            news.titleDe(newsRequest.getTitle());
            news.textDe(newsRequest.getText());
        } else {
            news.titleEn(newsRequest.getTitle());
            news.textEn(newsRequest.getText());
        }
        return news.build();
    }
}
