package com.omnicoder.instaace.model

data class Story(val code: String,val mediaType: Int, val imageUrl: String, val videoUrl: String?,val username: String,val profilePicUrl: String, var isSelected:Boolean=false, var downloaded: Boolean=false, var name: String?)
