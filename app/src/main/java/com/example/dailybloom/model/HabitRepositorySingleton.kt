package com.example.dailybloom.model

// гарантирует, что все части приложения работают с одним и тем же репозиторием

object HabitRepositorySingleton {
    val repository by lazy { HabitRepository() }
}