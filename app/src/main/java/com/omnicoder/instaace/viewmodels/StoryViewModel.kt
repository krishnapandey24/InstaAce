package com.omnicoder.instaace.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omnicoder.instaace.model.Story
import com.omnicoder.instaace.repository.InstagramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.cert.Extension
import javax.inject.Inject


@HiltViewModel
class StoryViewModel @Inject constructor(private val instagramRepository: InstagramRepository) : ViewModel() {
    var stories= MutableLiveData<MutableList<Story>>()
    var downloadId= MutableLiveData<Long>()

    fun fetchStory(url: String, map: String) {
        viewModelScope.launch(Dispatchers.IO) {
            stories.postValue(instagramRepository.fetchStory(url, map))
        }
    }

    private suspend fun doesPostExits(url: String):Boolean= withContext(Dispatchers.IO){
        instagramRepository.doesPostExits(url)
    }

    fun downloadStory(story: Story){
        viewModelScope.launch(Dispatchers.IO) {
            val downloadIdList= instagramRepository.downloadStory(story)
            downloadId.postValue(downloadIdList)
        }
    }

    fun downloadStoryDirect(story: Story,extension: String, downloadLink:String){
        viewModelScope.launch(Dispatchers.IO) {
            val downloadIdList= instagramRepository.downloadStoryDirect(story,extension,downloadLink)
            downloadId.postValue(downloadIdList)
        }
    }

}
