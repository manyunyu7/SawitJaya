package com.senjapagi.sawitjaya.util

import android.content.Context
import android.graphics.Color
import android.view.View
import cn.pedant.SweetAlert.SweetAlertDialog

class DialogBuilder(val mContext: Context,val view : View) {

    lateinit var pDialog : SweetAlertDialog

    fun loading(title : String,content : String,confirmText : String){
        pDialog = SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = title
        pDialog.contentText = content
        pDialog.setCancelable(true)
        pDialog.confirmText = confirmText
        pDialog.setConfirmClickListener { pDialog.dismissWithAnimation() }
        pDialog.show()
    }

    fun success(title : String,content : String,confirmText : String){
        pDialog = SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
        pDialog.titleText = title
        pDialog.contentText = content
        pDialog.setCancelable(true)
        pDialog.confirmText = confirmText
        pDialog.setConfirmClickListener { pDialog.dismissWithAnimation() }
        pDialog.show()
    }

    fun errorConnection(title : String,content : String,confirmText : String){
        pDialog = SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
        pDialog.titleText = title
        pDialog.contentText = content
        pDialog.setCancelable(true)
        pDialog.confirmText = confirmText
        pDialog.setConfirmClickListener { pDialog.dismissWithAnimation() }
        pDialog.show()
    }

    fun neutralWarning(title : String,content : String,confirmText : String){
        pDialog = SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
        pDialog.titleText = title
        pDialog.contentText = content
        pDialog.setCancelable(true)
        pDialog.confirmText = confirmText
        pDialog.setConfirmClickListener { pDialog.dismissWithAnimation() }
        pDialog.show()
    }
    fun progressType(title : String,content : String,contentText : String){
        pDialog = SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#00B0FF")
        pDialog.titleText = title
        pDialog.contentText = contentText
        pDialog.setCancelable(false)
        pDialog.show()
    }
    fun destroyAll(){
        pDialog.dismiss()
    }


}