package com.omnicoder.instaace.ui.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.omnicoder.instaace.R

class RequestLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_login)
        val loginButton: Button= findViewById(R.id.loginButton)
        val skip: Button= findViewById(R.id.skip)
        loginButton.setOnClickListener{
            startActivity(Intent(this,InstagramLoginActivity::class.java))
            finish()
        }
        skip.setOnClickListener{
            startActivity(Intent(this,MainActivity::class.java))
            finish()
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData: ClipData = ClipData.newPlainText("link","")
            clipboard.setPrimaryClip(clipData)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

}