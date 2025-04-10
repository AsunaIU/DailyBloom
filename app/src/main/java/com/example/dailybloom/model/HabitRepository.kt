package com.example.dailybloom.model

import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

// управляет хранилищем привычек (habits) и уведомляет listeners об изменениях
// работает как менеджер данных, предоставляя интерфейс для добавления, обновления, удаления и получения списка привычек

object HabitRepository {

    private val habits: MutableMap<String, Habit> = java.util.concurrent.ConcurrentHashMap()
    private val listeners = CopyOnWriteArrayList<WeakReference<HabitChangeListener>>()

    // Функции управления привычками

    fun addHabit(habit: Habit) {
        habits[habit.id] = habit
        notifyListeners()
    }

    fun updateHabit(habitId: String, updatedHabit: Habit) {
        habits[habitId] = updatedHabit
        notifyListeners()
    }

    fun removeHabit(habitId: String) {  // на будущее - добавление функционала удаления привычки
        habits.remove(habitId)
        notifyListeners()
    }

    fun getHabits(): Map<String, Habit> = habits.toMap()

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
        val currentHabits = getHabits()   // метод getHabits() - возвращает копию коллекции, передаем ee listener
        for (listener in listeners) {
            listener.get()?.onHabitsChanged(currentHabits)
        }
    }
}