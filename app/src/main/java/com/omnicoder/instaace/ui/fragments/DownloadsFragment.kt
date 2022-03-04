package com.omnicoder.instaace.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.omnicoder.instaace.R
import com.omnicoder.instaace.viewmodels.DownloadsViewModel

class DownloadsFragment : Fragment() {

    companion object {
        fun newInstance() = DownloadsFragment()
    }

    private lateinit var viewModel: DownloadsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.downloads_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DownloadsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}