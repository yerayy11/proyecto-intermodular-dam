package com.example.appcalistenia.data.repository

import com.example.appcalistenia.data.local.ParkDao
import com.example.appcalistenia.data.local.toEntity
import com.example.appcalistenia.data.remote.FirestoreDataSource
import com.example.appcalistenia.model.Park
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class ParkRepository(
    private val parkDao: ParkDao,
    private val firestoreSource: FirestoreDataSource = FirestoreDataSource()
) {
    private val repoScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // Sincronización automática: todo cambio en Firestore actualiza Room
        repoScope.launch {
            firestoreSource.observeParksRealtime().collect { remoteParks ->
                val unsyncedIds = parkDao.getUnsyncedParks().map { it.id }.toSet()
                val entitiesToCache = remoteParks
                    .filterNot { it.id in unsyncedIds }
                    .map { it.toEntity(isSynced = true) }
                parkDao.insertOrUpdateAll(entitiesToCache)
            }
        }
    }

    // La UI siempre lee desde Room (instantáneo y 100% funcional sin internet)
    val parks: Flow<List<Park>> = parkDao.observeAllParks().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun savePark(
        existingId: String?,
        name: String,
        city: String,
        description: String,
        userId: String
    ) {
        val park = Park(
            id = existingId ?: UUID.randomUUID().toString(),
            name = name.trim(),
            city = city.trim(),
            description = description.trim(),
            createdBy = userId,
            updatedAt = System.currentTimeMillis()
        )
        // 1. Guardamos primero en local (Room) marcado como pendiente
        parkDao.insertOrUpdate(park.toEntity(isSynced = false))

        // 2. Intentamos subir a Firestore; si hay red, marcamos como sincronizado
        runCatching {
            firestoreSource.upsertPark(park)
            parkDao.insertOrUpdate(park.toEntity(isSynced = true))
        }
    }

    suspend fun deletePark(parkId: String) {
        parkDao.deleteById(parkId)
        runCatching {
            firestoreSource.deletePark(parkId)
        }
    }

    suspend fun syncPendingChanges() {
        val pending = parkDao.getUnsyncedParks()
        pending.forEach { entity ->
            runCatching {
                firestoreSource.upsertPark(entity.toDomain())
                parkDao.insertOrUpdate(entity.copy(isSynced = true))
            }
        }
    }
}