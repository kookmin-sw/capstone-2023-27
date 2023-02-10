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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.htss.Adapter.CategoryDetailListAdapter
import com.example.htss.Adapter.MainNewsAdapter
import com.example.htss.Model.CategoryDetailListModel
import com.example.htss.Model.NewsModel
import com.example.htss.R
import com.example.htss.Retrofit.Model.KeywordIncludeNewsList
import com.example.htss.Retrofit.Model.SectorThemeIncludeList
import com.example.htss.Retrofit.Model.SectorThemeList
import com.example.htss.Retrofit.RetrofitClient
import com.example.htss.databinding.FragmentCategoryDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CategoryDetailFragment : Fragment(), View.OnClickListener {

    private lateinit var view: FragmentCategoryDetailBinding
    private val retrofit = RetrofitClient.create()

    private val CategoryDetailList = mutableListOf<CategoryDetailListModel>()

    private var CategoryDetailNewsList = mutableListOf<NewsModel>()

    private val categorydetailAdapter = CategoryDetailListAdapter(CategoryDetailList)
    private val categorydetailNewsAdapter = MainNewsAdapter(CategoryDetailNewsList)

    private var categoryName = ""
    private var categoryPercent = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = FragmentCategoryDetailBinding.inflate(inflater, container, false)

        categoryName = arguments?.getString("category_name").toString()
        categoryPercent = arguments?.getString("category_percent").toString()

        view.categoryName.text = categoryName
        view.categoryName2.text = categoryName

        if(categoryPercent.substring(0,1) == "+"){
            view.categoryValuePlus.text = categoryPercent
            view.categoryValueMinus.visibility = View.GONE
            view.categoryValuePlus.visibility = View.VISIBLE
        }

        if(categoryPercent.substring(0,1) == "-"){
            view.categoryValueMinus.text = categoryPercent
            view.categoryValueMinus.visibility = View.VISIBLE
            view.categoryValuePlus.visibility = View.GONE
        }

        view.recycleCategoryDetail1.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = categorydetailAdapter
        }
        view.recycleCategoryDetail2.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = categorydetailNewsAdapter
        }


       categorydetailAdapter.setItemClickListener(object : CategoryDetailListAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val bundle = Bundle()
                bundle.apply {
                    putString("stock_name", CategoryDetailList[position].CatagoryName)
                    putString("stock_percent",CategoryDetailList[position].CatagoryPercent)
                    putString("stock_price", CategoryDetailList[position].CatagoryPrice)
                }
                replaceFragment(StockFragment(), bundle)
            }
        })
        categorydetailNewsAdapter.setItemClickListener(object : MainNewsAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(CategoryDetailNewsList[position].rink)))
            }
        })

        view.back.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        view.categoryDetailOpenBtn.setOnClickListener(this)
        view.categoryDetailCloseBtn.setOnClickListener(this)
        view.categoryIncludeNewsCloseBtn.setOnClickListener(this)
        view.categoryIncludeNewsOpenBtn.setOnClickListener(this)

        getSectorInclude(categoryName,3)
        getSectorThemeKeywordIncludeNews(categoryName,3)

        return view.root
    }
    fun getSectorInclude(sector: String, num: Int){
        retrofit.getSectorInclude(sector, num).enqueue(object: Callback<SectorThemeIncludeList> {
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
        CategoryDetailList.clear()
        if(body != null){
            for(item in body) {
                if (item.rate >= 0.0) {
                    CategoryDetailList.add(
                        CategoryDetailListModel(
                            item.company_name,
                            item.end_price.toString(),
                            "+" + item.rate.toString() + "%"
                        )
                    )
                }
                else{
                    CategoryDetailList.add(
                        CategoryDetailListModel(
                            item.company_name,
                            item.end_price.toString(),
                            item.rate.toString() + "%"
                        )
                    )
                }
            }
        }
        categorydetailAdapter.notifyDataSetChanged()
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
        CategoryDetailNewsList.clear()
        if(body.isNullOrEmpty()){

        }
        else{
            for(item in body){
                CategoryDetailNewsList.add(NewsModel("관련 종목코드: "+item.ticker,item.provider,item.date,item.rink,item.title))
            }
            categorydetailNewsAdapter.notifyDataSetChanged()
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
        when(p0?.id){
            R.id.category_detail_open_btn -> {
                getSectorInclude(categoryName, 10)
                view.categoryDetailOpenBtn.visibility = View.GONE
                view.categoryDetailCloseBtn.visibility=View.VISIBLE
            }
            R.id.category_detail_close_btn->{
                getSectorInclude(categoryName,3)
                view.categoryDetailOpenBtn.visibility = View.VISIBLE
                view.categoryDetailCloseBtn.visibility=View.GONE
            }
            R.id.category_include_news_open_btn->{
                getSectorThemeKeywordIncludeNews(categoryName,10)
                view.categoryIncludeNewsOpenBtn.visibility = View.GONE
                view.categoryIncludeNewsCloseBtn.visibility = View.VISIBLE
            }
            R.id.category_include_news_close_btn -> {
                getSectorThemeKeywordIncludeNews(categoryName,3)
                view.categoryIncludeNewsOpenBtn.visibility = View.VISIBLE
                view.categoryIncludeNewsCloseBtn.visibility = View.GONE
            }
        }
    }
}