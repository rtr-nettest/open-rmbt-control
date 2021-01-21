package com.rtr.nettest.mapper.impl;

import com.rtr.nettest.mapper.NewsMapper;
import com.rtr.nettest.model.News;
import com.rtr.nettest.request.NewsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static com.rtr.nettest.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class NewsMapperImplTest {
    private NewsMapper newsMapper;

    @Mock
    private News news;
    @Mock
    private NewsRequest newsRequest;

    @Before
    public void setUp() {
        newsMapper = new NewsMapperImpl();
    }

    @Test
    public void newsToNewsResponse_whenDeLanguage_expectDeNewsResponse() {
        when(news.getId()).thenReturn(DEFAULT_UID);
        when(news.getTitleDe()).thenReturn(DEFAULT_NEWS_TITLE);
        when(news.getTextDe()).thenReturn(DEFAULT_NEWS_TEXT);

        var actualNewsResponse = newsMapper.newsToNewsResponse(news, "de");

        assertEquals(DEFAULT_UID, actualNewsResponse.getUid());
        assertEquals(DEFAULT_NEWS_TITLE, actualNewsResponse.getTitle());
        assertEquals(DEFAULT_NEWS_TEXT, actualNewsResponse.getText());
    }

    @Test
    public void newsToNewsResponse_whenNotDeLanguage_expectEnNewsResponse() {
        when(news.getId()).thenReturn(DEFAULT_UID);
        when(news.getTitleEn()).thenReturn(DEFAULT_NEWS_TITLE);
        when(news.getTextEn()).thenReturn(DEFAULT_NEWS_TEXT);

        var actualNewsResponse = newsMapper.newsToNewsResponse(news, "en");

        assertEquals(DEFAULT_UID, actualNewsResponse.getUid());
        assertEquals(DEFAULT_NEWS_TITLE, actualNewsResponse.getTitle());
        assertEquals(DEFAULT_NEWS_TEXT, actualNewsResponse.getText());
    }

    @Test
    public void newsRequestToNews_whenDeLanguage_expectNews() {
        when(newsRequest.getTitle()).thenReturn(DEFAULT_NEWS_TITLE_DE);
        when(newsRequest.getText()).thenReturn(DEFAULT_NEWS_TEXT_DE);
        when(newsRequest.getLanguage()).thenReturn("de");
        when(newsRequest.getPlatform()).thenReturn(DEFAULT_PLATFORM);
        when(newsRequest.getUuid()).thenReturn(DEFAULT_NEWS_UUID);
        when(newsRequest.getMaxSoftwareVersion()).thenReturn(DEFAULT_MAX_SOFTWARE_VERSION);
        when(newsRequest.getMinSoftwareVersion()).thenReturn(DEFAULT_MIN_SOFTWARE_VERSION);
        when(newsRequest.isActive()).thenReturn(DEFAULT_FLAG_TRUE);
        when(newsRequest.isForce()).thenReturn(DEFAULT_FLAG_TRUE);

        var news = newsMapper.newsRequestToNews(newsRequest);

        assertEquals(DEFAULT_NEWS_TEXT_DE, news.getTextDe());
        assertNull(news.getTextEn());
        assertEquals(DEFAULT_NEWS_TITLE_DE, news.getTitleDe());
        assertNull(news.getTitleEn());
        assertEquals(DEFAULT_PLATFORM, news.getPlatform());
        assertEquals(DEFAULT_NEWS_UUID, news.getUuid());
        assertEquals(DEFAULT_MAX_SOFTWARE_VERSION, news.getMaxSoftwareVersionCode());
        assertEquals(DEFAULT_MIN_SOFTWARE_VERSION, news.getMinSoftwareVersionCode());
        assertEquals(DEFAULT_FLAG_TRUE, news.isActive());
        assertEquals(DEFAULT_FLAG_TRUE, news.isForce());
    }

    @Test
    public void newsRequestToNews_whenNotDeLanguage_expectNews() {
        when(newsRequest.getTitle()).thenReturn(DEFAULT_NEWS_TITLE_EN);
        when(newsRequest.getText()).thenReturn(DEFAULT_NEWS_TEXT_EN);
        when(newsRequest.getLanguage()).thenReturn("en");
        when(newsRequest.getPlatform()).thenReturn(DEFAULT_PLATFORM);
        when(newsRequest.getUuid()).thenReturn(DEFAULT_NEWS_UUID);
        when(newsRequest.getMaxSoftwareVersion()).thenReturn(DEFAULT_MAX_SOFTWARE_VERSION);
        when(newsRequest.getMinSoftwareVersion()).thenReturn(DEFAULT_MIN_SOFTWARE_VERSION);
        when(newsRequest.isActive()).thenReturn(DEFAULT_FLAG_TRUE);
        when(newsRequest.isForce()).thenReturn(DEFAULT_FLAG_TRUE);

        var news = newsMapper.newsRequestToNews(newsRequest);

        assertEquals(DEFAULT_NEWS_TITLE_EN, news.getTitleEn());
        assertNull(news.getTextDe());
        assertEquals(DEFAULT_NEWS_TEXT_EN, news.getTextEn());
        assertNull(news.getTextDe());
        assertEquals(DEFAULT_PLATFORM, news.getPlatform());
        assertEquals(DEFAULT_NEWS_UUID, news.getUuid());
        assertEquals(DEFAULT_MAX_SOFTWARE_VERSION, news.getMaxSoftwareVersionCode());
        assertEquals(DEFAULT_MIN_SOFTWARE_VERSION, news.getMinSoftwareVersionCode());
        assertEquals(DEFAULT_FLAG_TRUE, news.isActive());
        assertEquals(DEFAULT_FLAG_TRUE, news.isForce());
    }
}