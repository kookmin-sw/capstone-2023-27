package com.example.htss.Fragment
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Selection.setSelection
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.RenderProcessGoneDetail
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.htss.Adapter.MainNewsAdapter
import com.example.htss.Adapter.HomeAdapter
import com.example.htss.Model.MainModel
import com.example.htss.Model.NewsModel
import com.example.htss.R
import com.example.htss.Retrofit.Model.NewsList
import com.example.htss.Retrofit.Model.SectorThemeList
import com.example.htss.Retrofit.RetrofitClient
import com.example.htss.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(), View.OnClickListener {

    var     selectedPosition = 0

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
        val items = resources.getStringArray(R.array.search_array)
        val myAdapter = object : ArrayAdapter<String>(requireContext(), R.layout.item_spinner) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                if (position == count) {
                    //마지막 포지션의 textView 를 힌트 용으로 사용합니다.
                    (v.findViewById<View>(R.id.tvItemSpinner) as TextView).text = ""
                    //아이템의 마지막 값을 불러와 hint로 추가해 줍니다.
                    (v.findViewById<View>(R.id.tvItemSpinner) as TextView).hint = getItem(count)
                }
                return v
            }
            override fun getCount(): Int {
                //마지막 아이템은 힌트용으로만 사용하기 때문에 getCount에 1을 빼줍니다.
                return super.getCount() - 1
            }
        }
        myAdapter.addAll(items.toMutableList())
        myAdapter.add("항목선택")
        view.searchSpinner.adapter = myAdapter
        view.searchSpinner.setSelection(myAdapter.count)
        view.searchSpinner.dropDownVerticalOffset = dipToPixels(35f).toInt()
//스피너 선택시 나오는 화면
        view.searchSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedPosition = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("MyTag", "onNothingSelected")
            }
        }

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

        setListenerToEditText()
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
        view.searchBtn.setOnClickListener(this)

        return view.root
    }

    //서치눌렀을 때 키보드 내려가게
    fun softkeyboardHide() {
        val imm = getSystemService(requireContext(), InputMethodManager::class.java)
        imm!!.hideSoftInputFromWindow(view.editText.windowToken, 0)
    }

// 엔터눌렀을 때 키보드 내려가게
    private fun setListenerToEditText() {
        view.editText.setOnKeyListener { view, keyCode, event ->
            // Enter Key Action
            if (event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                // 키패드 내리기
                val imm = getSystemService(requireContext(), InputMethodManager::class.java)
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.edit_text.windowToken, 0)
                }
                // Toast Message
                showToastMessage(view.edit_text.text.toString())
                true
            }
            false
        }
    }
    private fun showToastMessage(msg: String?) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }


    fun getTickerByStockName(name: String){
        retrofit.getTickerByStockName(name).enqueue(object: Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.code() == 200){
                    if(!response.body().isNullOrBlank()){
                        val bundle = Bundle()
                        bundle.putString("stock_ticker", response.body())
                        bundle.putString("stock_name", name)
                        replaceFragment(StockFragment(), bundle)
                    } else {
                        Toast.makeText(requireContext(),"일치하는 종목이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else Toast.makeText(requireContext(),"오류가 발생하였습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()

            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(requireContext(),"오류가 발생하였습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun getStockNameByTicker(ticker: String){
        retrofit.getStockNameByTicker(ticker).enqueue(object: Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.code() == 200){
                    if(!response.body().isNullOrBlank()){
                        val bundle = Bundle()
                        bundle.putString("stock_ticker", ticker)
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
    //dp 값을 px 값으로 변환해 주는 함수
    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
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
                softkeyboardHide()
                when(selectedPosition){
                    1 -> { // 종목번호
                        getStockNameByTicker(view.editText.text.toString().trim())
                    }
                    2 -> { // 종목명
                        getTickerByStockName(view.editText.text.toString().trim())
                    }
                    else -> { // 키워드, 업종, 테마
                        val bundle = Bundle()
                        bundle.putString("keyword", view.editText.text.toString().trim())
                        bundle.putInt("type", selectedPosition)
                        replaceFragment(KeyWordFragment(), bundle)
                    }
                }
                view.editText.text = null
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
