package com.omnicoder.instaace.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import javax.inject.Inject

class Downloader @Inject constructor(private val context: Context){
    private val path:String= "/Insta Video Downloader/instagram videos/"



    fun download(downloadPath: String,fileName:String){
        val uri: Uri = Uri.parse(downloadPath)
        val request: DownloadManager.Request= DownloadManager.Request(uri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setTitle(fileName)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            path + System.currentTimeMillis().toString() + ".mp4"
        )
        (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)


    }
}