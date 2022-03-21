package com.omnicoder.instaace.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(post: Post)

    @Delete
    fun delete(post: Post)

    @Query("SELECT * FROM post_table ")
    fun getAllPosts(): LiveData<List<Post>>

    @Query("SELECT COUNT(postID) FROM post_table ")
    fun getFileCount(): LiveData<Int>

    @Query("SELECT EXISTS(SELECT media_type FROM post_table WHERE link= :url)")
    fun doesPostExits(url:String): Boolean

    @Query("SELECT * FROM carousel_table WHERE postID= :postID")
    fun getCarousel(postID:String): LiveData<List<Carousel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCarousel(carousel: Carousel)

}