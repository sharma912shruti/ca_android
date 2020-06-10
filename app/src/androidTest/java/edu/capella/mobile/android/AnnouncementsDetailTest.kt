package edu.capella.mobile.android

import android.app.Activity
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import edu.capella.mobile.android.utils.Preferences
import edu.capella.mobile.android.activity.LoginActivity
import edu.capella.mobile.android.adapters.CourseViewHolder
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters


//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)

class AnnouncementsDetailTest {

    private val currentPkg = "edu.capella.mobile.android"

    lateinit var baseActivity: Activity

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
    fun init() {

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Preferences.getInstance(context)
        baseActivity = activityRule.activity
        Espresso.onView(ViewMatchers.withId(R.id.txtEmail)).perform(
            ViewActions.typeText("kpandya1"),
            ViewActions.closeSoftKeyboard()
        )
        Espresso.onView(ViewMatchers.withId(R.id.txtPassword))
            .perform(ViewActions.typeText("2402453Pa"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.btnLogin)).perform(ViewActions.click())


        try {
            Espresso.onView(ViewMatchers.withId(R.id.coachOkButton))
                .check((matches(ViewMatchers.isDisplayed())))
                .perform(ViewActions.click())
        } catch (noView: Throwable) {

        }

        Espresso.onView(ViewMatchers.withId(R.id.drawerImageView)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Courses")).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.currentCourseListView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(
                    2,
                    ViewActions.click()
                )
            )

        Espresso.onView(ViewMatchers.withId(R.id.announcementLayout)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.announcementsListView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(
                    0,
                    ViewActions.click()
                )
            )
        Thread.sleep(10000)

    }

    @Test
    fun check_title_is_AnnouncementDetail() {

        Espresso.onView(withId(R.id.headerTxt))
            .check(matches(ViewMatchers.withText("Announcements")))
    }


}
