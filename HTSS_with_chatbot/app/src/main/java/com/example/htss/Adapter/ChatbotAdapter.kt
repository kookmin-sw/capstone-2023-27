package com.example.htss.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.htss.Model.ChatbotMessageModel
import androidx.recyclerview.widget.RecyclerView
import com.example.htss.R

class ChatbotAdapter: RecyclerView.Adapter<ChatbotAdapter.ViewHolder>() {
    var messagesList = mutableListOf<ChatbotMessageModel>()

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    fun insertMessage(message: ChatbotMessageModel) {
        messagesList.add(message)
        notifyItemInserted(messagesList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.chatbot_message_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMessage = messagesList[position]
        val userMessage: TextView = holder.itemView.findViewById(R.id.user_message)
        val botMessage: TextView = holder.itemView.findViewById(R.id.chatbot_message)

        when(currentMessage.id) {
            "user_id" -> {
                userMessage.text = currentMessage.message
                userMessage.visibility = View.VISIBLE
                botMessage.visibility = View.GONE
            }
            "bot_id" -> {
                botMessage.text = currentMessage.message
                botMessage.visibility = View.VISIBLE
                userMessage.visibility = View.GONE

            }
        }
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }
}

