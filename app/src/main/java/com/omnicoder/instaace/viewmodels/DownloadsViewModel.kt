package com.omnicoder.instaace.viewmodels

import androidx.lifecycle.ViewModel
import com.omnicoder.instaace.repository.InstagramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel @Inject constructor(private val instagramRepository: InstagramRepository) : ViewModel() {
    val allPosts = instagramRepository.getAllPost

}