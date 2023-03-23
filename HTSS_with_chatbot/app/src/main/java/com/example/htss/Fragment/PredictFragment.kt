package com.example.htss.Fragment

import android.graphics.Color
import com.example.htss.databinding.FragmentPredictBinding
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.htss.Adapter.PredictListAdapter
import com.example.htss.Model.PredictListModel
import com.example.htss.Model.StockChartModel
import com.example.htss.Model.TrendChartModel
import com.example.htss.R
import com.example.htss.Retrofit.Model.Stock
import com.example.htss.Retrofit.Model.StockPriceList
import com.example.htss.Retrofit.Model.TrendList
import com.example.htss.Retrofit.RetrofitClient
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.time.Duration.Companion.days

class PredictFragment : Fragment(), View.OnClickListener {

    var selectedPosition = 0

    private lateinit var view: FragmentPredictBinding
    private val retrofit = RetrofitClient.create()

    private var predictList1 = mutableListOf<PredictListModel>()
    private var predictAdapter1 = PredictListAdapter(predictList1)

    private var predictList2 = mutableListOf<PredictListModel>()
    private var predictAdapter2 = PredictListAdapter(predictList2)

    private var predictList3 = mutableListOf<PredictListModel>()
    private var predictAdapter3 = PredictListAdapter(predictList3)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentPredictBinding.inflate(inflater, container, false)

