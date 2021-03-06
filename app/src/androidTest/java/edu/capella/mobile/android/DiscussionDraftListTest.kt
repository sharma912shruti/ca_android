package edu.capella.mobile.android

import android.app.Activity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import edu.capella.mobile.android.utils.Preferences
import edu.capella.mobile.android.activity.*
import edu.capella.mobile.android.adapters.CourseViewHolder
import edu.capella.mobile.android.app.CapellaApplication
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class DiscussionDraftListTest {


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

        onView(withId(R.id.txtEmail)).perform(
            ViewActions.typeText("KPANDYA1"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.txtPassword))
            .perform(ViewActions.typeText("2402453Pa"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.btnLogin)).perform(click())


        try {
            onView(withId(R.id.coachOkButton)).check((matches(isDisplayed())))
                .perform(click())
        }catch (noView: Throwable)
        {
            //THIS IS OK AS WE HAVE COACH SCREEN ONLY FOR FIRST TIME, SO CATCH THIS
            //EXCEPTION OTHERWISE WHOLE TEST WILL FAIL : JAYESH
        }

        onView(withId(R.id.drawerImageView)).perform(click())
        onView(ViewMatchers.withText("Courses")).perform(click())

        Thread.sleep(500)

        onView(withId(R.id.currentCourseListView)).perform(RecyclerViewActions.scrollToPosition<CourseViewHolder>(4));
        Thread.sleep(500)
        onView(withId(R.id.currentCourseListView))
            .perform( RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(3, click()))


//        onView(withId(R.id.currentCourseListView))
//            .perform( RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(5, click()))

        Thread.sleep(1000)

        //Espresso.onView(ViewMatchers.withId(R.id.classmatesLayout)).perform( click())
       // Espresso.onView(ViewMatchers.withText("Classmates")).perform( click())

        onView( withId( R.id.discussionDraftLayout)).perform( scrollTo(), click())
        Thread.sleep(1000)

        /*Espresso.onView(ViewMatchers.withId(R.id.currentCourseListView))
            .perform( RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(0, click()))*/

    }

    @Test
    fun check_title_is_DiscussionDraft() {
        onView(withId(R.id.headerTxt))
            .check(matches(ViewMatchers.withText("Discussion Drafts")))
    }


    @Test
    fun check_DiscussionDraft_list_have_list_non_null() {
        var activity = getCurrentActivity() as DiscussionDraftActivity
        Assert.assertTrue(activity.getDiscussionDraftListSize() >= 0)
    }

    fun getCurrentActivity(): Activity? {
        return (baseActivity.application as CapellaApplication).getCurrentRunningActivity()
    }

    @After
    fun cleanUp() {
       // Intents.release()
    }
}


