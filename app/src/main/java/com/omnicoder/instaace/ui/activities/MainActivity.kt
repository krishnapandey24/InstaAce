package com.omnicoder.instaace.ui.activities

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.omnicoder.instaace.R
import com.omnicoder.instaace.databinding.ActivityMainBinding
import com.omnicoder.instaace.ui.fragments.HomeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), HomeFragment.DownloadNavigation{
    private lateinit var binding: ActivityMainBinding
    private var readPermission= false
    private var writePermission= false
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var permissionsLauncher:ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val navController = findNavController(this, R.id.fragmentContainerView)
        bottomNavigationView = binding.activityMainBottomNavigationView
        setupWithNavController(bottomNavigationView, navController)
        onSharedIntent()
        permissionsLauncher= registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
            readPermission= permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
            readPermission= permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermission
        }
        updateOrRequestPermissions()
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
        if(!readPermission){
            permissionToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
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

    override fun navigateToDownload() {
        bottomNavigationView.selectedItemId=R.id.downloads
    }


}


