package com.example.orionhub

import java.util.Date

class PostFirebase(
        val postId: String, // Unique identifier for the post
        val userId: String, // ID of the user who created the post
        val subreddit: String, // The subreddit this post belongs to
        val contentType: String, // Type of content (e.g., "text", "image", "video")
        val title: String, // Title of the post
        val content: String, // The content of the post
        val commentList: List<String>, // List of comments (IDs or text)
        val votes: Int,
        val createdAt: Date = Date(), // Creation timestamp
)