package com.omnicoder.instaace.ui.activities

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
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
import com.omnicoder.instaace.util.SharedStorageMedia
import com.omnicoder.instaace.util.sdk29AndUp
import com.omnicoder.instaace.viewmodels.HomeViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ViewPostActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityViewPostBinding
    private var viewMore= true
    private lateinit var uri:Uri
    private lateinit var viewModel:HomeViewModel
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
        when (mediaType) {
            1 -> {
                binding.videoView.visibility = View.GONE
                binding.imageView.visibility = View.VISIBLE
                val photos = loadPhoto(name)
                if (photos != null) {
                    binding.imageView.setImageURI(photos)
                    uri=photos
                }
            }
            2 -> {
                binding.videoView.visibility = View.VISIBLE
                binding.imageView.visibility = View.GONE
                val videoUri = loadVideo(name)
                if (videoUri != null) {
                    binding.videoView.setMediaController(MediaController(this@ViewPostActivity2))
                    binding.videoView.setVideoURI(videoUri)
                    binding.videoView.start()
                    uri=videoUri
                }
                loadVideo(name)
                isImage = false
            }
            else -> {
                binding.imageView.visibility=View.GONE
                binding.videoView.visibility=View.GONE
                loadCarousel(instagramURL)

            }
        }

        binding.captionView.text=caption
        Picasso.get().load(profilePicture).into(binding.profilePicView)
        binding.usernameView.text= username
        setOnClickListeners(isImage,instagramURL,caption,username ?: "")


    }

    private fun loadCarousel(link:String){
        val viewStub= binding.viewStub.inflate()
        val viewPager:ViewPager2= viewStub.findViewById(R.id.viewpager)
        viewModel.getCarousel(link).observe(this){
            if(it!=null){
                val mediaList=fetchCarousel(it)
                viewPager.adapter= CarouselViewPagerAdapter(this@ViewPostActivity2,mediaList)
                val tabLayout: TabLayout = viewStub.findViewById(R.id.tabLayout)
                TabLayoutMediator(tabLayout, viewPager) { _: TabLayout.Tab, _: Int -> }.attach()
            }
        }
    }


    private fun fetchCarousel(carousels: List<Carousel>): List<CarouselMedia> {
        val medias = mutableListOf<CarouselMedia>()
        for (carousel in carousels) {
            val title = carousel.title ?: "lol"
            if (carousel.media_type == 1) {
                val uri = loadPhoto(title)
                medias.add(CarouselMedia(uri, 1))
            } else {
                val uri = loadVideo(title)
                medias.add(CarouselMedia(uri, 2))
            }
        }
        return medias
    }


    private fun setOnClickListeners(isImage:Boolean,instagramUrl: String,caption:String,username:String){
        val viewMore2 = "View More"
        val viewLess = "View Less"
        binding.viewMore.setOnClickListener {
            if (viewMore) {
                binding.captionView.maxLines = 70
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
            repost(caption, username)

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


    private fun loadPhoto(name:String) : Uri?{
            val collection = sdk29AndUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
            )
            val selection = "${MediaStore.Images.Media.DISPLAY_NAME} == ?"
            val selectionArgs = arrayOf(name)
            val photos = mutableListOf<SharedStorageMedia>()
            val uri: Uri? =contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
                cursor.moveToNext()
                 val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(displayNameColumn)
                Log.d("tagg", "Display name: $displayName")
                val width = cursor.getInt(widthColumn)
                val height = cursor.getInt(heightColumn)
                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                photos.add(SharedStorageMedia(id, displayName, width, height, contentUri))
                photos.toList()
                contentUri
            }
        return uri

    }

    private fun loadVideo(name:String): Uri?{
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
        val uri:Uri?=contentResolver.query(
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
            cursor.moveToNext()
             val id= cursor.getLong(idColumn)
            val displayName= cursor.getString(displayNameColumn)
            val contentUri= ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
            contentUri
        }

        return uri

    }

    private fun repost(caption:String,username:String) {
        val repostCaption= Constants.REPOST+"@"+username+" \n"+caption
        val clipboardManager=getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData= ClipData.newPlainText("text",repostCaption)
        clipboardManager.setPrimaryClip(clipData)
        val share = Intent(Intent.ACTION_SEND)
        share.type = "video/*"
        share.setPackage(Constants.INSTAGRAM)
        share.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(share, "Share to"))
        Toast.makeText(this@ViewPostActivity2,"Caption copied to clipboard",Toast.LENGTH_SHORT).show()
    }



}