package com.example.appcalistenia.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkDao {
    @Query("SELECT * FROM parks_table ORDER BY updatedAt DESC")
    fun observeAllParks(): Flow<List<ParkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(parks: List<ParkEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(park: ParkEntity)

    @Query("DELETE FROM parks_table WHERE id = :parkId")
    suspend fun deleteById(parkId: String)

    @Query("DELETE FROM parks_table")
    suspend fun clearAll()

    @Query("SELECT * FROM parks_table WHERE isSynced = 0")
    suspend fun getUnsyncedParks(): List<ParkEntity>
}