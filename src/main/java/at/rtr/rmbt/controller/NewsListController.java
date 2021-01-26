package at.rtr.rmbt.controller;

import at.rtr.rmbt.constant.URIConstants;
import at.rtr.rmbt.request.NewsParametersRequest;
import at.rtr.rmbt.request.NewsRequest;
import at.rtr.rmbt.response.NewsListResponse;
import at.rtr.rmbt.service.NewsListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NewsListController {

    private final NewsListService newsListService;

    @PostMapping(URIConstants.NEWS_URL)
    public NewsListResponse showNewsList(@RequestBody NewsParametersRequest request) {
        return newsListService.getAllNews(request);
    }

    @PostMapping(URIConstants.ADMIN_NEWS)
    @ResponseStatus(HttpStatus.CREATED)
    public void createNews(@RequestBody NewsRequest newsRequest) {
        newsListService.createNews(newsRequest);
    }
}
