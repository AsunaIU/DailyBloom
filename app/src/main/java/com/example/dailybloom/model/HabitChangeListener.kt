package com.example.dailybloom.model

// часть паттерна observer
// listener уведомляет об изменениях в данных привычек
// метод onHabitsChanged вызывается каждый раз, когда происходит изменение в мапе привычек

interface HabitChangeListener {
    fun onHabitsChanged(habits: Map<String, Habit>)
}