package com.example.htss.Fragment
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.htss.Adapter.MainNewsAdapter
import com.example.htss.Adapter.HomeAdapter
import com.example.htss.Model.MainModel
import com.example.htss.Model.NewsModel
import com.example.htss.R
import com.example.htss.Retrofit.Model.NewsList
import com.example.htss.Retrofit.Model.SectorThemeList
import com.example.htss.Retrofit.RetrofitClient
import com.example.htss.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var view: FragmentHomeBinding
    private val retrofit = RetrofitClient.create()
/////////////배열선언
    private val categoryRankList = mutableListOf<MainModel>()

    private val themeRankList = mutableListOf<MainModel>()

    private val newsRankList = mutableListOf<NewsModel>()
//////////////////////////어댑터에 배열선언
    private val categoryRankListAdapter = HomeAdapter(categoryRankList)
    private val themeRankListAdapter = HomeAdapter(themeRankList)
    private val newsRankListAdapter = MainNewsAdapter(newsRankList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        view = FragmentHomeBinding.inflate(inflater, container, false)
/////////리사이클러뷰에 어댑터 붙이기
        view.recycle1.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = categoryRankListAdapter
        }

        view.recycle2.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false)
            adapter = themeRankListAdapter

        }

        view.recycle3.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false)
            adapter = newsRankListAdapter
            addItemDecoration(
                DividerItemDecoration(
                    view.recycle3.context,
                    LinearLayoutManager(context).orientation
                )
            )
        }

        categoryRankListAdapter.setItemClickListener(object:HomeAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val bundle = Bundle()
                bundle.apply {
                    bundle.putString("category_name", categoryRankList[position].name)
                    bundle.putString("category_percent", categoryRankList[position].percent)
                }
                Log.d("argument", bundle.toString())
                replaceFragment(CategoryDetailFragment(),bundle)
            }

        })

        themeRankListAdapter.setItemClickListener(object :HomeAdapter.OnItemClickListener{
            override fun onClick(v:View, position: Int){
                val bundle = Bundle()
                bundle.apply{
                    bundle.putString("theme_name", themeRankList[position].name)
                    bundle.putString("theme_percent", themeRankList[position].percent)
                }
                replaceFragment(ThemeDetailFragment(),bundle)

            }
        })

        newsRankListAdapter.setItemClickListener(object : MainNewsAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(newsRankList[position].rink)))
            }

        })

        getHighSectorList(3)
        getHighThemeList(3)
        getMainNewsList(3)

        view.seeMore1.setOnClickListener(this)
        view.seeMore2.setOnClickListener(this)
        view.rightArrow1.setOnClickListener(this)
        view.rightArrow2.setOnClickListener(this)
        view.searchBtn.setOnClickListener(this)
        view.open.setOnClickListener(this)
        view.close.setOnClickListener(this)

        return view.root
    }

    fun getHighSectorList(num: Int){
        retrofit.getHighSectorList(num).enqueue(object: Callback<SectorThemeList> {
            override fun onResponse(
                call: Call<SectorThemeList>,
                response: Response<SectorThemeList>
            ) {
                if(response.code()==200) {
                    addResultSectorThemeHighList("sector",response.body())
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
    fun getMainNewsList(num: Int){
        retrofit.getMainNewsList(num).enqueue(object : Callback<NewsList>{
            override fun onResponse(
                call: Call<NewsList>,
                response: Response<NewsList>
            ) {
                if(response.code()==200) {
                    addNewsList(response.body())
                    Log.d("API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NewsList>, t: Throwable) {
                Log.d("API호출2", t.message.toString())
            }
        })
    }
    private fun addResultSectorThemeHighList(name:String, body: SectorThemeList?) {
        when(name){
            "sector" -> {
                categoryRankList.clear()
                if(body != null) {
                    for(item in body) {
                        Log.d("API결과",item.toString())
                        if(item.rate >= 0.0){
                            categoryRankList.add(MainModel(item.keyword, "+"+item.rate.toString()+"%"))
                        } else {
                            categoryRankList.add(MainModel(item.keyword, item.rate.toString()+"%"))
                        }
                    }
                }
                Log.d("API결과리스트", categoryRankList.toString())
                categoryRankListAdapter.notifyDataSetChanged()
            }
            "theme" -> {
                themeRankList.clear()
                if(body != null) {
                    for(item in body) {
                        Log.d("API결과",item.toString())
                        if(item.rate >= 0.0){
                            themeRankList.add(MainModel(item.keyword, "+"+item.rate.toString()+"%"))
                        } else {
                            themeRankList.add(MainModel(item.keyword, item.rate.toString()+"%"))
                        }
                    }
                }
                Log.d("API결과리스트", themeRankList.toString())
                themeRankListAdapter.notifyDataSetChanged()

            }
        }
    }
    private fun addNewsList(body: NewsList?){
        newsRankList.clear()
        if(body.isNullOrEmpty()){

        }
        else{
            for(item in body){
                Log.d("API뉴스결과",item.toString())
                newsRankList.add(NewsModel("관련 종목코드: "+item.ticker,item.provider,item.date,item.rink,item.title))
            }
            newsRankListAdapter.notifyDataSetChanged()
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
            R.id.see_more1 -> {
                val bundle =Bundle()
                Log.d("argument", "goodgood")
                bundle.putString("focus","good")
                replaceFragment(AllListFragment(), bundle)
            }
            R.id.see_more2 ->{
                val bundle = Bundle()
                bundle.putString("foccus", "hue")
                replaceFragment(AllListFragment(), bundle)
            }
            R.id.right_arrow1 ->{
                val bundle = Bundle()
                bundle.putString("focus", "thue")
                replaceFragment(AllListFragment(), bundle)
            }
            R.id.right_arrow2 ->{
                val bundle = Bundle()
                bundle.putString("foccus", "hue")
                replaceFragment(AllListFragment(), bundle)
            }
            R.id.search_btn -> {
                val bundle = Bundle()
                bundle.putString("keyword", view.editText.getText().toString())
                view.editText.text = null
                replaceFragment(KeyWordFragment(),bundle)
            }
            R.id.open -> {
                getMainNewsList(10)
                view.close.visibility = View.VISIBLE
                view.open.visibility = View.GONE
            }
            R.id.close -> {
                getMainNewsList(3)
                view.close.visibility = View.GONE
                view.open.visibility = View.VISIBLE
            }
        }
    }

}
