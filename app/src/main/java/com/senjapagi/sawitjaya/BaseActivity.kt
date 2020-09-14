package com.senjapagi.sawitjaya

import android.content.Context
import android.content.res.Configuration
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    fun adjustFontScale(configuration: Configuration) {
        if (configuration.fontScale > 1.0) {
            configuration.fontScale = 1.0f
            val metrics = resources.displayMetrics
            val wm =
                getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getMetrics(metrics)
            metrics.scaledDensity = configuration.fontScale * metrics.density
            baseContext.resources.updateConfiguration(configuration, metrics)
        }
    }
}