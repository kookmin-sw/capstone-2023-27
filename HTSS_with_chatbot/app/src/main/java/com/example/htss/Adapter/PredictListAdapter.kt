package com.example.htss.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.htss.Model.PredictListModel
import com.example.htss.R


class PredictListAdapter(private val itemSet: MutableList<PredictListModel>):
    RecyclerView.Adapter<PredictListAdapter.ViewHolder>() {

    private lateinit var itemClickListener: PredictListAdapter.OnItemClickListener

    override fun getItemCount(): Int {
        return itemSet.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val trendrank : TextView = view.findViewById(R.id.trend_similar_rank)
        val trendname : TextView = view.findViewById(R.id.trend_similar_name)
        val trendticker : TextView = view.findViewById(R.id.trend_similar_ticker)
        val period : TextView = view.findViewById(R.id.period)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictListAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.trend_similar_item_view,parent,false)
        return PredictListAdapter.ViewHolder(view)
    }


    override fun onBindViewHolder(holder: PredictListAdapter.ViewHolder, position: Int) {
        holder.trendrank.text = (position+1).toString()
        holder.trendname.text = itemSet[position].company
        holder.trendticker.text = itemSet[position].ticker
        holder.period.text = itemSet[position].period

        holder.itemView.setOnClickListener{
            itemClickListener.onClick(it,position)
        }
    }



    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
}