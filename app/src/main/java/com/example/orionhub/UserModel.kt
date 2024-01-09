package com.example.orionhub

import java.util.Date

class UserModel (
    val username: String,
    val password: String,
    val email: String,
    val profileUrl: String = "",  // Empty string instead of null
    val karma: String = "0",      // Default karma value
    val createdAt: Date = Date(), // Current date as default
    val postList: List<String> = listOf("0"),
    val commentList: List<String> = listOf("0"),
    val subredditList: List<String> = listOf("OrionHub"),
    val subredditAdmin: List<String> = listOf("OrionHub"),
    val savedposts: List<String> = listOf("0"),
)