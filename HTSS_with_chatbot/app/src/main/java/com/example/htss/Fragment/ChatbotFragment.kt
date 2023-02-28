package com.example.htss.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.htss.databinding.FragmentChatbotBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.htss.Adapter.ChatbotAdapter
import com.example.htss.Model.ChatbotMessageModel
import kotlinx.android.synthetic.main.fragment_chatbot.*
import kotlinx.coroutines.*


class ChatbotFragment: Fragment() {

    private lateinit var adapter: ChatbotAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentChatbotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView()
        clickEvents()
    }

    private fun clickEvents() {
        send_btn.setOnClickListener {
            sendMessage()
        }
    }

    private fun recyclerView() {
        adapter = ChatbotAdapter()
        rv_messages.adapter = adapter
        rv_messages.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    }

    private fun sendMessage() {
        val message = edit_message.text.toString()
        val timeStamp = System.currentTimeMillis()

        if (message.isNotEmpty()) {
            edit_message.setText("")
            adapter.insertMessage(ChatbotMessageModel(message, "user_id", timeStamp))
            rv_messages.scrollToPosition(adapter.itemCount-1)

            chatbotResponse(message)
        }
    }

    private fun chatbotResponse(message: String) {
        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                val baseUrl: String =
                    "http:/3.37.89.191:8080/"
                val requestUrl = baseUrl + "chatbot/ask?question=" + message
                val responseFetcher = ChatbotResponse(requestUrl)
                val timeStamp = System.currentTimeMillis()
                responseFetcher.fetch { response ->
                    adapter.insertMessage(ChatbotMessageModel(response, "bot_id", timeStamp))
                    rv_messages.scrollToPosition(adapter.itemCount - 1)
                    Log.d("챗봇 응답", response)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                rv_messages.scrollToPosition(adapter.itemCount-1)
            }
        }
    }
}