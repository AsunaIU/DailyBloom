package com.example.dailybloom.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dailybloom.R
import com.example.dailybloom.databinding.ActivityMainBinding
import com.example.dailybloom.model.Habit
import com.example.dailybloom.util.Constants
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(),
    HabitViewPagerFragment.HabitFragmentListener,
    CreateHabitFragment.CreateHabitListener,
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.habitViewPagerFragment, R.id.infoFragment),
            binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        binding.navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.habitViewPagerFragment, R.id.infoFragment -> {
                navController.navigate(item.itemId)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateNewHabit() {
        navController.navigate(R.id.createHabitFragment)
    }

    override fun onEditHabit(habit: Habit) {
        val bundle = Bundle().apply {
            putParcelable(Constants.ARG_HABIT, habit)
        }
        navController.navigate(R.id.createHabitFragment, bundle)
    }

    override fun onHabitSaved() {
        navController.navigateUp()
    }

    override fun onHabitDeleted() {
        navController.navigateUp()
    }
}

