package com.omnicoder.instaace.ui.activities

import android.app.Dialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.omnicoder.instaace.R
import com.omnicoder.instaace.database.Post
import com.omnicoder.instaace.databinding.ActivityViewDpBinding
import com.omnicoder.instaace.util.Constants
import com.omnicoder.instaace.viewmodels.DPViewerViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ViewDPActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewDpBinding
    private lateinit var viewModel: DPViewerViewModel
    private lateinit var username:String
    private lateinit var profilePicUrl: String
    private var downloadId: Long=0
    private lateinit var loadingDialog: Dialog
    private lateinit var onComplete: BroadcastReceiver
    private var downloaded= true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityViewDpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel= ViewModelProvider(this)[DPViewerViewModel::class.java]
        username= intent.getStringExtra("username") ?: ""
        val cookies= intent.getStringExtra("cookies") ?: ""
        val cookie="""mid=YmOsoAABAAHg5A7G0UOw2JnSKLNt;  ig_did=86D708E7-0401-4F3B-A833-8D200D0B3708;  ig_nrcb=1;  shbid="9344\05440850856630\0541682235452:01f78ba0194ae84c3805cb0e908e773d689ee2c5757980e1bdeb4591470251f8195c434d";  shbts="1650699452\05440850856630\0541682235452:01f712b285310538dac38dd88604175c398d01e8b82947579e0fc102917f2cfb8d74a1e6";  csrftoken=FJfcgLAS85EHOddNx5CitbTVgoDi883R;  ds_user_id=4614712471;  sessionid=4614712471%3AGvjABTsLCzfTjK%3A19;  rur="PRN\0544614712471\0541682427906:01f77892dcd5150094d39c037e71a63db4e482e31a46ccd29c30989bfe7cded05f6d679c"""
        viewModel.getDP("mostlysane", cookie)
        viewModel.profilePicUrl.observe(this){
            profilePicUrl=it
            Picasso.get().load(profilePicUrl).into(binding.imageView,object : Callback {
                override fun onSuccess() {
                    binding.progressBar.visibility= View.GONE
                }
                override fun onError(e: Exception?) {
                    e?.printStackTrace()
                }

            })
        }

        onComplete= object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if(id==downloadId){
                    downloaded=true
                    loadingDialog.dismiss()
                    Toast.makeText(context,"Download complete",Toast.LENGTH_SHORT).show()
                }

            }
        }
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.backButton.setOnClickListener{
            finish()
        }

        binding.downloadButton.setOnClickListener{
            if(downloaded){
                Toast.makeText(this,"Already Downloaded", Toast.LENGTH_SHORT).show()
            }else{
                val loadingDialog= Dialog(this)
                loadingDialog.setContentView(R.layout.download_loading_dialog)
                loadingDialog.setCancelable(false)
                loadingDialog.show()
                download()
            }

        }

    }

    fun download(){
        val uri: Uri = Uri.parse(profilePicUrl)
        val request: DownloadManager.Request = DownloadManager.Request(uri)
        val title=username+System.currentTimeMillis()
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        request.setTitle(title)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            Constants.IMAGE_FOLDER_NAME + title
        )
        downloadId=(getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
        viewModel.insertDP(Post(0,1,username,profilePicUrl,profilePicUrl,null,"$username's DP",null,profilePicUrl,".jpg",title,null,false))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onComplete)
    }
}