package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.CourseAssessmentBean
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  22-04-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */

class CourseAssessmentBeanTest {


    var courseAssessmentBean  =  CourseAssessmentBean()

    @Before
    fun setFakeData()
    {
        courseAssessmentBean  =  CourseAssessmentBean()

    }

    @Test
    fun check_for_getter_and_setter()
    {

        courseAssessmentBean.courseUnits = null
        courseAssessmentBean.serverName = null
        courseAssessmentBean.trackingId = null

        Assert.assertNull(courseAssessmentBean.courseUnits)
        Assert.assertNull(courseAssessmentBean.serverName)
        Assert.assertNull(courseAssessmentBean.trackingId)
    }

    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(courseAssessmentBean.toString())
    }

    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(courseAssessmentBean.hashCode())
    }

    @Test
    fun check_whether_bean_is_gson_supported()
    {
        var gson = Gson( )
        var json = gson.toJson(courseAssessmentBean)

        var newBean = gson.fromJson(json , CourseAssessmentBean::class.java )

        Assert.assertEquals(courseAssessmentBean, newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        var gson = Gson( )
        var json = gson.toJson(courseAssessmentBean)

        var newBean = gson.fromJson(json , CourseAssessmentBean::class.java )
        newBean.serverName = "New feed name"

        Assert.assertNotEquals(courseAssessmentBean, newBean)

    }
}