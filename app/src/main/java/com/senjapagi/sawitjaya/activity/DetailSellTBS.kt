package com.senjapagi.sawitjaya.activity


import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.preference.const
import com.senjapagi.sawitjaya.util.api
import com.senjapagi.sawitz.preference.Preference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_detail_sell_tbs.*
import kotlinx.android.synthetic.main.layout_input_error.*
import kotlinx.android.synthetic.main.layout_loading_transparent.*
import kotlinx.android.synthetic.main.layout_loading_upload.*
import kotlinx.android.synthetic.main.layout_question_dialog.*
import kotlinx.android.synthetic.main.xd_success_request_sell.*
import org.json.JSONObject
import java.io.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DetailSellTBS : AppCompatActivity() {

    var margin = 0.0
    var price = 0.0

    var imageFile1: File? = null
    var imageFile2: File? = null
    var imageFile3: File? = null
    var imageFile4: File? = null


    var address = ""
    var lat = ""
    var long = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_sell_tbs)

        margin = Preference(applicationContext).getPrefString(const.MARGIN)?.toDouble() ?: 0.0
        price = Preference(applicationContext).getPrefString(const.PRICE)?.toDouble() ?: 0.0

        address = intent.getStringExtra("address")
        lat = intent.getStringExtra("lat")
        long = intent.getStringExtra("long")

        shipper_field.setText(address)
        etSellMainTelp.setText(Preference(this).getPrefString(const.PHONE))

        btnBackFromDetail.setOnClickListener {
            super.onBackPressed()
        }

        btnRequestSell.setOnClickListener {
            uploadChecker()
        }

        btnInvoice.setOnClickListener {
            startActivity(Intent(this, UserTransaction::class.java))
            finish()
        }

        img1.setOnClickListener {
            try {
                CropImage.activity()
                    .setActivityTitle("img1")
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this@DetailSellTBS)
            } catch (e: Exception) {
                makeToast(e.toString())
            }

        }


        addSellPict.setOnClickListener {
            checkAndRequestPermission()
//            startActivityForResult(
//                Intent.createChooser(
//                    Intent().setAction(Intent.ACTION_GET_CONTENT)
//                        .setType("image/*"), "Pilih Gambar Tandan Buah Segar yang akan dijual"
//                ), 1 // 1 HERE IS FOR CODE_IMG_GALLERY
//            )

        }

        etSellEstimasiBerat?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    tvEstimasi.visibility = View.GONE
                } else {
                    tvEstimasi.visibility = View.VISIBLE

                    val hasil = (etSellEstimasiBerat.text.toString().toDouble() * price)
                    val number: Double = hasil
                    val number3digits: Double = String.format("%.3f", number).toDouble()
                    val number2digits: Double = String.format("%.2f", number3digits).toDouble()
                    val solution: Double = String.format("%.1f", number2digits).toDouble()
                    val formatter: NumberFormat = DecimalFormat("#,###")
                    val formattedNumber: String = formatter.format(hasil)
                    tvEstimasi.text =
                        "Estimasi Harga Jual = Rp $formattedNumber,- \n * : Belum dikurangi margin"
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }

    private fun resizeIF(targetFile: File): File {
        return Compressor.getDefault(this).compressToFile(targetFile)
    }

    fun getResizedBitmap(
        image: Bitmap?, bitmapWidth: Int,
        bitmapHeight: Int
    ): Bitmap? {
        return Bitmap.createScaledBitmap(
            image!!, bitmapWidth, bitmapHeight,
            true
        )
    }


    fun getRealPath(activity: Activity, contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor =
            activity.managedQuery(contentUri, proj, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        img1.scaleType = ImageView.ScaleType.CENTER_INSIDE
        img2.scaleType = ImageView.ScaleType.CENTER_INSIDE
        img3.scaleType = ImageView.ScaleType.CENTER_INSIDE
        img4.scaleType = ImageView.ScaleType.CENTER_INSIDE
        var clipData = data!!.clipData
//        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
//            val imageUri = data!!.data
//            clipData = data!!.clipData
//            makeToast("")
//            if (imageUri == null) {
//                //setTargetImage with imageUri
//                makeToast("Uri Is Null")
//            }
//        } else

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                img1.setImageURI(resultUri)
                imageFile1 = File(resultUri.path)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }

        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            var tempImage: ArrayList<ClipData.Item>
            tempImage = arrayListOf()
            if (intent != null) {
                clipData = data.clipData
                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        val item = clipData.getItemAt(i)
                        tempImage.add(item)
                    }
                    when (tempImage.size) {
                        1 -> {
                            loadImage(clipData.getItemAt(0).uri, img1)
                            img2.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24)
                            img3.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24)
                            img4.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24)
                            imageFile1 = resizeIF(File(getImagePath(clipData.getItemAt(0).uri)))
                        }
                        2 -> {
                            loadImage(clipData.getItemAt(0).uri, img1)
                            loadImage(clipData.getItemAt(1).uri, img2)
                            img3.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24)
                            img4.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24)
                            imageFile1 = resizeIF(File(getImagePath(clipData.getItemAt(0).uri)))
                            imageFile2 = resizeIF(File(getImagePath(clipData.getItemAt(1).uri)))
                        }
                        3 -> {
                            loadImage(clipData.getItemAt(0).uri, img1)
                            loadImage(clipData.getItemAt(1).uri, img2)
                            loadImage(clipData.getItemAt(2).uri, img3)
                            img4.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24)
                            imageFile1 = resizeIF(File(getImagePath(clipData.getItemAt(0).uri)))
                            imageFile2 = resizeIF(File(getImagePath(clipData.getItemAt(1).uri)))
                            imageFile3 = resizeIF(File(getImagePath(clipData.getItemAt(2).uri)))
                        }
                        4 -> {
                            loadImage(clipData.getItemAt(0).uri, img1)
                            loadImage(clipData.getItemAt(1).uri, img2)
                            loadImage(clipData.getItemAt(2).uri, img3)
                            loadImage(clipData.getItemAt(3).uri, img4)
                            imageFile1 = resizeIF(File(getImagePath(clipData.getItemAt(0).uri)))
                            imageFile2 = resizeIF(File(getImagePath(clipData.getItemAt(1).uri)))
                            imageFile3 = resizeIF(File(getImagePath(clipData.getItemAt(2).uri)))
                            imageFile4 = resizeIF(File(getImagePath(clipData.getItemAt(2).uri)))
                        }
                    }

                } else {
                    makeToast("Clip Data is Null")
                }
            }
        }


