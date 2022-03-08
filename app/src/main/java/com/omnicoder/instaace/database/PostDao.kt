package com.omnicoder.instaace.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post: Post)

    @Delete
    fun delete(post: Post)

    @Query("SELECT * FROM post_table ")
    fun getAllPosts(): LiveData<List<Post>>

    @Query("SELECT COUNT(postID) FROM post_table ")
    fun getFileCount(): LiveData<Int>
}