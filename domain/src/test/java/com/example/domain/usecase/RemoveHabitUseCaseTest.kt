package com.example.domain.usecase

import com.example.domain.repository.HabitRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/** Тесты проверяют, что:
 *  1) при ошибке RemoveHabitUseCase.invoke() возвращает Result.failure с тем же исключением
 *  2) при успешном удалении в репозитории RemoveHabitUseCase.invoke возвращает Result.success(true)
 */

class RemoveHabitUseCaseTest {

    private val habitRepository: HabitRepository = mock()
    private lateinit var removeHabitUseCase: RemoveHabitUseCase

    @BeforeEach
    fun setUp() {
        removeHabitUseCase = RemoveHabitUseCase(habitRepository)
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {

        val habitId = "test-habit-id"
        val expectedError = Exception("Database error")
        whenever(habitRepository.removeHabit(habitId))
            .thenReturn(Result.failure(expectedError))

        val result = removeHabitUseCase.invoke(habitId)

        // Проверка: репозиторий был вызван именно с этим ID
        verify(habitRepository).removeHabit(habitId)

        // Проверка: результат –> ошибка
        Assertions.assertTrue(result.isFailure)

        // Проверка: на выходе ошибка не поменялась
        Assertions.assertEquals(expectedError, result.exceptionOrNull())
    }

    @Test
    fun `invoke returns success when repository succeeds`() = runTest {
        val habitId = "test-habit-id1"
        whenever(habitRepository.removeHabit(habitId))
            .thenReturn(Result.success(true))

        val result = removeHabitUseCase.invoke(habitId)

        // Проверка: репозиторий был вызван именно с этим ID
        verify(habitRepository).removeHabit(habitId)

        // Проверка: результат –> успешно
        Assertions.assertTrue(result.isSuccess)

        Assertions.assertEquals(true, result.getOrNull())
    }

}