//        if (requestCode == 2 && resultCode == Activity.RESULT_OK) { //-1  STAND FOR RESULT OK
//            clipData = data.clipData
//            if (clipData != null) {
//                img1.scaleType = ImageView.ScaleType.CENTER_INSIDE
//                img2.scaleType = ImageView.ScaleType.CENTER_INSIDE
//                img3.scaleType = ImageView.ScaleType.CENTER_INSIDE
//                img4.scaleType = ImageView.ScaleType.CENTER_INSIDE
//
//                //set target image with clipdata[arrayIndex].getUri
//                //..example img1.setImageUri(clipData.getItemAt[1].getUri()
//
//                imageFile1 = null
//                imageFile2 = null
//                imageFile2 = null
//                imageFile4 = null
//
//                try {
//                    when (clipData.itemCount) {
//                        1 -> {
//                            loadImage(clipData.getItemAt(0).uri, img1, 1)
//                            img2.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24)
//                            img3.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24)
//                            img4.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24)
//
//                            imageFile1 =
//                                resizeImageFile(File(getRealPathFromURI(clipData.getItemAt(0).uri)))
//
////                            val e = toBitmap(img1)
////                            imageFile1=bitmapToFile(toBitmap(img1)).toFile()
//                            makeToast(imageFile1.toString())
//                        }
//                        2 -> {
//                            loadImage(clipData.getItemAt(0).uri, img1, 2)
//                            loadImage(clipData.getItemAt(1).uri, img2, 2)
//                            img3.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24)
//                            img4.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24)
//                            imageFile1 =
//                                resizeImageFile(File(getRealPathFromURI(clipData.getItemAt(0).uri)))
//                            imageFile2 =
//                                resizeImageFile(File(getRealPathFromURI(clipData.getItemAt(1).uri)))
//                        }
//                        3 -> {
//                            loadImage(clipData.getItemAt(0).uri, img1, 3)
//                            loadImage(clipData.getItemAt(1).uri, img2, 3)
//                            loadImage(clipData.getItemAt(2).uri, img3, 3)
//                            img4.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24)
//                            imageFile1 =
//                                resizeImageFile(File(getRealPathFromURI(clipData.getItemAt(0).uri)))
//                            imageFile2 =
//                                resizeImageFile(File(getRealPathFromURI(clipData.getItemAt(1).uri)))
//                            imageFile3 =
//                                resizeImageFile(File(getRealPathFromURI(clipData.getItemAt(2).uri)))
//                        }
//                        4 -> {
//                            loadImage(clipData.getItemAt(0).uri, img1, 4)
//                            loadImage(clipData.getItemAt(1).uri, img2, 4)
//                            loadImage(clipData.getItemAt(2).uri, img3, 4)
//                            loadImage(clipData.getItemAt(3).uri, img4, 4)
//                            imageFile1 =
//                                resizeImageFile(File(getRealPathFromURI(clipData.getItemAt(0).uri)))
//                            imageFile2 =
//                                resizeImageFile(File(getRealPathFromURI(clipData.getItemAt(1).uri)))
//                            imageFile3 =
//                                resizeImageFile(File(getRealPathFromURI(clipData.getItemAt(2).uri)))
//                            imageFile4 =
//                                resizeImageFile(File(getRealPathFromURI(clipData.getItemAt(3).uri)))
//
//                        }
//                        else -> {
//
//                        }
//                    }
//                } catch (e: Exception) {
//                    makeToast(e.toString())
//                    shipper_field.setText("Catch 259 "+e.toString())
//                    Log.e("URI ERROR", e.toString())
//                }
//
//
//                for (i in 0 until clipData.itemCount) {
//                    val item = clipData.getItemAt(i)
//                    val uri = item.uri
//                    Log.e("Picked Image URI", uri.toString())
//                }
//            } else {
//                makeToast(data.toString())
//            }
    }


    private fun toBitmap(target: ImageView): Bitmap {
        target.invalidate()
        val drawable: BitmapDrawable = target.drawable as BitmapDrawable
        val bitmap: Bitmap = drawable.bitmap
        bitmapToFile(bitmap)
        return bitmap
    }

    private fun getPathFromURI(
        context: Context,
        contentUri: Uri?
    ): String? {
        var cursor: Cursor? = null
        return try {
            val proj =
                arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(
                contentUri!!, proj, null,
                null, null
            )
            val column_index = cursor
                ?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor!!.moveToFirst()
            cursor.getString(column_index!!)
        } finally {
            cursor?.close()
        }
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

    private fun resizeImage(
        realImage: Bitmap, maxImageSize: Float,
        filter: Boolean
    ): Bitmap? {
        val ratio = Math.min(
            maxImageSize / realImage.width,
            maxImageSize / realImage.height
        )
        val width = Math.round(ratio * realImage.width)
        val height = Math.round(ratio * realImage.height)
        return Bitmap.createScaledBitmap(
            realImage, width,
            height, filter
        )
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

    private fun loadImage(uri: Uri, target: ImageView) {
        var bitmap: Bitmap? = null
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            // To handle depreciation on get Bitmap
            //dont change code structure for ease of understanding, cheers :D -Henry Augusta
            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
        } else {
            // Use older version
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }

        target.setImageBitmap(bitmap?.let { resizeImage(it, 500f, true) })
        target.scaleType = ImageView.ScaleType.CENTER_CROP
    }


    /*Parameter for sendSellRequest
        est_weight
        cord_lat
        cord_lon
        addr
        alt_contact
      */

    private fun uploadChecker() {
        var allOk = true
        if (etSellEstimasiBerat.text.toString().isBlank()) {
            etSellEstimasiBerat.error = "Mohon Isi Kolom ini terlebih dahulu"
            allOk = false
        }
        if (etSellAltTelp.text.toString().isBlank()) {
            etSellAltTelp.error = "Mohon Isi Kolom ini Terlebih dahulu"
            allOk = false
        }
        if (shipper_field.text.toString().isBlank()) {
            shipper_field.error = "Mohon Isi Kolom Ini Terlebih Dahulu"
            allOk = false
        }
        if (shipper_field.text.toString() == "0") {
            xDialog.visibility = View.VISIBLE
            xDialog.animation = loadAnimation(this, R.anim.item_animation_falldown)
            xDialogTitle.text = "Verifikasi Foto"
            xDialogContent.text =
                "Kami memerlukan minimal 2 gambar Tandan Buah Segar untuk memverifikasi penjualan, silakan tambah gambar TBS anda"
            xDialogButton.text = "OK"
            xDialogButton.setOnClickListener {
                xDialog.visibility = View.GONE
                xDialog.animation = loadAnimation(this, R.anim.item_animation_fallup)
            }
            allOk = false
        }
        if (imageFile1 == null || imageFile2 == null) {
            xDialog.visibility = View.VISIBLE
            xDialog.animation = loadAnimation(this, R.anim.item_animation_falldown)
            xDialogTitle.text = "Verifikasi Foto"
            xDialogContent.text =
                "Kami memerlukan minimal 2 gambar Tandan Buah Segar untuk memverifikasi penjualan, silakan tambah gambar TBS anda"
            xDialogButton.text = "OK"
            xDialogButton.setOnClickListener {
                xDialog.visibility = View.GONE
                xDialog.animation = loadAnimation(this, R.anim.item_animation_fallup)
            }
            allOk = false
        }
        if (allOk) {
            xQuestion.visibility = View.VISIBLE
            xQuestion.animation = loadAnimation(this, R.anim.item_animation_falldown)
            xQuestionTitle.text = ""
            xQuestionContent.text = "Anda Yakin Akan Mengirim Permintaan Jemput Sawit ??"
            xQuestionPButton.setOnClickListener {
                sendSellRequest()
                xQuestion.visibility = View.GONE
                xQuestion.animation = loadAnimation(this, R.anim.item_animation_fallup)
            }
            xQuestionNButton.setOnClickListener {
                xQuestion.visibility = View.GONE
                xQuestion.animation = loadAnimation(this, R.anim.item_animation_fallup)
            }
        }
    }

    private fun sendSellRequest() {
        val file_size =
            java.lang.String.valueOf(imageFile1!!.length() / 1024).toDouble()

        anim_upload.visibility = View.VISIBLE
        makeToast("Ukuran Foto = $file_size")
        AndroidNetworking.upload(api.UPLOAD)
            .addMultipartFile("photo[]", imageFile1)
            .addMultipartFile("photo[]", imageFile2)
            .addMultipartFile("photo[]", imageFile3)
            .addMultipartFile("photo[]", imageFile4)
            .addHeaders("token", Preference(this).getPrefString(const.TOKEN))
            .addMultipartParameter("est_weight", etSellEstimasiBerat.text.toString())
            .addMultipartParameter("cord_lat", lat)
            .addMultipartParameter("cord_lon", long)
            .addMultipartParameter("addr", address)
            .addMultipartParameter("alt_contact", etSellAltTelp.text.toString())
            .setPriority(Priority.HIGH)
            .build()
            .setUploadProgressListener { bytesUploaded, totalBytes ->
                // do anything with progress

                loadingProgressIndicator.text =
                    "${bytesUploaded / 1024} KB of ${totalBytes / 1024} KB"

//                loadingProgressIndicator.text =
//                    "${bytesUploaded / 1024 / 1024} MB of ${totalBytes / 1024 / 1024} MB"

//                var progress: Long = ((bytesUploaded / totalBytes) * 100.0).toLong()
//                loadingProgressIndicator.text = "$progress of 100%"
            }
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String) {
                    anim_upload.visibility = View.GONE
                    makeToast(response.toString())
                    lyt_success_req_sell.visibility = View.VISIBLE

//                    if (response.getBoolean("success_status")) {
//                        //..TODO : DO SOMETHING IF REQUeST IS SUCCESSFUL
//                        lyt_success_req_sell.visibility=View.VISIBLE
//                    } else {
//
//                    }
                }

                override fun onError(anError: ANError?) {
                    makeToast(anError?.errorBody.toString())
                    anim_upload.visibility = View.GONE
                    shipper_field.setText(anError?.errorBody.toString())
                    Log.e("Error FAN Upload", anError.toString())
                    Log.e("Error FAN Upload", anError?.errorBody.toString())
                }

            })

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

                        Preference(applicationContext).save(const.PRICE, price.toString())
                        Preference(applicationContext).save(const.MARGIN, margin.toString())

                        price = Preference(applicationContext)
                            .getPrefString(const.PRICE)
                            ?.toDouble()!!
                        margin = Preference(applicationContext)
                            .getPrefString(const.MARGIN)
                            ?.toDouble()!!
                    } else {
                        makeToast("Gagal Mengambil Data Harga Terbaru")
                    }
                }

                override fun onError(anError: ANError?) {
                    anim_loading.visibility = View.GONE
                }

            })
    }

    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun getDateTime(s: String): String? {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val netDate = Date(s.toLong() * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    private fun checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                Toast.makeText(this, "Accept All Permission Request", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            }
        } else openGallery()
    }


    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT


        startActivityForResult(
            Intent.createChooser(
                intent,
                "Pilih Gambar Tandan Buah Segar yang akan dijual"
            ), 2
        )
    }
}