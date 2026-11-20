package com.example.appcalistenia.model

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val username: String = "",
    val level: String = "Principiante",
    val streak: Int = 0
)