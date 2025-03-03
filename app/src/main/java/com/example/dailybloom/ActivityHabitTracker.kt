package com.example.dailybloom

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.example.dailybloom.viewmodel.HabitTrackerViewModel
import androidx.appcompat.app.AppCompatActivity
import com.example.dailybloom.databinding.ActivityHabitTrackerBinding

class ActivityHabitTracker : AppCompatActivity() {

    private var _binding: ActivityHabitTrackerBinding? = null
    private val binding: ActivityHabitTrackerBinding
        get() = _binding ?: throw IllegalStateException("Binding is null")

    // ViewModel для логики списка привычек (реализацию можно доработать)
    private val viewModel: HabitTrackerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHabitTrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupFAB()
    }

    private fun setupFAB() {
        binding.fab.setOnClickListener {
            startActivity(Intent(this, ActivityCreateHabit::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null // Очистка binding для избежания утечек памяти
    }
}