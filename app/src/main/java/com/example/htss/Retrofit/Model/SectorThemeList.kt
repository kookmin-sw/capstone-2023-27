package com.example.htss.Retrofit.Model

class SectorThemeList : ArrayList<ResultSectorThemeItem>()

data class ResultSectorThemeItem(
    val keyword: String,
    val rate: Float
)