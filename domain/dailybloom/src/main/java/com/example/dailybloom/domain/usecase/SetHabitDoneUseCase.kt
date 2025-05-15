package com.example.dailybloom.domain.usecase

import com.example.dailybloom.domain.repository.HabitRepository
import javax.inject.Inject

class SetHabitDoneUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: String): Result<Boolean> {
        return habitRepository.setHabitDone(habitId)
    }
}