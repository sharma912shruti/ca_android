package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.MuleSoftToken
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MuleSoftTokenTest {

   var muleSoftToken  =  MuleSoftToken()

    @Before
    fun setFakeData()
    {
        muleSoftToken  =  MuleSoftToken()


    }
    @Test
    fun check_for_getter_and_setter()
    {
        muleSoftToken.access_token = null
        muleSoftToken.expires_in = null
        muleSoftToken.scope = null
        muleSoftToken.token_type = null
        Assert.assertNull(muleSoftToken.access_token)
        Assert.assertNull(muleSoftToken.expires_in)
        Assert.assertNull(muleSoftToken.scope)
        Assert.assertNull(muleSoftToken.token_type)
    }

    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(muleSoftToken.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(muleSoftToken.hashCode())
    }

    @Test
    fun check_whether_bean_is_gson_supported()
    {
        muleSoftToken.access_token  ="FAKE ACCESS TOKEN"
        var gson = Gson( )
        var json = gson.toJson(muleSoftToken)

        var newBean = gson.fromJson(json , MuleSoftToken::class.java )

        assertEquals(muleSoftToken,newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        muleSoftToken.access_token  ="FAKE ACCESS TOKEN"
        var gson = Gson( )
        var json = gson.toJson(muleSoftToken)

        var newBean = gson.fromJson(json , MuleSoftToken::class.java )

        newBean.access_token  = "New Fake ACESS TOKEN"

        assertNotEquals(muleSoftToken,newBean)

    }




}
