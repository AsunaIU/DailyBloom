package com.example.dailybloom

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.example.dailybloom.viewmodel.CreateHabitViewModel
import androidx.appcompat.app.AppCompatActivity
import com.example.dailybloom.databinding.ActivityCreateHabitBinding
import com.example.dailybloom.viewmodel.Habit

class ActivityCreateHabit : AppCompatActivity() {

    private var _binding: ActivityCreateHabitBinding? = null
    private val binding: ActivityCreateHabitBinding
        get() = _binding ?: throw IllegalStateException("Binding is null")

    private val viewModel: CreateHabitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateHabitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        (intent?.getParcelableExtra<Habit>("habit"))?.let { existingHabit ->
            with(binding) {
                etHabitTitle.setText(existingHabit.title)
                etHabitDescription.setText(existingHabit.description)
            }
            viewModel.setHabit(existingHabit)
        }

        binding.btnSaveHabit.setOnClickListener {
            with(binding) {
                val title = etHabitTitle.text.toString().trim()
                if (title.isEmpty()) {
                    tilHabitTitle.error = "Заполните название привычки"
                    return@setOnClickListener
                }

                viewModel.saveHabit(
                    title = title,
                    description = etHabitDescription.text.toString().trim(),
                    priority = spinnerPriority.selectedItem.toString(),
                    type = spinnerType.selectedItem.toString(),
                    frequency = etHabitFrequency.text.toString().trim(),
                    color = spinnerColor.selectedItem.toString()
                )
            }
        }
    }

    private fun observeViewModel() {
        viewModel.habitSaved.observe(this) { saved ->
            if (saved) {
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
