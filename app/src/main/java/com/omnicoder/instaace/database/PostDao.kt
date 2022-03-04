package com.omnicoder.instaace.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PostDao {
    @Insert
    fun insert(post: Post)

    @Delete
    fun delete(post: Post)

    @Query("SELECT * FROM post_table")
    fun getAllPosts(): LiveData<List<Post>>
}