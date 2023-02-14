package com.example.htss.Fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.htss.Adapter.MainNewsAdapter
import com.example.htss.Model.NewsModel
import com.example.htss.Model.StockListModel
import com.example.htss.R
import com.example.htss.Retrofit.Model.KeywordIncludeNewsList
import com.example.htss.Retrofit.Model.NewsList
import com.example.htss.Retrofit.Model.StockPriceList
import com.example.htss.Retrofit.RetrofitClient
import com.example.htss.databinding.FragmentStockBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StockFragment : Fragment(), View.OnClickListener {
    var newsNum = 3
    private lateinit var view: FragmentStockBinding
    private val retrofit = RetrofitClient.create()


    private var StockNewsList = arrayListOf<NewsModel>()

    private var StockNewsListAdapter = MainNewsAdapter(StockNewsList)

    private var StockTicker = ""
    private var Stockname = ""
    private var StockPrice = ""
    private var StockPercent = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentStockBinding.inflate(inflater, container, false)

        StockTicker = arguments?.getString("stock_ticker").toString()
//        Stockname = arguments?.getString("stock_name").toString()
//        StockPrice= arguments?.getString("stock_price").toString()
//        StockPercent = arguments?.getString("stock_percent").toString()

//        view.ticker.text = StockTicker
//        view.stockName.text = Stockname
//        view.stockName2.text = Stockname
//        view.stockCurrent.text = StockPrice
//
//        if(StockPercent.substring(0,1) == "+"){
//            view.stockPlusPercent.text = StockPercent
//            view.stockMinusPercent.visibility = View.GONE
//            view.stockPlusPercent.visibility = View.VISIBLE
//        }
//
//        if(StockPercent.substring(0,1) == "-"){
//            view.stockMinusPercent.text = StockPercent
//            view.stockPlusPercent.visibility = View.GONE
//            view.stockMinusPercent.visibility = View.VISIBLE
//        }

        view.newsRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = StockNewsListAdapter

        }

        view.back.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

       StockNewsListAdapter.setItemClickListener(object : MainNewsAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(StockNewsList[position].rink)))
            }

        })
        view.newsCloseBtn.setOnClickListener(this)
        view.newsOpenBtn.setOnClickListener(this)
        view.stockSearchBtn.setOnClickListener(this)

        getSectorThemeKeywordIncludeNews(view.stockName.toString(),3)
        getStockNowPrice(StockTicker,1)


        return view.root
    }

    private fun init(body: StockPriceList) {
        view.ticker.text = StockTicker
        view.stockName.text = body[0].company_name
        view.stockName2.text = body[0].company_name
        view.stockCurrent.text = "${body[0].end_price}원"

        if(body[0].rate >= 0.0){
            view.stockPlusPercent.apply{
                setTextColor(ContextCompat.getColor(requireContext(),R.color.blue))
                text = "+${body[0].rate}%"
            }
        } else {
            view.stockPlusPercent.apply{
                setTextColor(ContextCompat.getColor(requireContext(),R.color.red))
                text = "${body[0].rate}%"
            }
        }
        getSectorThemeKeywordIncludeNews(body[0].company_name, newsNum)
    }
    fun getStockNowPrice(ticker: String,num: Int){
        retrofit.getStockNowPrice(ticker,1).enqueue(object : Callback<StockPriceList> {
            override fun onResponse(
                call: Call<StockPriceList>,
                response: Response<StockPriceList>
            ) {
                if(response.code()==200) {
                    if(!response.body().isNullOrEmpty()) init(response.body()!!)
                    // addStockNowPriceList(response.body())
                    Log.d("API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StockPriceList>, t: Throwable) {
                Log.d("API호출2", t.message.toString())
            }
        })
    }

    private fun addStockNowPriceList(body: StockPriceList?){
        if(body != null) {
            for (item in body) {
                if(item.rate >= 0.0) {
                    val item = StockListModel(
                        item.ticker,
                        item.company_name,
                        item.end_price.toString(),
                        "+"+item.rate.toString()+"%"
                    )
                }
                else{
                    val item = StockListModel(
                        item.ticker,
                        item.company_name,
                        item.end_price.toString(),
                        item.rate.toString()+"%"
                    )
                }
            }
        }
    }


    fun getSectorThemeKeywordIncludeNews(keyword: String, num: Int){
        retrofit. getSectorThemeKeywordIncludeNews(keyword,num).enqueue(object : Callback<KeywordIncludeNewsList> {
            override fun onResponse(
                call: Call<KeywordIncludeNewsList>,
                response: Response<KeywordIncludeNewsList>
            ) {
                if(response.code()==200) {
                    addSectorthemeIncludeNewsList(response.body())
                    Log.d("API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onFailure(call: Call<KeywordIncludeNewsList>, t: Throwable) {
                Log.d("API호출2", t.message.toString())
            }
        })
    }
    private fun addSectorthemeIncludeNewsList(body: KeywordIncludeNewsList?){
        StockNewsList.clear()
        if(body.isNullOrEmpty()){

        }
        else{
            for(item in body){
                StockNewsList.add(NewsModel("관련 종목코드: "+item.ticker,item.provider,item.date,item.rink,item.title))
            }
        }
        StockNewsListAdapter.notifyDataSetChanged()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.news_open_btn -> {
                getSectorThemeKeywordIncludeNews(view.stockName.toString(),10)
                view.newsCloseBtn.visibility = View.VISIBLE
                view.newsOpenBtn.visibility = View.GONE

            }
            R.id.news_close_btn -> {
                getSectorThemeKeywordIncludeNews(view.stockName.toString(),3)
                view.newsCloseBtn.visibility = View.GONE
                view.newsOpenBtn.visibility = View.VISIBLE

            }

        }
    }














}