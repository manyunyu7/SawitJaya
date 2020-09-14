package com.senjapagi.sawitjaya.activity.share

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.AnimationUtils.loadAnimation
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.senjapagi.sawitjaya.BaseActivity
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.activity.Webview
import com.senjapagi.sawitjaya.activity.admin.AdminHome
import com.senjapagi.sawitjaya.activity.staff.StaffHome
import com.senjapagi.sawitjaya.activity.user.HomeContainer
import com.senjapagi.sawitjaya.preference.const
import com.senjapagi.sawitjaya.util.DialogBuilder
import com.senjapagi.sawitjaya.util.api
import com.senjapagi.sawitz.preference.Preference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_user_home.*
import kotlinx.android.synthetic.main.layout_forgot_password.*
import kotlinx.android.synthetic.main.layout_loading_transparent.*
import kotlinx.android.synthetic.main.layout_login.*
import kotlinx.android.synthetic.main.layout_login.btnCloseLogin
import kotlinx.android.synthetic.main.layout_login.containerUserName
import kotlinx.android.synthetic.main.layout_login.etUsername
import kotlinx.android.synthetic.main.layout_register.*
import org.json.JSONObject
import kotlin.system.exitProcess


class MainActivity : BaseActivity() {

    lateinit var buildDialog: DialogBuilder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adjustFontScale(resources.configuration)
        setContentView(R.layout.activity_main)
        lyt_login.visibility = View.GONE
        lyt_register.visibility = View.GONE


        buildDialog = DialogBuilder(this, window.decorView)

        btnRegister.setOnClickListener {
            verifyRegister()
        }

        btnForgotPassword.setOnClickListener {
            lyt_forgot_password.visibility=View.VISIBLE
            lyt_forgot_password.animation= loadAnimation(this,R.anim.item_animation_appear_bottom)
        }

        btnCloseForgotPassword.setOnClickListener {
            lyt_forgot_password.visibility=View.GONE
            lyt_forgot_password.animation= loadAnimation(this,R.anim.item_animation_gone_bottom)
        }

        btnSendForgotPassword.setOnClickListener {
            if(etEmailForgotPassword.text.toString().isBlank()){
                etEmailForgotPassword.error="Isi Bidang Ini"
            }else{
                passwordReset()
            }
        }

        btnLoginAdmin.setOnClickListener {
            startActivity(Intent(this,Webview::class.java))
        }

        checkPreferences()

        btnLogin.setOnClickListener {
            btnLogin.visibility = View.GONE
            progress_loading.visibility = View.VISIBLE
            verifyLogin()
        }

        btnCloseReg.setOnClickListener {
            lyt_register.apply {
                animation = AnimationUtils.loadAnimation(
                    context,
                    R.anim.item_animation_gone_bottom
                )
                visibility = View.GONE
            }
        }

        btnCloseLogin.setOnClickListener {
            lyt_login.apply {
                animation = AnimationUtils.loadAnimation(
                    context,
                    R.anim.item_animation_gone_bottom
                )
                visibility = View.GONE
            }
        }



        btnStartLogin.setOnClickListener {
            lyt_login.apply {
                visibility = View.VISIBLE
                animation = AnimationUtils.loadAnimation(
                    context,
                    R.anim.item_animation_appear_bottom
                )
            }
        }

