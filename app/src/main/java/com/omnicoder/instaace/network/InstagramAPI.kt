package com.omnicoder.instaace.network

import com.omnicoder.instaace.model.*
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Url


interface InstagramAPI {
    @GET("{type}/{url}?__a=1")
    suspend fun getData(@Path("type") type: String,@Path("url") url:String, @Header("Cookie") map :String): InstagramResponse

    @GET("{type}/{url}?__a=1")
    suspend fun getDataWithoutLogin(@Path("type") type: String,@Path("url") url:String): Response

    @GET
    suspend fun getUserId(@Url url :String): UserGraphQL

    @GET
    suspend fun getStories(@Url url: String, @Header("Cookie") map: String, @Header("User-Agent") userAgent:String): StoryResponse

    @GET
    suspend fun getReelsTray(@Url url: String, @Header("Cookie") map: String, @Header("User-Agent") userAgent:String): ReelTrayResponse

    @GET
    suspend fun getReel(@Url url: String, @Header("Cookie") map: String, @Header("User-Agent") userAgent:String): ReelTrayResponse







}