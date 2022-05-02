package com.omnicoder.instaace.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.omnicoder.instaace.R
import com.omnicoder.instaace.database.Post
import com.omnicoder.instaace.ui.activities.ViewPostActivity
import com.squareup.picasso.Picasso


class DownloadViewAdapter2(private val context:Context?, private val dataHolder: List<Post>) : RecyclerView.Adapter<DownloadViewAdapter2.MyViewHolder>() {
    private var size: Int=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.download_view_item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val post= dataHolder[position]
        val picasso= Picasso.get()
        val mediaType=post.media_type
        when (mediaType){
            8 -> holder.mediaTypeIconView.setImageResource(R.drawable.ic_copy)
            2 -> holder.mediaTypeIconView.setImageResource(R.drawable.ic_play)
            else -> holder.mediaTypeIconView.visibility= View.GONE
        }
        picasso.load(post.image_url).into(holder.imageView)
        picasso.load(post.profile_pic_url).into(holder.profilePicView)
        holder.layout.setOnClickListener {
            val viewIntent = Intent(context, ViewPostActivity::class.java)
            val postDetail= Bundle()
            postDetail.putString("name",post.title)
            postDetail.putInt("media_type",mediaType)
            postDetail.putString("caption",post.caption)
            postDetail.putString("username",post.username)
            postDetail.putString("profilePicture",post.profile_pic_url)
            postDetail.putString("instagram_url",post.link)
            postDetail.putString("imageUrl",post.image_url)
            postDetail.putString("videoUrl",post.video_url)
            viewIntent.putExtras(postDetail)
            context?.startActivity(viewIntent)
        }
        holder.usernameView.text = post.username
        holder.captionView.text = post.caption
    }

    override fun getItemCount(): Int {
        size= dataHolder.size
        return size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val profilePicView: ImageView= itemView.findViewById(R.id.profile_pic_view)
        val usernameView: TextView = itemView.findViewById(R.id.username_view)
        val captionView: TextView = itemView.findViewById(R.id.caption_view)
        val layout: ConstraintLayout= itemView.findViewById(R.id.constraintLayout)
        val mediaTypeIconView: ImageView= itemView.findViewById(R.id.mediaTypeIconView)
    }



}