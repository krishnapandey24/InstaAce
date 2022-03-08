package com.omnicoder.instaace.ui.activities

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentResolverCompat.query
import com.omnicoder.instaace.R

class ViewPostActivity : AppCompatActivity() {
    var imageView: ImageView? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_post)
        imageView= findViewById(R.id.imageView)
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE
        )

        val selection = "${MediaStore.Video.Media.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(
            "umaru.jpg"
        )
        val sortOrder= "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
        val qq= contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
        qq?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val textView: TextView = findViewById(R.id.textView2)
            textView.setText(it.toString()+it.moveToFirst().toString()+it.moveToLast())
//            while (it.moveToNext()){
                val id = it.getLong(idColumn)
                val content: Uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,id)
                Log.d("tagg", "it is$content")

                val imageView: ImageView= findViewById(R.id.imageView)
                imageView.setImageURI(content)
//            }
        }



    }
}