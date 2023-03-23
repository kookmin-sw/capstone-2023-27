package com.example.htss.Retrofit.Model

import com.google.gson.annotations.SerializedName

data class TrendList(
    @SerializedName("0") val n0: Graph,
    @SerializedName("1") val n1: Graph,
    @SerializedName("2")val n2: Graph
)
data class Graph (
    val trendChart: List<TrendChart>,
    val stock: List<Stock>
)

data class Stock(
    val ticker: String,
    val company_name: String,
    val period: Int,
    val label: Int,
    val ranking: Int
)

data class TrendChart(
    val end_price: Float,
    val period: Int,
    val label: Int
)