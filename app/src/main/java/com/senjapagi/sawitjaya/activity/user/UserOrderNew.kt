package com.senjapagi.sawitjaya.activity.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.senjapagi.sawitjaya.BaseActivity
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.fragments.order.activeOrderFragment
import com.senjapagi.sawitjaya.fragments.order.allOrderFragment
import com.senjapagi.sawitjaya.fragments.order.canceledOrderFragment
import com.senjapagi.sawitjaya.fragments.order.finishedOrderFragment
import kotlinx.android.synthetic.main.activity_staff_order_page.*

class UserOrderNew : BaseActivity() {

    var count = supportFragmentManager.backStackEntryCount
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.adjustFontScale(resources.configuration)
        setContentView(R.layout.activity_staff_order_page)
        staffOrderNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        addFragment(allOrderFragment())
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_staff_all_order -> {
                    addFragment(allOrderFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_staff_canceled -> {
                    addFragment(canceledOrderFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_staff_success ->{
                    addFragment(finishedOrderFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_staff_processed_order ->{
                    addFragment(activeOrderFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.design_bottom_sheet_slide_in,
                R.anim.design_bottom_sheet_slide_out
            )
            .replace(R.id.content, fragment, fragment.javaClass.simpleName)
            .commit()
    }
    override fun onBackPressed() {

        if (count == 0) {
            startActivity(Intent(this,HomeContainer::class.java))
            finish()
            //additional code
        } else {
            count = 0;
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.design_bottom_sheet_slide_in,
                    R.anim.design_bottom_sheet_slide_out
                )
                .replace(R.id.content, allOrderFragment(), allOrderFragment().javaClass.simpleName)
                .commit()
        }
    }


}