package com.fscsoftware.managetasks

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup (){
        scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.moveToState(Lifecycle.State.RESUMED)
    }


    @Test
    fun addNewTask ( ){

        val name = "Task one"
        val priority = "Normals"
        val desc = "This is a new description of a new task"

        onView(withId(R.id.fab_add)).perform(click())
        onView(withId(R.id.et_title)).perform(typeText(name))
        onView(withId(R.id.spinner_priority)).perform(click())
        onView(withText(containsString(priority))).inRoot(RootMatchers.isPlatformPopup()).check(matches(isDisplayed())).perform(
            click())

        onView(withId(R.id.et_details)).perform(typeText(desc))
        onView(withText("OK")).inRoot(RootMatchers.isDialog()).check(matches(isDisplayed())).perform(click())


        val name2 = "Task two"
        val priority2 = "Important"
        val desc2 = "This is a new description of a new task"

        onView(withId(R.id.fab_add)).perform(click())
        onView(withId(R.id.et_title)).perform(typeText(name2))
        onView(withId(R.id.spinner_priority)).perform(click())
        onView(withText(containsString(priority2))).inRoot(RootMatchers.isPlatformPopup()).check(matches(isDisplayed())).perform(
            click())

        onView(withId(R.id.et_details)).perform(typeText(desc2))
        onView(withText("OK")).inRoot(RootMatchers.isDialog()).check(matches(isDisplayed())).perform(click())

    }


}