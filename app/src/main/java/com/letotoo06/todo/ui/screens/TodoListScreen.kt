package com.letotoo06.todo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.letotoo06.todo.data.Task
import com.letotoo06.todo.ui.TaskFilter
import com.letotoo06.todo.ui.TaskSort
import com.letotoo06.todo.ui.TodoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(viewModel: TodoViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState() // NOUVEAU

    var showAddDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskDesc by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var expandedSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Ma ToDo List", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    Box {
                        IconButton(onClick = { expandedSortMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Trier")
                        }
                        DropdownMenu(expanded = expandedSortMenu, onDismissRequest = { expandedSortMenu = false }) {
                            DropdownMenuItem(text = { Text("Date (Plus proche)") }, onClick = { viewModel.setSort(TaskSort.DATE_ASC); expandedSortMenu = false })
                            DropdownMenuItem(text = { Text("Date (Plus lointaine)") }, onClick = { viewModel.setSort(TaskSort.DATE_DESC); expandedSortMenu = false })
                            DropdownMenuItem(text = { Text("Alphabétique (A-Z)") }, onClick = { viewModel.setSort(TaskSort.ALPHA_ASC); expandedSortMenu = false })
                            DropdownMenuItem(text = { Text("Alphabétique (Z-A)") }, onClick = { viewModel.setSort(TaskSort.ALPHA_DESC); expandedSortMenu = false })
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) { Icon(Icons.Default.Add, contentDescription = "Ajouter") }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // NOUVEAU : BARRE DE RECHERCHE
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                label = { Text("Rechercher une tâche...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Recherche") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                item { FilterButton("Toutes", onClick = { viewModel.setFilter(TaskFilter.TOUTES) }) }
                item { FilterButton("À faire", onClick = { viewModel.setFilter(TaskFilter.NON_TERMINEES) }) }
                item { FilterButton("Fini", onClick = { viewModel.setFilter(TaskFilter.TERMINEES) }) }
                item { FilterButton("🔥 Urgentes", onClick = { viewModel.setFilter(TaskFilter.URGENTES) }) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tasks, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        isDarkTheme = isDarkTheme,
                        onCheckedChange = { viewModel.toggleTaskStatus(task) },
                        onDeleteClick = { viewModel.deleteTask(task) }
                    )
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Nouvelle tâche") },
                text = {
                    Column {
                        OutlinedTextField(value = newTaskTitle, onValueChange = { newTaskTitle = it }, label = { Text("Titre") }, singleLine = true)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = newTaskDesc, onValueChange = { newTaskDesc = it }, label = { Text("Description") })
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.DateRange, contentDescription = "Date")
                            Spacer(Modifier.width(8.dp))
                            val dateStr = if (datePickerState.selectedDateMillis != null) {
                                SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date(datePickerState.selectedDateMillis!!))
                            } else "Ajouter une date limite"
                            Text(dateStr)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            viewModel.addTask(newTaskTitle, newTaskDesc, datePickerState.selectedDateMillis)
                            newTaskTitle = ""
                            newTaskDesc = ""
                            datePickerState.selectedDateMillis = null
                            showAddDialog = false
                        }
                    }) { Text("Ajouter") }
                },
                dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Annuler") } }
            )
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = { TextButton(onClick = { showDatePicker = false }) { Text("OK") } },
                dismissButton = { TextButton(onClick = { showDatePicker = false; datePickerState.selectedDateMillis = null }) { Text("Annuler") } }
            ) { DatePicker(state = datePickerState) }
        }
    }
}

@Composable
fun FilterButton(text: String, onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        colors = ButtonDefaults.elevatedButtonColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.primary)
    ) { Text(text) }
}

@Composable
fun TaskItem(task: Task, isDarkTheme: Boolean, onCheckedChange: (Boolean) -> Unit, onDeleteClick: () -> Unit) {
    val currentTime = System.currentTimeMillis()
    val isCompleted = task.estTerminee
    val hasDate = task.dateLimite != null

    val isOverdue = hasDate && task.dateLimite!! < currentTime && !isCompleted
    val isUrgent = hasDate && task.dateLimite!! >= currentTime && task.dateLimite!! <= currentTime + 172800000L && !isCompleted

    val cardColor = when {
        isCompleted -> if (isDarkTheme) Color(0xFF1E3320) else Color(0xFFE8F5E9)
        isOverdue -> MaterialTheme.colorScheme.errorContainer
        isUrgent -> if (isDarkTheme) Color(0xFF422C10) else Color(0xFFFFF3E0)
        else -> if (isDarkTheme) Color(0xFF162436) else Color(0xFFE3F2FD)
    }

    val textColor = when {
        isCompleted -> if (isDarkTheme) Color(0xFFA5D6A7) else Color(0xFF2E7D32)
        isOverdue -> MaterialTheme.colorScheme.onErrorContainer
        isUrgent -> if (isDarkTheme) Color(0xFFFFB74D) else Color(0xFFE65100)
        else -> if (isDarkTheme) Color(0xFF90CAF9) else Color(0xFF1565C0)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 0.dp else 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = task.estTerminee,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = textColor.copy(alpha = 0.7f), uncheckedColor = textColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.titre,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                if (task.description.isNotBlank()) {
                    Text(text = task.description, color = textColor.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                }
                val dateStr = if (hasDate) SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date(task.dateLimite!!)) else ""
                val stateText = when {
                    isCompleted && hasDate -> "✅ Faite (Échéance: $dateStr)"
                    isCompleted -> "✅ Faite"
                    isOverdue -> "⚠️ En retard : $dateStr"
                    isUrgent -> "🔥 Bientôt : $dateStr"
                    hasDate -> "⏳ À faire : $dateStr"
                    else -> "⏳ À faire"
                }
                Text(text = stateText, color = textColor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, tint = if (isOverdue) MaterialTheme.colorScheme.error else textColor.copy(alpha = 0.8f), contentDescription = "Supprimer")
            }
        }
    }
}