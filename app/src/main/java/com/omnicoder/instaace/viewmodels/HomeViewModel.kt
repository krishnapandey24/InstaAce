package com.omnicoder.instaace.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omnicoder.instaace.database.Post
import com.omnicoder.instaace.repository.InstagramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val instagramRepository: InstagramRepository) : ViewModel() {
    val allPosts = instagramRepository.getAllPost
    val fileCount = instagramRepository.getFileCount
    val downloadDone= MutableLiveData<Boolean>()
    val postExits= MutableLiveData<Boolean>()


    fun downloadPost(url: String, map: String) : Boolean{
        var postExits=false
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if(!instagramRepository.doesPostExits(url)){
                    val posts: List<Post> = instagramRepository.fetchPost(url, map)
                    for (post in posts) {
                        Log.d("tagg","Added Post")
                        this.launch {
                            post.link=url
                            instagramRepository.download(post.downloadLink,post.file_url,post.title)
                            Log.d("tagg","Added Post to download")

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

    fun downloadPosts(url:String, map: String){
        postExits.value=false
        viewModelScope.launch {
            val postExits1=doesPostExits(url)
            Log.d("tagg","does pst e"+!postExits1)
            postExits.value=postExits1
            if(!postExits1){
                Log.d("tagg","does inside e"+!postExits1)
                val posts=fetchDownloadLink(url,map)
                downloadDone.value=syncAll(posts,url)!!
            }
        }

    }

    private suspend fun doesPostExits(url: String):Boolean= withContext(Dispatchers.IO){
        instagramRepository.doesPostExits(url)
    }

    private suspend fun fetchDownloadLink(url: String,map: String) : List<Post> = withContext(Dispatchers.IO){
        instagramRepository.fetchPost(url,map)
    }

    private suspend fun syncAll(posts: List<Post>,url:String):Boolean{
        val job= Job()
        CoroutineScope(Dispatchers.IO + job).launch {
            for(post in posts){
                instagramRepository.download(post.downloadLink,post.file_url,post.title)
                post.link=url
//                instagramRepository.addPost(post)
            }
        }.join()
        return job.complete()
    }







}