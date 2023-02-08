package com.example.htss.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.htss.Model.MainModel
import com.example.htss.R

///////////////////////////////////////////////////////////////////////////////////////////////////////////
class HomeAdapter(private val dataSet: MutableList<MainModel>) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
    ////////////////////////////////////////////////////////////////

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rank: TextView = view.findViewById(R.id.rank)
        val sector: TextView = view.findViewById(R.id.sector)
        val pluspercent: TextView = view.findViewById(R.id.plus_percent)
        val minuspercent: TextView = view.findViewById(R.id.minus_percent)
    }
    //반복되는 데이터 넣어두는 곳인 ViewHolder를 생성하는 함수... 13~15번 정도 호출되고 끝
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ////생성된 뷰홀더에 데이터를 바인딩 해주는 함수.. 데이터바인딩이 필요할때마다 호출된다.
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.rank.text = (position+1).toString()
        viewHolder.sector.text = dataSet[position].name

        if (dataSet[position].percent.substring(0, 1) == "+") {
            viewHolder.pluspercent.text = dataSet[position].percent
            viewHolder.pluspercent.visibility = View.VISIBLE
            viewHolder.minuspercent.visibility = View.GONE
        } else if (dataSet[position].percent.substring(0,1) == "-") {
            viewHolder.minuspercent.text = dataSet[position].percent
            viewHolder.minuspercent.visibility = View.VISIBLE
            viewHolder.pluspercent.visibility = View.GONE
        }
    ///////////////////////////////////////////////////////////////////////////////////////////
        // 아이템 클릭 이벤트
        viewHolder.itemView.setOnClickListener {
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



