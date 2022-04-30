package com.omnicoder.instaace.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omnicoder.instaace.database.DPRecent
import com.omnicoder.instaace.database.Post
import com.omnicoder.instaace.model.User
import com.omnicoder.instaace.model.SearchUser
import com.omnicoder.instaace.repository.InstagramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class DPViewerViewModel @Inject constructor(private val instagramRepository: InstagramRepository) : ViewModel() {
    var searchResult= MutableLiveData<List<SearchUser>>()
    var recents= MutableLiveData<List<DPRecent>>()
    var profilePicUrl= MutableLiveData<String>()



    fun searchUser(query:String,cookie: String){
        viewModelScope.launch {
            searchResult.postValue(instagramRepository.searchUsers(query,cookie))
        }
    }

    fun insertRecent(user: User){
        viewModelScope.launch {
            instagramRepository.insertDPRecent(user)
        }
    }

    fun getRecentSearches(){
        viewModelScope.launch {
            recents.postValue(instagramRepository.getRecentDPSearch())
        }
    }

    fun insertDP(post: Post){
        viewModelScope.launch {
            instagramRepository.insertPost(post)
        }
    }

    fun getDP(userId: Long, cookies: String){
        viewModelScope.launch {
            val profilePic=instagramRepository.getDP(userId,cookies)
            profilePicUrl.postValue(profilePic)
        }
    }




}