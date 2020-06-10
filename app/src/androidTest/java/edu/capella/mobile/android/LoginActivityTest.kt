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
import edu.capella.mobile.android.app.CapellaApplication
import org.junit.*

import org.junit.runner.RunWith
import org.junit.runners.MethodSorters




//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)

class LoginActivityTest {


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
    }

    @Test
    fun check_package_name() {

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals(currentPkg, appContext.packageName)
    }

    @Test
    fun check_email_should_be_editable()
    {
        onView(withId(R.id.txtEmail)).perform(typeText("JLAHARE"))
    }

    @Test
    fun check_password_should_be_editable()
    {
        onView(withId(R.id.txtPassword)).perform(typeText("PASSWORD"))

    }

    @Test
    fun check_internet_on_off() {

        //Thread.sleep(20000)

        var activity = getCurrentActivity() as LoginActivity
        Assert.assertTrue(activity.isInternetConnection)

    }
    

    @Test
    fun check_login_screen_components() {
        onView(withId(R.id.txtEmail)).check(matches(withHint("Email/Username")))
        onView(withId(R.id.txtPassword)).check(matches(withHint("Password")))
        onView(withId(R.id.btnLogin)).check(matches(withText("Log In")))

        onView(withId(R.id.keepMeLbl)).check(matches(withText("Keep Me Signed In")))
        onView(withId(R.id.forgotUserTV)).check(matches(withText("Forgot Username")))
        onView(withId(R.id.resetPswTV)).check(matches(withText("Reset Password")))

        onView(withId(R.id.tollNumberTxt)).check(matches(withText("1-888-227-3552")))
        var version  = baseActivity.resources.getString(R.string.capella_mobile)+" "+BuildConfig.VERSION_NAME+" ("+BuildConfig.VERSION_CODE+")"
        onView(withId(R.id.versionTxt)).check(matches(withText(version)))

    }

    @Test
    fun check_whether_its_accepting_email_and_password() {
        onView(withId(R.id.txtEmail)).perform(
            typeText("JLAHARE"),
            closeSoftKeyboard()
        );
        onView(withId(R.id.txtPassword))
            .perform(typeText("2400217Ja"), closeSoftKeyboard())


    }

    @Test
    fun check_whether_keep_me_sign_in_clickable() {
        onView(withId(R.id.keepMeLbl)).perform(click())
    }

    @Test
    fun check_whether_keep_me_sign_in_popup_cancel_button_works() {
        onView(withId(R.id.keepMeLbl)).perform(click())
        onView(withId(R.id.cancelTV)).perform(click())
    }

    @Test
    fun check_whether_keep_me_sign_in_popup_confirm_button_works() {
        onView(withId(R.id.keepMeLbl)).perform(click())
        onView(withId(R.id.comfirmTV)).perform(click())

    }

    @Test
    fun check_login_api_is_working_with_correct_creds_and_main_menu_appears_after_login()
    {
       // Intents.init()

        onView(withId(R.id.txtEmail)).perform(
            typeText("JLAHARE"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.txtPassword))
            .perform(typeText("2400217Ja"), closeSoftKeyboard())

        onView(withId(R.id.btnLogin)).perform(click())
        intended(hasComponent(CampusActivity::class.java.name))
      //  Intents.release()

//        Thread.sleep(5000)
//        val campusActivityRule: IntentsTestRule<CampusActivity> = IntentsTestRule(CampusActivity::class.java)
//        Assert.assertEquals(true, campusActivityRule.activity.isDataInCampusNewsList())

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
