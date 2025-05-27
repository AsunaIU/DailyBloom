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
    val doneDates: List<Long> = emptyList()
) {

    val done: Boolean
        get() = getCompletionsInCurrentPeriod() >= frequency

    fun getCompletionsInCurrentPeriod(): Int {
        val currentTime = System.currentTimeMillis()
        val periodStart = when (periodicity) {
            Periodicity.DAY -> getStartOfDay(currentTime)
            Periodicity.WEEK -> getStartOfWeek(currentTime)
            Periodicity.MONTH -> getStartOfMonth(currentTime)
        }

        return doneDates.count { it >= periodStart }
    }

    fun getRemainingCompletions(): Int {
        return maxOf(0, frequency - getCompletionsInCurrentPeriod())
    }

    fun canDoMore(): Boolean {
        return getCompletionsInCurrentPeriod() < frequency
    }

    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getStartOfWeek(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getStartOfMonth(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}