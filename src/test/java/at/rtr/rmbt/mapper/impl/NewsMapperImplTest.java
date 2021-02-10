package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.NewsMapper;
import at.rtr.rmbt.model.News;
import at.rtr.rmbt.response.NewsListItemResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static at.rtr.rmbt.TestFixtures.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class NewsMapperImplTest {
    private NewsMapper newsMapper;

    @Before
    public void setUp() {
        newsMapper = new NewsMapperImpl();
    }

    @Test
    public void newsToNewsResponse_whenDeLanguage_expectDeNewsResponse() {
        var actualNewsResponse = newsMapper.newsToNewsResponse(newsDe);

        assertEquals(TestConstants.DEFAULT_UID, actualNewsResponse.getUid());
        assertEquals(TestConstants.DEFAULT_NEWS_TITLE_DE, actualNewsResponse.getTitle());
        assertEquals(TestConstants.DEFAULT_NEWS_TEXT_DE, actualNewsResponse.getText());
    }

    @Test
    public void newsToNewsResponse_whenNotDeLanguage_expectEnNewsResponse() {
        var actualNewsResponse = newsMapper.newsToNewsResponse(newsEn);

        assertEquals(TestConstants.DEFAULT_UID, actualNewsResponse.getUid());
        assertEquals(TestConstants.DEFAULT_NEWS_TITLE_EN, actualNewsResponse.getTitle());
        assertEquals(TestConstants.DEFAULT_NEWS_TEXT_EN, actualNewsResponse.getText());
    }

    @Test
    public void newsRequestToNews_whenDeLanguage_expectNews() {
        var news = newsMapper.newsRequestToNews(newsRequestDe);

        assertEquals(TestConstants.DEFAULT_NEWS_TEXT_DE, news.getTextDe());
        assertNotNull(news.getTextEn());
        assertEquals(TestConstants.DEFAULT_NEWS_TITLE_DE, news.getTitleDe());
        assertNotNull(news.getTitleEn());
        assertEquals(TestConstants.DEFAULT_PLATFORM, news.getPlatform());
        assertNull(news.getUuid());
        assertEquals(TestConstants.DEFAULT_MAX_SOFTWARE_VERSION, news.getMaxSoftwareVersionCode());
        assertEquals(TestConstants.DEFAULT_MIN_SOFTWARE_VERSION, news.getMinSoftwareVersionCode());
        assertTrue(news.isActive());
        assertFalse(news.isForce());
    }

    @Test
    public void newsRequestToNews_whenNotDeLanguage_expectNews() {
        var news = newsMapper.newsRequestToNews(newsRequestEn);

        assertEquals(TestConstants.DEFAULT_NEWS_TITLE_EN, news.getTitleEn());
        assertNotNull(news.getTextDe());
        assertEquals(TestConstants.DEFAULT_NEWS_TEXT_EN, news.getTextEn());
        assertNotNull(news.getTextDe());
        assertEquals(TestConstants.DEFAULT_PLATFORM, news.getPlatform());
        assertNull(news.getUuid());
        assertEquals(TestConstants.DEFAULT_MAX_SOFTWARE_VERSION, news.getMaxSoftwareVersionCode());
        assertEquals(TestConstants.DEFAULT_MIN_SOFTWARE_VERSION, news.getMinSoftwareVersionCode());
        assertTrue(news.isActive());
        assertFalse(news.isForce());
    }

    @Test
    public void updateNewsByNewsRequest_whenAllIsOk_expectUpdatedNews() {
        News news = newsMapper.updateNewsByNewsRequest(newsEn, newsRequestDe);

        assertEquals(news.getTextDe(), newsRequestDe.getText());
        assertEquals(news.getTitleDe(), newsRequestDe.getTitle());
        assertNotNull(news.getTextEn());
        assertNotNull(news.getTitleEn());
        assertEquals(news.getUuid(), newsEn.getUuid());
        assertEquals(news.getId(), newsEn.getId());
        assertEquals(news.getTime(), newsEn.getTime());
        assertNotEquals("Must be new object", System.identityHashCode(news), System.identityHashCode(newsEn));
    }

    @Test
    public void newsViewToNewsListItem_whenAllIsOk_expectNewsListItem() {
        NewsListItemResponse news = newsMapper.newsViewToNewsListItem(newsViewEn);

        assertEquals(newsViewEn.getTextEn(), news.getContent());
        assertEquals(newsViewEn.getTitleEn(), news.getTitle());
        assertTrue(news.getAndroid());
        assertEquals(newsViewEn.getUuid(), news.getUuid());
        assertEquals(newsViewEn.getUid(), news.getUid());
        assertEquals(newsViewEn.getMinSoftwareVersionCode(), news.getMinSoftwareVersion());
        assertEquals(newsViewEn.getMaxSoftwareVersionCode(), news.getMaxSoftwareVersion());
        assertTrue(news.getActive());
        assertEquals(newsViewEn.getStartsAt(), news.getStartDate());
        assertEquals(newsViewEn.getEndsAt(), news.getEndDate());
        assertEquals(newsViewEn.getStatus(), news.getStatus());
    }
}