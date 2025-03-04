package com.example.dailybloom.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dailybloom.R
import com.example.dailybloom.databinding.ActivityHabitTrackerBinding
import com.example.dailybloom.model.Habit
import com.example.dailybloom.viewmodel.HabitTrackerViewModel

class ActivityHabitTracker : AppCompatActivity() {
    private lateinit var binding: ActivityHabitTrackerBinding
    private lateinit var viewModel: HabitTrackerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHabitTrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация ViewModel
        viewModel = ViewModelProvider(this)[HabitTrackerViewModel::class.java]

        setupFAB()
        updateHabitList()
    }

    // Настройка кнопки добавления
    private fun setupFAB() {
        binding.fab.setOnClickListener {
            startActivity(Intent(this, ActivityCreateHabit::class.java))
        }
    }

    // Обновление списка привычек
    private fun updateHabitList() {
        binding.llHabitsContainer.removeAllViews()

        viewModel.habits.forEach { habit ->
            // Создание элемента списка
            val habitView = layoutInflater.inflate(
                R.layout.item_habit,
                binding.llHabitsContainer,
                false
            ).apply {
                // Заполнение данных
                findViewById<TextView>(R.id.tvTitle).text = habit.title
                findViewById<TextView>(R.id.tvDescription).text = habit.description
                findViewById<View>(R.id.colorIndicator).setBackgroundColor(habit.color)

                // Обработка клика для редактирования
                setOnClickListener {
                    openEditScreen(habit)
                }
            }
            binding.llHabitsContainer.addView(habitView)
        }
    }

    // Открытие экрана редактирования
    private fun openEditScreen(habit: Habit) {
        val intent = Intent(this, ActivityCreateHabit::class.java).apply {
            putExtra("HABIT", habit)
        }
        startActivity(intent)
    }

    // Обновление при возврате на экран
    override fun onResume() {
        super.onResume()
        updateHabitList()
    }
}