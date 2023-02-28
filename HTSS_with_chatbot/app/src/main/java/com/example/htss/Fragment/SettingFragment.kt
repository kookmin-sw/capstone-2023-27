package com.example.htss.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.htss.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    private lateinit var view: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentSettingBinding.inflate(inflater, container, false)
        return view.root
    }
}