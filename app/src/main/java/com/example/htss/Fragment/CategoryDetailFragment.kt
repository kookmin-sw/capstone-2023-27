package com.example.htss.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.htss.Adapter.CategoryDetailListAdapter
import com.example.htss.Adapter.MainNewsAdapter
import com.example.htss.Model.CategoryDetailListModel
import com.example.htss.Model.NewsModel
import com.example.htss.R
import com.example.htss.databinding.FragmentCategoryDetailBinding


class CategoryDetailFragment : Fragment(), View.OnClickListener {

    private lateinit var view: FragmentCategoryDetailBinding

    private val CategoryDetailList = arrayListOf<CategoryDetailListModel>(
        CategoryDetailListModel("오브젠", "+30%", "9200"),
        CategoryDetailListModel("코난테크놀로지","+25%", "13000"),
        CategoryDetailListModel("이노룰스", "-13%", "104450")
    )

    private var CategoryDetailNewsList = arrayListOf<NewsModel>(
        NewsModel("뉴스뉴스뉴스"),
        NewsModel("add add add"),
        NewsModel("추추가가추추가가")
    )

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

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }

}