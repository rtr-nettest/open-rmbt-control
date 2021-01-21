package com.rtr.nettest.service.impl;

import com.rtr.nettest.mapper.NewsMapper;
import com.rtr.nettest.model.News;
import com.rtr.nettest.repository.NewsRepository;
import com.rtr.nettest.request.NewsParametersRequest;
import com.rtr.nettest.request.NewsRequest;
import com.rtr.nettest.response.NewsResponse;
import com.rtr.nettest.service.NewsListService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.rtr.nettest.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class NewsListServiceImplTest {
    private NewsListService newsListService;

    @MockBean
    private NewsRepository newsRepository;
    @MockBean
    private NewsMapper newsMapper;

    @Mock
    private NewsParametersRequest newsParametersRequest;
    @Mock
    private News news;
    @Mock
    private NewsResponse newsResponse;
    @Mock
    private NewsRequest newsRequest;

    @Before
    public void setUp() {
        newsListService = new NewsListServiceImpl(newsRepository, newsMapper);
    }

    @Test
    public void getAllNews_whenExistOneNews_expectOneNewsResponse() {
        when(newsParametersRequest.getLastNewsUid()).thenReturn(DEFAULT_LAST_NEWS_UID);
        when(newsParametersRequest.getLanguage()).thenReturn(DEFAULT_LANGUAGE);
        when(newsParametersRequest.getPlatform()).thenReturn(DEFAULT_PLATFORM);
        when(newsParametersRequest.getSoftwareVersionCode()).thenReturn(DEFAULT_SOFTWARE_VERSION_CODE);
        when(newsParametersRequest.getUuid()).thenReturn(DEFAULT_NEWS_UUID.toString());
        when(newsRepository.findActiveByUidAndPlatformAndSoftwareVersionCodeAndUuid(DEFAULT_LAST_NEWS_UID, DEFAULT_PLATFORM, DEFAULT_SOFTWARE_VERSION_CODE, DEFAULT_NEWS_UUID.toString()))
                .thenReturn(List.of(news));
        when(newsMapper.newsToNewsResponse(news, DEFAULT_LANGUAGE)).thenReturn(newsResponse);

        var expectedList = List.of(newsResponse);
        var actualList = newsListService.getAllNews(newsParametersRequest);

        assertEquals(expectedList, actualList.getNews());
    }

    @Test
    public void createNews_whenCommonData_expectNewsSaved() {
        when(newsMapper.newsRequestToNews(newsRequest)).thenReturn(news);

        newsListService.createNews(newsRequest);

        verify(newsRepository).save(news);
    }
}