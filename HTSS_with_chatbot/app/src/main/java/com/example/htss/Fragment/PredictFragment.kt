package com.example.htss.Fragment

import com.example.htss.databinding.FragmentPredictBinding
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class PredictFragment : Fragment() {

    private lateinit var view: FragmentPredictBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentPredictBinding.inflate(inflater, container, false)
        return view.root
    }
}