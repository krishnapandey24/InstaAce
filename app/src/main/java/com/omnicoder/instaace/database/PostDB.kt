package com.omnicoder.instaace.database

import androidx.room.Database
import com.omnicoder.instaace.database.Post
import androidx.room.RoomDatabase
import com.omnicoder.instaace.database.PostDao

@Database(entities = [Post::class], version = 4, exportSchema = false)
abstract class PostDB : RoomDatabase() {
    abstract fun postDao(): PostDao
}