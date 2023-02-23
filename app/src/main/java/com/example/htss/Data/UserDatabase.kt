package com.example.htss.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object{
        @Volatile //다른 thread에서 접근 가능하게 만드는 것입니다.
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context):UserDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){ //synchronized는 새로운 데이터베이스를 instance시킵니다.
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}