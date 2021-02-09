package at.rtr.rmbt.controller;

import at.rtr.rmbt.TestUtils;
import at.rtr.rmbt.advice.RtrAdvice;
import at.rtr.rmbt.enums.NewsStatus;
import at.rtr.rmbt.request.NewsParametersRequest;
import at.rtr.rmbt.request.NewsRequest;
import at.rtr.rmbt.response.NewsListItemResponse;
import at.rtr.rmbt.response.NewsListResponse;
import at.rtr.rmbt.response.NewsResponse;
import at.rtr.rmbt.service.NewsListService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static at.rtr.rmbt.TestConstants.*;
import static at.rtr.rmbt.constant.URIConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
public class NewsListControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private NewsListService newsListService;

    @Before
    public void setUp() {
        NewsListController newsListController = new NewsListController(newsListService);
        mockMvc = MockMvcBuilders.standaloneSetup(newsListController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setControllerAdvice(new RtrAdvice())
            .build();
    }

    @Test
    public void getNewsPage_whenCommonRequest_expectPageReturned() throws Exception {
        PageRequest request = PageRequest.of(0, 10);
        List<NewsListItemResponse> newsList = IntStream.range(0, 10)
            .mapToObj(i -> getNewsListItem())
            .collect(Collectors.toList());
        PageImpl<NewsListItemResponse> response = new PageImpl<>(newsList, request, 10);
        when(newsListService.listNews(request)).thenReturn(response);

        mockMvc.perform(get(ADMIN_NEWS + "?page=0&size=10"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(TestUtils.asJsonString(response)));
    }

    @Test
    public void getNewsItem_whenCommonRequest_expectNewsItem() throws Exception {
        NewsListItemResponse newsListItem = getNewsListItem();

        when(newsListService.getNews(newsListItem.getUid())).thenReturn(newsListItem);

        mockMvc.perform(get(ADMIN_NEWS_ITEM.replace("{newsId}", String.valueOf(newsListItem.getUid()))))
            .andExpect(status().isOk())
            .andExpect(content().json(TestUtils.asJsonString(newsListItem)));
    }

    @Test
    public void updateNews_whenCommonRequest_expectUpdateNewsCalled() throws Exception {
        NewsRequest newsRequest = getNewsRequest();

        mockMvc.perform(
            put(ADMIN_NEWS_ITEM.replace("{newsId}", String.valueOf(DEFAULT_LAST_NEWS_UID)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(newsRequest))
        ).andExpect(status().isOk());

        verify(newsListService).updateNews(DEFAULT_LAST_NEWS_UID, newsRequest);
    }

    @Test
    public void createNews_whenCommonRequest_expectCreateNewsCalled() throws Exception {
        NewsRequest newsRequest = getNewsRequest();

        mockMvc.perform(
            post(ADMIN_NEWS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(newsRequest))
        ).andExpect(status().isCreated());

        verify(newsListService).createNews(newsRequest);
    }

    @Test
    public void showNewsList_whenCommonRequest_expectGetAllNewsCalled() throws Exception {
        var request = getNewsParametersRequest();
        var response = getNewsListResponse();
        when(newsListService.getAllNews(request)).thenReturn(response);

        mockMvc.perform(post(NEWS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtils.asJsonString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.news", hasSize(1)))
            .andExpect(jsonPath("$.news[0].uid").value(DEFAULT_UID))
            .andExpect(jsonPath("$.news[0].title").value(DEFAULT_NEWS_TITLE))
            .andExpect(jsonPath("$.news[0].text").value(DEFAULT_NEWS_TEXT));

        verify(newsListService).getAllNews(request);
    }

    @Test
    public void createNews_whenDeLanguageRequest_expectCreated() throws Exception {
        var request = getNewsRequest();

        mockMvc.perform(post(ADMIN_NEWS)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtils.asJsonString(request)))
            .andExpect(status().isCreated());

        verify(newsListService).createNews(request);
    }

    private NewsRequest getNewsRequest() {
        return NewsRequest.builder()
            .title(DEFAULT_NEWS_TITLE)
            .text(DEFAULT_NEWS_TEXT)
            .language(DEFAULT_LANGUAGE)
            .active(DEFAULT_FLAG_TRUE)
            .force(DEFAULT_FLAG_TRUE)
            .androidMaxSoftwareVersion(DEFAULT_MAX_SOFTWARE_VERSION)
            .androidMinSoftwareVersion(DEFAULT_MIN_SOFTWARE_VERSION)
            .android(true)
            .build();
    }

    private NewsListResponse getNewsListResponse() {
        return new NewsListResponse(List.of(getNewsResponse()));
    }

    private NewsResponse getNewsResponse() {
        return NewsResponse.builder()
            .uid(DEFAULT_UID)
            .text(DEFAULT_NEWS_TEXT)
            .title(DEFAULT_NEWS_TITLE)
            .build();
    }

    private NewsParametersRequest getNewsParametersRequest() {
        return NewsParametersRequest.builder()
            .language(DEFAULT_LANGUAGE)
            .lastNewsUid(DEFAULT_LAST_NEWS_UID)
            .platform(DEFAULT_PLATFORM)
            .softwareVersionCode(DEFAULT_SOFTWARE_VERSION_CODE)
            .uuid(DEFAULT_NEWS_UUID.toString())
            .build();
    }

    private NewsListItemResponse getNewsListItem() {
        return NewsListItemResponse.builder()
            .uid(DEFAULT_UID)
            .active(true)
            .android(true)
            .maxSoftwareVersion(2L)
            .minSoftwareVersion(1L)
            .title(DEFAULT_NEWS_TITLE)
            .content(DEFAULT_NEWS_TEXT)
            .language(DEFAULT_LANGUAGE)
            .startDate(ZONED_DATE_TIME)
            .endDate(ZONED_DATE_TIME)
            .status(NewsStatus.DRAFT)
            .uuid(DEFAULT_NEWS_UUID)
            .build();

    }
}