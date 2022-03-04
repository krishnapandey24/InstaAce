package com.omnicoder.instaace.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omnicoder.instaace.database.Post
import com.omnicoder.instaace.repository.InstagramRepository
import com.omnicoder.instaace.util.InvalidLinkException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val instagramRepository: InstagramRepository): ViewModel() {
    var posts= MutableLiveData<List<Post>>()

    fun downloadPost(url: String, map: String){
        viewModelScope.launch{
                withContext(Dispatchers.IO){
//                    try{
                        instagramRepository.downloadPost(url,map)
//                    }catch (exception: Exception){
//                        throw InvalidLinkException(exception.message.toString())
//                    }
            }.let {
                addNewPost(it)
                }
        }
    }

    private fun addNewPost(Posts: List<Post>){
        for(post in Posts){
            viewModelScope.launch {
                instagramRepository.addPost(post)
            }
        }

    }

    fun getDownloadedPosts(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                instagramRepository.getAllPosts()
            }.let {
                if(it.value != null) {
                    posts.value = it.value
                }
            }
        }
    }



}