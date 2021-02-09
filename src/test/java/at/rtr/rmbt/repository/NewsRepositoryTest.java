package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.News;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static at.rtr.rmbt.TestConstants.ZONED_DATE_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Ignore
public class NewsRepositoryTest extends AbstractRepositoryTest<NewsRepository> {

    @Test
    public void findActiveByUidAndPlatformAndSoftwareVersionCodeAndUuid_whenUuidNotSpecified_expectActualNews() {
        Set<Long> expectedNewsIds = Set.of(1L, 2L, 5L, 7L, 10L);
        List<News> news = LongStream.range(1L, 11L)
            .mapToObj(l -> {
                News.NewsBuilder newsBuilder = News.builder()
                    .active(true)
                    .platform("Android")
                    .textEn("text " + l)
                    .titleEn("title " + l)
                    .force(false);

                // uid 3, 6, 9 - in future
                // uid 4, 8 - in past
                // uid 1, 2, 5, 7, 10 - active news
                if (l % 3 == 0)
                    newsBuilder.startsAt(ZONED_DATE_TIME.plusDays(1));
                else if (l % 4 == 0)
                    newsBuilder.endsAt(ZONED_DATE_TIME.minusDays(1));
                else newsBuilder.startsAt(ZONED_DATE_TIME.minusDays(1));

                return newsBuilder.build();
            }).collect(Collectors.toList());

        dao.saveAll(news);
        List<News> result = dao.findActiveByUidAndPlatformAndSoftwareVersionCodeAndUuid(-1L, "Android", 123L, null);

        assertEquals(expectedNewsIds.size(), result.size());
        result.forEach(n -> assertTrue(expectedNewsIds.contains(n.getId())));
    }
}
