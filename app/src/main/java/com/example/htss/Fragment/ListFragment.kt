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
import com.example.htss.Adapter.ThemeListAdapter
import com.example.htss.Model.CategorylistModel
import com.example.htss.Model.ThemelistModel
import com.example.htss.R
import com.example.htss.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private lateinit var view: FragmentListBinding

    private val categoryList = arrayListOf<CategorylistModel>(
        CategorylistModel("전기제품1","+100%"),
        CategorylistModel("전자제품1", "+50%"),
        CategorylistModel("가전제품1","-10%"),
        CategorylistModel("전기제품2","+100%"),
        CategorylistModel("전자제품2", "+50%"),
        CategorylistModel("가전제품2","-10%"),
        CategorylistModel("전기제품3","+100%"),
        CategorylistModel("전자제품3", "+50%"),
        CategorylistModel("가전제품3","-10%"),
        CategorylistModel("전기제품4","+100%"),
        CategorylistModel("전자제품4", "+50%"),
        CategorylistModel("가전제품4","-10%"),
        CategorylistModel("전기제품5","+100%"),
        CategorylistModel("전자제품5", "+50%"),
        CategorylistModel("가전제품5","-10%"),
        CategorylistModel("전기제품6","+100%"),
        CategorylistModel("전자제품6", "+50%"),
        CategorylistModel("가전제품6","-10%")
    )

    private val themeList = arrayListOf<ThemelistModel>(
        ThemelistModel("테마명1", "+100%"),
        ThemelistModel("테마명2", "+70%"),
        ThemelistModel("테마명3", "+60%"),
        ThemelistModel("테마명4", "+50%"),
        ThemelistModel("테마명5", "+30%"),
        ThemelistModel("테마명6", "+10%"),
        ThemelistModel("테마명7", "-10%"),
        ThemelistModel("테마명8", "-20%"),
        ThemelistModel("테마명9", "-30%"),
        ThemelistModel("테마명10", "-40%"),
        ThemelistModel("테마명11", "-45%"),
        ThemelistModel("테마명12", "-50%"),
        ThemelistModel("테마명13", "-60%"),
        ThemelistModel("테마명14", "-70%"),
        ThemelistModel("테마명15", "-80%"),
        ThemelistModel("테마명16", "-90%"),
    )

    private var ThemaFocus =""


    private val categoryListAdapter = CategoryListAdapter(categoryList)
    private val themeListAdapter = ThemeListAdapter(themeList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentListBinding.inflate(inflater, container, false)

        view.recycle4.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = categoryListAdapter
        }

        view.recycle5.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = themeListAdapter
        }

        ThemaFocus = arguments?.getString("foccus").toString()
        if(ThemaFocus == "hue"){
            view.themaList.isFocusableInTouchMode = true;
            view.themaList.requestFocus()
        }

        view.back.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        categoryListAdapter.setItemClickListener(object : CategoryListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val bundle = Bundle()
                bundle.apply {
                    putString("category_name", categoryList[position].categoryName)
                    putString("category_percent", categoryList[position].percent)
                }
                replaceFragment(CategoryDetailFragment(), bundle)
            }
        })

        themeListAdapter.setItemClickListener(object : ThemeListAdapter.OnItemClickListener{
            override fun onClick(v:View, position: Int){
                val bundle = Bundle()
                bundle.apply{
                    bundle.putString("theme_name", themeList[position].themeName)
                    bundle.putString("theme_percent", themeList[position].percent)
                }
                replaceFragment(ThemeDetailFragment(),bundle)

            }
        })

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