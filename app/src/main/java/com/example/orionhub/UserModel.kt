package com.example.orionhub

import java.util.Date

class UserModel (
    val username: String,
    val password: String,
    val email: String,
    val profileUrl: String = "",  // Empty string instead of null
    val karma: String = "0",      // Default karma value
    val createdAt: Date = Date(), // Current date as default
    val postList: List<String> = listOf("empty"),
    val commentList: List<String> = listOf("empty"),
    val subredditList: List<String> = listOf("empty"),
    val subredditAdmin: List<String> = listOf("empty"),
    val savedposts: List<String> = listOf("empty"),
)