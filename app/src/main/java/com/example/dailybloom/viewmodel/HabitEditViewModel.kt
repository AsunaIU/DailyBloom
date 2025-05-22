package com.example.dailybloom.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailybloom.R
import com.example.dailybloom.util.Constants
import com.example.dailybloom.viewmodel.viewmodeldata.HabitMapper
import com.example.dailybloom.viewmodel.viewmodeldata.UiHabit
import com.example.domain.model.Habit
import com.example.domain.model.HabitType
import com.example.domain.model.Periodicity
import com.example.domain.model.Priority
import com.example.domain.usecase.AddHabitUseCase
import com.example.domain.usecase.GetHabitsUseCase
import com.example.domain.usecase.RemoveHabitUseCase
import com.example.domain.usecase.SetHabitDoneUseCase
import com.example.domain.usecase.UpdateHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitEditViewModel @Inject constructor(
    private val getHabitsUseCase: GetHabitsUseCase,
    private val addHabitUseCase: AddHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val removeHabitUseCase: RemoveHabitUseCase,
    private val setHabitDoneUseCase: SetHabitDoneUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // StateFlow instead of LiveData
    private val _uiState = MutableStateFlow(UiHabit())
    val uiState: StateFlow<UiHabit> = _uiState.asStateFlow()

    // StateFlow for operation status
    private val _operationStatus = MutableStateFlow<OperationStatus?>(null)
    val operationStatus: StateFlow<OperationStatus?> = _operationStatus.asStateFlow()

    // ID редактируемой привычки, null - если создается новая привычка
    private val habitId: String? = savedStateHandle.get<String>(Constants.ARG_HABIT_ID)

    // Flow to collect habits from repository
    private val _habits = MutableStateFlow<Map<String, Habit>>(emptyMap())
    val habits: StateFlow<Map<String, Habit>> = _habits.asStateFlow()

    init {
        loadHabits()    // сбор всей мапы привычек из репозитория через Flow -> в _habits: StateFlow<Map<String, Habit>>
        loadHabitById() // если habitId != null, корутина «подсветит» в _uiState привычку по ID
    }

    // Load habits from repository using Flow
    private fun loadHabits() {
        viewModelScope.launch {
            getHabitsUseCase().collect { habitsMap ->
                _habits.value = habitsMap
            }
        }
    }

    // Загружаем привычку по идентификатору из репозитория, если идентификатор доступен
    private fun loadHabitById() {
        habitId?.let { id ->
            viewModelScope.launch {
                getHabitsUseCase().collect { habitsMap ->
                    habitsMap[id]?.let { habit ->
                        val newUiState = HabitMapper.toUiHabit(habit)
                        if (_uiState.value != newUiState) {
                            _uiState.value = newUiState
                        }
                    }
                }
            }
        }
    }

    fun setUIState(state: UiHabit) {  // устанавливаем новое состояние UI вместо текущего
        _uiState.value = state
    }

    fun updateColor(color: Int) {
        _uiState.value = _uiState.value.copy(selectedColor = color)
    }

    fun updateDoneStatus(done: Boolean) {
        _uiState.value = _uiState.value.copy(done = done)
    }

    fun updateUIState(
        title: String? = null,
        description: String? = null,
        priorityPos: Int? = null,
        typeId: Int? = null,
        frequency: String? = null,
        periodicityPos: Int? = null,
        done: Boolean? = null,
    ) {
        val current = _uiState.value
        // current не null – либо используется текущее состояние, либо создаётся новое с дефолтными значениями

        _uiState.value = current.copy(
            title = title ?: current.title,
            description = description ?: current.description,
            priorityPos = priorityPos ?: current.priorityPos,
            typeId = typeId ?: current.typeId,
            frequency = frequency ?: current.frequency,
            periodicityPos = periodicityPos ?: current.periodicityPos,
            done = done ?: current.done,
        )
    }

    private fun validateInput(): Boolean {
        val state = _uiState.value
        return state.title.isNotBlank() && state.frequency.isNotBlank() && state.frequency.toIntOrNull() != null
    }

    fun saveHabit() { // nullable-тип String? разделяет два сценария (создание новой привычки/обновление существующей)

        if (!validateInput()) {
            _operationStatus.value = OperationStatus.Error("Invalid input data")
            return
        }
        _operationStatus.value = OperationStatus.InProgress

        val state = _uiState.value

        val priority = Priority.entries[state.priorityPos]
        val type = if (state.typeId == R.id.rbHabitGood) HabitType.GOOD else HabitType.BAD
        val periodicity = Periodicity.entries[state.periodicityPos]
        val frequency = state.frequency.toIntOrNull() ?: 1

        val habit = Habit( // обновляется привычка с указанным id
            id = habitId ?: "",
            title = state.title,
            description = state.description,
            priority = priority,
            type = type,
            frequency = frequency,
            periodicity = periodicity,
            color = state.selectedColor,
            done = state.done
        )

        viewModelScope.launch {
            val result = if (habitId == null) {
                addHabitUseCase(habit)
            } else {
                updateHabitUseCase(habitId, habit)
            }
            Log.d("ViewModel", "$result")

            _operationStatus.value = if (result.isSuccess) OperationStatus.Success
            else OperationStatus.Error("Failed to save habit")
        }
    }

    fun deleteHabit() {
        if (habitId == null) {
            _operationStatus.value =
                OperationStatus.Error("Cannot delete a habit that hasn't been saved")
            return
        }

        _operationStatus.value = OperationStatus.InProgress

        viewModelScope.launch {
            val result = removeHabitUseCase(habitId)

            _operationStatus.value = if (result.isSuccess) OperationStatus.Success
            else OperationStatus.Error("Failed to delete habit")
        }
    }

    fun setHabitDone() {
        if (habitId == null) {
            _operationStatus.value =
                OperationStatus.Error("Cannot mark a habit as done that hasn't been saved")
            return
        }

        _operationStatus.value = OperationStatus.InProgress

        viewModelScope.launch {
            updateDoneStatus(true)

            val result = setHabitDoneUseCase(habitId)

            _operationStatus.value = if (result.isSuccess) OperationStatus.Success
            else OperationStatus.Error("Failed to mark habit as done")
        }
    }

    // Сбрасывает статус операции, чтобы избежать повторных срабатываний при пересоздании UI
    fun resetOperationStatus() {
        _operationStatus.value = null
    }

    sealed class OperationStatus {
        object InProgress : OperationStatus()
        object Success : OperationStatus()
        data class Error(val message: String) : OperationStatus()
    }
}