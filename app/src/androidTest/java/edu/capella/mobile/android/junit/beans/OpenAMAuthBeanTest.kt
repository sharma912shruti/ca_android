package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.OpenAMAuth
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class OpenAMAuthTest {

   var openAMAuth  =  OpenAMAuth()

    @Before
    fun setFakeData()
    {
        openAMAuth  =  OpenAMAuth()
    }
    @Test
    fun check_for_getter_and_setter()
    {
        openAMAuth.realm = null
        openAMAuth.successUrl = null
        openAMAuth.tokenId = null
        Assert.assertNull(openAMAuth.realm)
        Assert.assertNull(openAMAuth.successUrl)
        Assert.assertNull(openAMAuth.tokenId)
    }

    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(openAMAuth.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(openAMAuth.hashCode())
    }

    @Test
    fun check_whether_bean_is_gson_supported()
    {
        openAMAuth.tokenId ="DUMMY_TOKEN"
        var gson = Gson( )
        var json = gson.toJson(openAMAuth)

        var newBean = gson.fromJson<OpenAMAuth>(json , OpenAMAuth::class.java )

        assertEquals(openAMAuth,newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        openAMAuth.tokenId ="DUMMY_TOKEN"
        var gson = Gson( )
        var json = gson.toJson(openAMAuth)

        var newBean = gson.fromJson<OpenAMAuth>(json , OpenAMAuth::class.java )
        newBean.tokenId = "New Fake Token"

        assertNotEquals(openAMAuth,newBean)

    }




}
