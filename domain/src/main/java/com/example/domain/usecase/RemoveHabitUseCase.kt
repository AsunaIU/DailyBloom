package com.example.domain.usecase

import com.example.domain.repository.HabitRepository
import javax.inject.Inject

class RemoveHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: String): Result<Boolean> {
        return habitRepository.removeHabit(habitId)
    }
}