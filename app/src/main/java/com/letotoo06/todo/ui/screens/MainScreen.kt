package com.letotoo06.todo.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.letotoo06.todo.ui.TodoViewModel
import com.letotoo06.todo.data.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Accueil : BottomNavItem("accueil", "Accueil", Icons.Default.Home)
    object Gestion : BottomNavItem("gestion", "Gestion", Icons.Default.List)
    object Compte : BottomNavItem("compte", "Compte", Icons.Default.Person)
}

@Composable
fun MainScreen(viewModel: TodoViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Accueil.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Accueil.route) { TodoListScreen(viewModel = viewModel) }
            // NOUVEAU : On passe le viewModel à la page Gestion !
            composable(BottomNavItem.Gestion.route) { GestionScreen(viewModel = viewModel) }
            composable(BottomNavItem.Compte.route) { CompteScreen(viewModel = viewModel) }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(BottomNavItem.Accueil, BottomNavItem.Gestion, BottomNavItem.Compte)
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                ),
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

// NOUVEAU : PAGE DE GESTION COMPLÈTE (Modification & Suppression)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionScreen(viewModel: TodoViewModel) {
    val tasks by viewModel.tasks.collectAsState()

    // États pour la popup de modification
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var editTitle by remember { mutableStateOf("") }
    var editDesc by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Gérer mes tâches",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tasks, key = { it.id }) { task ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = task.titre, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            if (task.dateLimite != null) {
                                val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date(task.dateLimite!!))
                                Text(text = "Échéance : $dateStr", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        // Bouton Modifier
                        IconButton(onClick = {
                            taskToEdit = task
                            editTitle = task.titre
                            editDesc = task.description
                            datePickerState.selectedDateMillis = task.dateLimite
                        }) { Icon(Icons.Default.Edit, tint = MaterialTheme.colorScheme.primary, contentDescription = "Modifier") }

                        // Bouton Supprimer
                        IconButton(onClick = { viewModel.deleteTask(task) }) {
                            Icon(Icons.Default.Delete, tint = MaterialTheme.colorScheme.error, contentDescription = "Supprimer")
                        }
                    }
                }
            }
        }
    }

    // --- POPUP DE MODIFICATION ---
    if (taskToEdit != null) {
        AlertDialog(
            onDismissRequest = { taskToEdit = null },
            title = { Text("Modifier la tâche") },
            text = {
                Column {
                    OutlinedTextField(value = editTitle, onValueChange = { editTitle = it }, label = { Text("Titre") }, singleLine = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editDesc, onValueChange = { editDesc = it }, label = { Text("Description") })
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.DateRange, contentDescription = "Date")
                        Spacer(Modifier.width(8.dp))
                        val dateStr = if (datePickerState.selectedDateMillis != null) {
                            SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date(datePickerState.selectedDateMillis!!))
                        } else "Aucune date"
                        Text(dateStr)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (editTitle.isNotBlank()) {
                        // On sauvegarde les modifications !
                        viewModel.updateTask(taskToEdit!!.copy(
                            titre = editTitle,
                            description = editDesc,
                            dateLimite = datePickerState.selectedDateMillis
                        ))
                        taskToEdit = null
                    }
                }) { Text("Sauvegarder") }
            },
            dismissButton = { TextButton(onClick = { taskToEdit = null }) { Text("Annuler") } }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { showDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false; datePickerState.selectedDateMillis = null }) { Text("Effacer") } }
        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
fun CompteScreen(viewModel: TodoViewModel) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    var notificationsEnabled by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Mon Compte", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(bottom = 24.dp, top = 16.dp))
        Text(text = "Préférences", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 8.dp))
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
            Column {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifs", tint = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Activer les notifications", color = MaterialTheme.colorScheme.onSurface)
                    }
                    Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, contentDescription = "Thème", tint = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Forcer le mode sombre", color = MaterialTheme.colorScheme.onSurface)
                    }
                    Switch(checked = isDarkTheme, onCheckedChange = { viewModel.setDarkTheme(it) })
                }
            }
        }

        Text(text = "Gestion des données", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 8.dp))
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth().clickable { showDeleteDialog = true }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Effacer toutes les tâches", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Supprimer tout ?") },
            text = { Text("Êtes-vous sûr de vouloir supprimer toutes vos tâches ? Cette action est irréversible.") },
            confirmButton = { Button(onClick = { viewModel.deleteAllTasks(); showDeleteDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Supprimer définitivement") } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Annuler") } }
        )
    }
}