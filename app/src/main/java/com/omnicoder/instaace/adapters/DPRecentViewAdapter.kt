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
import com.omnicoder.instaace.database.DPRecent
import com.omnicoder.instaace.ui.activities.ViewDPActivity
import com.squareup.picasso.Picasso


class DPRecentViewAdapter(private val context:Context?, private val dataHolder: List<DPRecent>, private val cookies: String) : RecyclerView.Adapter<DPRecentViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.story_search_view_item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user= dataHolder[position]
        Picasso.get().load(user.profile_pic_url).into(holder.imageView)
        holder.usernameView.text=user.username
        holder.fullNameView.text=user.full_name
        holder.layout.setOnClickListener{
            val intent= Intent(context,ViewDPActivity::class.java)
            intent.putExtra("username",user.username)
            intent.putExtra("full_name",user.full_name)
            intent.putExtra("profilePicUrl",user.profile_pic_url)
            intent.putExtra("userId",user.pk)
            intent.putExtra("cookies",cookies)
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
        val fullNameView: TextView= itemView.findViewById(R.id.fullNameView)
    }






}