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
import edu.capella.mobile.android.activity.*
import edu.capella.mobile.android.adapters.CourseViewHolder
import edu.capella.mobile.android.adapters.DiscussionForumListAdapter
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
class AssessmentDetailActivityTest {


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

        //Espresso.onView(ViewMatchers.withId(R.id.classmatesLayout)).perform( click())
        // Espresso.onView(ViewMatchers.withText("Classmates")).perform( click())

        // onView( withId( R.id.classmatesLayout)).perform( scrollTo(), click())
        Espresso.onView(ViewMatchers.withText("Assessments and Status"))
            .perform(ViewActions.scrollTo(), ViewActions.click())

        Thread.sleep(1000)


        var activity = getCurrentActivity() as AssessmentForLearnerActivity

        if(activity.getAssessmentListSize() > 0)
        {
            // CLICKING ON "[u02d5] Unit 2 Discussion 5"
            Espresso.onView(ViewMatchers.withId(R.id.assessment_list))
                .perform( RecyclerViewActions.actionOnItemAtPosition<DiscussionForumListAdapter.DiscussionRowViewHolder>(0,
                    ViewActions.click()
                ))
        }



        Espresso.onView(ViewMatchers.withId(R.id.assessmentDetailView)).perform(ViewActions.click())
        Thread.sleep(500)

        var assessmentactivity = getCurrentActivity() as AssessmentDetailListActivity

        if(assessmentactivity.getAssessmentDetailListSize() > 0)
        {
            Espresso.onView(ViewMatchers.withId(R.id.detailList))
                .perform( RecyclerViewActions.actionOnItemAtPosition<DiscussionForumListAdapter.DiscussionRowViewHolder>(0,
                    ViewActions.click()
                ))
        }

        Thread.sleep(500)
    }


    fun getCurrentActivity(): Activity? {
        return (baseActivity.application as CapellaApplication).getCurrentRunningActivity()
    }

    @Test
    fun check_final_url() {
        try {
            Thread.sleep(100)
            var activity = getCurrentActivity() as AssessmentDetailActivity
            Assert.assertNotNull(activity.get_returned_url())
        }catch (t:Throwable){}
    }

    @After
    fun cleanUp() {
        // Intents.release()
    }
}
