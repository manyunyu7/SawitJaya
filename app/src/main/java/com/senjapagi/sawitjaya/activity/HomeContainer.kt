package com.senjapagi.sawitjaya.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.fragments.menuUser.fragment_user_home


class HomeContainer : AppCompatActivity() {

    lateinit var fragmentUserHome: fragment_user_home
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_container)

        fragmentUserHome =
            fragment_user_home()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.userFrameLayout, fragmentUserHome)
            .commit()
  
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }
    fun transactionFragment(Fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.userFrameLayout, Fragment)
            .commit()
    }

}