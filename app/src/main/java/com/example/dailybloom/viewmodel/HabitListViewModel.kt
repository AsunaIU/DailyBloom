package com.example.dailybloom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.dailybloom.model.Habit
import com.example.dailybloom.model.HabitChangeListener
import com.example.dailybloom.model.HabitRepository

class HabitListViewModel(private val state: SavedStateHandle) : ViewModel() {

    private val isGood = state.get<Boolean>("IS_BAD")

    private val _habits = MutableLiveData(HabitRepository.getHabits())
    val habits: LiveData<Map<String, Habit>> = _habits

    private val habitsListener = HabitChangeListener { habits ->
        _habits.postValue(habits)
    }

    init {
        HabitRepository.addListener(habitsListener)
    }

    override fun onCleared() {
        super.onCleared()
        HabitRepository.removeListener(habitsListener)
    }
}
