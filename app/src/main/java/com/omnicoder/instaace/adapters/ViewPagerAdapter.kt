package com.omnicoder.instaace.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.omnicoder.instaace.R


class ViewPagerAdapter(private val context: Context,private val tabs: Array<String>,private val getData: (Int) -> List<Uri>) : RecyclerView.Adapter<ViewPagerAdapter.PageHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder {
        return PageHolder(LayoutInflater.from(parent.context).inflate(R.layout.story_search_view_item_layout, parent,false))
    }

    override fun onBindViewHolder(holder: PageHolder, position: Int) {
        val uris: List<Uri> = getData(position)


//        viewModel.getAnimeList(position).observe(lifecycleOwner) { animeList ->
//            if (b) {
//                recyclerView = holder.binding.recyclerView
//                val adapter = AnimeListAdapter(
//                    context,
//                    animeList,
//                    this@ViewPagerAdapter,
//                    this@ViewPagerAdapter
//                )
//                recyclerView!!.layoutManager = LinearLayoutManager(
//                    context,
//                    LinearLayoutManager.VERTICAL,
//                    false
//                )
//                recyclerView!!.adapter = adapter
//            }
//        }
//        b = true
    }

    override fun getItemCount(): Int {
        return 6
    }

    class PageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    }


}