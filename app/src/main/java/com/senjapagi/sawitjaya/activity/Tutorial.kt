package com.senjapagi.sawitjaya.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.senjapagi.sawitjaya.BaseActivity
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.preference.const
import com.senjapagi.sawitz.preference.Preference
import kotlinx.android.synthetic.main.activity_tutorial.*

class Tutorial : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.adjustFontScale(resources.configuration)
        setContentView(R.layout.activity_tutorial)
        var assetName = ""
        var title = ""

        if (Preference(this).getPrefString(const.LEVEL) == "admin"
            || Preference(this).getPrefString(const.LEVEL) == "staff"
        ) {
            assetName = "tutor_driver.pdf"
            title = "Petunjuk Untuk Staff"
        } else {
            assetName = "tutor_user.pdf"
            title = "Petunjuk Untuk Pelanggan"
        }
        try {
            pdfView.fromAsset(assetName)
                .spacing(5)
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .load()


        } catch (e: Exception) {
            makeToast("Gagal Memuat Tutorial karena $e")
            super.onBackPressed()
            finish()
        } finally {
            titleKonten.text = title
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}