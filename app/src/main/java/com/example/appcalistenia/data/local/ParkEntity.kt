package com.example.appcalistenia.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.appcalistenia.model.Park

@Entity(tableName = "parks_table")
data class ParkEntity(
    @PrimaryKey val id: String,
    val name: String,
    val city: String,
    val description: String,
    val createdBy: String,
    val updatedAt: Long,
    val isSynced: Boolean = true
) {
    fun toDomain() = Park(id, name, city, description, createdBy, updatedAt)
}

fun Park.toEntity(isSynced: Boolean = true) = ParkEntity(
    id = id,
    name = name,
    city = city,
    description = description,
    createdBy = createdBy,
    updatedAt = updatedAt,
    isSynced = isSynced
)