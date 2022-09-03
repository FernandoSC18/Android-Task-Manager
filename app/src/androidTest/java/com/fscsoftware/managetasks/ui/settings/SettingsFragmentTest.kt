package com.fscsoftware.managetasks.ui.settings

import android.R.id
import android.content.Context
import android.util.Log
import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fscsoftware.managetasks.R
import com.fscsoftware.managetasks.adapter.PrioritiesAdapter
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SettingsFragmentTest  {

    private lateinit var scenario: FragmentScenario<SettingsFragment>

    @Before
    fun setup (){
        val context = ApplicationProvider.getApplicationContext<Context>()

        scenario = launchFragmentInContainer <SettingsFragment>(themeResId = R.style.Theme_ManageTasks)
        scenario.moveToState(Lifecycle.State.RESUMED)
    }


    @Test
    fun editPriorities( ) {

        editPriority("Otros", "Normals")

    }

    fun editPriority(label : String, newLabel : String ) {

        onView(withId(R.id.rv_priorities))
        onView(withId(R.id.rv_priorities))
            .check( ViewAssertions.matches(hasDescendant(withText(label))))
            .perform(RecyclerViewActions.actionOnItemAtPosition<PrioritiesAdapter.ViewHolder>(

                0, object : ViewAction {
                    override fun getConstraints() = null
                    override fun getDescription() = "Description of a Child View"
                    override fun perform(uiController: UiController, view: View) {
                        val v: View = view.findViewById(R.id.iv_priority_item)
                        v.performClick()
                    } }
            ))


        onView(withId(R.id.et_name)).perform(ViewActions.clearText())
        onView(withId(R.id.et_name)).perform(ViewActions.typeText(newLabel))
        onView(withText("OK")).inRoot(RootMatchers.isDialog()).check(
            ViewAssertions.matches(
                isDisplayed()
            )
        ).perform(click())

    }

}