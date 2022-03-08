package com.omnicoder.instaace.database

import androidx.room.PrimaryKey
import androidx.room.Entity
import androidx.room.OnConflictStrategy

@Entity(tableName = "post_table")
data class Post(
    @PrimaryKey(autoGenerate = false)
    val postID: String,
    val media_type: Int,
    val username: String,
    val profile_pic_url: String,
    val image_url: String,
    val video_url: String?,
    val caption: String,
    val file_url: String?,
    val in_app_url:String?,
    val downloadLink:String?,
    val extension:String?,
    val title:String?
)