        val items = resources.getStringArray(R.array.predict_array)
        val myAdapter = object : ArrayAdapter<String>(requireContext(), R.layout.item_spinner) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)

                return v
            }
        }
        myAdapter.addAll(items.toMutableList())
        view.predictSpinner1.adapter = myAdapter
        view.predictSpinner1.setSelection(0)
        view.predictSpinner1.dropDownVerticalOffset = dipToPixels(35f).toInt()
        //스피너 선택시 나오는 화면
        view.predictSpinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedPosition = position
                when(selectedPosition){
                    0 -> {
                        getTrendData(5)
                        getTrendPriceChart(5)
                    }
                    1-> {
                        getTrendData(15)
                        getTrendPriceChart(15)
                    }
                    2->{
                        getTrendData(30)
                        getTrendPriceChart(30)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("MyTag", "onNothingSelected")
            }
        }

        view.risePredict1.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
            adapter = predictAdapter1

            predictAdapter1.setItemClickListener(object : PredictListAdapter.OnItemClickListener{
                override fun onClick(v: View, position: Int) {
                    val bundle = Bundle()
                    bundle.apply {
                        bundle.putString("stock_ticker", predictList1[position].ticker)
                        bundle.putString("stock_name", predictList1[position].company)
                    }
                    Log.d("risepredict1",predictList1[position].ticker)
                    Log.d("risepredict1",predictList1[position].company)
                    replaceFragment(StockFragment(), bundle)
                }
            })
        }
        view.risePredict2.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
            adapter = predictAdapter2

            predictAdapter2.setItemClickListener(object : PredictListAdapter.OnItemClickListener{
                override fun onClick(v: View, position: Int) {
                    val bundle = Bundle()
                    bundle.apply {
                        bundle.putString("stock_ticker", predictList2[position].ticker)
                        bundle.putString("stock_name", predictList2[position].company)
                    }
                    Log.d("risepredict2",predictList2[position].ticker)
                    Log.d("risepredict2",predictList2[position].company)
                    replaceFragment(StockFragment(), bundle)
                }
            })
        }

        view.risePredict3.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
            adapter = predictAdapter3

            predictAdapter3.setItemClickListener(object : PredictListAdapter.OnItemClickListener{
                override fun onClick(v: View, position: Int) {
                    val bundle = Bundle()
                    bundle.apply {
                        bundle.putString("stock_ticker", predictList3[position].ticker)
                        bundle.putString("stock_name", predictList3[position].company)
                    }
                    Log.d("risepredict3",predictList3[position].ticker)
                    Log.d("risepredict3",predictList3[position].company)
                    replaceFragment(StockFragment(), bundle)
                }
            })
        }


        return view.root
    }

    //API 호출
    fun getTrendData(period: Int){
        retrofit.getTrendData(period).enqueue(object: Callback<TrendList> {
            override fun onResponse(call: Call<TrendList>, response: Response<TrendList>) {
                if(response.code() == 200){
                    addTrendDataList(response.body())
                    Log.d("trend API호출", response.raw().toString())
                } else Toast.makeText(requireContext(),"오류가 발생하였습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()

            }
            override fun onFailure(call: Call<TrendList>, t: Throwable) {
                Toast.makeText(requireContext(),"오류가 발생하였습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                Log.d("trend API호출22", t.message.toString())
            }
        })
    }

    private fun addTrendDataList(body: TrendList?) {
        predictList1.clear()
        predictList2.clear()
        predictList3.clear()

        if (body != null) {
            if(body.n0.stock.toString() == "[]"){
                view.trendSimilarNoBtn1.visibility = View.VISIBLE
            }
            else{
                for (item in body.n0.stock) {
                    predictList1.add(
                        PredictListModel(
                            item.ticker,
                            item.company_name,
                            item.period.toString() + "일"
                        )
                    )
                }
                view.trendSimilarNoBtn1.visibility = View.GONE
            }
        }
        predictAdapter1.notifyDataSetChanged()

        if (body != null) {
            if(body.n1.stock.toString() == "[]"){
                view.trendSimilarNoBtn2.visibility = View.VISIBLE
            }
            else{
                for (item in body.n1.stock) {
                    predictList2.add(
                        PredictListModel(
                            item.ticker,
                            item.company_name,
                            item.period.toString() + "일"
                        )
                    )
                }
                view.trendSimilarNoBtn2.visibility = View.GONE
            }
        }
        predictAdapter2.notifyDataSetChanged()

        if (body != null) {
            if(body.n2.stock.toString() == "[]"){
                view.trendSimilarNoBtn3.visibility = View.VISIBLE
            }
            else{
                for (item in body.n2.stock) {
                    predictList3.add(
                        PredictListModel(
                            item.ticker,
                            item.company_name,
                            item.period.toString() + "일"
                        )
                    )
                }
                view.trendSimilarNoBtn3.visibility = View.GONE
            }
        }
        predictAdapter3.notifyDataSetChanged()
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

    fun drawStockChart1(item: MutableList<TrendChartModel>){
        Log.d("item.toString()", item.toString())
        // 평균값 구하기
        var average = 0F

        for( stock in item) {
            average += stock.price.toFloat()
        }
        average /= item.size

        // 그래프에 들어갈 데이터 준비
        val entries = ArrayList<Entry>()
        val colors = Stack<Int>()
        for (stock in item) {
            if (stock.price >= average) {
//                if (colors.isNotEmpty() && colors.peek() == Color.BLUE) {
//                    entries.add(Entry(stock.createdAt.toFloat(), average))
//                    colors.add(Color.TRANSPARENT)
//                    entries.add(Entry(stock.createdAt.toFloat(), average))
//                    colors.add(Color.RED)
//                }
                entries.add(Entry(stock.createdAt.toFloat(), stock.price.toFloat()))
                colors.add(Color.RED)
            } else {
//                if (colors.isNotEmpty() && colors.peek() == Color.RED) {
//                    entries.add(Entry(stock.createdAt.toFloat(), average))
//                    colors.add(Color.TRANSPARENT)
//                    entries.add(Entry(stock.createdAt.toFloat(), average))
//                    colors.add(Color.BLUE)
//                }
                entries.add(Entry(stock.createdAt.toFloat(), stock.price.toFloat()))
                colors.add(Color.BLUE)
            }
            Collections.sort(entries, EntryXComparator())
        }

        val dataSet = LineDataSet(entries, "").apply {
            setDrawCircles(false)
            color = Color.RED
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0F
            lineWidth = 1.5F
        }

        val lineData = LineData(dataSet)
        view.predictChart1.run {
            data = lineData
            description.isEnabled = false // 하단 Description Label 제거함
            invalidate() // refresh
        }

        val averageLine = LimitLine(average).apply {
            lineWidth = 1F
            enableDashedLine(4F, 10F, 10F)
            lineColor = Color.DKGRAY
        }

        // 범례
        view.predictChart1.legend.apply {
            isEnabled = false // 사용하지 않음
        }
        // Y 축
        view.predictChart1.axisLeft.apply {
            // 라벨, 축라인, 그리드 사용하지 않음
            setDrawLabels(false)
            setDrawAxisLine(false)
            setDrawGridLines(false)

            // 한계선 추가
            removeAllLimitLines()
            addLimitLine(averageLine)
        }
        view.predictChart1.axisRight.apply {
            // 우측 Y축은 사용함
            isEnabled = true
        }
        // X 축
        view.predictChart1.xAxis.apply {
            // x축 값은 투명으로
            textColor = Color.TRANSPARENT
            // 축라인, 그리드 사용하지 않음
            setDrawAxisLine(false)
            setDrawGridLines(false)
        }
    }
    fun drawStockChart2(item: MutableList<TrendChartModel>){
        Log.d("item.toString()", item.toString())
        // 평균값 구하기
        var average = 0F

        for( stock in item) {
            average += stock.price.toFloat()
        }
        average /= item.size

        // 그래프에 들어갈 데이터 준비
        val entries = ArrayList<Entry>()
        val colors = Stack<Int>()
        for (stock in item) {
            if (stock.price >= average) {
//                if (colors.isNotEmpty() && colors.peek() == Color.BLUE) {
//                    entries.add(Entry(stock.createdAt.toFloat(), average))
//                    colors.add(Color.TRANSPARENT)
//                    entries.add(Entry(stock.createdAt.toFloat(), average))
//                    colors.add(Color.RED)
//                }
                entries.add(Entry(stock.createdAt.toFloat(), stock.price.toFloat()))
                colors.add(Color.RED)
            } else {
//                if (colors.isNotEmpty() && colors.peek() == Color.RED) {
//                    entries.add(Entry(stock.createdAt.toFloat(), average))
//                    colors.add(Color.TRANSPARENT)
//                    entries.add(Entry(stock.createdAt.toFloat(), average))
//                    colors.add(Color.BLUE)
//                }
                entries.add(Entry(stock.createdAt.toFloat(), stock.price.toFloat()))
                colors.add(Color.BLUE)
            }
            Collections.sort(entries, EntryXComparator())
        }

        val dataSet = LineDataSet(entries, "").apply {
            setDrawCircles(false)
            color = Color.RED
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0F
            lineWidth = 1.5F
        }

        val lineData = LineData(dataSet)
        view.predictChart2.run {
            data = lineData
            description.isEnabled = false // 하단 Description Label 제거함
            invalidate() // refresh
        }

        val averageLine = LimitLine(average).apply {
            lineWidth = 1F
            enableDashedLine(4F, 10F, 10F)
            lineColor = Color.DKGRAY
        }

        // 범례
        view.predictChart2.legend.apply {
            isEnabled = false // 사용하지 않음
        }
        // Y 축
        view.predictChart2.axisLeft.apply {
            // 라벨, 축라인, 그리드 사용하지 않음
            setDrawLabels(false)
            setDrawAxisLine(false)
            setDrawGridLines(false)

            // 한계선 추가
            removeAllLimitLines()
            addLimitLine(averageLine)
        }
        view.predictChart2.axisRight.apply {
            // 우측 Y축은 사용함
            isEnabled = true
        }
        // X 축
        view.predictChart2.xAxis.apply {
            // x축 값은 투명으로
            textColor = Color.TRANSPARENT
            // 축라인, 그리드 사용하지 않음
            setDrawAxisLine(false)
            setDrawGridLines(false)
        }
    }
    fun drawStockChart3(item: MutableList<TrendChartModel>){
        Log.d("item.toString()", item.toString())
        // 평균값 구하기
        var average = 0F

        for( stock in item) {
            average += stock.price.toFloat()
        }
        average /= item.size

        // 그래프에 들어갈 데이터 준비
        val entries = ArrayList<Entry>()
        val colors = Stack<Int>()
        for (stock in item) {
            if (stock.price >= average) {
//                if (colors.isNotEmpty() && colors.peek() == Color.BLUE) {
//                    entries.add(Entry(stock.createdAt.toFloat(), average))
//                    colors.add(Color.TRANSPARENT)
//                    entries.add(Entry(stock.createdAt.toFloat(), average))
//                    colors.add(Color.RED)
//                }
                entries.add(Entry(stock.createdAt.toFloat(), stock.price.toFloat()))
                colors.add(Color.RED)
            } else {
//                if (colors.isNotEmpty() && colors.peek() == Color.RED) {
//                    entries.add(Entry(stock.createdAt.toFloat(), average))
//                    colors.add(Color.TRANSPARENT)
//                    entries.add(Entry(stock.createdAt.toFloat(), average))
//                    colors.add(Color.BLUE)
//                }
                entries.add(Entry(stock.createdAt.toFloat(), stock.price.toFloat()))
                colors.add(Color.BLUE)
            }
            Collections.sort(entries, EntryXComparator())
        }

        val dataSet = LineDataSet(entries, "").apply {
            setDrawCircles(false)
            color = Color.RED
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0F
            lineWidth = 1.5F
        }

        val lineData = LineData(dataSet)
        view.predictChart3.run {
            data = lineData
            description.isEnabled = false // 하단 Description Label 제거함
            invalidate() // refresh
        }

        val averageLine = LimitLine(average).apply {
            lineWidth = 1F
            enableDashedLine(4F, 10F, 10F)
            lineColor = Color.DKGRAY
        }

        // 범례
        view.predictChart3.legend.apply {
            isEnabled = false // 사용하지 않음
        }
        // Y 축
        view.predictChart3.axisLeft.apply {
            // 라벨, 축라인, 그리드 사용하지 않음
            setDrawLabels(false)
            setDrawAxisLine(false)
            setDrawGridLines(false)

            // 한계선 추가
            removeAllLimitLines()
            addLimitLine(averageLine)
        }
        view.predictChart3.axisRight.apply {
            // 우측 Y축은 사용함
            isEnabled = true
        }
        // X 축
        view.predictChart3.xAxis.apply {
            // x축 값은 투명으로
            textColor = Color.TRANSPARENT
            // 축라인, 그리드 사용하지 않음
            setDrawAxisLine(false)
            setDrawGridLines(false)
        }
    }
    fun getTrendPriceChart(period: Int){
        retrofit.getTrendData(period).enqueue(object : Callback<TrendList> {
            override fun onResponse(
                call: Call<TrendList>,
                response: Response<TrendList>
            ) {
                if(response.code()==200) {
                    if(response.body()!=null) {
                        var result1 = getTrendPredictData1(response.body()!!)
                        Log.d("result", result1.toString())
                        drawStockChart1(result1)

                        var result2 = getTrendPredictData2(response.body()!!)
                        Log.d("result", result2.toString())
                        drawStockChart2(result2)
                        var result3 = getTrendPredictData3(response.body()!!)
                        Log.d("result", result3.toString())
                        drawStockChart3(result3)
                    }
                    Log.d("trend/data/API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TrendList>, t: Throwable) {
                Log.d("API호출2222222", t.message.toString())
            }
        })
    }
    // 주식차트 데이터 가져오기
    private fun getTrendPredictData1(body:TrendList): MutableList<TrendChartModel> {
        var result = mutableListOf<TrendChartModel>()
        var x1 = 0
        for(item in body.n0.trendChart) {
            result.add((TrendChartModel(x1, item.end_price)))
            x1 +=100
        }
        Log.d("TrendPrice",result.toString())
        return result
    }
    private fun getTrendPredictData2(body:TrendList): MutableList<TrendChartModel> {
        var result = mutableListOf<TrendChartModel>()
        var x1 = 0
        for(item in body.n1.trendChart) {
            result.add((TrendChartModel(x1, item.end_price)))
            x1 += 100
        }
        Log.d("TrendPrice",result.toString())
        return result
    }
    private fun getTrendPredictData3(body:TrendList): MutableList<TrendChartModel> {
        var result = mutableListOf<TrendChartModel>()
        var x1 = 0
        for(item in body.n2.trendChart) {
            result.add((TrendChartModel(x1, item.end_price)))
            x1 += 100
        }
        Log.d("TrendPrice",result.toString())
        return result
    }

    override fun onClick(p0: View?) {

    }
}