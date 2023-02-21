package com.example.htss.Retrofit.Model

class NewsList: ArrayList<NewsListItem>()

data class NewsListItem (
    val ticker: String,
    val provider: String,
    val date: String,
    val rink: String,
    val title: String,
    val sentiment: String
    )