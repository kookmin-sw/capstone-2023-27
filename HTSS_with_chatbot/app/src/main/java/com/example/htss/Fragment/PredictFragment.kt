package com.example.htss.Fragment

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.htss.Adapter.PredictListAdapter
import com.example.htss.Model.PredictListModel
import com.example.htss.R
import com.example.htss.Retrofit.RetrofitClient

class PredictFragment : Fragment(), View.OnClickListener {

    var selectedPosition = 0

    private lateinit var view: FragmentPredictBinding
    private val retrofit = RetrofitClient.create()

    private var predictList = mutableListOf<PredictListModel>()
    private var predictAdapter = PredictListAdapter(predictList)

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
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("MyTag", "onNothingSelected")
            }
        }

        view.risePredict1.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
            adapter = predictAdapter

            predictAdapter.setItemClickListener(object : PredictListAdapter.OnItemClickListener{
                override fun onClick(v: View, position: Int) {
                    val bundle = Bundle()
                    bundle.apply {
                        bundle.putString("stock_ticker", predictList[position].ticker)
                        bundle.putString("stock_name", predictList[position].company)
                    }
                    replaceFragment(StockFragment(), bundle)
                }
            })
        }
        view.risePredict2.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
            adapter = predictAdapter

            predictAdapter.setItemClickListener(object : PredictListAdapter.OnItemClickListener{
                override fun onClick(v: View, position: Int) {
                    val bundle = Bundle()
                    bundle.apply {
                        bundle.putString("stock_ticker", predictList[position].ticker)
                        bundle.putString("stock_name", predictList[position].company)
                    }
                    replaceFragment(StockFragment(), bundle)
                }
            })
        }

        view.risePredict3.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
            adapter = predictAdapter

            predictAdapter.setItemClickListener(object : PredictListAdapter.OnItemClickListener{
                override fun onClick(v: View, position: Int) {
                    val bundle = Bundle()
                    bundle.apply {
                        bundle.putString("stock_ticker", predictList[position].ticker)
                        bundle.putString("stock_name", predictList[position].company)
                    }
                    replaceFragment(StockFragment(), bundle)
                }
            })
        }

        view.risePredict4.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
            adapter = predictAdapter

            predictAdapter.setItemClickListener(object : PredictListAdapter.OnItemClickListener{
                override fun onClick(v: View, position: Int) {
                    val bundle = Bundle()
                    bundle.apply {
                        bundle.putString("stock_ticker", predictList[position].ticker)
                        bundle.putString("stock_name", predictList[position].company)
                    }
                    replaceFragment(StockFragment(), bundle)
                }
            })
        }

        view.risePredict5.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
            adapter = predictAdapter

            predictAdapter.setItemClickListener(object : PredictListAdapter.OnItemClickListener{
                override fun onClick(v: View, position: Int) {
                    val bundle = Bundle()
                    bundle.apply {
                        bundle.putString("stock_ticker", predictList[position].ticker)
                        bundle.putString("stock_name", predictList[position].company)
                    }
                    replaceFragment(StockFragment(), bundle)
                }
            })
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