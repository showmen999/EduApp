package com.example.eduapp

import com.example.eduapp.database.AppDao
import com.example.eduapp.database.User
import com.example.eduapp.viewmodel.AppViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Test

class AppViewModelTest {

    private val fakeDao = object : AppDao {
        override suspend fun insert(user: User): Long = 1L
        override fun getAllUsers(): Flow<List<User>> = flowOf(emptyList())
        override fun getTopScores(): Flow<List<User>> = flowOf(emptyList())
        override suspend fun deleteAll() {}
    }

    private val viewModel = AppViewModel(fakeDao)

    @Test
    fun submitAnswer_correctIncrementScore() {
        // Given a fresh quiz state (handled by internal logic or startQuiz)
        // Note: startQuiz requires a Context which is hard to mock in local unit test without Robolectric.
        // We will test the pure logic of submitAnswer which uses StateFlows.
        
        // When
        viewModel.submitAnswer("10", "10")
        
        // Then
        assertEquals(10, viewModel.score.value)
        assertEquals(true, viewModel.isCorrect.value)
        assertEquals(true, viewModel.isSubmitted.value)
    }

    @Test
    fun submitAnswer_incorrectDoesNotIncrementScore() {
        // When
        viewModel.submitAnswer("5", "10")
        
        // Then
        assertEquals(0, viewModel.score.value)
        assertEquals(false, viewModel.isCorrect.value)
        assertEquals(true, viewModel.isSubmitted.value)
    }
}
