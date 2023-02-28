package com.example.htss.Retrofit.Model

data class StockMarketList(
    val market : String,
    val now_value :Float,
    val change_value:Float,
    val change_rate :Float
)