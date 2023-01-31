package com.example.htss.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.htss.R
import com.example.htss.databinding.FragmentThemeDetailBinding


class ThemeDetailFragment : Fragment() {
    private lateinit var view: FragmentThemeDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = FragmentThemeDetailBinding.inflate(inflater, container, false)
        return view.root
    }
}