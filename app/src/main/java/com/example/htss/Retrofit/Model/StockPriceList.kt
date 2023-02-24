package com.example.htss.Retrofit.Model

import java.util.*
import kotlin.collections.ArrayList

class StockPriceList: ArrayList<StockPriceListItem>()

data class StockPriceListItem(
    val ticker: String,
    val date: Date,
    val end_price: Int,
    val rate: Float,
    val company_name: String
)