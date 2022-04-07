package com.omnicoder.instaace.viewmodels

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
    val allPosts = instagramRepository.getAllPost
    val fileCount = instagramRepository.getFileCount
    val postExits= MutableLiveData<Boolean>()
    var downloadID= MutableLiveData<MutableList<Long>>()

    private suspend fun doesPostExits(url: String):Boolean= withContext(Dispatchers.IO){
        instagramRepository.doesPostExits(url)
    }

    fun downloadPost(url: String, map: String?){
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

    fun getCarousel(giveMeTheLink: String): LiveData<List<Carousel>> {
        return instagramRepository.getCarousel(giveMeTheLink)
    }


//    fun deletePost(url:String){
//        TODO("Add this later")
//    }











}