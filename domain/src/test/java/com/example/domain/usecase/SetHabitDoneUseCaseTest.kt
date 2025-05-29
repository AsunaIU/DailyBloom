package com.example.domain.usecase

import com.example.domain.repository.HabitRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any
import org.mockito.kotlin.eq


class SetHabitDoneUseCaseTest {

    private val habitRepository: HabitRepository = mock()
    private lateinit var setHabitDoneUseCase: SetHabitDoneUseCase

    @Test
    fun `setHabitDoneUseCase should call repository with correct habitId and return success`()
    = runTest {

        setHabitDoneUseCase = SetHabitDoneUseCase(habitRepository)

        val habitId = "test-habit-id"
        val expectedResult = Result.success(true)

        // передаем 2 параметра для вызова:
        whenever(habitRepository.setHabitDone(eq(habitId), any<Long>()))
            .thenReturn(expectedResult)

        val result = setHabitDoneUseCase.invoke(habitId)

        verify(habitRepository).setHabitDone(eq(habitId), any<Long>())

        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }
}