package com.senjapagi.sawitjaya.util

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.preference.const
import com.senjapagi.sawitz.preference.Preference
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.custom_navdraw.*
import kotlinx.android.synthetic.main.custom_navdraw.view.*

class NavDrawSetter(val context : Context? ,val view:View) : AppCompatActivity() {

    fun setNavDraw(){
        view.ndTvEmail?.text = context?.let { Preference(it).getPrefString(const.EMAIL) }
        view.ndTvLevel.text = context?.let { Preference(it).getPrefString(const.LEVEL) }
        view.ndTvName.text = context?.let { Preference(it).getPrefString(const.NAME) }
        downloadPicasso(view.navDrawProfile)
    }

    private fun downloadPicasso(target: ImageView){
        Picasso.get()
            .load(api.PROFILE_PIC_URL+Preference(context!!).getPrefString(const.PROFILE_URL))
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .fit()
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .into(target ?:view.navDrawProfile)
    }

}