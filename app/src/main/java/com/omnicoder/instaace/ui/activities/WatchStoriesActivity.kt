package com.omnicoder.instaace.ui.activities

import android.app.Dialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.omnicoder.instaace.R
import com.omnicoder.instaace.databinding.ActivityWatchStoriesBinding
import com.omnicoder.instaace.model.ReelMedia
import com.omnicoder.instaace.model.ReelTray
import com.omnicoder.instaace.model.Story
import com.omnicoder.instaace.util.storyviewer.StoriesProgressView
import com.omnicoder.instaace.viewmodels.StoryViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchStoriesActivity : AppCompatActivity(),StoriesProgressView.StoriesListener {
    private lateinit var binding:ActivityWatchStoriesBinding
    private lateinit var viewModel: StoryViewModel
    private val reelMediaList: MutableList<ReelMedia> = mutableListOf()
    private var counter=0
    private var imageUrl=""
    private lateinit var loadingDialog: Dialog
    private var videoUrl: String?=null
    private lateinit var reelTray: ReelTray
    private lateinit var onDownloadComplete: BroadcastReceiver
    private var downloadID: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWatchStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[StoryViewModel::class.java]
        val reelId: Long=intent.getLongExtra("reelId",0L)
        val cookie: String=intent.getStringExtra("cookie") ?: ""
        viewModel.fetchReelMedia(reelId,cookie)
        binding.stories.setStoriesListener(this@WatchStoriesActivity)
        loadingDialog=Dialog(this)
        loadingDialog.setContentView(R.layout.download_loading_dialog)
        loadingDialog.setCancelable(false)
        observeData()
        setOnClickListener()
        onDownloadComplete= object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if(id==downloadID){
                    reelTray.items[counter].downloaded=true
                    loadingDialog.dismiss()
                    binding.stories.resume()
                    Toast.makeText(context,"Download complete", Toast.LENGTH_SHORT).show()
                }
            }
        }
        registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

    }

    private fun observeData(){
        viewModel.reelMedia.observe(this) {
            if (it != null) {
                reelTray=it
                val user = it.user
                Picasso.get().load(user.profile_pic_url).into(binding.profilePicView)
                binding.usernameView.text=user.username
                val items=it.items
                for(item in items){
                    val uri: String
                    val mediaType=if(item.media_type==1){
                        uri=item.image_versions2.candidates[0].url
                        true
                    }else{
                        uri= item.video_versions?.get(0)?.url ?: ""
                        false
                    }
                    reelMediaList.add(ReelMedia(uri,mediaType))
                }
                setStory()
            }
        }


        viewModel.downloadId.observe(this) {
            downloadID = it
        }

    }

    private fun setOnClickListener() {
        binding.profilePicView.setOnClickListener {
            binding.stories.pause()
        }

        binding.usernameView.setOnClickListener {
            binding.stories.resume()
        }

        binding.downloadButton.setOnClickListener {
            binding.stories.pause()
            loadingDialog.show()
            val item = reelTray.items[counter]
            if (item.downloaded) {
                Toast.makeText(this, "Already Downloaded", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.downloadStory(Story(item.code,item.media_type,item.image_versions2.candidates[0].url,videoUrl,reelTray.user.username,reelTray.user.profile_pic_url,
                    isSelected = false,
                    downloaded = false,
                    name = null
                ))
            }
        }

    }

    private fun setStory() {
        val size=reelMediaList.size
        val storiesProgressView= binding.stories
        storiesProgressView.setStoriesCount(size)
        storiesProgressView.setStoryDuration(7000L)
        binding.reverse.setOnClickListener{
            storiesProgressView.reverse()
        }
        binding.skip.setOnClickListener{
            storiesProgressView.skip()
        }
        storiesProgressView.startStories()
        changeMedia(reelMediaList[counter],0)

    }

    private fun changeMedia(reelMedia: ReelMedia, index: Int){
        binding.progressBar.visibility=View.VISIBLE
        if(reelMedia.isImage){
            binding.videoView.visibility = View.GONE
            binding.imageView.visibility= View.VISIBLE
            imageUrl=reelMedia.uri
            Picasso.get().load(reelMedia.uri).into(binding.imageView, object : Callback {
                override fun onSuccess() {
                    binding.progressBar.visibility= View.GONE
                    binding.stories.resume()

                }
                override fun onError(e: Exception?) {
                    e?.printStackTrace()
                }

            })
        }else{
            binding.imageView.visibility = View.GONE
            videoUrl=reelMedia.uri
            val videoUri: Uri?= Uri.parse(reelMedia.uri)
            setVideo(videoUri,index)
        }
    }

    private fun setVideo(videoUri:Uri?,index: Int){
        if (videoUri != null) {
            binding.videoView.setVideoURI(videoUri)
            binding.videoView.visibility= View.VISIBLE
            binding.videoView.setOnPreparedListener {
                val duration: Long= it.duration.toLong()
                binding.stories.progressBars[index].animation.duration=duration
                binding.videoView.start()
                binding.progressBar.visibility = View.GONE
                binding.stories.resume()
            }
        }
    }

    override fun onNext() {
        counter += 1
        changeMedia(reelMediaList[counter],counter)
    }

    override fun onPrev() {
        if((counter -1 ) <0 ) return
        changeMedia(reelMediaList[--counter],counter)
    }

    override fun onComplete() {
        finish()
    }

    override fun onDestroy() {
        binding.stories.destroy()
        super.onDestroy()
        if(this::onDownloadComplete.isInitialized){
            unregisterReceiver(onDownloadComplete)
        }
    }


}