package com.example.htss.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.example.htss.Fragment.HomeFragment
import com.example.htss.Fragment.SettingFragment
import com.example.htss.R
import com.example.htss.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener, View.OnClickListener {

    private lateinit var view:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        view = ActivityMainBinding.inflate(layoutInflater)
        setContentView(view.root)
        replaceFragment(HomeFragment())
        view.navigationBar.setOnItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.homeFragment -> {
//                showProgress(true)
//                thread(start = true) {
//                    Thread.sleep(3000)
//                    runOnUiThread {
//                        showProgress(false)
//                        progress()
//                    }
                    val fragment = supportFragmentManager.fragmentFactory.instantiate(
                        classLoader,
                        HomeFragment::class.java.name
                    )
                    replaceFragment(fragment)
                    Log.d("framgent", "home")
                }

//            R.id.listFragment -> {
//                val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, AllListFragment::class.java.name)
//                replaceFragment(fragment)
//                Log.d("framgent","category")
//            }
            R.id.settingFragment -> {
                val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, SettingFragment::class.java.name)
                replaceFragment(fragment)
                Log.d("framgent","setting")
            }
        }
        return false
    }


    // 프래그먼트 전환 함수
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.mainFrameLayout, fragment).commit()
    }

    override fun onClick(p0: View?) {

    }
}

