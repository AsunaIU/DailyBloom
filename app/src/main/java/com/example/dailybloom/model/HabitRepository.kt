package com.example.dailybloom.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import com.example.dailybloom.data.local.HabitDatabase
import com.example.dailybloom.data.local.HabitEntity
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

// управляет хранилищем привычек (habits) и уведомляет listeners об изменениях
// работает как менеджер данных, предоставляя интерфейс для добавления, обновления, удаления и получения списка привычек

object HabitRepository {

    private lateinit var database: HabitDatabase
    private val listeners = CopyOnWriteArrayList<WeakReference<HabitChangeListener>>()

    private val _habits = MediatorLiveData<Map<String, Habit>>()
    val habits: LiveData<Map<String, Habit>> = _habits

    fun initialize(appDatabase: HabitDatabase) {
        database = appDatabase

        val dbSource = database.habitDao().getAllHabits().map { habitEntities ->
            habitEntities.associate { entity ->
                val habit = HabitEntity.toHabit(entity)
                habit.id to habit
            }
        }
        _habits.addSource(dbSource) { habitMap ->
            _habits.value = habitMap
            notifyListeners()
        }
    }

    // Функции управления привычками

    fun addHabit(habit: Habit) {
        val habitEntity = HabitEntity.fromHabit(habit)
        database.habitDao().insertHabit(habitEntity)
    }

    fun updateHabit(habitId: String, updatedHabit: Habit) {
        val habitToUpdate = updatedHabit.copy(id = habitId)
        val habitEntity = HabitEntity.fromHabit(habitToUpdate)
        database.habitDao().updateHabit(habitEntity)
    }

    fun removeHabit(habitId: String) {  // на будущее - добавление функционала удаления привычки
        database.habitDao().deleteHabit(habitId)
    }

    fun getHabits(): Map<String, Habit> = _habits.value ?: emptyMap()

    // Функции управления слушателями

    fun addListener(listener: HabitChangeListener) {
        if (!listeners.any { it.get() == listener }) {
            listeners.add(WeakReference(listener))
        }
    }

    fun removeListener(listener: HabitChangeListener) {
        listeners.removeIf { it.get() == listener || it.get() == null }
    }

    private fun notifyListeners() {
        val currentHabits =
            getHabits()   // метод getHabits() - возвращает копию коллекции, передаем ee listener
        for (listener in listeners) {
            listener.get()?.onHabitsChanged(currentHabits)
        }
    }
}
