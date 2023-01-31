package com.example.htss.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.htss.Adapter.CategoryDetailListAdapter
import com.example.htss.Model.CategoryDetailListModel
import com.example.htss.R
import com.example.htss.databinding.FragmentCategoryDetailBinding


class CategoryDetailFragment : Fragment(), View.OnClickListener {

    private lateinit var view: FragmentCategoryDetailBinding

    private val CategoryDetailList = arrayListOf<CategoryDetailListModel>(
        CategoryDetailListModel("오브젠", "+30%", 9200),
        CategoryDetailListModel("코난테크놀로지","+25%", 13000),
        CategoryDetailListModel("이노룰스", "-13%", 104450)
    )

    private val categorydetailAdapter = CategoryDetailListAdapter(CategoryDetailList)

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


       categorydetailAdapter.setItemClickListener(object : CategoryDetailListAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                Log.d("주식", CategoryDetailList[position].CatagoryName)
                val bundle = Bundle()
                bundle.apply {
                    putString("category_detail_name", CategoryDetailList[position].CatagoryName)
                    putString("category_detail_percent",CategoryDetailList[position].CatagoryPercent)
                    putInt("category_detail_price", CategoryDetailList[position].CatagoryPrice)
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