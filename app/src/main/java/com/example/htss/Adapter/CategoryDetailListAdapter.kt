package com.example.htss.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.htss.Model.CategoryDetailListModel
import com.example.htss.Model.StockListModel
import com.example.htss.R


class CategoryDetailListAdapter(private val dataSet: MutableList<StockListModel>):

    RecyclerView.Adapter<CategoryDetailListAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val categoryDetailName : TextView = view.findViewById(R.id.category_detail_name)
        val categoryCurrentPrice : TextView = view.findViewById(R.id.category_current_value)
        val categoryPlusPercent : TextView = view.findViewById(R.id.category_detail_pluspercent)
        val categoryMinusPercent : TextView = view.findViewById(R.id.category_detail_minuspercent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_detail_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.categoryDetailName.text = dataSet[position].StockName
        holder.categoryCurrentPrice.text = dataSet[position].StockPrice
        if (dataSet[position].StockPercent.substring(0,1) == "+") {
            holder.categoryPlusPercent.text = dataSet[position].StockPercent
            holder.categoryPlusPercent.visibility = View.VISIBLE
            holder.categoryMinusPercent.visibility = View.GONE
        }
        if (dataSet[position].StockPercent.substring(0,1) == "-") {
            holder.categoryMinusPercent.text = dataSet[position].StockPercent
            holder.categoryMinusPercent.visibility = View.VISIBLE
            holder.categoryPlusPercent.visibility = View.GONE
        }
        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener: OnItemClickListener
}