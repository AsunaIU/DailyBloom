package com.example.domain.usecase

import com.example.domain.repository.HabitRepository
import javax.inject.Inject

class SyncHabitsUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke() {
        habitRepository.syncWithServer()
    }
}