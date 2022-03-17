package com.omnicoder.instaace.ui.activities


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.omnicoder.instaace.databinding.ActivityInstagramLoginBinding
import com.omnicoder.instaace.util.Constants

class InstagramLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInstagramLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInstagramLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpWebViewAndButton()
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebViewAndButton() {
        val myWebView: WebView = binding.webView
        myWebView.settings.javaScriptEnabled=true
        myWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(myWebView, url)
                if(url==Constants.INSTAGRAM_SAVE_LOGIN_LINK || url== Constants.INSTAGRAM_HOMEPAGE_LINK){
                    saveCookies()
                    Toast.makeText(this@InstagramLoginActivity, "Logged in successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@InstagramLoginActivity, MainActivity::class.java))
                }
            }
        }
        myWebView.loadUrl("https://www.instagram.com/accounts/login/")
        binding.button.setOnClickListener {
            saveCookies()
        }

    }
    private fun saveCookies() {

        val manager = CookieManager.getInstance()
        val map = mutableMapOf<String, String>()

        manager.getCookie("https://www.instagram.com/")?.let { cookies ->
            Log.d("tagg", "Cookies we got: $cookies")
            val typedArray =
                cookies.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (element in typedArray) {
                val split =
                    element.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (split.size >= 2) {
                    map[split[0]] = split[1]
                } else if (split.size == 1) {
                    map[split[0]] = ""
                }
            }
        }
        var mapInString = map.toString()
        mapInString = mapInString.replace("{", "")
        mapInString = mapInString.replace("}", "")
        mapInString = mapInString.replace(",", ";")
        mapInString.trim()
        val sharedPreferences = getSharedPreferences("Cookies", 0)
        sharedPreferences?.edit()?.putString("loginCookies", mapInString)?.apply()
    }
}
