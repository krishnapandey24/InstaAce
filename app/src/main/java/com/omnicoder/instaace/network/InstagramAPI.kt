package com.omnicoder.instaace.network

import com.omnicoder.instaace.model.*
import retrofit2.http.*


interface InstagramAPI {
    @GET("{type}/{url}?__a=1")
    suspend fun getData(@Path("type") type: String,@Path("url") url:String, @Header("Cookie") map :String): InstagramResponse

    @GET("{type}/{url}?__a=1")
    suspend fun getDataWithoutLogin(@Path("type") type: String,@Path("url") url:String): Response

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