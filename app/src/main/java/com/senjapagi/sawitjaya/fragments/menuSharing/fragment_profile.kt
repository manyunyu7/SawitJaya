package com.senjapagi.sawitjaya.fragments.menuSharing

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.senjapagi.sawitjaya.Logout
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.activity.Tutorial
import com.senjapagi.sawitjaya.activity.user.UserOrderNew
import com.senjapagi.sawitjaya.fragments.menuStaff.fragment_staff_home
import com.senjapagi.sawitjaya.fragments.menuUser.fragment_user_home
import com.senjapagi.sawitjaya.preference.const
import com.senjapagi.sawitjaya.util.NavDrawSetter
import com.senjapagi.sawitjaya.util.NavDrawSetterStaff
import com.senjapagi.sawitjaya.util.api
import com.senjapagi.sawitz.preference.Preference
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.custom_navdraw.*
import kotlinx.android.synthetic.main.custom_navdraw_staff.*
import kotlinx.android.synthetic.main.fragment_user_home.btnToggleNavdraw
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.layout_error_dialog.*
import kotlinx.android.synthetic.main.layout_loading_transparent.*
import kotlinx.android.synthetic.main.layout_success_dialog.*
import kotlinx.android.synthetic.main.user_profile.lyt_user_profile
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var imageFileProfile: File? = null
    var updateURL = ""
    var getDataURL = ""
    var sourceCode = 0;
    //source 1 if this fragment called from USER activity
    //source 2 if this fragment called from STAFF activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imageFileProfile = null
        xError.visibility=View.GONE
        val bundle = this.arguments
        if (bundle != null) {

            val source = bundle.getString("source")
            makeToast(source.toString())
            if (source == "staff") {
                sourceCode = 2
                updateURL = api.STAFF_EDIT_PROFILE
                getDataURL = api.STAFF_DATA
            } else {
                sourceCode = 1
                updateURL = api.USER_EDIT_PROFILE
                getDataURL = api.USER_DATA
            }
        } else {
            sourceCode = 1
            updateURL = api.USER_EDIT_PROFILE
            getDataURL = api.USER_DATA
        }
        NavDrawSetter(context, activity?.window?.decorView!!).setNavDraw()
        lyt_navdraw?.visibility = View.GONE
        btnToggleNavdraw.setOnClickListener { NavDrawToggle("open") }


        if (sourceCode == 1) {
            //------------USER Navdraw Operation-------------------//
            NavDrawSetter(context, activity?.window?.decorView!!).setNavDraw()
            btnCloseNavDraw.setOnClickListener { NavDrawToggle("close") }
            lyt_navdraw_shadow.setOnClickListener { NavDrawToggle("close") }
            ndBtnLogOut.setOnClickListener { val logout = Logout(requireContext());logout.logoutDialog() }
            ndBtnProfile.setOnClickListener { changeLayout(fragment_profile()) }
            ndBtnHome.setOnClickListener { changeLayout(fragment_user_home()) }
            ndBtnSell.setOnClickListener { moveActivity(UserOrderNew())}
            ndBtnHistory.setOnClickListener { moveActivity(UserOrderNew()) }
            ndBtnAbout.setOnClickListener {
                val intent = Intent(activity, Tutorial::class.java)
                intent.putExtra("source","user")
                startActivity(intent)
            }
            ndBtnSend.setOnClickListener {  makeToast("Fitur ini akan segera hadir") }
        }
        if (sourceCode == 2) {
            //------------STAFF Navdraw Operation-------------------//
            btnToggleNavdraw.visibility=View.INVISIBLE
            btnToggleNavdraw.isEnabled=false
            lyt_navdraw_staff_shadow.setOnClickListener { NavDrawToggle("close") }
            btnStaffCloseNavDraw.setOnClickListener { NavDrawToggle("close") }
            ndStaffBtnHome.setOnClickListener { changeLayout(fragment_staff_home()) }
            ndStaffBtnLogOut.setOnClickListener {
                val logout = Logout(requireContext());logout.logoutDialog()
            }
            lyt_navdraw_staff_shadow.setOnClickListener { NavDrawToggle("close") }

        }

        getUserData()

        srlProfile.setOnRefreshListener {
            srlProfile.isRefreshing = false
            getUserData()
            updateLayout()
        }



        btnChangeProfile.setOnClickListener {
            CropImage.activity()
                .start(requireContext(), this);
        }

        btnSaveUpdateProfile.setOnClickListener {
            verifyingData()
        }

        super.onViewCreated(view, savedInstanceState)
    }



    private fun moveActivity(dest: Activity) {
        NavDrawToggle("close")
        val intent = Intent(activity, dest::class.java)
        startActivity(intent)
    }

    private fun updateLayout() {
        val pref = Preference(requireContext())

        tvProfileTelp.text = pref.getPrefString(const.PHONE)
        tvProfileName.text = pref.getPrefString(const.NAME)
        tvProfileEmail.text = Preference(requireContext()).getPrefString(const.EMAIL)
        etProfileName.setText(pref.getPrefString(const.NAME))
        etProfileTelp.setText(pref.getPrefString(const.PHONE))
        etProfileNIK.setText(pref.getPrefString(const.USER_NIK))
        etProfileEmail.setText(pref.getPrefString(const.EMAIL))
        etProfileMainAddress.setText(pref.getPrefString(const.ADDRESS))
        val imageUrl =
            api.PROFILE_PIC_URL + Preference(requireContext()).getPrefString(const.PROFILE_URL)
        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.ic_profile)
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .error(R.drawable.ic_profile)
            .into(ivProfilePict)


        NavDrawSetter(context, activity?.window?.decorView!!).setNavDraw()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data); comment this unless you want to pass your result to the activity.
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri: Uri = result.uri
                ivProfilePict.setImageURI(resultUri)
                imageFileProfile = File(resultUri.path)
                makeToast(imageFileProfile.toString())
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    private fun changeLayout(dest: Fragment) {
        NavDrawToggle("close")
        Handler().postDelayed({
            val fragmentManager: FragmentManager? = requireActivity().supportFragmentManager
            fragmentManager?.beginTransaction()?.replace(R.id.userFrameLayout, dest)
                ?.commit()
        }, 500)
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

    fun getImagePath(uri: Uri?): String? {
        var cursor: Cursor? = activity?.contentResolver?.query(uri!!, null, null, null, null)
        cursor?.moveToFirst()
        var document_id: String = cursor?.getString(0)!!
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
        cursor.close()
        cursor = activity?.contentResolver?.query(
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
        val wrapper = ContextWrapper(context)
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

    private fun NavDrawToggle(indicator: String) {
        if (sourceCode == 1) {
            if (indicator == "open") {
                lyt_navdraw.visibility = View.VISIBLE
                lyt_navdraw.animation =
                    AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation)
                lyt_user_profile.background.alpha = 200
            } else {
                lyt_navdraw.animation =
                    AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation_go)
                lyt_navdraw.visibility = View.GONE
                lyt_user_profile.background.alpha = 255
            }
        }
        if (sourceCode == 2) {
            if (indicator == "open") {
                lyt_navdraw_staff.visibility = View.VISIBLE
                lyt_navdraw_staff.animation =
                    AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation)
                lyt_user_profile.background.alpha = 200
            } else {
                lyt_navdraw_staff.animation =
                    AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation_go)
                lyt_navdraw_staff.visibility = View.GONE
                lyt_user_profile.background.alpha = 255
            }
        }

    }

    private fun updateData() {
        anim_loading.visibility = View.VISIBLE
        AndroidNetworking.upload(updateURL)
            .addHeaders("token", Preference(requireContext()).getPrefString(const.TOKEN))
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
                    updateLayout()
                    if (response.getBoolean("success_status")) {
                        successDialog("Berhasil Mengupdate Profile","")
                        val raz = response.getJSONObject("data")
                        Preference(context!!).save(const.NAME, raz.getString("name"))
                        Preference(context!!).save(const.EMAIL, raz.getString("email"))
                        Preference(context!!).save(const.ADDRESS, raz.getString("address"))
                        Preference(context!!).save(const.PHONE, raz.getString("phone"))
                        Preference(context!!).save(
                            const.PROFILE_URL,
                            raz.getString("photo_profile")
                        )
                        Preference(context!!).save(
                            const.ID_CARD_URL,
                            raz.getString("photo_identity")
                        )
                        Preference(context!!).save(const.USER_NIK, raz.getString("identity_number"))


                    }else{
                        errorDialog(
                            "Gagal Mengupdate Data User",
                            "Periksa dan Pastikan Data anda sudah terisi dengan benar"
                        )
                    }
                    anim_loading.visibility = View.GONE
                    makeToast(response.toString())
                    Log.d("upload response", response.toString())
                }

                override fun onError(anError: ANError?) {
                    updateLayout()
                    errorDialog(
                        "Gagal Mengupdate Data User",
                        "Periksa koneksi internet anda dan coba lagi nanti"
                    )
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
        AndroidNetworking.upload(updateURL)
            .addHeaders("token", Preference(requireContext()).getPrefString(const.TOKEN))
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
                        successDialog("Berhasil Mengupdate Profile","")
                        val raz = response.getJSONObject("data")
                        Preference(context!!).save(const.NAME, raz.getString("name"))
                        Preference(context!!).save(const.EMAIL, raz.getString("email"))
                        Preference(context!!).save(const.ADDRESS, raz.getString("address"))
                        Preference(context!!).save(const.PHONE, raz.getString("phone"))
                        Preference(context!!).save(
                            const.PROFILE_URL,
                            raz.getString("photo_profile")
                        )
                        Preference(context!!).save(
                            const.ID_CARD_URL,
                            raz.getString("photo_identity")
                        )
                        Preference(context!!).save(const.USER_NIK, raz.getString("identity_number"))
                        getUserData()
                    }
                }

                override fun onError(anError: ANError?) {
                    errorDialog(
                        "Gagal Mengupdate Data User",
                        "Periksa dan Pastikan Data anda sudah benar"
                    )
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
        AndroidNetworking.get(getDataURL)
            .addHeaders("token", Preference(requireContext()).getPrefString(const.TOKEN))
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    etProfileMainAddress.setText(response.toString())
                    anim_loading.visibility = View.GONE
                    if (response.getBoolean("success_status")) {
                        val raz = response.getJSONObject("data")
                        Preference(context!!).save(const.NAME, raz.getString("name"))
                        Preference(context!!).save(const.EMAIL, raz.getString("email"))
                        Preference(context!!).save(const.ADDRESS, raz.getString("address"))
                        Preference(context!!).save(const.PHONE, raz.getString("phone"))
                        Preference(context!!).save(
                            const.PROFILE_URL,
                            raz.getString("photo_profile")
                        )
                        Preference(context!!).save(
                            const.ID_CARD_URL,
                            raz.getString("photo_identity")
                        )
                        Preference(context!!).save(const.USER_NIK, raz.getString("identity_number"))
                        updateLayout()
                    }else{
                        errorDialog(
                            "Gagal Terhubung dengan Serve",
                            "Periksa Koneksi Internet Anda atau Coba Lagi Nanti"
                        )
                    }
                }

                override fun onError(anError: ANError?) {
                    errorDialog(
                        "Gagal Terhubung dengan Server",
                        "Periksa Koneksi Internet Anda atau Coba Lagi Nanti"
                    )
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


    private fun makeToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun successDialog(title: String, message: String) {
        xSuccess.visibility = View.VISIBLE
        xSuccess.animation = loadAnimation(context, R.anim.item_animation_appear_bottom)
        xSuccessTitle.text = title
        xSuccessContent.text=message
        xSuccessPButton.setOnClickListener {
            xSuccess.visibility = View.GONE
            xSuccess.animation = loadAnimation(context, R.anim.item_animation_gone_bottom)
        }
    }

    private fun errorDialog(title: String, message: String) {
        xError.visibility = View.VISIBLE
        xError.animation = loadAnimation(context, R.anim.item_animation_appear_bottom)
        xErrorTitle.text = title
        xErrorContent.text=message
        xErrorPButton.setOnClickListener {
            xError.visibility = View.GONE
            xError.animation = loadAnimation(context, R.anim.item_animation_gone_bottom)
            getUserData()
        }
        xErrorNeutralButton.setOnClickListener {
            xError.animation= loadAnimation(context,R.anim.item_animation_gone_bottom)
            xError.visibility=View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}