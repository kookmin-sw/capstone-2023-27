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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.htss.Adapter.MainNewsAdapter
import com.example.htss.Adapter.ThemeDetailListAdapter
import com.example.htss.Model.CategoryDetailListModel
import com.example.htss.Model.NewsModel
import com.example.htss.Model.StockListModel
import com.example.htss.Model.ThemeDetailListModel
import com.example.htss.R
import com.example.htss.Retrofit.Model.KeywordIncludeNewsList
import com.example.htss.Retrofit.Model.SectorThemeIncludeList
import com.example.htss.Retrofit.RetrofitClient
import com.example.htss.databinding.FragmentThemeDetailBinding
import kotlinx.android.synthetic.main.fragment_all_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat


class ThemeDetailFragment : Fragment(),View.OnClickListener {
    private lateinit var view: FragmentThemeDetailBinding
    private val retrofit = RetrofitClient.create()
    private var ThemeDetailList = mutableListOf<StockListModel>()
    private var ThemeNewsList = mutableListOf<NewsModel>()


    private var themeDetailListAdapter = ThemeDetailListAdapter(ThemeDetailList)
    private  var themeNewslListAdapter = MainNewsAdapter(ThemeNewsList)

    private var themename = ""
    private var themepercent = ""
    val dec = DecimalFormat("#,###.##")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = FragmentThemeDetailBinding.inflate(inflater, container, false)


        themename = arguments?.getString("theme_name").toString()
        themepercent = arguments?.getString("theme_percent").toString()

        view.themeName.text = themename
        view.themeName2.text = themename


        if(themepercent.substring(0,1) == "+"){
            view.themeValuePlus.text = themepercent
            view.themeValueMinus.visibility = View.GONE
            view.themeValuePlus.visibility = View.VISIBLE
        }

        if(themepercent.substring(0,1) == "-"){
            view.themeValueMinus.text = themepercent
            view.themeValueMinus.visibility = View.VISIBLE
            view.themeValuePlus.visibility = View.GONE
        }
        view.themeDetail1.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = themeDetailListAdapter
        }
        view.themeDetail2.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = themeNewslListAdapter
            addItemDecoration(
                DividerItemDecoration(
                    view.themeDetail2.context,
                    LinearLayoutManager(context).orientation
                )
            )
        }

        view.back2.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        themeDetailListAdapter.setItemClickListener(object : ThemeDetailListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                Log.d("주식", ThemeDetailList[position].StockName)
                val bundle = Bundle()
                bundle.apply {
                    putString("stock_ticker", ThemeDetailList[position].ticker)
                    putString("stock_name", ThemeDetailList[position].StockName)
                    putString("stock_percent", ThemeDetailList[position].StockPercent)
                    putString("stock_price", ThemeDetailList[position].StockPrice)
                }
                replaceFragment(StockFragment(), bundle)
            }
        })

        themeNewslListAdapter.setItemClickListener(object : MainNewsAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ThemeNewsList[position].rink)))
            }
        })

        themeNewslListAdapter.setLinkClickListener(object : MainNewsAdapter.OnLinkClickListener{
            override fun onClick(v: View, position: Int) {
                getStockNameByTicker(ThemeNewsList[position].ticker)
            }
        })

        getThemeInclude(themename,3)
        getSectorThemeKeywordIncludeNews(themename,3)

        view.themeDetailCloseBtn.setOnClickListener(this)
        view.themeDetailOpenBtn.setOnClickListener(this)
        view.themeIncludeNewsOpenBtn.setOnClickListener(this)
        view.themeIncludeNewsCloseBtn.setOnClickListener(this)

        return view.root
    }

    fun getThemeInclude(theme: String, num: Int){
        retrofit.getThemeInclude(theme, num).enqueue(object: Callback<SectorThemeIncludeList> {
            override fun onResponse(
                call: Call<SectorThemeIncludeList>,
                response: Response<SectorThemeIncludeList>
            ) {
                if(response.code()==200) {
                    addSectorthemeIncludeList(response.body())
                    Log.d("API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<SectorThemeIncludeList>, t: Throwable) {
                Log.d("API호출2", t.message.toString())
            }
        })
    }
    private fun addSectorthemeIncludeList(body: SectorThemeIncludeList?){
        ThemeDetailList.clear()
        if(body.isNullOrEmpty()){

        }
        else{
            for(item in body) {
                if (item.rate >= 0.0) {
                    ThemeDetailList.add(
                        StockListModel(
                            item.ticker,
                            item.company_name,
                            dec.format(item.end_price).toString(),
                            "+" + item.rate.toString() + "%"
                        )
                    )
                }
                else{
                    ThemeDetailList.add(
                        StockListModel(
                            item.ticker,
                            item.company_name,
                            dec.format(item.end_price).toString(),
                            item.rate.toString()+"%"
                        )
                    )
                }
            }
        }
        themeDetailListAdapter.notifyDataSetChanged()
        if(ThemeDetailList.size in 1..2 ){
            view.themeDetailOpenBtn.visibility = View.GONE
        }
        else if(ThemeDetailList.size == 0){
            view.themeDetailOpenBtn.visibility = View.GONE
            view.themeDetailNoBtn.visibility = View.VISIBLE
        }
    }
    fun getStockNameByTicker(ticker: String){
        retrofit.getStockNameByTicker(ticker).enqueue(object: Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.code() == 200){
                    if(!response.body().isNullOrBlank()){
                        val bundle = Bundle()
                        bundle.putString("stock_ticker", ticker)
                        bundle.putString("stock_name", response.body())
                        Log.d("keywordfragment",response.body().toString())
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
        retrofit.getSectorThemeKeywordIncludeNews(keyword, num).enqueue(object: Callback<KeywordIncludeNewsList> {
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
        ThemeNewsList.clear()
        if(body.isNullOrEmpty()){
        }
        else{
            for(item in body){
                ThemeNewsList.add(NewsModel(item.ticker,item.provider,item.date,item.rink,item.title,item.sentiment))
            }
        }
        themeNewslListAdapter.notifyDataSetChanged()
        if(ThemeNewsList.size in 1..2){
            view.themeIncludeNewsOpenBtn.visibility = View.GONE
        }
        else if(ThemeNewsList.size == 0){
            view.themeIncludeNewsOpenBtn.visibility = View.GONE
            view.themeIncludeNewsNoBtn.visibility = View.VISIBLE
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

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.theme_detail_open_btn -> {
                getThemeInclude(themename, 10)
                view.themeDetailCloseBtn.visibility = View.VISIBLE
                view.themeDetailOpenBtn.visibility = View.GONE
            }
            R.id.theme_detail_close_btn -> {
                getThemeInclude(themename, 3)
                view.themeDetailCloseBtn.visibility = View.GONE
                view.themeDetailOpenBtn.visibility = View.VISIBLE
            }
            R.id.theme_include_news_open_btn -> {
                getSectorThemeKeywordIncludeNews(themename,10)
                view.themeIncludeNewsCloseBtn.visibility = View.VISIBLE
                view.themeIncludeNewsOpenBtn.visibility = View.GONE
            }
            R.id.theme_include_news_close_btn -> {
                getSectorThemeKeywordIncludeNews(themename,3)
                view.themeIncludeNewsCloseBtn.visibility = View.GONE
                view.themeIncludeNewsOpenBtn.visibility = View.VISIBLE
            }
        }
    }
}