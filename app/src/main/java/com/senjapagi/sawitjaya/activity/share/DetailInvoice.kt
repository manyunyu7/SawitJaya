package com.senjapagi.sawitjaya.activity.share

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.github.dhaval2404.imagepicker.ImagePicker
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.modelAndAdapter.CustomListAdapter
import com.senjapagi.sawitjaya.preference.const
import com.senjapagi.sawitjaya.util.api
import com.senjapagi.sawitz.preference.Preference
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_invoice.*
import kotlinx.android.synthetic.main.layout_edit_weight.*
import kotlinx.android.synthetic.main.layout_error_dialog.*
import kotlinx.android.synthetic.main.layout_list_weight.*
import kotlinx.android.synthetic.main.layout_loading_transparent.*
import kotlinx.android.synthetic.main.layout_order_invoice.*
import kotlinx.android.synthetic.main.layout_question_dialog.*
import kotlinx.android.synthetic.main.layout_signature.*
import kotlinx.android.synthetic.main.layout_signature_pic.*
import kotlinx.android.synthetic.main.layout_success_dialog.*
import kotlinx.android.synthetic.main.list_weight.view.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class DetailInvoice : AppCompatActivity() {
    var listBerat = mutableListOf<Double>()
    var price = 0.0
    var margin = 0.0
    var mapUploadWeight = mutableMapOf<String, MutableMap<String, String>>()
    var weightData = mutableMapOf<String, String>()

    lateinit var imgUpload1 : File
    lateinit var imgUpload2 : File
    lateinit var imgUpload3 : File

    var source = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_invoice)

        source = intent.getStringExtra("source")
        btnOpenDetailWeight.visibility=View.GONE
        lyt_detail_weight.visibility=View.GONE

        btnNextInputInvoice.setOnClickListener {
            lyt_signature.visibility = View.VISIBLE
            lyt_signature.animation = loadAnimation(this, R.anim.item_animation_appear_bottom)
        }

        btnBackFromDetailInvoice.setOnClickListener {
            super.onBackPressed()
        }
        imgInvoice.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly()
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        if (source == "see") {
            imgInvoice.setOnClickListener {
                //Nothing
            }
            labelFotoJemput.text="Foto Penjemputan Tandan Buah Segar"
            labelHasilTimbangan.visibility=View.GONE
            listViewWeight.visibility=View.GONE
            btnOpenDetailWeight.visibility=View.VISIBLE
            btnOpenDetailWeight.setOnClickListener {
                lyt_detail_weight.visibility=View.VISIBLE
                lyt_detail_weight.animation= loadAnimation(this,R.anim.item_animation_appear_bottom)
            }
            btnCloseDetailWeight.setOnClickListener {
                lyt_detail_weight.visibility=View.GONE
                lyt_detail_weight.animation= loadAnimation(this,R.anim.item_animation_gone_bottom)
            }
            scrollViewInvoice.requestFocus(View.FOCUS_UP);
            scrollViewInvoice.scrollTo(0, 0);
            try {
                getInvoice()
            } catch (e: Exception) {
                Log.e("ERROR GET INVOICE", e.toString())
            }
            titleInvoice.text = "Tanda Terima ( Invoice )"
            btnNextInputInvoice.text = "Lihat Tanda Tangan Terima Invoice"
            linearLayout2.visibility = View.GONE
            etInputWeight.visibility = View.GONE
            btnInputWeight.visibility = View.GONE
            btnResetWeight.visibility = View.GONE
            btnNextInputInvoice.setOnClickListener {
                lyt_pic_signature.visibility = View.VISIBLE
                lyt_pic_signature.animation =
                    loadAnimation(this, R.anim.item_animation_appear_bottom)
            }
            btnClosePicSignature.setOnClickListener {
                lyt_pic_signature.visibility = View.GONE
                lyt_pic_signature.animation = loadAnimation(this, R.anim.item_animation_gone_bottom)
            }
        }else{
            updatePrice()
        }




        btnCloseSignature.setOnClickListener {
            lyt_signature.visibility = View.GONE
            lyt_signature.animation = loadAnimation(this, R.anim.item_animation_gone_bottom)
        }

        resetSignatureDriver.setOnClickListener {
            mSignaturePadDriver.clear()
        }
        resetSignatureStaff.setOnClickListener {
            mSignaturePadStaff.clear()
        }
        resetSignatureUser.setOnClickListener {
            mSignaturePadUser.clear()
        }


        btnUploadInvoice.setOnClickListener {
            xQuestion.visibility = View.VISIBLE
            xQuestion.animation = loadAnimation(this, R.anim.item_animation_falldown)
            xQuestionTitle.text = "Konfirmasi Kirim Invoice"
            xQuestionContent.text = "Anda Yakin akan Data Invoice Sudah Sesuai ???"
            xQuestionNButton.text = "Periksa Ulang"
            xQuestionNButton.setOnClickListener {
                xQuestion.visibility = View.GONE
                xQuestion.animation = loadAnimation(this, R.anim.item_animation_fallup)
            }
            xQuestionPButton.text = "Upload Invoice"
            xQuestionPButton.setOnClickListener {
                xQuestion.visibility = View.GONE
                xQuestion.animation = loadAnimation(this, R.anim.item_animation_fallup)
                uploadInvoice()
            }
        }


