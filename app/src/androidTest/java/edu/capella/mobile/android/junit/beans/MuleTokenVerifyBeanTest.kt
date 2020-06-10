package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson

import edu.capella.mobile.android.bean.MuleTokenVerify

import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MuleTokenVerifyTest {

   var muleTokenVerify  =  MuleTokenVerify()

    @Before
    fun setFakeData()
    {
        muleTokenVerify  =  MuleTokenVerify()


    }
    @Test
    fun check_for_getter_and_setter()
    {
        muleTokenVerify.capellaLearnerVerify = null
        muleTokenVerify.exp = null
        muleTokenVerify.expires_in = null
        muleTokenVerify.iat = null
        muleTokenVerify.iss = null
        muleTokenVerify.ssn = null
        muleTokenVerify.sub = null

        Assert.assertNull(muleTokenVerify.capellaLearnerVerify)
        Assert.assertNull(muleTokenVerify.exp)
        Assert.assertNull(muleTokenVerify.expires_in)
        Assert.assertNull(muleTokenVerify.iat)
        Assert.assertNull(muleTokenVerify.iss)
        Assert.assertNull(muleTokenVerify.ssn)
        Assert.assertNull(muleTokenVerify.sub)
    }

    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(muleTokenVerify.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(muleTokenVerify.hashCode())
    }

    @Test
    fun check_whether_bean_is_gson_supported()
    {
        muleTokenVerify.ssn  ="FAKE SSN"
        var gson = Gson( )
        var json = gson.toJson(muleTokenVerify)

        var newBean = gson.fromJson(json , MuleTokenVerify::class.java )

        assertEquals(muleTokenVerify,newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        muleTokenVerify.ssn  ="FAKE SSN"
        var gson = Gson( )
        var json = gson.toJson(muleTokenVerify)

        var newBean = gson.fromJson(json , MuleTokenVerify::class.java )

        newBean.ssn  = "New Fake SSN"

        assertNotEquals(muleTokenVerify,newBean)

    }




}
