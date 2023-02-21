package com.example.htss.Retrofit.Model

class KeywordIncludeNewsList : ArrayList<KeywordIncludeNewsListItem>()

data class KeywordIncludeNewsListItem (
    val ticker: String,
    val provider: String,
    val date: String,
    val rink: String,
    val title: String,
    val sentiment: String
)