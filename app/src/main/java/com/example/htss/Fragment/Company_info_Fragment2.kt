package com.example.htss.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.htss.R
import com.example.htss.Retrofit.RetrofitClient
import com.example.htss.databinding.FragmentCompanyInfo1Binding
import com.example.htss.databinding.FragmentCompanyInfo2Binding

class Company_info_Fragment2 : Fragment() {

    private lateinit var view: FragmentCompanyInfo2Binding
    private val retrofit = RetrofitClient.create()
    private var StockTicker = " "


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentCompanyInfo2Binding.inflate(inflater, container, false)




        return view.root
    }
}