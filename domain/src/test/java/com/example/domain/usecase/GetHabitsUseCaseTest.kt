package com.example.domain.usecase

import com.example.domain.model.Habit
import com.example.domain.model.HabitType
import com.example.domain.model.Periodicity
import com.example.domain.model.Priority
import com.example.domain.repository.HabitRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

/** Тест проверяет, что:
 *  при вызове invoke() вызывается именно habitRepository.getHabitsFlow()
 *  возвращаемый Flow эмитит те же данные, которые вернул репозиторий
*/

class GetHabitsUseCaseTest  {

    private val habitRepository: HabitRepository = mock()
    private lateinit var getHabitsUseCase: GetHabitsUseCase

    private val sampleHabit = Habit(
        id = "habit1",
        title = "First Habit",
        description = "",
        priority = Priority.MEDIUM,
        type = HabitType.GOOD,
        frequency = 1,
        periodicity = Periodicity.DAY,
        color = 1,
        createdAt = System.currentTimeMillis(),
        doneDates = emptyList()
    )

    @BeforeEach
    fun setUp() {
        getHabitsUseCase = GetHabitsUseCase(habitRepository)
    }

    @Test
    fun `invoke() should return the same data as in repository`() = runTest {

        val expectedHabits = mapOf(
            "habit1" to sampleHabit,
            "habit2" to sampleHabit.copy(id = "habit2", title = "Second Habit")
        )

        val expectedFlow = flowOf(expectedHabits)
        `when`(habitRepository.getHabitsFlow()).thenReturn(expectedFlow)

        // When
        val resultFlow = getHabitsUseCase.invoke()
        val actualData = resultFlow.first()

        // Then
        verify(habitRepository).getHabitsFlow()

        Assertions.assertEquals(expectedHabits, actualData)
        Assertions.assertEquals(2, actualData.size)
        Assertions.assertTrue(actualData.containsKey("habit1"))
        Assertions.assertTrue(actualData.containsKey("habit2"))
    }

    @Test
    fun `getHabitsUseCase should handle empty habits map correctly`() = runTest {

        val emptyHabitsMap = emptyMap<String, Habit>()
        val emptyFlow = flowOf(emptyHabitsMap)
        `when`(habitRepository.getHabitsFlow()).thenReturn(emptyFlow)

        val resultFlow = getHabitsUseCase.invoke()
        val emittedData = resultFlow.first()

        verify(habitRepository).getHabitsFlow()
        Assertions.assertTrue(emittedData.isEmpty())
        Assertions.assertEquals(0, emittedData.size)
    }

}