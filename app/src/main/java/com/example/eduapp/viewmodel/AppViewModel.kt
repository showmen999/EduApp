package com.example.eduapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eduapp.database.AppDao
import com.example.eduapp.database.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppViewModel(private val dao: AppDao) : ViewModel() {

    val users: Flow<List<User>> = dao.getAllUsers()
    val topScores: Flow<List<User>> = dao.getTopScores()

    private val _lastInsertedId = MutableStateFlow<Long>(-1)
    val lastInsertedId: StateFlow<Long> = _lastInsertedId

    fun addUser(username: String, level: String = "1", score: Int = 0) {
        viewModelScope.launch {
            val user = User(username = username, level = level, score = score)
            val id = dao.insert(user)
            _lastInsertedId.value = id
        }
    }

    fun clearUsers() {
        viewModelScope.launch {
            dao.deleteAll()
        }
    }
}
