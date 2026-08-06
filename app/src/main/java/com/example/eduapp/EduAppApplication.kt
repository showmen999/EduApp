package com.example.eduapp

import android.app.Application
import androidx.room.Room
import com.example.eduapp.database.AppDatabase

class EduAppApplication : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "app_db"
        ).build()
    }
}
