package com.senjapagi.sawitjaya.fragments

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
import com.senjapagi.sawitjaya.activity.HomeContainer
import com.senjapagi.sawitjaya.Logout
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.activity.UserSellTBS
import com.senjapagi.sawitjaya.preference.const
import com.senjapagi.sawitz.preference.Preference
import kotlinx.android.synthetic.main.custom_navdraw.*
import kotlinx.android.synthetic.main.fragment_user_home.*
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_user_home.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_user_home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val calendar = Calendar.getInstance()
    lateinit var main: HomeContainer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                val calendar = Calendar.getInstance()
                realClock?.text =
                    SimpleDateFormat("HH:mm:ss", Locale.US).format(calendar.time).toString()
                handler.postDelayed(this, 1000)
            }
        }, 0)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        realDate.text = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(calendar.time).toString()
        NavDrawToggle("close")

        homeName.text =
            activity?.applicationContext?.let { Preference(it).getPrefString(const.NAME) }

        btnAllTransaction.setOnClickListener { changeLayout(fragment_user_all_transaction()) }
        btnToggleNavdraw.setOnClickListener { NavDrawToggle("open") }
        btnCloseNavDraw.setOnClickListener { NavDrawToggle("close") }
        btnSell.setOnClickListener { moveActivity(UserSellTBS()) }
        btnAllTransaction.setOnClickListener { changeLayout(fragment_user_all_transaction()) }
        lyt_navdraw_shadow.setOnClickListener { NavDrawToggle("close") }
        ndBtnLogOut.setOnClickListener { val logout = Logout(activity?.applicationContext!!);logout.logoutDialog() }
        ndBtnProfile.setOnClickListener { changeLayout(fragment_profile()) }
        ndBtnHome.setOnClickListener { changeLayout(fragment_user_home()) }
        ndBtnHistory.setOnClickListener { changeLayout(fragment_user_all_transaction()) }
        ndBtnAbout.setOnClickListener { changeLayout(fragment_about()) }
        ndBtnSell.setOnClickListener { moveActivity(UserSellTBS()) }

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
            lyt_navdraw?.visibility = View.VISIBLE
            lyt_navdraw?.animation =
                AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation)
            lyt_user_home.background.alpha = 200
        } else {
            lyt_navdraw?.animation =
                AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation_go)
            lyt_navdraw?.visibility = View.GONE
            lyt_user_home?.background?.alpha = 255
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_home, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_user_home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_user_home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}