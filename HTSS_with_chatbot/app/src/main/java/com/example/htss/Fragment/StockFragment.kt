package com.example.htss.Fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.htss.Adapter.MainNewsAdapter
import com.example.htss.Model.NewsModel
import com.example.htss.Model.StockChartModel
import com.example.htss.R
import com.example.htss.Retrofit.Model.*
import com.example.htss.Retrofit.RetrofitClient
import com.example.htss.databinding.FragmentStockBinding
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.fragment_stock.*
import kotlinx.android.synthetic.main.fragment_stock.view.*
import kotlinx.android.synthetic.main.mymarker_view.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StockFragment : Fragment(), View.OnClickListener {

    var selectedPosition = 0
    var selectedPosition2 = 0
    var newsNum = 3
    var first = "company_info"

    private lateinit var view: FragmentStockBinding
    private val retrofit = RetrofitClient.create()


    private var StockNewsList = arrayListOf<NewsModel>()

    private var StockNewsListAdapter = MainNewsAdapter(StockNewsList)

    private var StockTicker = ""
    private var StockName = ""
    val dec = DecimalFormat("#,###.##")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {

        view = FragmentStockBinding.inflate(inflater, container, false)

        view.companyInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        view.companyInvestInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.hmmm))
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedPosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("MyTag", "onNothingSelected")
            }
        }
////////////////////////////// 차트 스피너
        val items2 = resources.getStringArray(R.array.chart_array)
        val myAdapter2 = object : ArrayAdapter<String>(requireContext(), R.layout.chart_spinner) {
            override fun getView(position2: Int, convertView: View?, parent: ViewGroup): View {
                val v2 = super.getView(position2, convertView, parent)

                return v2
            }
        }
        myAdapter2.addAll(items2.toMutableList())
        view.chartSpinner.adapter = myAdapter2
        view.chartSpinner.setSelection(0)
        view.chartSpinner.dropDownVerticalOffset = dipToPixels(30f).toInt()
        //스피너 선택시 나오는 화면
        view.chartSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position2: Int,
                id: Long
            ) {
                selectedPosition2 = position2
                Log.d("position", selectedPosition2.toString())
                when (selectedPosition2) {
                    0 -> {
                        getStockPrice(StockTicker, 60)
                    }
                    1 -> {
                        getStockPrice(StockTicker, 90)
                    }
                    2 -> {
                        getStockPrice(StockTicker, 180)
                    }
                    3 -> {
                        getStockPrice(StockTicker, 365)
                    }
                    4 -> {
                        getStockPrice(StockTicker, 730)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("MyTag", "onNothingSelected")
            }
        }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        StockTicker = arguments?.getString("stock_ticker").toString()
        StockName = arguments?.getString("stock_name").toString()


        val bundle = Bundle()
        bundle.putString("stock_ticker", StockTicker)
        when (first) {
            "company_info" -> replaceFragment2(Company_info_Fragment1(), bundle)
        }

//
        view.newsRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = StockNewsListAdapter
            addItemDecoration(
                DividerItemDecoration(
                    view.newsRecyclerview.context,
                    LinearLayoutManager(context).orientation
                )
            )
        }

        view.back.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        StockNewsListAdapter.setItemClickListener(object : MainNewsAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(StockNewsList[position].rink)))
            }
        })

        StockNewsListAdapter.setLinkClickListener(object : MainNewsAdapter.OnLinkClickListener {
            override fun onClick(v: View, position: Int) {
                getStockNameByTicker(StockNewsList[position].ticker)
            }
        })

        view.newsCloseBtn.setOnClickListener(this)
        view.newsOpenBtn.setOnClickListener(this)
        view.stockSearchBtn.setOnClickListener(this)
        view.companyInfo.setOnClickListener(this)
        view.companyInvestInfo.setOnClickListener(this)
//        view.thirtyDays.setOnClickListener(this)
//        view.ninetyDays.setOnClickListener(this)
//        view.oneYear.setOnClickListener(this)
//        view.hundredDays.setOnClickListener(this)

        setListenerToEditText()
        getStockNowPrice(StockTicker)
        getCompanyInfo(StockTicker)
