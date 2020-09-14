package com.senjapagi.sawitjaya.activity.staff

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.gcacace.signaturepad.views.SignaturePad
import com.senjapagi.sawitjaya.BaseActivity
import com.senjapagi.sawitjaya.R
import kotlinx.android.synthetic.main.activity_staff_signature_facture.*


class StaffSignatureFacture : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.adjustFontScale(resources.configuration)
        setContentView(R.layout.activity_staff_signature_facture)
        var counter = 2

        btnClearPad.setOnClickListener {
            mSignaturePad.clear()
        }

        btnNext.setOnClickListener {
            counter++
            btnNext.text = counter.toString()
            when(counter){
                3->{
                    titleSignature.text = "Tanda Tangan Petugas Pelanggan"
                    userSignature.setImageBitmap(mSignaturePad.signatureBitmap)
                }
                4->{
                    titleSignature.text = "Tanda Tangan Petugas Timbang"
                    staffSignature.setImageBitmap(mSignaturePad.signatureBitmap)
                }
                5->{
                    titleSignature.text = "Tanda Tangan Driver"
                    driverSignature.setImageBitmap(mSignaturePad.signatureBitmap)
                }
            }

        }

        mSignaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
                //Event triggered when the pad is touched
            }

            override fun onSigned() {
                //Event triggered when the pad is signed
            }

            override fun onClear() {
                //Event triggered when the pad is cleared
            }
        })
    }
}