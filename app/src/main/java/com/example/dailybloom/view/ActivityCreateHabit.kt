package com.example.dailybloom.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.dailybloom.R
import com.example.dailybloom.databinding.ActivityCreateHabitBinding
import com.example.dailybloom.model.Habit
import com.example.dailybloom.viewmodel.HabitEditViewModel
import com.example.dailybloom.viewmodel.UIState


class ActivityCreateHabit : AppCompatActivity() {

    private val viewModel: HabitEditViewModel by viewModels()
    private lateinit var binding: ActivityCreateHabitBinding
    private var currentHabit: Habit? = null

    companion object {
        private const val KEY_UI_STATE = "ui_state"
        private const val TAG = "ActivityCreateHabit"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateHabitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleIntent()
        restoreState(savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun handleIntent() {
        currentHabit = intent.getParcelableExtra("HABIT")
        currentHabit?.let {
            viewModel.setUIState(UIState(
                title = it.title,
                description = it.description,
                priorityPos = when (it.priority) {
                    "High" -> 0
                    "Medium" -> 1
                    else -> 2
                },
                typeId = if (it.type == "Good") R.id.rbHabitGood else R.id.rbHabitBad,
                frequency = it.frequency.toString(),
                periodicityPos = resources.getStringArray(R.array.periodicity_options)
                    .indexOf(it.periodicity).coerceAtLeast(0),
                selectedColor = it.color
            ))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.uiState.value?.let {
            outState.putParcelable(KEY_UI_STATE, it)
        }
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.getParcelable<UIState>(KEY_UI_STATE)?.let {
            viewModel.setUIState(it)
        }
    }

    private fun setupObservers() {
        viewModel.uiState.observe(this) { state ->
            updateUI(state)
        }
    }

    private fun updateUI(state: UIState) {
        with(binding) {
            if (!etHabitTitle.hasFocus()) etHabitTitle.setText(state.title)
            if (!etHabitDescription.hasFocus()) etHabitDescription.setText(state.description)
            if (!etHabitFrequency.hasFocus()) etHabitFrequency.setText(state.frequency)

            spinnerPriority.setSelection(state.priorityPos)
            rgHabitType.check(state.typeId)
            spinnerFrequencyUnit.setSelection(state.periodicityPos)
            colorPicker.setSelectedColor(state.selectedColor)
        }
    }

    private fun setupUI() {
        with(binding) {
            etHabitTitle.doAfterTextChanged {
                if (etHabitTitle.hasFocus()) {
                    viewModel.updateUIState(title = it.toString())
                }
            }

            etHabitDescription.doAfterTextChanged {
                if (etHabitDescription.hasFocus()) {
                    viewModel.updateUIState(description = it.toString())
                }
            }

            etHabitFrequency.doAfterTextChanged {
                if (etHabitFrequency.hasFocus()) {
                    viewModel.updateUIState(frequency = it.toString())
                }
            }

            spinnerPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.updateUIState(priorityPos = position)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            spinnerFrequencyUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.updateUIState(periodicityPos = position)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            rgHabitType.setOnCheckedChangeListener { _, checkedId ->
                viewModel.updateUIState(typeId = checkedId)
            }

            colorPicker.setOnColorSelectedListener { color ->
                viewModel.updateColor(color)
            }

            btnSaveHabit.setOnClickListener {
                if (saveHabit()) {
                    finish()
                }
            }
        }
    }

    private fun saveHabit(): Boolean {
        val success = viewModel.saveHabit(currentHabit?.id)

        if (!success) {
            with(binding) {
                val state = viewModel.uiState.value ?: return false
                if (state.title.isBlank()) etHabitTitle.error = "Enter a title"
                if (state.frequency.isBlank()) etHabitFrequency.error = "Enter frequency"
            }
            Log.e(TAG, "Failed to save habit")
        }

        return success
    }
}