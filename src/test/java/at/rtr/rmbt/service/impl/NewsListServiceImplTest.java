package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.NewsMapper;
import at.rtr.rmbt.model.News;
import at.rtr.rmbt.request.NewsParametersRequest;
import at.rtr.rmbt.repository.NewsRepository;
import at.rtr.rmbt.request.NewsRequest;
import at.rtr.rmbt.response.NewsResponse;
import at.rtr.rmbt.service.NewsListService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

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
        when(newsParametersRequest.getLastNewsUid()).thenReturn(TestConstants.DEFAULT_LAST_NEWS_UID);
        when(newsParametersRequest.getLanguage()).thenReturn(TestConstants.DEFAULT_LANGUAGE);
        when(newsParametersRequest.getPlatform()).thenReturn(TestConstants.DEFAULT_PLATFORM);
        when(newsParametersRequest.getSoftwareVersionCode()).thenReturn(TestConstants.DEFAULT_SOFTWARE_VERSION_CODE);
        when(newsParametersRequest.getUuid()).thenReturn(TestConstants.DEFAULT_NEWS_UUID.toString());
        when(newsRepository.findActiveByUidAndPlatformAndSoftwareVersionCodeAndUuid(TestConstants.DEFAULT_LAST_NEWS_UID, TestConstants.DEFAULT_PLATFORM, TestConstants.DEFAULT_SOFTWARE_VERSION_CODE, TestConstants.DEFAULT_NEWS_UUID.toString()))
                .thenReturn(List.of(news));
        when(newsMapper.newsToNewsResponse(news, TestConstants.DEFAULT_LANGUAGE)).thenReturn(newsResponse);

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