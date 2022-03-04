package com.omnicoder.instaace.database

import androidx.room.PrimaryKey
import androidx.room.Entity

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
    val path: String
)