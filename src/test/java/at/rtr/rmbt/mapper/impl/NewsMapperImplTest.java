package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.NewsMapper;
import at.rtr.rmbt.model.News;
import at.rtr.rmbt.request.NewsRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        when(news.getId()).thenReturn(TestConstants.DEFAULT_UID);
        when(news.getTitleDe()).thenReturn(TestConstants.DEFAULT_NEWS_TITLE);
        when(news.getTextDe()).thenReturn(TestConstants.DEFAULT_NEWS_TEXT);

        var actualNewsResponse = newsMapper.newsToNewsResponse(news, "de");

        assertEquals(TestConstants.DEFAULT_UID, actualNewsResponse.getUid());
        assertEquals(TestConstants.DEFAULT_NEWS_TITLE, actualNewsResponse.getTitle());
        assertEquals(TestConstants.DEFAULT_NEWS_TEXT, actualNewsResponse.getText());
    }

    @Test
    public void newsToNewsResponse_whenNotDeLanguage_expectEnNewsResponse() {
        when(news.getId()).thenReturn(TestConstants.DEFAULT_UID);
        when(news.getTitleEn()).thenReturn(TestConstants.DEFAULT_NEWS_TITLE);
        when(news.getTextEn()).thenReturn(TestConstants.DEFAULT_NEWS_TEXT);

        var actualNewsResponse = newsMapper.newsToNewsResponse(news, "en");

        assertEquals(TestConstants.DEFAULT_UID, actualNewsResponse.getUid());
        assertEquals(TestConstants.DEFAULT_NEWS_TITLE, actualNewsResponse.getTitle());
        assertEquals(TestConstants.DEFAULT_NEWS_TEXT, actualNewsResponse.getText());
    }

    @Test
    public void newsToNewsResponse_whenUuidIsNull_expectUuidIsGenerated() {
        prepareNewsRequestDe();
        when(newsRequest.getUuid()).thenReturn(null);

        var result = newsMapper.newsRequestToNews(newsRequest);

        assertNotNull(result.getUuid());
    }

    @Test
    public void newsRequestToNews_whenDeLanguage_expectNews() {
        prepareNewsRequestDe();

        var news = newsMapper.newsRequestToNews(newsRequest);

        Assert.assertEquals(TestConstants.DEFAULT_NEWS_TEXT_DE, news.getTextDe());
        assertNull(news.getTextEn());
        Assert.assertEquals(TestConstants.DEFAULT_NEWS_TITLE_DE, news.getTitleDe());
        assertNull(news.getTitleEn());
        Assert.assertEquals(TestConstants.DEFAULT_PLATFORM, news.getPlatform());
        Assert.assertEquals(TestConstants.DEFAULT_NEWS_UUID, news.getUuid());
        Assert.assertEquals(TestConstants.DEFAULT_MAX_SOFTWARE_VERSION, news.getMaxSoftwareVersionCode());
        Assert.assertEquals(TestConstants.DEFAULT_MIN_SOFTWARE_VERSION, news.getMinSoftwareVersionCode());
        Assert.assertEquals(TestConstants.DEFAULT_FLAG_TRUE, news.isActive());
        Assert.assertEquals(TestConstants.DEFAULT_FLAG_TRUE, news.isForce());
    }

    @Test
    public void newsRequestToNews_whenNotDeLanguage_expectNews() {
        prepareNewsRequestEn();

        var news = newsMapper.newsRequestToNews(newsRequest);

        Assert.assertEquals(TestConstants.DEFAULT_NEWS_TITLE_EN, news.getTitleEn());
        assertNull(news.getTextDe());
        Assert.assertEquals(TestConstants.DEFAULT_NEWS_TEXT_EN, news.getTextEn());
        assertNull(news.getTextDe());
        Assert.assertEquals(TestConstants.DEFAULT_PLATFORM, news.getPlatform());
        Assert.assertEquals(TestConstants.DEFAULT_NEWS_UUID, news.getUuid());
        Assert.assertEquals(TestConstants.DEFAULT_MAX_SOFTWARE_VERSION, news.getMaxSoftwareVersionCode());
        Assert.assertEquals(TestConstants.DEFAULT_MIN_SOFTWARE_VERSION, news.getMinSoftwareVersionCode());
        Assert.assertEquals(TestConstants.DEFAULT_FLAG_TRUE, news.isActive());
        Assert.assertEquals(TestConstants.DEFAULT_FLAG_TRUE, news.isForce());
    }


    private void prepareNewsRequestDe() {
        when(newsRequest.getTitle()).thenReturn(TestConstants.DEFAULT_NEWS_TITLE_DE);
        when(newsRequest.getText()).thenReturn(TestConstants.DEFAULT_NEWS_TEXT_DE);
        when(newsRequest.getLanguage()).thenReturn("de");
        prepareNewsRequest();
    }

    private void prepareNewsRequestEn() {
        when(newsRequest.getTitle()).thenReturn(TestConstants.DEFAULT_NEWS_TITLE_EN);
        when(newsRequest.getText()).thenReturn(TestConstants.DEFAULT_NEWS_TEXT_EN);
        when(newsRequest.getLanguage()).thenReturn("en");
        prepareNewsRequest();
    }

    private void prepareNewsRequest() {
        when(newsRequest.getPlatform()).thenReturn(TestConstants.DEFAULT_PLATFORM);
        when(newsRequest.getUuid()).thenReturn(TestConstants.DEFAULT_NEWS_UUID);
        when(newsRequest.getMaxSoftwareVersion()).thenReturn(TestConstants.DEFAULT_MAX_SOFTWARE_VERSION);
        when(newsRequest.getMinSoftwareVersion()).thenReturn(TestConstants.DEFAULT_MIN_SOFTWARE_VERSION);
        when(newsRequest.isActive()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
        when(newsRequest.isForce()).thenReturn(TestConstants.DEFAULT_FLAG_TRUE);
    }
}