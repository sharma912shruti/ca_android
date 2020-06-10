package edu.capella.mobile.android
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import edu.capella.mobile.android.utils.Preferences
import edu.capella.mobile.android.activity.*
import edu.capella.mobile.android.adapters.CourseViewHolder
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters


//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)

class AlertDetailTest {


    @Rule
    @JvmField
    var activityRule: ActivityTestRule<LoginActivity> = ActivityTestRule(LoginActivity::class.java)

    @Before
    fun init(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Preferences.getInstance(context)

        Espresso.onView(ViewMatchers.withId(R.id.txtEmail)).perform(
            ViewActions.typeText("kpandya1"),
            ViewActions.closeSoftKeyboard()
        );
        Espresso.onView(ViewMatchers.withId(R.id.txtPassword))
            .perform(ViewActions.typeText("2402453Pa"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.btnLogin)).perform(ViewActions.click())

        try {
            Espresso.onView(ViewMatchers.withId(R.id.coachOkButton)).check((matches(isDisplayed())))
                .perform(ViewActions.click())
        }catch (noView: Throwable)
        {
            //THIS IS OK AS WE HAVE COACH SCREEN ONLY FOR FIRST TIME
        }


        Espresso.onView(ViewMatchers.withId(R.id.campusListView))
            .perform( RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(0,
                ViewActions.click()
            ))
    }


    @Test
    fun check_if_title_is_Alert_detail()
    {
        Espresso.onView(ViewMatchers.withId(R.id.headerTxt))
            .check(ViewAssertions.matches(ViewMatchers.withText("Alerts")))
    }


}
