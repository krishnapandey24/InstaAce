package com.omnicoder.instaace.ui.activities

import android.content.ContentUris
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.omnicoder.instaace.databinding.ActivityViewPostBinding
import com.omnicoder.instaace.util.SharedStorageMedia
import com.omnicoder.instaace.util.sdk29AndUp


class ViewPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewPostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityViewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val name:String= intent.getStringExtra("name") ?: "lol"
        val mediaType= intent.getIntExtra("media_type",1)
        if(mediaType==1){
            binding.videoView.visibility= View.GONE
            loadPhoto(name)
        }else{
            binding.imageView.visibility= View.GONE
            loadVideo(name)
        }

    }

    private fun loadPhoto(name:String){
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
        val selectionArgs= arrayOf(name)
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
        if(photos.isNotEmpty()){
            binding.imageView.setImageURI(photos[0].contentUri)
        }

    }

   private fun loadVideo(name:String){
        val collection= sdk29AndUp {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } ?: MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection= arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                )
        val selection="${MediaStore.Video.Media.DISPLAY_NAME} == ?"
        val selectionArgs= arrayOf(name)
        val photos= mutableListOf<SharedStorageMedia>()
        contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
        )?.use { cursor ->
                val idColumn= cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val displayNameColumn= cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val widthColumn= cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
            val heightColumn= cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)

            while (cursor.moveToNext()){
                val id= cursor.getLong(idColumn)
                val displayName= cursor.getString(displayNameColumn)
                Log.d("tagg","Display name: $displayName")
                val width= cursor.getInt(widthColumn)
                val height= cursor.getInt(heightColumn)
                val contentUri= ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                photos.add(SharedStorageMedia(id,displayName,width,height,contentUri))
            }
            photos.toList()
        } ?: listOf()
       if(photos.isNotEmpty()){
           binding.videoView.setMediaController(MediaController(this@ViewPostActivity))
           binding.videoView.setVideoURI(photos[0].contentUri)
           binding.videoView.start()
       }
    }
}