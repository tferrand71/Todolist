package com.letotoo06.todo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.letotoo06.todo.data.Task
import com.letotoo06.todo.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter { TOUTES, TERMINEES, NON_TERMINEES, URGENTES }
enum class TaskSort { DATE_ASC, DATE_DESC, ALPHA_ASC, ALPHA_DESC }

class TodoViewModel(private val dao: TaskDao) : ViewModel() {

    private val _filter = MutableStateFlow(TaskFilter.TOUTES)
    private val _sort = MutableStateFlow(TaskSort.DATE_ASC)
    private val _isDarkTheme = MutableStateFlow(false)

    // NOUVEAU : État pour la barre de recherche
    private val _searchQuery = MutableStateFlow("")

    val isDarkTheme = _isDarkTheme
    val searchQuery = _searchQuery

    // NOUVEAU : On écoute aussi la barre de recherche (query)
    val tasks = combine(dao.getAll(), _filter, _sort, _searchQuery) { taskList, filter, sort, query ->
        val currentTime = System.currentTimeMillis()

        // 1. Filtrage par statut
        var result = when (filter) {
            TaskFilter.TOUTES -> taskList
            TaskFilter.TERMINEES -> taskList.filter { it.estTerminee }
            TaskFilter.NON_TERMINEES -> taskList.filter { !it.estTerminee }
            TaskFilter.URGENTES -> taskList.filter {
                !it.estTerminee && it.dateLimite != null && it.dateLimite!! <= currentTime + 172800000L
            }
        }

        // 2. NOUVEAU : Filtrage par recherche de texte
        if (query.isNotBlank()) {
            result = result.filter {
                it.titre.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }

        // 3. Tri
        result = when (sort) {
            TaskSort.DATE_ASC -> result.sortedBy { it.dateLimite ?: Long.MAX_VALUE }
            TaskSort.DATE_DESC -> result.sortedByDescending { it.dateLimite ?: Long.MIN_VALUE }
            TaskSort.ALPHA_ASC -> result.sortedBy { it.titre.lowercase() }
            TaskSort.ALPHA_DESC -> result.sortedByDescending { it.titre.lowercase() }
        }
        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(filter: TaskFilter) { _filter.value = filter }
    fun setSort(sort: TaskSort) { _sort.value = sort }
    fun setDarkTheme(isDark: Boolean) { _isDarkTheme.value = isDark }
    fun setSearchQuery(query: String) { _searchQuery.value = query } // NOUVEAU

    fun addTask(titre: String, description: String, dateLimite: Long?) {
        viewModelScope.launch { dao.insert(Task(titre = titre, description = description, dateLimite = dateLimite)) }
    }

    // NOUVEAU : Fonction générique pour modifier une tâche entière
    fun updateTask(task: Task) {
        viewModelScope.launch { dao.update(task) }
    }

    fun toggleTaskStatus(task: Task) {
        viewModelScope.launch { dao.update(task.copy(estTerminee = !task.estTerminee)) }
    }
    fun deleteTask(task: Task) {
        viewModelScope.launch { dao.delete(task) }
    }
    fun deleteAllTasks() {
        viewModelScope.launch { tasks.value.forEach { dao.delete(it) } }
    }
}

class TodoViewModelFactory(private val dao: TaskDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}