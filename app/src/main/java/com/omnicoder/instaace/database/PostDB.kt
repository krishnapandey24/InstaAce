package com.omnicoder.instaace.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Post::class,Carousel::class,StoryRecent::class, DPRecent::class], version = 15, exportSchema = false)
abstract class PostDB : RoomDatabase() {
    abstract fun postDao(): PostDao
}