package com.example.dailybloom.view

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dailybloom.R
import com.example.dailybloom.databinding.ActivityCreateHabitBinding
import com.example.dailybloom.viewmodel.HabitTrackerViewModel
import com.example.dailybloom.model.Habit

class ActivityCreateHabit : AppCompatActivity() {
    private lateinit var binding: ActivityCreateHabitBinding
    private lateinit var viewModel: HabitTrackerViewModel
    private var currentHabit: Habit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateHabitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[HabitTrackerViewModel::class.java]

        currentHabit = (intent.getParcelableExtra("HABIT") ?: com.example.dailybloom.model.Habit()) as Habit?
        setupUI()
        setupSaveButton()
    }

    private fun setupUI() {
        with(binding) {
            etHabitTitle.setText(currentHabit?.title)
            etHabitDescription.setText(currentHabit?.description)

            // Простая инициализация Spinner
            spinnerPriority.setSelection(
                when (currentHabit?.priority) {
                    "High" -> 0
                    "Medium" -> 1
                    else -> 2
                }
            )
        }
    }

    private fun setupSaveButton() {
        binding.btnSaveHabit.setOnClickListener {
            if (binding.etHabitTitle.text?.isNotEmpty() == true) {
                saveHabit()
                finish()
            } else {
                binding.etHabitTitle.error = "Введите название"
            }
        }
    }

    private fun saveHabit() {
        // Валидация
        if (!validateInput()) return

        // Сбор данных
        val title = binding.etHabitTitle.text.toString()
        val description = binding.etHabitDescription.text.toString()
        val priority = binding.spinnerPriority.selectedItem.toString()
        val type = if (binding.rgHabitType.checkedRadioButtonId == R.id.rbHabitGood) "Good" else "Bad"
        val frequency = binding.etHabitFrequency.text.toString().toIntOrNull() ?: 1
        val periodicity = binding.spinnerFrequencyUnit.selectedItem.toString()
        val color = getSelectedColor()

        // Создание/обновление объекта
        val habit = currentHabit?.copy(
            title = title,
            description = description,
            priority = priority,
            type = type,
            frequency = frequency,
            periodicity = periodicity,
            color = color
        ) ?: Habit(
            title = title,
            description = description,
            priority = priority,
            type = type,
            frequency = frequency,
            periodicity = periodicity,
            color = color
        )

        // Сохранение
        if (currentHabit == null) {
            viewModel.addHabit(habit)
        } else {
            viewModel.updateHabit(habit)
        }

        finish()
    }

    private fun getSelectedColor(): Int {
        val colorHex = resources.getStringArray(R.array.color_values)[binding.spinnerColor.selectedItemPosition]
        return Color.parseColor(colorHex)
    }

    private fun validateInput(): Boolean {
        var isValid = true
        with(binding) {
            if (etHabitTitle.text.isNullOrBlank()) {
                etHabitTitle.error = "Введите название"
                isValid = false
            }
            if (etHabitFrequency.text.isNullOrBlank()) {
                etHabitFrequency.error = "Введите количество"
                isValid = false
            }
        }
        return isValid
    }
}