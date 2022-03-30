package com.omnicoder.instaace.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.omnicoder.instaace.R
import com.omnicoder.instaace.model.CarouselMedia


class CarouselViewPagerAdapter(private val context: Context?, private val dataHolder: List<CarouselMedia>) : RecyclerView.Adapter<CarouselViewPagerAdapter.PageHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view: View= inflater.inflate(R.layout.carousel_item_layout2,parent,false)
        return PageHolder(view)
    }

    override fun onBindViewHolder(holder: PageHolder, position: Int) {
        val media= dataHolder[position]
        val mediaType= media.mediaType
        val uri= media.uri
//        if(mediaType==1) {
//            val imageView: ImageView= holder.imageViewViewStub.inflate().findViewById(R.id.imageView)
//            imageView.setImageURI(uri)
//        }else{
//            val videoView: VideoView= holder.videoViewViewStub.inflate().findViewById(R.id.videoView)
//            videoView.setMediaController(MediaController(context))
//            videoView.setVideoURI(uri)
//            videoView.start()
//        }
        if(mediaType==1) {
            holder.videoView.visibility=View.GONE
            holder.imageView.visibility=View.VISIBLE
            holder.imageView.setImageURI(uri)
        }else{
            holder.imageView.visibility=View.GONE
            holder.videoView.visibility=View.VISIBLE
            holder.videoView.setMediaController(MediaController(context))
            holder.videoView.setVideoURI(uri)
            holder.videoView.start()
        }
    }

    override fun getItemCount(): Int {
        return dataHolder.size
    }

    class PageHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        val imageViewViewStub: ViewStub= itemView.findViewById(R.id.imageViewViewStub)
//        val videoViewViewStub: ViewStub= itemView.findViewById(R.id.videoViewViewStub)
        val imageView:ImageView= itemView.findViewById(R.id.imageView)
        val videoView:VideoView= itemView.findViewById(R.id.videoView)
    }


}