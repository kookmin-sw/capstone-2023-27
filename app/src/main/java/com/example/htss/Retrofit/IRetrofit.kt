package com.example.htss.Retrofit

import com.example.htss.Retrofit.Model.KeywordIncludeNewsList
import com.example.htss.Retrofit.Model.NewsList
import com.example.htss.Retrofit.Model.SectorThemeIncludeList
import com.example.htss.Retrofit.Model.SectorThemeList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IRetrofit {

    //https://54.180.109.32:8080/sector/high/?num=""
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

    //검색한 업종이 포함된 뉴스
    @GET("/news/like")
    fun getSectorIncludeNews(
        @Query("keyword") keyword: String,
        @Query("num") num: Int) :
            Call<KeywordIncludeNewsList>

    fun getThemeIncludeNews()


}