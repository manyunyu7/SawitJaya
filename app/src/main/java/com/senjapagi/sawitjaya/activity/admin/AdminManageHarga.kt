package com.senjapagi.sawitjaya.activity.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.AnimationUtils.loadAnimation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.modelAndAdapter.adapterAllOrderStaff
import com.senjapagi.sawitjaya.modelAndAdapter.adapterPrice
import com.senjapagi.sawitjaya.modelAndAdapter.modelPrice
import com.senjapagi.sawitjaya.modelAndAdapter.modelReqOrder
import com.senjapagi.sawitjaya.preference.const
import com.senjapagi.sawitjaya.util.api
import com.senjapagi.sawitz.preference.Preference
import kotlinx.android.synthetic.main.activity_admin_home.*
import kotlinx.android.synthetic.main.activity_admin_manage_harga.*
import kotlinx.android.synthetic.main.fragment_user_home.*
import kotlinx.android.synthetic.main.fragment_user_home.realDate
import kotlinx.android.synthetic.main.layout_edit_price.*
import kotlinx.android.synthetic.main.layout_error_dialog.*
import kotlinx.android.synthetic.main.layout_loading_transparent.*
import kotlinx.android.synthetic.main.layout_success_dialog.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdminManageHarga : AppCompatActivity() {
    var data = ArrayList<modelPrice>()
    lateinit var adapterPrice: adapterPrice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_manage_harga)

        btnBack.setOnClickListener {
            super.onBackPressed()
        }
        srlPrice.setOnRefreshListener {
            srlPrice.isRefreshing = false
            getPrice()
        }
        getPrice()

        addPriceTriggerButton.setOnClickListener {
            xEditPrice.visibility = View.VISIBLE
            xEditPrice.animation = loadAnimation(this, R.anim.item_animation_falldown)
            xEditPriceNeutralButton.setOnClickListener {
                xEditPrice.visibility = View.GONE
                xEditPrice.animation = loadAnimation(this, R.anim.item_animation_fallup)
            }
            xEditPriceAddPrice.setOnClickListener {
                var error = false
                if(xEditPriceInputMargin.text.toString().isBlank()){
                    xEditPriceInputMargin.error="Mohon Lengkapi Bidang Ini"
                    error=true
                }
                if( xEditPriceInputPrice.text.toString().isBlank()){
                    xEditPriceInputPrice.error="Mohon Lengkapi Bidang Ini"
                    error=true
                }
                if(!error){
                    xEditPrice.visibility = View.GONE
                    xEditPrice.animation = loadAnimation(this, R.anim.item_animation_fallup)
                    addPrice()
                }
            }
        }
    }

//
//    api/admin/price/create
//    (post)
//    price (required), price_grade (opt, kalau null ikut yang lama), weight_grade (opt juga)
//    (header) token

    private fun addPrice() {
        var priceMargin = xEditPriceInputPrice.text.toString().toDouble()
        priceMargin /= 100.0
        AndroidNetworking.post(api.BASE_URL + "admin/price/create")
            .addBodyParameter("price", xEditPriceInputPrice.text.toString())
            .addBodyParameter("price_grade", priceMargin.toString())
            .addHeaders("token", Preference(this).getPrefString(const.TOKEN))
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    if (response.getBoolean("success_status")) {
                        successDialog("Berhasil","Berhasil Menambahkan Harga",0)
                        getPrice()
                    } else {
                        errorDialog("Gagal","Gagal Menambahkan Harga karena "+response.getString("message"),0)
                    }
                }

                override fun onError(anError: ANError?) {
                    errorDialog("Gagal","Gagal Terhubung Dengan Server, Silakan Coba Lagi Nanti",1)
                }

            })
    }

    private fun getPrice() {
        try {
            recyclerViewPrice.setHasFixedSize(true)
            recyclerViewPrice.layoutManager = LinearLayoutManager(
                this, RecyclerView.VERTICAL, false
            )
//
            priceErrorPlaceHolder.visibility = View.GONE
            anim_loading.visibility = View.VISIBLE
            data.clear()
            AndroidNetworking.get(api.GET_PRICE)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        anim_loading.visibility = View.GONE
                        priceErrorPlaceHolder.text = response.toString()
                        val raz = response.getJSONArray("data")
                        if (raz.length() < 1) {
                            priceErrorPlaceHolder.visibility = View.VISIBLE
                            priceErrorPlaceHolder.text = "Anda Belum Memiliki Order Aktif"
                        }
                        if (response.getBoolean("success_status")) {
                            for (i in 0 until raz.length()) {
                                val id = raz.getJSONObject(i).getString("id")
                                val price = raz.getJSONObject(i).getString("price")
                                val weight_grade = raz.getJSONObject(i).getString("weight_grade")
                                val price_grade = raz.getJSONObject(i).getString("price_grade")
                                val created_at = raz.getJSONObject(i).getString("created_at")
                                val updated_at = raz.getJSONObject(i).getString("updated_at")
                                val deleted_at = raz.getJSONObject(i).getString("deleted_at")

                                data.add(
                                    modelPrice(
                                        id,
                                        price,
                                        price_grade,
                                        weight_grade,
                                        created_at,
                                        updated_at,
                                        deleted_at
                                    )
                                )
                            }
                            adapterPrice = adapterPrice(data, this@AdminManageHarga)
                            recyclerViewPrice?.adapter = adapterPrice
                            recyclerViewPrice?.visibility = View.VISIBLE
                        } else {
                            priceErrorPlaceHolder.text = "Terjadi Error yang tidak diketahui"
                        }
                    }

                    override fun onError(anError: ANError?) {
                        anim_loading.visibility = View.GONE
                        priceErrorPlaceHolder.text =
                            "Gagal Terhubung Dengan Server \n ${anError.toString()}"
                    }
//
                })
        } catch (err: Exception) {
            Log.e("Error", err.toString())
        }
    }

    fun errorDialog(title: String, content: String, code: Int) {
        xError.visibility = View.VISIBLE
        xError.animation =
            AnimationUtils.loadAnimation(this, R.anim.item_animation_falldown)
        xErrorTitle.text = title
        xErrorContent.text = content
        xErrorNeutralButton.setOnClickListener {
            xError.visibility = View.GONE
            xError.animation =
                AnimationUtils.loadAnimation(this, R.anim.item_animation_fallup)
        }
        xErrorPButton.setOnClickListener {
            xError.visibility = View.GONE
            xError.animation =
                AnimationUtils.loadAnimation(this, R.anim.item_animation_fallup)
            when (code) {
                0 -> {
                    super.onBackPressed()
                    finish()
                }
                1 -> {
                   addPrice()
                }
                else->{
                    //DO NOTHING
                }
            }
        }
    }

    fun successDialog(title: String, content: String, code: Int) {
        xSuccess.visibility = View.VISIBLE
        xSuccess.animation =
            loadAnimation(this, R.anim.item_animation_falldown)
        xSuccessTitle.text = title
        xSuccessContent.text = content
        xSuccessPButton.setOnClickListener {
            xSuccess.visibility = View.GONE
            xSuccess.animation =
                loadAnimation(this, R.anim.item_animation_fallup)
            when (code) {
                0 -> {
                    //Do Nothing
                }
                1 -> {
                    super.onBackPressed()
                    finish()
                }
            }

        }
    }

}