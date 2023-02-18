package com.example.htss.Retrofit.Model

data class CompanyInfoListItem (
    val ticker:String,
    val company_name:String,
    val company_info:String,
    val market_cap:Int,
    val per:Float,
    val eps: Int,
    val est_per : Float,
    val est_eps : Int,
    val pbr : Float,
    val dvr : Float,
)