package com.senjapagi.sawitjaya.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.fragments.fragment_user_home


class HomeContainer : AppCompatActivity() {

    lateinit var fragmentUserHome: fragment_user_home
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_container)

        fragmentUserHome = fragment_user_home()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.userFrameLayout, fragmentUserHome)
            .commit()

    }

    fun transactionFragment(Fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.userFrameLayout, Fragment)
            .commit()
    }

}