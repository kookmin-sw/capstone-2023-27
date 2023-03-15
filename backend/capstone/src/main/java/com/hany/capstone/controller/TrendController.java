package com.hany.capstone.controller;

import com.hany.capstone.dto.ChartTrendDto;
import com.hany.capstone.dto.KeywordRateDto;
import com.hany.capstone.dto.TrendStockDto;
import com.hany.capstone.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/trend")
public class TrendController {
    @Autowired
    private ApiService apiService;

    @ResponseBody
    @RequestMapping(value = "/graph")
    public List<ChartTrendDto> getChartTrend(@RequestParam int period){
        List<ChartTrendDto> list = apiService.chartTrend(period);
        return list;
    }

    @ResponseBody
    @RequestMapping(value = "/stock")
    public List<TrendStockDto> getTrendStock(@RequestParam int period){
        List<TrendStockDto> list = apiService.trendStock(period);
        return list;
    }
}
