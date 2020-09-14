package com.senjapagi.sawitjaya.activity.admin

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.activity.share.DetailInvoice
import com.senjapagi.sawitjaya.activity.staff.StaffOrderPage
import com.senjapagi.sawitjaya.preference.const
import com.senjapagi.sawitjaya.util.api
import com.senjapagi.sawitz.preference.Preference
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_staff_order_detail.*
import kotlinx.android.synthetic.main.activity_staff_order_detail.Detimg1
import kotlinx.android.synthetic.main.activity_staff_order_detail.Detimg2
import kotlinx.android.synthetic.main.activity_staff_order_detail.Detimg3
import kotlinx.android.synthetic.main.activity_staff_order_detail.Detimg4
import kotlinx.android.synthetic.main.activity_staff_order_detail.btnBackFromDetail
import kotlinx.android.synthetic.main.activity_staff_order_detail.btnOrdDetailChat
import kotlinx.android.synthetic.main.activity_staff_order_detail.orderDetAccName
import kotlinx.android.synthetic.main.activity_staff_order_detail.orderDetAccPhone
import kotlinx.android.synthetic.main.activity_staff_order_detail.orderDetAddress
import kotlinx.android.synthetic.main.activity_staff_order_detail.orderDetAltContact
import kotlinx.android.synthetic.main.activity_staff_order_detail.orderDetDueDate
import kotlinx.android.synthetic.main.activity_staff_order_detail.orderDetEstimate
import kotlinx.android.synthetic.main.activity_staff_order_detail.orderDetID
import kotlinx.android.synthetic.main.activity_staff_order_detail.orderDetStatus
import kotlinx.android.synthetic.main.layout_loading_transparent.*
import kotlinx.android.synthetic.main.layout_question_dialog.*
import kotlinx.android.synthetic.main.layout_success_dialog.*
import org.json.JSONObject
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class AdminOrderDetail : AppCompatActivity() {
    var stat = ""
    var custPhone = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_order_detail)
