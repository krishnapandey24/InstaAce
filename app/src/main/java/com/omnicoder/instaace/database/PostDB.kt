package com.omnicoder.instaace.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Post::class,Carousel::class], version = 11, exportSchema = false)
abstract class PostDB : RoomDatabase() {
    abstract fun postDao(): PostDao
}