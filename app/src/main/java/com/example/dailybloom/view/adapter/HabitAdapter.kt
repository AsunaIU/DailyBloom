package com.example.dailybloom.view.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.dailybloom.R
import com.example.dailybloom.model.Habit
import com.example.dailybloom.model.HabitType
import com.example.dailybloom.model.Priority
import com.example.dailybloom.util.Constants
import com.example.dailybloom.view.HabitFilterFragment.Companion.TAG
import com.example.dailybloom.viewmodel.HabitListViewModel


sealed class ListItem {
    data class HabitItem(val habit: Habit) : ListItem()
    data class SectionHeader(val title: String) : ListItem()
}

class HabitAdapter(
    private val onClick: (Habit) -> Unit,
    private val viewModel: HabitListViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<ListItem> = emptyList()

    fun submitList(habits: List<Habit>) {
        Log.d(TAG, "submitList: received ${habits.size} habits")

        val sortedHabits = habits.sortedBy { it.done }
        Log.d(TAG, "submitList: sorted habits, first done=${sortedHabits.firstOrNull()?.done}")
        //  т.к. `done` - типа Boolean, сортировка от false (невыполненные) к true (выполненные)
        //  false < true

        val newItems = mutableListOf<ListItem>()  // лист с заголовками

        val unfulfilledHabits = sortedHabits.filter { !it.done }
        val completedHabits = sortedHabits.filter { it.done }

        // фильтрация невыполненных привычек
        if (unfulfilledHabits.isNotEmpty()) {
            Log.d(TAG, "submitList: adding Unfulfilled section with ${unfulfilledHabits.size} items")
            newItems.add(ListItem.SectionHeader("Unfulfilled"))
            unfulfilledHabits.forEach { newItems.add(ListItem.HabitItem(it)) }
        }

        // фильтрация невыполненных привычек
        if (completedHabits.isNotEmpty()) {
            Log.d(TAG, "submitList: adding Completed section with ${completedHabits.size} items")
            newItems.add(ListItem.SectionHeader("Completed"))
            completedHabits.forEach { newItems.add(ListItem.HabitItem(it)) }
        }

        items = newItems
        notifyDataSetChanged()
        Log.d(TAG, "submitList: items updated, total display count=${items.size}")
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.SectionHeader -> Constants.TYPE_SECTION_HEADER
            is ListItem.HabitItem -> Constants.TYPE_HABIT_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d(TAG, "onCreateViewHolder: viewType=$viewType")
        return when (viewType) {
            Constants.TYPE_SECTION_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.done_not_selection_header, parent, false)
                SectionHeaderViewHolder(view)
            }
            Constants.TYPE_HABIT_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_habit, parent, false)
                HabitViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: position=$position, item=$items")
        when (val item = items[position]) {
            is ListItem.SectionHeader -> (holder as SectionHeaderViewHolder).bind(item.title)
            is ListItem.HabitItem -> (holder as HabitViewHolder).bind(item.habit, onClick, viewModel)
        }
    }

    override fun getItemCount(): Int = items.size

    // ViewHolder для заголовка секции
    class SectionHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tvSectionHeader)
        fun bind(headerTitle: String) {
            title.text = headerTitle
        }
    }

    // ViewHolder для элемента привычки
    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tvTitle)
        private val priority: TextView = itemView.findViewById(R.id.tvPriority)
        private val type: TextView = itemView.findViewById(R.id.tvType)
        private val frequency: TextView = itemView.findViewById(R.id.tvFrequency)
        private val description: TextView = itemView.findViewById(R.id.tvDescription)
        private val colorIndicator: View = itemView.findViewById(R.id.colorIndicator)
        private val actionButton: Button = itemView.findViewById(R.id.btnHabitAction)

        // Привязка объекта привычки к представлениям
        @SuppressLint("SetTextI18n")
        fun bind(habit: Habit, onClick: (Habit) -> Unit, viewModel: HabitListViewModel) {
            Log.d(TAG, "Binding habit id=${habit.id}, done=${habit.done}")
            title.text = habit.title
            description.text = habit.description
            colorIndicator.setBackgroundColor(habit.color)
            priority.text = Priority.toDisplayString(habit.priority)
            type.text = HabitType.toDisplayString(habit.type)
            frequency.text = "${habit.frequency} per ${habit.periodicity}"
            actionButton.text = if (habit.done) "Done" else "Do it"

            // Обработчик клика на весь элемент
            itemView.setOnClickListener { onClick(habit) }

            // Обработчик клика на кнопку действия
            actionButton.setOnClickListener {
                Log.d(TAG, "Action button clicked for habit id=${habit.id}, done=${habit.done}")

                if (!habit.done) {
                    // Use the viewModel to update the habit status
                    viewModel.setHabitDone(habit.id)

                    if (habit.type == HabitType.GOOD) {
                        Toast.makeText(
                            itemView.context,
                            "You are breathtaking!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            itemView.context,
                            "Stop doing this",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // For already done habits, use the same viewModel logic
                    // We'll toggle its done status by updating it
                    viewModel.setHabitDone(habit.id)

                    if (habit.type == HabitType.GOOD) {
                        Toast.makeText(
                            itemView.context,
                            "It is worth doing this ${habit.frequency} more times",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            itemView.context,
                            "You can do this ${habit.frequency} more times",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}