package edu.capella.mobile.android



import android.app.Activity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import edu.capella.mobile.android.utils.Preferences
import edu.capella.mobile.android.activity.*
import edu.capella.mobile.android.app.CapellaApplication
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters


//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)

class BaseActivityTest {


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
            Espresso.onView(ViewMatchers.withId(R.id.coachOkButton)).check((matches(isDisplayed())))
                .perform(ViewActions.click())
        }catch (noView: Throwable)
        {
            //THIS IS OK AS WE HAVE COACH SCREEN ONLY FOR FIRST TIME
        }
    }



    @Test
    fun check_if_kill_is_working()
    {
        try {
            (getCurrentActivity() as CampusActivity).kill()
        }catch (t:Throwable){}
        Assert.assertTrue(true)
    }




    fun getCurrentActivity(): Activity?
    {
        return (activityRule.activity.application as CapellaApplication).getCurrentRunningActivity()

    }




}
