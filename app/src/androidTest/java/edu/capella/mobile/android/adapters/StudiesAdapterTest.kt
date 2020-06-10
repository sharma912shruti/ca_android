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
import edu.capella.mobile.android.activity.AssessmentForLearnerActivity
import edu.capella.mobile.android.activity.CoursesActivity
import edu.capella.mobile.android.activity.LoginActivity
import edu.capella.mobile.android.activity.StudyActivity
import edu.capella.mobile.android.app.CapellaApplication
import edu.capella.mobile.android.utils.Preferences
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  16-05-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
class StudiesAdapterTest {
    private val currentPkg = "edu.capella.mobile.android"

    lateinit var baseActivity: Activity

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<LoginActivity> = ActivityTestRule(LoginActivity::class.java)


    @Before
    fun initPreWork(){

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
            Espresso.onView(ViewMatchers.withId(R.id.coachOkButton))
                .check((ViewAssertions.matches(ViewMatchers.isDisplayed())))
                .perform(ViewActions.click())
        }catch (noView: Throwable)
        {
            //THIS IS OK AS WE HAVE COACH SCREEN ONLY FOR FIRST TIME, SO CATCH THIS
            //EXCEPTION OTHERWISE WHOLE TEST WILL FAIL : JAYESH
        }

        Espresso.onView(ViewMatchers.withId(R.id.drawerImageView)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Courses")).perform(ViewActions.click())


        val cAct = getCurrentActivity() as CoursesActivity

        Espresso.onView(ViewMatchers.withId(R.id.currentCourseListView))
            .perform( RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(cAct.getGpCourseIndex(),
                ViewActions.click()
            ))

        Thread.sleep(1000)

        Espresso.onView(ViewMatchers.withText("Studies"))
            .perform(ViewActions.scrollTo(), ViewActions.click())

        Thread.sleep(1000)

        Thread.sleep(1500)
    }

    @Test
    fun check_assessment_list_adapter_is_non_null() {
        Thread.sleep(500)
        var activity = getCurrentActivity() as StudyActivity

        Assert.assertNotEquals(activity.studyAdapter , null)
    }

    @Test
    fun check_item_list_is_non_null() {
        Thread.sleep(500)
        var activity = getCurrentActivity() as StudyActivity

        Assert.assertNotEquals(activity.studyAdapter!!.studyList , null)
    }

    @Test
    fun check_context_is_non_null() {
        Thread.sleep(500)
        var activity = getCurrentActivity() as StudyActivity

        Assert.assertNotEquals(activity.studyAdapter!!.context , null)
    }

    @Test
    fun check_item_listener_is_non_null() {
        Thread.sleep(500)
        var activity = getCurrentActivity() as StudyActivity

        Assert.assertNotEquals(activity.studyAdapter!!.studyEventListener , null)
    }


    fun getCurrentActivity(): Activity? {
        return (baseActivity.application as CapellaApplication).getCurrentRunningActivity()
    }
}