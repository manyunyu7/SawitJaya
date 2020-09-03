package com.senjapagi.sawitjaya.activity.staff

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.view.get
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.modelAndAdapter.CustomListAdapter
import com.senjapagi.sawitjaya.preference.const
import com.senjapagi.sawitjaya.util.api
import com.senjapagi.sawitz.preference.Preference
import kotlinx.android.synthetic.main.activity_staff_input_invoice.*
import kotlinx.android.synthetic.main.activity_staff_input_invoice.etInputWeight
import kotlinx.android.synthetic.main.activity_staff_signature_facture.*
import kotlinx.android.synthetic.main.layout_edit_weight.*
import kotlinx.android.synthetic.main.layout_loading_transparent.*
import kotlinx.android.synthetic.main.layout_order_invoice.*
import kotlinx.android.synthetic.main.layout_question_dialog.*
import kotlinx.android.synthetic.main.layout_signature.*
import kotlinx.android.synthetic.main.list_weight.view.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class StaffInputInvoice : AppCompatActivity() {
    var listBerat = mutableListOf<Double>()
    var price = 0.0
    var margin = 0.0
    var mapUploadWeight = mutableMapOf<String,MutableMap<String,String>>()
    var weightData = mutableMapOf<String,String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_input_invoice)
        updatePrice()



        btnNextInputInvoice.setOnClickListener {
            val intent = Intent(this,StaffSignatureFacture::class.java)
            startActivity(intent)
        }

        btnNextInputInvoice.setOnClickListener {
            lyt_signature.visibility=View.VISIBLE
            lyt_signature.animation=loadAnimation(this,R.anim.item_animation_appear_bottom)
        }

        btnCloseSignature.setOnClickListener {
            lyt_signature.visibility=View.GONE
            lyt_signature.animation=loadAnimation(this,R.anim.item_animation_gone_bottom)
        }

        btnUploadInvoice.setOnClickListener {
            uploadInvoice()
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


    private fun uploadInvoice(){
        try{
            Log.i("Panjang Array Berat :",listBerat.size.toString())
            var weightRawStringdata : String = ""
            if(listBerat.size>=1){
                weightRawStringdata=listBerat.joinToString()
            }
            Log.i("upload weight[]",weightRawStringdata)


//          getRealPathFromURI from file
            mSignaturePadUser.signatureBitmap
            val signStaffFile = File(getRealPathFromURI(bitmapToFile(mSignaturePadUser.signatureBitmap)))
            val signUserFile =  File(getRealPathFromURI(bitmapToFile(mSignaturePadUser.signatureBitmap)))
            val signDriverFile =File(getRealPathFromURI(bitmapToFile(mSignaturePadUser.signatureBitmap)))

            AndroidNetworking.upload(api.STAFF_UPLOAD_INVOICE+intent.getStringExtra("order_id")+"/invoice")
//        sign_invoice, sign_driver, sign_user
                .addMultipartFile("sign_user",signUserFile)
                .addMultipartFile("sign_driver",signDriverFile)
                .addMultipartFile("sign_invoice",signStaffFile)
                .addHeaders("token",Preference(this).getPrefString(const.TOKEN))
                .addMultipartParameter("weight_comma",weightRawStringdata)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener{
                    override fun onResponse(response: JSONObject?) {
                        makeToast(response.toString())
                        Log.i("Respon Upload Invoice",response.toString())
                    }

                    override fun onError(anError: ANError?) {
                        Log.e("Error Upload Invoice",anError.toString())
                        Log.i("Error Upload Invoice",anError.toString())
                    }

                })
        }catch (e : Exception){
            Log.e("Exception",e.toString())
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

    fun getImagePath(uri: Uri?): String? {
        var cursor: Cursor? = contentResolver.query(uri!!, null, null, null, null)
        cursor?.moveToFirst()
        var document_id: String = cursor?.getString(0)!!
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
        cursor.close()
        cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null, MediaStore.Images.Media._ID + " = ? ", arrayOf(document_id), null
        )
        cursor?.moveToFirst()
        val path: String =
            cursor?.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))!!
        cursor?.close()
        return path
    }
    private fun updateTotalPrice() {
        makeToast(margin.toString())
        var currentWeight = totalWeight.text.toString().toDouble()
        if(currentWeight.toString().isNotBlank()){
            totalFinalWeight.text =
                (currentWeight - (currentWeight * margin)).toString()
            val hasil = totalFinalWeight.text.toString().toDouble() * price
//            val number: Double = hasil
//            val number3digits: Double = String.format("%.3f", number).toDouble()
//            val number2digits: Double = String.format("%.2f", number3digits).toDouble()
//            val solution: Double = String.format("%.1f", number2digits).toDouble()
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
                        infoPayment.text="Grading : $margin  , Harga Sawit per Kg : $price"
                    } else {
                        makeToast("Gagal Mengambil Data Harga Terbaru")
                    }
                }

                override fun onError(anError: ANError?) {
                    anim_loading.visibility = View.GONE
                }

            })
    }
}