//        e.putExtra("order_id",orderDetID.toString())
//        e.putExtra("order_date",orderDetDueDate.toString())
//        e.putExtra("nama",orderDetAccName.toString())
//        e.putExtra("alt_con",orderDetAltContact.toString())
//        e.putExtra("main_con",orderDetAccPhone.toString())
//        e.putExtra("address",orderDetAddress.toString())
//        e.putExtra("est_weight",orderDetEstimate.toString())

        orderID.text = intent.getStringExtra("order_id")
        orderDueDate.text = intent.getStringExtra("order_date")
        orderEstimate.text = intent.getStringExtra("est_weight")
        orderAddress.text = intent.getStringExtra("address")
        orderInvDetAccName.text = intent.getStringExtra("name")
        btnInputWeight.setOnClickListener {
            if (!etInputWeight.text.toString().isBlank()) {
                val weight = etInputWeight.text.toString().toDouble()
                listBerat.add(weight)
                makeToast("Berhasil Menambah Hasil Timbangan")
                etInputWeight.setText("")
                updateWeightInfo()
                updateTotalPrice()
            }
        }

        btnResetWeight.setOnClickListener {
            listBerat.clear()
            makeToast("Berhasil Menghapus Semua Hasil Timbangan")
            updateWeightInfo()
            updateTotalPrice()
        }

        listViewWeight.adapter = CustomListAdapter(this, listBerat)
//        val e = listViewWeight.rootView
//        e.findViewById<ImageButton>(R.id.btnDeleteWeight)
//        e.setOnClickListener {
//
//        }


        listViewWeight.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                xEditWeight.visibility = View.VISIBLE
                xEditWeight.animation =
                    AnimationUtils.loadAnimation(this, R.anim.item_animation_falldown)
                xEditWeightTitle.text = view.tv_weight_order.text.toString()
                xEditInputWeight.setText(listBerat[position].toString())
                xEditWeightPButton.setOnClickListener {
                    if (xEditInputWeight.text.toString().isNotBlank()) {
                        listBerat[position] = xEditInputWeight.text.toString().toDouble()
                        totalWeight.text = listBerat.toList().toDoubleArray().sum().toString()
                        xEditWeight.visibility = View.GONE
                        xEditWeight.animation =
                            AnimationUtils.loadAnimation(this, R.anim.item_animation_fallup)
                        listViewWeight.invalidateViews()
                        updateTotalPrice()
                    } else {
                        etInputWeight.error = "Isi Bidang ini untuk mengupdate"
                    }

                }
                xEditWeightNeutralButton.setOnClickListener {
                    xEditWeight.visibility = View.GONE
                    xEditWeight.animation = loadAnimation(this, R.anim.item_animation_fallup)
                }
                xEditWeightNButton.setOnClickListener {
                    totalWeight.text = listBerat.toList().toDoubleArray().sum().toString()
                    xEditWeight.visibility = View.GONE
                    xEditWeight.animation = loadAnimation(this, R.anim.item_animation_fallup)
                    listBerat.removeAt(position)
                    listViewWeight.invalidateViews()
                    updateTotalPrice()
                }
            }


