package com.example.dailybloom.view

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.dailybloom.R
import com.example.dailybloom.databinding.ActivityMainBinding
import com.example.dailybloom.model.Habit

class MainActivity : AppCompatActivity(),
    HabitTrackerFragment.HabitFragmentListener,
    CreateHabitFragment.CreateHabitListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager

        if (savedInstanceState == null) { // проверяем, что активность создается впервые (не воссоздается после поворота экрана)
            replaceFragment(HabitTrackerFragment.newInstance())
        }
    }

    private fun replaceFragment(fragment: Fragment) { //инкапсуляция логики для перехода между фрагментами
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_main, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onCreateNewHabit() {
        replaceFragment(CreateHabitFragment.newInstance())
    }

    override fun onEditHabit(habit: Habit) {
        replaceFragment(CreateHabitFragment.newInstance(habit))
    }

    override fun onHabitSaved() {
        supportFragmentManager.popBackStack() // сохранение возвращает к предыдущему фрагменту
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Выход")
                .setMessage("Закрыть приложение?")
                .setPositiveButton("Да") { _, _ -> finish() }
                .setNegativeButton("Нет", null)
                .show()
        }
    }
}


