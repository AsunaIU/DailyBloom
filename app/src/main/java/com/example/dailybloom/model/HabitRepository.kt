package com.example.dailybloom.model


class HabitRepository {

    private val habits: MutableMap<String, Habit> = java.util.concurrent.ConcurrentHashMap()

    private val listeners = java.util.concurrent.CopyOnWriteArrayList<HabitChangeListener>()

    fun addHabit(habit: Habit) {
        habits[habit.id] = habit
        notifyListeners()
    }

    fun updateHabit(habitId: String, updatedHabit: Habit) {
        habits[habitId] = updatedHabit
        notifyListeners()
    }

    fun removeHabit(habitId: String) {
        habits.remove(habitId)
        notifyListeners()
    }

    fun getHabits(): Map<String, Habit> = habits.toMap()

    fun addListener(listener: HabitChangeListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: HabitChangeListener) {
        listeners.remove(listener)
    }

    private fun notifyListeners() {
        val currentHabits = getHabits()
        for (listener in listeners) {
            listener.onHabitsChanged(currentHabits)
        }
    }
}