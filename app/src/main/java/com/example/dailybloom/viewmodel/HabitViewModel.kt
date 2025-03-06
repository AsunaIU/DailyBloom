package com.example.dailybloom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dailybloom.model.Habit
import com.example.dailybloom.model.HabitChangeListener
import com.example.dailybloom.model.HabitRepository

// HabitViewModel подписан на изменения (реализует интерфейс HabitChangeListener), также

class HabitViewModel(private val repository: HabitRepository) : ViewModel(), HabitChangeListener {

    private val _habits = MutableLiveData(repository.getHabits())
    val habits: LiveData<Map<String, Habit>> = _habits  //  создаем переменную типа LiveData, чтобы отслеживать изменения данных

    init {
        repository.addListener(this)
    }

    /** далее вызываются методы репозитория (addHabit, removeHabit, updateHabit) изменения происходят внутри репозитория,
      * после этого через механизм слушателей (onHabitsChanged()) уведомляется ViewModel о том, что данные изменились
    */

    fun addHabit(habit: Habit) {
        repository.addHabit(habit)
    }

    fun removeHabit(habitId: String) {  // на будущее - добавление функционала удаления привычки
        repository.removeHabit(habitId)
    }

    fun updateHabit(id: String, habit: Habit) {
        repository.updateHabit(id, habit)
    }

    override fun onHabitsChanged(habits: Map<String, Habit>) {
        _habits.postValue(habits)
    }

    // очистка слушателя при уничтожении ViewModel (уничтожение activity)
    override fun onCleared() {
        super.onCleared()
        repository.removeListener(this)
    }
}
