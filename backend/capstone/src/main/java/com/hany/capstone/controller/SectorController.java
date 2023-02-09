package com.hany.capstone.controller;

import com.hany.capstone.dto.KeywordRateDto;
import com.hany.capstone.dto.NounDto;
import com.hany.capstone.dto.PriceDto;
import com.hany.capstone.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/sector")
public class SectorController {
    @Autowired
    private ApiService apiService;

    @ResponseBody
    @RequestMapping(value = "/high")
    public List<KeywordRateDto> getSectorHigh(@RequestParam int num){
        List<KeywordRateDto> sector_list = apiService.sectorHigh(num);
        return sector_list;
    }

    @ResponseBody
    @RequestMapping(value = "/include")
    public List<PriceDto> getSectorInclude(@RequestParam String sector, @RequestParam int num){
        List<PriceDto> sector_list = apiService.sectorInclude(sector,num);
        return sector_list;
    }

    @ResponseBody
    @RequestMapping(value = "/like")
    public List<KeywordRateDto> getSectorLike(@RequestParam String keyword, @RequestParam int num){
        List<KeywordRateDto> sector_list = apiService.sectorLike(keyword,num);
        return sector_list;
    }



}
