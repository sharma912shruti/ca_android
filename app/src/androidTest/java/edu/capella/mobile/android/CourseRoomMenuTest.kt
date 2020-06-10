package edu.capella.mobile.android

import android.app.Activity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import edu.capella.mobile.android.utils.Preferences
import edu.capella.mobile.android.activity.CourseRoomMenuActivity
import edu.capella.mobile.android.activity.LoginActivity
import edu.capella.mobile.android.adapters.CourseViewHolder
import edu.capella.mobile.android.app.CapellaApplication
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * CourseRoomMenuTest.kt : class description goes here
 *
 * @author  :  SSHARMA45
 * @version :  1.0
 * @since   :  3/9/2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class CourseRoomMenuTest {

    private val currentPkg = "edu.capella.mobile.android"
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
            onView(withId(R.id.coachOkButton)).check((matches(ViewMatchers.isDisplayed())))
                    .perform(click())
        } catch (noView: Throwable) {
            //THIS IS OK AS WE HAVE COACH SCREEN ONLY FOR FIRST TIME, SO CATCH THIS
            //EXCEPTION OTHERWISE WHOLE TEST WILL FAIL :
        }

        onView(withId(R.id.drawerImageView)).perform(click())
        onView(ViewMatchers.withText("Courses")).perform(click())

        onView(withId(R.id.currentCourseListView))
                .perform(RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(0, click()))

        Thread.sleep(1000)

    }





//    @Test
//    fun check_course_basic_information_object() {
//        var activity = getCurrentActivity() as CourseRoomMenuActivity
//        Assert.assertTrue(activity.getCourseBasicInformationDetail() == 1)
//    }

    @Test
    fun check_course_detail_object() {
        var activity = getCurrentActivity() as CourseRoomMenuActivity
        Assert.assertTrue(activity.getCourseDetail() == 1)
    }

    @Test
    fun check_ui_for_fp(){
        var activity = getCurrentActivity() as CourseRoomMenuActivity
        activity.getIsFp1_0()
    }

    @Test
    fun check_ui_for_gp_1_0(){
        var activity = getCurrentActivity() as CourseRoomMenuActivity
        activity.getIsGp1_0()
    }

    @Test
    fun check_grades_and_status(){
        var activity = getCurrentActivity() as CourseRoomMenuActivity
        activity.getGradesAndStatusUnReadCount()
    }

   /* @Test
    fun check_instructor_profile(){
        onView(withId(R.id.profileLayout))
            .perform(ViewActions.click(), ViewActions.closeSoftKeyboard())
    }*/

    fun getCurrentActivity(): Activity? {
        return (baseActivity.application as CapellaApplication).getCurrentRunningActivity()
    }

    @After
    fun cleanUp() {
        // Intents.release()
    }
}
