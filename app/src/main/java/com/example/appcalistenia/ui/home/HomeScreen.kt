package com.example.appcalistenia.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appcalistenia.model.Park

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val parks by homeViewModel.parks.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingPark by remember { mutableStateOf<Park?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Parques de Calistenia", style = MaterialTheme.typography.titleMedium)
                        Text(homeViewModel.currentUserEmail, style = MaterialTheme.typography.bodySmall)
                    }
                },
                actions = {
                    IconButton(onClick = { homeViewModel.forceSync() }) {
                        Icon(Icons.Default.Sync, contentDescription = "Sincronizar")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingPark = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir parque")
            }
        }
    ) { padding ->
        if (parks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay parques guardados. Pulsa + para añadir uno.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(parks, key = { it.id }) { park ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(park.name, style = MaterialTheme.typography.titleMedium)
                                Text(park.city, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                if (park.description.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(park.description, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            Row {
                                IconButton(onClick = {
                                    editingPark = park
                                    showDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }
                                IconButton(onClick = { homeViewModel.deletePark(park.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        ParkFormDialog(
            initialPark = editingPark,
            onDismiss = { showDialog = false },
            onConfirm = { name, city, desc ->
                homeViewModel.savePark(editingPark?.id, name, city, desc)
                showDialog = false
            }
        )
    }
}

@Composable
private fun ParkFormDialog(
    initialPark: Park?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialPark?.name ?: "") }
    var city by remember { mutableStateOf(initialPark?.city ?: "") }
    var description by remember { mutableStateOf(initialPark?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialPark == null) "Nuevo parque" else "Editar parque") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del parque") })
                OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("Ciudad / Zona") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Barras disponibles") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, city, description) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
