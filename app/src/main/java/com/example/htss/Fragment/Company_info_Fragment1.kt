package com.example.htss.Fragment

import android.os.Bundle
import android.util.Log
import android.view.FocusFinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.htss.R
import com.example.htss.Retrofit.Model.CompanyInfoListItem
import com.example.htss.Retrofit.RetrofitClient
import com.example.htss.databinding.FragmentCompanyInfo1Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Company_info_Fragment1 : Fragment() {

    private lateinit var view: FragmentCompanyInfo1Binding
    private val retrofit = RetrofitClient.create()
    private var StockTicker = ""
    private var Info = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentCompanyInfo1Binding.inflate(inflater, container, false)

        StockTicker = arguments?.getString("stock_ticker").toString()

        Info = arguments?.getString("Focus").toString()
        Log.d("Gg",Info)

        if(Info == "hu"){
            view.companyInfo2 .isFocusableInTouchMode = true
            view.companyInfo2.requestFocus()
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
                    Log.d("stock/info/API호출31313131", response.raw().toString())
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
        view.companyInfo2.text = body.company_info
    }
}