package com.example.htss.Retrofit.Model

import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class StockPriceList: ArrayList<StockPriceListItem>()

data class StockPriceListItem(
    val ticker: String,
    val start_price: Int,
    val high_price: Int,
    val low_price: Int,
    val end_price: Int,
    val share_volume: Int,
    val trade_volume: Int,
    val date: Date,
    val rate: Float,
    val company_name: String
)