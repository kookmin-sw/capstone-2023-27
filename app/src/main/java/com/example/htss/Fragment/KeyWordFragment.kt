package com.example.htss.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.htss.databinding.FragmentKeyWordBinding


class KeyWordFragment : Fragment() {
    private lateinit var view: FragmentKeyWordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentKeyWordBinding.inflate(inflater, container, false)
        return view.root
    }
}