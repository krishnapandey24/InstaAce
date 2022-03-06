package com.omnicoder.instaace.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omnicoder.instaace.repository.InstagramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val instagramRepository: InstagramRepository) : ViewModel() {
    val allPosts= instagramRepository.getAllPost


    fun downloadPost(url: String, map: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                    instagramRepository.fetchPost(url, map, this)
            }.let {
                    withContext(Dispatchers.IO){
                        for(post in it){
                            this.launch { instagramRepository.addPost(post) }
                            this.launch { instagramRepository.download(post.downloadLink,post.username,post.extension,post.file_url) }
                            this.launch { instagramRepository.download(post.downloadLink,post.username,post.extension,post.in_app_url) }
                        }
                    }
                }
            }
        }



    fun getPosts() {
        viewModelScope.launch {
            instagramRepository.getAllPost.value
        }
    }


}