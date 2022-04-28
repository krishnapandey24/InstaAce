package com.omnicoder.instaace.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omnicoder.instaace.model.*
import com.omnicoder.instaace.repository.InstagramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StoryViewModel @Inject constructor(private val instagramRepository: InstagramRepository) : ViewModel() {
    var stories= MutableLiveData<MutableList<Story>>()
    var reelTray= MutableLiveData<List<ReelTray>>()
    var storyHighlights= MutableLiveData<List<StoryHighlight>>()
    var reelMedia= MutableLiveData<ReelTray?>()
    var downloadId= MutableLiveData<Long>()
    var searchResult= MutableLiveData<List<SearchUser>>()

    fun fetchStory(userId: Long, map: String) {
        viewModelScope.launch(Dispatchers.IO) {
            stories.postValue(instagramRepository.fetchStory(userId, map))
        }
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

    fun fetchReelTray(cookie: String){
        viewModelScope.launch(Dispatchers.IO) {
            reelTray.postValue(instagramRepository.fetchReelTray(cookie))
        }
    }

    fun fetchReelMedia(reelId: Long,cookie: String){
        viewModelScope.launch {
            reelMedia.postValue(instagramRepository.fetchReelMedia(reelId, cookie))
        }
    }

    fun fetchStoryHighlights(userId: Long,cookie: String){
        viewModelScope.launch {
            storyHighlights.postValue(instagramRepository.fetchStoryHighlights(userId, cookie))
        }
    }

    fun searchUser(query:String,cookie: String){
        viewModelScope.launch {
            searchResult.postValue(instagramRepository.searchUsers(query,cookie))
        }
    }

}
