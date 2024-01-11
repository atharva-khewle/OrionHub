package com.example.orionhub

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AdapterForSearchviewForUsers(
    private var myList: List<String>,
    private val onSubredditSelected: (String) -> Unit // Callback for item clicks
) : RecyclerView.Adapter<AdapterForSearchviewForUsers.ListViewHolder>() {

    fun setFilteredList(newList: List<String>) {
        myList = newList
        notifyDataSetChanged()
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subText: TextView = itemView.findViewById(R.id.searchbar_sub_text)

        init {
            itemView.setOnClickListener {
                onSubredditSelected(myList[adapterPosition]) // Invoke the callback
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_for_search, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = myList.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.subText.text = "u/"+myList[position]
    }
}
