package com.example.dailybloom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dailybloom.model.Habit
import com.example.dailybloom.model.HabitChangeListener
import com.example.dailybloom.model.HabitRepository


class HabitViewModel(private val repository: HabitRepository) : ViewModel(), HabitChangeListener {

    private val _habits = MutableLiveData(repository.getHabits())
    val habits: LiveData<Map<String, Habit>> = _habits

    init {
        repository.addListener(this)
    }

    fun getCurrentHabits() = repository.getHabits()

    fun addHabit(habit: Habit) {
        repository.addHabit(habit)
    }

    fun removeHabit(habitId: String) {
        repository.removeHabit(habitId)
    }

    fun updateHabit(id: String, habit: Habit) {
        repository.updateHabit(id, habit)
    }

    override fun onHabitsChanged(habits: Map<String, Habit>) {
        _habits.postValue(habits)
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeListener(this)
    }
}
