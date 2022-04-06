package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.request.NewsParametersRequest;
import at.rtr.rmbt.request.NewsRequest;
import at.rtr.rmbt.response.NewsListItemResponse;
import at.rtr.rmbt.response.NewsListResponse;
import at.rtr.rmbt.service.NewsListService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NewsListController {

    private final NewsListService newsListService;

    @ApiOperation(value = "Get news page", notes = "Get news page")
    @GetMapping(URIConstants.ADMIN_NEWS)
    public Page<NewsListItemResponse> getNewsPage(@PageableDefault Pageable pageable) {
        return newsListService.listNews(pageable);
    }

    @ApiOperation(value = "Update news", notes = "Update news by id.")
    @PutMapping(URIConstants.ADMIN_NEWS_ITEM)
    public void updateNews(@PathVariable Long newsId, @RequestBody @Validated NewsRequest newsRequest) {
        newsListService.updateNews(newsId, newsRequest);
    }

    @ApiOperation(value = "Get news item", notes = "Get single news by id.")
    @GetMapping(URIConstants.ADMIN_NEWS_ITEM)
    public NewsListItemResponse getNewsItem(@PathVariable Long newsId) {
        return newsListService.getNews(newsId);
    }

    @ApiOperation(value = "Get all active news", notes = "Get news from backend to display news info to the user just after it is opened")
    @PostMapping(URIConstants.NEWS_URL)
    public NewsListResponse showNewsList(@RequestBody NewsParametersRequest request) {
        return newsListService.getAllNews(request);
    }

    @ApiOperation(value = "Add news", notes = "Create news item.")
    @PostMapping(URIConstants.ADMIN_NEWS)
    @ResponseStatus(HttpStatus.CREATED)
    public void createNews(@Validated @RequestBody NewsRequest newsRequest) {
        newsListService.createNews(newsRequest);
    }
}
