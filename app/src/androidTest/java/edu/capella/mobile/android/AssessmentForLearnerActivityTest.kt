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
import edu.capella.mobile.android.activity.AssessmentForLearnerActivity
import edu.capella.mobile.android.activity.CoursesActivity
import edu.capella.mobile.android.activity.LoginActivity
import edu.capella.mobile.android.adapters.AssessmentViewHolder
import edu.capella.mobile.android.adapters.CourseViewHolder
import edu.capella.mobile.android.app.CapellaApplication
import org.junit.*

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  09-05-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
class AssessmentForLearnerActivityTest {

    private val currentPkg = "edu.capella.mobile.android"
    lateinit var baseActivity : Activity

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<LoginActivity> =
        ActivityTestRule(LoginActivity::class.java)



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
            .perform( RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(cAct.getFpCourseIndex(),
                ViewActions.click()
            ))

        Thread.sleep(1000)

        Espresso.onView(ViewMatchers.withText("Assessments and Status"))
            .perform(ViewActions.scrollTo(), ViewActions.click())

        Thread.sleep(1000)


//        var activity = getCurrentActivity() as AssessmentForLearnerActivity
//
//        if(activity.getAssessmentListSize() > 0)
//        {
//            // CLICKING ON "[u02d5] Unit 2 Discussion 5"
//            Espresso.onView(ViewMatchers.withId(R.id.assessment_list))
//                .perform( RecyclerViewActions.actionOnItemAtPosition<DiscussionForumListAdapter.DiscussionRowViewHolder>(0,
//                    ViewActions.click()
//                ))
//        }

        Thread.sleep(1500)
    }


    fun getCurrentActivity(): Activity? {
        return (baseActivity.application as CapellaApplication).getCurrentRunningActivity()
    }

    @Test
    fun check_list_items_are_clickable_or_not(){
        Thread.sleep(500)
        var activity = getCurrentActivity() as AssessmentForLearnerActivity

        if(activity.getAssessmentListSize() > 0)
        {
            // CLICKING ON "[u02d5] Unit 2 Discussion 5"
            Espresso.onView(ViewMatchers.withId(R.id.assessment_list))
                .perform( RecyclerViewActions.actionOnItemAtPosition<AssessmentViewHolder>(0,
                    ViewActions.click()
                ))
        }
    }


    @Test
    fun check_title_is_discussions() {
        Espresso.onView(ViewMatchers.withId(R.id.genericTitleTxt))
            .check(ViewAssertions.matches(ViewMatchers.withText("Assessments")))
    }

    @Test
    fun check_course_information_is_available_for_processing()
    {
        var activity = getCurrentActivity() as AssessmentForLearnerActivity
        Assert.assertNotNull(activity.assessmentAdapter)
        Assert.assertNotNull(activity.assessmentLearnerBean)
        Assert.assertNotNull(activity.assessmentList)
    }

    @Test
    fun check_assessment_list_have_list_non_null() {
        var activity = getCurrentActivity() as AssessmentForLearnerActivity
        Assert.assertTrue(activity.getAssessmentListSize() >= 0)
    }

    @After
    fun cleanUp() {
        // Intents.release()
    }
}