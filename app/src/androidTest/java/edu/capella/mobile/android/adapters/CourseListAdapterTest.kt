package edu.capella.mobile.android.adapters


import android.app.Activity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.Preferences
import edu.capella.mobile.android.activity.CoursesActivity
import edu.capella.mobile.android.activity.LoginActivity
import edu.capella.mobile.android.app.CapellaApplication


import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

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




//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)

class CourseListAdapterTest {

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
            Espresso.onView(ViewMatchers.withId(R.id.coachOkButton))
                .check((matches(ViewMatchers.isDisplayed())))
                .perform(ViewActions.click())
        } catch (noView: Throwable) {

        }

        Espresso.onView(ViewMatchers.withId(R.id.drawerImageView)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Courses")).perform(ViewActions.click())

        // STATY ON CURRENT COURSE SCREEN AND TEST FOR ADAPTER
       /* Espresso.onView(ViewMatchers.withId(R.id.currentCourseListView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(
                    1,
                    ViewActions.click()
                )
            )*/


        Thread.sleep(10000)

    }

    @Test
    fun check_classmate_list_adapter_is_non_null() {

        var activity = getCurrentActivity() as CoursesActivity

        Assert.assertNotEquals(activity.getCurrentCourseAdapter() , null)
    }

    @Test
    fun check_classmate_item_list_is_non_null() {

        var activity = getCurrentActivity() as CoursesActivity

        Assert.assertNotEquals(activity.getCurrentCourseAdapter()!!.items , null)
    }

    @Test
    fun check_classmate_context_is_non_null() {

        var activity = getCurrentActivity() as CoursesActivity

        Assert.assertNotEquals(activity.getCurrentCourseAdapter()!!.context , null)
    }

    @Test
    fun check_classmate_item_listener_is_non_null() {

        var activity = getCurrentActivity() as CoursesActivity

        Assert.assertNotEquals(activity.getCurrentCourseAdapter()!!.courseItemListener , null)
    }

    @Test
    fun check_classmate_picture_loader_is_non_null() {

        var activity = getCurrentActivity() as CoursesActivity

        Assert.assertNotEquals(activity.getCurrentCourseAdapter()!!.picLoader , null)
    }




    fun getCurrentActivity(): Activity? {
        return (baseActivity.application as CapellaApplication).getCurrentRunningActivity()
    }
}
