package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incidents")
data class Incident(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val incidentType: String, // "Roubo", "Furto", etc.
    val isSafe: Boolean,
    val hasAudioDescription: Boolean,
    val attachedFileName: String?,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Pendente (Offline)", // Pendente (Offline), Sincronizado, etc.
    val protocolNumber: String? = null // Protocolo recebido do backend
)
