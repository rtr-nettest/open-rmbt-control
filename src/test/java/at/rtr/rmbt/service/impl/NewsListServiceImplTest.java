package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.exception.NotFoundException;
import at.rtr.rmbt.mapper.NewsMapper;
import at.rtr.rmbt.model.NewsView;
import at.rtr.rmbt.repository.NewsRepository;
import at.rtr.rmbt.repository.NewsViewRepository;
import at.rtr.rmbt.request.NewsParametersRequest;
import at.rtr.rmbt.response.NewsListItemResponse;
import at.rtr.rmbt.response.NewsResponse;
import at.rtr.rmbt.service.NewsListService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static at.rtr.rmbt.TestConstants.DEFAULT_UID;
import static at.rtr.rmbt.TestFixtures.*;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class NewsListServiceImplTest {
    private NewsListService newsListService;

    @MockBean
    private NewsRepository newsRepository;
    @MockBean
    private NewsViewRepository newsViewRepository;
    @MockBean
    private NewsMapper newsMapper;

    @Mock
    private NewsParametersRequest newsParametersRequest;
    @Mock
    private NewsResponse newsResponse;

    @Before
    public void setUp() {
        newsListService = new NewsListServiceImpl(newsRepository, newsViewRepository, newsMapper);
    }

    @Test
    public void getAllNews_whenExistOneNews_expectOneNewsResponse() {
        when(newsParametersRequest.getLastNewsUid()).thenReturn(DEFAULT_UID);
        when(newsParametersRequest.getLanguage()).thenReturn(TestConstants.LANGUAGE_EN);
        when(newsParametersRequest.getPlatform()).thenReturn(TestConstants.DEFAULT_PLATFORM);
        when(newsParametersRequest.getSoftwareVersionCode()).thenReturn(TestConstants.DEFAULT_SOFTWARE_VERSION_CODE);
        when(newsParametersRequest.getUuid()).thenReturn(TestConstants.DEFAULT_NEWS_UUID.toString());
        when(newsRepository.findActiveByUidAndPlatformAndSoftwareVersionCodeAndUuid(DEFAULT_UID, TestConstants.DEFAULT_PLATFORM, TestConstants.DEFAULT_SOFTWARE_VERSION_CODE, TestConstants.DEFAULT_NEWS_UUID.toString()))
                .thenReturn(List.of(newsEn));
        when(newsMapper.newsToNewsResponse(newsEn, TestConstants.LANGUAGE_EN)).thenReturn(newsResponse);

        var expectedList = List.of(newsResponse);
        var actualList = newsListService.getAllNews(newsParametersRequest);

        assertEquals(expectedList, actualList.getNews());
    }

    @Test
    public void createNews_whenCommonData_expectNewsSaved() {
        when(newsMapper.newsRequestToNews(newsRequestEn)).thenReturn(newsEn);

        newsListService.createNews(newsRequestEn);

        verify(newsRepository).save(newsEn);
    }

    @Test
    public void updateNews_whenCommonData_expectNewsUpdated() {
        when(newsRepository.findById(DEFAULT_UID)).thenReturn(Optional.of(newsEn));
        when(newsMapper.updateNewsByNewsRequest(newsEn, newsRequestDe)).thenReturn(newsDe);

        newsListService.updateNews(DEFAULT_UID, newsRequestDe);

        verify(newsRepository).save(newsDe);
    }

    @Test
    public void updateNews_whenNewsNotFound_expectNotFoundException() {
        when(newsRepository.findById(DEFAULT_UID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> newsListService.updateNews(DEFAULT_UID, newsRequestDe));
        verify(newsRepository, never()).save(any());
    }

    @Test
    public void listNews_whenCommonData_expectNewsPage() {
        PageRequest request = PageRequest.of(0, 10);
        Page<NewsView> page = new PageImpl<>((IntStream.range(0, 10)
            .mapToObj(i -> newsViewEn)
            .collect(Collectors.toList())));

        when(newsMapper.newsViewToNewsListItem(newsViewEn)).thenReturn(newsListItemEn);
        when(newsViewRepository.findAll(request)).thenReturn(page);

        Page<NewsListItemResponse> result = newsListService.listNews(request);

        assertEquals(10, result.getSize());
        result.forEach(n -> assertEquals(newsListItemEn, n));
    }
}