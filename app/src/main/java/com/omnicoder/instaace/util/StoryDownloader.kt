package com.omnicoder.instaace.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.omnicoder.instaace.database.PostDao
import com.omnicoder.instaace.model.Story
import com.omnicoder.instaace.network.InstagramAPI
import javax.inject.Inject


class StoryDownloader @Inject constructor(private val context: Context, private val instagramAPI: InstagramAPI, private val postDao:PostDao) {

     suspend fun fetchFromUrl(storyUrl:String, cookie:String): MutableList<Story>{
         val stories= mutableListOf<Story>()
         try {
             val url = storyUrl.split("?")[0] + Constants.A1
             val user= instagramAPI.getUserId(url).user
             val link = Constants.STORY_DOWNLOAD.format(user.id)
             val items = instagramAPI.getStories(link, cookie, Constants.USER_AGENT).reel.items
             for(item in items){
                 stories.add(Story(item.media_type,item.image_versions2.candidates[0].url, item.video_versions?.get(0)?.url,user.username,user.profile_pic_url))
             }
         }catch (e:Exception){
             e.printStackTrace()
         }
         return stories
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


}

