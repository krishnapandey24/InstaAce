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
import java.lang.Exception
import javax.inject.Inject


class PostDownloader @Inject constructor(private val context: Context,private val instagramAPI: InstagramAPI, private val postDao:PostDao) {

    suspend fun fetchDownloadLink2(url: String):Long{
        try {
            val postID = getPostCode(url)
            val media = instagramAPI.getDataWithoutLogin("p", postID).graphql.shortcode_media
            val post: Post
            var downloadId: Long = 0
            val media_type=if(media.edge_sidecar_to_children==null) Constants.CAROUSEL else (if(media.video_url==null) Constants.IMAGE else Constants.VIDEO)
            if (media_type == Constants.CAROUSEL) {
                for ((index, item) in media.edge_sidecar_to_children.edges.withIndex()) {
                    if (index == 0) {
                        item.node.owner = media.owner
                        item.node.edge_media_to_caption = media.edge_media_to_caption
                        val currentPost = downloadPost2(postID, item.node,media_type)
                        currentPost.isCarousel = true
                        downloadId =
                            download(
                                currentPost.downloadLink,
                                currentPost.file_url,
                                currentPost.title
                            )
                        currentPost.link = url
                        currentPost.media_type = 8
                        postDao.insertPost(currentPost)
                        val carousel = Carousel(
                            0,
                            currentPost.media_type,
                            currentPost.image_url,
                            currentPost.video_url,
                            currentPost.extension,
                            url,
                            currentPost.title
                        )
                        postDao.insertCarousel(carousel)
                    } else {
                        item.node.owner = media.owner
                        item.node.edge_media_to_caption = media.edge_media_to_caption
                        val currentPost = downloadPost2(postID, item.node,media_type)
                        download(currentPost.downloadLink, currentPost.file_url, currentPost.title)
                        downloadId += 1
                        val carousel = Carousel(
                            0,
                            currentPost.media_type,
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
                post = downloadPost2(postID, media,media_type)
                post.link = url
                postDao.insertPost(post)
                downloadId = download(post.downloadLink, post.file_url, post.title)

            }

            return downloadId
        }catch (e:com.squareup.moshi.JsonEncodingException){
            e.printStackTrace()
            return 3
        }
    }


    suspend fun fetchDownloadLink(url:String,map: String): Long{
        val postID = getPostCode(url)
        val items = instagramAPI.getData("p", postID, map).items[0]
        val post: Post
        var downloadId: Long = 0
        if (items.media_type == 8) {
            for ((index, item) in items.carousel_media.withIndex()) {
                if (index == 0) {
                    item.user = items.user
                    item.caption = items.caption
                    val currentPost = downloadPost(postID, item)
                    currentPost.isCarousel = true
                    downloadId = download(currentPost.downloadLink, currentPost.file_url, currentPost.title)
                    currentPost.link = url
                    currentPost.media_type = 8
                    postDao.insertPost(currentPost)
                    val carousel = Carousel(0, item.media_type, currentPost.image_url, currentPost.video_url, currentPost.extension, currentPost.link, currentPost.title)
                    postDao.insertCarousel(carousel)
                } else {
                    item.user = items.user
                    item.caption = items.caption
                    val currentPost = downloadPost(postID, item)
                    download(currentPost.downloadLink, currentPost.file_url, currentPost.title)
                    downloadId += 1
                    val carousel = Carousel(0, item.media_type, currentPost.image_url, currentPost.video_url, currentPost.extension, url, currentPost.title)
                    postDao.insertCarousel(carousel)
                }
            }
        } else {
            post = downloadPost(postID, items)
            post.link = url
            postDao.insertPost(post)
            downloadId = download(post.downloadLink, post.file_url, post.title)
        }
        return downloadId
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
        val caption: String?= item.caption?.text

        return Post(postID,item.media_type,item.user.username,item.user.profile_pic_url,item.image_versions2.candidates[0].url,videoUrl,caption,path,inAppPath,downloadLink,extension,title,null,false)
    }

    private fun downloadPost2(postID: String,item: ShortCodeMedia,media_type: String): Post {
        var videoUrl:String?=null
        val path: String
        val extension: String
        val mediaType: Int
        var imageUrl=""
        val downloadLink= when (media_type) {
            Constants.IMAGE -> {
                extension=".jpg"
                mediaType=1
                path= Constants.IMAGE_FOLDER_NAME
                imageUrl=item.display_resources.last().src
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
                item.display_resources.last().src
            }
        }
        val inAppPath=context.filesDir.absolutePath
        val title=item.owner.username +"_"+System.currentTimeMillis().toString() + extension
        val caption: String?= item.edge_media_to_caption?.edges?.node?.text

        return Post(postID,mediaType,item.owner.username,item.owner.profile_pic_url,imageUrl,videoUrl,caption,path,inAppPath,downloadLink,extension,title,null,false)
    }


    private fun download(downloadLink: String?, path: String?, title: String?): Long {
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
        url = when {
            url.contains("/?utm_source=ig_web_copy_link") -> url.replace("/?utm_source=ig_web_copy_link", "")
            url.contains("/?utm_source=ig_web_button_share_sheet") -> url.replace("/?utm_source=ig_web_button_share_sheet", "")
            url.contains("/?utm_medium=share_sheet") -> url.replace("/?utm_medium=share_sheet", "")
            url.contains("/?utm_medium=copy_link") -> url.replace("/?utm_medium=copy_link", "")
            else -> url
        }
        var length = url.length
        if(url.last()==("/".last())){
            url=url.dropLast(1)
            length -= 1
        }
        return if(length>30){
            url.substring(length- 39,length)
        }else{
            url.substring(length - 11, length)
        }

    }










}

