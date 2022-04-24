package com.omnicoder.instaace.adapters

import android.app.appsearch.SearchResult
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.omnicoder.instaace.R
import com.omnicoder.instaace.di.BaseApplication
import com.omnicoder.instaace.model.ReelTray
import com.omnicoder.instaace.model.SearchUser
import com.omnicoder.instaace.ui.activities.SearchStoriesActivity
import com.omnicoder.instaace.ui.activities.WatchStoriesActivity
import com.omnicoder.instaace.util.Constants
import com.squareup.picasso.Picasso


class StorySearchViewAdapter(private val context:Context?, private val dataHolder: List<SearchUser>) : RecyclerView.Adapter<StorySearchViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.story_search_view_item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user= dataHolder[position].user
        Picasso.get().load(user.profile_pic_url).into(holder.imageView)
        holder.usernameView.text=user.username
        holder.fullNameView.text=user.full_name
    }

    override fun getItemCount(): Int {
        return dataHolder.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val layout: ConstraintLayout= itemView.findViewById(R.id.constraintLayout)
        val imageView: ImageView = itemView.findViewById(R.id.profile_pic_view)
        val usernameView: TextView= itemView.findViewById(R.id.username_view)
        val fullNameView: TextView= itemView.findViewById(R.id.fullNameView)
    }






}