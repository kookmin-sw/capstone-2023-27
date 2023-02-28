package com.example.htss.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.htss.R
import com.example.htss.Retrofit.Model.CompanyInfoListItem
import com.example.htss.Retrofit.RetrofitClient
import com.example.htss.databinding.FragmentCompanyInfo1Binding
import com.example.htss.databinding.FragmentCompanyInfo2Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class Company_info_Fragment2 : Fragment() {

    private lateinit var view: FragmentCompanyInfo2Binding
    private val retrofit = RetrofitClient.create()
    private var StockTicker = " "
    private var Info2 = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentCompanyInfo2Binding.inflate(inflater, container, false)


        StockTicker = arguments?.getString("stock_ticker").toString()
        Info2 = arguments?.getString("focuss").toString()
        if(Info2 == "huu"){
            view.invest.isFocusableInTouchMode = true
            view.invest.requestFocus()
        }
        getCompanyInfo(StockTicker)
        return view.root
    }

    fun getCompanyInfo(ticker: String){
        retrofit.getCompanyInfo(ticker).enqueue(object : Callback<CompanyInfoListItem> {
            override fun onResponse(
                call: Call<CompanyInfoListItem>,
                response: Response<CompanyInfoListItem>
            ) {
                if(response.code()==200) {
                    if(response.body() != null ) addcompanyInfo(response.body()!!)
                    Log.d("stock/info/API호출!!!!!!", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<CompanyInfoListItem>, t: Throwable) {
                Log.d("API호출3333", t.message.toString())
            }
        })
    }
    private fun addcompanyInfo(body: CompanyInfoListItem) {
        var a = 0
        val dec = DecimalFormat("#,###.##")
        view.totalCurrent2.text = dec.format(body.market_cap).toString() + "억원"
        if(body.per == a.toFloat()) {
            view.per2.text = "N/A"
        }
        else{
            view.per2.text = body.per.toString() + "배"
        }
        if(body.eps == a){
            view.eps2.text = "N/A"
        }
        else{
            view.eps2.text = dec.format(body.eps).toString()+"원"
        }
        if(body.est_per == a.toFloat()){
            view.estPer2.text = "N/A"
        }
        else{
            view.estPer2.text = body.est_per.toString()+"배"
        }
        if(body.est_eps == a){
            view.estEps2.text = "N/A"
        }
        else{
            view.estEps2.text = dec.format(body.est_eps).toString()+"원"
        }
        if(body.pbr == a.toFloat()){
            view.pbr2.text ="N/A"
        }
        else{
            view.pbr2.text = body.pbr.toString()+"배"
        }
        if(body.dvr == a.toFloat()){
            view.dvr.text = "N/A"
        }
        else{
            view.dvr.text = body.dvr.toString()+"%"
        }
    }


}

