package com.example.domain.usecase

import com.example.domain.model.Habit
import com.example.domain.model.HabitType
import com.example.domain.model.Periodicity
import com.example.domain.model.Priority
import com.example.domain.repository.HabitRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

/**
 * Тест проверяет работу AddHabitUseCase,
 * ожидаем что при вызове addHabitUseCase.invoke(sampleHabit) -> вернет Result.success(true)
 */


class AddHabitUseCaseTest {

    private val habitRepository: HabitRepository = mock()
    private lateinit var addHabitUseCase: AddHabitUseCase

    private val sampleHabit = Habit(
        id = "habit-id",
        title = "Test habit",
        description = "",
        priority = Priority.MEDIUM,
        type = HabitType.GOOD,
        frequency = 1,
        periodicity = Periodicity.DAY,
        color = 1,
        createdAt = System.currentTimeMillis(),
        doneDates = emptyList()
    )

    @Test
    fun `invoke() should call repository addHabit() and return success result`() = runTest {

        // Given: подготовили mock и входные данные
        addHabitUseCase = AddHabitUseCase(habitRepository)
        val expected = Result.success(true)
        whenever(habitRepository.addHabit(sampleHabit)).thenReturn(expected)


        // When
        val result = addHabitUseCase.invoke(sampleHabit)

        // Then
        verify(habitRepository).addHabit(sampleHabit) // проверяем что внутри UseCase был вызван метод

        Assertions.assertTrue(result.isSuccess)
        Assertions.assertEquals(true, result.getOrNull())
    }
}