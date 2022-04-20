package com.omnicoder.instaace.ui.activities

import android.app.Activity
import android.app.Dialog
import android.app.DownloadManager
import android.content.*
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.omnicoder.instaace.R
import com.omnicoder.instaace.databinding.ViewStoryActivityBinding
import com.omnicoder.instaace.model.Story
import com.omnicoder.instaace.util.sdk29AndUp
import com.omnicoder.instaace.viewmodels.StoryViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception


@AndroidEntryPoint
class ViewStoryActivity : AppCompatActivity() {
    private lateinit var binding: ViewStoryActivityBinding
    private lateinit var uri:Uri
    private lateinit var viewModel:StoryViewModel
    private lateinit var username:String
    private lateinit var onComplete: BroadcastReceiver
    private lateinit var loadingDialog: Dialog
    private var mediaType:Int=1
    private var downloadLink:String?=null
    private var downloadID: Long = 0
    private var downloaded=false
    private var position=-1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ViewStoryActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[StoryViewModel::class.java]
        val story: Bundle?= intent.extras
        val code: String=story?.getString("code") ?: System.currentTimeMillis().toString()
        mediaType= story?.getInt("media_type",1) ?: 1
        username= story?.getString("username") ?: ""
        val profilePicture= story?.getString("profilePicture")
        val extension: String
        val imageUrl= story?.getString("imageUrl")
        val videoUrl =story?.getString("videoUrl")
        val alreadyDownloaded=story?.getBoolean("alreadyDownloaded") ?: false
        val name: String= story?.getString("name") ?: ""
        position=story?.getInt("position",-1) ?: -1
        val picasso= Picasso.get()
        Log.d("tagg","so here it: $alreadyDownloaded $name")
        downloadLink=if(mediaType==1){
            binding.videoView.visibility = View.GONE
            if(alreadyDownloaded){
                loadPhoto(name,imageUrl)
            }else{
                picasso.load(imageUrl).into(binding.imageView)
            }
            extension=".jpg"
            imageUrl
        }else{
            binding.progressBar.inflate()
            binding.imageView.visibility = View.GONE
            val videoUri: Uri?= if(alreadyDownloaded) loadVideo(name,videoUrl) else Uri.parse(videoUrl)
            setVideo(videoUri)
            extension=".mp4"
            videoUrl
        }
        picasso.load(profilePicture).into(binding.profilePicView)
        binding.usernameView.text= username

        binding.backButton.setOnClickListener{
            onBackPressed()
        }

        binding.downloadButton.setOnClickListener{
            loadingDialog=Dialog(this)
            loadingDialog.setContentView(R.layout.download_loading_dialog)
            loadingDialog.show()
            viewModel.downloadStoryDirect(Story(code,mediaType,imageUrl ?: "",videoUrl,username, profilePicture ?: "",
                isSelected = false,
                downloaded = true,
            null),extension,downloadLink ?: "")

        }

        viewModel.downloadId.observe(this) {
            downloadID = it
        }

        onComplete= object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if(id==downloadID){
                    downloaded=true
                    loadingDialog.dismiss()
                    Toast.makeText(context,"Download complete",Toast.LENGTH_SHORT).show()
                }

            }
        }
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }


    private fun loadPhoto(name:String,imageUrl: String?){
        try {
            val collection = sdk29AndUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(MediaStore.Images.Media._ID)
            val selection = "${MediaStore.Images.Media.DISPLAY_NAME} == ?"
            val selectionArgs = arrayOf(name)
            val uri: Uri? = contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                cursor.moveToNext()
                val id = cursor.getLong(idColumn)
                val contentUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                contentUri
            }
            binding.imageView.setImageURI(uri)
            binding.downloadButton.visibility=View.GONE
        }catch(e:android.database.CursorIndexOutOfBoundsException){
            e.printStackTrace()
            Picasso.get().load(imageUrl).into(binding.imageView)
        }
    }

    private fun loadVideo(name:String, videoUrl:String?): Uri?{
        try {
            val collection = sdk29AndUp {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } ?: MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(MediaStore.Video.Media._ID)
            val selection = "${MediaStore.Video.Media.DISPLAY_NAME} == ?"
            val selectionArgs = arrayOf(name)
            val uri: Uri? = contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                cursor.moveToNext()
                val id = cursor.getLong(idColumn)
                val contentUri =
                    ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                contentUri
            }
            binding.downloadButton.visibility=View.GONE
            return uri
        }catch(e:Exception){
            return Uri.parse(videoUrl)
        }
    }


    private fun setVideo(videoUri:Uri?){
        if (videoUri != null) {
            binding.videoView.setMediaController(MediaController(this@ViewStoryActivity))
            binding.videoView.setVideoURI(videoUri)
            binding.videoView.start()
            uri=videoUri
            val onInfoToPlayStateListener: MediaPlayer.OnInfoListener = MediaPlayer.OnInfoListener { _, what, _ ->
                    if (MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START == what) {
                        binding.progressBar.visibility = View.GONE
                    }
                    false
                }
            binding.videoView.setOnInfoListener(onInfoToPlayStateListener)
            binding.videoView.setOnCompletionListener {
                it.seekTo(0)
                it.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onComplete)
    }

    override fun onBackPressed() {
        val returnIntent= Intent()
        returnIntent.putExtra("position",position)
        returnIntent.putExtra("downloaded",downloaded)
        setResult(Activity.RESULT_OK,returnIntent)
        finish()
        super.onBackPressed()
    }









}