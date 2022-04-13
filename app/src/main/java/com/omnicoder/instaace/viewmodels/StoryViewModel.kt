package com.omnicoder.instaace.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omnicoder.instaace.model.Story
import com.omnicoder.instaace.repository.InstagramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StoryViewModel @Inject constructor(private val instagramRepository: InstagramRepository) : ViewModel() {
    var stories= MutableLiveData<MutableList<Story>>()

    fun fetchStory(url: String, map: String) {
        viewModelScope.launch(Dispatchers.IO) {
            stories.postValue(instagramRepository.fetchStory(url, map))
        }
    }
}
