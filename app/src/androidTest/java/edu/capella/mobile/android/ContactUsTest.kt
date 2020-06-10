package edu.capella.mobile.android

import android.app.Activity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
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
class ContactUsTest {

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
                typeText("SSHARMA45"),
                closeSoftKeyboard()
        )
        onView(withId(R.id.txtPassword))
                .perform(typeText("2402451Sh"), closeSoftKeyboard())

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
        onView(ViewMatchers.withText("Contact Us")).perform(click())

//        onView(withId(R.id.currentCourseListView))
//                .perform(RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(0, click()))

        Thread.sleep(100)

    }

    @Test
    fun check_phone_numbers() {
        onView(withId(R.id.tollFreeNoCapella))
            .check(ViewAssertions.matches(ViewMatchers.withText("1-888-CAPELLA")))

        onView(withId(R.id.tollFreeNoSecond))
            .check(ViewAssertions.matches(ViewMatchers.withText("1-888-227-3552")))

        onView(withId(R.id.internationalNo))
            .check(ViewAssertions.matches(ViewMatchers.withText("1-612-977-5000")))
    }

    @Test
    fun check_button_string() {
        onView(withId(R.id.technicalSupportButton))
            .check(ViewAssertions.matches(ViewMatchers.withText("Technical Support")))

        onView(withId(R.id.academicAdvisingButton))
            .check(ViewAssertions.matches(ViewMatchers.withText("Academic Advising")))

        onView(withId(R.id.capellaCampusButton))
            .check(ViewAssertions.matches(ViewMatchers.withText("Capella Campus")))
    }

    @Test
    fun check_toll_free_no_one_click() {
        onView(withId(R.id.tollFreeNoCapella)).perform(click())
    }

    @Test
    fun check_toll_free_no_two_click(){
        onView(withId(R.id.tollFreeNoSecond)).perform(click())
    }

    @Test
    fun check_international_no_click(){
        onView(withId(R.id.internationalNo)).perform(click())
    }



}