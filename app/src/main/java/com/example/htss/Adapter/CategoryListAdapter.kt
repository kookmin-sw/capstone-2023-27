package com.example.htss.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.htss.Model.CategorylistModel
import com.example.htss.R


class CategoryListAdapter(private val itemSet: ArrayList<CategorylistModel>) :
    RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {

    private lateinit var itemClickListener: OnItemClickListener

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = itemSet.size
    ////////////////////////////////////////////////////////////////

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.categorylist)
        val pluspercent : TextView = view.findViewById(R.id.category_pluspercent)
        val minuspercent : TextView = view.findViewById(R.id.category_minuspercent)
    }
    //반복되는 데이터 넣어두는 곳인 ViewHolder를 생성하는 함수... 13~15번 정도 호출되고 끝
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.categorylist_view, parent, false)
        return ViewHolder(view)
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ////생성된 뷰홀더에 데이터를 바인딩 해주는 함수.. 데이터바인딩이 필요할때마다 호출된다.
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.categoryName.text = itemSet[position].categoryName
        if (itemSet[position].percent.substring(0,1) == "+") {
            viewHolder.pluspercent.text = itemSet[position].percent
            viewHolder.pluspercent.visibility = View.VISIBLE
            viewHolder.minuspercent.visibility = View.GONE
        }
        if (itemSet[position].percent.substring(0,1) == "-") {
            viewHolder.minuspercent.text = itemSet[position].percent
            viewHolder.minuspercent.visibility = View.VISIBLE
            viewHolder.pluspercent.visibility = View.GONE
        }
        ///////////////////////////////////////////////////////////////////////////////////////////
        // 아이템 클릭 이벤트

        ///////////////////////////////구분선 간격 조절
        val layoutParams = viewHolder.itemView.layoutParams
        layoutParams.height = 160
        viewHolder.itemView.requestLayout()
///////////////////////////////////////////////////////////////////////////////

        viewHolder.itemView.setOnClickListener{
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
