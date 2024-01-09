package com.example.orionhub

import java.util.Date

class SubredditModel (
    var title: String,
    var description: String,
    val mods: List<String> = listOf(), // Empty list as default
    val tags: List<String> = listOf(title),  // Empty list as default
    val usersList: List<String> = listOf("u8"), // Empty list as default
    val createdAt: Date = Date(), // Current date as default
    val postIDList: List<String> = listOf("0"), // Empty list as default
    val totalUsers: Int = usersList.size  // 0 as default
)