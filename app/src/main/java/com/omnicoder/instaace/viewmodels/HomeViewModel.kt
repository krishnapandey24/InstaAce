package com.omnicoder.instaace.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omnicoder.instaace.database.Post
import com.omnicoder.instaace.repository.InstagramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val instagramRepository: InstagramRepository) : ViewModel() {
    val allPosts = instagramRepository.getAllPost
    val fileCount = instagramRepository.getFileCount
    val downloadDone= MutableLiveData<Boolean>()


    fun downloadPost(url: String, map: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val posts: List<Post> = instagramRepository.fetchPost(url, map)
                for (post in posts) {
                    this.launch { instagramRepository.download(post.downloadLink,post.username,post.extension,post.file_url,post.title) }
                    this.launch { instagramRepository.addPost(post) }
                }
            }.let {
                downloadDone.value=true
            }
        }
    }
}