package com.example.dailybloom.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.core.view.children
import com.example.dailybloom.R
import com.example.dailybloom.databinding.ActivityCreateHabitBinding
import com.example.dailybloom.model.Habit
import com.example.dailybloom.model.HabitChangeListener
import com.example.dailybloom.model.HabitRepositorySingleton
import com.example.dailybloom.viewmodel.HabitViewModel
import com.example.dailybloom.viewmodel.HabitViewModelFactory
import kotlinx.parcelize.Parcelize


class ActivityCreateHabit : AppCompatActivity(), HabitChangeListener {

    private val repository = HabitRepositorySingleton.repository
    private lateinit var viewModel: HabitViewModel
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

        val factory = HabitViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HabitViewModel::class.java]

        binding = ActivityCreateHabitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository.addListener(this)

        viewModel.habits.observe(this) { habits ->
            Log.d(TAG, "Habits updated: ${habits.size} habits")
            habits.forEach { (id, habit) ->
                Log.d(TAG, "Habit: $id - ${habit.title}")
            }
        }

        handleIntent()
        restoreState(savedInstanceState)
        setupUI()
        setupColorPicker()
        setupSaveButton()
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
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_UI_STATE, uiState)
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.getParcelable<UIState>(KEY_UI_STATE)?.let {
            uiState = it
        }
    }

    private fun updateSelectedColorDisplay(color: Int) {
        with(binding) {
            selectedColorView.setBackgroundColor(color)

            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)

            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            val hsvText = "HSV: %.1f°, %.1f%%, %.1f%%".format(hsv[0], hsv[1]*100, hsv[2]*100)

            colorValuesTextView.text = "RGB: $red, $green, $blue\n$hsvText"
        }
    }

    private fun setupUI() {
        with(binding) {
            etHabitTitle.setText(uiState.title)
            etHabitDescription.setText(uiState.description)
            spinnerPriority.setSelection(uiState.priorityPos)
            rgHabitType.check(uiState.typeId)
            etHabitFrequency.setText(uiState.frequency)
            spinnerFrequencyUnit.setSelection(uiState.periodicityPos)

            layoutColorContainer.post {
                setupColorPicker()
            }

            layoutColorContainer.children.forEach { colorView ->
                colorView.setOnClickListener {
                    val color = (colorView.background as ColorDrawable).color
                    uiState = uiState.copy(selectedColor = color)
                    updateSelectedColorDisplay(color)
                }
            }

            updateSelectedColorDisplay(uiState.selectedColor)
        }
    }

    private fun setupColorPicker() {

        val colorGradientView = binding.colorGradientView
        val layoutColorContainer = binding.layoutColorContainer

        layoutColorContainer.removeAllViews()

        val squareSize = resources.getDimensionPixelSize(R.dimen.color_square_size)
        val squareMargin = (squareSize * 0.25).toInt()

        val squareCount = 16

        val gradientWidth = colorGradientView.width
        if (gradientWidth <= 0) {
            colorGradientView.post {
                setupColorPicker()
            }
            return
        }

        val step = gradientWidth / (squareCount + 1)

        for (i in 0 until squareCount) {
            val colorView = View(this)
            val layoutParams = LinearLayout.LayoutParams(squareSize, squareSize)
            layoutParams.setMargins(squareMargin, squareMargin, squareMargin, squareMargin)
            colorView.layoutParams = layoutParams

            val position = (i + 1) * step

            val hue = (position.toFloat() / gradientWidth) * 360f
            val hsv = floatArrayOf(hue, 1f, 1f)
            val color = Color.HSVToColor(hsv)

            colorView.setBackgroundColor(color)

            colorView.setOnClickListener {
                uiState = uiState.copy(selectedColor = color)
                updateSelectedColorDisplay(color)
            }

            layoutColorContainer.addView(colorView)
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
        val habit = currentHabit?.copy(
            title = uiState.title,
            description = uiState.description,
            priority = when (uiState.priorityPos) {
                0 -> "High"
                1 -> "Medium"
                else -> "Low"
            },
            type = if (uiState.typeId == R.id.rbHabitGood) "Good" else "Bad",
            frequency = uiState.frequency.toIntOrNull() ?: 1,
            periodicity = resources.getStringArray(R.array.periodicity_options)
                .getOrElse(uiState.periodicityPos) { "Day" },
            color = uiState.selectedColor
        ) ?: Habit(
            title = uiState.title,
            description = uiState.description,
            priority = when (uiState.priorityPos) {
                0 -> "High"
                1 -> "Medium"
                else -> "Low"
            },
            type = if (uiState.typeId == R.id.rbHabitGood) "Good" else "Bad",
            frequency = uiState.frequency.toIntOrNull() ?: 1,
            periodicity = resources.getStringArray(R.array.periodicity_options)
                .getOrElse(uiState.periodicityPos) { "Day" },
            color = uiState.selectedColor
        )

        Log.d(TAG, "Saving habit: ${habit.title}")

        if (currentHabit == null) {
            viewModel.addHabit(habit)
        } else {
            viewModel.updateHabit(currentHabit!!.id, habit)
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

    override fun onHabitsChanged(habits: Map<String, Habit>) {
        runOnUiThread {
            Log.d(TAG, "Habits Changed Callback: ${habits.size} habits")
            habits.forEach { (id, habit) ->
                Log.d(TAG, "Updated Habit: $id - ${habit.title}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        repository.removeListener(this)
    }
}
