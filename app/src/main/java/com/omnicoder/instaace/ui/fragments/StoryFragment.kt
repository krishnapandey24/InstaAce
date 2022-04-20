package com.omnicoder.instaace.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.omnicoder.instaace.adapters.StoryViewAdapter
import com.omnicoder.instaace.databinding.StoryFragmentBinding
import com.omnicoder.instaace.model.Story
import com.omnicoder.instaace.viewmodels.StoryViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
@SuppressLint("NotifyDataSetChanged")
class StoryFragment : Fragment() {
    private lateinit var binding: StoryFragmentBinding
    private lateinit var viewModel: StoryViewModel
    private lateinit var cookies: String
    private lateinit var adapter: StoryViewAdapter
    private var selecting: Boolean=false
    private lateinit var stories: List<Story>
    private lateinit var onComplete: BroadcastReceiver
    private val downloadIds: MutableList<Long> = mutableListOf()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding= StoryFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[StoryViewModel::class.java]
        val args: StoryFragmentArgs by navArgs()
        cookies= args.cookie
        observeData(context)
        setOnClickListeners()
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(selecting){
                    adapter.reset()
                    adapter.notifyDataSetChanged()
                    selecting=false
                    binding.downloadButton.visibility=View.GONE
                }else{
                    NavHostFragment.findNavController(this@StoryFragment).navigateUp()
                }
            }
        })

        onComplete= object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                downloadIds.remove(id)
                if(downloadIds.isEmpty()){
                    adapter.isEnabled=false
                    val selectedStories: List<Int> = adapter.selectedStories
                    for(position in selectedStories){
                        Log.d("tagg","loop position $position ${adapter.dataHolder[position].name}")
                        adapter.dataHolder[position].downloaded=true
                    }
                    selecting=false
                    adapter.reset()
                    adapter.notifyDataSetChanged()
                    binding.downloadButton.visibility=View.GONE
                    Toast.makeText(context,"Download complete",Toast.LENGTH_SHORT).show()
                }

            }
        }
        activity?.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
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
        viewModel.stories.observe(viewLifecycleOwner){
            setRecyclerView(it,context)
            val size=it.size
            binding.fileCount.text=size.toString()
            stories=it
            binding.progressBar.visibility=View.GONE
            if(size<1){
                if(binding.noStoriesFoundViewStub.parent!=null){
                    binding.noStoriesFoundViewStub.inflate()
                }else{
                    binding.noStoriesFoundViewStub.visibility=View.VISIBLE
                }
            }else{
                binding.noStoriesFoundViewStub.visibility=View.GONE
            }
        }

        viewModel.downloadId.observe(viewLifecycleOwner){
            downloadIds.add(it)
        }

    }

    private fun setOnClickListeners() {
        binding.fetchButton.setOnClickListener{
            binding.progressBar.visibility=View.VISIBLE
            viewModel.fetchStory(binding.editText.text.toString(),cookies)
            hideKeyboard()
            binding.editText.text.clear()
        }

        binding.downloadButton.setOnClickListener {
            adapter.loading=true
            val selectedStories: List<Int> = adapter.selectedStories
            for(position in selectedStories){
                adapter.notifyItemChanged(position)
                viewModel.downloadStory(stories[position])
            }
        }

        binding.backButton.setOnClickListener{
            NavHostFragment.findNavController(this@StoryFragment).navigateUp()
        }
    }

    private fun setRecyclerView(stories: List<Story>, context: Context?) {
        val recyclerView: RecyclerView = binding.downloadView
        recyclerView.layoutManager=GridLayoutManager(context,3)
        adapter=StoryViewAdapter(context,stories,resultLauncher){
            showDownload()
        }
        recyclerView.adapter = adapter
    }

    private fun showDownload(){
        adapter.notifyDataSetChanged()
        selecting=true
        binding.downloadButton.visibility=View.VISIBLE
    }

    private fun fetchStories(){

    }

    override fun onResume() {
        super.onResume()
        fetchStories()
    }


    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(onComplete)
    }

}