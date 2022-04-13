package com.omnicoder.instaace.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.omnicoder.instaace.R
import com.omnicoder.instaace.model.Story
import com.omnicoder.instaace.ui.activities.ViewStoryActivity
import com.squareup.picasso.Picasso


class StoryViewAdapter(private val context:Context?, private val dataHolder: List<Story>) : RecyclerView.Adapter<StoryViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.story_item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story= dataHolder[position]
        val picasso= Picasso.get()
        val mediaType=story.mediaType
        holder.mediaTypeIconView.visibility=if(mediaType==2) View.VISIBLE else View.GONE
        picasso.load(story.imageUrl).into(holder.imageView)
        holder.imageView.setOnClickListener {
            val viewIntent = Intent(context, ViewStoryActivity::class.java)
            val storyDetail= Bundle()
            storyDetail.putInt("media_type",mediaType)
            storyDetail.putString("username",story.username)
            storyDetail.putString("profilePicture",story.profilePicUrl)
            storyDetail.putString("imageUrl",story.imageUrl)
            storyDetail.putString("videoUrl",story.videoUrl)
            viewIntent.putExtras(storyDetail)
            context?.startActivity(viewIntent)
        }
    }

    override fun getItemCount(): Int {
        return dataHolder.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val mediaTypeIconView: ImageView= itemView.findViewById(R.id.mediaTypeIconView)
    }

}