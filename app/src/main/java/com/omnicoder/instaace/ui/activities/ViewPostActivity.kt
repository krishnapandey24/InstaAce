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
import com.omnicoder.instaace.databinding.ActivityViewPostBinding
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
class ViewPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewPostBinding
    private var viewMore= true
    private lateinit var uri:Uri
    private lateinit var viewModel:HomeViewModel
    private var captionDialog: Dialog?= null
    private lateinit var mediaList: List<CarouselMedia>
    private lateinit var intentSenderLauncher : ActivityResultLauncher<IntentSenderRequest>
    private var deletedImageUri: Uri?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityViewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        val post: Bundle?= intent.extras
        val name:String= post?.getString("name") ?: "lol"
        val mediaType= post?.getInt("media_type",1)
        val caption= post?.getString("caption") ?: ""
        val username= post?.getString("username")
        val profilePicture= post?.getString("profilePicture")
        val instagramURL=post?.getString("instagram_url") ?: Constants.INSTAGRAM_HOMEPAGE_LINK
        var isImage=true
        var isCarousel=false
        when (mediaType) {
            1 -> {
                binding.videoView.visibility = View.GONE
                binding.imageView.visibility = View.VISIBLE
                val photos = loadPhoto(name, post.getString("imageUrl"))
                if (photos != null) {
                    binding.imageView.setImageURI(photos)
                    uri=photos
                }
            }
            2 -> {
                binding.videoView.visibility = View.VISIBLE
                binding.imageView.visibility = View.GONE
                setVideo(loadVideo(name, post.getString("videoUrl")))
                isImage=false
            }
            else -> {
                loadCarousel(instagramURL)
                binding.imageView.visibility=View.GONE
                binding.videoView.visibility=View.GONE
                isCarousel=true

            }
        }

        binding.captionView.text=caption
        Picasso.get().load(profilePicture).into(binding.profilePicView)
        binding.usernameView.text= username
        setOnClickListeners(isImage,instagramURL,caption,username ?: "",isCarousel)
        initIntentSenderLauncher()
    }

    private fun loadCarousel(link:String){
        val viewStub= binding.viewStub.inflate()
        val viewPager:ViewPager2= viewStub.findViewById(R.id.viewpager)
        viewModel.getCarousel(link).observe(this){
            if(it!=null){
                mediaList=fetchCarousel(it)
                viewPager.adapter= CarouselViewPagerAdapter(this@ViewPostActivity,mediaList)
                val tabLayout: TabLayout = viewStub.findViewById(R.id.tabLayout)
                TabLayoutMediator(tabLayout, viewPager) { _: TabLayout.Tab, _: Int -> }.attach()
                uri= mediaList[0].uri!!
            }
        }
    }

    private fun initIntentSenderLauncher() {
        intentSenderLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                        deleteFile(deletedImageUri ?: return@registerForActivityResult)
                    }
                } else {
                    throw InvalidLinkException("Unable to delete post")
                }
        }
    }


    private fun fetchCarousel(carousels: List<Carousel>): List<CarouselMedia> {
        val medias = mutableListOf<CarouselMedia>()
        for (carousel in carousels) {
            val title = carousel.title ?: "lol"
            if (carousel.media_type == 1) {
                val uri = loadPhoto(title,carousel.image_url)
                medias.add(CarouselMedia(uri, 1))
            } else {
                val uri = loadVideo(title,carousel.video_url)
                medias.add(CarouselMedia(uri, 2))
            }
        }
        return medias
    }


    private fun setOnClickListeners(isImage:Boolean,instagramUrl: String,caption:String,username:String,isCarousel:Boolean){
        val viewMore2 = "View More"
        val viewLess = "View Less"
        binding.viewMore.setOnClickListener {
            if (viewMore) {
                binding.captionView.maxLines = 30
                binding.viewMore.text = viewLess
            } else {
                binding.captionView.maxLines = 3
                binding.viewMore.text = viewMore2
            }
            viewMore = !viewMore
        }
        binding.backButton.setOnClickListener{
            finish()
        }

        binding.repost.setOnClickListener{
            repost(caption, username,isImage)
        }

        binding.caption.setOnClickListener{
            launchCaptionDialog(caption,username)
        }

        binding.menu.setOnClickListener{
            val popup = PopupMenu(this, it)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.view_post_menu, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId){
                    R.id.openInInstagram-> {
                        val uri= Uri.parse(instagramUrl)
                        val intent= Intent(Intent.ACTION_VIEW,uri)
                        intent.setPackage(Constants.INSTAGRAM)
                        startActivity(intent)
                    }
                    R.id.delete ->{
                        deletePost(instagramUrl,isCarousel)
                    }
                }
                true
            }
            popup.show()

        }

        binding.share.setOnClickListener{
            val intent = Intent(Intent.ACTION_SEND)
            val intentType=if(isImage){
                "image/jpeg"
            }else{
                "video/mp4"
            }
            intent.type = intentType
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(intent, "Share"))
        }

    }


    private fun loadPhoto(name:String,imageUrl: String?) : Uri?{
        Log.d("tagg","loadPhoto called")
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
            return uri
        }catch(e:android.database.CursorIndexOutOfBoundsException){
            e.printStackTrace()
            if(imageUrl!=null) {
                val alertDialog = AlertDialog.Builder(this@ViewPostActivity)
                    .setTitle("Image Not Found!")
                    .setMessage("The Image is Either deleted, renamed or moved to other media.")
                    .setPositiveButton("Download Again") { dialogInterface, _ ->
                        download(imageUrl, true, name)
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                alertDialog.show()
            }
            return null
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
            return uri
        }catch(e:android.database.CursorIndexOutOfBoundsException){
            Log.d("tagg","catch it $videoUrl")
            if(videoUrl!=null) {
                val alertDialog = AlertDialog.Builder(this@ViewPostActivity)
                    .setTitle("Video Not Found!")
                    .setMessage("The Video is Either deleted, renamed or moved to other media.")
                    .setPositiveButton("Download Again") { dialogInterface, _ ->
                        download(videoUrl, false, name)
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                alertDialog.show()
            }
            return null
        }

    }

    private fun repost(caption:String,username:String,isImage:Boolean) {
        val repostCaption= Constants.REPOST+"@"+username+" \n"+caption
        val clipboardManager=getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData= ClipData.newPlainText("text",repostCaption)
        clipboardManager.setPrimaryClip(clipData)
        val share = Intent(Intent.ACTION_SEND)
        share.type = if(isImage) "image/*" else "video/*"
        share.setPackage(Constants.INSTAGRAM)
        share.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(share, "Share to"))
        Toast.makeText(this@ViewPostActivity,"Caption copied to clipboard",Toast.LENGTH_SHORT).show()
    }

    private fun launchCaptionDialog(caption: String, username: String) {
        if (captionDialog == null) {
            val clipboardManager=getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            captionDialog = Dialog(this@ViewPostActivity)
            captionDialog!!.setContentView(R.layout.caption_dialog_layout)
            captionDialog!!.window?.setBackgroundDrawable(ContextCompat.getDrawable(this@ViewPostActivity,R.drawable.rounded_corner_white_rectangle))
            captionDialog!!.findViewById<TextView>(R.id.captionView).text = caption

            captionDialog!!.findViewById<TextView>(R.id.copyAll).setOnClickListener{
                val clipData= ClipData.newPlainText("text",caption)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(this@ViewPostActivity,"Caption copied!",Toast.LENGTH_SHORT).show()
            }

            captionDialog!!.findViewById<TextView>(R.id.copyHashtags).setOnClickListener{
                val loadingViewStub=captionDialog!!.findViewById<ViewStub>(R.id.loadingViewStub)
                loadingViewStub.inflate()
                val stringBuilder= StringBuilder()
                val pattern= Pattern.compile("#(\\w+)")
                val match=pattern.matcher(caption)
                while(match.find()){
                    stringBuilder.append("#"+match.group(1)+" ")
                }
                val clipData= ClipData.newPlainText("text",stringBuilder)
                clipboardManager.setPrimaryClip(clipData)
                loadingViewStub.visibility=View.GONE
                Toast.makeText(this@ViewPostActivity,"Hashtags copied!",Toast.LENGTH_SHORT).show()
            }

            captionDialog!!.findViewById<TextView>(R.id.copyUsername).setOnClickListener{
                val clipData= ClipData.newPlainText("text",username)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(this@ViewPostActivity,"Username copied!",Toast.LENGTH_SHORT).show()
            }
        }
        captionDialog!!.show()
    }

    private fun setVideo(videoUri:Uri?){
        if (videoUri != null) {
            binding.videoView.setMediaController(MediaController(this@ViewPostActivity))
            binding.videoView.setVideoURI(videoUri)
            binding.videoView.start()
            uri=videoUri
            binding.videoView.setOnCompletionListener {
                it.seekTo(0)
                it.start()
            }
        }
    }

    private fun deleteFile(uri:Uri) {
        try {
            contentResolver.delete(uri, null, null)
        } catch (e: SecurityException) {
            try {
                val intentSender = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                        MediaStore.createDeleteRequest(contentResolver, listOf(uri)).intentSender
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                        val recoverableSecurityException = e as? RecoverableSecurityException
                        recoverableSecurityException?.userAction?.actionIntent?.intentSender
                    }
                    else -> null
                }
                intentSender?.let {
                    intentSenderLauncher.launch(IntentSenderRequest.Builder(it).build())
                }
            }catch (e: InvalidLinkException){
                Toast.makeText(this@ViewPostActivity,e.message,Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun deletePost(instagramUrl:String,isCarousel:Boolean){
        val clipboardManager=getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val primaryClip= clipboardManager.primaryClip?.getItemAt(0)?.text.toString()
        if(primaryClip==instagramUrl){
            clipboardManager.setPrimaryClip(ClipData.newPlainText("text",""))
        }
        viewModel.deletePost(instagramUrl)
        if(isCarousel){
            for(media in mediaList){
                viewModel.deleteCarousel(instagramUrl)
                val uri= media.uri
                if(uri!=null){
                    deletedImageUri=uri
                    deleteFile(uri)
                }
            }
        }else{
            deletedImageUri=uri
            deleteFile(uri)
        }
        Toast.makeText(this@ViewPostActivity,"Post Delete successfully",Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun download(link: String,isImage:Boolean,title:String){
        Log.d("tagg","Inside download")
        val loadingDialog=Dialog(this)
        loadingDialog.setContentView(R.layout.download_loading_dialog)
        loadingDialog.show()
        val path= if(isImage) Constants.IMAGE_FOLDER_NAME else Constants.VIDEO_FOLDER_NAME
        var downloadId=0L
        viewModel.downloadPost2(link,path,title)
        viewModel.downloadId.observe(this) {
            Log.d("tagg","it changed")
            downloadId = it
        }
        lateinit var onComplete:BroadcastReceiver
        onComplete= object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.d("tagg","OnReciever")
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if(id==downloadId){
                    if(isImage){
                        Log.d("tagg","title of file is: $title")
                        binding.imageView.setImageURI(loadPhoto(title,null))
                    }else{
                        Log.d("tagg","title of file is: $title")
                        setVideo(loadVideo(title,null))
                    }
                    loadingDialog.dismiss()
                    unregisterReceiver(onComplete)
                }

            }
        }

        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }





}