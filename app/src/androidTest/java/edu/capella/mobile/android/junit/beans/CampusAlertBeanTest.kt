package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.CampusAlertBean
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test


class CampusAlertBeanTest {

   var campusAlertBean  =  CampusAlertBean()

    @Before
    fun setFakeData()
    {
        campusAlertBean  =  CampusAlertBean()

    }
    @Test
    fun check_for_getter_and_setter()
    {
        campusAlertBean.eventMessage = null
        campusAlertBean.serverName = null
        campusAlertBean.trackingId = null

        Assert.assertNull(campusAlertBean.eventMessage)
        Assert.assertNull(campusAlertBean.serverName)
        Assert.assertNull(campusAlertBean.trackingId)
    }

    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(campusAlertBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(campusAlertBean.hashCode())
    }
    @Test
    fun check_whether_bean_is_gson_supported()
    {
        campusAlertBean.trackingId ="Dummy tracking id"
        var gson = Gson( )
        var json = gson.toJson(campusAlertBean)

        var newBean = gson.fromJson(json , CampusAlertBean::class.java )

        assertEquals(campusAlertBean,newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        campusAlertBean.trackingId ="Dummy tracking id"
        var gson = Gson( )
        var json = gson.toJson(campusAlertBean)

        var newBean = gson.fromJson(json , CampusAlertBean::class.java )
        newBean.trackingId = "New tracking id"

        assertNotEquals(campusAlertBean,newBean)

    }




}
