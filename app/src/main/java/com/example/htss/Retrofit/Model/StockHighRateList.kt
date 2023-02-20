package com.example.htss.Retrofit.Model


class StockHighRateList : ArrayList<StockHighRateListItem>()

data class StockHighRateListItem(
    val ticker: String,
    val start_price:Int,
    val high_price:Int,
    val low_price:Int,
    val end_price:Int,
    val rate:Float,
    val company_name:String
)