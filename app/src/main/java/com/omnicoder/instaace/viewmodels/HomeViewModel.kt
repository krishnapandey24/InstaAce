package com.omnicoder.instaace.viewmodels

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.lifecycle.*
import com.omnicoder.instaace.database.Post
import com.omnicoder.instaace.repository.InstagramRepository
import com.omnicoder.instaace.util.InvalidLinkException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val instagramRepository: InstagramRepository) : ViewModel() {
    var posts = MutableLiveData<List<Post>>()

//    val allWords: List<Post> = instagramRepository.getAllPost


    fun downloadPost(url: String, map: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    instagramRepository.fetchPost(url, map, this)
                } catch (exception: Exception) {
                    Log.d("tagg","catch1")
                    throw InvalidLinkException(exception.message.toString())
                }
            }.let {
                try {
                    Log.d("tagg","catch1hhhh")
                    addNewPost(it)
                    withContext(Dispatchers.IO){
                        for(post in it){
                            this.launch { instagramRepository.download(post.downloadLink,post.username,post.extension,post.file_url) }
                            this.launch { instagramRepository.download(post.downloadLink,post.username,post.extension,post.in_app_url) }
                        }
                    }
                } catch (exception: SQLiteConstraintException) {
                    Log.d("tagg","try1")
                    throw InvalidLinkException("Download Already Exits")
                }
            }
        }
    }

    private fun addNewPost(Posts: List<Post>) {
        for (post in Posts) {
            viewModelScope.launch {
                instagramRepository.addPost(post)
            }
        }

    }

    fun getPosts() {
        viewModelScope.launch {
            instagramRepository.getAllPost.value
        }
    }

    val allPosts= instagramRepository.getAllPost


    fun getDownloadedPosts() {
        Log.d("tagg","We got the valussse")
        viewModelScope.launch {
            Log.d("tagg","We got the valussse")
            withContext(Dispatchers.IO) {
                instagramRepository.getAllPost
            }.let {
                Log.d("tagg","We got the valussse")
                posts.value=it.value
//                if (it.value != null) {
//                    Log.d("tagg","We got the value")
//                    posts.value = it.value
//                }
            }
        }
    }


}