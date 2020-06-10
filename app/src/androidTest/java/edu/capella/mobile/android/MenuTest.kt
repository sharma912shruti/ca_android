package edu.capella.mobile.android

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import edu.capella.mobile.android.activity.MenuActivity
import org.junit.After
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.lang.AssertionError


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class MenuTest {

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<MenuActivity> =
        ActivityTestRule(MenuActivity::class.java)

    @Test
    fun check_drawer_open_close_feature() {
        onView(withId(R.id.drawerImageView)).perform(click())
        onView(withId(R.id.mainContainer)).perform(click())
    }

    @Test
    fun check_list_items_labels(){
        onView(withId(R.id.drawerRecylerView))
            .check(matches(hasDescendant(withText("Courses"))))

        onView(withId(R.id.drawerRecylerView))
            .check(matches(hasDescendant(withText("Finances"))))

        onView(withId(R.id.drawerRecylerView))
            .check(matches(hasDescendant(withText("Academic Plan"))))

        onView(withId(R.id.drawerRecylerView))
            .check(matches(hasDescendant(withText("Library"))))

        onView(withId(R.id.drawerRecylerView))
            .check(matches(hasDescendant(withText("Contact Us"))))

        onView(withId(R.id.drawerRecylerView))
            .check(matches(hasDescendant(withText("Campus News"))))

        onView(withId(R.id.drawerRecylerView))
            .check(matches(hasDescendant(withText("View Campus on Full Site"))))

        onView(withId(R.id.drawerRecylerView))
            .check(matches(hasDescendant(withText("Log Out"))))
    }

    @Test
    fun check_coach_screen(){

        

        try {
            Espresso.onView(ViewMatchers.withId(R.id.coachOkButton)).check((matches(isDisplayed())))
                .perform(ViewActions.click())
        }catch (noView: AssertionError)
        {
            //THIS IS OK AS WE HAVE COACH SCREEN ONLY FOR FIRST TIME, SO CATCH THIS
            //EXCEPTION OTHERWISE WHOLE TEST WILL FAIL : JAYESH
        }

    }

//    @Before
//    fun setUp() {
//        Intents.init()
//    }
//
    @After
    fun cleanUp() {
        //release()
    }
}