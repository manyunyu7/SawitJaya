package com.senjapagi.sawitjaya.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import com.senjapagi.sawitjaya.BaseActivity
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.modelAndAdapter.UserOrderPagerAdapter
import kotlinx.android.synthetic.main.activity_user_all_transaction.*

class UserTransaction : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_user_all_transaction)
        super.onCreate(savedInstanceState)
        super.adjustFontScale(resources.configuration)

        toolBar.title = "List Request Jemput"
        setSupportActionBar(toolBar)

        //this line set VP adapter
        val fragmentAdapter =
            UserOrderPagerAdapter(
                supportFragmentManager
            )
        viewPager.adapter=fragmentAdapter

        orderTabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        //wrapping my pager with tabLayout
        orderTabLayout.setupWithViewPager(viewPager)


    }
}