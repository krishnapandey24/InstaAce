package com.omnicoder.instaace

import android.content.Context
import android.os.Environment
import com.omnicoder.instaace.database.Post
import com.omnicoder.instaace.model.Items
import com.omnicoder.instaace.util.Constants

class TestClass (val context: Context){
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

        return Post(postID,item.media_type,item.user.username,item.user.profile_pic_url,item.image_versions2.candidates[0].url,videoUrl,caption,filePath,inAppPath,downloadLink,extension,title,null,false)
    }

    private fun downloadPost2(postID: String,item: Items,): Post {
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

        return Post(postID,item.media_type,item.user.username,item.user.profile_pic_url,item.image_versions2.candidates[0].url,videoUrl,caption,filePath,inAppPath,downloadLink,extension,title,null,false)
    }
}