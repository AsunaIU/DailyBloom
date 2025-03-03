package com.example.dailybloom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class Habit (
    val title: String,
    val description: String,
    val priority: String,
    val type: String,
    val frequency: String,
    val color: String
)

class CreateHabitViewModel : ViewModel() {

    private val _habitSaved = MutableLiveData<Boolean>()
    val habitSaved: LiveData<Boolean> get() = _habitSaved

    fun saveHabit(title: String, description: String, priority: String, type: String, frequency: String, color: String) {
        if (title.isBlank() || description.isBlank() || priority.isBlank() || type.isBlank() || frequency.isBlank() || color.isBlank()) {
            _habitSaved.value = false // Не сохраняем, если что-то не заполнено
            return
        }

        val newHabit = Habit(title, description, priority, type, frequency, color)

        viewModelScope.launch {
            _habitSaved.value = true // Фиксируем успешное сохранение
        }
    }
}