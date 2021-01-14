package com.rtr.nettest.controller;

import com.rtr.nettest.request.NewsParametersRequest;
import com.rtr.nettest.response.NewsListResponse;
import com.rtr.nettest.service.NewsListService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.rtr.nettest.constant.URIConstants.NEWS_URL;

@RestController
@RequestMapping(NEWS_URL)
@RequiredArgsConstructor
public class NewsListController {

    private final NewsListService newsListService;

    @PostMapping
    public NewsListResponse showNewsList(@RequestBody NewsParametersRequest request) {
        return newsListService.getAllNews(request);
    }
}
