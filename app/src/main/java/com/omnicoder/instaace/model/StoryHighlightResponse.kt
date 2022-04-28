package com.omnicoder.instaace.model

data class StoryHighlightResponse(val tray: List<StoryHighlight>)

data class StoryHighlight(val id: String, val cover_media: CoverMedia, val title: String, val media_count: Int)

data class CoverMedia(val cropped_image_version: Candidate)