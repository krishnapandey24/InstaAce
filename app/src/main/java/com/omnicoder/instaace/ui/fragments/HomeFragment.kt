package com.omnicoder.instaace.ui.fragments

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.omnicoder.instaace.DownloadViewAdapter
import com.omnicoder.instaace.database.Post
import com.omnicoder.instaace.databinding.HomeFragmentBinding
import com.omnicoder.instaace.viewmodels.HomeViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: HomeFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding= HomeFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        viewModel.allPosts.observe(this){
            setRecyclerView(it,context)
        }
        val sharedPreferences = activity?.getSharedPreferences("Cookies", 0)
        val cookies= sharedPreferences?.getString("loginCookies","lol") ?: "lol"
        binding.downloadButton.setOnClickListener{
            val postLink=binding.editText.text.toString()
            if(postLink.length<26){
                Toast.makeText(context, "Invalid link", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Downloading....", Toast.LENGTH_SHORT).show()
                try {
                    viewModel.downloadPost(postLink, cookies)
                }catch (e: Exception){
                    e.printStackTrace()
                    Toast.makeText(context,e.message.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun setRecyclerView(posts: List<Post>,context: Context?) {
        val recyclerView: RecyclerView = binding.downloadView
        val adapter = DownloadViewAdapter(posts)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

    }

}