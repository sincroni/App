package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {
    @Query("SELECT * FROM incidents ORDER BY timestamp DESC")
    fun getAllIncidents(): Flow<List<Incident>>

    @Query("SELECT * FROM incidents WHERE id = :id LIMIT 1")
    fun getIncidentById(id: Int): Flow<Incident?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: Incident): Long

    @Query("UPDATE incidents SET status = :status, protocolNumber = :protocolNumber WHERE id = :id")
    suspend fun updateIncidentStatus(id: Int, status: String, protocolNumber: String)

    @Query("DELETE FROM incidents WHERE id = :id")
    suspend fun deleteIncidentById(id: Int)
}
