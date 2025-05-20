package com.example.domain.model

import java.util.UUID

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val priority: Priority,
    val type: HabitType,
    val frequency: Int,
    val periodicity: Periodicity,
    val color: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val done: Boolean = false
)