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
data class UIState(
    val title: String = "",
    val description: String = "",
    val priorityPos: Int = 1,
    val typeId: Int = R.id.rbHabitGood,
    val frequency: String = "1",
    val periodicityPos: Int = 0,
    val selectedColor: Int = Color.WHITE
) : Parcelable

class HabitEditViewModel(private val handle: SavedStateHandle) : ViewModel(), HabitChangeListener {

    private val currentHabit = handle.get<Habit>("current_habit")
    private val _uiState = MutableLiveData(UIState()) // создаётся объект UIState с значениями по умолчанию
    val uiState: LiveData<UIState> = _uiState

    private val _habits = MutableLiveData(HabitRepository.getHabits())
    val habits: LiveData<Map<String, Habit>> = _habits

    init {
        HabitRepository.addListener(this)
    }

    fun setUIState(state: UIState) {  // устанавливаем новое состояние UI вместо текущего
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
        val current = _uiState.value ?: UIState() // current не null – либо используется текущее состояние, либо создаётся новое с дефолтными значениями
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

//    Надо удалить если нет ошибок
//    private fun getPeriodicity(periodicityPos: Int): String {
//        return when (periodicityPos) {
//            0 -> "Day"
//            1 -> "Week"
//            2 -> "Month"
//            else -> "Day"
//        }
//    }

    override fun onHabitsChanged(habits: Map<String, Habit>) {
        _habits.postValue(habits)
    }

    override fun onCleared() {
        super.onCleared()
        HabitRepository.removeListener(this)
    }
}