package com.example.cjproductions.comprarProductos.activity

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.cjproductions.R
import kotlinx.android.synthetic.main.activity_productos.*

class ProductosActivity : AppCompatActivity() {

    private var enlace: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        intent.getStringExtra("enlace")?.let { enlace = it }
        intent.getStringExtra("nombre")?.let { title = it }

        //Swipe Refresh
        swipeRefresh.setOnRefreshListener {
            webView.reload()
        }

        webView.webChromeClient = object : WebChromeClient(){
        }

        webView.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                swipeRefresh.isRefreshing = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                swipeRefresh.isRefreshing = false
            }
        }

        val settings = webView.settings
        settings.javaScriptEnabled = true

        webView.loadUrl(enlace)
    }

    override fun onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack()
        }else{
            super.onBackPressed()
        }
    }
}