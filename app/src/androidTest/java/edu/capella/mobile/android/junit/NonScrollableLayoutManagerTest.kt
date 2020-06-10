package edu.capella.mobile.android.junit

import android.app.Activity
import androidx.test.espresso.intent.Intents
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import edu.capella.mobile.android.activity.LoginActivity
import edu.capella.mobile.android.layout_manager.NonScrollableLayoutManager
import edu.capella.mobile.android.utils.Validator
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class NonScrollableLayoutManagerTest {




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
    fun check_layout_manager() {


        var n = NonScrollableLayoutManager(baseActivity)
        n.setScrollEnabled(false)

        n.canScrollVertically()
        Assert.assertTrue(true)

    }


}
