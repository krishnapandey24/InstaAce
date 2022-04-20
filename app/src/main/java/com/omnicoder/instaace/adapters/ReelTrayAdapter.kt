package com.omnicoder.instaace.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.omnicoder.instaace.R
import com.omnicoder.instaace.model.Story
import com.omnicoder.instaace.ui.activities.ViewStoryActivity
import com.omnicoder.instaace.ui.activities.WatchStoriesActivity
import com.squareup.picasso.Picasso


class ReelTrayAdapter(private val context:Context?, private val dataHolder: List<Story>) : RecyclerView.Adapter<ReelTrayAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.reel_tray_item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story= dataHolder[position]
        Picasso.get().load(story.profilePicUrl).into(holder.imageView)
        holder.usernameView.text=story.username
        holder.layout.setOnClickListener{
            context?.startActivity(Intent(context,WatchStoriesActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return dataHolder.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val layout: ConstraintLayout= itemView.findViewById(R.id.constraintLayout)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val usernameView: TextView= itemView.findViewById(R.id.username_view)
    }






}