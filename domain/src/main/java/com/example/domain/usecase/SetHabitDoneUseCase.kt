package com.example.domain.usecase

import com.example.domain.repository.HabitRepository
import javax.inject.Inject

class SetHabitDoneUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: String): Result<Boolean> {
        return habitRepository.setHabitDone(habitId)
    }
}