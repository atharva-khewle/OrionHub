package com.example.orionhub

import java.util.Date

class PostShownModel (
    var subredditName: String,
    var title: String,
    var contentType: String,
    var contenttext:String,
    var medialink: String,
    var votes: Int,
    var comments: Int,
    var postId: String,
    val createdAt: Date = Date(), // Current date as default
)
