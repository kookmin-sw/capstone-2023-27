package com.example.htss.Retrofit

import com.example.htss.Retrofit.Model.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IRetrofit {

    // 업종 상위
    @GET("/sector/high")
    fun getHighSectorList(
        @Query("num") num: Int) :
            Call<SectorThemeList>
    //테마 상위
    @GET("/thema/high")
    fun getHighThemeList(
        @Query("num") num: Int) :
            Call<SectorThemeList>
    //최근 뉴스
    @GET("/news/recent")
    fun getMainNewsList(
        @Query("num") num: Int) :
            Call<NewsList>

    //검색한 업종에 포함된 종목들
    @GET("/sector/include")
    fun getSectorInclude(
        @Query("sector") sector: String,
        @Query("num") num: Int) :
            Call<SectorThemeIncludeList>

    //검색한 테마에 포함된 종목들
    @GET("thema/include")
    fun getThemeInclude(
        @Query("thema") thema: String,
        @Query("num") num: Int) :
            Call<SectorThemeIncludeList>

    //검색한 업종,테마, 키워드 포함된 뉴스
    @GET("/news/like")
    fun getSectorThemeKeywordIncludeNews(
        @Query("keyword") keyword: String,
        @Query("num") num: Int) :
            Call<KeywordIncludeNewsList>

    //단어와 매칭 많이 된 종목들 가져오기
    @GET("/noun/matching")
    fun getKeywordMatchingStock(
        @Query("noun") noun: String,
        @Query("num") num: Int
    ) : Call<NounMatchingStockList>

    //업종 like 키워드
    @GET("/sector/like")
    fun getSectorLikeKeyword(
        @Query("keyword") keyword: String,
        @Query("num") num: Int
    ) : Call<SectorThemeLikeList>

    //테마 like 키워드
    @GET("/thema/like")
    fun getThemeLikeKeyword(
        @Query("keyword") keyword: String,
        @Query("num") num: Int
    ) : Call<SectorThemeLikeList>

    //티커로 종목명 검색
    @GET("/stock/name")
    fun getStockNameByTicker(
        @Query("ticker") ticker: String
    ) : Call<String>

    //종목명으로 티커 검색
    @GET("/stock/ticker")
    fun getTickerByStockName(
        @Query("company_name") company_name: String
    ):Call<String>

    //기업개요
    @GET("/stock/info")
    fun getCompanyInfo(
        @Query("ticker") ticker:String
    ) : Call<CompanyInfoListItem>


    @GET("/stock/now-price")
    fun getStockNowPrice(
        @Query("ticker") ticker: String
    ) : Call<StockNowPriceListItem>

    @GET("/stock/high-now-price")
    fun getStockHighRate(
        @Query("num") num: Int
    ) :Call<StockHighRateList>

    @GET("/stock/market")
    fun getStockMarket(
        @Query("name") name: String
    ) :Call<StockMarketList>
    @GET("/stock/price")
    fun getStockPrice(
        @Query("ticker") ticker: String,
        @Query("num") num: Int
    ) :Call<StockPriceList>
}