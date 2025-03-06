package com.example.dailybloom.model

// управляет хранилищем привычек (habits) и уведомляет listeners об изменениях
// работает как менеджер данных, предоставляя интерфейс для добавления, обновления, удаления и получения списка привычек

class HabitRepository {

    private val habits: MutableMap<String, Habit> = java.util.concurrent.ConcurrentHashMap()
    private val listeners = java.util.concurrent.CopyOnWriteArrayList<HabitChangeListener>()

    // Функции управления привычками

    fun addHabit(habit: Habit) { // вставляет новую привычку или заменяет существующую, если такая уже есть
        habits[habit.id] = habit
        notifyListeners()
    }


    fun updateHabit(habitId: String, updatedHabit: Habit) { //разделение на будущее: заменяет habits, не проверяя существование старой записи
        habits[habitId] = updatedHabit
        notifyListeners()
    }


    fun removeHabit(habitId: String) {  // на будущее - добавление функционала удаления привычки
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
        val currentHabits = getHabits()   // метод getHabits() - возвращает копию коллекции, передаем ee listener
        for (listener in listeners) {
            listener.onHabitsChanged(currentHabits)
        }
    }
}