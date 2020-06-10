package edu.capella.mobile.android.junit

import android.app.Activity
import androidx.test.espresso.intent.Intents
import androidx.test.rule.ActivityTestRule
import edu.capella.mobile.android.activity.LoginActivity
import edu.capella.mobile.android.interfaces.EventListener
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class EventListenerTest {




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
    fun check_event_interface() {

         var e = TestEventListener()
         e.cancleUpdate()
        e.cancel()
        e.update()
        e.confirm()

        Assert.assertNotNull(e)

    }

    inner class TestEventListener : EventListener
    {
        override fun cancel() {
            super.cancel()
        }

        override fun cancleUpdate() {
            super.cancleUpdate()
        }

        override fun confirm() {
            super.confirm()
        }

        override fun update() {
            super.update()
        }
    }


}
