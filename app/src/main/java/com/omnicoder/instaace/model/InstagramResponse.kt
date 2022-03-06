package com.omnicoder.instaace.model


data class InstagramResponse(val items : List<Items>)

data class Items(val media_type: Int, var user: User, val image_versions2: ImageVersion, val video_versions: List<VideoVersion>, val carousel_media: List<Items>, var caption: Caption)

data class User(val username: String, val full_name: String, val profile_pic_url: String)

data class ImageVersion(val candidates: List<Candidates>)

data class VideoVersion(val width: Int, val height: Int, val url: String)

data class Candidates(val width: Int, val height: Int, val url: String)

data class Caption(val text: String)

