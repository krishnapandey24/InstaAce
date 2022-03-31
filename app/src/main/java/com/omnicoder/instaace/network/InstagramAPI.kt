package com.omnicoder.instaace.network

import com.omnicoder.instaace.model.InstagramResponse
import com.omnicoder.instaace.model.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path


interface InstagramAPI {
    @GET("{type}/{url}?__a=1")
    suspend fun getData(@Path("type") type: String,@Path("url") url:String, @Header("Cookie")map :String): InstagramResponse

    @GET("{type}/{url}?__a=1")
    suspend fun getDataWithoutLogin(@Path("type") type: String,@Path("url") url:String): Response

}