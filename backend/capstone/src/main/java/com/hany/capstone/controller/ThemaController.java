package com.hany.capstone.controller;

import com.hany.capstone.dto.KeywordRateDto;
import com.hany.capstone.dto.PriceDto;
import com.hany.capstone.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/thema")
public class ThemaController {
    @Autowired
    private ApiService apiService;

    @ResponseBody
    @RequestMapping(value = "/high")
    public List<KeywordRateDto> getThemaHigh(@RequestParam int num){
        List<KeywordRateDto>thema_list = apiService.themaHigh(num);
        return thema_list;
    }

    @ResponseBody
    @RequestMapping(value = "/include")
    public List<PriceDto> getThemaInclude(@RequestParam String thema, @RequestParam int num){
        List<PriceDto> thema_list = apiService.themaInclude(thema,num);
        return thema_list;
    }

    @ResponseBody
    @RequestMapping(value = "/like")
    public List<KeywordRateDto> getThemaLike(@RequestParam String keyword, @RequestParam int num){
        List<KeywordRateDto> thema_list = apiService.themaLike(keyword,num);
        return thema_list;
    }

}

