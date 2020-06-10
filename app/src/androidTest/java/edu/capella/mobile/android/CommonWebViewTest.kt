package edu.capella.mobile.android

import android.app.Activity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import edu.capella.mobile.android.utils.Preferences
import edu.capella.mobile.android.activity.LoginActivity
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class CommonWebViewTest {

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
                typeText("SSHARMA45"),
                closeSoftKeyboard()
        )
        onView(withId(R.id.txtPassword))
                .perform(typeText("2402451Sh"), closeSoftKeyboard())

        onView(withId(R.id.btnLogin)).perform(click())


        try {
            onView(withId(R.id.coachOkButton)).check((ViewAssertions.matches(ViewMatchers.isDisplayed())))
                    .perform(click())
        } catch (noView: Throwable) {
            //THIS IS OK AS WE HAVE COACH SCREEN ONLY FOR FIRST TIME, SO CATCH THIS
            //EXCEPTION OTHERWISE WHOLE TEST WILL FAIL :
        }


        Thread.sleep(1000)

        onView(withId(R.id.drawerImageView)).perform(click())
        onView(ViewMatchers.withText("Finances")).perform(click())

//        onView(withId(R.id.currentCourseListView))
//                .perform(RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(0, click()))

        Thread.sleep(1000)

    }



    @Test
    fun check_if_menu_is_able_to_open() {
        Thread.sleep(100)
        onView(withId(R.id.dotImage)).perform(click())

    }
   /* @Test
    fun check_if_share_is_able_to_work() {
        Thread.sleep(100)
        onView(withId(R.id.dotImage)).perform(click())
        onView(withId(R.id.shareTxt)).perform(click())

    }
    @Test
    fun check_if_browse_is_able_to_work() {
        Thread.sleep(100)
        onView(withId(R.id.dotImage)).perform(click())
        onView(withId(R.id.openBrowserTxt)).perform(click())

    }*/





}
