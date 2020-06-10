package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.OpenAMValidToken
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class OpenAMValidateTokenTest {

   var openAMValidToken  =  OpenAMValidToken()

    @Before
    fun setFakeData()
    {
        openAMValidToken  =  OpenAMValidToken()
    }

    @Test
    fun check_for_getter_and_setter()
    {
        openAMValidToken.realm = null
        openAMValidToken.uid = null
        openAMValidToken.valid = null
        Assert.assertNull(openAMValidToken.realm)
        Assert.assertNull(openAMValidToken.uid)
        Assert.assertNull(openAMValidToken.valid)
    }

    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(openAMValidToken.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(openAMValidToken.hashCode())
    }

    @Test
    fun check_whether_bean_is_gson_supported()
    {
        openAMValidToken.uid  ="FAKE UID"
        var gson = Gson( )
        var json = gson.toJson(openAMValidToken)

        var newBean = gson.fromJson(json , OpenAMValidToken::class.java )

        assertEquals(openAMValidToken,newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        openAMValidToken.uid  ="FAKE UID"
        var gson = Gson( )
        var json = gson.toJson(openAMValidToken)

        var newBean = gson.fromJson(json , OpenAMValidToken::class.java )

        newBean.uid  = "New Fake UID"

        assertNotEquals(openAMValidToken,newBean)

    }

}
