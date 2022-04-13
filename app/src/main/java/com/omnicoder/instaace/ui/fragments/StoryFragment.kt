package com.omnicoder.instaace.ui.fragments

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.omnicoder.instaace.adapters.DownloadViewAdapter
import com.omnicoder.instaace.adapters.StoryViewAdapter
import com.omnicoder.instaace.database.Post
import com.omnicoder.instaace.databinding.StoryFragmentBinding
import com.omnicoder.instaace.model.Story
import com.omnicoder.instaace.viewmodels.StoryViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StoryFragment : Fragment() {
    private lateinit var binding: StoryFragmentBinding
    private lateinit var viewModel: StoryViewModel
    private lateinit var cookies: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
    }


    private fun observeData(context: Context?){
        viewModel.stories.observe(this){
            Log.d("tagg","list $it")
            setRecyclerView(it,context)
        }

    }

    private fun setOnClickListeners() {
        binding.downloadButton.setOnClickListener{
            viewModel.fetchStory(binding.editText.text.toString(),cookies)
        }
    }

    private fun setRecyclerView(stories: List<Story>, context: Context?) {
        val recyclerView: RecyclerView = binding.downloadView
        recyclerView.layoutManager=GridLayoutManager(context,3)
        recyclerView.adapter = StoryViewAdapter(context,stories)
    }

}