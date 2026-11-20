package com.example.appcalistenia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appcalistenia.ui.auth.AuthViewModel
import com.example.appcalistenia.ui.auth.LoginScreen
import com.example.appcalistenia.ui.home.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface {
                    val authViewModel: AuthViewModel = viewModel()
                    val authState by authViewModel.uiState.collectAsState()

                    if (authState.isAuthenticated) {
                        HomeScreen(onLogout = { authViewModel.logout() })
                    } else {
                        LoginScreen(viewModel = authViewModel)
                    }
                }
            }
        }
    }
}
