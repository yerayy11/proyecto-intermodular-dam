package com.example.appcalistenia.ui.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appcalistenia.R
import com.example.appcalistenia.data.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(isAuthenticated = repository.currentUser != null)
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Introduce un email válido y contraseña de al menos 6 caracteres.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            repository.login(email.trim(), password)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isAuthenticated = true) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Error al iniciar sesión") } }
        }
    }

    fun register(email: String, password: String, username: String) {
        if (username.isBlank() || email.isBlank() || password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Completa todos los campos (contraseña mín. 6 caracteres).") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            repository.register(email.trim(), password, username.trim())
                .onSuccess { _uiState.update { it.copy(isLoading = false, isAuthenticated = true) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Error en el registro") } }
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(context, request)
                val credential = result.credential

                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    repository.loginWithGoogle(googleToken)
                        .onSuccess { _uiState.update { it.copy(isLoading = false, isAuthenticated = true) } }
                        .onFailure { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) } }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Credencial de Google no válida") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error con Google Sign-In: ${e.localizedMessage}") }
            }
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Escribe tu email para enviarte el enlace de recuperación.") }
            return
        }
        viewModelScope.launch {
            repository.resetPassword(email.trim())
                .onSuccess { _uiState.update { it.copy(infoMessage = "Correo de recuperación enviado a $email", errorMessage = null) } }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.localizedMessage) } }
        }
    }

    fun logout() {
        repository.logout()
        _uiState.value = AuthUiState(isAuthenticated = false)
    }
}
