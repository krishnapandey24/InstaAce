package com.omnicoder.instaace.model

data class ReelTrayResponse(val tray: List<ReelTray>)

data class ReelTray(val user: User, val items: List<StoryItem>)

//data class StoryUser(val pk: Long,val username: String, val profile_pic_url: String, val full)

data class  ReelMediaResponse(val reels: Map<String,ReelTray>)





