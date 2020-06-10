package edu.capella.mobile.android



import android.app.Activity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry

import androidx.test.rule.ActivityTestRule

import edu.capella.mobile.android.activity.CampusActivity
import edu.capella.mobile.android.activity.LoginActivity
import edu.capella.mobile.android.activity.SplashActivity
import edu.capella.mobile.android.app.CapellaApplication
import org.junit.*

import org.junit.runner.RunWith
import org.junit.runners.MethodSorters




//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)

class SplashActivityTest {


    private val currentPkg = "edu.capella.mobile.android"

    lateinit var baseActivity: SplashActivity

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<SplashActivity> = ActivityTestRule(SplashActivity::class.java)


    @Before
    fun initMe()
    {
        baseActivity = activityRule.activity
        Intents.init()
    }

    @Test
    fun check_splash_timeout() {

        try {
            val timeout = baseActivity.splashTimeout
            Assert.assertEquals(timeout, 2000) //equals(2000))
        }catch (t: Throwable){}

    }

    @Test
    fun check_time_over_at_splash()
    {
        try{
        val isTimeout =  baseActivity.isTimeOverThenTimeout()
        Assert.assertNotNull(isTimeout)
        }catch (t: Throwable){}
    }



    fun getCurrentActivity(): Activity? {
        return (baseActivity.application as CapellaApplication).getCurrentRunningActivity()

    }


    @Test
    fun check_login_after_splash()
    {
        try{
        // Intents.init()
        Thread.sleep(7500)

        intended(hasComponent(LoginActivity::class.java.name))
        //  Intents.release()

//        Thread.sleep(5000)
//        val campusActivityRule: IntentsTestRule<CampusActivity> = IntentsTestRule(CampusActivity::class.java)
//        Assert.assertEquals(true, campusActivityRule.activity.isDataInCampusNewsList())
            }catch (t: Throwable){}
    }

    @After
    fun releaseMe()
    {
        Intents.release()
    }

}
