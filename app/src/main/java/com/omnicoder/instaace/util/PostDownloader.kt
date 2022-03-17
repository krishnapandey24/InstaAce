package com.omnicoder.instaace.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.omnicoder.instaace.database.Post
import com.omnicoder.instaace.model.Items
import com.omnicoder.instaace.network.InstagramAPI
import javax.inject.Inject


class PostDownloader @Inject constructor(private val context: Context,private val instagramAPI: InstagramAPI) {

    suspend fun fetchDownloadLink(url:String,map: String): List<Post>{
        val postID= getPostCode(url)
        val items=instagramAPI.getData("p",postID,map).items[0]
        val posts= mutableListOf<Post>()
        if(items.media_type==8){
            for(item in items.carousel_media){
                item.user=items.user
                item.caption=items.caption
                posts.add(downloadPost(postID,item))
            }
        }else{
            posts.add(downloadPost(postID,instagramAPI.getData("p",postID,map).items[0]))
        }
        return posts
    }


    private fun downloadPost(postID: String,item: Items): Post {
        var videoUrl:String?=null
        val path: String
        val extension: String
        val downloadLink= when (item.media_type) {
            1 -> {
                extension=".jpg"
                path= Constants.IMAGE_FOLDER_NAME
                item.image_versions2.candidates[0].url

            }
            2 -> {
                extension=".mp4"
                path= Constants.VIDEO_FOLDER_NAME
                videoUrl=item.video_versions[0].url
                videoUrl
            }
            else -> {
                extension=".jpg"
                path= Constants.IMAGE_FOLDER_NAME
                item.image_versions2.candidates[0].url
            }
        }
        val inAppPath=context.filesDir.absolutePath
        val title=item.user.username +"_"+System.currentTimeMillis().toString() + extension
        val filePath= Environment.DIRECTORY_DOWNLOADS+path
        val caption: String?= item.caption?.text
        return Post(postID,item.media_type,item.user.username,item.user.profile_pic_url,item.image_versions2.candidates[0].url,videoUrl,caption,filePath,inAppPath,downloadLink,extension,title,null)
    }

    fun download(downloadLink: String?,path: String?,title: String?){
        val uri: Uri = Uri.parse(downloadLink)
        val request: DownloadManager.Request= DownloadManager.Request(uri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setTitle(title)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                path + title
            )
        (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
    }


    private fun getPostCode(link: String): String {
        val index = link.indexOf("instagram.com")
        val totalIndex = index + 13
        var url = link.substring(totalIndex, link.length)
        url = when {
            url.contains("/?utm_source=ig_web_copy_link") -> url.replace("/?utm_source=ig_web_copy_link", "")
            url.contains("/?utm_source=ig_web_button_share_sheet") -> url.replace("/?utm_source=ig_web_button_share_sheet", "")
            url.contains("/?utm_medium=share_sheet") -> url.replace("/?utm_medium=share_sheet", "")
            url.contains("/?utm_medium=copy_link") -> url.replace("/?utm_medium=copy_link", "")
            else -> url
        }
        val length = url.length
        return url.substring(length - 11, length)
    }



}

