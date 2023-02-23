package com.example.htss.Data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)//같은 데이터가 있으면 무시
    suspend fun addUser(user:User) //suspend:코루틴사용 해서 user추가

    @Query("SELECT * FROM keyword_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<User>>
}