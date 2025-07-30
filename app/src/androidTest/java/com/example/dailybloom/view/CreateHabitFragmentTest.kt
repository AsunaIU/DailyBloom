package com.example.dailybloom.view

import android.content.Context
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.dailybloom.R
import com.example.dailybloom.util.Constants
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.anything
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@MediumTest
class CreateHabitFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var scenario: FragmentScenario<CreateHabitFragment>
    private lateinit var context: Context

    @Before
    fun setup() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
    }

    /**
     * Test 1: Запуск фрагмента
     * Проверяет, что фрагмент запущен и все UI-компоненты фрагмента видны
     */
    @Test
    fun fragment_launches_successfully_and_displays_all_components() {

        // Запускаем фрагмент с помощью launchFragment, автоматически создаст EmptyFragmentActivity
        scenario = launchFragmentInContainer<CreateHabitFragment>()

        // // Проверяем видимость элементов
        onView(withId(R.id.etHabitTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.etHabitDescription)).check(matches(isDisplayed()))
        onView(withId(R.id.rgHabitType)).check(matches(isDisplayed()))
        onView(withId(R.id.etHabitFrequency)).check(matches(isDisplayed()))
        onView(withId(R.id.spinnerFrequencyUnit)).check(matches(isDisplayed()))
        onView(withId(R.id.spinnerPriority)).check(matches(isDisplayed()))
        onView(withId(R.id.colorPicker)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSaveHabit)).check(matches(isDisplayed()))

        // Delete button скрыта для новых привычек
        onView(withId(R.id.btnDeleteHabit)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    /**
     * Test 2: Отображение кнопки удаления в редакторе привычки
     */
    @Test
    fun fragment_shows_delete_button_when_editing_existing_habit() {

        val bundle = bundleOf(Constants.ARG_HABIT_ID to "test-habit-id")
        scenario = launchFragmentInContainer<CreateHabitFragment>(fragmentArgs = bundle)

        // Delete button видна в режиме редактирования
        onView(withId(R.id.btnDeleteHabit)).check(matches(isDisplayed()))
    }

    /**
     * Test 3: Ввод текста в поля и его сохранение
     */
    @Test
    fun text_input_works_correctly_in_all_fields() {
        scenario = launchFragment<CreateHabitFragment>()

        val testTitle = "Daily Exercise"
        val testDescription = "Go for a 30-minute walk every day"
        val testFrequency = "2"

        onView(withId(R.id.etHabitTitle)).perform(typeText(testTitle), closeSoftKeyboard())
        onView(withId(R.id.etHabitTitle)).check(matches(withText(testTitle)))

        onView(withId(R.id.etHabitDescription)).perform(
            typeText(testDescription),
            closeSoftKeyboard()
        )
        onView(withId(R.id.etHabitDescription)).check(matches(withText(testDescription)))

        onView(withId(R.id.etHabitFrequency)).perform(typeText(testFrequency), closeSoftKeyboard())
        onView(withId(R.id.etHabitFrequency)).check(matches(withText(testFrequency)))
    }

    /**
     * Test 4: Радиокнопки переключаются корректно и сохраняют своё состояние
     */
    @Test
    fun radio_group_selection_works_correctly() {
        scenario = launchFragment<CreateHabitFragment>()

        // Проверка состояния фрагмента
        scenario.moveToState(Lifecycle.State.RESUMED)

        onView(withId(R.id.rbHabitGood)).perform(click())
        onView(withId(R.id.rbHabitGood)).check(matches(isChecked()))
        onView(withId(R.id.rbHabitBad)).check(matches(isNotChecked()))

        onView(withId(R.id.rbHabitBad)).perform(click())
        onView(withId(R.id.rbHabitBad)).check(matches(isChecked()))
        onView(withId(R.id.rbHabitGood)).check(matches(isNotChecked()))
    }

    /**
     * Test 5: Выбор значений в спиннерах
     */
    @Test
    fun spinner_selection_works_correctly() {
        scenario = launchFragment<CreateHabitFragment>()

        onView(withId(R.id.spinnerPriority)).perform(click())
        onData(anything()).atPosition(1).perform(click())

        onView(withId(R.id.spinnerFrequencyUnit)).perform(click())
        onData(anything()).atPosition(2).perform(click())
    }

}