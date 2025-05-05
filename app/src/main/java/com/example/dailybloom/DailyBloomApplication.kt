package com.example.dailybloom

import android.app.Application
import com.example.dailybloom.data.local.HabitDatabase
import com.example.dailybloom.data.local.HabitRepository

class DailyBloomApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        val database = HabitDatabase.getDatabase(this)
        HabitRepository.initialize(database)
    }
}