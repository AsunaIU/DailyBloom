package com.example.dailybloom.view

import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dailybloom.R
import com.example.dailybloom.databinding.ActivityCreateHabitBinding
import com.example.dailybloom.model.Habit
import com.example.dailybloom.viewmodel.HabitEditViewModel
import kotlinx.parcelize.Parcelize


class ActivityCreateHabit : AppCompatActivity() {

    private lateinit var viewModel: HabitEditViewModel
    private lateinit var binding: ActivityCreateHabitBinding
    private var currentHabit: Habit? = null

    companion object {
        private const val KEY_UI_STATE = "ui_state"
        private const val TAG = "ActivityCreateHabit"
    }

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

    private var uiState = UIState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[HabitEditViewModel::class.java]

        binding = ActivityCreateHabitBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        viewModel.habits.observe(this) { habits ->
//            Log.d(TAG, "Habits updated: ${habits.size} habits")
//            habits.forEach { (id, habit) ->
//                Log.d(TAG, "Habit: $id - ${habit.title}")
//            }
//        }

        handleIntent()
        restoreState(savedInstanceState)
        setupUI()
        setupColorPicker()
        setupSaveButton()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.selectedColor.observe(this) { color ->
            binding.colorPicker.setSelectedColor(color)
        }

        viewModel.habits.observe(this) { habits ->
            Log.d(TAG, "Habits updated: ${habits.size} habits")
        }
    }

    private fun handleIntent() {
        currentHabit = intent.getParcelableExtra("HABIT")
        currentHabit?.let {
            uiState = UIState(
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
            )
            viewModel.selectColor(it.color)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_UI_STATE, uiState)
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.getParcelable<UIState>(KEY_UI_STATE)?.let {
            uiState = it
            viewModel.selectColor(it.selectedColor)
        }
    }

//    private fun updateSelectedColorDisplay(color: Int) {
//        with(binding) {
//            selectedColorView.setBackgroundColor(color)
//
//            val red = Color.red(color)
//            val green = Color.green(color)
//            val blue = Color.blue(color)
//
//            val hsv = FloatArray(3)
//            Color.colorToHSV(color, hsv)
//            val hsvText = "HSV: %.1f°, %.1f%%, %.1f%%".format(hsv[0], hsv[1]*100, hsv[2]*100)
//
//            colorValuesTextView.text = "RGB: $red, $green, $blue\n$hsvText"
//        }
//    }

    private fun setupUI() {
        with(binding) {
            etHabitTitle.setText(uiState.title)
            etHabitDescription.setText(uiState.description)
            spinnerPriority.setSelection(uiState.priorityPos)
            rgHabitType.check(uiState.typeId)
            etHabitFrequency.setText(uiState.frequency)
            spinnerFrequencyUnit.setSelection(uiState.periodicityPos)
        }
    }

    private fun setupColorPicker() {
        binding.colorPicker.setOnColorSelectedListener { color ->
            uiState = uiState.copy(selectedColor = color)
            viewModel.selectColor(color)
        }
    }

    private fun collectCurrentState(): UIState {
        return with(binding) {
            UIState(
                title = etHabitTitle.text.toString(),
                description = etHabitDescription.text.toString(),
                priorityPos = spinnerPriority.selectedItemPosition,
                typeId = rgHabitType.checkedRadioButtonId,
                frequency = etHabitFrequency.text.toString(),
                periodicityPos = spinnerFrequencyUnit.selectedItemPosition,
                selectedColor = uiState.selectedColor
            )
        }
    }

    private fun setupSaveButton() {
        binding.btnSaveHabit.setOnClickListener {
            if (validateInput()) {
                uiState = collectCurrentState()
                saveHabit()
                finish()
            }
        }
    }

    private fun saveHabit() {
        val periodicity = resources.getStringArray(R.array.periodicity_options)
            .getOrElse(uiState.periodicityPos) { "Day" }

        val isGoodHabit = uiState.typeId == R.id.rbHabitGood

        val frequency = uiState.frequency.toIntOrNull() ?: 1

        val success = viewModel.createOrUpdateHabit(
            currentHabitId = currentHabit?.id,
            title = uiState.title,
            description = uiState.description,
            priorityPos = uiState.priorityPos,
            isGoodHabit = isGoodHabit,
            frequency = frequency,
            periodicity = periodicity,
            color = uiState.selectedColor
        )

        if (!success) {
            Log.e(TAG, "Failed to save habit")
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true
        with(binding) {
            if (etHabitTitle.text.isNullOrBlank()) {
                etHabitTitle.error = "Enter a title"
                isValid = false
            }
            if (etHabitFrequency.text.isNullOrBlank()) {
                etHabitFrequency.error = "Enter frequency"
                isValid = false
            }
        }
        return isValid
    }
}


