package com.example.htss.Adapter

import android.service.voice.VoiceInteractionSession.VisibleActivityCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.htss.Model.KeywordRelatedStockModel
import com.example.htss.R
import org.w3c.dom.Text

class KeywordRelatedStockAdapter(private val itemSet: MutableList<KeywordRelatedStockModel>) :
    RecyclerView.Adapter<KeywordRelatedStockAdapter.ViewHolder>() {

    private lateinit var itemClickListener: OnItemClickListener


    override fun getItemCount(): Int {
        return itemSet.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val stockrank : TextView = view.findViewById(R.id.stock_rank)
        val stockname : TextView = view.findViewById(R.id.stock_related_name)
        val stockprice: TextView = view.findViewById(R.id.stock_related_price)
        val stockpluspercent : TextView = view.findViewById(R.id.stock_pluspercent)
        val stockminuspercent : TextView = view.findViewById(R.id.stock_minuspercent)
        val stockmention : TextView = view.findViewById(R.id.stock_mention)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder{
        val view =
        LayoutInflater.from(parent.context).inflate(R.layout.related_stock_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        holder.stockrank.text = (position+1).toString()
        holder.stockname.text = itemSet[position].Stockname
        holder.stockprice.text = itemSet[position].Stockprice
        if(itemSet[position].Stockpercent.substring(0,1) == "+"){
            holder.stockpluspercent.text = itemSet[position].Stockpercent
            holder.stockpluspercent.visibility = View.VISIBLE
            holder.stockminuspercent.visibility = View.GONE
        }
        if(itemSet[position].Stockpercent.substring(0,1) == "-"){
            holder.stockminuspercent.text = itemSet[position].Stockpercent
            holder.stockminuspercent.visibility = View.VISIBLE
            holder.stockpluspercent.visibility = View.GONE
        }
        holder.stockmention.text = itemSet[position].Stockmention

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