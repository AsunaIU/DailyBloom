package com.example.dailybloom.domain.usecase

import com.example.dailybloom.domain.model.Habit
import com.example.dailybloom.domain.repository.HabitRepository
import javax.inject.Inject

class UpdateHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: String, habit: Habit): Result<Boolean> {
        return habitRepository.updateHabit(habitId, habit)
    }
}
