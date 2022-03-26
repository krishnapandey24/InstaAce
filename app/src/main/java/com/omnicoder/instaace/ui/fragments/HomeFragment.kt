package com.omnicoder.instaace.ui.fragments

import android.app.Activity
import android.app.DownloadManager
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.os.Bundle
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
import com.omnicoder.instaace.ui.activities.FirstStartActivity
import com.omnicoder.instaace.ui.activities.InstagramLoginActivity
import com.omnicoder.instaace.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.content.*
import android.util.Log


@AndroidEntryPoint
open class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: HomeFragmentBinding
    private lateinit var cookies: String
    private var downloadID:Long =0
    private lateinit var onComplete:BroadcastReceiver

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
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
            if(cookies!="lol") {
                checkClipboard()
            }
        }
        onComplete= object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadID == id) {
                    Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility= View.GONE
                }
            }
        }

        activity?.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

    }

    private fun setOnClickListeners(){
        binding.faqButton.setOnClickListener{
            startActivity(Intent(context,InstagramLoginActivity::class.java))
        }
        binding.downloadButton.setOnClickListener{
            val postLink=binding.editText.text.toString()
            if(isInstagramLink(postLink)){
                download(postLink)
                hideKeyboard()
            }else{
                binding.editText.text.clear()
                Toast.makeText(context, "Invalid Link!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.instagramButton.setOnClickListener{
            val intent= Intent(Intent.ACTION_VIEW)
            intent.setPackage("com.instagram.android")
            startActivity(intent)
        }

    }

    private fun download(link:String){
        if(cookies=="lol"){
            startActivity(Intent(context,FirstStartActivity::class.java))
            activity?.finish()
        }
        binding.progressBar.visibility= View.VISIBLE
        viewModel.downloadPost(link, cookies)
        binding.editText.text.clear()
    }

    private fun checkClipboard() {
        val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if(clipboard.primaryClipDescription?.hasMimeType(MIMETYPE_TEXT_PLAIN)==true && clipboard.hasPrimaryClip()){
            val item = clipboard.primaryClip?.getItemAt(0)
            val link=item?.text.toString()
            if(isInstagramLink(link)){
                binding.editText.setText(link)
                download(link)
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


        viewModel.postExits.observe(this){
            Log.d("tagg","Download id changed $it")
            if (it){
                binding.editText.text.clear()
                viewModel.postExits.value=false
                binding.progressBar.visibility= View.GONE
            }
        }

        viewModel.downloadID.observe(this){
            Log.d("tagg","Download jjjjjid changed $it")
            if(it>0){
                downloadID=it
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


    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(onComplete)
    }
}