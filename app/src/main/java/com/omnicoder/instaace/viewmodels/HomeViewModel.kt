package com.omnicoder.instaace.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
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
    val allPosts = instagramRepository.getAllPost
    val fileCount = instagramRepository.getFileCount
    val postExits= MutableLiveData<Boolean>()
    var downloadID= MutableLiveData<Long>()


    private suspend fun doesPostExits(url: String):Boolean= withContext(Dispatchers.IO){
        instagramRepository.doesPostExits(url)
    }

    fun downloadPost(url: String, map: String){
        Log.d("tagg","this got called")
        viewModelScope.launch(Dispatchers.IO) {
            var downloadId: Long=990;
            val postDoesNotExits= withContext(Dispatchers.IO){!doesPostExits(url)}
            if(postDoesNotExits){
                val downloadID:Long =  instagramRepository.fetchPost(url,map)
                downloadId=downloadID
            }
            downloadID.postValue(downloadId)
        }
    }











}