package com.omnicoder.instaace.repository

import android.util.Log
import com.omnicoder.instaace.database.Post
import com.omnicoder.instaace.database.PostDao
import com.omnicoder.instaace.network.InstagramAPI
import com.omnicoder.instaace.util.PostDownloader
import javax.inject.Inject


class InstagramRepository @Inject constructor(private val instagramAPI: InstagramAPI,private val postDao: PostDao,private val postDownloader: PostDownloader){



    suspend fun fetchPost(url: String, map: String): Long{
        return postDownloader.fetchDownloadLink(url,map)
    }

    fun doesPostExits(url: String): Boolean{
        return postDao.doesPostExits(url)

    }



    fun download(downloadLink: String?, path: String?, title: String?){
        Log.d("tagg","Download added")
        postDownloader.download(downloadLink,path,title)
    }

    val getAllPost = postDao.getAllPosts()
    val getFileCount= postDao.getFileCount()


    fun addPost(post: Post){
        Log.d("tagg","Save added")
        return postDao.insertPost(post)
    }



}