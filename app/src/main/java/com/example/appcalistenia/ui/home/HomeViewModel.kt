package com.example.appcalistenia.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appcalistenia.data.local.AppDatabase
import com.example.appcalistenia.data.repository.ParkRepository
import com.example.appcalistenia.model.Park
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).parkDao()
    private val repository = ParkRepository(dao)

    val currentUserEmail: String
        get() = FirebaseAuth.getInstance().currentUser?.email ?: "Usuario"

    val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: "offline_user"

    val parks: StateFlow<List<Park>> = repository.parks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun savePark(existingId: String?, name: String, city: String, description: String) {
        if (name.isBlank() || city.isBlank()) return
        viewModelScope.launch {
            repository.savePark(existingId, name, city, description, currentUserId)
        }
    }

    fun deletePark(parkId: String) {
        viewModelScope.launch {
            repository.deletePark(parkId)
        }
    }

    fun forceSync() {
        viewModelScope.launch {
            repository.syncPendingChanges()
        }
    }
}
