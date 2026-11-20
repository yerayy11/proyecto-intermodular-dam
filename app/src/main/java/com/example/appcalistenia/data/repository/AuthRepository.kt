package com.example.appcalistenia.data.repository

import com.example.appcalistenia.data.remote.FirebaseAuthDataSource
import com.example.appcalistenia.data.remote.FirestoreDataSource
import com.example.appcalistenia.model.UserProfile
import com.google.firebase.auth.FirebaseUser

class AuthRepository(
    private val authSource: FirebaseAuthDataSource = FirebaseAuthDataSource(),
    private val firestoreSource: FirestoreDataSource = FirestoreDataSource()
) {
    val currentUser: FirebaseUser? get() = authSource.currentUser

    suspend fun login(email: String, password: String): Result<FirebaseUser> = runCatching {
        authSource.loginWithEmail(email, password)
    }

    suspend fun register(email: String, password: String, username: String): Result<FirebaseUser> = runCatching {
        val user = authSource.registerWithEmail(email, password)
        firestoreSource.saveUserProfileIfNew(
            UserProfile(uid = user.uid, email = email, username = username)
        )
        user
    }

    suspend fun loginWithGoogle(idToken: String): Result<FirebaseUser> = runCatching {
        val user = authSource.loginWithGoogleIdToken(idToken)
        val defaultName = user.displayName ?: user.email?.substringBefore("@") ?: "Atleta"
        firestoreSource.saveUserProfileIfNew(
            UserProfile(uid = user.uid, email = user.email.orEmpty(), username = defaultName)
        )
        user
    }

    suspend fun resetPassword(email: String): Result<Unit> = runCatching {
        authSource.sendPasswordReset(email)
    }

    fun logout() = authSource.signOut()
}
