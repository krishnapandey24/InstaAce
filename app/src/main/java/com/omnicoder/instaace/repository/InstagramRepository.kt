package com.omnicoder.instaace.repository

import androidx.lifecycle.LiveData
import com.omnicoder.instaace.database.Carousel
import com.omnicoder.instaace.database.PostDao
import com.omnicoder.instaace.util.PostDownloader
import javax.inject.Inject


class InstagramRepository @Inject constructor(private val postDao: PostDao,private val postDownloader: PostDownloader){
    val getAllPost = postDao.getAllPosts()
    val getFileCount= postDao.getFileCount()


    suspend fun fetchPost(url: String, map: String?): Long{
        return if(map!=null){
            postDownloader.fetchDownloadLink(url,map)
        }else{
            postDownloader.fetchDownloadLink2(url)
        }
    }

    fun doesPostExits(url: String): Boolean{
        return postDao.doesPostExits(url)
    }

    fun getCarousel(giveMeTheLink: String): LiveData<List<Carousel>> {
        return postDao.getCarousel(giveMeTheLink)

    }





}