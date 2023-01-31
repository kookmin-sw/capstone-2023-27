package com.example.htss.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.htss.Model.ThemelistModel
import com.example.htss.R


class ThemeListAdapter(private val itemSet: ArrayList<ThemelistModel>) :
    RecyclerView.Adapter<ThemeListAdapter.ViewHolder>() {

    private lateinit var itemClickListener: OnItemClickListener

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = itemSet.size
    ////////////////////////////////////////////////////////////////

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val themeName: TextView = view.findViewById(R.id.themelist)
        val pluspercent : TextView = view.findViewById(R.id.theme_pluspercent)
        val minuspercent : TextView = view.findViewById(R.id.theme_minuspercent)
    }
    //반복되는 데이터 넣어두는 곳인 ViewHolder를 생성하는 함수... 13~15번 정도 호출되고 끝
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ThemeListAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.themelist_view, parent, false)
        return ThemeListAdapter.ViewHolder(view)
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ////생성된 뷰홀더에 데이터를 바인딩 해주는 함수.. 데이터바인딩이 필요할때마다 호출된다.
    override fun onBindViewHolder(holder: ThemeListAdapter.ViewHolder, position: Int) {
        holder.themeName.text = itemSet[position].themeName
        if (itemSet[position].percent.substring(0,1) == "+") {
            holder.pluspercent.text = itemSet[position].percent
            holder.pluspercent.visibility = View.VISIBLE
            holder.minuspercent.visibility = View.GONE
        }
        if (itemSet[position].percent.substring(0,1) == "-") {
            holder.minuspercent.text = itemSet[position].percent
            holder.minuspercent.visibility = View.VISIBLE
            holder.pluspercent.visibility = View.GONE
        }
        ///////////////////////////////////////////////////////////////////////////////////////////
        // 아이템 클릭 이벤트

        ///////////////////////////////구분선 간격 조절
        val layoutParams = holder.itemView.layoutParams
        layoutParams.height = 160
        holder.itemView.requestLayout()
///////////////////////////////////////////////////////////////////////////////

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
