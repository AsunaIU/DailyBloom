package com.example.dailybloom.viewmodel

import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.dailybloom.model.Habit


class HabitTrackerViewModel : ViewModel() {
    private val _habits = mutableListOf<Habit>()
    val habits: List<Habit> get() = _habits

    fun addHabit(newHabit: Habit) {
        _habits.add(newHabit)
    }

    fun updateHabit(updatedHabit: Habit) {
        val index = _habits.indexOfFirst { it.id == updatedHabit.id }
        if (index != -1) {
            _habits[index] = updatedHabit
        }
    }
}
