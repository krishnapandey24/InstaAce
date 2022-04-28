package com.omnicoder.instaace.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.omnicoder.instaace.adapters.StorySearchViewAdapter
import com.omnicoder.instaace.databinding.ActivitySearchStoriesBinding
import com.omnicoder.instaace.viewmodels.StoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchStoriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchStoriesBinding
    private lateinit var viewModel: StoryViewModel
    private lateinit var cookie: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel= ViewModelProvider(this)[StoryViewModel::class.java]
        binding.usersView.layoutManager=LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        cookie=intent.getStringExtra("cookie") ?: ""
        viewModel.searchResult.observe(this){
            binding.usersView.adapter = StorySearchViewAdapter(this,it,cookie)
        }
        binding.editText.doOnTextChanged { text, _, _, _ ->
            viewModel.searchUser(text.toString(),cookie)
        }
    }


}