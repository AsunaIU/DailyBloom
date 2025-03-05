package com.example.dailybloom.model

object HabitRepositorySingleton {
    val repository by lazy { HabitRepository() }
}