package com.omnicoder.instaace

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.omnicoder.instaace.database.Post
import com.squareup.picasso.Picasso


class DownloadViewAdapter( private val  dataHolder: List<Post>) : RecyclerView.Adapter<DownloadViewAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.download_view_item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val post= dataHolder[position]
        val picasso= Picasso.get()
        picasso.load(post.image_url).into(holder.imageView)
        picasso.load(post.profile_pic_url).into(holder.profilePicView)
        holder.usernameView.text = post.username
        holder.captionView.text= post.caption
    }

    override fun getItemCount(): Int {
        return dataHolder.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val profilePicView: ImageView= itemView.findViewById(R.id.profile_pic_view)
        val usernameView: TextView = itemView.findViewById(R.id.username_view)
        val captionView: TextView = itemView.findViewById(R.id.caption_view)
    }

}