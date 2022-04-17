package com.omnicoder.instaace.model

data class StoryResponse(val reel : Reel)

data class Reel(val items: List<StoryItem>)

data class StoryItem(val code: String, val media_type: Int,val image_versions2: ImageVersion, val video_versions: List<Candidates>?)