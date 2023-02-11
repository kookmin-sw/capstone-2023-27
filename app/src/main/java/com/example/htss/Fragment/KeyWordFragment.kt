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
import com.example.htss.Adapter.CategoryListAdapter
import com.example.htss.Adapter.HomeAdapter
import com.example.htss.Adapter.KeywordRelatedStockAdapter
import com.example.htss.Adapter.MainNewsAdapter
import com.example.htss.Model.CategorylistModel
import com.example.htss.Model.KeywordRelatedStockModel
import com.example.htss.Model.MainModel
import com.example.htss.Model.NewsModel
import com.example.htss.R
import com.example.htss.Retrofit.Model.KeywordIncludeNewsList
import com.example.htss.Retrofit.Model.NewsList
import com.example.htss.Retrofit.Model.NounMatchingStockList
import com.example.htss.Retrofit.Model.SectorThemeLikeList
import com.example.htss.Retrofit.RetrofitClient
import com.example.htss.databinding.FragmentKeyWordBinding
import kotlinx.android.synthetic.main.fragment_key_word.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class KeyWordFragment : Fragment(), View.OnClickListener {
    private lateinit var view: FragmentKeyWordBinding
    private val retrofit = RetrofitClient.create()
    private var KeywordCategoryList = mutableListOf<MainModel>()

    private var KeywordThemeList = mutableListOf<MainModel>()

    private var RelatedStockList = mutableListOf<KeywordRelatedStockModel>()

    private var RelatedNewsList = mutableListOf<NewsModel>()

    private var KeywordCategoryListAdapter = HomeAdapter(KeywordCategoryList)
    private var KeywordThemeListAdapter = HomeAdapter(KeywordThemeList)
    private var RelatedStockListAdapter = KeywordRelatedStockAdapter(RelatedStockList)
    private var RelatedNewsListAdapter = MainNewsAdapter(RelatedNewsList)


    private var KeyWordName = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentKeyWordBinding.inflate(inflater, container, false)

        view.categoryRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = KeywordCategoryListAdapter
        }

        view.themeRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = KeywordThemeListAdapter
        }

        view.stockRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = RelatedStockListAdapter
        }

        view.newsRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = RelatedNewsListAdapter
        }

        KeyWordName = arguments?.getString("keyword").toString()

        view.keywordName.text = KeyWordName
        view.keywordName2.text = KeyWordName
        view.keywordName3.text = KeyWordName

       KeywordCategoryListAdapter.setItemClickListener(object: HomeAdapter.OnItemClickListener{
           override fun onClick(v: View, position: Int) {
               val bundle = Bundle()
               bundle.apply {
                   bundle.putString("category_name", KeywordCategoryList[position].name)
                   bundle.putString("category_percent", KeywordCategoryList[position].percent)
               }
               replaceFragment(CategoryDetailFragment(),bundle)
           }
       })

        KeywordThemeListAdapter.setItemClickListener(object: HomeAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val bundle = Bundle()
                bundle.apply {
                    bundle.putString("theme_name", KeywordThemeList[position].name)
                    bundle.putString("theme_percent", KeywordThemeList[position].percent)
                }
                replaceFragment(ThemeDetailFragment(),bundle)
            }
        })

        RelatedStockListAdapter.setItemClickListener(object: KeywordRelatedStockAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val bundle = Bundle()
                bundle.apply {
                    bundle.putString("stock_name", RelatedStockList[position].Stockname)
                    bundle.putString("stock_price", RelatedStockList[position].Stockprice)
                    bundle.putString("stock_percent", RelatedStockList[position].Stockpercent)
                }
                replaceFragment(StockFragment(),bundle)
            }
        })
        RelatedNewsListAdapter.setItemClickListener(object : MainNewsAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(RelatedNewsList[position].rink)))
            }

        })

        view.back.setOnClickListener {
            parentFragmentManager.popBackStack()
        }



        getKeywordMatchingStock(KeyWordName,3) //키워드와 같이 언급된 주식 종목
        getSectorThemeKeywordIncludeNews(KeyWordName,3)// 키워드가 포함된 뉴스
        getSectorLikeKeyword(KeyWordName,3)// 키워드와 관련된 종목
        getThemeLikeKeyword(KeyWordName,3)// 키워드와 관련된 테마

        view.categoryOpenBtn.setOnClickListener(this)
        view.categoryCloseBtn.setOnClickListener(this)
        view.themeCloseBtn.setOnClickListener(this)
        view.themeOpenBtn.setOnClickListener(this)
        view.stockCloseBtn.setOnClickListener(this)
        view.stockOpenBtn.setOnClickListener(this)
        view.newsCloseBtn.setOnClickListener(this)
        view.newsOpenBtn.setOnClickListener(this)


        return view.root
    }

    fun getKeywordMatchingStock(noun: String, num: Int){
        retrofit. getKeywordMatchingStock(noun,num).enqueue(object : Callback<NounMatchingStockList> {
            override fun onResponse(
                call: Call<NounMatchingStockList>,
                response: Response<NounMatchingStockList>
            ) {
                if(response.code()==200) {
                    addKeywordMatchingStock(response.body())
                    Log.d("API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NounMatchingStockList>, t: Throwable) {
                Log.d("API호출2", t.message.toString())
            }
        })
    }
    private fun addKeywordMatchingStock(body: NounMatchingStockList?){
        RelatedStockList.clear()

        if(body.isNullOrEmpty()){

        }
        else{
            for(item in body) {
                if (item.rate >= 0.0) {
                    RelatedStockList.add(
                        KeywordRelatedStockModel(
                            item.company_name,
                            item.end_price.toString(),
                            "+"+item.rate.toString()+"%",
                            item.count.toString() + "회"
                        )
                    )
                }

                else{
                    RelatedStockList.add(
                        KeywordRelatedStockModel(
                            item.company_name,
                            item.end_price.toString(),
                            item.rate.toString()+"%",
                            item.count.toString() + "회"
                        )
                    )
                }
            }
            RelatedStockListAdapter.notifyDataSetChanged()
        }
    }
    fun getSectorLikeKeyword(keyword: String, num: Int){
        retrofit. getSectorLikeKeyword(keyword,num).enqueue(object : Callback<SectorThemeLikeList> {
            override fun onResponse(
                call: Call<SectorThemeLikeList>,
                response: Response<SectorThemeLikeList>
            ) {
                if(response.code()==200) {
                    addSectorLikeKeywordList(response.body())
                    Log.d("API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SectorThemeLikeList>, t: Throwable) {
                Log.d("API호출2", t.message.toString())
            }
        })
    }
    private fun addSectorLikeKeywordList(body: SectorThemeLikeList?){
        KeywordCategoryList.clear()
        Log.d("아아아","되라")

        if(body.isNullOrEmpty()){

        }
        else{
            for(item in body){
                if(item.rate >=0.0){
                    KeywordCategoryList.add(MainModel(item.keyword, "+"+item.rate.toString()+"%"))
                }
                else{
                    KeywordCategoryList.add(MainModel(item.keyword, item.rate.toString() + "%"))
                }
            }
        }
        KeywordCategoryListAdapter.notifyDataSetChanged()

    }

    fun getThemeLikeKeyword(keyword: String, num: Int){
        retrofit. getThemeLikeKeyword(keyword,num).enqueue(object : Callback<SectorThemeLikeList> {
            override fun onResponse(
                call: Call<SectorThemeLikeList>,
                response: Response<SectorThemeLikeList>
            ) {
                if(response.code()==200) {
                    addgetThemeLikeKeyword(response.body())
                    Log.d("API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SectorThemeLikeList>, t: Throwable) {
                Log.d("API호출2", t.message.toString())
            }
        })
    }
    private fun addgetThemeLikeKeyword(body: SectorThemeLikeList?){
        KeywordThemeList.clear()
        if(body!=null){
            for(item in body){
                if(item.rate >= 0.0) {
                    KeywordThemeList.add(MainModel(item.keyword, "+"+item.rate.toString()+"%"))
                }
                else{
                    KeywordThemeList.add(MainModel(item.keyword,item.rate.toString()+"%"))
                }
            }
        }
        KeywordThemeListAdapter.notifyDataSetChanged()
    }
//수정..
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
            //wpqkfgfgfgwpqkfehlfkxptmxmrtrtrtrtrtrtrt

            override fun onFailure(call: Call<KeywordIncludeNewsList>, t: Throwable) {
                Log.d("API호출2", t.message.toString())
            }
        })
    }
    private fun addSectorthemeIncludeNewsList(body: KeywordIncludeNewsList?){
        RelatedNewsList.clear()
        if(body.isNullOrEmpty()){

        }
        else{
            for(item in body){
                RelatedNewsList.add(NewsModel("관련 종목코드: "+item.ticker,item.provider,item.date,item.rink,item.title))
            }
        }
        RelatedNewsListAdapter.notifyDataSetChanged()
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
            R.id.category_close_btn->{
                getSectorLikeKeyword(KeyWordName,3)
                view.categoryOpenBtn.visibility = View.VISIBLE
                view.categoryCloseBtn.visibility = View.GONE
            }
            R.id.category_open_btn->{
                getSectorLikeKeyword(KeyWordName,10)
                view.categoryOpenBtn.visibility = View.GONE
                view.categoryCloseBtn.visibility = View.VISIBLE

            }
            R.id.theme_close_btn->{
                getThemeLikeKeyword(KeyWordName,3)
                view.themeOpenBtn.visibility =  View.VISIBLE
                view.themeCloseBtn.visibility = View.GONE

            }
            R.id.theme_open_btn->{
                getThemeLikeKeyword(KeyWordName,10)
                view.themeOpenBtn.visibility =  View.GONE
                view.themeCloseBtn.visibility = View.VISIBLE

            }
            R.id.stock_close_btn ->{
                getKeywordMatchingStock(KeyWordName,3)
                view.stockCloseBtn.visibility = View.GONE
                view.stockOpenBtn.visibility = View.VISIBLE
            }
            R.id.stock_open_btn->{
                getKeywordMatchingStock(KeyWordName,10)
                view.stockCloseBtn.visibility = View.VISIBLE
                view.stockOpenBtn.visibility = View.GONE
            }
            R.id.news_close_btn -> {
                getSectorThemeKeywordIncludeNews(KeyWordName,3)
                view.newsCloseBtn.visibility = View.GONE
                view.newsOpenBtn.visibility = View.VISIBLE

            }
            R.id.news_open_btn ->{
                getSectorThemeKeywordIncludeNews(KeyWordName,10)
                view.newsCloseBtn.visibility = View.VISIBLE
                view.newsOpenBtn.visibility = View.GONE
            }
        }
    }
}