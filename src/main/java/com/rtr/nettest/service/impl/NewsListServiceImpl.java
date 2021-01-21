package com.rtr.nettest.service.impl;

import com.rtr.nettest.mapper.NewsMapper;
import com.rtr.nettest.repository.NewsRepository;
import com.rtr.nettest.request.NewsParametersRequest;
import com.rtr.nettest.request.NewsRequest;
import com.rtr.nettest.response.NewsListResponse;
import com.rtr.nettest.service.NewsListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsListServiceImpl implements NewsListService {

    private final NewsRepository newsRepository;

    private final NewsMapper newsMapper;

    @Override
    public NewsListResponse getAllNews(NewsParametersRequest request) {
        var newsList = newsRepository.findActiveByUidAndPlatformAndSoftwareVersionCodeAndUuid(request.getLastNewsUid(), request.getPlatform(), request.getSoftwareVersionCode(), request.getUuid()).stream()
                .map(news -> newsMapper.newsToNewsResponse(news, request.getLanguage()))
                .collect(Collectors.toList());
        return new NewsListResponse(newsList);
    }

    @Override
    public void createNews(NewsRequest newsRequest) {
        Optional.of(newsRequest)
                .map(newsMapper::newsRequestToNews)
                .ifPresent(newsRepository::save);
    }
}
