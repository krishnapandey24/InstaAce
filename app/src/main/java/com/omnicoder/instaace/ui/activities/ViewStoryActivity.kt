package com.omnicoder.instaace.ui.activities

import android.app.Activity
import android.app.Dialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.omnicoder.instaace.R
import com.omnicoder.instaace.databinding.ViewStoryActivityBinding
import com.omnicoder.instaace.model.Story
import com.omnicoder.instaace.viewmodels.StoryViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ViewStoryActivity : AppCompatActivity() {
    private lateinit var binding: ViewStoryActivityBinding
    private lateinit var uri:Uri
    private lateinit var viewModel:StoryViewModel
    private lateinit var username:String
    private var mediaType:Int=1
    private lateinit var onComplete: BroadcastReceiver
    private var downloadLink:String?=null
    private var downloadID: Long = 0
    private lateinit var loadingDialog: Dialog
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
        position=story?.getInt("position",-1) ?: -1
        val picasso= Picasso.get()
        downloadLink=if(mediaType==1){
            binding.videoView.visibility = View.GONE
            picasso.load(imageUrl).into(binding.imageView)
            extension=".jpg"
            imageUrl
        }else{
            binding.imageView.visibility = View.GONE
            setVideo(Uri.parse(videoUrl))
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
                downloaded = true
            ),extension,downloadLink ?: "")

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

    private fun setVideo(videoUri:Uri?){
        if (videoUri != null) {
            binding.videoView.setMediaController(MediaController(this@ViewStoryActivity))
            binding.videoView.setVideoURI(videoUri)
            binding.videoView.start()
            uri=videoUri
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
        Log.d("tagg","onBAckpross $position $downloaded")
        val returnIntent= Intent()
        returnIntent.putExtra("position",position)
        returnIntent.putExtra("downloaded",downloaded)
        setResult(Activity.RESULT_OK,returnIntent)
        finish()
        super.onBackPressed()

    }









}