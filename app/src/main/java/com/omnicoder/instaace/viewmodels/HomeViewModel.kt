package com.omnicoder.instaace.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omnicoder.instaace.database.Carousel
import com.omnicoder.instaace.repository.InstagramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val instagramRepository: InstagramRepository) : ViewModel() {
    val allPosts = instagramRepository.getRecentDownload
    val postExits= MutableLiveData<Boolean>()
    var downloadID= MutableLiveData<MutableList<Long>>()
    var downloadId= MutableLiveData<Long>()

    private suspend fun doesPostExits(url: String):Boolean= withContext(Dispatchers.IO){
        instagramRepository.doesPostExits(url)
    }

    fun downloadPost(url: String, map: String){
        viewModelScope.launch(Dispatchers.IO) {
            var downloadIdList= mutableListOf<Long>()
            val postDoesNotExits= withContext(Dispatchers.IO){!doesPostExits(url)}
            if(postDoesNotExits){
                downloadIdList= instagramRepository.fetchPost(url,map)
            }else{
                postExits.postValue(true)
            }
            downloadID.postValue(downloadIdList)
        }
    }

    fun downloadPost2(url: String, path:String,title: String){
        Log.d("tagg","going to download")
        viewModelScope.launch(Dispatchers.IO) {
            downloadId.postValue(instagramRepository.directDownload(url,path,title))
        }
    }

    fun getCarousel(giveMeTheLink: String): LiveData<List<Carousel>> {
        return instagramRepository.getCarousel(giveMeTheLink)
    }


    fun deletePost(url:String){
        viewModelScope.launch (Dispatchers.IO){
            instagramRepository.deletePost(url)
        }
    }

    fun deleteCarousel(url:String){
        viewModelScope.launch {
            instagramRepository.deleteCarousel(url)
        }
    }

}