package com.omnicoder.instaace.repository

import androidx.lifecycle.LiveData
import com.omnicoder.instaace.database.*
import com.omnicoder.instaace.model.*
import com.omnicoder.instaace.util.PostDownloader
import com.omnicoder.instaace.util.ProfileDownloader
import com.omnicoder.instaace.util.StoryDownloader
import javax.inject.Inject


class InstagramRepository @Inject constructor(private val postDao: PostDao,private val postDownloader: PostDownloader, private val storyDownloader: StoryDownloader, private val profileDownloader: ProfileDownloader){
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

    suspend fun fetchStory(userId: Long, map: String): MutableList<Story>{
        return storyDownloader.fetchFromUrl(userId,map)
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

    suspend fun fetchStoryHighlightsStories(reelId: String,cookie: String): MutableList<Story>{
        return storyDownloader.getReelMedia(reelId,cookie)
    }

    suspend fun fetchStoryHighlights(userId: Long,cookie: String): List<StoryHighlight>{
        return storyDownloader.getStoryHighlights(userId,cookie)
    }

    suspend fun searchUsers(query: String,cookie: String): List<SearchUser>{
        return storyDownloader.searchUsers(query, cookie)
    }

    fun insertRecent(user: User){
        postDao.insertRecent(StoryRecent(user.pk,user.username,user.full_name,user.profile_pic_url,null))
    }

    fun insertDPRecent(user: User){
        postDao.insertDPRecent(DPRecent(user.pk,user.username,user.full_name,user.profile_pic_url))
    }

    fun getRecentSearch(): List<StoryRecent>{
        return postDao.getRecentSearch()
    }

    fun getRecentDPSearch(): List<DPRecent>{
        return postDao.getRecentDPSearch()
    }

    fun insertPost(post: Post){
        postDao.insertPost(post)
    }

    suspend fun getDP(username: String,cookies: String): String{
        return profileDownloader.getDP(username, cookies)
    }






}