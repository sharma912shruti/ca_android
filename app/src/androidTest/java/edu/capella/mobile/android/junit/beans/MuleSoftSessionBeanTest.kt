package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.*
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MuleSoftSessionTest {

   var muleSoftSession  =  MuleSoftSession()

    @Before
    fun setFakeData()
    {
        muleSoftSession  =  MuleSoftSession()


    }

    @Test
    fun check_for_getter_and_setter()
    {
        muleSoftSession.token = null
        Assert.assertNull(muleSoftSession.token)
    }
    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(muleSoftSession.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(muleSoftSession.hashCode())
    }

    @Test
    fun check_whether_bean_is_gson_supported()
    {
        muleSoftSession.token   ="FAKE SESSION"
        var gson = Gson( )
        var json = gson.toJson(muleSoftSession)

        var newBean = gson.fromJson(json , MuleSoftSession::class.java )

        assertEquals(muleSoftSession,newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        muleSoftSession.token   ="FAKE SESSION"
        var gson = Gson( )
        var json = gson.toJson(muleSoftSession)

        var newBean = gson.fromJson(json , MuleSoftSession::class.java )

        newBean.token  = "New Fake SESSION"

        assertNotEquals(muleSoftSession,newBean)

    }




}