        btnStartRegister.setOnClickListener {
            lyt_register.apply {
                visibility = View.VISIBLE
                animation = AnimationUtils.loadAnimation(
                    context,
                    R.anim.item_animation_appear_bottom
                )
            }
        }
    }


    private fun verifyRegister() {

        var isTrue = false

        if (etRegFullName.text.isNullOrEmpty()) {
            etRegFullName.error = "Mohon Isi Bidang Ini"
            isTrue = false
        }
        if (etRegMainEmail.text.isNullOrEmpty()) {
            etRegMainEmail.error = "Mohon Isi Bidang Ini"
            isTrue = false
        }
        if (etRegVerifPassword.text.toString().length < 6) {
            etRegVerifPassword.error = "Minimum 6 Karakter"
            isTrue = false
        }
        if (etRegMainPassword.text.toString().length < 6) {
            etRegMainPassword.error = "Minimum 6 Karakter"
            isTrue = false
        }
        if (etRegMainPassword.text.toString() != etRegVerifPassword.text.toString()) {
            makeToast("Kolom Password Belum Sesuai")
            isTrue = false
        }
        if (etRegMainPassword.text.isNullOrEmpty()) {
            etRegMainPassword.error = "Mohon Isi Bidang Ini"
            isTrue = false
        }
        if (etRegMainTelp.text.isNullOrEmpty()) {
            etRegMainTelp.error = "Mohon Isi Bidang Ini"
            isTrue = false
        } else isTrue = true
        if (isTrue) {
            register()
        }
    }

    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun clear() {
        etRegFullName.setText("")
        etRegMainPassword.setText("")
        etRegAddress.setText("")
        etRegVerifPassword.setText("")
        etRegMainTelp.setText("")
        etRegNIK.setText("")
        etRegMainEmail.setText("")
    }

    private fun verifyLogin() {
        when {
            etUsername.text.isNullOrEmpty() -> {
                containerUserName.error = ""
            }
            etPassword.text.isNullOrEmpty() -> {
                containerPassword.error = ""
            }
            else -> {
                login()
            }
        }
    }

    private fun checkPreferences() {
        if (Preference(this).getPrefString("user_id").isNullOrBlank()) {
            //do nothing
        } else {
            when (Preference(this).getPrefString(const.LEVEL).toString()) {
                "user" -> {
                    startActivity(Intent(this@MainActivity, HomeContainer::class.java))
                }
                "staff" -> {
                    startActivity(Intent(this@MainActivity, StaffHome::class.java))
                }
                "admin" -> {
                    startActivity(Intent(this@MainActivity, AdminHome::class.java))
                }
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
        exitProcess(-1)
    }

    private fun passwordReset() {
        anim_loading.visibility=View.VISIBLE
        AndroidNetworking.post(api.FORGOT_PASSWORD)
            .addBodyParameter("email", etEmailForgotPassword.text.toString())
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.i("Email",etEmailForgotPassword.text.toString())
                    anim_loading.visibility=View.GONE
                    btnLogin.visibility = View.VISIBLE
                    progress_loading.visibility = View.GONE
                    if (response.getBoolean("success_status")) {
                        makeToast("Berhasil Mengirim Email Reset Password")
                        lyt_forgot_password.visibility=View.GONE
                        lyt_forgot_password.animation= loadAnimation(this@MainActivity,R.anim.item_animation_gone_bottom)
                    } else {
                        makeToast(response.toString())
                    }
                }

                override fun onError(anError: ANError?) {
                    anim_loading.visibility=View.GONE
                    btnLogin.visibility = View.VISIBLE
                    progress_loading.visibility = View.GONE
                    makeToast("Gagal Terhubung Dengan Server")
                    lyt_forgot_password.visibility=View.GONE
                    lyt_forgot_password.animation= loadAnimation(this@MainActivity,R.anim.item_animation_gone_bottom)
                    buildDialog.errorConnection(
                        "Gagal Reset",
                        "Email Gagal Terkirim, Silakan Coba lagi nanti",
                        "OK"
                    )
                }

            })
    }

    private fun login() {
        AndroidNetworking.post(api.LOGIN)
            .addBodyParameter("email", etUsername.text.toString())
            .addBodyParameter("password", etPassword.text.toString())
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    btnLogin.visibility = View.VISIBLE
                    progress_loading.visibility = View.GONE
                    if (response.getBoolean("success_status")) {
                        makeToast(response.getString("message"))
                        val raz = response.getJSONObject("data")
                        Preference(applicationContext).save(const.USER_ID, raz.getString("id"))
                        Preference(applicationContext).save(const.TOKEN, raz.getString("token"))
                        Preference(applicationContext).save(const.LEVEL, raz.getString("level"))
                        Preference(applicationContext).save(const.NAME, raz.getString("name"))
                        Preference(applicationContext).save(const.EMAIL, raz.getString("email"))
                        Preference(applicationContext).save(const.ADDRESS, raz.getString("address"))
                        Preference(applicationContext).save(const.PHONE, raz.getString("phone"))
                        Preference(applicationContext).save(
                            const.PROFILE_URL,
                            raz.getString("photo_profile")
                        )
                        Preference(applicationContext).save(
                            const.ID_CARD_URL,
                            raz.getString("photo_identity")
                        )
                        Preference(applicationContext).save(
                            const.USER_NIK,
                            raz.getString("identity_number")
                        )
                        Preference(applicationContext).save(
                            const.USER_OLD_PASS,
                            etPassword.text.toString()
                        )

                        when (raz.getString("level")) {
                            "user" -> {
                                startActivity(Intent(this@MainActivity, HomeContainer::class.java))
                            }
                            "staff" -> {
                                startActivity(Intent(this@MainActivity, StaffHome::class.java))
                            }
                            "admin" -> {
                                startActivity(Intent(this@MainActivity, AdminHome::class.java))
                            }
                            else -> {
                                Preference(this@MainActivity).clearPreferences()
                            }
                        }

                    } else {
                        makeToast("Akun tidak ditemukan")
                    }
                }

                override fun onError(anError: ANError?) {
                    btnLogin.visibility = View.VISIBLE
                    progress_loading.visibility = View.GONE
                    makeToast("Gagal Terhubung Dengan Server")
                    buildDialog.errorConnection(
                        "Login Gagal",
                        "Gagal Terhubung Dengan Server,Coba lagi nanti",
                        "OK"
                    )
                }

            })
    }

    private fun register() {
        anim_loading.visibility = View.VISIBLE
//        register
//        api/register
//        name, phone, address, identity_number, email, password, repassword
        AndroidNetworking.post(api.REGISTER)
            .addBodyParameter("name", etRegFullName.text.toString())
            .addBodyParameter("phone", etRegMainTelp.text.toString())
            .addBodyParameter("address", etRegAddress.text.toString())
            .addBodyParameter("identity_number", etRegNIK.text.toString())
            .addBodyParameter("email", etRegMainEmail.text.toString())
            .addBodyParameter("password", etRegMainPassword.text.toString())
            .addBodyParameter("repassword", etRegMainPassword.text.toString())
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    anim_loading.visibility = View.GONE
                    if (response.getBoolean("success_status")) {
                        buildDialog.success(
                            "Pendaftaran Berhasil",
                            "Silakan Login Untuk Menggunakan Aplikasi",
                            "OK"
                        )
                        clear()
                        lyt_register.visibility = View.GONE
                        lyt_register.animation =
                            loadAnimation(
                                applicationContext,
                                R.anim.item_animation_gone_bottom
                            )
                    } else {
                        makeToast("Gagal Membuat Akun, Coba Lagi Nanti")
                        buildDialog.neutralWarning(
                            "Gagal Membuat akun",
                            "Email anda sudah terdaftar di akun lain",
                            "OK"
                        )
                    }
                }

                override fun onError(anError: ANError?) {
                    anim_loading.visibility = View.GONE
                    buildDialog.errorConnection(
                        "Gagal Membuat Akun",
                        "Gagal Terhubung Dengan Server, Silakan Coba Lagi Nanti",
                        "OK"
                    )
                    makeToast("Gagal Terhubung Dengan Server")
                }

            })
    }


}