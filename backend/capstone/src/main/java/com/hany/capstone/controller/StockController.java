package com.hany.capstone.controller;

import com.hany.capstone.dto.*;
import com.hany.capstone.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/stock")
public class StockController {
    @Autowired
    private ApiService apiService;

    @ResponseBody
    @RequestMapping(value = "/info")
    public CompanyInfoDto getStockInfo(@RequestParam String ticker){
        CompanyInfoDto info = apiService.stockInfo(ticker);
        return info;
    }

    @ResponseBody
    @RequestMapping(value = "/name")
    public String getCompanyName(@RequestParam String ticker){
        String company_name = apiService.stockName(ticker);
        return company_name;
    }

    @ResponseBody
    @RequestMapping(value = "/ticker")
    public String getCompanyTicker(@RequestParam String company_name){
        String ticker = apiService.stockTicker(company_name);
        return ticker;
    }

    @ResponseBody
    @RequestMapping(value = "/price")
    public List<DatePriceDto> getStockPrice(@RequestParam String ticker, @RequestParam int num){
        List<DatePriceDto>  list = apiService.stockPrice(ticker,num);
        return list;
    }
    @ResponseBody
    @RequestMapping(value = "/now-price")
    public NowPriceDto getNowPrice(@RequestParam String ticker){
        NowPriceDto price = apiService.stockNowPrice(ticker);
        return price;
    }

    @ResponseBody
    @RequestMapping(value = "/high-now-price")
    public List<NowPriceDto> getHighNowPrice(@RequestParam int num){
        List<NowPriceDto> price = apiService.stockHighNowPrice(num);
        return price;
    }

    @ResponseBody
    @RequestMapping(value = "/like")
    public List<String> getLike(@RequestParam String keyword){
        List<String> list = apiService.stockLike(keyword);
        return list;
    }

    @ResponseBody
    @RequestMapping(value = "/date")
    public DateDto getStockDate(){
        DateDto date = apiService.stockDate();
        return date;
    }
    @ResponseBody
    @RequestMapping(value = "/market")
    public MarketIndexDto getStockMarket(@RequestParam String name){
        MarketIndexDto mi = apiService.StockMarketId(name);
        return mi;
    }
}
