package com.example.htss.Retrofit

import android.content.Context
import android.content.SharedPreferences

object MySharedPreferences {

    // 키워드리스트 세팅하기
    fun setKeywordList(
        context: Context,
        keywordList: String
    ) {
        val prefs: SharedPreferences =
            context.getSharedPreferences("", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString("keywordList", keywordList)
        editor.apply()
    }

    // 키워드리스트 가져오기
    fun getKeywordList(context: Context): String {
        val prefs: SharedPreferences =
            context.getSharedPreferences("", Context.MODE_PRIVATE)
        return prefs.getString("keywordList", "").toString()
    }

    // 관심키워드 데이터 있는지 여부
    fun isNotEmpty(context: Context): Boolean{
        val prefs: SharedPreferences =
            context.getSharedPreferences("", Context.MODE_PRIVATE)
        return prefs.getString("keywordList", "").toString().isNotEmpty()
    }

    // 초기화 원할때
    fun clear(context: Context) {
        val prefs: SharedPreferences =
            context.getSharedPreferences("", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

}