//        drawStockChart22()
//        getStockPrice(StockTicker,100)

        return view.root
    }

    fun getCompanyInfo(ticker: String) {
        retrofit.getCompanyInfo(ticker).enqueue(object : Callback<CompanyInfoListItem> {
            override fun onResponse(
                call: Call<CompanyInfoListItem>,
                response: Response<CompanyInfoListItem>
            ) {
                if (response.code() == 200) {
                    if (response.body() != null)
                        addcompanyInfo(response.body()!!)
                    Log.d("stock/info/API호출!!!!!!", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(), "오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<CompanyInfoListItem>, t: Throwable) {
                Log.d("API호출3333", t.message.toString())
            }
        })
    }

    private fun addcompanyInfo(body: CompanyInfoListItem) {
        view.stockName.text = body.company_name
        view.stockName2.text = body.company_name
        getSectorThemeKeywordIncludeNews(body.company_name, newsNum)
    }


    fun getStockNowPrice(ticker: String) {
        retrofit.getStockNowPrice(ticker).enqueue(object : Callback<StockNowPriceListItem> {
            override fun onResponse(
                call: Call<StockNowPriceListItem>,
                response: Response<StockNowPriceListItem>
            ) {
                if (response.code() == 200) {
                    if (response.body() != null)
                        addStockNowPrice(response.body()!!)
                    Log.d("stock/now-price/API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(), "오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<StockNowPriceListItem>, t: Throwable) {
                Log.d("API호출2222222", t.message.toString())
            }
        })
    }

    private fun addStockNowPrice(body: StockNowPriceListItem) {
        view.ticker.text = StockTicker
        view.stockCurrent.text = dec.format(body.end_price).toString()
        if (body.rate >= 0.0) {
            view.stockPercent.apply {
                setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                text = "+" + body.rate.toString() + "%"
            }
        } else {
            view.stockPercent.apply {
                setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
                text = body.rate.toString() + "%"
            }
        }
    }


    fun getTickerByStockName(name: String) {
        retrofit.getTickerByStockName(name).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.code() == 200) {
                    if (!response.body().isNullOrBlank()) {
                        val bundle = Bundle()
                        bundle.putString("stock_ticker", response.body())
                        bundle.putString("stock_name", name)
                        replaceFragment(StockFragment(), bundle)
                    } else {
                        Toast.makeText(requireContext(), "일치하는 종목이 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else Toast.makeText(
                    requireContext(),
                    "오류가 발생하였습니다.\n다시 시도해주세요.",
                    Toast.LENGTH_SHORT
                ).show()

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(requireContext(), "오류가 발생하였습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    fun getStockNameByTicker(ticker: String) {
        retrofit.getStockNameByTicker(ticker).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.code() == 200) {
                    if (!response.body().isNullOrBlank()) {
                        val bundle = Bundle()
                        bundle.putString("stock_ticker", ticker)
                        bundle.putString("stock_name", response.body())
                        Log.d("hmmmddd", response.body().toString())
                        replaceFragment(StockFragment(), bundle)
                    } else {
                        Toast.makeText(requireContext(), "일치하는 종목이 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else Toast.makeText(
                    requireContext(),
                    "오류가 발생하였습니다.\n다시 시도해주세요.",
                    Toast.LENGTH_SHORT
                ).show()

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(requireContext(), "일치하는 종목이 없습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }


    fun getSectorThemeKeywordIncludeNews(keyword: String, num: Int) {
        retrofit.getSectorThemeKeywordIncludeNews(keyword, num)
            .enqueue(object : Callback<KeywordIncludeNewsList> {
                override fun onResponse(
                    call: Call<KeywordIncludeNewsList>,
                    response: Response<KeywordIncludeNewsList>
                ) {
                    if (response.code() == 200) {
                        addSectorthemeIncludeNewsList(response.body())
                        Log.d("stock/news/like/ API호출", response.raw().toString())
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "오류가 발생했습니다.\n다시 시도해주세요",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<KeywordIncludeNewsList>, t: Throwable) {
                    Log.d("API호출2", t.message.toString())
                }
            })
    }

    private fun addSectorthemeIncludeNewsList(body: KeywordIncludeNewsList?) {
        StockNewsList.clear()
        if (body.isNullOrEmpty()) {

        } else {
            for (item in body) {
                StockNewsList.add(
                    NewsModel(
                        item.ticker,
                        item.provider,
                        item.date,
                        item.rink,
                        item.title,
                        item.sentiment
                    )
                )
            }
        }
        StockNewsListAdapter.notifyDataSetChanged()
        if (StockNewsList.size in 1..2) {
            view.newsOpenBtn.visibility = View.GONE
        } else if (StockNewsList.size == 0) {
            view.newsOpenBtn.visibility = View.GONE
            view.newsStockNoBtn.visibility = View.VISIBLE
        }
    }

    fun softkeyboardHide() {
        val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
        imm!!.hideSoftInputFromWindow(view.stockKeywordEdit.windowToken, 0)
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.news_open_btn -> {
                getSectorThemeKeywordIncludeNews(StockName, 10)
                view.newsCloseBtn.visibility = View.VISIBLE
                view.newsOpenBtn.visibility = View.GONE

            }
            R.id.news_close_btn -> {
                getSectorThemeKeywordIncludeNews(StockName, 3)
                view.newsCloseBtn.visibility = View.GONE
                view.newsOpenBtn.visibility = View.VISIBLE
            }
            R.id.stock_search_btn -> {
                softkeyboardHide()
                when (selectedPosition) {
                    1 -> { // 종목번호
                        getStockNameByTicker(view.stockKeywordEdit.text.toString().trim())
                    }
                    2 -> { // 종목명
                        getTickerByStockName(view.stockKeywordEdit.text.toString().trim())
                    }
                    else -> { // 키워드
                        val bundle = Bundle()
                        bundle.putString("keyword", view.stockKeywordEdit.text.toString().trim())
                        bundle.putInt("type", selectedPosition)
                        replaceFragment(KeyWordFragment(), bundle)
                    }
                }
                view.stockKeywordEdit.text = null
            }
            R.id.company_info -> {
                val bundle = Bundle()
                bundle.putString("stock_ticker", StockTicker)
                bundle.putString("Focus", "hu")
                Log.d("good", bundle.toString())
                view.companyInfo.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                view.companyInvestInfo.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.hmmm
                    )
                )
                replaceFragment2(Company_info_Fragment1(), bundle)
            }
            R.id.company_invest_info -> {
                val bundle = Bundle()
                bundle.putString("stock_ticker", StockTicker)
                bundle.putString("Focuss", "huu")
                view.companyInvestInfo.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                view.companyInfo.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.hmmm
                    )
                )
                replaceFragment2(Company_info_Fragment2(), bundle)
            }

        }
    }
//

    private fun replaceFragment(fragment: Fragment, bundle: Bundle) {
        fragment.arguments = bundle
        Log.d("argument", bundle.toString())
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrameLayout, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun replaceFragment2(fragment: Fragment, bundle: Bundle) {
        fragment.arguments = bundle
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.company_frame_layout, fragment)
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

    // 엔터치면 키보드 내리기
    private fun setListenerToEditText() {
        view.stockKeywordEdit.setOnKeyListener { view, keyCode, event ->
            // Enter Key Action
            if (event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                // 키패드 내리기
                val imm =
                    ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.stock_keyword_edit.windowToken, 0)
                }
                // Toast Message
                showToastMessage(view.stock_keyword_edit.text.toString())
                true
            }
            false
        }
    }

    ////////////////////////////
    private fun showToastMessage(msg: String?) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    fun getStockPrice(ticker: String, num: Int) {
        retrofit.getStockPrice(ticker, num).enqueue(object : Callback<StockPriceList> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<StockPriceList>,
                response: Response<StockPriceList>
            ) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        var result = getStockData(response.body()!!)
                        drawChart(result)
                    }
                    Log.d("stock/price/API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(), "오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<StockPriceList>, t: Throwable) {
                Log.d("API호출2222222", t.message.toString())
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun drawChart(item: MutableList<StockChartModel>) {
        val entries = ArrayList<CandleEntry>()
        val entries2 = ArrayList<Entry>()
        val saveDate = ArrayList<String>()

        var trend_x = 0
        var candle_x = 0
        for (csStock in item) {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val simpleDate: String = simpleDateFormat.format(csStock.createdAt)
            saveDate.add(
                simpleDate
            )
            Log.d("saveDate", saveDate.toString())

            entries.add(
                CandleEntry(
                    candle_x.toFloat(),
                    csStock.price_high,
                    csStock.price_low,
                    csStock.price_start,
                    csStock.price_end
                )
            )
            candle_x += 1

            if(csStock.trend == "상승") {
                var num2 = 4000
                entries2.add(
                    Entry(
                        trend_x.toFloat(),
                        num2.toFloat()
                    )
                )
            }
            else if(csStock.trend == "하락"){
                var num2 = 800
                entries2.add(
                    Entry(
                        trend_x.toFloat(),
                        num2.toFloat()
                    )
                )
            }
            trend_x += 1

        }
        Log.d("캔들 엔트리데이터", entries.toString())
        Log.d("꺾은선 엔트리데이터", entries2.toString())
        val dataSet = CandleDataSet(entries, "").apply {
            // 심지 부분
            shadowColor = Color.GRAY
            shadowWidth = 0.8F

            // 음봉
            decreasingColor = Color.BLUE
            decreasingPaintStyle = Paint.Style.FILL
            // 양봉
            increasingColor =  Color.RED
            increasingPaintStyle = Paint.Style.FILL

            neutralColor = Color.DKGRAY
            setDrawValues(false)
            // 터치시 노란 선 제거
            highLightColor = Color.TRANSPARENT
        }

        val LineDataSet = LineDataSet(entries2, "").apply {
            setDrawCircles(false)
            setMode(LineDataSet.Mode.HORIZONTAL_BEZIER)

            color = Color.rgb(219, 17, 179)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }

        view.chart.axisLeft.run {
            setDrawAxisLine(false)
            setDrawGridLines(false)
            textColor = Color.TRANSPARENT
        }

        view.chart.axisRight.run {
            setDrawAxisLine(true)
            setDrawGridLines(true)
            textColor = Color.BLACK
            textSize = 0.5f
        }

        // X 축
        view.chart.xAxis.run {
            textColor = Color.BLACK
            textSize = 0.2f
            valueFormatter = MyXAxisFormatter(saveDate)
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(true)
            setDrawAxisLine(false)
            setDrawGridLines(true)
            setAvoidFirstLastClipping(true)
        }

        // 범례
        view.chart.legend.run {
            isEnabled = false
        }

        view.chart.apply {
            val combinedData = CombinedData()
            combinedData.setData(CandleData(dataSet))
//            val lineData = LineData(LineDataSet)
//            combinedData.setData(lineData)
            this.data = combinedData

            description.isEnabled = false
            isHighlightPerDragEnabled = true
            requestDisallowInterceptTouchEvent(true)
            invalidate()
        }

        val marker = MyMarkerViewcontext(this.requireContext(),R.layout.mymarker_view)
        chart!!.marker = marker
    }


    // 주식차트 데이터 가져오기
    private fun getStockData(body: StockPriceList): MutableList<StockChartModel> {
        var result = mutableListOf<StockChartModel>()

        if (body.isNullOrEmpty()) {

        } else {
            for (item in body) {
                result.add(
                    (StockChartModel(
                        item.date,
                        item.high_price.toFloat(),
                        item.low_price.toFloat(),
                        item.start_price.toFloat(),
                        item.end_price.toFloat(),
                        item.trend
                    ))
                )
            }
//            Log.d("StockPrice", StockPrice.toString())
        }

        result.sortBy { it.createdAt }
        Log.d("StockPrice22", result.toString())
        return result
    }

    inner class MyXAxisFormatter(saveDate: ArrayList<String>) : ValueFormatter() {
        private val stockdate = saveDate
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return stockdate.getOrNull(value.toInt()) ?: value.toString()
            }
        }

    inner class MyMarkerViewcontext(context: Context, layoutResource: Int) : MarkerView(context, layoutResource){
        private var textView: TextView = findViewById(R.id.marker)

        var price = ""
        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            if(e is CandleEntry){
                textView.text = "시가 : ${e.open}\n" +
                        "종가 : ${e.close}\n" +
                        "고가 : ${e.high}\n " +
                        "저가 : ${e.low}\n "
            }
//            textView = open
//            if (e != null) {
//                if (highlight != null) {
//                    Log.d("Entry_data",
//                        e.x.toString() + "|" + e.y.toString() + "|" + highlight.x.toString() + "|" + highlight.y.toString() + "|" + highlight.axis.toString() + "|" + highlight.dataIndex.toString() + "|" + highlight.dataSetIndex.toString())
//                }
//            }
//            if (e != null) {
//                price = makeCommaNumber(e.y)
//            }
//            Log.d("markerkerker",price.toString())
////            textView.text = "${price} 원"
//            textView.text = price
            super.refreshContent(e, highlight)
        }
//        fun makeCommaNumber(input: Float): String {
////            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
////            val simpleDate: String = simpleDateFormat.format(input)
//            val formatter = DecimalFormat("###,###")
////        println(input.length)
//            price = formatter.format(input.toLong())
//
//            return price
//        }
        override fun getOffset(): MPPointF {
            return MPPointF(-(width / 2f), -height.toFloat() - 20f)
        }
    }
}




