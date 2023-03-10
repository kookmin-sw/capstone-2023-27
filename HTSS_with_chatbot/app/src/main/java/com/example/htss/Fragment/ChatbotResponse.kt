package com.example.htss.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.renderscript.ScriptGroup.Input
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.concurrent.thread

class ChatbotResponse(private val url: String) {
    fun fetch(onFetched: (String) -> Unit) {
        Thread {
            val url = URL(url)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.connect()
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val input = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuffer()
                var inputLine: String?
                Log.d("ChatbotResponse.input", input.toString())
                var currentLine = input.readLine()
                var nextLine = input.readLine()

                while (currentLine != null) {
                    // 마지막 줄 아닌 경우 줄바꿈 적용
                    if (null != nextLine) {
                        response.append(currentLine+'\n')
                    } else {
                        response.append(currentLine)
                    }
                    currentLine = nextLine
                    nextLine = input.readLine()
                }

                input.close()
                Handler(Looper.getMainLooper()).post {
                    onFetched(response.toString())
                }
            } else {
                Handler(Looper.getMainLooper()).post {
                    onFetched("")
                }
            }
            connection.disconnect()
        }.start()
    }
}

