package edu.capella.mobile.android

import android.app.Activity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import edu.capella.mobile.android.utils.Preferences
import edu.capella.mobile.android.activity.CommonDetailActivity
import edu.capella.mobile.android.activity.CourseRoomSyllabusActivity
import edu.capella.mobile.android.activity.CoursesActivity
import edu.capella.mobile.android.activity.LoginActivity
import edu.capella.mobile.android.adapters.CourseSyllabusListAdapter
import edu.capella.mobile.android.adapters.CourseViewHolder
import edu.capella.mobile.android.app.CapellaApplication
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Class Name.kt : class description goes here
 *
 * @author  :  SSHARMA45
 * @version :  1.0
 * @since   :  4/4/2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
class CommonDetailActivityTest {

    lateinit var baseActivity: Activity

    private val currentPkg = "edu.capella.mobile.android"
    @Rule
    @JvmField
    var activityRule: ActivityTestRule<LoginActivity> = ActivityTestRule(LoginActivity::class.java)


    @Before
    fun initPreWork() {

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Preferences.getInstance(context)
        baseActivity = activityRule.activity

        Espresso.onView(ViewMatchers.withId(R.id.txtEmail)).perform(
            ViewActions.typeText("JLAHARE"),
            ViewActions.closeSoftKeyboard()
        )
        Espresso.onView(ViewMatchers.withId(R.id.txtPassword))
            .perform(ViewActions.typeText("2400217Ja"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.btnLogin)).perform(ViewActions.click())


        try {
            Espresso.onView(ViewMatchers.withId(R.id.coachOkButton))
                .check((ViewAssertions.matches(ViewMatchers.isDisplayed())))
                .perform(ViewActions.click())
        } catch (noView: Throwable) {
            //THIS IS OK AS WE HAVE COACH SCREEN ONLY FOR FIRST TIME, SO CATCH THIS
            //EXCEPTION OTHERWISE WHOLE TEST WILL FAIL : JAYESH
        }

        Espresso.onView(ViewMatchers.withId(R.id.drawerImageView)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Courses")).perform(ViewActions.click())


        val cAct = getCurrentActivity() as CoursesActivity

        Espresso.onView(ViewMatchers.withId(R.id.currentCourseListView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(
                    cAct.getCurrentCourseListSize() - 1,
                    ViewActions.click()
                )
            )

        Thread.sleep(1000)

        Espresso.onView(ViewMatchers.withText("Syllabus"))
            .perform(ViewActions.scrollTo(), ViewActions.click())

        Thread.sleep(1000)


        var activity = getCurrentActivity() as CourseRoomSyllabusActivity

        if (activity.getSyllabusListSize() > 0) {
            // CLICKING ON "[u02d5] Unit 2 Discussion 5"
            Espresso.onView(ViewMatchers.withId(R.id.syllabusListView))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<CourseSyllabusListAdapter.SyllabusViewHolder>(
                        0,
                        ViewActions.click()
                    )
                )
        } /*else {
            Espresso.onView(ViewMatchers.withId(R.id.syllabusListView))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<CourseSyllabusListAdapter.SyllabusViewHolder>(
                        0,
                        ViewActions.click()
                    )
                )
        }*/

        Thread.sleep(1000)

    }



    @Test
    fun check_final_url() {
        Thread.sleep(500)
        var activity = getCurrentActivity() as CommonDetailActivity
        Assert.assertNotNull(activity.get_returned_url())
    }


    fun getCurrentActivity(): Activity? {
        return (baseActivity.application as CapellaApplication).getCurrentRunningActivity()
    }


}
