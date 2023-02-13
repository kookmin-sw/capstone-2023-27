package com.example.htss.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.htss.Model.StockListModel
import com.example.htss.Model.ThemeDetailListModel
import com.example.htss.R


class ThemeDetailListAdapter(private val dataSet: MutableList<StockListModel>):

    RecyclerView.Adapter<ThemeDetailListAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val themeDetailName : TextView = view.findViewById(R.id.theme_detail_name)
        val themeCurrentPrice : TextView = view.findViewById(R.id.theme_current_value)
        val themePlusPercent : TextView = view.findViewById(R.id.theme_detail_pluspercent)
        val themeMinusPercent : TextView = view.findViewById(R.id.theme_detail_minuspercent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.theme_detail_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.themeDetailName.text = dataSet[position].StockName
        holder.themeCurrentPrice.text = dataSet[position].StockPrice
        if (dataSet[position].StockPercent.substring(0,1) == "+") {
            holder.themePlusPercent.text = dataSet[position].StockPercent
            holder.themePlusPercent.visibility = View.VISIBLE
            holder.themeMinusPercent.visibility = View.GONE
        }
        if (dataSet[position].StockPercent.substring(0,1) == "-") {
            holder.themeMinusPercent.text = dataSet[position].StockPercent
            holder.themeMinusPercent.visibility = View.VISIBLE
            holder.themePlusPercent.visibility = View.GONE
        }
        holder.itemView.setOnClickListener{
            itemClickListener.onClick(it,position)
        }
    }

    private lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
}
