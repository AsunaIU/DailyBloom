package com.example.dailybloom.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.dailybloom.databinding.FragmentHabitViewPagerBinding
import com.example.dailybloom.model.Habit
import com.google.android.material.tabs.TabLayoutMediator

class HabitViewPagerFragment : Fragment(), HabitTrackerFragment.HabitFragmentListener {

    // вероятно лучше использовать LiveData

    private var _binding: FragmentHabitViewPagerBinding? = null
    private val binding get() = _binding!!
    private var fragmentListener: HabitTrackerFragment.HabitFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as HabitTrackerFragment.HabitFragmentListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitViewPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
    }

    private fun setupViewPager() {
        val viewPager = binding.viewPager2
        val tabLayout = binding.tabLayout

        // requireActivity() - передаёт текущую активность в адаптер, который отвечает за создание и управление фрагментами
        viewPager.adapter = PagerAdapter(requireActivity())

        // TabLayoutMediator автоматически синхронизирует tabLayout с viewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Good Habits"
                1 -> "Bad Habits"
                else -> ""
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateNewHabit() {
        fragmentListener?.onCreateNewHabit()
    }

    override fun onEditHabit(habit: Habit) {
        fragmentListener?.onEditHabit(habit)
    }

    class PagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> GoodHabitsFragment.newInstance()
                1 -> BadHabitsFragment.newInstance()
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
}