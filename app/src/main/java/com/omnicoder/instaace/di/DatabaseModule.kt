package com.omnicoder.instaace.di

import android.app.Application
import androidx.room.Room
import com.omnicoder.instaace.database.PostDB
import com.omnicoder.instaace.database.PostDao
import com.omnicoder.instaace.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun providePostDB(application: Application): PostDB {
        return Room.databaseBuilder(
            application,
            PostDB::class.java, Constants.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }


    @Provides
    @Singleton
    fun providePostDao(postDB: PostDB): PostDao {
        return postDB.postDao()
    }

}