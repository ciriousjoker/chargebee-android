package com.chargebee.example

import android.content.Context
import android.view.View
import android.view.autofill.AutofillManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.concurrent.TimeUnit

@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4ClassRunner::class)
class CBProductsTest {

    lateinit var context: Context

    private val RESOURCE = "GLOBAL"
    @JvmField val countingIdlingResource = CountingIdlingResource(RESOURCE)


    private val WAIT_RESOURCE = "WAIT"
    @JvmField val waitIdlingResource = CountingIdlingResource(WAIT_RESOURCE)


    @get:Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        setUpBefore()
    }

    @After
    fun unregisterIdlingResource() {
        setUpAfter()
    }

    @Test
    fun test_subscriptionStatus(){
        Espresso.onView(ViewMatchers.isRoot()).perform(waitFor(1000))
       // Espresso.onView(ViewMatchers.withId(R.id.rv_list_feature))

        val recyclerView = Espresso.onView(AllOf.allOf(ViewMatchers.withId(R.id.rv_list_feature)))
        recyclerView.perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(7, ViewActions.click()))
    }

    fun setUpBefore() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        InstrumentationRegistry.getInstrumentation().targetContext.getSystemService(AutofillManager::class.java).disableAutofillServices()

        IdlingPolicies.setMasterPolicyTimeout(2, TimeUnit.MINUTES)
        IdlingPolicies.setIdlingResourceTimeout(2, TimeUnit.MINUTES)

        IdlingRegistry.getInstance().register(countingIdlingResource, waitIdlingResource)

        val instrumentation = InstrumentationRegistry.getInstrumentation()

    }


    fun setUpAfter() {
        IdlingRegistry.getInstance().unregister(countingIdlingResource, waitIdlingResource)
    }

    fun waitFor(millis: Long): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isRoot()
            }

            override fun getDescription(): String {
                return "Wait for $millis milliseconds."
            }

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadForAtLeast(millis)
            }
        }
    }
}