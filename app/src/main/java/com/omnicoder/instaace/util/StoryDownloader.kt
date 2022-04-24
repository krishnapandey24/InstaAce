package com.omnicoder.instaace.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.omnicoder.instaace.database.Post
import com.omnicoder.instaace.database.PostDao
import com.omnicoder.instaace.model.ReelTray
import com.omnicoder.instaace.model.SearchUser
import com.omnicoder.instaace.model.Story
import com.omnicoder.instaace.network.InstagramAPI
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class StoryDownloader @Inject constructor(private val context: Context, private val instagramAPI: InstagramAPI, private val postDao:PostDao) {
    private val formatter= SimpleDateFormat("dd/MM/yyyy",Locale.getDefault())
     suspend fun fetchFromUrl(storyUrl:String, cookie:String): MutableList<Story>{
         val stories= mutableListOf<Story>()
         try {
             val url = storyUrl.split("?")[0] + Constants.A1
             val user= instagramAPI.getUserId(url).user
             val link = Constants.STORY_DOWNLOAD.format(user.id)
             val items = instagramAPI.getStories(link, cookie, Constants.USER_AGENT).reel.items
             for(item in items){
                 stories.add(Story(item.code,item.media_type,item.image_versions2.candidates[0].url, item.video_versions?.get(0)?.url,user.username,user.profile_pic_url, name = null))
             }
         }catch (e:Exception){
             e.printStackTrace()
         }
         return stories
     }

    fun downloadStory(story: Story): Long{
        val extension: String
        val downloadLink = if (story.mediaType == 1) {
            extension = ".jpg"
            story.imageUrl
        } else {
            extension = ".mp4"
            story.videoUrl
        }
        val username= story.username
        val code= story.code
        val currentTime= System.currentTimeMillis()
        val title="${username}_${currentTime}$extension"
        story.name=title
        val caption="${username}'s story from ${formatter.format(currentTime)}"
        postDao.insertPost(Post(code,story.mediaType,username,story.profilePicUrl,story.imageUrl,story.videoUrl,caption,null,null,null,extension,title,code,false))
        return download(downloadLink,Constants.STORY_FOLDER_NAME,title)
    }

    fun downloadStoryDirect(story: Story,extension: String,downloadLink: String): Long{
        val username= story.username
        val currentTime= System.currentTimeMillis()
        val title="${username}_${currentTime}$extension"
        val caption="${username}'s story from ${formatter.format(currentTime)}"
        postDao.insertPost(Post(story.code,story.mediaType,username,story.profilePicUrl,story.imageUrl,story.videoUrl,caption,null,null,null,extension,title,story.code,false))
        return download(downloadLink,Constants.STORY_FOLDER_NAME,title)
    }

    suspend fun fetchReelTray(cookie: String): List<ReelTray>{
        return instagramAPI.getReelsTray(Constants.REEL_TRAY, cookie, Constants.USER_AGENT).tray
    }

    fun download(downloadLink: String?, path: String?, title: String?): Long {
        val uri: Uri = Uri.parse(downloadLink)
        val request: DownloadManager.Request = DownloadManager.Request(uri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        request.setTitle(title)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            path + title
        )
        return (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
    }

    suspend fun getReelMedia(reelId: Long,cookie: String): ReelTray?{
        val link=Constants.REEL_MEDIA.format(reelId)
        return instagramAPI.getReelMedia(link,cookie,Constants.USER_AGENT).reels[reelId.toString()]
    }

    suspend fun searchUsers(query: String,cookie: String):List<SearchUser>{
        val url= Constants.SEARCH_USER.format(query)
        return instagramAPI.searchUsers(url,cookie).users
    }
}

