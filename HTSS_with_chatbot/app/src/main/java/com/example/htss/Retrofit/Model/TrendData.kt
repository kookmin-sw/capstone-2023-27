package com.example.htss.Retrofit.Model

import com.google.gson.annotations.SerializedName

data class TrendList(
    @SerializedName("0") val n0: Graph,
    @SerializedName("1") val n1: Graph,
    @SerializedName("2") val n2: Graph,
    @SerializedName("3") val n3: Graph,
    @SerializedName("4") val n4: Graph,
    @SerializedName("5") val n5: Graph,
    @SerializedName("6") val n6: Graph,
    @SerializedName("7") val n7: Graph,
    @SerializedName("8") val n8: Graph,
    @SerializedName("9") val n9: Graph
)
data class Graph (
    val meanAndCount: MeanCount,
    val trendChart: List<TrendChart>,
    val stock: List<Stock>
)

data class MeanCount(
    val mean: Float,
    val count : Int
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