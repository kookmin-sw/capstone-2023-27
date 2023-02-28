package com.example.htss.Adapter

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.htss.Model.StockRaiseListModel
import com.example.htss.R
import org.w3c.dom.Text

class StockRaiseListAdapter(private val itemSet: MutableList<StockRaiseListModel>):
        RecyclerView.Adapter<StockRaiseListAdapter.ViewHolder>(){

        private lateinit var itemClickListener: StockRaiseListAdapter.OnItemClickListener

    override fun getItemCount(): Int {
        return itemSet.size
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val stockRaiseRank: TextView = view.findViewById(R.id.stock_raise_rank)
        val stockRaiseName: TextView = view.findViewById(R.id.stock_raise_name)
        val stockRaisePrice: TextView = view.findViewById(R.id.stock_raise_price)
        val stockRaisePlusRate: TextView = view.findViewById(R.id.stock_raise_pluspercent)
        val stockRaiseMinusRate: TextView = view.findViewById(R.id.stock_raise_minuspercent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.stockraiserankitem_view,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.stockRaiseRank.text = (position+1).toString()
        holder.stockRaiseName.text = itemSet[position].StockRaisename
        holder.stockRaisePrice.text = itemSet[position].StockRaisePrice
        if(itemSet[position].StockRaiseRate.substring(0,1) == "+"){
            holder.stockRaisePlusRate.text = itemSet[position].StockRaiseRate
            holder.stockRaiseMinusRate.visibility = View.GONE
            holder.stockRaisePlusRate.visibility = View.VISIBLE
        }
        if(itemSet[position].StockRaiseRate.substring(0,1) == "-"){
            holder.stockRaiseMinusRate.text = itemSet[position].StockRaiseRate
            holder.stockRaiseMinusRate.visibility = View.VISIBLE
            holder.stockRaisePlusRate.visibility = View.GONE
        }

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