package edu.capella.mobile.android



import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry

import androidx.test.rule.ActivityTestRule
import edu.capella.mobile.android.utils.Preferences

import edu.capella.mobile.android.activity.LoginActivity
import org.junit.*

import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)


/**
 * Class FinancesDashboardTest.kt : class description goes here
 *
 * @author  : KPANDYA1
 * @version :  1.0
 * @since   :  12/3/2020
 *
 */

class FinancesDashboardTest {


    @Rule
    @JvmField
    var activityRule: ActivityTestRule<LoginActivity> = ActivityTestRule(LoginActivity::class.java)

    @Before
    fun init(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Preferences.getInstance(context)

        Espresso.onView(ViewMatchers.withId(R.id.txtEmail)).perform(
            ViewActions.typeText("JLAHARE"),
            ViewActions.closeSoftKeyboard()
        );
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
        Thread.sleep(5000)
        Espresso.onView(ViewMatchers.withId(R.id.drawerImageView)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Finances")).perform(ViewActions.click())

        Thread.sleep(10000)
    }


    @Test
    fun check_if_menu_is_able_to_open()
    {
//         Espresso.onView(ViewMatchers.withId(R.id.drawerImageView)).perform(ViewActions.click())
        onView(withId(R.id.dotImage)).perform(click())
    }

    @Test
    fun check_if_inapp_browser_have_desired_components() {
        onView(withId(R.id.openBrowserTxt)).check(matches(withText("Open in browser")))
        onView(withId(R.id.copyLintTxt)).check(matches(withText("Copy link")))
        onView(withId(R.id.shareTxt)).check(matches(withText("Share…")))
    }

}
