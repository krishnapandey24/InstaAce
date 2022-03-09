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


    fun downloadPost(url: String, map: String) : Boolean{
        var postExits=false
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if(!instagramRepository.doesPostExits(url)){
                    val posts: List<Post> = instagramRepository.fetchPost(url, map)
                    for (post in posts) {
                        this.launch {
                            post.link=url
                            instagramRepository.download(post.downloadLink,post.file_url,post.title)
                        }
                        this.launch { instagramRepository.addPost(post) }
                    }
                }else{
                    postExits=true
                }
            }.let {
                downloadDone.value=true
            }
        }
        return postExits
    }
}