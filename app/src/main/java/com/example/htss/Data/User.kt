package com.example.htss.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keyword_table") //데이터를 불러올때 테이블의 이름을 정합니다.
data class User(
    @PrimaryKey(autoGenerate = true) //primary key는 자동으로 만들게 합니다.
    val id: Int,
    val keyword:String
) {
}