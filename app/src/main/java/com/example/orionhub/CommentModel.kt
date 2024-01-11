package com.example.orionhub

import java.util.Date

class CommentModel (
        val commentId: String,
        val postId: String,
        val authorId: String,
        val content: String,
        val replyingTo: String?,
        val createdAt: Date = Date() // Current date as default
        )