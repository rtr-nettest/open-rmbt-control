package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.exception.NotFoundException;
import at.rtr.rmbt.mapper.NewsMapper;
import at.rtr.rmbt.model.News;
import at.rtr.rmbt.repository.NewsRepository;
import at.rtr.rmbt.repository.NewsViewRepository;
import at.rtr.rmbt.request.NewsParametersRequest;
import at.rtr.rmbt.request.NewsRequest;
import at.rtr.rmbt.response.NewsListItemResponse;
import at.rtr.rmbt.response.NewsListResponse;
import at.rtr.rmbt.service.NewsListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * News list service impl class.
 */
@Service
@RequiredArgsConstructor
public class NewsListServiceImpl implements NewsListService {

    private final NewsRepository newsRepository;
    private final NewsViewRepository newsViewRepository;

    private final NewsMapper newsMapper;

    @Override
    public NewsListResponse getAllNews(NewsParametersRequest request) {
        var newsList = newsRepository.findActiveByUidAndPlatformAndSoftwareVersionCodeAndUuid(request.getLastNewsUid(), request.getPlatform(), request.getSoftwareVersionCode(), request.getUuid()).stream()
            .map(newsMapper::newsToNewsResponse)
            .collect(Collectors.toList());
        return new NewsListResponse(newsList);
    }

    /**
     * Create news.
     *
     * @param newsRequest the News request
     */
    @Override
    public void createNews(NewsRequest newsRequest) {
        newsRepository.save(newsMapper.newsRequestToNews(newsRequest));
    }

    /**
     * Update news.
     *
     * @param uid the Uid
     * @param newsRequest the News request
     * @return the result
     */
    @Override
    public News updateNews(Long uid, NewsRequest newsRequest) {
        News news = newsRepository.findById(uid).orElseThrow(NotFoundException::new);
        return newsRepository.save(newsMapper.updateNewsByNewsRequest(news, newsRequest));
    }

    /**
     * List news.
     *
     * @param pageable the Pageable
     * @return the result
     */
    @Override
    public Page<NewsListItemResponse> listNews(Pageable pageable) {
        return newsViewRepository.findAll(pageable)
            .map(newsMapper::newsViewToNewsListItem);
    }

    @Override
    public NewsListItemResponse getNews(Long newsId) {
        return newsViewRepository.findById(newsId)
            .map(newsMapper::newsViewToNewsListItem)
            .orElseThrow(NotFoundException::new);
    }
}
