package com.omnicoder.instaace.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.omnicoder.instaace.databinding.WebViewFragmentBinding
import com.omnicoder.instaace.viewmodels.WebViewViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebViewFragment : Fragment() {
    private lateinit var binding:  WebViewFragmentBinding
    private lateinit var viewModel: WebViewViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = WebViewFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[WebViewViewModel::class.java]
        setUpWebViewAndButton()
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebViewAndButton() {
        val myWebView: WebView = binding.webview
        myWebView.settings.javaScriptEnabled=true
        myWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(myWebView, url)
                Toast.makeText(context, "Done!", Toast.LENGTH_SHORT).show()
            }

        }
        myWebView.loadUrl("https://www.instagram.com/accounts/login/")
        binding.saveCookies.setOnClickListener {
            val map = getCookieMap()
            var mapInString = map.toString()
            mapInString = mapInString.replace("{", "")
            mapInString = mapInString.replace("}", "")
            mapInString = mapInString.replace(",", ";")
            mapInString.trim()
            val sharedPreferences = activity?.getSharedPreferences("Cookies", 0)
            sharedPreferences?.edit()?.putString("loginCookies", mapInString)?.apply()
        }

    }
    private fun getCookieMap(): Map<String,String> {

        val manager = CookieManager.getInstance()
        val map = mutableMapOf<String,String>()

        manager.getCookie("https://www.instagram.com/")?.let {cookies ->
            Log.d("tagg", "Cookies we got: $cookies")
            val typedArray = cookies.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (element in typedArray) {
                val split = element.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if(split.size >= 2) {
                    map[split[0]] = split[1]
                } else if(split.size == 1) {
                    map[split[0]] = ""
                }
            }
        }

        return map
    }

}