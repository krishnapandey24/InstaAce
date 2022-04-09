package com.omnicoder.instaace.ui.fragments

import android.content.ContentUris
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.omnicoder.instaace.R
import com.omnicoder.instaace.util.sdk29AndUp
import com.omnicoder.instaace.viewmodels.DownloadsViewModel

class DownloadsFragment : Fragment() {

    private lateinit var viewModel: DownloadsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.downloads_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[DownloadsViewModel::class.java]
        val collection = sdk29AndUp {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } ?: MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Video.Media._ID)
        val selection = "${MediaStore.Video.Media.BUCKET_DISPLAY_NAME} == ?"
        val selectionArgs = arrayOf("Instagram Videos")
        activity?.contentResolver?.query(
            collection,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            var count=0
            Log.d("tagg","Starting count")
            while(cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                Log.d("tagg","$id $contentUri $count")
                count+=1
            }
        }
    }


}