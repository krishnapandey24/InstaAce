package com.omnicoder.instaace.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.omnicoder.instaace.adapters.StoryHighlightViewAdapter
import com.omnicoder.instaace.adapters.StoryViewAdapter
import com.omnicoder.instaace.databinding.ActivityDownloadStoryBinding
import com.omnicoder.instaace.model.Story
import com.omnicoder.instaace.viewmodels.StoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DownloadStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDownloadStoryBinding
    private lateinit var viewModel: StoryViewModel
    private lateinit var cookies: String
    private lateinit var username: String
    private lateinit var adapter: StoryViewAdapter
    private var selecting: Boolean=false
    private lateinit var stories: List<Story>
    private lateinit var onComplete: BroadcastReceiver
    private val downloadIds: MutableList<Long> = mutableListOf()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDownloadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        observeData(this)
        setOnClickListeners()
        onComplete= object : BroadcastReceiver() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                downloadIds.remove(id)
                if(downloadIds.isEmpty()  ){
                    adapter.isEnabled=false
                    val selectedStories: List<Int> = adapter.selectedStories
                    for(position in selectedStories){
                        adapter.dataHolder[position].downloaded=true
                    }
                    selecting=false
                    adapter.reset()
                    adapter.notifyDataSetChanged()
                    binding.downloadButton.visibility= View.GONE
                    Toast.makeText(context,"Download complete", Toast.LENGTH_SHORT).show()
                }

            }
        }
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

    }

    private fun init(){
        viewModel = ViewModelProvider(this)[StoryViewModel::class.java]
        cookies=intent.getStringExtra("cookie") ?: ""
        username=intent.getStringExtra("username") ?: ""
        val showHighlights=intent.getBooleanExtra("showHighlights",true)
        val userId=intent.getLongExtra("userId",0)
        binding.usernameView.text=username
        binding.progressBar.visibility=View.VISIBLE
        if(showHighlights) {
            viewModel.fetchStoryHighlights(userId, cookies)
            viewModel.fetchStory(userId,cookies)
        }else{
            val highlightId=intent.getStringExtra("highlightId")?:""
            viewModel.fetchStoryHighlightsStories(highlightId,cookies)
        }
        binding.downloadView.layoutManager= GridLayoutManager(this,3)
        binding.storyHighlightView.layoutManager= LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent: Intent? = result.data
                val downloaded=intent?.getBooleanExtra("downloaded",true) ?: true
                val position=intent?.getIntExtra("position",-1) ?: -1
                if(downloaded && position!=-1){
                    adapter.dataHolder[position].downloaded=true
                    adapter.notifyItemChanged(position)
                }
            }
        }
    }

    private fun observeData(context: Context?){
        viewModel.stories.observe(this){
            adapter=StoryViewAdapter(context,it,resultLauncher){
                showDownload()
            }
            binding.downloadView.adapter=adapter
            stories=it
            binding.progressBar.visibility=View.GONE
            if(it.isEmpty()){
                if(binding.noStoriesFoundViewStub.parent!=null){
                    binding.noStoriesFoundViewStub.inflate()
                }else{
                    binding.noStoriesFoundViewStub.visibility=View.VISIBLE
                }
            }else{
                binding.noStoriesFoundViewStub.visibility=View.GONE
            }

        }

        viewModel.storyHighlights.observe(this){
            binding.storyHighlightView.adapter = StoryHighlightViewAdapter(context,it,cookies)
        }

        viewModel.downloadId.observe(this){
            downloadIds.add(it)
        }

    }

    private fun setOnClickListeners() {
        binding.downloadButton.setOnClickListener {
            adapter.loading=true
            val selectedStories: List<Int> = adapter.selectedStories
            for(position in selectedStories){
                adapter.notifyItemChanged(position)
                viewModel.downloadStory(stories[position])
            }
        }

        binding.backButton.setOnClickListener{
            finish()

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showDownload(){
        adapter.notifyDataSetChanged()
        selecting=true
        binding.downloadButton.visibility=View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onComplete)
    }

}