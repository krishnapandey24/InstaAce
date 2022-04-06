package com.omnicoder.instaace.util

import android.content.Context
import javax.inject.Inject

class Downloader @Inject constructor(private val context: Context){

//    private fun download(downloadLink: String, item: Items, extension: String, path: String){
//        val uri: Uri = Uri.parse(downloadLink)
//        val request: DownloadManager.Request= DownloadManager.Request(uri)
//        val title=item.user.username +"_"+System.currentTimeMillis().toString() + extension
//        val filePath= Environment.DIRECTORY_DOWNLOADS.toString()+path+title
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//        request.setTitle(title)
//        request.setDestinationInExternalPublicDir(
//            Environment.DIRECTORY_DOWNLOADS,
//            path + title
//        )
//        (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
//
//    }

}