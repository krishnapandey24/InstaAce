package com.omnicoder.instaace.ui.activities

import android.app.Dialog
import android.app.DownloadManager
import android.app.RecoverableSecurityException
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.view.ViewStub
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.omnicoder.instaace.R
import com.omnicoder.instaace.adapters.CarouselViewPagerAdapter
import com.omnicoder.instaace.database.Carousel
import com.omnicoder.instaace.databinding.ViewStoryActivityBinding
import com.omnicoder.instaace.model.CarouselMedia
import com.omnicoder.instaace.util.Constants
import com.omnicoder.instaace.util.InvalidLinkException
import com.omnicoder.instaace.util.sdk29AndUp
import com.omnicoder.instaace.viewmodels.HomeViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.lang.StringBuilder
import java.util.regex.Pattern


@AndroidEntryPoint
class ViewStoryActivity : AppCompatActivity() {
    private lateinit var binding: ViewStoryActivityBinding
    private var viewMore= true
    private lateinit var uri:Uri
    private lateinit var viewModel:HomeViewModel
    private var captionDialog: Dialog?= null
    private lateinit var mediaList: List<CarouselMedia>
    private lateinit var intentSenderLauncher : ActivityResultLauncher<IntentSenderRequest>
    private var deletedImageUri: Uri?=null
    private var notDeleted=true
    private lateinit var username:String
    private var mediaType:Int=1
    private var downloadLink:String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ViewStoryActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        val story: Bundle?= intent.extras
        mediaType= story?.getInt("media_type",1) ?: 1
        username= story?.getString("username") ?: ""
        val profilePicture= story?.getString("profilePicture")
        val picasso= Picasso.get()
        downloadLink=if(mediaType==1){
            val imageUrl= story?.getString("imageUrl")
            binding.videoView.visibility = View.GONE
            picasso.load(imageUrl).into(binding.imageView)
            imageUrl
        }else{
            binding.imageView.visibility = View.GONE
            val videoUrl =story?.getString("videoUrl")
            setVideo(Uri.parse(videoUrl))
            videoUrl
        }
        picasso.load(profilePicture).into(binding.profilePicView)
        binding.usernameView.text= username
//        /setOnClickListeners(isImage,instagramURL,caption,username ?: "",isCarousel)
//        initIntentSenderLauncher()
    }

//    private fun setOnClickListeners(isImage:Boolean,instagramUrl: String,caption:String,username:String,isCarousel:Boolean){
//        val viewMore2 = "View More"
//        val viewLess = "View Less"
//        binding.viewMore.setOnClickListener {
//            if (viewMore) {
//                binding.captionView.maxLines = 30
//                binding.viewMore.text = viewLess
//            } else {
//                binding.captionView.maxLines = 3
//                binding.viewMore.text = viewMore2
//            }
//            viewMore = !viewMore
//        }
//        binding.backButton.setOnClickListener{
//            finish()
//        }
//
//        binding.repost.setOnClickListener{
//            repost(caption, username,isImage)
//        }
//
//        binding.caption.setOnClickListener{
//            launchCaptionDialog(caption,username)
//        }
//
//        binding.menu.setOnClickListener{
//            val popup = PopupMenu(this, it)
//            val inflater: MenuInflater = popup.menuInflater
//            inflater.inflate(R.menu.view_post_menu, popup.menu)
//            popup.setOnMenuItemClickListener { menuItem ->
//                when(menuItem.itemId){
//                    R.id.openInInstagram-> {
//                        val uri= Uri.parse(instagramUrl)
//                        val intent= Intent(Intent.ACTION_VIEW,uri)
//                        intent.setPackage(Constants.INSTAGRAM)
//                        startActivity(intent)
//                    }
//                    R.id.delete ->{
//                        deletePost(instagramUrl,isCarousel)
//                    }
//                }
//                true
//            }
//            popup.show()
//
//        }
//
//        binding.share.setOnClickListener{
//            val intent = Intent(Intent.ACTION_SEND)
//            val intentType=if(isImage){
//                "image/jpeg"
//            }else{
//                "video/mp4"
//            }
//            intent.type = intentType
//            intent.putExtra(Intent.EXTRA_STREAM, uri)
//            startActivity(Intent.createChooser(intent, "Share"))
//        }
//
//    }


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








}