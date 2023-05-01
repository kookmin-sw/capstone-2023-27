package com.example.htss.Model

import java.util.*

data class StockChartModel(
    var createdAt: Date,
    var price_high: Long,
    var price_low: Long,
    var price_start: Long,
    var price_end: Long
)