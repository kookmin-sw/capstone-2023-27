package com.example.htss.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.htss.R

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        Handler(Looper.getMainLooper()).postDelayed({


            // 일정 시간이 지나면 MainActivity로 이동
            val intent= Intent( this, MainActivity::class.java)
            startActivity(intent)

            finish()

        }, 2000)
    }
}


