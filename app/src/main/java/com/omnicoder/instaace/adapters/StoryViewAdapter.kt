package com.omnicoder.instaace.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.omnicoder.instaace.R
import com.omnicoder.instaace.model.Story
import com.omnicoder.instaace.ui.activities.ViewStoryActivity
import com.squareup.picasso.Picasso


class StoryViewAdapter(private val context:Context?, val dataHolder: List<Story>, private val resultLauncher: ActivityResultLauncher<Intent>,private val showDownload: (Boolean) -> Unit) : RecyclerView.Adapter<StoryViewAdapter.MyViewHolder>() {
    var isEnabled= false
    var loading=false
    var selectedStories= mutableListOf<Int>()

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
        holder.selector.setImageResource(R.drawable.story_not_selected)
        holder.selector.visibility=if(isEnabled) View.VISIBLE else View.GONE
        if(story.downloaded){
            holder.selector.visibility=View.GONE
        }
        holder.layout.setOnClickListener {
            if(isEnabled){
                if(selectedStories.contains(position)){
                    holder.selector.setImageResource(R.drawable.story_not_selected)
                    selectedStories.remove(position)
                    story.isSelected=false
                }else{
                    selectedStories.add(position)
                    holder.selector.setImageResource(R.drawable.story_selected)
                    story.isSelected=true
                }
            }else {
                val viewIntent = Intent(context, ViewStoryActivity::class.java)
                val storyDetail = Bundle()
                storyDetail.putString("code",story.code)
                storyDetail.putInt("media_type", mediaType)
                storyDetail.putString("username", story.username)
                storyDetail.putString("profilePicture", story.profilePicUrl)
                storyDetail.putString("imageUrl", story.imageUrl)
                storyDetail.putString("videoUrl", story.videoUrl)
                storyDetail.putInt("position",position)
                storyDetail.putBoolean("alreadyDownloaded",story.downloaded)
                storyDetail.putString("name",story.name)
                viewIntent.putExtras(storyDetail)
                resultLauncher.launch(viewIntent)
            }
        }
        if(loading){
            holder.loadingViewStub.inflate()
            holder.selector.visibility= View.GONE
        }else{
            holder.loadingViewStub.visibility=View.GONE
        }

        if(story.downloaded){
            holder.downloaded.visibility= View.VISIBLE
        }else{
            holder.downloaded.visibility=View.GONE
        }

        holder.layout.setOnLongClickListener {
            isEnabled=!isEnabled
            showDownload(true)
            true
        }


    }

    override fun getItemCount(): Int {
        return dataHolder.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val layout: ConstraintLayout= itemView.findViewById(R.id.constraintLayout)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val mediaTypeIconView: ImageView= itemView.findViewById(R.id.mediaTypeIconView)
        val selector: ImageView= itemView.findViewById(R.id.selector)
        val loadingViewStub: ViewStub = itemView.findViewById(R.id.loadingViewStub)
        val downloaded: ImageView= itemView.findViewById(R.id.downloaded)
    }

    fun reset(){
        isEnabled= false
        loading=false
        selectedStories.clear()
    }





}