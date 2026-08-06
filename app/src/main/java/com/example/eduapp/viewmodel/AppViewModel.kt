package com.example.eduapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eduapp.database.AppDao
import com.example.eduapp.database.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(private val dao: AppDao) : ViewModel() {

    val users: Flow<List<User>> = dao.getAllUsers()
    val topScores: Flow<List<User>> = dao.getTopScores()

    private val _lastInsertedId = MutableStateFlow<Long>(-1)
    val lastInsertedId: StateFlow<Long> = _lastInsertedId

    // Quiz State
    private val _quizImages = MutableStateFlow<List<String>>(emptyList())
    val quizImages: StateFlow<List<String>> = _quizImages.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _isSubmitted = MutableStateFlow(false)
    val isSubmitted: StateFlow<Boolean> = _isSubmitted.asStateFlow()

    private val _isCorrect = MutableStateFlow(false)
    val isCorrect: StateFlow<Boolean> = _isCorrect.asStateFlow()

    private var scoreSaved = false

    fun startQuiz(context: Context, level: Int, questionsCount: Int) {
        if (_quizImages.value.isEmpty()) {
            val allFiles = context.assets.list(level.toString()) ?: emptyArray()
            val filtered = allFiles.filter { it.contains("_") && (it.endsWith(".png") || it.endsWith(".jpg")) }
                .shuffled()
                .take(questionsCount)
            _quizImages.value = filtered
            _currentIndex.value = 0
            _score.value = 0
            _isSubmitted.value = false
            _isCorrect.value = false
            scoreSaved = false
        }
    }

    fun submitAnswer(userAnswer: String, correctAnswer: String) {
        if (!_isSubmitted.value) {
            _isSubmitted.value = true
            _isCorrect.value = userAnswer.trim().equals(correctAnswer.trim(), ignoreCase = true)
            if (_isCorrect.value) {
                _score.value += 10
            }
        }
    }

    fun nextQuestion() {
        if (_currentIndex.value < _quizImages.value.size - 1) {
            _currentIndex.value += 1
            _isSubmitted.value = false
            _isCorrect.value = false
        }
    }

    fun resetQuizState() {
        _quizImages.value = emptyList()
        _currentIndex.value = 0
        _score.value = 0
        _isSubmitted.value = false
        _isCorrect.value = false
        scoreSaved = false
    }

    fun addUser(username: String, level: String = "1", score: Int = 0) {
        if (scoreSaved) return
        viewModelScope.launch {
            val user = User(username = username, level = level, score = score)
            val id = dao.insert(user)
            _lastInsertedId.value = id
            scoreSaved = true
        }
    }

    fun clearUsers() {
        viewModelScope.launch {
            dao.deleteAll()
        }
    }
}
