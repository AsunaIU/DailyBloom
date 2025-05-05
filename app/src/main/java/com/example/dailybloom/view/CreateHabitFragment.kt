package com.example.dailybloom.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.example.dailybloom.R
import com.example.dailybloom.databinding.FragmentCreateHabitBinding
import com.example.dailybloom.model.Habit
import com.example.dailybloom.util.Constants
import com.example.dailybloom.viewmodel.HabitEditViewModel
import com.example.dailybloom.viewmodel.viewmodeldata.UiHabit

class CreateHabitFragment : Fragment() {

    private val viewModel: HabitEditViewModel by viewModels()

    private var currentHabit: Habit? = null

    private var _binding: FragmentCreateHabitBinding? = null
    private val binding get() = _binding!!

    private var fragmentListener: CreateHabitListener? = null

    companion object {
        fun newInstance(habit: Habit? = null): CreateHabitFragment {
            val fragment = CreateHabitFragment()
            val args = Bundle()
            args.putParcelable(Constants.ARG_HABIT, habit)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as CreateHabitListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateHabitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getParcelable<Habit>(Constants.ARG_HABIT)?.let { // замена handleArguments(); передаем привычку в ViewModel
                currentHabit = it
                viewModel.setCurrentHabit(it)
            }

        restoreState(savedInstanceState)
        setupUI()
        setupObservers()
    }

    // сохраняет состояние UI в Bundle
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.uiState.value?.let {
            outState.putParcelable(Constants.KEY_UI_STATE, it)
        }
    }

    // восстанавливает состояние UI из Bundle
    private fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.getParcelable<UiHabit>(Constants.KEY_UI_STATE)?.let {
            viewModel.setUIState(it)
        }
    }

    // фрагмент наблюдает за LiveData из ViewModel (uiState) и вызывает updateUI при изменениях
    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            updateUI(state)
        }
    }

    // обновление UI при изменении состояния UiHabit
    private fun updateUI(state: UiHabit) {
        with(binding) {
            if (!etHabitTitle.hasFocus()) etHabitTitle.setText(state.title) // если поле не в фокусе (т.е. не вводятся данные)
            if (!etHabitDescription.hasFocus()) etHabitDescription.setText(state.description)
            if (!etHabitFrequency.hasFocus()) etHabitFrequency.setText(state.frequency)

            spinnerPriority.setSelection(state.priorityPos)
            rgHabitType.check(state.typeId)
            spinnerFrequencyUnit.setSelection(state.periodicityPos)
            colorPicker.setSelectedColor(state.selectedColor)
        }
    }

    // получение данных UI из пользовательского ввода и передача во ViewModel
    private fun setupUI() {
        with(binding) {

            // после каждого изменения полей отправляем новое значение во ViewModel (если поле в фокусе)
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

            spinnerPriority.onItemSelected { position ->
                viewModel.updateUIState(priorityPos = position)  // position - индекс выбранного пункта
            }

            spinnerFrequencyUnit.onItemSelected { position ->
                viewModel.updateUIState(periodicityPos = position)
            }
            rgHabitType.setOnCheckedChangeListener { _, checkedId ->
                viewModel.updateUIState(typeId = checkedId)
            }

            colorPicker.setOnColorSelectedListener { color ->
                viewModel.updateColor(color)
            }

            btnDeleteHabit.apply {
                visibility = if (currentHabit != null) View.VISIBLE else View.GONE
                setOnClickListener { showDeleteConfirmationDialog()}
            }

            // при нажатии на кнопку (если saveHabit() вернул true) уведомляем fragmentListener
            btnSaveHabit.setOnClickListener {
                if (saveHabit()) {
                    fragmentListener?.onHabitSaved()
                }
            }
        }
    }

    private fun saveHabit(): Boolean {
        val isSaved = viewModel.saveHabit(currentHabit?.id)

        if (!isSaved) {
            with(binding) {
                val state = viewModel.uiState.value ?: return false
                if (state.title.isBlank()) etHabitTitle.error = getString(R.string.error_empty_title)
                if (state.frequency.isBlank()) etHabitFrequency.error = getString(R.string.error_empty_frequency)
                Log.d("SaveHabit", "Frequency value: ${state.frequency}")
            }
        }
        return isSaved
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_habit_title))
            .setMessage(getString(R.string.delete_habit_message))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                currentHabit?.id?.let { habitId ->
                    viewModel.deleteHabit(habitId)
                    fragmentListener?.onHabitDeleted()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface CreateHabitListener {
        fun onHabitSaved()
        fun onHabitDeleted()
    }
    // интерфейс - способ уведомить Activity о том, что фрагмент успешно сохранил привычку
    // контракт: «любая внешняя сущность (обычно Activity), желающая реагировать на событие “привычка сохранена”, должна реализовать этот метод»
}

// extension для Spinner (централизует пустую реализацию onNothingSelected)
fun Spinner.onItemSelected(action: (position: Int) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>, view: View?, position: Int, id: Long
        ) = action(position)

        override fun onNothingSelected(parent: AdapterView<*>) = Unit
    }
}