package com.omnicoder.instaace.ui.activities

import android.content.ContentUris
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.omnicoder.instaace.R
import com.omnicoder.instaace.util.SharedStorageMedia
import com.omnicoder.instaace.util.sdk29AndUp


class ViewPostActivity : AppCompatActivity() {
    var imageView: ImageView? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_post)
        imageView= findViewById(R.id.imageView)
        loadPhoto()

    }

    private fun loadPhoto(){
        val collection= sdk29AndUp {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection= arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
        )
        val selection="${MediaStore.Images.Media.DISPLAY_NAME} == ?"
        val selectionArgs= arrayOf("mostlysane.jpg")
        val photos= mutableListOf<SharedStorageMedia>()
        contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
        )?.use { cursor ->
            val idColumn= cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val displayNameColumn= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val widthColumn= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightColumn= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)

            while (cursor.moveToNext()){
                val id= cursor.getLong(idColumn)
                val displayName= cursor.getString(displayNameColumn)
                Log.d("tagg","Display name: $displayName")
                val width= cursor.getInt(widthColumn)
                val height= cursor.getInt(heightColumn)
                val contentUri= ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                photos.add(SharedStorageMedia(id,displayName,width,height,contentUri))
            }
            photos.toList()
        } ?: listOf()

        Log.d("tagg","Name in the list with the tag"+ photos[0].name+ photos[0].contentUri)
        imageView?.setImageURI(photos[0].contentUri)
    }
}