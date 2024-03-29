package com.omnicoder.instaace.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.omnicoder.instaace.database.Carousel
import com.omnicoder.instaace.database.Post
import com.omnicoder.instaace.database.PostDao
import com.omnicoder.instaace.model.Items
import com.omnicoder.instaace.model.ShortCodeMedia
import com.omnicoder.instaace.network.InstagramAPI
import javax.inject.Inject


class PostDownloader @Inject constructor(private val context: Context,private val instagramAPI: InstagramAPI, private val postDao:PostDao) {

    suspend fun fetchDownloadLink(url:String,map: String): MutableList<Long>{
        try {
            val postID = getPostCode(url)
            val items: Items = if(postID.length>23){
                instagramAPI.getData(Constants.PRIVATE_POST_URL.format(postID),map,Constants.USER_AGENT).items[0]
            }else{
                val mediaId = getMediaId(postID)
                val logd="$mediaId \n $map \n ${Constants.USER_AGENT} \n $url"
                Log.d("tagg",logd)
                instagramAPI.getData(mediaId,map,Constants.USER_AGENT).items[0]
            }
           val post: Post
           val downloadId = mutableListOf<Long>()
            if (items.media_type == 8) {
                for ((index, item) in items.carousel_media.withIndex()) {
                    if (index == 0) {
                        item.user = items.user
                        item.caption = items.caption
                        val currentPost = downloadPost(item)
                        currentPost.isCarousel = true
                        downloadId.add(
                            download(
                                currentPost.downloadLink,
                                currentPost.path,
                                currentPost.title
                            )
                        )
                        currentPost.link = url
                        currentPost.media_type = 8
                        postDao.insertPost(currentPost)
                        val carousel = Carousel(
                            0,
                            item.media_type,
                            currentPost.image_url,
                            currentPost.video_url,
                            currentPost.extension,
                            currentPost.link,
                            currentPost.title
                        )
                        postDao.insertCarousel(carousel)
                    } else {
                        item.user = items.user
                        item.caption = items.caption
                        val currentPost = downloadPost(item)
                        downloadId.add(
                            download(
                                currentPost.downloadLink,
                                currentPost.path,
                                currentPost.title
                            )
                        )
                        val carousel = Carousel(
                            0,
                            item.media_type,
                            currentPost.image_url,
                            currentPost.video_url,
                            currentPost.extension,
                            url,
                            currentPost.title
                        )
                        postDao.insertCarousel(carousel)
                    }
                }
            } else {
                post = downloadPost(items)
                post.link = url
                postDao.insertPost(post)
                downloadId.add(download(post.downloadLink, post.path, post.title))
            }
            return downloadId
        }catch (e: Exception){
            return mutableListOf()
        }
    }


    private fun downloadPost(item: Items): Post {
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
        val title=item.user.username +"_"+System.currentTimeMillis().toString() + extension
        val caption: String?= item.caption?.text

        return Post(0,item.media_type,item.user.username,item.user.profile_pic_url,item.image_versions2.candidates[0].url,videoUrl,caption,path,downloadLink,extension,title,null,false)
    }

    private fun downloadPost2(item: ShortCodeMedia,media_type: String): Post {
        var videoUrl:String?=null
        val path: String
        val extension: String
        val mediaType: Int
        var imageUrl=item.display_resources.last().src
        val downloadLink= when (media_type) {
            Constants.IMAGE -> {
                extension=".jpg"
                mediaType=1
                path= Constants.IMAGE_FOLDER_NAME
                imageUrl

            }
            Constants.VIDEO -> {
                extension=".mp4"
                mediaType=2
                path= Constants.VIDEO_FOLDER_NAME
                videoUrl=item.video_url
                videoUrl
            }
            else -> {
                extension=".jpg"
                mediaType=1
                path= Constants.IMAGE_FOLDER_NAME
                imageUrl=item.display_resources.last().src
                imageUrl
            }
        }
        val title=item.owner.username +"_"+System.currentTimeMillis() + extension
        val caption: String?= if(item.edge_media_to_caption.edges == null) null else item.edge_media_to_caption.edges?.get(0)?.node?.text
        return Post(0,mediaType,item.owner.username,item.owner.profile_pic_url,imageUrl,videoUrl,caption,path,downloadLink,extension,title,null,false)
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


    private fun getPostCode(link: String): String {
        val index = link.indexOf("instagram.com")
        val totalIndex = index + 13
        var url = link.substring(totalIndex, link.length)
        url = url.split("/?")[0]
        url= url.split("/")[2]
        return url
    }

    private fun getMediaId(shortcode: String): Long{
        var mediaId=0L
        val alphabets= Constants.SHORTCODE_CHARACTERS
        for(letter in shortcode){
            mediaId= (mediaId*64) + alphabets.indexOf(letter)
        }
        return mediaId
    }

//    private fun getPostCode2(link: String): String {
//        return link.split("/")[4]
//    }
}

