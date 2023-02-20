package com.example.htss.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.htss.Adapter.ThemeListAdapter
import com.example.htss.Model.CategorylistModel
import com.example.htss.Model.ThemelistModel
import com.example.htss.R
import com.example.htss.Retrofit.Model.SectorThemeList
import com.example.htss.Retrofit.RetrofitClient
import com.example.htss.databinding.FragmentThemeListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ThemeListFragment : Fragment() {


    private lateinit var view: FragmentThemeListBinding

    private val retrofit = RetrofitClient.create()
    private val themeList = arrayListOf<ThemelistModel>()
    private var ThemaFocus =""
    private val themeListAdapter = ThemeListAdapter(themeList)

    private var num = 20

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentThemeListBinding.inflate(inflater, container, false)
        getHighThemeList(num)
        view.recycle5.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = themeListAdapter
            addItemDecoration(
                DividerItemDecoration(
                    view.recycle5.context,
                    LinearLayoutManager(context).orientation
                )
            )


            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() // 화면에 보이는 마지막 아이템의 position
                    val itemTotalCount =
                        recyclerView.adapter!!.itemCount - 1 // 어댑터에 등록된 아이템의 총 개수 -1

                    //최하단에 도달 1
                    if (!view.recycle5.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount && itemTotalCount + 1 >= num) {
                        num += 20
                        getHighThemeList(num)
                    }
                }
            })
        }

        ThemaFocus = arguments?.getString("foccus").toString()

        if(ThemaFocus == "hue"){
            view.recycle5.isFocusableInTouchMode = true;
            view.recycle5.requestFocus()
        }


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

    fun getHighThemeList(num: Int){
        retrofit.getHighThemeList(num).enqueue(object: Callback<SectorThemeList> {
            override fun onResponse(
                call: Call<SectorThemeList>,
                response: Response<SectorThemeList>
            ) {
                if(response.code()==200) {
                    addResultSectorThemeHighList("theme",response.body())
                    Log.d("API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SectorThemeList>, t: Throwable) {
                Log.d("API호출2", t.message.toString())
            }
        })
    }

    private fun addResultSectorThemeHighList(name:String, body: SectorThemeList?) {
        when(name){
            "theme" -> {
                themeList.clear()
                if(body != null) {
                    for(item in body) {
                        Log.d("API결과",item.toString())
                        if(item.rate >= 0.0){
                            themeList.add(ThemelistModel(item.keyword, "+"+item.rate.toString()+"%"))
                        } else {
                            themeList.add(ThemelistModel(item.keyword, item.rate.toString()+"%"))
                        }
                    }
                }
                themeListAdapter.notifyDataSetChanged()
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

}