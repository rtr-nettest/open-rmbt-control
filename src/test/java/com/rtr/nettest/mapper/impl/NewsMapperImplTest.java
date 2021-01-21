package com.rtr.nettest.mapper.impl;

import com.rtr.nettest.model.News;
import com.rtr.nettest.mapper.NewsMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static com.rtr.nettest.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class NewsMapperImplTest {
    private NewsMapper newsMapper;

    @Mock
    private News news;

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
}