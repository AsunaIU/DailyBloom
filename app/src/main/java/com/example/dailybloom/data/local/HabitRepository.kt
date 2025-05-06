package com.example.dailybloom.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dailybloom.model.Habit
import androidx.lifecycle.asLiveData

// управляет хранилищем привычек (habits) и уведомляет listeners об изменениях
// работает как менеджер данных, предоставляя интерфейс для добавления, обновления, удаления и получения списка привычек

object HabitRepository {

    private lateinit var implementation: HabitRepositoryImpl
    private var observerRegistered = false

    val habits: LiveData<Map<String, Habit>>
        get() = implementation.habits.asLiveData()

    fun initialize(repositoryImpl: HabitRepositoryImpl) {
        if (!::implementation.isInitialized) {
            implementation = repositoryImpl
        }
    }

    suspend fun addHabit(habit: Habit): Boolean {
        return implementation.addHabit(habit)
    }

    suspend fun updateHabit(habitId: String, updatedHabit: Habit): Boolean {
        return implementation.updateHabit(habitId, updatedHabit)
    }

    suspend fun removeHabit(habitId: String): Boolean {
        return implementation.removeHabit(habitId)
    }

    fun getHabits(): Map<String, Habit> = implementation.getHabits()

    fun syncWithServer() {
        implementation.syncWithServer()
    }
}
