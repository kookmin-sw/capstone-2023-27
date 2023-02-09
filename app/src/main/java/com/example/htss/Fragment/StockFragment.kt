package com.example.htss.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.htss.Adapter.MainNewsAdapter
import com.example.htss.Model.NewsModel
import com.example.htss.Retrofit.RetrofitClient
import com.example.htss.databinding.FragmentStockBinding

class StockFragment : Fragment() {

    private lateinit var view: FragmentStockBinding
    private val retrofit = RetrofitClient.create()
    private var StockNewsList = arrayListOf<NewsModel>()

    private var StockNewsListAdapter = MainNewsAdapter(StockNewsList)

    private var Stockname = ""
    private var StockPrice = ""
    private var StockPercent = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentStockBinding.inflate(inflater, container, false)

        Stockname = arguments?.getString("stock_name").toString()
        StockPrice= arguments?.getString("stock_price").toString()
        StockPercent = arguments?.getString("stock_percent").toString()

        view.stockName.text = Stockname
        view.stockName2.text = Stockname
        view.stockCurrent.text = StockPrice

        if(StockPercent.substring(0,1) == "+"){
            view.stockPlusPercent.text = StockPercent
            view.stockMinusPercent.visibility = View.GONE
            view.stockPlusPercent.visibility = View.VISIBLE
        }

        if(StockPercent.substring(0,1) == "-"){
            view.stockMinusPercent.text = StockPercent
            view.stockPlusPercent.visibility = View.GONE
            view.stockMinusPercent.visibility = View.VISIBLE
        }

        view.newsRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = StockNewsListAdapter

        }

        view.back.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return view.root
    }
}