package com.example.appcalistenia.data.remote

import com.example.appcalistenia.model.Park
import com.example.appcalistenia.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreDataSource(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersRef = db.collection("users")
    private val parksRef = db.collection("parks")

    suspend fun saveUserProfileIfNew(profile: UserProfile) {
        val doc = usersRef.document(profile.uid).get().await()
        if (!doc.exists()) {
            usersRef.document(profile.uid).set(profile, SetOptions.merge()).await()
        }
    }

    fun observeParksRealtime(): Flow<List<Park>> = callbackFlow {
        val registration = parksRef.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            if (snapshot != null) {
                val parks = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Park::class.java)?.copy(id = doc.id)
                }
                trySend(parks)
            }
        }
        awaitClose { registration.remove() }
    }

    suspend fun upsertPark(park: Park) {
        parksRef.document(park.id).set(park).await()
    }

    suspend fun deletePark(parkId: String) {
        parksRef.document(parkId).delete().await()
    }
}

