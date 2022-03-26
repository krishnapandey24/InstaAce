package com.omnicoder.instaace.util

import android.net.Uri


data class SharedStorageMedia(
    val id: Long,
    val name: String,
    val width: Int,
    val height: Int,
    val contentUri: Uri
)