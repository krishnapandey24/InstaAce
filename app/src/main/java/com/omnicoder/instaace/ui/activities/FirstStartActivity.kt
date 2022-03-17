package com.omnicoder.instaace.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.omnicoder.instaace.R

class FirstStartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_start)
        val button: Button =findViewById(R.id.button)
        button.setOnClickListener{
            startActivity(Intent(this,InstagramLoginActivity::class.java))
        }
    }
}