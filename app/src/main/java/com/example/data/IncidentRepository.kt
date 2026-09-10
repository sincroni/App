package com.example.data

import kotlinx.coroutines.flow.Flow

class IncidentRepository(private val incidentDao: IncidentDao) {
    val allIncidents: Flow<List<Incident>> = incidentDao.getAllIncidents()

    suspend fun insert(incident: Incident): Long {
        return incidentDao.insertIncident(incident)
    }

    fun getIncident(id: Int): Flow<Incident?> {
        return incidentDao.getIncidentById(id)
    }

    suspend fun markAsSynced(id: Int, protocolNumber: String) {
        incidentDao.updateIncidentStatus(id, "Sincronizado", protocolNumber)
    }
}
