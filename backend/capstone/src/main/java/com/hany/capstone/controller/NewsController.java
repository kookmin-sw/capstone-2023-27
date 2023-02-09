package com.hany.capstone.controller;

import com.hany.capstone.dto.NewsDto;
import com.hany.capstone.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/news")
public class NewsController {
    @Autowired
    private ApiService apiService;

//   키워드를 포함하는 뉴스 검색
    @ResponseBody
    @RequestMapping(value = "/recent")
    public List<NewsDto> getNewsRecent(@RequestParam int num){
        List<NewsDto> news_list = apiService.newsRecent(num);
        return news_list;
    }

    @ResponseBody
    @RequestMapping(value = "/like")
    public List<NewsDto> getNewsLike(@RequestParam String keyword,@RequestParam int num){
        List<NewsDto> news_list = apiService.newsLike(keyword,num);
        return news_list;
    }
}
