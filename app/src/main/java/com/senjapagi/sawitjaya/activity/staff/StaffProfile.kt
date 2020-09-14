package com.senjapagi.sawitjaya.activity.staff

import android.content.Context
import android.content.ContextWrapper
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.senjapagi.sawitjaya.BaseActivity
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.preference.const
import com.senjapagi.sawitjaya.util.NavDrawSetter
import com.senjapagi.sawitjaya.util.api
import com.senjapagi.sawitz.preference.Preference
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_staff_profile.*
import kotlinx.android.synthetic.main.activity_staff_profile.btnChangeProfile
import kotlinx.android.synthetic.main.activity_staff_profile.btnSaveUpdateProfile
import kotlinx.android.synthetic.main.activity_staff_profile.etProfileEmail
import kotlinx.android.synthetic.main.activity_staff_profile.etProfileMainAddress
import kotlinx.android.synthetic.main.activity_staff_profile.etProfileNIK
import kotlinx.android.synthetic.main.activity_staff_profile.etProfileName
import kotlinx.android.synthetic.main.activity_staff_profile.etProfileNewPassword
import kotlinx.android.synthetic.main.activity_staff_profile.etProfileOldPassword
import kotlinx.android.synthetic.main.activity_staff_profile.etProfileTelp
import kotlinx.android.synthetic.main.activity_staff_profile.ivProfilePict
import kotlinx.android.synthetic.main.activity_staff_profile.profileErrorMessage
import kotlinx.android.synthetic.main.activity_staff_profile.srlProfile
import kotlinx.android.synthetic.main.activity_staff_profile.tvProfileEmail
import kotlinx.android.synthetic.main.activity_staff_profile.tvProfileName
import kotlinx.android.synthetic.main.layout_loading_transparent.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class StaffProfile : BaseActivity() {
    var imageFileProfile: File? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_profile)
        super.adjustFontScale(resources.configuration)
        getUserData()
        btnToggleNavdraw.setOnClickListener {

        }
        srlProfile.setOnRefreshListener {
            srlProfile.isRefreshing=false
            getUserData()
        }

        btnChangeProfile.setOnClickListener {
            CropImage.activity()
                .start(this);
        }

        btnSaveUpdateProfile.setOnClickListener {
            verifyingData()
        }
    }

    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun updateLayout() {
        val pref = Preference(this)
        tvProfileName.text = pref.getPrefString(const.NAME)
        tvProfileEmail.text = pref.getPrefString(const.EMAIL)
        etProfileName.setText(pref.getPrefString(const.NAME))
        etProfileTelp.setText(pref.getPrefString(const.PHONE))
        etProfileNIK.setText(pref.getPrefString(const.USER_NIK))
        etProfileEmail.setText(pref.getPrefString(const.EMAIL))
        etProfileMainAddress.setText(pref.getPrefString(const.ADDRESS))
        val imageUrl =
            api.PROFILE_PIC_URL + pref.getPrefString(const.PROFILE_URL)
        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.ic_profile)
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .error(R.drawable.ic_profile)
            .into(ivProfilePict)
        NavDrawSetter(this, window?.decorView!!).setNavDraw()
    }

    fun getImagePath(uri: Uri?): String? {
        var cursor: Cursor? = contentResolver?.query(uri!!, null, null, null, null)
        cursor?.moveToFirst()
        var document_id: String = cursor?.getString(0)!!
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
        cursor.close()
        cursor = contentResolver?.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null, MediaStore.Images.Media._ID + " = ? ", arrayOf(document_id), null
        )
        cursor?.moveToFirst()
        val path: String =
            cursor?.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))!!
        cursor?.close()
        return path
    }

    private fun bitmapToFile(bitmap: Bitmap): Uri {
        // Get the context wrapper
        val wrapper = ContextWrapper(this)
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

    private fun updateData() {
        anim_loading.visibility = View.VISIBLE
        AndroidNetworking.upload(api.USER_EDIT_PROFILE)
            .addHeaders("token", Preference(this).getPrefString(const.TOKEN))
            .addMultipartParameter("name", etProfileName.text.toString())
            .addMultipartParameter("phone", etProfileTelp.text.toString())
            .addMultipartParameter("address", etProfileMainAddress.text.toString())
            .addMultipartParameter("identity_number", etProfileNIK.text.toString())
            .addMultipartParameter("email", etProfileEmail.text.toString())
            .addMultipartParameter("oldpassword", etProfileOldPassword.text.toString())
            .addMultipartParameter("password", etProfileNewPassword.text.toString())
            .addMultipartParameter("repassword", etProfileNewPassword.text.toString())
            .addMultipartFile("photo_profile", imageFileProfile)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject) {
                    if (response.getBoolean("success_status")) {
                        val raz = response.getJSONObject("data")
                        Preference(this@StaffProfile).save(const.NAME, raz.getString("name"))
                        Preference(this@StaffProfile).save(const.EMAIL, raz.getString("email"))
                        Preference(this@StaffProfile).save(const.ADDRESS, raz.getString("address"))
                        Preference(this@StaffProfile).save(const.PHONE, raz.getString("phone"))
                        Preference(this@StaffProfile).save(
                            const.PROFILE_URL,
                            raz.getString("photo_profile")
                        )
                        Preference(this@StaffProfile).save(
                            const.ID_CARD_URL,
                            raz.getString("photo_identity")
                        )
                        Preference(this@StaffProfile).save(
                            const.USER_NIK,
                            raz.getString("identity_number")
                        )
                        updateLayout()
                    }
                    anim_loading.visibility = View.GONE
                    makeToast(response.toString())
                    Log.d("upload response", response.toString())
                }

                override fun onError(anError: ANError?) {
                    anim_loading.visibility = View.GONE
                    makeToast(anError.toString())
                    Log.e("upload eror", anError.toString())
                    Log.e("upload eror", anError?.errorBody)
                    Log.e("upload eror", anError?.errorDetail)
                }

            })
    }

    fun updateDataNoPass() {
        anim_loading.visibility = View.VISIBLE
        AndroidNetworking.upload(api.USER_EDIT_PROFILE)
            .addHeaders("token", Preference(this@StaffProfile).getPrefString(const.TOKEN))
            .addMultipartParameter("name", etProfileName.text.toString())
            .addMultipartParameter("phone", etProfileTelp.text.toString())
            .addMultipartParameter("address", etProfileMainAddress.text.toString())
            .addMultipartParameter("identity_number", etProfileNIK.text.toString())
            .addMultipartParameter("email", etProfileEmail.text.toString())
            .addMultipartParameter("password", etProfileNewPassword.text.toString())
            .addMultipartFile("photo_profile", imageFileProfile)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject) {
                    anim_loading.visibility = View.GONE
                    if (response.getBoolean("success_status")) {
                        val raz = response.getJSONObject("data")
                        Preference(this@StaffProfile).save(const.NAME, raz.getString("name"))
                        Preference(this@StaffProfile).save(const.EMAIL, raz.getString("email"))
                        Preference(this@StaffProfile).save(const.ADDRESS, raz.getString("address"))
                        Preference(this@StaffProfile).save(const.PHONE, raz.getString("phone"))
                        Preference(this@StaffProfile).save(
                            const.PROFILE_URL,
                            raz.getString("photo_profile")
                        )
                        Preference(this@StaffProfile).save(
                            const.ID_CARD_URL,
                            raz.getString("photo_identity")
                        )
                        Preference(this@StaffProfile).save(
                            const.USER_NIK,
                            raz.getString("identity_number")
                        )
                        getUserData()
                    }
                }

                override fun onError(anError: ANError?) {
                    anim_loading.visibility = View.GONE
                    makeToast(anError.toString())
                    Log.e("upload eror", anError.toString())
                    Log.e("upload eror", anError?.errorBody)
                    Log.e("upload eror", anError?.errorDetail)
                }

            })
    }

    private fun getUserData() {
        profileErrorMessage.visibility = View.GONE
        anim_loading.visibility = View.VISIBLE
        AndroidNetworking.get(api.STAFF_DATA)
            .addHeaders("token", Preference(this).getPrefString(const.TOKEN))
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    etProfileMainAddress.setText(response.toString())
                    anim_loading.visibility = View.GONE
                    if (response.getBoolean("success_status")) {
                        val raz = response.getJSONObject("data")
                        Preference(this@StaffProfile).save(const.NAME, raz.getString("name"))
                        Preference(this@StaffProfile).save(const.EMAIL, raz.getString("email"))
                        Preference(this@StaffProfile).save(const.ADDRESS, raz.getString("address"))
                        Preference(this@StaffProfile).save(const.PHONE, raz.getString("phone"))
                        Preference(this@StaffProfile).save(
                            const.PROFILE_URL,
                            raz.getString("photo_profile")
                        )
                        Preference(this@StaffProfile).save(
                            const.ID_CARD_URL,
                            raz.getString("photo_identity")
                        )
                        Preference(this@StaffProfile).save(
                            const.USER_NIK,
                            raz.getString("identity_number")
                        )
                        updateLayout()
                    }
                }

                override fun onError(anError: ANError?) {
                    profileErrorMessage.visibility = View.VISIBLE
                    profileErrorMessage.text =
                        "Gagal Terhubung dengan Server : " + anError.toString()
                    anim_loading.visibility = View.GONE
                    Log.e("Error get Data User mas", anError.toString())
                    makeToast("Gagal Terhubung Dengan Server")
                    etProfileMainAddress.setText(anError.toString())
                }

            })
    }

    fun verifyingData() {
        var isTrue = true
        var changePass = false
        var oldPass = ""
        var newPass = ""
        try {
            if (imageFileProfile == null) {
                val drawable: Drawable = ivProfilePict.drawable
                val bitmap = (drawable as BitmapDrawable).bitmap
                imageFileProfile = File(getImagePath(bitmapToFile(bitmap)))
                makeToast("tok2")
            }
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }

        if (etProfileName.text.toString().isBlank()) {
            etProfileName.error = "Mohon isi kolom ini terlebih dahulu"
            isTrue = false
        }
        if (etProfileNIK.text.toString().isBlank()) {
            etProfileNIK.error = "Mohon isi kolom ini terlebih dahulu"
            isTrue = false
        }
        if (etProfileEmail.text.toString().isBlank()) {
            etProfileEmail.error = "Mohon isi kolom ini terlebih dahulu"
            isTrue = false
        }
        if (etProfileTelp.text.toString().isBlank()) {
            etProfileTelp.error = "Mohon isi kolom ini terlebih dahulu"
            isTrue = false
        }
        if (etProfileMainAddress.text.toString().isBlank()) {
            etProfileName.error = "Mohon isi kolom ini terlebih dahulu"
            isTrue = false
        }
        if (etProfileName.text.toString().isBlank()) {
            etProfileName.error = "Mohon isi kolom ini terlebih dahulu"
            isTrue = false
        }
        if (!etProfileOldPassword.text.toString().isBlank() || !etProfileNewPassword.text.toString()
                .isBlank()
        ) {
            changePass = true
            if (etProfileNewPassword.text.toString().length < 6) {
                etProfileNewPassword.error = "Minimum 6 Karakter"
                isTrue = false
            }
            if (etProfileOldPassword.text.toString().length < 6) {
                etProfileOldPassword.error = "Minimum 6 Karakter"
                isTrue = false
            }
            newPass = etProfileNewPassword.text.toString()
            oldPass = etProfileOldPassword.text.toString()
        }

        if (isTrue) {
            if (changePass) {
                updateData()
            } else {
                updateDataNoPass()
            }
        }

    }


}