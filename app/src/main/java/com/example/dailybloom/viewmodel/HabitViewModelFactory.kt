package com.example.dailybloom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dailybloom.model.HabitRepository

/** HabitViewModelFactory) выступает в качестве посредника. :
  * Принимает необходимые зависимости (например, репозиторий) в своем конструкторе.
  * Создает экземпляр ViewModel, передавая эти зависимости в конструктор.
  * Возвращает готовую к использованию ViewModel через механизм ViewModelProvider
  */

class HabitViewModelFactory(private val repository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) { //
            @Suppress("UNCHECKED_CAST")
            return HabitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}