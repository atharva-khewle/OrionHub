package com.example.orionhub

import AdapterForComments
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.orionhub.databinding.FragmentPostpageBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID


class postpage : Fragment() {
    private lateinit var recyclerView : RecyclerView
    private var commentslist :List<CommentModel> = listOf()
    private lateinit var adapter: AdapterForComments
    private var postId: String? = null
    private lateinit var binding: FragmentPostpageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostpageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            postId = it.getString(ARG_POST_ID)
        }
        val fireclass = Firebasefun();
        fireclass.initialiseFirebase()


        lifecycleScope.launch {


            val postdata = fireclass.getPostDataFromPostID(postId!!)

            val contentType = postdata?.child("contentType")?.getValue(String::class.java) ?: "unknown"


            binding.fullpostSubname.setText(postdata?.child("subreddit")?.getValue(String::class.java) ?: "unknown")
            binding.fullpostUsername.setText(postdata?.child("userId")?.getValue(String::class.java) ?: "unknown")
            binding.fullpostTitle.setText(postdata?.child("title")?.getValue(String::class.java) ?: "unknown")

            when (contentType) {
                "video" -> {
                    binding.fullpostImgContent.visibility=View.GONE
                    binding.fullpostTextContent.visibility=View.GONE
                    binding.fullpostPlayerView.visibility = View.VISIBLE
                    // Handle video content
                    // Create an ExoPlayer instance
                    val player = SimpleExoPlayer.Builder(requireContext()).build()

                    // Attach player to the PlayerView
                    binding.fullpostPlayerView.player = player

                    val link = postdata?.child("content")?.getValue(String::class.java) ?: ""
//                    Toast.makeText(requireContext(), link, Toast.LENGTH_SHORT).show()


                    // Build the media item
                    val mediaItem = MediaItem.fromUri(Uri.parse(
                        link
                    ))

                    // Add the media item to the player and prepare it
                    player.setMediaItem(mediaItem)
                    player.prepare()

                    // Optional: Start playing automatically
                    player.playWhenReady = false
                }
                "image" -> {
                    binding.fullpostPlayerView.visibility=View.GONE
                    binding.fullpostTextContent.visibility=View.GONE
                    // Handle image content
                    Glide.with(requireActivity())
                        .load(
                        postdata?.child("content")?.getValue(String::class.java) ?: ""
                    )
                        .into(binding.fullpostImgContent)

                }
                "text" -> {
                    binding.fullpostImgContent.visibility=View.GONE
                    binding.fullpostPlayerView.visibility=View.GONE

                    // Handle text content
                    binding.fullpostTextContent.setText(postdata?.child("content")?.getValue(String::class.java) ?: "unknown")
                }
                else -> {
                    // Handle unknown or default case
                }
            }


//////////////////////////////////////
            recyclefn()


            binding.commentOnPost.setOnClickListener{

                lifecycleScope.launch{
                    openBottomModelSheet(CommentModel("","","","",""),"Post")
                }
            }





            //button
            var selectedButtonId = -1 // Track selected button

            var currentVoteCount : Int = postdata?.child("votes")?.getValue(Int::class.java)?:0
            binding.fullpostVoteNo.setText(currentVoteCount.toString())

            // Assuming you have the postID available
            val postID = /* Your post ID here */

                binding.fullpostUpvoteBtn.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (it.isSelected) {
                            it.isSelected = false
                            selectedButtonId = -1
                            val result =fireclass.updateVotetoPostbyPostID(postId!!, -1)
                            if (result == 1) {
                                withContext(Dispatchers.Main) {
                                    currentVoteCount -= 1
                                    binding.fullpostVoteNo.text = currentVoteCount.toString()
                                }
                            }
                        } else {
                            if (binding.fullpostDownvoteBtn.isSelected) {
                                binding.fullpostDownvoteBtn.isSelected = false
                               fireclass.updateVotetoPostbyPostID(postId!!, 1) // Cancel downvote
                            }
                            it.isSelected = true
                            selectedButtonId = R.id.upvote_btn
                            val result = fireclass.updateVotetoPostbyPostID(postId!!, 1)
                            if (result == 1) {
                                withContext(Dispatchers.Main) {
                                    currentVoteCount += 1
                                    binding.fullpostVoteNo.text = currentVoteCount.toString()
                                }
                            }
                        }
                    }
                }

            binding.fullpostDownvoteBtn.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    if (it.isSelected) {
                        it.isSelected = false
                        selectedButtonId = -1
                        val result = fireclass. updateVotetoPostbyPostID(postId!!, 1)
                        if (result == 1) {
                            withContext(Dispatchers.Main) {
                                currentVoteCount += 1
                                binding.fullpostVoteNo.text = currentVoteCount.toString()
                            }
                        }
                    } else {
                        if (binding.fullpostUpvoteBtn.isSelected) {
                            binding.fullpostUpvoteBtn.isSelected = false
                            fireclass.updateVotetoPostbyPostID(postId!!, -1) // Cancel upvote
                        }
                        it.isSelected = true
                        selectedButtonId = R.id.downvote_btn
                        val result = fireclass.updateVotetoPostbyPostID(postId!!, -1)
                        if (result == 1) {
                            withContext(Dispatchers.Main) {
                                currentVoteCount -= 1
                                binding.fullpostVoteNo.text = currentVoteCount.toString()
                            }
                        }
                    }
                }
            }





        }

