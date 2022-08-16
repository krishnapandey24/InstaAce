package com.omnicoder.instaace.network

import com.omnicoder.instaace.model.*
import retrofit2.http.*


interface InstagramAPI {
    @GET("media/{mediaId}/info")
    suspend fun getData(@Path("mediaId") mediaId: Long, @Header("Cookie") map :String,@Header("User-Agent") userAgent:String): InstagramResponse

    @GET
    suspend fun getData(@Url url: String, @Header("Cookie") map :String,@Header("User-Agent") userAgent:String): InstagramResponse

    @GET
    suspend fun searchUsers(@Url url: String, @Header("Cookie") map: String): Users

    @GET
    suspend fun getStories(@Url url: String, @Header("Cookie") map: String, @Header("User-Agent") userAgent:String): StoryResponse

    @GET
    suspend fun getReelsTray(@Url url: String, @Header("Cookie") map: String, @Header("User-Agent") userAgent:String): ReelTrayResponse

    @GET
    suspend fun getReelMedia(@Url url: String, @Header("Cookie") map: String, @Header("User-Agent") userAgent:String): ReelMediaResponse

    @GET
    suspend fun getStoryHighlights(@Url url: String, @Header("Cookie") map: String, @Header("User-Agent") userAgent:String): StoryHighlightResponse

    @GET
    suspend fun getDP(@Url url: String, @Header("Cookie",) map: String,@Header("User-Agent") userAgent:String): UserResponse







}