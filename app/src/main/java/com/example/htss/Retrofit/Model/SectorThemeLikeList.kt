package com.example.htss.Retrofit.Model

class SectorThemeLikeList : ArrayList<SectorThemeLikeListItem>()

data class SectorThemeLikeListItem(
    val keyword: String,
    val rate: Float
)