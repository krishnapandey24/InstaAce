package com.omnicoder.instaace.repository

import androidx.lifecycle.LiveData
import com.omnicoder.instaace.database.Carousel
import com.omnicoder.instaace.database.PostDao
import com.omnicoder.instaace.model.ReelTray
import com.omnicoder.instaace.model.Story
import com.omnicoder.instaace.util.PostDownloader
import com.omnicoder.instaace.util.StoryDownloader
import java.security.cert.Extension
import javax.inject.Inject


class InstagramRepository @Inject constructor(private val postDao: PostDao,private val postDownloader: PostDownloader, private val storyDownloader: StoryDownloader){
    val getAllPost = postDao.getAllPosts()
    val getFileCount= postDao.getFileCount()


    suspend fun fetchPost(url: String, map: String?): MutableList<Long>{
        return if(map!=null){
            postDownloader.fetchDownloadLink(url,map)
        }else{
            postDownloader.fetchDownloadLink2(url)
        }
    }

    fun downloadStory(story: Story): Long{
        return storyDownloader.downloadStory(story)
    }

    fun downloadStoryDirect(story: Story,extension: String, downloadLink: String): Long{
        return storyDownloader.downloadStoryDirect(story,extension, downloadLink)
    }



    suspend fun fetchStory(url:String, map: String): MutableList<Story>{
        return storyDownloader.fetchFromUrl(url,map)
    }

    fun directDownload(link:String,path:String,title:String): Long{
        return postDownloader.download(link,path,title)
    }

    fun doesPostExits(url: String): Boolean{
        return postDao.doesPostExits(url)
    }

    fun getCarousel(giveMeTheLink: String): LiveData<List<Carousel>> {
        return postDao.getCarousel(giveMeTheLink)
    }

    fun deletePost(url:String){
        postDao.deletePost(url)
    }

    fun deleteCarousel(url:String){
        postDao.deletePost(url)
        postDao.deleteCarousel(url)
    }

    suspend fun fetchReelTray(cookie: String): List<ReelTray>{
        return storyDownloader.fetchReelTray(cookie)
    }

    suspend fun fetchReelMedia(reelId: Long,cookie: String): ReelTray?{
        return storyDownloader.getReelMedia(reelId,cookie)
    }







}