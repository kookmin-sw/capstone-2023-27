package com.example.htss.Fragment
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.htss.Adapter.MainNewsAdapter
import com.example.htss.Adapter.HomeAdapter
import com.example.htss.Model.MainModel
import com.example.htss.Model.NewsModel
import com.example.htss.R
import com.example.htss.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var view: FragmentHomeBinding
/////////////배열선언
    private val categoryRankList = arrayListOf<MainModel>(
        MainModel("1","업종1", "+10%" ),
        MainModel("2","업종2","-5.8%"),
        MainModel("3","업종3","-10%")
    )

    private val themeRankList = arrayListOf<MainModel>(
        MainModel("1","테마1", "-2%" ),
        MainModel("2","테마2","-5.8%"),
        MainModel("3","테마3","-10%")
    )

    private val newsRankList = arrayListOf<NewsModel>(
        NewsModel("금리인상 중단 기대감…환율, 1230원 하향이탈 주목"),
        NewsModel("기술주 실적에 실망, 급락하다 보합권 혼조…나스"),
        NewsModel("뉴욕증시, 기업 실적 우려에 혼조…출근길 눈 '펑")
    )
//////////////////////////어댑터에 배열선언
    private val categoryRankListAdapter = HomeAdapter(categoryRankList)
    private val themeRankListAdapter = HomeAdapter(themeRankList)
    private val newsRankListAdapter = MainNewsAdapter(newsRankList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        view = FragmentHomeBinding.inflate(inflater, container, false)
/////////리사이클러뷰에 어댑터 붙이기
        view.recycle1.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = categoryRankListAdapter
        }

        view.recycle2.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false)
            adapter = themeRankListAdapter

        }

        view.recycle3.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false)
            adapter = newsRankListAdapter
            addItemDecoration(
                DividerItemDecoration(
                    view.recycle3.context,
                    LinearLayoutManager(context).orientation
                )
            )
        }

        categoryRankListAdapter.setItemClickListener(object:HomeAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val bundle = Bundle()
                bundle.apply {
                    bundle.putString("category_name", categoryRankList[position].name)
                    bundle.putString("category_percent", categoryRankList[position].percent)
                }
                Log.d("argument", bundle.toString())
                replaceFragment(CategoryDetailFragment(),bundle)
            }

        })

        themeRankListAdapter.setItemClickListener(object :HomeAdapter.OnItemClickListener{
            override fun onClick(v:View, position: Int){
                val bundle = Bundle()
                bundle.apply{
                    bundle.putString("theme_name", themeRankList[position].name)
                    bundle.putString("theme_percent", themeRankList[position].percent)
                }
                replaceFragment(ThemeDetailFragment(),bundle)

            }
        })

        view.seeMore1.setOnClickListener(this)
        view.seeMore2.setOnClickListener(this)
        view.rightArrow1.setOnClickListener(this)
        view.rightArrow2.setOnClickListener(this)
        view.searchBtn.setOnClickListener(this)

        return view.root
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
        when(p0?.id){
            R.id.see_more1 -> {
                val bundle =Bundle()
                Log.d("argument", "goodgood")
                bundle.putString("focus","good")
                replaceFragment(ListFragment(), bundle)
            }
            R.id.see_more2 ->{
                val bundle = Bundle()
                bundle.putString("foccus", "hue")
                replaceFragment(ListFragment(), bundle)
            }
            R.id.right_arrow1 ->{
                val bundle = Bundle()
                bundle.putString("foccus", "hue")
                replaceFragment(ListFragment(), bundle)
            }
            R.id.right_arrow2 ->{
                val bundle = Bundle()
                bundle.putString("foccus", "hue")
                replaceFragment(ListFragment(), bundle)
            }
            R.id.search_btn -> {
                val bundle = Bundle()
                bundle.putString("keyword", view.editText.getText().toString())
                view.editText.text = null
                replaceFragment(KeyWordFragment(),bundle)
            }
        }

    }
}
