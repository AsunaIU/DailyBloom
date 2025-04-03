package com.example.dailybloom.viewmodel

import android.graphics.Color
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.dailybloom.R
import com.example.dailybloom.model.Habit
import com.example.dailybloom.model.HabitChangeListener
import com.example.dailybloom.model.HabitRepository
import com.example.dailybloom.model.HabitType
import com.example.dailybloom.model.Periodicity
import com.example.dailybloom.model.Priority
import kotlinx.parcelize.Parcelize

@Parcelize
data class UiHabit(
    val title: String = "",
    val description: String = "",
    val priorityPos: Int = Priority.MEDIUM.ordinal,
    val typeId: Int = R.id.rbHabitGood,
    val frequency: String = "1",
    val periodicityPos: Int = Periodicity.DAY.ordinal,
    val selectedColor: Int = Color.WHITE
) : Parcelable

class HabitEditViewModel(handle: SavedStateHandle) : ViewModel(), HabitChangeListener {

    private val _uiState = MutableLiveData(UiHabit()) // создаётся объект UIState с значениями по умолчанию
    val uiState: LiveData<UiHabit> = _uiState

    private val _habits = MutableLiveData(HabitRepository.getHabits())
    val habits: LiveData<Map<String, Habit>> = _habits

    private var currentHabit: Habit? = null

    init {
        HabitRepository.addListener(this)
        handle.get<Habit>("habit")?.let {
            setCurrentHabit(it)
        }
    }

    fun setCurrentHabit(habit: Habit) {
        currentHabit = habit
        _uiState.value = habit.toUiHabit()
    }

    private fun Habit.toUiHabit(): UiHabit {
        return UiHabit(
            title = this.title,
            description = this.description,
            priorityPos = this.priority.ordinal,
            typeId = if (this.type == HabitType.GOOD) R.id.rbHabitGood else R.id.rbHabitBad,
            frequency = this.frequency.toString(),
            periodicityPos = this.periodicity.ordinal,
            selectedColor = this.color
        )
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
        if (!validateInput()) return false

        val state = _uiState.value ?: return false

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
        if (currentHabitId == null) {
            HabitRepository.addHabit(habit)
        } else {
            HabitRepository.updateHabit(currentHabitId, habit)
        }

        return true
    }

    override fun onHabitsChanged(habits: Map<String, Habit>) {
        _habits.postValue(habits)
    }

    override fun onCleared() {
        super.onCleared()
        HabitRepository.removeListener(this)
    }
}