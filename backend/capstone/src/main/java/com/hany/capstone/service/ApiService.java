package com.hany.capstone.service;

import com.hany.capstone.dto.*;
import com.hany.capstone.mapper.ApiMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiService {
    @Autowired
    private ApiMapper apiMapper;

    public List<NounDto> nounMatching(String noun,int num){
        return apiMapper.nounMatching(noun,num);
    }
    public List<NounDto> multNounMatching(String noun ,int num, List<String> nounLi, int len){
        return apiMapper.multNounMatching(noun,num,nounLi,len);
    }


    public List<KeywordRateDto> sectorHigh(int num){
        return apiMapper.sectorHigh(num);
    }
    public List<PriceDto> sectorInclude(String sector,int num){
        return apiMapper.sectorInclude(sector,num);
    }
    public List<KeywordRateDto> sectorLike(String keyword,int num){
        return apiMapper.sectorLike(keyword,num);
    }


    public List<KeywordRateDto> themaHigh(int num){
        return apiMapper.themaHigh(num);
    }
    public List<PriceDto> themaInclude(String thema,int num){
        return apiMapper.themaInclude(thema,num);
    }
    public List<KeywordRateDto> themaLike(String keyword,int num){
        return apiMapper.themaLike(keyword,num);
    }


    public List<NewsDto> newsRecent(int num){
        return apiMapper.newsRecent(num);
    }
    public List<NewsDto> newsLike(String keyword,int num){
        return apiMapper.newsLike(keyword,num);
    }


    public CompanyInfoDto stockInfo(String ticker){
        return apiMapper.stockInfo(ticker);
    }
    public String stockName(String ticker){
        return apiMapper.stockName(ticker);
    }
    public String stockTicker(String company_name){
        return apiMapper.stockTicker(company_name);
    }
    public List<DatePriceDto> stockPrice(String ticker, int num){
        List<DatePriceDto> li = apiMapper.stockPrice(ticker,num);
        for(DatePriceDto dto:li){
            int endp = dto.getEnd_price();
            if (dto.getStart_price() == 0){
                dto.setStart_price(endp);
            }
            if (dto.getHigh_price() == 0){
                dto.setHigh_price(endp);
            }
            if (dto.getLow_price() == 0){
                dto.setLow_price(endp);
            }
        }

        return li;
    }
    public NowPriceDto stockNowPrice(String ticker){
        return apiMapper.stockNowPrice(ticker);
    }
    public List<NowPriceDto> stockHighNowPrice(int num){
        return apiMapper.stockHighNowPrice(num);
    }

    public List<String> stockLike(String keyword){return apiMapper.stockLike(keyword);}
    public DateDto stockDate(){return apiMapper.stockDate();}
    public MarketIndexDto StockMarketId(String market){
        return apiMapper.StockMarketId(market);
    };

    public TermsDto searchTerms(String terms){
        return apiMapper.searchTerms(terms);
    };

    public List<ChartTrendDto> chartTrend(int period,Integer best_label){
        return apiMapper.chartTrend(period,best_label);
    }
    public List<TrendStockDto> trendStock(int period,int label){
        return apiMapper.trendStock(period, label);
    };

    public List<PeriodBestLabelDto> periodBestLabel(int period){
        return apiMapper.periodBestLabel(period);
    };




}
