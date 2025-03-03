package com.example.dailybloom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HabitTrackerViewModel : ViewModel() {
    private val _habits = MutableLiveData<List<Habit>>(emptyList())
    val habits: LiveData<List<Habit>> get() = _habits

    fun addHabit(newHabit: Habit) {
        _habits.value = _habits.value?.plus(newHabit) ?: listOf(newHabit)
    }
}

data class Habit(
    val title: String,
    val description: String,
    val frequency: String,
    val creationDate: Long = System.currentTimeMillis()
)