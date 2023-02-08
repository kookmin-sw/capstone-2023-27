package com.example.htss.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.htss.databinding.FragmentKeyWordBinding


class KeyWordFragment : Fragment() {
    private lateinit var view: FragmentKeyWordBinding

    private var KeywordCategoryList = arrayListOf<MainModel>(
        MainModel("업종1", "+10%" ),
        MainModel("업종2","-5.8%"),
        MainModel("업종3","-10%")
    )

    private var KeywordThemeList = arrayListOf<MainModel>(
        MainModel("테마1", "-2%" ),
        MainModel("테마2","-5.8%"),
        MainModel("테마3","-10%")
    )

    private var RelatedStockList = arrayListOf<KeywordRelatedStockModel>(
        KeywordRelatedStockModel( "삼성전자","9200","+85%","100회"),
        KeywordRelatedStockModel( "SK하이닉스","8000","+10%","30회"),
        KeywordRelatedStockModel( "현대자동차","2000","-50%","10회"),
    )

    private var RelatedNewsList = arrayListOf<NewsModel>()

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



        view.back.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view.root
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
}