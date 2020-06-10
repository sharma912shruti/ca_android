package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.CacheCourseRoomBean
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  09-05-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */

class CacheCourseRoomBeanTest {


    var cacheCourseBean  =  CacheCourseRoomBean()

    @Before
    fun setFakeData()
    {
        cacheCourseBean  =  CacheCourseRoomBean()

    }
    @Test
    fun check_for_getter_and_setter()
    {
        cacheCourseBean.courseId = null
        cacheCourseBean.jsonData = null
        cacheCourseBean.type = null

        Assert.assertNull(cacheCourseBean.courseId)
        Assert.assertNull(cacheCourseBean.jsonData)
        Assert.assertNull(cacheCourseBean.type)
    }

    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(cacheCourseBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(cacheCourseBean.hashCode())
    }
    @Test
    fun check_whether_bean_is_gson_supported()
    {
        cacheCourseBean.type ="Dummy tracking id"
        var gson = Gson( )
        var json = gson.toJson(cacheCourseBean)

        var newBean = gson.fromJson(json , CacheCourseRoomBean::class.java )

        Assert.assertNotNull( newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        cacheCourseBean.type ="Dummy tracking id"
        var gson = Gson( )
        var json = gson.toJson(cacheCourseBean)

        var newBean = gson.fromJson(json , CacheCourseRoomBean::class.java )
        newBean.type = "New tracking id"

        Assert.assertNotEquals(cacheCourseBean, newBean)

    }
}
