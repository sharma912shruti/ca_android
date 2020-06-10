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
class GettingStartedBeanTest {

   var gettingStartedBean  =  GettingStartedBean()

    @Before
    fun setFakeData()
    {
        gettingStartedBean  =  GettingStartedBean()

    }
    @Test
    fun check_for_getter_and_setter()
    {
        gettingStartedBean.gettingstarted= null
        gettingStartedBean.gettingstarted = GettingStartedBean.GettingStarted()
        gettingStartedBean.gettingstarted?.contentId = null
        gettingStartedBean.gettingstarted?.gettingStartedContent = null
        gettingStartedBean.gettingstarted?.longCourseId = null


        Assert.assertNull(gettingStartedBean.gettingstarted?.contentId )
        Assert.assertNull(gettingStartedBean.gettingstarted?.gettingStartedContent)
        Assert.assertNull(gettingStartedBean.gettingstarted?.longCourseId )

    }
    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(gettingStartedBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(gettingStartedBean.hashCode())
    }
    @Test
    fun check_whether_bean_is_gson_supported()
    {

        var gson = Gson( )
        var json = gson.toJson(gettingStartedBean)

        var newBean = gson.fromJson(json , GettingStartedBean::class.java )

        assertEquals(gettingStartedBean,newBean)

    }






}
