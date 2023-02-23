package com.example.htss.Data

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {
    val readAllData: LiveData<List<User>> = userDao.readAllData()

    suspend fun addUser(user:User){ //suspend를 붙여준 이유는 coroutine을 사용하기 위함입니다.
        userDao.addUser(user) //DAO에서 만들었던 adduser을 실행합니다.
    }

}