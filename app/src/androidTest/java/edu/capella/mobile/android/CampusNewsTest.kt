package edu.capella.mobile.android



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
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters


//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)

class CampusNewsTest {


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
    fun check_if_title_is_campus_news()
    {
        Espresso.onView(ViewMatchers.withId(R.id.toolbarTitle))
            .check(ViewAssertions.matches(ViewMatchers.withText("Campus News")))
    }




    @Test
    fun check_if_profile_option_menu_option_works()
    {
        Espresso.onView(ViewMatchers.withId(R.id.drawerImageView)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.profileBaseLayout)).perform(ViewActions.click())

    }

    @Test
    fun check_if_logout_option_menu_option_works()
    {
         Espresso.onView(ViewMatchers.withId(R.id.drawerImageView)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Log Out")).perform(ViewActions.click())

    }

    @Test
    fun check_if_courses_menu_option_works()
    {
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.drawerImageView)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Courses")).perform(ViewActions.click())
        intended(hasComponent(CoursesActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun check_if_library_menu_option_works()
    {
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.drawerImageView)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Library")).perform(ViewActions.click())
        intended(hasComponent(LibraryActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun check_if_contact_us_menu_option_works()
    {
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.drawerImageView)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Contact Us")).perform(ViewActions.click())
        intended(hasComponent(ContactUsActivity::class.java.name))
        Intents.release()
    }

}
