package com.example.htss.Retrofit.Model

class NounMatchingStockList : ArrayList<NounMatchingStockListItem>()

data class NounMatchingStockListItem (

    val ticker: String,
    val noun: String,
    val count: Int,
    val company_name: String,
    val end_price: Int,
    val rate: Float
)