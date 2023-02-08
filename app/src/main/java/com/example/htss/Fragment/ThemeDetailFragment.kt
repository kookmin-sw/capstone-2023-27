package com.example.htss.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.htss.Adapter.MainNewsAdapter
import com.example.htss.Adapter.ThemeDetailListAdapter
import com.example.htss.Model.CategoryDetailListModel
import com.example.htss.Model.NewsModel
import com.example.htss.Model.ThemeDetailListModel
import com.example.htss.R
import com.example.htss.databinding.FragmentThemeDetailBinding


class ThemeDetailFragment : Fragment(),View.OnClickListener {
    private lateinit var view: FragmentThemeDetailBinding

    private var ThemeDetailList = arrayListOf<ThemeDetailListModel>(
        ThemeDetailListModel("신세계건설", "+30%","9200"),
        ThemeDetailListModel("시공테크","+25%", "13000"),
        ThemeDetailListModel("이월드", "-100%", "104450")
    )
    private var ThemeNewsList = arrayListOf<NewsModel>()


    private var themeDetailListAdapter = ThemeDetailListAdapter(ThemeDetailList)
    private  var themeNewslListAdapter = MainNewsAdapter(ThemeNewsList)

    private var themename = ""
    private var themepercent = ""

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
        }

        view.back2.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        themeDetailListAdapter.setItemClickListener(object : ThemeDetailListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                Log.d("주식", ThemeDetailList[position].ThemeName)
                val bundle = Bundle()
                bundle.apply {
                    putString("stock_name", ThemeDetailList[position].ThemeName)
                    putString("stock_percent", ThemeDetailList[position].Themepercent)
                    putString("stock_price", ThemeDetailList[position].ThemePrice)
                }
                replaceFragment(StockFragment(), bundle)
            }
        })
        view.back2.setOnClickListener {
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