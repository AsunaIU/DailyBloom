package com.example.dailybloom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailybloom.R
import com.example.dailybloom.model.Habit
import com.example.dailybloom.data.local.HabitRepository
import com.example.dailybloom.model.HabitType
import com.example.dailybloom.model.Periodicity
import com.example.dailybloom.model.Priority
import com.example.dailybloom.viewmodel.viewmodeldata.UiHabit
import kotlinx.coroutines.launch

class HabitEditViewModel(handle: SavedStateHandle) : ViewModel() {

    private val _uiState = MutableLiveData(UiHabit()) // создаётся объект UIState с значениями по умолчанию
    val uiState: LiveData<UiHabit> = _uiState

    private val _operationStatus = MutableLiveData<OperationStatus>()
    val operationStatus: LiveData<OperationStatus> = _operationStatus

    val habits: LiveData<Map<String, Habit>> = HabitRepository.habits

    private var currentHabit: Habit? = null

    init {
        handle.get<Habit>("habit")?.let {
            setCurrentHabit(it)
        }
    }

    fun setCurrentHabit(habit: Habit) {
        currentHabit = habit
        _uiState.value = Habit.toUiHabit(habit)
    }

    fun setUIState(state: UiHabit) {  // устанавливаем новое состояние UI вместо текущего
        _uiState.value = state
    }

    fun updateColor(color: Int) {
        _uiState.value = _uiState.value?.copy(selectedColor = color)
    }

    fun updateUIState(
        title: String? = null,
        description: String? = null,
        priorityPos: Int? = null,
        typeId: Int? = null,
        frequency: String? = null,
        periodicityPos: Int? = null
    ) {
        val current = _uiState.value ?: UiHabit() // current не null – либо используется текущее состояние, либо создаётся новое с дефолтными значениями
        _uiState.value = current.copy(
            title = title ?: current.title,
            description = description ?: current.description,
            priorityPos = priorityPos ?: current.priorityPos,
            typeId = typeId ?: current.typeId,
            frequency = frequency ?: current.frequency,
            periodicityPos = periodicityPos ?: current.periodicityPos
        )
    }

    private fun validateInput(): Boolean {
        val state = _uiState.value ?: return false
        return state.title.isNotBlank() && state.frequency.isNotBlank() && state.frequency.toIntOrNull() != null
    }

    fun saveHabit(currentHabitId: String?): Boolean { // nullable-тип String? разделяет два сценария (создание новой привычки/обновление существующей)

        if (!validateInput()) {
            _operationStatus.value = OperationStatus.Error("Invalid input data")
            return false
        }

        val state = _uiState.value ?: run {
            _operationStatus.value = OperationStatus.Error("UI state is null")
            return false
        }

        _operationStatus.value = OperationStatus.InProgress

        val priority = Priority.entries[state.priorityPos]
        val type = if (state.typeId == R.id.rbHabitGood) HabitType.GOOD else HabitType.BAD
        val periodicity = Periodicity.entries[state.periodicityPos]
        val frequency = state.frequency.toIntOrNull() ?: 1

        val habit = if (currentHabitId != null) { // обновляется привычка с указанным id
            Habit(
                id = currentHabitId,
                title = state.title,
                description = state.description,
                priority = priority,
                type = type,
                frequency = frequency,
                periodicity = periodicity,
                color = state.selectedColor
            )
        } else { // создается новая привычка (id генерируется при создании экземпляра Habit "UUID.randomUUID().toString()")
            Habit(
                title = state.title,
                description = state.description,
                priority = priority,
                type = type,
                frequency = frequency,
                periodicity = periodicity,
                color = state.selectedColor
            )
        }

        viewModelScope.launch {
            val result = if (currentHabitId == null) {
                HabitRepository.addHabit(habit)
            } else {
                HabitRepository.updateHabit(currentHabitId, habit)
            }

            _operationStatus.value = if (result) {
                OperationStatus.Success
            } else {
                OperationStatus.Error("Failed to save habit")
            }
        }
        return true
    }

    fun deleteHabit(habitId: String) {
        _operationStatus.value = OperationStatus.InProgress

        viewModelScope.launch {
            val result = HabitRepository.removeHabit(habitId)

            _operationStatus.value = if (result) {
                OperationStatus.Success
            } else {
                OperationStatus.Error("Failed to delete habit")
            }
        }
    }

    sealed class OperationStatus {
        object InProgress : OperationStatus()
        object Success : OperationStatus()
        data class Error(val message: String) : OperationStatus()
    }
}