package com.example.htss.Retrofit

import com.example.htss.Retrofit.Model.NewsList
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

    fun getSectorInclude()

    fun getThemeInclude()

    fun getSectorLikeKeyword()

    fun getThemeLikeKeyword()


}