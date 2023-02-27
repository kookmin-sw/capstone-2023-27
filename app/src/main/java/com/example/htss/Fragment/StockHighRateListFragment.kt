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
import com.example.htss.Adapter.StockHighRaiseListAdapter
import com.example.htss.Adapter.StockRaiseListAdapter
import com.example.htss.Model.StockRaiseListModel
import com.example.htss.Model.ThemelistModel
import com.example.htss.R
import com.example.htss.Retrofit.Model.StockHighRateList
import com.example.htss.Retrofit.RetrofitClient
import com.example.htss.databinding.FragmentStockHighRateListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class StockHighRateListFragment : Fragment(), View.OnClickListener {
    var num = 200
    private lateinit var view: FragmentStockHighRateListBinding
    private val retrofit = RetrofitClient.create()

    private var stockRaiseRateList = arrayListOf<StockRaiseListModel>()
    private var stockRaiseRateAdapter = StockHighRaiseListAdapter(stockRaiseRateList)
    val dec = DecimalFormat("#,###.##")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentStockHighRateListBinding.inflate(inflater, container, false)
        getStockHighRateList(num)
        view.stockRaiselistRank.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = stockRaiseRateAdapter


            addItemDecoration(
                DividerItemDecoration(
                    view.stockRaiselistRank.context,
                    LinearLayoutManager(context).orientation
                )
            )


//            addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    super.onScrolled(recyclerView, dx, dy)
//                    val lastVisibleItemPosition =
//                        (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() // 화면에 보이는 마지막 아이템의 position
//                    val itemTotalCount =
//                        recyclerView.adapter!!.itemCount - 1 // 어댑터에 등록된 아이템의 총 개수 -1
//
//                    //최하단에 도달 1
//                    if (!view.stockRaiselistRank.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount && itemTotalCount + 1 >= num) {
//                        num += 5
//                        getStockHighRateList(num)
//                    }
//                }
//            })

            stockRaiseRateAdapter.setItemClickListener(object: StockHighRaiseListAdapter.OnItemClickListener{
                override fun onClick(v: View, position: Int) {
                    val bundle = Bundle()
                    bundle.apply{
                        bundle.putString("stock_ticker", stockRaiseRateList[position].StockRaiseTicker)
                        bundle.putString("stock_name", stockRaiseRateList[position].StockRaisename)
                    }
                    replaceFragment(StockFragment(),bundle)
                }
            })
            return view.root
        }
    }
    fun getStockHighRateList(num: Int){
        retrofit.getStockHighRate(num).enqueue(object: Callback<StockHighRateList> {
            override fun onResponse(call: Call<StockHighRateList>, response: Response<StockHighRateList>) {
                if(response.code() == 200){
                    addStockHighRateList(response.body()!!)
                    Log.d("API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StockHighRateList>, t: Throwable) {
                Log.d("API호출2", t.message.toString())
            }
        })
    }
    private fun addStockHighRateList(body: StockHighRateList?){
        stockRaiseRateList.clear()
        if(body.isNullOrEmpty()){

        }
        else{
            for(item in body) {
                if (item.rate >= 0) {
                    stockRaiseRateList.add(
                        StockRaiseListModel(
                            item.ticker,
                            item.company_name,
                            dec.format(item.end_price).toString(),
                            "+"+item.rate.toString()+"%"
                        )
                    )
                }
                else{
                    stockRaiseRateList.add(
                        StockRaiseListModel(
                            item.ticker,
                            item.company_name,
                            dec.format(item.end_price).toString(),
                            item.rate.toString()+"%"
                        )
                    )
                }
            }
        }
        stockRaiseRateAdapter.notifyDataSetChanged()
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