package com.omnicoder.instaace.ui.activities

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.omnicoder.instaace.databinding.ActivityWatchStoriesBinding
import com.omnicoder.instaace.model.ReelMedia
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWatchStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[StoryViewModel::class.java]
        val reelId: Long=intent.getLongExtra("reelId",0L)
        val cookie: String=intent.getStringExtra("cookie") ?: ""
        viewModel.fetchReelMedia(reelId,cookie)
        binding.stories.setStoriesListener(this@WatchStoriesActivity)
        viewModel.reelMedia.observe(this) {
            if (it != null) {
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
        binding.profilePicView.setOnClickListener{
            binding.stories.pause()
        }

        binding.usernameView.setOnClickListener{
            binding.stories.resume()
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
        Log.d("tagg","chaningmedia")
        binding.progressBar.visibility=View.VISIBLE
        if(reelMedia.isImage){
            Log.d("tagg","it is image ${reelMedia.uri}")
            binding.videoView.visibility = View.GONE
            binding.imageView.visibility= View.VISIBLE
            Picasso.get().load(reelMedia.uri).into(binding.imageView, object : Callback {
                override fun onSuccess() {
                    Log.d("tagg","image llade")
                    binding.progressBar.visibility= View.GONE
                    try{
                    binding.stories.resume()}catch (e: Exception){e.printStackTrace()}
                }
                override fun onError(e: Exception?) {
                    e?.printStackTrace()
                }

            })
        }else{
            Log.d("tagg","is a video")
            binding.imageView.visibility = View.GONE
            val videoUri: Uri?= Uri.parse(reelMedia.uri)
            Log.d("tagg","video uri $videoUri and url: ${reelMedia.uri}")
            setVideo(videoUri,index)
        }
    }

    private fun setVideo(videoUri:Uri?,index: Int){
        Log.d("tagg","setting the video: yes")
        if (videoUri != null) {
            Log.d("tagg","not null")
            binding.videoView.setVideoURI(videoUri)
            Log.d("tagg","setting vdoe ueri")
            Log.d("tagg","starting")
                    binding.videoView.visibility= View.VISIBLE
            binding.videoView.start()
            val onInfoToPlayStateListener: MediaPlayer.OnInfoListener = MediaPlayer.OnInfoListener { mp, what, _ ->
                val duration: Long=mp.duration.toLong()
                binding.stories.progressBars[index].animation.duration=duration

                if (MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START == what) {
                    binding.progressBar.visibility = View.GONE
                    binding.stories.resume()
                    Log.d("tagg","starting in the if")

                }else{
                    Log.d("tagg"," what $what")
                }
                false
            }
            binding.videoView.setOnInfoListener(onInfoToPlayStateListener)


        }
    }

    override fun onNext() {
        counter += 1
        Log.d("tagg","OnNext $counter")
        changeMedia(reelMediaList[counter],counter)
    }

    override fun onPrev() {
        Log.d("tagg","OnPrev")
        if((counter -1 ) <0 ) return
        changeMedia(reelMediaList[--counter],counter)
    }

    override fun onComplete() {
        Log.d("tagg","Completed")
        finish()
    }

    override fun onDestroy() {
        binding.stories.destroy()
        super.onDestroy()
    }


}