//
//        e.putExtra("id",orderData[position].id)
//        e.putExtra("address",orderData[position].id)
//        e.putExtra("est",orderData[position].id)
//        e.putExtra("created_at",holder.created_at.text.toString())
//        e.putExtra("status",orderData[position].status)
        val status = intent.getStringExtra("status")

        orderDetID.text = intent.getStringExtra("id")
        orderDetAddress.setText(intent.getStringExtra("address"))
        orderDetDueDate.text = intent.getStringExtra("created_at")
        orderDetEstimate.text = intent.getStringExtra("est")
        getDetail()


        btnStaffInputInvoice.setOnClickListener {
            val e = Intent(this,
                    DetailInvoice::class.java)
            e.putExtra("source","edit")
            e.putExtra("order_id",orderDetID.text.toString())
            e.putExtra("order_date",orderDetDueDate.text.toString())
            e.putExtra("name",orderDetAccName.text.toString())
            e.putExtra("alt_con",orderDetAltContact.text.toString())
            e.putExtra("main_con",orderDetAccPhone.text.toString())
            e.putExtra("address",orderDetAddress.text.toString())
            e.putExtra("est_weight",orderDetEstimate.text.toString())
            startActivity(e)
        }

        btnStaffSeeInvoice.setOnClickListener {
            val e = Intent(this,
                    DetailInvoice::class.java)
            e.putExtra("source","see")
            e.putExtra("url","admin")
            e.putExtra("order_id",orderDetID.text.toString())
            e.putExtra("order_date",orderDetDueDate.text.toString())
            e.putExtra("name",orderDetAccName.text.toString())
            e.putExtra("alt_con",orderDetAltContact.text.toString())
            e.putExtra("main_con",orderDetAccPhone.text.toString())
            e.putExtra("address",orderDetAddress.text.toString())
            e.putExtra("est_weight",orderDetEstimate.text.toString())
            startActivity(e)
        }

        //waiting,processed,successed,failed

        if (status == "processed" || status == "failed") {
            btnStaffCancelOrder.setOnClickListener {
                makeToast("Order yang sudah diproses tidak dapat dibatalkan")
            }
        }

        onStatusName()

        btnStaffUntakeOrder.setOnClickListener {
            xQuestion.visibility = View.VISIBLE
            xQuestion.animation = AnimationUtils.loadAnimation(this, R.anim.item_animation_falldown)
            xQuestionTitle.text = ""
            xQuestionContent.text = "Anda Yakin Akan Batal Mengambil Order Ini ??"
            xQuestionPButton.setOnClickListener {
                untakeOrder()
                xQuestion.visibility = View.GONE
                xQuestion.animation =
                        AnimationUtils.loadAnimation(this, R.anim.item_animation_fallup)
            }
            xQuestionNButton.setOnClickListener {
                xQuestion.visibility = View.GONE
                xQuestion.animation =
                        AnimationUtils.loadAnimation(this, R.anim.item_animation_fallup)
            }
        }

        btnStaffAcceptOrder.setOnClickListener {
            xQuestion.visibility = View.VISIBLE
            xQuestion.animation = AnimationUtils.loadAnimation(this, R.anim.item_animation_falldown)
            xQuestionTitle.text = ""
            xQuestionContent.text = "Anda Yakin Akan Mengambil Order Ini ??"
            xQuestionPButton.setOnClickListener {
                takeOrder()
                xQuestion.visibility = View.GONE
                xQuestion.animation =
                        AnimationUtils.loadAnimation(this, R.anim.item_animation_fallup)
            }
            xQuestionNButton.setOnClickListener {
                xQuestion.visibility = View.GONE
                xQuestion.animation =
                        AnimationUtils.loadAnimation(this, R.anim.item_animation_fallup)
            }

        }

        btnBackFromDetail.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun onStatusName() {
        btnStaffInputInvoice.visibility = View.GONE
        btnStaffCancelOrder.visibility = View.GONE
        btnStaffSeeInvoice.visibility=View.GONE
        when (intent.getStringExtra("status")) {
            "waiting" -> {
                stat = "Menunggu Diproses"
                btnOrdDetailCall.setOnClickListener{
                    openNumber(custPhone)
                }
                btnOrdDetailChat.setOnClickListener {
                    sendWhatsapp(
                            "#OrderID : ${orderDetID.text}\n" +
                                    "Pesan :"
                            , custPhone
                    )
                }
            }
            "processed" -> {
                stat = "Sedang Diproses"
                btnStaffAcceptOrder.visibility = View.GONE
                btnStaffInputInvoice.visibility = View.VISIBLE
                btnOrdDetailChat.visibility = View.VISIBLE
                btnOrdDetailCall.setOnClickListener{
                    openNumber(custPhone)
                }
                btnOrdDetailChat.setOnClickListener {
                    sendWhatsapp(
                            "#OrderID : ${orderDetID.text}\n" +
                                    "Pesan :"
                            , custPhone
                    )
                }
            }
            "successed" -> {
                stat = "Selesai"
                btnStaffSeeInvoice.visibility=View.VISIBLE
                btnStaffAcceptOrder.visibility = View.GONE
                btnStaffUntakeOrder.visibility=View.GONE
                btnOrdDetailCall.setOnClickListener{
                    openNumber(custPhone)
                }
                btnOrdDetailChat.setOnClickListener {
                    sendWhatsapp(
                            "#OrderID : ${orderDetID.text}\n" +
                                    "Pesan :"
                            , custPhone
                    )
                }
            }
            "failed" -> {
                stat = "Dibatalkan"
                btnStaffAcceptOrder.visibility = View.GONE
                btnOrdDetailChat.visibility=View.GONE
                btnStaffUntakeOrder.visibility=View.GONE
                btnOrdDetailCall.setOnClickListener{
                    openNumber(custPhone)
                }
                btnOrdDetailChat.setOnClickListener {
                    sendWhatsapp(
                            "#OrderID : ${orderDetID.text}\n" +
                                    "Pesan :"
                            , custPhone
                    )
                }

            }
        }
        orderDetStatus.text = stat
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

    private fun takeOrder() {
        val orderID = orderDetID.text.toString()
        AndroidNetworking.post(api.STAFF_TAKE_ORDER + orderID + "/take")
                .addHeaders("token", Preference(this).getPrefString(const.TOKEN))
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        if (response.getBoolean("success_status")) {
                            successDialog("Info", "Berhasil Ambil Order", 1)
                        } else {
                            successDialog("Info", "Gagal Mengambil Order", 0)
                        }
                        Log.i("Response Accept Order", response.toString())
                    }

                    override fun onError(anError: ANError?) {
                        Log.i("Response Accept Order", anError.toString())
                        successDialog(
                                "Gagal Terhubung Dengan Server",
                                "Gagal Mengambil Order , Coba Lagi Nanti",
                                0
                        )
                    }

                })
    }

    private fun untakeOrder() {
        val orderID = orderDetID.text.toString()
        AndroidNetworking.post(api.STAFF_TAKE_ORDER + orderID + "/untake")
                .addHeaders("token", Preference(this).getPrefString(const.TOKEN))
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        if (response.getBoolean("success_status")) {
                            successDialog("Info", "Batal Ambil Order Berhasil", 1)
                        } else {
                            successDialog("Info", "Batal Ambil Order Gagal", 0)
                        }
                        Log.i("Response Accept Order", response.toString())
                    }

                    override fun onError(anError: ANError?) {
                        successDialog(
                                "Gagal Terhubung Dengan Server",
                                "Gagal Mengambil Order , Coba Lagi Nanti",
                                0
                        )
                    }

                })
    }

    fun successDialog(title: String, content: String, code: Int) {
        xSuccess.visibility = View.VISIBLE
        xSuccess.animation =
                AnimationUtils.loadAnimation(this@AdminOrderDetail, R.anim.item_animation_falldown)
        xSuccessTitle.text = title
        xSuccessContent.text = content
        xSuccessPButton.setOnClickListener {
            xSuccess.visibility = View.GONE
            xSuccess.animation =
                    AnimationUtils.loadAnimation(this@AdminOrderDetail, R.anim.item_animation_fallup)
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

    private fun getDetail() {
        try {
            anim_loading.visibility = View.VISIBLE
            AndroidNetworking.get(api.ADMIN_ORDER_DETAIL + intent.getStringExtra("id"))
                    .addHeaders("token", Preference(this).getPrefString(const.TOKEN))
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            anim_loading.visibility = View.GONE
                            if (response.getBoolean("success_status")) {
                                //                            "id": "4",
//                            "user_id": "7",
//                            "est_weight": "96",
//                            "addr": "Jl. Perum Zamrud No.1, RT.001/RW.007, Cimuning, Kec. Mustika Jaya, Kota Bks, Jawa Barat 17155, Indonesia-Kecamatan Mustika Jaya-Jawa Barat",
//                            "cord_lat": "-6.311210314931492",
//                            "cord_lon": "107.02734477818012",
//                            "alt_contact": "089663645267",
//                            "staff_id": null,
//                            "created_at": "1598114056",
//                            "updated_at": "1598114056",
//                            "deleted_at": null,
//                            "status": "waiting",


                                try {
                                    val raz = response.getJSONObject("data")
                                    val ky = raz.getJSONObject("user_detail")

                                    var custData = raz.opt("user_detail")
                                    if (custData.toString() == "null") {
                                        containerStaffAvailable.visibility = View.VISIBLE
                                        Log.i("Info JSON", "Emp Data is Null")
                                        containerStaffAvailable.visibility = View.GONE

                                    } else {
                                        Log.i("Info JSON Not Null",custData.toString())
                                        val cust_dataJSON = raz.getJSONObject("user_detail")
                                        val empName = cust_dataJSON.getString("name")
                                        custPhone = cust_dataJSON.getString("phone")
                                        orderDetCust.text = empName
                                        orderDetCustPhone.text = custPhone
                                        val empID = cust_dataJSON.getString("photo_profile")
                                        downloadPicasso(api.PROFILE_PIC_URL + empID, OrderDetEmpPic)
                                    }

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

                                    val lat = raz.getString("cord_lat") ?: "-"
                                    val long = raz.getString("cord_lon") ?: "-"

                                    btnStaffOpenMap.setOnClickListener {

                                        val intent = Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("geo:<$lat>,<$long>?q=<$lat>,<$long>(Kemem)")
                                        )
                                        intent.setPackage("com.google.android.apps.maps")
                                        startActivity(intent)

                                        intent.resolveActivity(packageManager)?.let {
                                            startActivity(intent)
                                        }
                                    }

                                    orderDetAccName.text = namaUser
                                    orderDetAccPhone.text = telpUtamaUser
                                    orderDetStatus.text = status
                                    orderDetID.text = orderID
                                    orderDetAddress.setText(addr)
                                    orderDetDueDate.text = getDateTime(created_at)
                                    orderDetEstimate.text = est_weight
                                    orderDetAltContact.text = alt_contact
                                    onStatusName()
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
    private fun openNumber(num : String){
        try {
            makeToast("Membuka Telpon")
            var number = num
            number = number.replaceFirst("0","62",true)
            val intent =
                    Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            startActivity(intent)
        }catch (e:Exception){
            makeToast(e.toString())
        }
    }

    private fun sendWhatsapp(message: String, num: String) {
        var number = num
        number = number.replaceFirst("0","62",true)
        makeToast("Membuka Whatsapp")
        val pm = packageManager
        try {
            val packageManager: PackageManager = pm
            val i = Intent(Intent.ACTION_VIEW)
            val url =
                    "https://api.whatsapp.com/send?phone=$number&text=" + URLEncoder.encode(
                            message,
                            "UTF-8"
                    )
            i.setPackage("com.whatsapp")
            i.data = Uri.parse(url)
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
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