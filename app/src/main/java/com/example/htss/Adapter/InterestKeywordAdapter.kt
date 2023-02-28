package com.example.htss.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.htss.R

class InterestKeywordAdapter(private var itemSet: MutableList<String>) :
    RecyclerView.Adapter<InterestKeywordAdapter.ViewHolder>() {

    private lateinit var itemClickListener: InterestKeywordAdapter.OnItemClickListener
    private lateinit var deleteClickListener: OnDeleteClickListener

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val keyword: TextView = view.findViewById(R.id.interest_list)
        val delete : ImageView = view.findViewById(R.id.delete)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.interest_item_view, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.keyword.text = itemSet[position]
        holder.delete.setOnClickListener{
            deleteClickListener.onClick(it,position)
            Log.d("delete_position", position.toString())
        }
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    override fun getItemCount() = itemSet.size

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    interface OnDeleteClickListener {
        fun onClick(v: View, position: Int)
    }
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    fun setDeleteClickListener(onDeleteClickListener: OnDeleteClickListener){
        this.deleteClickListener = onDeleteClickListener
    }

//    fun setData(user:MutableList<String>){
//        //유저리스트가 변경 되었을때, 업데이트해줍니다.
//        this.itemSet = user
//        notifyDataSetChanged()
//    }
}

