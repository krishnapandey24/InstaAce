package com.omnicoder.instaace.di

import android.content.Context
import com.omnicoder.instaace.network.InstagramAPI
import com.omnicoder.instaace.util.Downloader
import com.omnicoder.instaace.util.PostDownloader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideInstagramAPI(): InstagramAPI{
        return Retrofit.Builder()
            .baseUrl("https://www.instagram.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InstagramAPI::class.java)

    }

    @Provides
    @Singleton
    fun provideDownloader(@ApplicationContext appContext : Context): Downloader {
        return Downloader(appContext)
    }

    @Provides
    @Singleton
    fun providePostDownloader(@ApplicationContext appContext : Context,instagramAPI: InstagramAPI): PostDownloader {
        return PostDownloader(appContext,instagramAPI,null)
    }




}