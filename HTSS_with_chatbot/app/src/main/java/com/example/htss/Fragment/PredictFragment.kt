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
                    }
                    1-> {
                        getTrendData(15)
                    }
                    2->{
                        getTrendData(30)
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


    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }
}