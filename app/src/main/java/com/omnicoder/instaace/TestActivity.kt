package com.omnicoder.instaace

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.omnicoder.instaace.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream


class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val textView: TextView = findViewById(R.id.textView)
        val cookie = intent.getStringExtra("cookie")
        Log.d("tagg","cookie $cookie")
        val client: OkHttpClient = OkHttpClient().newBuilder()
            .addInterceptor {
                val original = it.request()
                val authorized = original.newBuilder()
                    .addHeader("Cookie", cookie ?: "")
                    .addHeader("User-Agent", Constants.USER_AGENT)
                    .build()
                it.proceed(authorized)
            }
            .build()

//        val request: Request = Request.Builder().url("https://i.instagram.com/api/v1/feed/reels_media/?reel_ids=6989693455").build()
        val request: Request = Request.Builder().url("https://i.instagram.com/api/v1/feed/user/349922303/story/").build()

        lifecycleScope.launch {

        val texter:String = withContext(Dispatchers.IO) {
            var returnText = "nothing chnaged"
            returnText = try {
                val response = client.newCall(request).execute()
                val body = response.body()
                if (body != null) {
                    body.string()
                } else {
                    "body is null"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "Error: ${e.message}"
            }
            returnText
        }
            Log.d("tagg","response \n $texter \n")
            textView.text= texter

    }




    }
}