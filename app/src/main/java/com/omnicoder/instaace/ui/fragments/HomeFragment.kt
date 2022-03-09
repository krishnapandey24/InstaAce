package com.omnicoder.instaace.ui.fragments

import android.app.Activity
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.omnicoder.instaace.DownloadViewAdapter
import com.omnicoder.instaace.database.Post
import com.omnicoder.instaace.databinding.HomeFragmentBinding
import com.omnicoder.instaace.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.content.ClipData
import android.view.Window
import androidx.core.content.ContextCompat.getSystemService


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: HomeFragmentBinding
    private lateinit var cookies: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding= HomeFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        observeData(context)
        setOnClickListeners()
        val sharedPreferences = activity?.getSharedPreferences("Cookies", 0)
        cookies= sharedPreferences?.getString("loginCookies","lol") ?: "lol"
        view.viewTreeObserver?.addOnWindowFocusChangeListener {
            checkClipboard()
        }
    }

    private fun setOnClickListeners(){
        binding.downloadButton.setOnClickListener{
            val postLink=binding.editText.text.toString()
            if(isInstagramLink(postLink)){
                if(download(postLink)){
                    binding.progressBar.visibility=View.VISIBLE
                }
                hideKeyboard()
            }else{
                binding.editText.text.clear()
                Toast.makeText(context, "Invalid Link!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun download(link:String): Boolean{
        val download=viewModel.downloadPost(link, cookies)
        viewModel.downloadDone.value=false
        return !download
    }

    private fun checkClipboard() {
        val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if(clipboard.primaryClipDescription?.hasMimeType(MIMETYPE_TEXT_PLAIN)==true){
            val item = clipboard.primaryClip?.getItemAt(0)
            val link=item?.text.toString()
            if(isInstagramLink(link)){
                if(download(link)){
                    binding.editText.setText(link)
                    binding.progressBar.visibility=View.VISIBLE
                }
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
            }else{
                binding.noDownloadsTextView.visibility=View.INVISIBLE
            }
        }
        viewModel.downloadDone.observe(this){
            if(it==true){
                binding.progressBar.visibility=View.GONE
                binding.editText.text.clear()
                viewModel.downloadDone.value=false
            }

        }
    }

    private fun isInstagramLink(link: String): Boolean {
        var isInstagramLink=false
        val containsInstagram = link.contains("instagram.com/")
        if (link.length > 26 && containsInstagram) {
            val index = link.indexOf("instagram.com")
            val totalIndex = index + 13
            val url = link.substring(totalIndex, link.length)
            isInstagramLink=url.length >= 14
        }
        return isInstagramLink
    }

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