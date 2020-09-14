package com.senjapagi.sawitjaya.activity.staff

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.senjapagi.sawitjaya.BaseActivity
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.fragments.orderStaff.orderStaffAll
import com.senjapagi.sawitjaya.fragments.orderStaff.orderStaffCancelled
import com.senjapagi.sawitjaya.fragments.orderStaff.orderStaffProcessed
import com.senjapagi.sawitjaya.fragments.orderStaff.orderStaffSuccessfull
import kotlinx.android.synthetic.main.activity_staff_order_page.*

class StaffOrderPage : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.adjustFontScale(resources.configuration)
        setContentView(R.layout.activity_staff_order_page)
        staffOrderNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        addFragment(orderStaffAll())
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_staff_all_order -> {
                    addFragment(orderStaffAll())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_staff_canceled -> {
                    addFragment(orderStaffCancelled())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_staff_success->{
                    addFragment(orderStaffSuccessfull())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_staff_processed_order->{
                    addFragment(orderStaffProcessed())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun addFragment(fragment: Fragment) {
        val bundle = Bundle()
        val myMessage = intent.getStringExtra("source")
        bundle.putString("source", myMessage)
        fragment.arguments=bundle
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .replace(R.id.content, fragment, fragment.javaClass.simpleName)
            .commit()
    }


}