package com.rtr.nettest.controller;

import com.rtr.nettest.request.NewsParametersRequest;
import com.rtr.nettest.request.NewsRequest;
import com.rtr.nettest.response.NewsListResponse;
import com.rtr.nettest.service.NewsListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.rtr.nettest.constant.URIConstants.ADMIN_NEWS;
import static com.rtr.nettest.constant.URIConstants.NEWS_URL;

@RestController
@RequiredArgsConstructor
public class NewsListController {

    private final NewsListService newsListService;

    @PostMapping(NEWS_URL)
    public NewsListResponse showNewsList(@RequestBody NewsParametersRequest request) {
        return newsListService.getAllNews(request);
    }

    @PostMapping(ADMIN_NEWS)
    @ResponseStatus(HttpStatus.CREATED)
    public void createNews(@RequestBody NewsRequest newsRequest) {
        newsListService.createNews(newsRequest);
    }
}
