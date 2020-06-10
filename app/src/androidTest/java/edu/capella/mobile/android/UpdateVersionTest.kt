package edu.capella.mobile.android

import android.app.Activity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import edu.capella.mobile.android.activity.LoginActivity
import edu.capella.mobile.android.app.CapellaApplication
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters




//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)

class UpdateVersionTest {


    private val currentPkg = "edu.capella.mobile.android"

    lateinit var baseActivity: Activity

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<LoginActivity> = ActivityTestRule(LoginActivity::class.java)


    @Before
    fun initMe()
    {
        baseActivity = activityRule.activity
        Intents.init()

        var activity = getCurrentActivity() as LoginActivity

        activity.openUpdateScreen()
    }


    @Test
    fun check_whether_get_update_button_works() {

        onView(withId(R.id.updateTxt)).perform(click())
    }

    @Test
    fun check_whether_cross_button_works() {

        onView(withId(R.id.backButton)).perform(click())
    }

    fun getCurrentActivity(): Activity? {
        return (baseActivity.application as CapellaApplication).getCurrentRunningActivity()

    }



    @After
    fun releaseMe()
    {
        Intents.release()
    }

}
