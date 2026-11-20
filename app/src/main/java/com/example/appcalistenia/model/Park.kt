package com.example.appcalistenia.model

data class Park(
    val id: String = "",
    val name: String = "",
    val city: String = "",
    val description: String = "",
    val createdBy: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)