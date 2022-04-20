package com.omnicoder.instaace.model

data class ReelTrayResponse(val tray: List<ReelTray>)

data class ReelTray(val id: String,val user: User)

