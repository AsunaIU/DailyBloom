package com.example.domain.usecase

import com.example.domain.model.Habit
import com.example.domain.model.Priority
import com.example.domain.repository.HabitRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class UpdateHabitUseCaseTest {

    private val habitRepository: HabitRepository = mock()
    private lateinit var updateHabitUseCase: UpdateHabitUseCase

    private val sampleHabit = Habit(
        id = "test-habit-id1",
        title = "Title",
        description = "",
        priority = Priority.MEDIUM,
        type = com.example.domain.model.HabitType.GOOD,
        frequency = 1,
        periodicity = com.example.domain.model.Periodicity.DAY,
        color = 0,
        createdAt = System.currentTimeMillis(),
        doneDates = emptyList()
    )

    @BeforeEach
    fun setUp() {
        updateHabitUseCase = UpdateHabitUseCase(habitRepository)
    }

    @Test
    fun `invoke returns success when repository updates habit`() = runTest {
        val habitId = "test-habit-id2"
        val updatedHabit = sampleHabit.copy(
            title = "Updated Title",
            description = "Updated description",
            priority = Priority.LOW,
            frequency = 3
        )
        whenever(habitRepository.updateHabit(habitId, updatedHabit))
            .thenReturn(Result.success(true))

        val result = updateHabitUseCase.invoke(habitId, updatedHabit)

        // Проверка: вызов репозитория с тем же id и тем же объектом
        verify(habitRepository).updateHabit(eq(habitId), eq(updatedHabit))

        // Проверка: UseCase возвращает true
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }
}