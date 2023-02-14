package com.example.htss.Retrofit.Model

data class StockNowPriceListItem(
    val ticker: String,
    val start_price: Int,
    val high_price: Int,
    val low_price: Int,
    val end_price: Int,
    val rate: Float
)