package com.omnicoder.instaace.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.omnicoder.instaace.adapters.DownloadViewAdapter2
import com.omnicoder.instaace.database.Post
import com.omnicoder.instaace.databinding.DownloadsFragmentBinding
import com.omnicoder.instaace.viewmodels.DownloadsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DownloadsFragment : Fragment() {
    private lateinit var binding: DownloadsFragmentBinding
    private lateinit var viewModel: DownloadsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding= DownloadsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[DownloadsViewModel::class.java]
//        setOnClickListeners()

    }

    private fun setOnClickListeners() {
//        TODO("Not yet implemented")
    }

    private fun setRecyclerView(posts: List<Post>, context: Context?) {
        val recyclerView: RecyclerView = binding.downloadView
        val adapter = DownloadViewAdapter2(context,posts)
        val layoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        layoutManager.stackFromEnd=true
        recyclerView.layoutManager=layoutManager
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.allPosts.observe(viewLifecycleOwner){
            setRecyclerView(it,context)
        }
    }






}