package com.example.htss.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.htss.R
import com.example.htss.databinding.FragmentAllListBinding
import kotlinx.android.synthetic.main.fragment_all_list.*

class AllListFragment : Fragment(), View.OnClickListener {

    private lateinit var view: FragmentAllListBinding

    var ThemaFocus = ""
    var first = "sector"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentAllListBinding.inflate(inflater, container, false)

        when(first) {
           "sector" -> replaceFragment(SectorListFragment())
        }
        view.sector.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        view.theme.setTextColor(ContextCompat.getColor(requireContext(), R.color.hmmm))


        ThemaFocus = arguments?.getString("foccus").toString()

        if(ThemaFocus == "hue"){
            replaceFragment(ThemeListFragment())
            view.theme.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            view.sector.setTextColor(ContextCompat.getColor(requireContext(), R.color.hmmm))
        }

        view.sector.setOnClickListener(this)
        view.theme.setOnClickListener(this)


        return view.root
    }


    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.sector -> {
                view.sector.setTextColor(ContextCompat.getColor(requireContext(), R.color.hmm))
                view.theme.setTextColor(ContextCompat.getColor(requireContext(), R.color.hmmm))
                replaceFragment(SectorListFragment())
            }
            R.id.theme -> {
                view.theme.setTextColor(ContextCompat.getColor(requireContext(), R.color.hmm))
                view.sector.setTextColor(ContextCompat.getColor(requireContext(), R.color.hmmm))
                replaceFragment(ThemeListFragment())
            }
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.all_list_frame_layout, fragment)
            .commit()
    }

}






