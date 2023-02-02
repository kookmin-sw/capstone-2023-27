package com.example.htss.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.htss.databinding.FragmentStockBinding

class StockFragment : Fragment() {

    private lateinit var view: FragmentStockBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentStockBinding.inflate(inflater, container, false)

        view.back.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return view.root
    }
}