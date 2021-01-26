package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.mapper.NewsMapper;
import at.rtr.rmbt.request.NewsParametersRequest;
import at.rtr.rmbt.repository.NewsRepository;
import at.rtr.rmbt.request.NewsRequest;
import at.rtr.rmbt.response.NewsListResponse;
import at.rtr.rmbt.service.NewsListService;
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
