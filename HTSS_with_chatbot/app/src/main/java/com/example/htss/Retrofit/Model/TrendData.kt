package com.example.htss.Retrofit.Model

data class list(
    val n0: Graph,
    val n1: Graph,
    val n2: Graph
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