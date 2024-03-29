package com.omnicoder.instaace.adapters

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
import com.omnicoder.instaace.model.StoryHighlight
import com.omnicoder.instaace.ui.activities.DownloadStoryActivity
import com.omnicoder.instaace.ui.activities.WatchStoriesActivity
import com.squareup.picasso.Picasso


class StoryHighlightViewAdapter(private val context:Context?, private val dataHolder: List<StoryHighlight>, private val cookie: String) : RecyclerView.Adapter<StoryHighlightViewAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.reel_tray_item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val tray = dataHolder[position]
        Picasso.get().load(tray.cover_media.cropped_image_version.url).into(holder.imageView)
        holder.usernameView.text = tray.title
        holder.layout.setOnClickListener {
            val intent = Intent(context, DownloadStoryActivity::class.java)
            intent.putExtra("highlightId", tray.id)
            intent.putExtra("cookie", cookie)
            intent.putExtra("showHighlights", false)
            intent.putExtra("username", tray.title)
            context?.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return dataHolder.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val layout: ConstraintLayout= itemView.findViewById(R.id.constraintLayout)
        val imageView: ImageView = itemView.findViewById(R.id.profile_pic_view)
        val usernameView: TextView= itemView.findViewById(R.id.username_view)
    }






}