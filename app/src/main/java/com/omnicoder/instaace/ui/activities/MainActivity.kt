package com.omnicoder.instaace.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.omnicoder.instaace.R
import com.omnicoder.instaace.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        val navController = findNavController(this, R.id.fragmentContainerView)
        val bottomNavigationView: BottomNavigationView = binding.activityMainBottomNavigationView
        setupWithNavController(bottomNavigationView, navController)

        val downloadFolder= File(filesDir,"Download")
        if(!downloadFolder.exists()){
            downloadFolder.mkdirs()
        }
        Log.d("tagg","path: "+downloadFolder.absolutePath)
//        window.navigationBarColor = resources.getColor(R.color.navigationBarColor)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

}


