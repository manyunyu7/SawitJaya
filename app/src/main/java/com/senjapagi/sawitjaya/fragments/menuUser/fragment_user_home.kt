package com.senjapagi.sawitjaya.fragments.menuUser

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.senjapagi.sawitjaya.Logout
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.activity.Tutorial
import com.senjapagi.sawitjaya.activity.user.HomeContainer
import com.senjapagi.sawitjaya.activity.user.UserOrderNew
import com.senjapagi.sawitjaya.activity.UserSellTBS
import com.senjapagi.sawitjaya.fragments.menuSharing.fragment_about
import com.senjapagi.sawitjaya.fragments.menuSharing.fragment_profile
import com.senjapagi.sawitjaya.preference.const
import com.senjapagi.sawitjaya.util.NavDrawSetter
import com.senjapagi.sawitjaya.util.Permissions
import com.senjapagi.sawitjaya.util.api
import com.senjapagi.sawitz.preference.Preference
import kotlinx.android.synthetic.main.custom_navdraw.*
import kotlinx.android.synthetic.main.fragment_user_home.*
import kotlinx.android.synthetic.main.layout_error_dialog.*
import kotlinx.android.synthetic.main.layout_loading_transparent.*
import kotlinx.android.synthetic.main.xd_gps_error.*
import org.json.JSONObject
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

    lateinit var perms1: String
    lateinit var perms2: String
    var checkVal1: Int = 0
    var checkVal2: Int = 0

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

    override fun onResume() {
        super.onResume()
        updatePrice()
        getTransaksi()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        realDate.text = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(calendar.time).toString()
        NavDrawToggle("close")

        tvTBSPrice?.text = Preference(requireActivity()).getPrefString(const.PRICE)
        tvMarginPrice?.text = Preference(requireActivity()).getPrefString(const.MARGIN)

        srlUserHome.setOnRefreshListener {
            srlUserHome.isRefreshing=false
            updatePrice()
            getTransaksi()
        }

        homeName.text = activity?.applicationContext?.let {
            Preference(it).getPrefString(const.NAME)
        }

        updatePrice()
        getTransaksi()

        btnRefreshPrice.setOnClickListener {
            updatePrice()
            getTransaksi()
        }
        var gpsStatPermission = false
        //Hide GPS Error Layout when GPS is Available
        if (checkVal1 == PackageManager.PERMISSION_GRANTED && checkVal2 == PackageManager.PERMISSION_GRANTED) {
            lyt_gps_error.visibility = View.GONE
        }
        //Check Loop until GPS Permission Granted
        val handlerA = Handler()
        handlerA.postDelayed(object : Runnable {
            var gpsStatPermission = false
            override fun run() {
                checkPermission()
                if (checkVal1 == PackageManager.PERMISSION_GRANTED && checkVal2 == PackageManager.PERMISSION_GRANTED) {
                    gpsStatPermission = true
                    lyt_gps_error.visibility = View.GONE
                    handlerA.removeCallbacksAndMessages(null)
                    gpsStatPermission = true
                } else {
                    lyt_gps_error.visibility = View.VISIBLE
                    gpsStatPermission = false
                }
                handlerA.postDelayed(this, 500)
                if (gpsStatPermission) {
                    handlerA.removeCallbacksAndMessages(null)
                }
            }
        }, 0)

        btnGivePermission.setOnClickListener {
            Permissions(context, activity).grantAccess()
        }
        btnDenyPermission.setOnClickListener {
            requireActivity().finishAffinity() //Close App if the permission is denied
        }

        btnSend.setOnClickListener {
            makeToast("Fitur ini akan seger hadir")
        }


        NavDrawSetter(context, activity?.window?.decorView!!).setNavDraw()
        btnToggleNavdraw.setOnClickListener { NavDrawToggle("open") }
        btnCloseNavDraw.setOnClickListener { NavDrawToggle("close") }
        btnSell.setOnClickListener { moveActivity(UserSellTBS()) }
        btnAllTransaction.setOnClickListener {moveActivity(UserOrderNew()) }
        lyt_navdraw_shadow.setOnClickListener { NavDrawToggle("close") }
        ndBtnLogOut.setOnClickListener { val logout = Logout(requireContext());logout.logoutDialog() }
        ndBtnProfile.setOnClickListener { changeLayout(fragment_profile()) }
        ndBtnHome.setOnClickListener { changeLayout(fragment_user_home()) }
        ndBtnHistory.setOnClickListener { moveActivity(UserOrderNew()) }
        ndBtnAbout.setOnClickListener {
            val intent = Intent(activity, Tutorial::class.java)
            intent.putExtra("source","user")
            startActivity(intent)
        }
        ndBtnSell.setOnClickListener { moveActivity(UserSellTBS()) }
        ndBtnSend.setOnClickListener {  makeToast("Fitur ini akan seger hadir") }


        val timeOfDay = calendar[Calendar.HOUR_OF_DAY]

        if (timeOfDay in 0..11) {
            greetingMessage.text = "Selamat Pagi"
        } else if (timeOfDay in 12..15) {
            greetingMessage.text = "Selamat Siang"
        } else if (timeOfDay in 16..18) {
            greetingMessage.text = "Selamat Sore"
        } else if (timeOfDay in 18..21) {
            greetingMessage.text = "Selamat Malam"
        } else if (timeOfDay in 21..23) {
            greetingMessage.textSize = 15f
            greetingMessage.text = "Selamat Beristirahat"
        }
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
        }, 300)
    }


    private fun NavDrawToggle(indicator: String) {
        if (indicator.equals("open")) {
            lyt_navdraw?.visibility = View.VISIBLE
            lyt_navdraw?.animation =
                AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation)
          srlUserHome.background.alpha = 200
        } else {
            lyt_navdraw?.animation =
                AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation_go)
            lyt_navdraw?.visibility = View.GONE
            srlUserHome?.background?.alpha = 255
        }
    }

    private fun checkPermission() {
        perms1 = Manifest.permission.ACCESS_COARSE_LOCATION
        perms2 = Manifest.permission.ACCESS_FINE_LOCATION
        checkVal1 = activity?.checkCallingOrSelfPermission(perms1)!!
        checkVal2 = activity?.checkCallingOrSelfPermission(perms2)!!
    }

    private fun updatePrice() {
        anim_loading.visibility = View.VISIBLE
        AndroidNetworking.post(api.CURRENT_PRICE)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    anim_loading.visibility = View.GONE
                    val raz = response?.getJSONObject("data")
                    if (response?.getBoolean("success_status")!!) {
                        val price = raz?.getString("price")
                        val margin = raz?.getString("price_grade")


                        Preference(activity!!).save(const.PRICE, price.toString())
                        Preference(activity!!).save(const.MARGIN, margin.toString())
                        tvTBSPrice?.text = Preference(activity!!).getPrefString(const.PRICE)
                        tvMarginPrice?.text = Preference(activity!!).getPrefString(const.MARGIN)
                    } else {
                        makeToast("Gagal Mengambil Data Harga Terbaru")
                    }
                }

                override fun onError(anError: ANError?) {
                    anim_loading.visibility = View.GONE
                    errorDialog("Gagal Terhubung Dengan Server","Terjadi Kesalahan saat mengambil data transaksi")
                }

            })
    }

    private fun makeToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    private fun getTransaksi(){
        AndroidNetworking.get(api.USER_HOME)
            .addHeaders("token",Preference(requireContext()).getPrefString(const.TOKEN))
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener{
                override fun onResponse(response: JSONObject) {
                    if(response.getBoolean("success_status")){
                        val totalTransaction = response.getJSONObject("data").getString("total_jual")
                        invoiceActive.text = totalTransaction + " Invoice"
                    }else{
                        errorDialog("Gagal Terhubung Dengan Server","Terjadi Kesalahan saat mengambil data transaksi")
                    }
                }

                override fun onError(anError: ANError?) {
                    errorDialog("Gagal Terhubung Dengan Server","Periksa Koneksi Internet Anda dan Coba Lagi Nanti")
                }

            })
    }

    private fun errorDialog(title: String, message: String) {
        xError.visibility = View.VISIBLE
        xError.animation =
            AnimationUtils.loadAnimation(context, R.anim.item_animation_falldown)
        xErrorTitle.text = title
        xErrorContent.text=message
        xErrorPButton.setOnClickListener {
            xError.visibility = View.GONE
            xError.animation =
                AnimationUtils.loadAnimation(context, R.anim.item_animation_fallup)
            getTransaksi()
            updatePrice()
        }
        xErrorNeutralButton.setOnClickListener {
            xError.animation=
                AnimationUtils.loadAnimation(context, R.anim.item_animation_fallup)
            xError.visibility=View.GONE
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