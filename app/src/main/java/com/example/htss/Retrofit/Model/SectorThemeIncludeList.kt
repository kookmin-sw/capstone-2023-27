package com.example.htss.Retrofit.Model

class SectorThemeIncludeList : ArrayList<SectorThemeIncludeItem>()

data class SectorThemeIncludeItem(
    val company_name : String,
    val keyword: String,
    val ticker: String,
    val end_price: Int,
    val rate: Float
)