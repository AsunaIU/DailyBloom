package com.example.dailybloom.viewmodel

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dailybloom.model.Habit
import com.example.dailybloom.model.HabitChangeListener
import com.example.dailybloom.model.HabitRepository

class HabitEditViewModel : ViewModel(), HabitChangeListener {

    private val _habits = MutableLiveData(HabitRepository.getHabits())
    val habits: LiveData<Map<String, Habit>> = _habits
    private val _selectedColor = MutableLiveData<Int>()
    val selectedColor: LiveData<Int> = _selectedColor

    private val _colorOptions = MutableLiveData<List<Int>>()
    val colorOptions: LiveData<List<Int>> = _colorOptions

    private val _colorInfo = MutableLiveData<Pair<Int, String>>()
    val colorInfo: LiveData<Pair<Int, String>> = _colorInfo

    init {
        HabitRepository.addListener(this)
        _selectedColor.value = Color.BLUE
        updateColorInfo(_selectedColor.value ?: Color.BLUE)
    }

    fun addHabit(habit: Habit) {
        HabitRepository.addHabit(habit)
    }

    fun updateHabit(id: String, habit: Habit) {
        HabitRepository.updateHabit(id, habit)
    }

    fun selectColor(color: Int) {
        _selectedColor.value = color
        updateColorInfo(color)
    }

    private fun updateColorInfo(color: Int) {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        val hsvText = "HSV: %.1f°, %.1f%%, %.1f%%".format(hsv[0], hsv[1]*100, hsv[2]*100)

        val rgbText = "RGB: $red, $green, $blue"
        val colorText = "$rgbText\n$hsvText"

        _colorInfo.value = Pair(color, colorText)
    }

    fun generateColorSpectrum(count: Int) {
        val colors = List(count) { i ->
            val hue = (i.toFloat() / count) * 360f
            Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
        }
        _colorOptions.value = colors
    }

    fun createOrUpdateHabit(
        currentHabitId: String?,
        title: String,
        description: String,
        priorityPos: Int,
        isGoodHabit: Boolean,
        frequency: Int,
        periodicity: String,
        color: Int
    ): Boolean {
        if (title.isBlank() || frequency <= 0) {
            return false
        }

        val priority = when (priorityPos) {
            0 -> "High"
            1 -> "Medium"
            else -> "Low"
        }

        val type = if (isGoodHabit) "Good" else "Bad"

        val habit = if (currentHabitId != null) {
            Habit(
                id = currentHabitId,
                title = title,
                description = description,
                priority = priority,
                type = type,
                frequency = frequency,
                periodicity = periodicity,
                color = color
            )
        } else {
            Habit(
                title = title,
                description = description,
                priority = priority,
                type = type,
                frequency = frequency,
                periodicity = periodicity,
                color = color
            )
        }

        if (currentHabitId == null) {
            addHabit(habit)
        } else {
            updateHabit(currentHabitId, habit)
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