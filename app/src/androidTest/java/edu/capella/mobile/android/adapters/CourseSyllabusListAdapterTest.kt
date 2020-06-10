package edu.capella.mobile.android.adapters

import android.app.Activity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.Preferences
import edu.capella.mobile.android.activity.CourseRoomSyllabusActivity
import edu.capella.mobile.android.activity.LoginActivity
import edu.capella.mobile.android.app.CapellaApplication
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Class Name.kt : class description goes here
 *
 * @param param1
 *
 *Constructor Param1 Description Goes here
 * @param param2
 *
 *Constructor Param2 Description Goes here
 * @author :  jayesh.lahare
 * @version :
 * @since :  3/21/2020
 */
class CourseSyllabusListAdapterTest{

    private val currentPkg = "edu.capella.mobile.android"

    lateinit var baseActivity: Activity

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<LoginActivity> = ActivityTestRule(LoginActivity::class.java)


    @Before
    fun init() {

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Preferences.getInstance(context)
        baseActivity = activityRule.activity
        Espresso.onView(ViewMatchers.withId(R.id.txtEmail)).perform(
            ViewActions.typeText("SSHARMA45"),
            ViewActions.closeSoftKeyboard()
        )
        Espresso.onView(ViewMatchers.withId(R.id.txtPassword))
            .perform(ViewActions.typeText("2402451Sh"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.btnLogin)).perform(ViewActions.click())


        try {
            Espresso.onView(ViewMatchers.withId(R.id.coachOkButton)).check((ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )))
                .perform(ViewActions.click())
        }catch (noView: Throwable)
        {
            //THIS IS OK AS WE HAVE COACH SCREEN ONLY FOR FIRST TIME, SO CATCH THIS
            //EXCEPTION OTHERWISE WHOLE TEST WILL FAIL : JAYESH
        }

        Espresso.onView(ViewMatchers.withId(R.id.drawerImageView)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Courses")).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.currentCourseListView))
            .perform( RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(0,
                ViewActions.click()
            ))

        Thread.sleep(1000)

        //Espresso.onView(ViewMatchers.withId(R.id.classmatesLayout)).perform( click())
        // Espresso.onView(ViewMatchers.withText("Classmates")).perform( click())

        Espresso.onView(ViewMatchers.withId(R.id.syllabusLayout))
            .perform(ViewActions.scrollTo(), ViewActions.click())

        Thread.sleep(1000)

        /*Espresso.onView(ViewMatchers.withId(R.id.currentCourseListView))
            .perform( RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(0, click()))*/


    }

    @Test
    fun check_syllabus_list_adapter_is_non_null() {

        var activity = getCurrentActivity() as CourseRoomSyllabusActivity

        Assert.assertNotEquals(activity.syllabusListAdapter , null)
    }

    @Test
    fun check_syllabus_item_list_is_non_null() {

        var activity = getCurrentActivity() as CourseRoomSyllabusActivity

        Assert.assertNotEquals(activity.syllabusListAdapter!!.items , null)
    }

    @Test
    fun check_syllabus_context_is_non_null() {

        var activity = getCurrentActivity() as CourseRoomSyllabusActivity

        Assert.assertNotEquals(activity.syllabusListAdapter!!.context , null)
    }

    @Test
    fun check_syllabus_item_listener_is_non_null() {

        var activity = getCurrentActivity() as CourseRoomSyllabusActivity

        Assert.assertNotEquals(activity.syllabusListAdapter!!.syllabusItemListener , null)
    }





    fun getCurrentActivity(): Activity? {
        return (baseActivity.application as CapellaApplication).getCurrentRunningActivity()
    }
}
