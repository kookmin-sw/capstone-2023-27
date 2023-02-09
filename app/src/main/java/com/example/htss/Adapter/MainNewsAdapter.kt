package com.example.htss.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.htss.Model.NewsModel
import com.example.htss.R
import org.w3c.dom.Text

///////////////////////////////////////////////////////////////////////////////////////////////////////////
class MainNewsAdapter(private val itemSet: MutableList<NewsModel>) :
    RecyclerView.Adapter<MainNewsAdapter.ViewHolder>() {

    private lateinit var itemClickListener: OnItemClickListener

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = itemSet.size
    ////////////////////////////////////////////////////////////////

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val ticker: TextView = view.findViewById(R.id.ticker)
        val provider: TextView = view.findViewById(R.id.provider)
        val date: TextView = view.findViewById(R.id.date)
//        val link: TextView = view.findViewById(R.id.link)
        }
    //반복되는 데이터 넣어두는 곳인 ViewHolder를 생성하는 함수... 13~15번 정도 호출되고 끝
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.news_view, parent, false)
        return ViewHolder(view)
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ////생성된 뷰홀더에 데이터를 바인딩 해주는 함수.. 데이터바인딩이 필요할때마다 호출된다.
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.title.text = itemSet[position].title
        viewHolder.ticker.text = itemSet[position].ticker
        viewHolder.provider.text = itemSet[position].provider
        viewHolder.date.text = itemSet[position].date
//        viewHolder.link.text = itemSet[position].rink
        ///////////////////////////////////////////////////////////////////////////////////////////


//        ///////////////////////////////구분선 간격 조절
//        val layoutParams = viewHolder.itemView.layoutParams
//        layoutParams.height = 160
//        viewHolder.itemView.requestLayout()
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



