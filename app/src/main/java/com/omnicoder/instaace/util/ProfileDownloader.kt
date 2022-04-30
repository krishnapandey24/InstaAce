package com.omnicoder.instaace.util

import android.content.Context
import com.omnicoder.instaace.database.PostDao
import com.omnicoder.instaace.network.InstagramAPI
import javax.inject.Inject


class ProfileDownloader @Inject constructor(private val context: Context, private val instagramAPI: InstagramAPI, private val postDao:PostDao) {

    suspend fun getDP(userId: Long,cookies: String): String{
        val link= Constants.DP.format(userId)
        return instagramAPI.getDP(link, cookies,Constants.USER_AGENT).user.hd_profile_pic_url_info.url
    }

}

