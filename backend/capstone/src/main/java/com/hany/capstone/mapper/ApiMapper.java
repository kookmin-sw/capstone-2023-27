package com.hany.capstone.mapper;

import com.hany.capstone.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApiMapper {
    List<NounDto> nounMatching(String noun,int num);
    List<NounDto> multNounMatching(String noun ,int num, List<String> nounLi, int len);


    List<KeywordRateDto> sectorHigh(int num);
    List<PriceDto> sectorInclude(String sector,int num);
    List<KeywordRateDto> sectorLike(String keyword,int num);


    List<KeywordRateDto> themaHigh(int num);
    List<PriceDto> themaInclude(String thema,int num);
    List<KeywordRateDto> themaLike(String keyword,int num);


    List<NewsDto> newsRecent(int num);
    List<NewsDto> newsLike(String keyword,int num);


    CompanyInfoDto stockInfo(String ticker);
    String stockName(String ticker);
    String stockTicker(String company_name);
    List<DatePriceDto> stockPrice(String ticker, int num);
    NowPriceDto stockNowPrice(String ticker);
    List<NowPriceDto> stockHighNowPrice(int num);


    List<String> stockLike(String keyword);
    DateDto stockDate();
    MarketIndexDto StockMarketId(String market);


    TermsDto searchTerms(String terms);



}