//        listViewWeight.setOnItemClickListener { parent, view, position, id ->
//            view.setOnClickListener(object : View.OnClickListener {
//                override fun onClick(v: View?) {
//                    //use getItem(position) to get the item
//                    v.
//                }
//            })
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data
                imgInvoice.setImageURI(fileUri)
                //You can get File object from intent
                val file:File = ImagePicker.getFile(data)!!

                //You can also get File Path from intent
                val filePath:String = ImagePicker.getFilePath(data)!!
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }catch (e:Exception){
            makeToast(e.toString())
        }

    }

    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun bitmapToFile(bitmap: Bitmap): Uri {
        // Get the context wrapper
        val wrapper = ContextWrapper(applicationContext)
        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("Error Bitmap ", e.toString())
        }
        // Return the saved bitmap uri
        return Uri.parse(file.absolutePath)
    }

    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor =
            contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }


    private fun uploadInvoice() {
        try {
            anim_loading.visibility = View.VISIBLE
            Log.i("Panjang Array Berat :", listBerat.size.toString())
            var weightRawStringdata: String = ""
            if (listBerat.size >= 1) {
                weightRawStringdata = listBerat.joinToString()
            }
            Log.i("upload weight[]", weightRawStringdata)
//          getRealPathFromURI from file
            mSignaturePadUser.signatureBitmap
            val signStaffFile =
                File(getRealPathFromURI(bitmapToFile(mSignaturePadStaff.signatureBitmap)))
            val signUserFile =
                File(getRealPathFromURI(bitmapToFile(mSignaturePadUser.signatureBitmap)))
            val signDriverFile =
                File(getRealPathFromURI(bitmapToFile(mSignaturePadDriver.signatureBitmap)))
            //        sign_invoice, sign_driver, sign_user
            AndroidNetworking.upload(api.STAFF_UPLOAD_INVOICE + intent.getStringExtra("order_id") + "/invoice")
                .addMultipartFile("sign_user", signUserFile)
                .addMultipartFile("sign_driver", signDriverFile)
                .addMultipartFile("sign_invoice", signStaffFile)
                .addHeaders("token", Preference(this).getPrefString(const.TOKEN))
                .addMultipartParameter("weight_comma", weightRawStringdata)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        anim_loading.visibility = View.GONE
                        makeToast(response.toString())
                        Log.i("Respon Upload Invoice", response.toString())
                        if (response.getBoolean("success_status")) {
                            makeToast("Berhasil Upload Invoice")
                            xSuccess.visibility = View.VISIBLE
                            xSuccess.animation =
                                loadAnimation(this@DetailInvoice, R.anim.item_animation_falldown)
                            xSuccessTitle.text = "Berhasil Upload"
                            xSuccessContent.text =
                                "Invoice dan data penjualan berhasil terinput ke database"
                            xSuccessPButton.setOnClickListener {
                                xSuccess.visibility = View.GONE
                                xSuccess.animation =
                                    loadAnimation(this@DetailInvoice, R.anim.item_animation_fallup)
                                onBackPressed()
                                finish()
                            }
                        } else {
                            makeToast(response.getString("message"))
                            xError.visibility = View.VISIBLE
                            xError.animation =
                                loadAnimation(this@DetailInvoice, R.anim.item_animation_falldown)
                            xErrorTitle.text = "Gagal Mengupload Invoice"
                            xErrorContent.text = response.getString("message")
                            xErrorPButton.text = "Coba Lagi"
                            xErrorPButton.setOnClickListener {
                                xError.visibility = View.GONE
                                xError.animation =
                                    loadAnimation(this@DetailInvoice, R.anim.item_animation_fallup)
                                uploadInvoice()
                            }
                            xErrorNeutralButton.text = "Periksa Data Invoice"
                            xErrorNeutralButton.setOnClickListener {
                                xError.visibility = View.GONE
                                xError.animation =
                                    loadAnimation(this@DetailInvoice, R.anim.item_animation_fallup)
                            }
                        }
                    }

                    override fun onError(anError: ANError?) {
                        anim_loading.visibility = View.GONE
                        xError.visibility = View.VISIBLE
                        xError.animation =
                            loadAnimation(this@DetailInvoice, R.anim.item_animation_falldown)
                        xErrorTitle.text = "Gagal Mengupload Invoice"
                        xErrorContent.text =
                            ("Gagal Terhubung dengan server, periksa koneksi internet anda atau coba lagi nanti")
                        xErrorPButton.text = "Coba Lagi"
                        xErrorPButton.setOnClickListener {
                            xError.visibility = View.GONE
                            xError.animation =
                                loadAnimation(this@DetailInvoice, R.anim.item_animation_fallup)
                            uploadInvoice()
                        }
                        xErrorNeutralButton.text = "Periksa Koneksi"
                        xErrorNeutralButton.setOnClickListener {
                            xError.visibility = View.GONE
                            xError.animation =
                                loadAnimation(this@DetailInvoice, R.anim.item_animation_fallup)
                        }
                    }
                })
        } catch (e: Exception) {
            anim_loading.visibility = View.GONE
            Log.e("Exception", e.toString())
        }
    }

    fun getRealPath(activity: Activity, contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor =
            activity.managedQuery(contentUri, proj, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    private fun updateTotalPrice() {
        makeToast("Margin = $margin%")
        var currentWeight = totalWeight.text.toString().toDouble()
        if (currentWeight.toString().isNotBlank()) {
            totalFinalWeight.text =
                (currentWeight - (currentWeight * margin)).toString()
            val hasil = totalFinalWeight.text.toString().toDouble() * price
            val formatter: NumberFormat = DecimalFormat("#,###")

            val formattedNumber: String = formatter.format(hasil)

            totalPayment.text = "$formattedNumber"
        }

    }

    private fun updateWeightInfo() {
        listViewWeight.adapter = CustomListAdapter(this, listBerat)
        totalWeight.text = listBerat.toList().toDoubleArray().sum().toString()
    }

    public fun removeItem(index: Int) {
        listBerat.removeAt(index)
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
                        val pricex = raz?.getString("price")
                        val marginx = raz?.getString("price_grade")

                        Preference(applicationContext).save(const.PRICE, pricex.toString())
                        Preference(applicationContext).save(const.MARGIN, marginx.toString())

                        price = Preference(applicationContext)
                            .getPrefString(const.PRICE)
                            ?.toDouble()!!
                        margin = Preference(applicationContext)
                            .getPrefString(const.MARGIN)
                            ?.toDouble()!!
                        infoPayment.text = "Grading : $margin  , Harga Sawit per Kg : $price"
                    } else {
                        makeToast("Gagal Mengambil Data Harga Terbaru")
                    }
                }

                override fun onError(anError: ANError?) {
                    anim_loading.visibility = View.GONE
                }

            })
    }

    private fun getInvoice() {
        try {
            var urlGetDetail = ""
            if(intent.getStringExtra("source")=="see"){
                urlGetDetail = api.USER_GET_INVOICE
            }
            if (intent.getStringExtra("url")=="staff"){
                urlGetDetail=api.STAFF_ORDER_DETAIL
            }
            Log.i("Info Intent",urlGetDetail.toString())
            Log.i("Info Intent",intent.getStringExtra("source"))
            anim_loading.visibility = View.VISIBLE
            //    http://103.253.27.21:10000/sawitz/index.php?/api/staff/jual/9/invoice
            AndroidNetworking.get(urlGetDetail + intent.getStringExtra("order_id") + "/invoice")
                .addHeaders("token", Preference(this).getPrefString(const.TOKEN))
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        anim_loading.visibility = View.GONE
                        if (response.getBoolean("success_status")) {
                            val raz = response.getJSONObject("data")
                            val signPicUser = raz.getString("sign_user")
                            val signPicStaff = raz.getString("sign_invoice")
                            val signPicDriver = raz.getString("sign_driver")
                            val pricex = raz.getString("price")
                            val marginx = raz.getString("price_grade")
                            val photo = raz.getJSONArray("photo")

//                            if(photo.length()>0){
//                                loadImageWithPicasso(api.INVOICE_PIC_URL+photo.getJSONObject(0).)
//                            }

                            loadImageWithPicasso(
                                api.SIGNATURE_PIC_URL + signPicDriver,
                                mPicSignaturePadDriver
                            )
                            loadImageWithPicasso(
                                api.SIGNATURE_PIC_URL + signPicStaff,
                                mPicSignaturePadStaff
                            )
                            loadImageWithPicasso(
                                api.SIGNATURE_PIC_URL + signPicUser,
                                mPicSignaturePadUser
                            )

                            Preference(applicationContext).save(const.PRICE, pricex.toString())
                            Preference(applicationContext).save(const.MARGIN, marginx.toString())

                            price = Preference(applicationContext)
                                .getPrefString(const.PRICE)
                                ?.toDouble()!!
                            margin = Preference(applicationContext)
                                .getPrefString(const.MARGIN)
                                ?.toDouble()!!

                            infoPayment.text = "Grading : $margin  , Harga Sawit per Kg : $price"

                            val ky = raz.getJSONArray("weight")
                            for (i in 0 until ky.length()) {
                                val weightInvoice = ky.getJSONObject(i).getString("weight")
                                listBerat.add(weightInvoice.toDouble())
                            }

                            listViewShowWeight.adapter = CustomListAdapter(this@DetailInvoice, listBerat)
                            totalWeight.text = listBerat.toList().toDoubleArray().sum().toString()
                            listViewShowWeight.visibility = View.VISIBLE
                            updateTotalPrice()

                        } else {
                            makeToast("Gagal Mengambil Data Invoice karena " + response.getString("message"))
                        }
                    }

                    override fun onError(anError: ANError) {
                        makeToast("Gagal Mengambil Data Invoice")
                        anim_loading.visibility = View.GONE
                    }

                })
        } catch (e: Exception) {
            anim_loading.visibility = View.GONE
        }

    }

    private fun loadImageWithPicasso(url: String, target: ImageView) {
        Picasso.get()
            .load(url)
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .fit()
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .into(target, object : Callback {
                override fun onSuccess() {

                }

                override fun onError(e: Exception?) {
                    Log.e("Error Picasso" , e.toString())
                }

            })
    }
}