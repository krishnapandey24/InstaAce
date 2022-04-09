package com.omnicoder.instaace.database

import androidx.lifecycle.LiveData
import androidx.room.*

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

    @Query("SELECT EXISTS(SELECT media_type FROM carousel_table WHERE link= :url)")
    fun doesPostExits2(url:String): Boolean

    @Query("SELECT * FROM carousel_table WHERE link=:url")
    fun getCarousel(url:String): LiveData<List<Carousel>>

    @Query("SELECT * FROM carousel_table WHERE link=:url")
    fun getCarousel2(url:String): List<Carousel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCarousel(carousel: Carousel)

    @Query("DELETE FROM post_table WHERE link= :url")
    fun deletePost(url: String)

    @Query("DELETE FROM carousel_table WHERE link= :url")
    fun deleteCarousel(url: String)




}