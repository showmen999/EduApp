package com.example.eduapp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.eduapp.database.AppDao
import com.example.eduapp.database.AppDatabase
import com.example.eduapp.database.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: AppDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.appDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetTopScores() = runBlocking {
        val user1 = User(username = "Alice", score = 50, level = "Explorer")
        val user2 = User(username = "Bob", score = 60, level = "Challenger")
        
        dao.insert(user1)
        dao.insert(user2)
        
        val topScores = dao.getTopScores().first()
        assertEquals(2, topScores.size)
        assertEquals("Bob", topScores[0].username) // Bob has higher score
        assertEquals(60, topScores[0].score)
    }
}
