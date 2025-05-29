package com.example.domain.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class HabitTest {

    private val sampleHabit = Habit(
        id = "test id",
        title = "Test Habit",
        description = "",
        priority = Priority.MEDIUM,
        type = HabitType.GOOD,
        frequency = 1,
        periodicity = Periodicity.MONTH,
        color = 1,
        createdAt = System.currentTimeMillis(),
        doneDates = emptyList()
    )

    @Test
    fun `habit model should calculate done status correctly`() {

        val currentTime = System.currentTimeMillis()
        val twoDaysMs = TimeUnit.DAYS.toMillis(2)
        val tenDaysMs = TimeUnit.DAYS.toMillis(10)

        val habit = sampleHabit.copy(
            frequency = 2,
            doneDates = listOf(
                currentTime - twoDaysMs,  // 2 дня назад
                currentTime - tenDaysMs   // 10 дней назад
            )
            // doneDates = listOf(currentTime - 1000, currentTime - 3000) // 1000 мс = 1 сек
        )

        // Проверка: привычка считается выполненной
        val expectedDone = true
        val actualDone = habit.done
        Assertions.assertEquals(expectedDone, actualDone)

        // Проверка: число выполнений в текущем периоде
        val expectedCompletions = 2
        val actualCompletions = habit.getCompletionsInCurrentPeriod()
        Assertions.assertEquals(expectedCompletions, actualCompletions)

        // Проверка: сколько еще требуется выполнить
        val expectedRemaining = 0
        val actualRemaining = habit.getRemainingCompletions()
        Assertions.assertEquals(expectedRemaining, actualRemaining)

        // Проверка: можно сделать больше
        val expectedCanDoMore = false
        val actualCanDoMore = habit.canDoMore()
        Assertions.assertEquals(expectedCanDoMore, actualCanDoMore)
    }
}