package com.example.dailybloom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dailybloom.model.Habit
import com.example.dailybloom.model.HabitChangeListener
import com.example.dailybloom.model.HabitRepository

class HabitListViewModel : ViewModel(), HabitChangeListener {

    private val _habits = MutableLiveData(HabitRepository.getHabits())
    val habits: LiveData<Map<String, Habit>> = _habits

    init {
        HabitRepository.addListener(this)
    }

    override fun onHabitsChanged(habits: Map<String, Habit>) {
        _habits.postValue(habits)
    }

    // очистка слушателя при уничтожении ViewModel (уничтожении activity)
    override fun onCleared() {
        super.onCleared()
        HabitRepository.removeListener(this)
    }
}
