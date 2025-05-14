package com.example.dailybloom.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dailybloom.R
import com.example.dailybloom.model.Habit
import com.example.dailybloom.data.local.HabitRepository
import com.example.dailybloom.model.HabitType
import com.example.dailybloom.model.Periodicity
import com.example.dailybloom.model.Priority
import com.example.dailybloom.util.Constants
import com.example.dailybloom.viewmodel.viewmodeldata.UiHabit
import kotlinx.coroutines.launch

class HabitEditViewModel(handle: SavedStateHandle) : ViewModel() {

    private val _uiState = MutableLiveData(UiHabit()) // создаётся объект UIState с значениями по умолчанию
    val uiState: LiveData<UiHabit> = _uiState

    private val _operationStatus = MutableLiveData<OperationStatus?>()
    val operationStatus: LiveData<OperationStatus?> = _operationStatus

    // ID редактируемой привычки, null - если создается новая привычка
    private val habitId: String? = handle.get<String>(Constants.ARG_HABIT_ID)

    val habits: LiveData<Map<String, Habit>> =
        HabitRepository.habits.asLiveData(viewModelScope.coroutineContext)


    init {
        loadHabitById()  // Попытаемся загрузить привычку по ID, если мы редактируем существующую привычку
    }

    // Загружаем привычку по идентификатору из репозитория, если идентификатор доступен
    private fun loadHabitById() {
        habitId?.let { id ->
            habits.value?.get(id)?.let { habit ->
                _uiState.value = Habit.toUiHabit(habit)
            }
        }

        if (habitId != null) {
            viewModelScope.launch {
                habits.observeForever { habitsMap ->

                    habitId.let { id ->
                        habitsMap[id]?.let { updatedHabit ->
                            if (_uiState.value != Habit.toUiHabit(updatedHabit)) {
                                _uiState.value = Habit.toUiHabit(updatedHabit)
                            }
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
        val current = _uiState.value ?: UiHabit()
        // current не null – либо используется текущее состояние, либо создаётся новое с дефолтными значениями

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

    fun saveHabit() { // nullable-тип String? разделяет два сценария (создание новой привычки/обновление существующей)

        if (!validateInput()) {
            _operationStatus.value = OperationStatus.Error("Invalid input data")
            return
        }
        _operationStatus.value = OperationStatus.InProgress

        val state = _uiState.value ?: run {
            _operationStatus.value = OperationStatus.Error("UI state is null")
            return
        }

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
            color = state.selectedColor
        )

        viewModelScope.launch {
            val result = if (habitId == null) {
                HabitRepository.addHabit(habit)
            } else {
                HabitRepository.updateHabit(habitId, habit)
            }
            Log.d("ViewModel", "$result")

            _operationStatus.value =
                if (result) OperationStatus.Success
                else OperationStatus.Error("Failed to save habit")
        }
    }

    fun deleteHabit() {
        if (habitId == null) {
            _operationStatus.value = OperationStatus.Error("Cannot delete a habit that hasn't been saved")
            return
        }

        _operationStatus.value = OperationStatus.InProgress

        viewModelScope.launch {
            val result = HabitRepository.removeHabit(habitId)

            _operationStatus.value =
                if (result) OperationStatus.Success
                else OperationStatus.Error("Failed to delete habit")
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