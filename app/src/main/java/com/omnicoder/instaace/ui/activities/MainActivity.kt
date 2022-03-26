package com.omnicoder.instaace.ui.activities

import android.Manifest
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.omnicoder.instaace.R
import com.omnicoder.instaace.databinding.ActivityMainBinding
import com.omnicoder.instaace.ui.fragments.HomeFragment
import com.omnicoder.instaace.util.PostDownloader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),HomeFragment.DownloadIdInterface {
    private lateinit var binding: ActivityMainBinding
    private var readPermission= false
    private var writePermission= false
    private lateinit var permissionsLauncher:ActivityResultLauncher<Array<String>>
    var downloadId: Long=0
    private lateinit var onComplete:BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("tagg","on create")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val navController = findNavController(this, R.id.fragmentContainerView)
        val bottomNavigationView: BottomNavigationView = binding.activityMainBottomNavigationView
        setupWithNavController(bottomNavigationView, navController)
        onSharedIntent()
        onComplete= object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadId == id) {
                    Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility= View.GONE
                }
            }
        }
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        binding.progressBar.visibility=View.GONE
        Log.d("tagg","We registered the recicivner")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private fun updateOrRequestPermissions(){
        val hasReadPermission= ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val minSdk29= Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        readPermission= hasReadPermission
        writePermission= hasWritePermission || minSdk29
        val permissionToRequest= mutableListOf<String>()
        if(!writePermission){
            permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if(permissionToRequest.isNotEmpty()){
            permissionsLauncher.launch(permissionToRequest.toTypedArray())
        }

    }

    private fun onSharedIntent(){
        val intent:Intent= intent
        if(intent.action.equals(Intent.ACTION_SEND)){
            val receivedLink= intent.getStringExtra(Intent.EXTRA_TEXT)
            if(receivedLink!=null){
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData:ClipData= ClipData.newPlainText("link",receivedLink)
                clipboard.setPrimaryClip(clipData)
            }
        }
    }

    override fun setId(id: Long) {
        downloadId=id
    }

    override fun startProgressBar() {
        binding.progressBar.visibility=View.VISIBLE
    }

    override fun stopProgressBar(){
        binding.progressBar.visibility= View.GONE
    }

    override fun onResume() {
        super.onResume()
        Log.d("tagg","resume")
    }

    override fun onStop() {
        super.onStop()
        Log.d("tagg","stop")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("tagg","destroy")
        unregisterReceiver(onComplete)

    }




}


