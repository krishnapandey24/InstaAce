package com.omnicoder.instaace.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.omnicoder.instaace.database.Post
import com.omnicoder.instaace.database.PostDao
import com.omnicoder.instaace.model.Items
import com.omnicoder.instaace.network.InstagramAPI
import com.omnicoder.instaace.util.PostDownloader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class InstagramRepository @Inject constructor(private val instagramAPI: InstagramAPI,private val postDao: PostDao,private val postDownloader: PostDownloader){

//    suspend fun getData(id:String): InstagramResponse{
//        return instagramAPI.getData(id,"jjghj")
//    }

    suspend fun fetchPost(url: String, map: String, coroutineScope: CoroutineScope): List<Post>{
        return postDownloader.fetchDownloadLink(url,map,coroutineScope)
    }

    fun download(downloadLink: String?, username:String?, extension: String?, path: String?){
        postDownloader.download(downloadLink,username,extension,path)
    }

    val getAllPost: List<Post> = postDao.getAllPosts()


    fun addPost(post: Post){
        return postDao.insert(post)
    }

//    suspend fun DownloadPost(url:String){
//        postDownloader.
//    }

//    suspend fun download(url:String){
//        val url2="https://scontent-bom1-2.cdninstagram.com/v/t50.2886-16/272077774_1046570922554985_4726103676935762530_n.mp4?_nc_ht=scontent-bom1-2.cdninstagram.com&_nc_cat=109&_nc_ohc=eZOngSvcf18AX8XOe-5&edm=AABBvjUBAAAA&ccb=7-4&oe=61F108E4&oh=00_AT_vSrwyhFLSIwzkJb5t8NMdXh8hez_P4k-I2IoiwcTMbg&_nc_sid=83d603"
//        downloader.download(url2,"lol")
//    }

}