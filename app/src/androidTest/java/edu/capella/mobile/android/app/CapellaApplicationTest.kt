package edu.capella.mobile.android.app

import android.app.Activity
import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.Preferences
import edu.capella.mobile.android.activity.LoginActivity
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test


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
 * @since :  3/15/2020
 */
class CapellaApplicationTest {

    lateinit var capellaApplication : CapellaApplication

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<LoginActivity> = ActivityTestRule(LoginActivity::class.java)

    @Before
    fun initPreWork() {

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Preferences.getInstance(context)
        capellaApplication  = (activityRule.activity.application as CapellaApplication)

        Espresso.onView(ViewMatchers.withId(R.id.txtEmail)).perform(
            ViewActions.typeText("JLAHARE"),
            ViewActions.closeSoftKeyboard()
        )
        Espresso.onView(ViewMatchers.withId(R.id.txtPassword))
            .perform(ViewActions.typeText("2400217Ja"), ViewActions.closeSoftKeyboard())


        Thread.sleep(1000)

    }

    @Test
    fun check_if_application_display_ui_in_foreground()
    {
        Assert.assertEquals(capellaApplication.IS_APP_DISPLAYING , true)
    }

    @Test
    fun check_if_application_work_in_background()
    {

       // Assert.assertEquals(capellaApplication.IS_APP_DISPLAYING , true)
        sendAppInBackground(capellaApplication.getCurrentRunningActivity()!!);
        // eventually sleep, or implement an idling resource
        bringAppInForeground(capellaApplication.getCurrentRunningActivity()!!);
    }

    private fun sendAppInBackground(activity: Activity) {
        val intent = Intent()
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_HOME)
        activity.startActivity(intent)
    }

    private fun bringAppInForeground(activity: Activity) {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        activity.startActivity(intent)
    }

    @Test
    fun check_if_cleaning_cache_works_without_error()
    {
            capellaApplication.clearOldData()
            Assert.assertTrue(true) // This line executes, it means no error
    }

}
