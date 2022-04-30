package com.omnicoder.instaace.util

import android.content.Context
import android.util.Log
import com.omnicoder.instaace.database.PostDao
import com.omnicoder.instaace.network.InstagramAPI
import java.lang.Exception
import javax.inject.Inject


class ProfileDownloader @Inject constructor(private val context: Context, private val instagramAPI: InstagramAPI, private val postDao:PostDao) {

    suspend fun getDP(username: String,cookies: String): String{
        val link= Constants.DP.format(username)
        return try {
            val graphQL=instagramAPI.getDP(link, cookies).graphQL
            Log.d("tagg","lnk: $link")
            Log.d("tagg","everyting: $graphQL")
            graphQL.user.profile_pic_url_hd
        }catch (e: Exception){
            "https://img.i-scmp.com/cdn-cgi/image/fit=contain,width=425,format=auto/sites/default/files/styles/768x768/public/d8/images/methode/2019/03/27/dffa4156-4f80-11e9-8617-6babbcfb60eb_image_hires_141554.JPG?itok=FNC2TjNJ&v=1553667358"
        }

    }

}

