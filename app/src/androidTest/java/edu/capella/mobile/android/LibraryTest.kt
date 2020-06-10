package edu.capella.mobile.android

import android.app.Activity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import edu.capella.mobile.android.utils.Preferences
import edu.capella.mobile.android.activity.LoginActivity
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class LibraryTest {

    lateinit var baseActivity: Activity

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<LoginActivity> =
            ActivityTestRule(LoginActivity::class.java)


    @Before
    fun initPreWork() {

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Preferences.getInstance(context)
        baseActivity = activityRule.activity

        onView(withId(R.id.txtEmail)).perform(
                ViewActions.typeText("SSHARMA45"),
                ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.txtPassword))
                .perform(ViewActions.typeText("2402451Sh"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.btnLogin)).perform(click())


        try {
            onView(withId(R.id.coachOkButton)).check((ViewAssertions.matches(ViewMatchers.isDisplayed())))
                    .perform(click())
        } catch (noView: Throwable) {
            //THIS IS OK AS WE HAVE COACH SCREEN ONLY FOR FIRST TIME, SO CATCH THIS
            //EXCEPTION OTHERWISE WHOLE TEST WILL FAIL :
        }


        Thread.sleep(1000)

        onView(withId(R.id.drawerImageView)).perform(click())
        onView(ViewMatchers.withText("Library")).perform(click())

//        onView(withId(R.id.currentCourseListView))
//                .perform(RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(0, click()))

        Thread.sleep(1000)

    }

    @Test
    fun check_phone_numbers() {
        onView(withId(R.id.tollFreeTxt))
            .check(matches(ViewMatchers.withText("1-888-375-8221")))

        onView(withId(R.id.internationalText))
            .check(matches(ViewMatchers.withText("1-612-977-4033")))
    }

    @Test
    fun check_url_labels() {
        onView(withId(R.id.summonTxt))
            .check(matches(ViewMatchers.withText("Summon")))

        onView(withId(R.id.databaseTxt))
            .check(matches(ViewMatchers.withText("Database A-Z")))
    }

    @Test
    fun check_toll_free_number_click() {
        onView(withId(R.id.tollFreeTxt)).perform(click())
    }

    @Test
    fun check_international_number() {
        onView(withId(R.id.internationalText)).perform(click())
    }

    @Test
    fun check_summon_click(){
        onView(withId(R.id.summonTxt)).perform(click())
    }

    @Test
    fun check_database_click(){
        onView(withId(R.id.databaseTxt)).perform(click())
    }

}
