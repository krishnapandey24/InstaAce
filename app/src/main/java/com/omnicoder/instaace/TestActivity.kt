package com.omnicoder.instaace

import android.content.ContentUris
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.omnicoder.instaace.util.Constants
import com.omnicoder.instaace.util.sdk29AndUp
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

    fun fetchPosts(){
        val collection = sdk29AndUp {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } ?: MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Video.Media._ID)
        val selection = "${MediaStore.Video.Media.BUCKET_DISPLAY_NAME} == ?"
        val selectionArgs = arrayOf("Instagram Videos")
        contentResolver?.query(
            collection,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            var count=0
            Log.d("tagg","Starting count")
            while(cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                Log.d("tagg","$id $contentUri $count")
                count+=1
            }
        }
    }
}