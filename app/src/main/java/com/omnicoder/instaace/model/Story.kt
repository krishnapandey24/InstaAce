package com.omnicoder.instaace.model

data class Story(val mediaType: Int, val imageUrl: String, val videoUrl: String?,val username: String,val profilePicUrl: String, val isSelected:Boolean=false)
