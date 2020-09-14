package com.senjapagi.sawitjaya.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.senjapagi.sawitjaya.Logout
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.activity.staff.StaffOrderPage
import com.senjapagi.sawitjaya.preference.const
import com.senjapagi.sawitz.preference.Preference
import kotlinx.android.synthetic.main.activity_admin_home.*
import kotlinx.android.synthetic.main.fragment_user_home.*
import kotlinx.android.synthetic.main.fragment_user_home.realDate
import java.text.SimpleDateFormat
import java.util.*

class AdminHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)
        adminHomeName.text= Preference(this).getPrefString(const.NAME)
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                val calendar = Calendar.getInstance()
                realClock?.text =
                    SimpleDateFormat("HH:mm:ss", Locale.US).format(calendar.time).toString()
                handler.postDelayed(this, 1000)
            }
        }, 0)
        realDate.text =
            SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Calendar.getInstance().time).toString()

        btnManageHarga.setOnClickListener {
            startActivity(Intent(this, AdminManageHarga::class.java))
        }

        btnLogout.setOnClickListener {
            Logout(this).logoutDialog()
        }

        btnManageOrder.setOnClickListener {
            val intent = Intent(this, StaffOrderPage::class.java)
            intent.putExtra("source","admin")
            startActivity(intent)

        }
    }
}