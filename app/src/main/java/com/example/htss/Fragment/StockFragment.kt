package com.example.htss.Fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.htss.Adapter.MainNewsAdapter
import com.example.htss.Model.NewsModel
import com.example.htss.R
import com.example.htss.Retrofit.Model.*
import com.example.htss.Retrofit.RetrofitClient
import com.example.htss.databinding.FragmentStockBinding
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_stock.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StockFragment : Fragment(), View.OnClickListener {
    val spinnerList = arrayOf("키워드","종목번호","종목명")
    var selectedPosition = 0
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
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, spinnerList)
        view.searchSpinner.apply{
            setSelection(0)
            adapter = spinnerAdapter
            onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                    selectedPosition = position
                    Log.d("selectedPosition", selectedPosition.toString())
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

            }
        }



        StockTicker = arguments?.getString("stock_ticker").toString()
//
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


        setListenerToEditText()
        getSectorThemeKeywordIncludeNews(view.stockName.toString(),3)
        getStockNowPrice(StockTicker)
        getCompanyInfo(StockTicker)


        return view.root
    }

    fun getCompanyInfo(ticker: String){
        retrofit.getCompanyInfo(ticker).enqueue(object : Callback<CompanyInfo> {
            override fun onResponse(
                call: Call<CompanyInfo>,
                response: Response<CompanyInfo>
            ) {
                if(response.code()==200) {
                    if(!response.body().isNullOrEmpty()) addcompanyInfo(response.body()!!)
                    Log.d("stock/info/API호출!!!!!!", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<CompanyInfo>, t: Throwable) {
                Log.d("API호출2", t.message.toString())
            }
        })
    }
    private fun addcompanyInfo(body: CompanyInfo) {
        view.stockName.text = body[0].company_name
        view.stockName2.text = body[0].company_name
        view.stockInfo.text = body[0].company_info
        getSectorThemeKeywordIncludeNews(body[0].company_name, newsNum)
    }

    fun getStockNowPrice(ticker: String){
        retrofit.getStockNowPrice(ticker).enqueue(object : Callback<StockNowPriceListItem> {
            override fun onResponse(
                call: Call<StockNowPriceListItem>,
                response: Response<StockNowPriceListItem>
            ) {
                if(response.code()==200) {
                    if(response.body()!=null)
                        addStockNowPrice(response.body()!!)
                    Log.d("stock/now-price/API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StockNowPriceListItem>, t: Throwable) {
                Log.d("API호출2222222", t.message.toString())
            }
        })
    }
    private fun addStockNowPrice(body: StockNowPriceListItem) {
        view.ticker.text = StockTicker
        view.stockCurrent.text = body.end_price.toString()
        if(body.rate >= 0.0){
            view.stockPercent.apply{
                setTextColor(ContextCompat.getColor(requireContext(),R.color.blue))
                text = "+"+body.rate.toString()+"%"
            }
        } else {
            view.stockPercent.apply{
                setTextColor(ContextCompat.getColor(requireContext(),R.color.red))
                text = body.rate.toString()+"%"
            }
        }
    }


    fun getTickerByStockName(name: String){
        retrofit.getTickerByStockName(name).enqueue(object: Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.code() == 200){
                    if(!response.body().isNullOrBlank()){
                        val bundle = Bundle()
                        bundle.putString("stock_ticker", response.body())
                        replaceFragment(StockFragment(), bundle)
                    } else {
                        Toast.makeText(requireContext(),"일치하는 종목이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else Toast.makeText(requireContext(),"오류가 발생하였습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()

            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(requireContext(),"오류가 발생하였습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getStockNameByTicker(ticker: String){
        retrofit.getStockNameByTicker(ticker).enqueue(object: Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.code() == 200){
                    if(!response.body().isNullOrBlank()){
                        val bundle = Bundle()
                        bundle.putString("stock_ticker", ticker)
                        replaceFragment(StockFragment(), bundle)
                    } else {
                        Toast.makeText(requireContext(),"일치하는 종목이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else Toast.makeText(requireContext(),"오류가 발생하였습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()

            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(requireContext(),"일치하는 종목이 없습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }

        })
    }


    fun getSectorThemeKeywordIncludeNews(keyword: String, num: Int){
        retrofit. getSectorThemeKeywordIncludeNews(keyword,num).enqueue(object : Callback<KeywordIncludeNewsList> {
            override fun onResponse(
                call: Call<KeywordIncludeNewsList>,
                response: Response<KeywordIncludeNewsList>
            ) {
                if(response.code()==200) {
                    addSectorthemeIncludeNewsList(response.body())
                    Log.d("stock/news/like/ API호출", response.raw().toString())
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
            R.id.stock_search_btn -> {
                when(selectedPosition){
                    1 -> { // 종목번호
                        getStockNameByTicker(view.stockKeywordEdit.text.toString().trim())
                    }
                    2 -> { // 종목명
                        getTickerByStockName(view.stockKeywordEdit.text.toString().trim())
                    }
                    else -> { // 키워드, 업종, 테마
                        val bundle = Bundle()
                        bundle.putString("keyword", view.stockKeywordEdit.text.toString().trim())
                        bundle.putInt("type", selectedPosition)
                        replaceFragment(KeyWordFragment(), bundle)
                    }
                }
                view.stockKeywordEdit.text = null
            }

        }
    }

    private fun replaceFragment(fragment: Fragment, bundle: Bundle) {
        fragment.arguments = bundle
        Log.d("argument", bundle.toString())
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrameLayout, fragment)
            .addToBackStack(null)
            .commit()
    }
    // 엔터치면 키보드 내리기
    private fun setListenerToEditText() {
        view.stockKeywordEdit.setOnKeyListener { view, keyCode, event ->
            // Enter Key Action
            if (event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                // 키패드 내리기
                val imm =
                    ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.stock_keyword_edit.windowToken, 0)
                }
                // Toast Message
                showToastMessage(view.stock_keyword_edit.text.toString())
                true
            }
            false
        }
    }
    ////////////////////////////
    private fun showToastMessage(msg: String?) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}