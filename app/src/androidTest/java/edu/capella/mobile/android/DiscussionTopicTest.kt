package edu.capella.mobile.android

import android.app.Activity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import edu.capella.mobile.android.utils.Preferences
import edu.capella.mobile.android.activity.*
import edu.capella.mobile.android.adapters.CourseViewHolder
import edu.capella.mobile.android.adapters.DiscussionForumListAdapter
import edu.capella.mobile.android.app.CapellaApplication
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class DiscussionTopicTest {


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
            ViewActions.typeText("JLAHARE"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.txtPassword))
            .perform(ViewActions.typeText("2400217Ja"), ViewActions.closeSoftKeyboard())

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


        val cAct = getCurrentActivity() as CoursesActivity

        onView(withId(R.id.currentCourseListView))
            .perform( RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(cAct.getCurrentCourseListSize()-1, click()))

        Thread.sleep(1000)

        //Espresso.onView(ViewMatchers.withId(R.id.classmatesLayout)).perform( click())
       // Espresso.onView(ViewMatchers.withText("Classmates")).perform( click())

       // onView( withId( R.id.classmatesLayout)).perform( scrollTo(), click())
        onView( ViewMatchers.withText( "Discussions")).perform( scrollTo(), click())

        Thread.sleep(1000)


        var activity = getCurrentActivity() as DiscussionForumActivity

        if(activity.getDiscussionForumListSize() > 6)
        {
           // CLICKING ON "[u02d5] Unit 2 Discussion 5"
            onView(withId(R.id.discussionForumListView))
                .perform( RecyclerViewActions.actionOnItemAtPosition<DiscussionForumListAdapter.DiscussionRowViewHolder>(5, click()))
        }
        else
        {
            onView(withId(R.id.discussionForumListView))
                .perform( RecyclerViewActions.actionOnItemAtPosition<DiscussionForumListAdapter.DiscussionRowViewHolder>(0, click()))
        }

        Thread.sleep(1000)
        /*Espresso.onView(ViewMatchers.withId(R.id.currentCourseListView))
            .perform( RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(0, click()))*/

    }

    @Test
    fun check_title_is_threads() {
        onView(withId(R.id.genericTitleTxt))
            .check(matches(ViewMatchers.withText("Threads")))
    }

    @Test
    fun check_course_information_is_available_for_processing()
    {
        var activity = getCurrentActivity() as DiscussionTopicActivity
        Assert.assertNotNull(activity.courseId)
        Assert.assertNotNull(activity.forumId)
        Assert.assertNotNull(activity.discussionTitle)
    }

    @Test
    fun check_able_to_open_create_new_thread_screen()
    {
       //Thread.sleep(20000)
        Espresso.onView(ViewMatchers.withId(R.id.createThreadImg)).perform( click())
/*
        onView(withId(R.id.createNewThreadTxt) ).check(matches(isCompletelyDisplayed())).perform(click())
        onView(withId(R.id.createNewThreadTxt)).check(matches(hasDescendant(withId(R.id.createNewThreadTxt))))*/
    }
    @Test
    fun check_view_description_link_is_operable()
    {

        Espresso.onView(ViewMatchers.withId(R.id.viewDescriptionTxt)).perform( click())

    }

    @Test
    fun check_discussion_list_have_list_non_null() {
        var activity = getCurrentActivity() as DiscussionTopicActivity
        Assert.assertTrue(activity.getTopicListSize()  >= 0)
    }



    fun getCurrentActivity(): Activity? {
        return (baseActivity.application as CapellaApplication).getCurrentRunningActivity()
    }

    @After
    fun cleanUp() {
       // Intents.release()
    }
}


