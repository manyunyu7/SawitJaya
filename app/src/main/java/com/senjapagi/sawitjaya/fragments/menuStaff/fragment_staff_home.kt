package com.senjapagi.sawitjaya.fragments.menuStaff

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.senjapagi.sawitjaya.Logout
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.activity.staff.StaffOrderPage
import com.senjapagi.sawitjaya.fragments.menuSharing.fragment_profile
import com.senjapagi.sawitjaya.preference.const
import com.senjapagi.sawitjaya.util.NavDrawSetter
import com.senjapagi.sawitjaya.util.api
import com.senjapagi.sawitz.preference.Preference
import kotlinx.android.synthetic.main.custom_navdraw_staff.*
import kotlinx.android.synthetic.main.fragment_staff_home.*
import kotlinx.android.synthetic.main.layout_loading_transparent.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_staff_home.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_staff_home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_staff_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timeKeeper()
        getOrder()


        btnStaffHomeTakeOrder.setOnClickListener {
            moveActivity(StaffOrderPage())
        }
        //update profile bundle
        val bundle = Bundle()
        bundle.putString("source", "staff")
        val fragmentProfile =
            fragment_profile()
        fragmentProfile.arguments = bundle

        //------------Navdraw Operation-------------------//
        NavDrawSetter(context, activity?.window?.decorView!!).setNavDraw()
        staffHomeName.text = Preference(context!!).getPrefString(const.NAME)
        btnStaffToggleNavdraw.setOnClickListener { NavDrawToggle("open") }
        ndStaffBtnLogOut.setOnClickListener { val logout = Logout(context!!);logout.logoutDialog() }
        lyt_navdraw_staff_shadow.setOnClickListener { NavDrawToggle("close") }
        ndStaffBtnProfile.setOnClickListener { changeLayout(fragmentProfile) }

    }

    private fun moveActivity(dest: Activity) {
        NavDrawToggle("close")
        val intent = Intent(activity, dest::class.java)
        startActivity(intent)
    }

    private fun changeLayout(dest: Fragment) {
        NavDrawToggle("close")
        Handler().postDelayed({
            val fragmentManager: FragmentManager? = fragmentManager
            fragmentManager?.beginTransaction()?.replace(R.id.userFrameLayout, dest)
                ?.commit()
        }, 700)
    }

    private fun NavDrawToggle(indicator: String) {
        if (indicator.equals("open")) {
            lyt_navdraw_staff?.visibility = View.VISIBLE
            lyt_navdraw_staff?.animation =
                AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation)
            lyt_staff_home.background.alpha = 200
        } else {
            lyt_navdraw_staff?.animation =
                AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation_go)
            lyt_navdraw_staff?.visibility = View.GONE
            lyt_staff_home?.background?.alpha = 255
        }
    }

    private fun timeKeeper() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                val calendar = Calendar.getInstance()
                staffRealClock?.text =
                    SimpleDateFormat("HH:mm:ss", Locale.US).format(calendar.time).toString()
                handler.postDelayed(this, 1000)
            }
        }, 0)
        when (calendar[Calendar.HOUR_OF_DAY]) {
            in 0..11 -> {
                greetingMessage.text = "Selamat Pagi"
            }
            in 12..15 -> {
                greetingMessage.text = "Selamat Siang"
            }
            in 16..18 -> {
                greetingMessage.text = "Selamat Sore"
            }
            in 18..21 -> {
                greetingMessage.text = "Selamat Malam"
            }
            in 21..23 -> {
                greetingMessage.textSize = 15f
                greetingMessage.text = "Selamat Beristirahat"
            }
        }
    }

    private fun getOrder() {
        AndroidNetworking.get(api.STAFF_ORDER_ALL)
            .addHeaders("token", Preference(context!!).getPrefString(const.TOKEN))
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    anim_loading.visibility = View.GONE
                    val raz = response.getJSONArray("data")
                    if (raz.length() < 1) {
                        totalOrderMasuk.text = "0"
                    } else {
                        totalOrderMasuk.text = (raz.length()).toString()
                    }
                    if (!response.getBoolean("success_status"))
                        totalOrderMasuk.text = "Error"
                }

                override fun onError(anError: ANError?) {

                }

            })
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_staff_home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_staff_home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}