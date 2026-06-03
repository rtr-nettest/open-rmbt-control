package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.NewsMapper;
import at.rtr.rmbt.model.News;
import at.rtr.rmbt.model.NewsView;
import at.rtr.rmbt.request.NewsRequest;
import at.rtr.rmbt.response.NewsListItemResponse;
import at.rtr.rmbt.response.NewsResponse;
import org.springframework.stereotype.Service;

/**
 * News mapper impl class.
 */
@Service
public class NewsMapperImpl implements NewsMapper {
    private static String LANGUAGE_DE = "de";
    private static String LANGUAGE_EN = "en";

    /**
     * News to news response.
     *
     * @param news the News
     * @return the result
     */
    @Override
    public NewsResponse newsToNewsResponse(News news) {
        var isEn = news.getTitleEn() != null;
        var response = NewsResponse.builder()
            .uid(news.getId())
            .title(isEn ? news.getTitleEn() : news.getTitleDe())
            .text(isEn ? news.getTextEn() : news.getTextDe());
        return response.build();
    }

    /**
     * News request to news.
     *
     * @param newsRequest the News request
     * @return the result
     */
    @Override
    public News newsRequestToNews(NewsRequest newsRequest) {
        return newsRequestToNewsBuilder(newsRequest).build();
    }

    /**
     * Update news by news request.
     *
     * @param news the News
     * @param newsRequest the News request
     * @return the result
     */
    @Override
    public News updateNewsByNewsRequest(News news, NewsRequest newsRequest) {
        return newsRequestToNewsBuilder(newsRequest)
            .id(news.getId())
            .time(news.getTime())
            .uuid(news.getUuid()).build();
    }

    /**
     * News view to news list item.
     *
     * @param news the News
     * @return the result
     */
    @Override
    public NewsListItemResponse newsViewToNewsListItem(NewsView news) {
        var isEn = news.getTitleEn() != null;
        return NewsListItemResponse.builder()
            .uid(news.getUid())
            .uuid(news.getUuid())
            .title(isEn ? news.getTitleEn() : news.getTitleDe())
            .content(isEn ? news.getTextEn() : news.getTextDe())
            .language(isEn ? LANGUAGE_EN : LANGUAGE_DE)
            .active(news.isActive())
            .android(true)
            .status(news.getStatus())
            .minSoftwareVersion(news.getMinSoftwareVersionCode())
            .maxSoftwareVersion(news.getMaxSoftwareVersionCode())
            .startDate(news.getStartsAt())
            .endDate(news.getEndsAt())
            .build();
    }

    /**
     * News request to news builder.
     *
     * @param newsRequest the News request
     * @return the result
     */
    private News.NewsBuilder newsRequestToNewsBuilder(NewsRequest newsRequest) {
        News.NewsBuilder newsBuilder = News.builder()
            .minSoftwareVersionCode(newsRequest.getAndroidMinSoftwareVersion())
            .maxSoftwareVersionCode(newsRequest.getAndroidMaxSoftwareVersion())
            .active(newsRequest.isActive())
            .force(newsRequest.isForce())
            .platform("Android") // only support android for now
            .endsAt(newsRequest.getEndDate());

        if (newsRequest.getStartDate() != null)
            newsBuilder.startsAt(newsRequest.getStartDate());

        newsBuilder.titleDe(newsRequest.getTitle());
        newsBuilder.textDe(newsRequest.getText());
        newsBuilder.titleEn(newsRequest.getTitle());
        newsBuilder.textEn(newsRequest.getText());

        return newsBuilder;
    }
}
