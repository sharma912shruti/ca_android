package edu.capella.mobile.android.junit

import androidx.test.platform.app.InstrumentationRegistry
import edu.capella.mobile.android.utils.PreferenceKeys
import edu.capella.mobile.android.utils.Preferences

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class SharedPrefrenceTest {



    @Before
    fun initialize()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Preferences.getInstance(context)
    }

    @Test
    fun check_if_able_to_add_string()
    {

        Preferences.addValue(PreferenceKeys.USER_NAME , "Capella Secret User")

        assertThat(Preferences.getValue(PreferenceKeys.USER_NAME) ,  equalTo("Capella Secret User") )
    }

    @Test
    fun check_if_able_to_add_boolean()
    {

        Preferences.addBoolean(PreferenceKeys.IS_APP_GONE_OUTSIDE , false)

        assertThat(Preferences.getBoolean(PreferenceKeys.IS_APP_GONE_OUTSIDE) ,  equalTo(false) )
    }

    @Test
    fun check_if_able_to_add_int()
    {

        Preferences.addInt(PreferenceKeys.SECRET , 12345)

        assertThat(Preferences.getInt(PreferenceKeys.SECRET) ,  equalTo(12345) )
    }




}
