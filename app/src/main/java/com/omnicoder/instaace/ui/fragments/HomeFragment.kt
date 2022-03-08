package com.omnicoder.instaace.ui.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.omnicoder.instaace.DownloadViewAdapter
import com.omnicoder.instaace.database.Post
import com.omnicoder.instaace.databinding.HomeFragmentBinding
import com.omnicoder.instaace.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint


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
        observeData(context)
        val sharedPreferences = activity?.getSharedPreferences("Cookies", 0)
        val cookies= sharedPreferences?.getString("loginCookies","lol") ?: "lol"
        binding.downloadButton.setOnClickListener{
            val postLink=binding.editText.text.toString()
            if(postLink.length<26){
                Toast.makeText(context, "Invalid link", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Downloading....", Toast.LENGTH_SHORT).show()
                viewModel.downloadPost(postLink, cookies)
                binding.progressBar.visibility=View.VISIBLE
                binding.editText.text.clear()
                viewModel.downloadDone.value=false
                hideKeyboard()
            }
        }

    }

    private fun observeData(context: Context?) {
        viewModel.allPosts.observe(this){
            setRecyclerView(it,context)
        }
        viewModel.fileCount.observe(this){
            binding.fileCount.text=it.toString()
            if(it==0){
                binding.noDownloadsTextView.visibility=View.VISIBLE
            }
        }
        viewModel.downloadDone.observe(this){
            if(it==true){
                binding.progressBar.visibility=View.GONE
                viewModel.downloadDone.value=false
            }

        }
    }

//    private fun isInstagramLink(link:String):Boolean {
//        if (link.length < 26 &&) {
//        }
//    }

    private fun setRecyclerView(posts: List<Post>,context: Context?) {
        val recyclerView: RecyclerView = binding.downloadView
        val adapter = DownloadViewAdapter(context,posts)
        val layoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        layoutManager.stackFromEnd=true
        recyclerView.layoutManager=layoutManager
        recyclerView.adapter = adapter
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }



}