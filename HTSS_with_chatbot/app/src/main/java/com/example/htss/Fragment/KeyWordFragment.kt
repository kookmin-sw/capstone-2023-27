package com.example.htss.Fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
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
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_key_word.*
import kotlinx.android.synthetic.main.fragment_key_word.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat


class KeyWordFragment : Fragment(), View.OnClickListener {
    private lateinit var view: FragmentKeyWordBinding
    private val retrofit = RetrofitClient.create()

    var selectedPosition = 0

    private var KeywordCategoryList = mutableListOf<MainModel>()

    private var KeywordThemeList = mutableListOf<MainModel>()

    private var RelatedStockList = mutableListOf<KeywordRelatedStockModel>()

    private var RelatedNewsList = mutableListOf<NewsModel>()

    private var KeywordCategoryListAdapter = HomeAdapter(KeywordCategoryList)
    private var KeywordThemeListAdapter = HomeAdapter(KeywordThemeList)
    private var RelatedStockListAdapter = KeywordRelatedStockAdapter(RelatedStockList)
    private var RelatedNewsListAdapter = MainNewsAdapter(RelatedNewsList)


    private var KeyWordName = ""
    val dec = DecimalFormat("#,###.##")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentKeyWordBinding.inflate(inflater, container, false)

        val items = resources.getStringArray(R.array.search_array)
        val myAapter = object : ArrayAdapter<String>(requireContext(), R.layout.item_spinner) {
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

        myAapter.addAll(items.toMutableList())
        myAapter.add("항목선택")
        view.searchSpinner.adapter = myAapter
        view.searchSpinner.setSelection(myAapter.count)
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
            addItemDecoration(
                DividerItemDecoration(
                    view.newsRecyclerview.context,
                    LinearLayoutManager(context).orientation
                )
            )
        }



        KeyWordName = arguments?.getString("keyword").toString()

        view.keywordName1.text = KeyWordName
        view.keywordName2.text = KeyWordName
        view.keywordName3.text = KeyWordName
        view.keywordName4.text = KeyWordName

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
                    bundle.putString("stock_ticker", RelatedStockList[position].ticker)
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
        RelatedNewsListAdapter.setLinkClickListener(object : MainNewsAdapter.OnLinkClickListener{
            override fun onClick(v: View, position: Int) {
                getStockNameByTicker(RelatedNewsList[position].ticker)
            }
        })

        view.back.setOnClickListener {
            parentFragmentManager.popBackStack()
        }


        setListenerToEditText()
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
        view.searchBtn2.setOnClickListener(this)

        return view.root
    }

    // 엔터치면 키보드 내리기
    private fun setListenerToEditText() {
        view.keywordEdit.setOnKeyListener { view, keyCode, event ->
            // Enter Key Action
            if (event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                // 키패드 내리기
                val imm =
                    ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.keyword_edit.windowToken, 0)
                }
                // Toast Message
                showToastMessage(view.keyword_edit.text.toString())
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
                        bundle.putString("stock_name", response.body())
                        Log.d("keywordfragment",response.body().toString())
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
                            item.ticker,
                            item.company_name,
                            dec.format(item.end_price).toString(),
                            "+"+item.rate.toString()+"%",
                            item.count.toString() + "회"
                        )
                    )
                }

                else{
                    RelatedStockList.add(
                        KeywordRelatedStockModel(
                            item.ticker,
                            item.company_name,
                            dec.format(item.end_price).toString(),
                            item.rate.toString()+"%",
                            item.count.toString() + "회"
                        )
                    )
                }
            }
        }
        RelatedStockListAdapter.notifyDataSetChanged()
        if(RelatedStockList.size in 1..2){
            view.stockOpenBtn.visibility = View.GONE
        }
        else if(RelatedStockList.size == 0){
            view.stockNoBtn.visibility = View.VISIBLE
            view.stockOpenBtn.visibility = View.GONE
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
        if(KeywordCategoryList.size in 1..2){
            view.categoryOpenBtn.visibility = View.GONE
        }
        else if(KeywordCategoryList.size == 0){
            view.categoryOpenBtn.visibility = View.GONE
            view.categoryNoBtn.visibility = View.VISIBLE
        }

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

        if(body.isNullOrEmpty()){
        }
        else{
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
        if(KeywordThemeList.size in 1..2){
            view.themeOpenBtn.visibility = View.GONE
        }
        else if(KeywordThemeList.size == 0){
            view.themeOpenBtn.visibility = View.GONE
            view.themeNoBtn.visibility = View.VISIBLE
        }
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
                RelatedNewsList.add(NewsModel(item.ticker,item.provider,item.date,item.rink,item.title,item.sentiment))
                Log.d("count","H")

            }
        }
        RelatedNewsListAdapter.notifyDataSetChanged()
        if(RelatedNewsList.size in 1 ..2){
            view.newsOpenBtn.visibility = View.GONE
        }
        else if(RelatedNewsList.size == 0){
            view.newsNoBtn.visibility = View.VISIBLE
            view.newsOpenBtn.visibility = View.GONE
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

    //서치눌렀을 때 키보드 내려가게
    fun softkeyboardHide() {
        val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
        imm!!.hideSoftInputFromWindow(view.keywordEdit.windowToken, 0)
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
            R.id.search_btn2 -> {
                softkeyboardHide()
                when (selectedPosition) {
                    1 -> { //종목번호
                        getStockNameByTicker(view.keywordEdit.text.toString().trim())
                    }
                    2 -> { //종목명
                        getTickerByStockName(view.keywordEdit.text.toString().trim())
                    }
                    else -> { //키워드, 업종, 테마
                        val bundle = Bundle()
                        bundle.putString("keyword", view.keywordEdit.text.toString().trim())
                        bundle.putInt("type", selectedPosition)
                        replaceFragment(KeyWordFragment(), bundle)
                    }
                }
                view.keywordEdit.text = null
            }
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