package com.omnicoder.instaace

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val intent = intent
        val cookie = intent.getStringExtra("cookie") ?: "lol"
        val client = OkHttpClient()
        val textView = findViewById<TextView>(R.id.textView)
        lifecycleScope.launch {
            val str=withContext(Dispatchers.IO){
            val request = Request.Builder().url("https://www.instagram.com/p/CbelegIP-2jW56kjYT4AQW2vcFERtSFQYaN-4Q0/?__a=1")
                .header("Cookie", cookie).build()
            var message = "lol"
                message = try {
                    val response = client.newCall(request).execute()
                    val body = response.body()
                    "body"+body?.string() ?: "body is null"
                } catch (e: Exception) {
                    e.printStackTrace()
                    "the message is: " + e.message
                }
            message
        }
            textView.text=str
        }
    }
}