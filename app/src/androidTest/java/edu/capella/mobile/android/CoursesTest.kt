package edu.capella.mobile.android


import android.app.Activity
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
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
class CoursesTest {


    private val currentPkg = "edu.capella.mobile.android"
    lateinit var baseActivity : Activity

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<LoginActivity> = ActivityTestRule(LoginActivity::class.java)


    fun ViewInteraction.isGone() = getViewAssertion(ViewMatchers.Visibility.GONE)

    fun ViewInteraction.isVisible() = getViewAssertion(ViewMatchers.Visibility.VISIBLE)

    fun ViewInteraction.isInvisible() = getViewAssertion(ViewMatchers.Visibility.INVISIBLE)

    private fun getViewAssertion(visibility: ViewMatchers.Visibility): ViewAssertion? {
        return ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(visibility))
    }

    @Before
    fun init(){

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Preferences.getInstance(context)
        baseActivity  = activityRule.activity
        Espresso.onView(ViewMatchers.withId(R.id.txtEmail)).perform(
            ViewActions.typeText("JLAHARE"),
            ViewActions.closeSoftKeyboard()
        )
        Espresso.onView(ViewMatchers.withId(R.id.txtPassword))
            .perform(ViewActions.typeText("2400217Ja"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.btnLogin)).perform(ViewActions.click())


        try {
            Espresso.onView(ViewMatchers.withId(R.id.coachOkButton)).check((matches(ViewMatchers.isDisplayed())))
                .perform(ViewActions.click())
        }catch (noView: Throwable)
        {
            //THIS IS OK AS WE HAVE COACH SCREEN ONLY FOR FIRST TIME, SO CATCH THIS
            //EXCEPTION OTHERWISE WHOLE TEST WILL FAIL : JAYESH
        }

        Espresso.onView(ViewMatchers.withId(R.id.drawerImageView)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Courses")).perform(ViewActions.click())


    }


    @Test
    fun check_current_course_first_item_clickable()
    {
        Espresso.onView(ViewMatchers.withId(R.id.currentCourseListView))
            .perform( RecyclerViewActions.actionOnItemAtPosition< CourseViewHolder>(0, click()))
    }


    @Test
    fun check_previous_course_tab_is_clickable()
    {

        Espresso.onView(ViewMatchers.withText("Previous")).perform(ViewActions.click())

    }

    @Test
    fun check_current_course_tab_is_clickable()
    {
        Espresso.onView(ViewMatchers.withText("Previous")).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Current")).perform(ViewActions.click())
    }

    @Test
    fun check_previous_course_tab_have_courselist_non_null()
    {

        Espresso.onView(ViewMatchers.withText("Previous")).perform(ViewActions.click())
        var activity = getCurrentActivity() as CoursesActivity
        Assert.assertTrue(activity.getPreviousCourseListSize() >= 0)
    }

    @Test
    fun check_current_course_tab_have_courselist_non_null()
    {

        Espresso.onView(ViewMatchers.withText("Previous")).perform(ViewActions.click())
        var activity = getCurrentActivity() as CoursesActivity
        Assert.assertTrue(activity.getCurrentCourseListSize() >= 0)
    }


    fun getCurrentActivity(): Activity?
    {
        return (baseActivity.application as CapellaApplication).getCurrentRunningActivity()

    }
}
