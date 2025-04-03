package com.example.dailybloom.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.example.dailybloom.databinding.FragmentCreateHabitBinding
import com.example.dailybloom.model.Habit
import com.example.dailybloom.util.Constants
import com.example.dailybloom.viewmodel.HabitEditViewModel
import com.example.dailybloom.viewmodel.UiHabit


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

    private fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.getParcelable<UiHabit>(Constants.KEY_UI_STATE)?.let {
            viewModel.setUIState(it)
        }
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            updateUI(state)
        }
    }

    // обновление UI в соответствии с переданным состоянием
    private fun updateUI(state: UiHabit) {
        with(binding) {
            if (!etHabitTitle.hasFocus()) etHabitTitle.setText(state.title) // поле не в фокусе (т.е. не вводятся данные)
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
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.updateUIState(priorityPos = position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            spinnerFrequencyUnit.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
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
                if (state.title.isBlank()) etHabitTitle.error = "Enter a title"
                if (state.frequency.isBlank()) etHabitFrequency.error = "Enter frequency"
                Log.d("SaveHabit", "Frequency value: ${state.frequency}")
            }
        }
        return isSaved
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface CreateHabitListener {
        fun onHabitSaved()
    }
}