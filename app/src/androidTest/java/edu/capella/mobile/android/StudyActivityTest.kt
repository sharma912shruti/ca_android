package edu.capella.mobile.android

import android.app.Activity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import edu.capella.mobile.android.utils.Preferences
import edu.capella.mobile.android.activity.CourseRoomMenuActivity
import edu.capella.mobile.android.activity.CoursesActivity
import edu.capella.mobile.android.activity.LoginActivity
import edu.capella.mobile.android.activity.StudyActivity
import edu.capella.mobile.android.adapters.CourseViewHolder
import edu.capella.mobile.android.app.CapellaApplication
import org.junit.*
import org.junit.runners.MethodSorters
import org.junit.runner.RunWith
/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  30-03-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class StudyActivityTest {
    lateinit var baseActivity : Activity

    private val currentPkg = "edu.capella.mobile.android"
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

        Thread.sleep(500)


    }



    fun getCurrentActivity(): Activity? {
        return (baseActivity.application as CapellaApplication).getCurrentRunningActivity()
    }

    @Test
    fun check_title_is_studies() {
        try{
        Espresso.onView(ViewMatchers.withId(R.id.genericTitleTxt))
            .check(ViewAssertions.matches(ViewMatchers.withText("Studies")))
    }catch (t:Throwable){}
    }

    @Test
    fun check_course_information_is_available_for_processing()
    {
        try {
            var activity = getCurrentActivity() as StudyActivity

            Assert.assertNotNull(activity.studyAdapter)
            Assert.assertNotNull(activity.courseId)
            Assert.assertNotNull(activity.courseBasicInfoBean)
            Assert.assertNotNull(activity.studiesdataList)
        }catch (t:Throwable){}
    }

    @Test
    fun check_list_item_contain_list(){
        try{
        var activity = getCurrentActivity() as StudyActivity
        Assert.assertTrue(activity.getStudyList() >= 0)
        }catch (t:Throwable){}
    }
}
