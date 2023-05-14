package com.example.htss.Model

import java.util.*

data class StockChartModel(
    var createdAt: Date,
    var price_high: Float,
    var price_low: Float,
    var price_start: Float,
    var price_end: Float,
    var trend : String
)