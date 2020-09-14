package com.senjapagi.sawitjaya.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.senjapagi.sawitjaya.R
import com.senjapagi.sawitjaya.activity.admin.AdminHome
import com.senjapagi.sawitjaya.activity.share.MainActivity
import com.senjapagi.sawitjaya.util.api
import kotlinx.android.synthetic.main.activity_webview.*
import kotlinx.android.synthetic.main.layout_loading_transparent.*


open class Webview : AppCompatActivity() {
    var webUrl: String = api.WEBSITE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        findViewById<View>(R.id.webview).visibility = View.INVISIBLE
        val webSettings =webview.settings
        webview.settings.domStorageEnabled = true
        webview.settings.javaScriptEnabled = true
        webview.settings.allowFileAccess = true
        webview.settings.allowContentAccess = true
        webview.clearCache(true)
        webview.settings.allowUniversalAccessFromFileURLs = true
        webview.webChromeClient = object : WebChromeClient() {
        }
        webview.loadUrl(webUrl)
        webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                anim_loading.visibility = View.GONE
                findViewById<View>(R.id.webview).visibility = View.VISIBLE
            }

            override fun onPageStarted(
                view: WebView,
                url: String,
                favicon: Bitmap?
            ) {
                super.onPageStarted(view, url, favicon)
                anim_loading.visibility = View.VISIBLE
            }

        }
        findViewById<View>(R.id.btn_refresh).setOnClickListener {   webview.loadUrl(webUrl) }
        findViewById<View>(R.id.btn_home).setOnClickListener {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val webview = findViewById<View>(R.id.webview) as WebView
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (webview.canGoBack()) {
                        webview.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webview.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webview.restoreState(savedInstanceState)
    }

    companion object {
        var mWebView: WebView? = null
        const val REQUEST_SELECT_FILE = 100
        private const val FILECHOOSER_RESULTCODE = 1
    }
}