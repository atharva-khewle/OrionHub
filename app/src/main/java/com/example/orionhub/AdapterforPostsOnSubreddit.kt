package com.example.orionhub

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

class AdapterforPostsOnSubreddit (private  val posts : List<PostShownModel>) : RecyclerView.Adapter<AdapterforPostsOnSubreddit.MyPostsHolder>(){



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterforPostsOnSubreddit.MyPostsHolder {
        val itemview = LayoutInflater.from(parent.context).inflate(R.layout.item_post,parent,false)
        return MyPostsHolder(itemview)
    }

    override fun onBindViewHolder(holder: AdapterforPostsOnSubreddit.MyPostsHolder, position: Int) {
        val currentItem = posts[position]

        ///////button selection
        var selectedButtonId = -1 // Track selected button
        holder.downvotebtn.setOnClickListener {
            if (it.isSelected) {
                // Deselect if already selected
                it.isSelected = false
                selectedButtonId = -1
            } else {
                holder.upvotebtn.isSelected = false
                it.isSelected = true
                selectedButtonId = R.id.downvote_btn // Assuming ID of downvote button
            }
        }

        holder.upvotebtn.setOnClickListener {
            if (it.isSelected) {
                // Deselect if already selected
                it.isSelected = false
                selectedButtonId = -1
            } else {
                holder.downvotebtn.isSelected = false
                it.isSelected = true
                selectedButtonId = R.id.upvote_btn // Assuming ID of upvote button
            }
        }




        /////other tasks

//        holder.subreddit.setText(currentItem.subredditName)
        holder.subreddit.visibility = View.GONE
        holder.title.setText(currentItem.title)
        holder.title.setPadding(30,35,30,2)
        holder.voteno.setText(currentItem.votes.toString())
        holder.commentno.setText(currentItem.comments.toString())

        when (currentItem.contentType) {
            "text" -> {

                fun truncateText(text: String, wordLimit: Int): String {
                    val words = text.split("\\s+".toRegex())

                    return if (words.size > wordLimit) {
                        words.take(wordLimit).joinToString(" ") + "..."
                    } else {
                        text
                    }
                }

                holder.textcontent.text = currentItem.contenttext
                holder.textcontent.text = truncateText(currentItem.contenttext, 30)
                holder.textcontent.visibility = View.VISIBLE
                holder.imgcontent.visibility = View.GONE
//                holder.vidcontent.visibility = View.GONE
//                holder.vidContainer.visibility = View.GONE
            }
            "image" -> {
                // Use a library like Glide or Picasso to load images
                Glide.with(holder.itemView.context).load(currentItem.medialink).into(holder.imgcontent)
                holder.imgcontent.visibility = View.VISIBLE
                holder.textcontent.visibility = View.GONE
//                holder.vidcontent.visibility = View.GONE
//                holder.vidContainer.visibility = View.GONE
            }
//            "video" -> {
//                val mediaController = MediaController(holder.itemView.context)
////                mediaController.setAnchorView(holder.vidcontent)
//                holder.vidcontent.setMediaController(mediaController)
////                holder.vidcontent.setVideoPath(currentItem.medialink)
//                holder.vidcontent.setVideoURI(Uri.parse(currentItem.medialink))
//                holder.vidcontent.requestFocus()
//                holder.vidContainer.visibility = View.VISIBLE
//                holder.vidcontent.visibility = View.VISIBLE
//                holder.textcontent.visibility = View.GONE
//                holder.imgcontent.visibility = View.GONE
//            }
            "video" -> {
                // Create an ExoPlayer instance
                val player = SimpleExoPlayer.Builder(holder.itemView.context).build()

                // Attach player to the PlayerView
                holder.playerView.player = player

                // Build the media item
                val mediaItem = MediaItem.fromUri(Uri.parse(currentItem.medialink))

                // Add the media item to the player and prepare it
                player.setMediaItem(mediaItem)
                player.prepare()

                // Control visibility of views
                holder.playerView.visibility = View.VISIBLE
                holder.textcontent.visibility = View.GONE
                holder.imgcontent.visibility = View.GONE

                // Optional: Start playing automatically
                player.playWhenReady = true

                // Clean up: Release the player in the ViewHolder to prevent memory leaks
//                holder.itemView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
//
//                    override fun onViewAttachedToWindow(p0: View) {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun onViewDetachedFromWindow(p0: View) {
//                        TODO("Not yet implemented")
//                        player.release()
//                    }
//                })
            }

        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }



    class MyPostsHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val textcontent : TextView = itemView.findViewById(R.id.post_text_content)
        val imgcontent : ImageView = itemView.findViewById(R.id.post_img_content)
        //        val vidcontent : VideoView = itemView.findViewById(R.id.post_video_content)
        val subreddit : TextView =  itemView.findViewById(R.id.post_subreddit_name)
        val title : TextView =  itemView.findViewById(R.id.post_title)
        val commentno : TextView =  itemView.findViewById(R.id.post_comment_no)
        val voteno : TextView =  itemView.findViewById(R.id.post_vote_no)
        //        val vidContainer : LinearLayout = itemView.findViewById(R.id.vid_Container)
        val playerView: PlayerView = itemView.findViewById(R.id.player_view) // Replace with your actual PlayerView ID
        val downvotebtn: ImageButton = itemView.findViewById(R.id.downvote_btn)
        val upvotebtn: ImageButton = itemView.findViewById(R.id.upvote_btn)

    }
}
