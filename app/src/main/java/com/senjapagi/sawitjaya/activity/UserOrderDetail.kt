package com.senjapagi.sawitjaya.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.preference.const
import com.senjapagi.sawitjaya.util.api
import com.senjapagi.sawitz.preference.Preference
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_detail_order.*
import kotlinx.android.synthetic.main.layout_loading_transparent.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class UserOrderDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_detail_order)
//
//        e.putExtra("id",orderData[position].id)
//        e.putExtra("address",orderData[position].id)
//        e.putExtra("est",orderData[position].id)
//        e.putExtra("created_at",holder.created_at.text.toString())
//        e.putExtra("status",orderData[position].status)
        val status = intent.getStringExtra("status")
        orderDetStatus.text = intent.getStringExtra("status")
        orderDetID.text = intent.getStringExtra("id")
        orderDetAddress.setText(intent.getStringExtra("address"))
        orderDetDueDate.text = intent.getStringExtra("created_at")
        orderDetEstimate.text = intent.getStringExtra("est")
        getDetail()


        //waiting,processed,successed,failed


        when (status) {

            "waiting" -> {
                btnOrdSeeInvoice.setOnClickListener {
                    makeToast("Invoice baru tersedia setelah order sukses dijemput")
                }
                btnOrdDetailChat.visibility = View.GONE
//                stat = "Menunggu Diproses"
//                btnOrdDetailChat.visibility = View.GONE
            }
            "processed" -> {
                btnOrdDetailCancel.visibility=View.GONE
                btnOrdSeeInvoice.setOnClickListener {
                    makeToast("Invoice baru tersedia setelah order sukses dijemput")
                }
//                stat = "Sedang Diproses"
//                btnStaffAcceptOrder.visibility = View.GONE
//                btnStaffInputInvoice.visibility = View.VISIBLE
//                btnOrdDetailChat.visibility = View.VISIBLE
            }
            "successed" -> {
                btnOrdDetailCancel.visibility=View.GONE
                btnOrdDetailChat.visibility = View.GONE
//                stat = "Selesai"
//                btnStaffSeeInvoice.visibility=View.VISIBLE
//                btnStaffAcceptOrder.visibility = View.GONE
//                btnOrdDetailChat.visibility=View.GONE
//                btnStaffUntakeOrder.visibility=View.GONE
            }
            "failed" -> {
                btnOrdDetailCancel.visibility=View.GONE
                btnOrdSeeInvoice.setOnClickListener {
                    makeToast("Invoice baru tersedia setelah order sukses dijemput")
                }
            }
        }
        if (status == "processed" || status == "failed") {
            btnOrdDetailCancel.setOnClickListener {
                makeToast("Order yang sudah diproses tidak dapat dibatalkan")
            }
        }

        btnBackFromDetail.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun getDateTime(s: String): String? {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val netDate = Date(s.toLong() * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    private fun getDetail() {
        try {
            anim_loading.visibility = View.VISIBLE
            AndroidNetworking.get(api.USER_ORDER_DETAIL + intent.getStringExtra("id"))
                .addHeaders("token", Preference(this).getPrefString(const.TOKEN))
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        anim_loading.visibility = View.GONE
                        if (response.getBoolean("success_status")) {
                            try {
                                val raz = response.getJSONObject("data")
                                val ky = raz.getJSONObject("user_detail")
                                val est_weight = raz.getString("est_weight")
                                val addr = raz.getString("addr")
                                val staff_id = raz.getString("staff_id")
                                val created_at = raz.getString("created_at")
                                val status = raz.getString("status")
                                val alt_contact = raz.getString("alt_contact")
                                val orderID = raz.getString("id")
                                val userID = raz.getString("user_id")
                                val namaUser = ky.getString("name") ?: "-"
                                val telpUtamaUser = ky.getString("phone") ?: "-"

                                orderDetAccName.text = namaUser
                                orderDetAccPhone.text = telpUtamaUser
                                orderDetStatus.text = status
                                orderDetID.text = orderID
                                orderDetAddress.setText(addr)
                                orderDetDueDate.text = getDateTime(created_at)
                                orderDetEstimate.text = est_weight
                                orderDetAltContact.text = alt_contact

                                val razPhoto = raz.getJSONArray("photo")
                                val baseLink = api.USER_ORDER_PHOTO
                                var linkPhoto1 = ""
                                var linkPhoto2 = ""
                                var linkPhoto3 = ""
                                var linkPhoto4 = ""
                                when (razPhoto.length()) {
                                    0 -> {

                                    }
                                    1 -> {
                                        linkPhoto1 =
                                            baseLink + razPhoto.getJSONObject(0).getString("photo")
                                    }
                                    2 -> {
                                        linkPhoto1 =
                                            baseLink + razPhoto.getJSONObject(0).getString("photo")
                                        linkPhoto2 =
                                            baseLink + razPhoto.getJSONObject(1).getString("photo")
                                    }
                                    3 -> {
                                        linkPhoto1 =
                                            baseLink + razPhoto.getJSONObject(0).getString("photo")
                                        linkPhoto2 =
                                            baseLink + razPhoto.getJSONObject(1).getString("photo")
                                        linkPhoto3 =
                                            baseLink + razPhoto.getJSONObject(2).getString("photo")
                                    }
                                    4 -> {
                                        linkPhoto1 =
                                            baseLink + razPhoto.getJSONObject(0).getString("photo")
                                        linkPhoto2 =
                                            baseLink + razPhoto.getJSONObject(1).getString("photo")
                                        linkPhoto3 =
                                            baseLink + razPhoto.getJSONObject(2).getString("photo")
                                        linkPhoto4 =
                                            baseLink + razPhoto.getJSONObject(3).getString("photo")
                                    }
                                }

                                downloadPicasso(linkPhoto1, Detimg1)
                                downloadPicasso(linkPhoto2, Detimg2)
                                downloadPicasso(linkPhoto3, Detimg3)
                                downloadPicasso(linkPhoto4, Detimg4)

                                Log.i("URL 1", linkPhoto1)
                                Log.i("URL 2", linkPhoto2)
                                Log.i("URL 3", linkPhoto3)
                                Log.i("URL 4", linkPhoto4)
                            } catch (e: Exception) {
                                Log.e("Error apa tuh", e.toString())
                            }
                        } else {
                            //if success status is not true or in other words "failed"
                            makeToast("Gagal Mengambil Data")
                        }
                    }

                    override fun onError(anError: ANError?) {
                        anim_loading.visibility = View.GONE
                        makeToast("Gagal Terhubung dengan Server , Silakan coba lagi nanti")
                    }

                })
        } catch (e: Exception) {
            Log.e("Error Pak", e.toString())
        }
    }

    private fun downloadPicasso(url: String, target: ImageView) {
        try {
            anim_loading.visibility = View.VISIBLE
            Picasso.get()
                .load(url)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .fit()
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(target, object : Callback {
                    override fun onSuccess() {
                        anim_loading.visibility = View.GONE
                        target.scaleType = ImageView.ScaleType.CENTER_CROP
                    }

                    override fun onError(e: Exception?) {
                        anim_loading.visibility = View.GONE
                        target.visibility = View.GONE
                        Log.e("Picasso Get Image Error", e.toString())
                    }

                })
        } catch (e: Exception) {
            Log.e("Catch Error Picasso", e.toString())
        }

    }
//
//        "success_status": true,
//        "http_code": 200,
//        "message": "Berhasil mendapatkan data penjualan",
//        "data": {
//        "id": "4",
//        "user_id": "7",
//        "est_weight": "96",
//        "addr": "Jl. Perum Zamrud No.1, RT.001/RW.007, Cimuning, Kec. Mustika Jaya, Kota Bks, Jawa Barat 17155, Indonesia-Kecamatan Mustika Jaya-Jawa Barat",
//        "cord_lat": "-6.311210314931492",
//        "cord_lon": "107.02734477818012",
//        "alt_contact": "089663645267",
//        "staff_id": null,
//        "created_at": "1598114056",
//        "updated_at": "1598114056",
//        "deleted_at": null,
//        "status": "waiting",
//        "photo": [
//        {
//            "id": "10",
//            "sellrequest_id": "4",
//            "photo": "10.jpeg"
//        },
//        {
//            "id": "11",
//            "sellrequest_id": "4",
//            "photo": "11.jpeg"
//        },
//        {
//            "id": "12",
//            "sellrequest_id": "4",
//            "photo": "12.jpeg"
//        }
//        ]
//    }
//    }
}