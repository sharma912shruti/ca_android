package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.AssignmentsBean
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
class AssignmentsBeanTest {

    var assignmentsBean  =  AssignmentsBean()

    @Before
    fun setFakeData()
    {
        assignmentsBean  =  AssignmentsBean()

    }
    @Test
    fun check_for_getter_and_setter()
    {
        assignmentsBean.courseAssignment = null
        assignmentsBean.serverName = null
        assignmentsBean.trackingId = null

        Assert.assertNull(assignmentsBean.courseAssignment)
        Assert.assertNull(assignmentsBean.serverName)
        Assert.assertNull(assignmentsBean.trackingId)
    }

    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(assignmentsBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(assignmentsBean.hashCode())
    }
    @Test
    fun check_whether_bean_is_gson_supported()
    {
        assignmentsBean.trackingId ="Dummy tracking id"
        var gson = Gson( )
        var json = gson.toJson(assignmentsBean)

        var newBean = gson.fromJson(json , AssignmentsBean::class.java )

        Assert.assertEquals(assignmentsBean, newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        assignmentsBean.trackingId ="Dummy tracking id"
        var gson = Gson( )
        var json = gson.toJson(assignmentsBean)

        var newBean = gson.fromJson(json , AssignmentsBean::class.java )
        newBean.trackingId = "New tracking id"

        Assert.assertNotEquals(assignmentsBean, newBean)

    }
}