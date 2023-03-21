package com.hany.capstone.controller;

import com.hany.capstone.dto.ChartTrendDto;
import com.hany.capstone.dto.KeywordRateDto;
import com.hany.capstone.dto.PeriodBestLabelDto;
import com.hany.capstone.dto.TrendStockDto;
import com.hany.capstone.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping(value = "/trend")
public class TrendController {
    @Autowired
    private ApiService apiService;

    @ResponseBody
    @RequestMapping(value = "/data")
    public HashMap<Integer,HashMap<String,Object>> getChartTrend(@RequestParam int period){

        List<PeriodBestLabelDto> pbl = apiService.periodBestLabel(period);
        List<Integer> best_label = new ArrayList<>();
        for (int i = 0; i < pbl.size(); i++) {
            best_label.add(pbl.get(i).getLabel());
        }
        HashMap<Integer,HashMap<String,Object>> trendChartDict= new HashMap<>();

        for (int i = 0; i < best_label.size(); i++) {
            HashMap<String,Object> tmpDic = new HashMap<>();
            List<ChartTrendDto> trendChart = apiService.chartTrend(period,best_label.get(i));
            tmpDic.put("trendChart",trendChart);
            List<TrendStockDto> tstock = apiService.trendStock(period,best_label.get(i));
            tmpDic.put("stock",tstock);
            trendChartDict.put(i,tmpDic);
        }


        System.out.println(trendChartDict);


        return trendChartDict;
    }
}
