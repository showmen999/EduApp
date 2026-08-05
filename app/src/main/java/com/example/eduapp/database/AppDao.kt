package com.example.eduapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert
    suspend fun insert(user: User): Long
    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): Flow<List<User>>
    @Query("SELECT * FROM users ORDER BY score DESC LIMIT 10")
    fun getTopScores(): Flow<List<User>>
    @Query("DELETE FROM users")
    suspend fun deleteAll()
}
