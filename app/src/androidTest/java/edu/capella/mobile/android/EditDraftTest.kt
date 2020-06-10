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
class EditDraftTest {


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

        onView(withId(R.id.currentCourseListView))
            .perform( RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(3, click()))

        Thread.sleep(1000)

        //Espresso.onView(ViewMatchers.withId(R.id.classmatesLayout)).perform( click())
       // Espresso.onView(ViewMatchers.withText("Classmates")).perform( click())

        onView( withId( R.id.discussionDraftLayout)).perform( scrollTo(), click())
        Thread.sleep(1000)

        var activity = getCurrentActivity() as DiscussionDraftActivity
       if(activity.getDiscussionDraftListSize() >= 0)
       {
           Espresso.onView(ViewMatchers.withId(R.id.discussionDraftListView))
           .perform( RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(0, click()))
       }

        Thread.sleep(100)

        try {
            onView(withId(R.id.cancelTV)).check((matches(isDisplayed())))
                .perform(click())
        }catch (noView: Throwable)
        {
            //THIS IS OK AS WE HAVE COACH SCREEN ONLY FOR FIRST TIME, SO CATCH THIS
            //EXCEPTION OTHERWISE WHOLE TEST WILL FAIL : JAYESH
        }



    }

    @Test
    fun check_title_is_EditDraft() {
            onView(withId(R.id.headerTxt))
                .check(matches(ViewMatchers.withText("Edit Draft")))

            onView(withId(R.id.backTxt))
                .check(matches(ViewMatchers.withText("Cancel")))

//        onView(withId(R.id.headerTxt))
//            .check(matches(ViewMatchers.withText("Edit Draft")))
//
//        onView(withId(R.id.backTxt))
//            .check(matches(ViewMatchers.withText("Cancel")))
    }

    @Test
    fun check_subject_should_be_editable()
    {
            onView(withId(R.id.subjectTxt)).perform(ViewActions.typeText(" test"))
    }

    @Test
    fun check_discription_should_be_editable()
    {
            onView(withId(R.id.descriptionTxt)).perform(ViewActions.typeText(" Sample draft"))

    }

  /*  @Test
    fun check_whether_click_save_as_draft_button_works() {
//        onView(withId(R.id.subjectTxt)).perform(ViewActions.typeText(" test"))
//        onView(withId(R.id.descriptionTxt)).perform(ViewActions.typeText(" Sample draft"))
            onView(withId(R.id.saveDraftTxt)).perform(click())
    }*/

    /*@Test
    fun check_whether_submit_as_draft_button_works() {
            onView(withId(R.id.submitTxt)).perform(click())
    }*/


    fun getCurrentActivity(): Activity? {
        return (baseActivity.application as CapellaApplication).getCurrentRunningActivity()
    }

    @After
    fun cleanUp() {
       // Intents.release()
    }
}


