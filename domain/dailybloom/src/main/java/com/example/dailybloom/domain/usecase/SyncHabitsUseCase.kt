package com.example.dailybloom.domain.usecase

import com.example.dailybloom.domain.repository.HabitRepository
import javax.inject.Inject

class SyncHabitsUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke() {
        habitRepository.syncWithServer()
    }
}