//        ///////button selection
//        binding.fullpostDownvoteBtn.setOnClickListener {
//            if (it.isSelected) {
//                // Deselect if already selected
//                it.isSelected = false
//                selectedButtonId = -1
//            } else {
//                binding.fullpostUpvoteBtn.isSelected = false
//                it.isSelected = true
//                selectedButtonId = R.id.downvote_btn // Assuming ID of downvote button
//            }
//        }
//
//        binding.fullpostUpvoteBtn.setOnClickListener {
//            if (it.isSelected) {
//                // Deselect if already selected
//                it.isSelected = false
//                selectedButtonId = -1
//            } else {
//                binding.fullpostDownvoteBtn.isSelected = false
//                it.isSelected = true
//                selectedButtonId = R.id.upvote_btn // Assuming ID of upvote button
//            }
//        }




        // Use the postId to fetch and display post details
        // Example: fetchPostDetails(postId)
    }

    suspend fun openBottomModelSheet(modelcomment : CommentModel,to:String){
        val view : View = layoutInflater.inflate(R.layout.comment_modal_sheet,null)

        val dialog = BottomSheetDialog(requireContext())

        val fireclass = Firebasefun();
        fireclass.initialiseFirebase()
        var replyto:String;
        if(to=="Post"){
            replyto="Post"
        }else{
            replyto=modelcomment.authorId
        }
        view.findViewById<TextView>(R.id.comment_replying_to).text = replyto

        view.findViewById<Button>(R.id.post_comment_button).setOnClickListener {
            val content =view.findViewById<TextInputEditText>(R.id.comment_text_content).text.toString()


        lifecycleScope.launch {


                if(content!=""){
                    val prefs: SharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    val myusername = prefs.getString("USERNAME_KEY", null)?:"default"
                    val uuidtext = UUID.randomUUID().toString()

                    val model : CommentModel=CommentModel(
                        uuidtext,
                        postId!!,
                        myusername,
                        content,
                        replyto
                    )

                    val a= fireclass.createComment(model)
                    val b= fireclass.addCommentIDtoPost(model.commentId,postId!!)
                    val c= fireclass.addCommentIDtoUsername(model.commentId,requireContext())

                    if(a==1 && b==1 && c==1){
                        Toast.makeText(requireContext(), "Commented!!!", Toast.LENGTH_SHORT).show()
                        recyclefn()
                        dialog.dismiss()
                    }else{
                        Toast.makeText(requireContext(), "Please try again", Toast.LENGTH_SHORT).show()
                    }



                }else{
                    Toast.makeText(requireContext(), "Enter comment", Toast.LENGTH_SHORT).show()
                }

            }

        }


        //make commentmodel ......
        //add commentid ot user, post
        //addonclicklistner on button to comment on post

        dialog.setContentView(view)
        dialog.show()
    }

    suspend fun recyclefn(){
        val fireclass = Firebasefun();
        fireclass.initialiseFirebase()

        recyclerView = binding.recyclerviewComments
        commentslist=fireclass.getCommentModelfromPostID(postId!!,requireContext())
        binding.fullpostCommentNo.setText(commentslist.size.toString())
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AdapterForComments(commentslist,requireContext()){
            lifecycleScope.launch {
                openBottomModelSheet(it,"0")
            }

        }
        recyclerView.adapter = adapter


    }

    companion object {
        private const val ARG_POST_ID = "postId"

        fun newInstance(postId: String) =
            postpage().apply {
                arguments = Bundle().apply {
                    putString(ARG_POST_ID, postId)
                }
            }
    }
}
