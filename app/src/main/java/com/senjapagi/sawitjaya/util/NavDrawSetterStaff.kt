package com.senjapagi.sawitjaya.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.preference.const
import com.senjapagi.sawitz.preference.Preference
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.custom_navdraw_staff.view.*
import java.io.FileInputStream
import java.io.FileOutputStream


class NavDrawSetterStaff(val context : Context?, val view:View) : AppCompatActivity() {
    var e : Bitmap? = null

    fun setNavDraw(){
        try {
        view.ndTvEmail?.text = context?.let { Preference(it).getPrefString(const.EMAIL) }
        view.ndTvLevel.text = context?.let { Preference(it).getPrefString(const.LEVEL) }
        view.ndTvName.text = context?.let { Preference(it).getPrefString(const.NAME) }

        val a = Preference(context!!).getPrefString("photo").toString()
            val savedImage = loadImageBitmap(context,"prof_pic","PNG")
            view.navDrawProfile.setImageBitmap(savedImage)
        }catch (err :Exception){
            Log.e("Error",err.toString())
        }

        downloadPicasso(view.navDrawProfile)
    }

    fun saveImage(
        context: Context,
        bitmap: Bitmap,
        name: String,
        extension: String
    ) {
        var name = name
        name = "$name.$extension"
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = context.openFileOutput(name, Context.MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream)
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun loadImageBitmap(context: Context,name:String,extension: String) : Bitmap? {
        var nameNew = "$name.$extension"
        var fileInputStream : FileInputStream
        var bitmap : Bitmap? = null

        try {
            fileInputStream= context.openFileInput(nameNew)
            bitmap=BitmapFactory.decodeStream(fileInputStream)
            fileInputStream.close()
        }
        catch (err : java.lang.Exception){
            Log.e("Error Bitmap Decode",err.toString())
        }
        return bitmap
    }


    private fun downloadPicasso(target: ImageView){
        Picasso.get()
            .load(api.PROFILE_PIC_URL+Preference(context!!).getPrefString(const.PROFILE_URL))
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .fit()
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .into(target , object :Callback{
                override fun onSuccess() {
                    e = view.navDrawProfile.drawable.toBitmap()
                    saveImage(context, e!!,"prof_pic","PNG")

                }

                override fun onError(e: Exception?) {
//                    TODO("Not yet implemented")
                }

            })

    }
}