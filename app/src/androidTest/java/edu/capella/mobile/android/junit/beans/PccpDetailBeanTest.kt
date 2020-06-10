package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.CacheCourseRoomBean
import edu.capella.mobile.android.bean.PccpDetailBean
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
class PccpDetailBeanTest {

    var pccpDetailBean  =  PccpDetailBean()

    @Before
    fun setFakeData()
    {
        pccpDetailBean  =  PccpDetailBean()

    }
    @Test
    fun check_for_getter_and_setter()
    {
        pccpDetailBean.assignment  = null
        pccpDetailBean.serverName = null
        pccpDetailBean.type = null
        pccpDetailBean.trackingId = null

        Assert.assertNull(pccpDetailBean.assignment)
        Assert.assertNull(pccpDetailBean.serverName)
        Assert.assertNull(pccpDetailBean.type)
        Assert.assertNull(pccpDetailBean.trackingId)

        var assignment = PccpDetailBean.Assignment()
        assignment.IsActivityCompleted = null
        assignment.assignments = null
        assignment.courseEndDate = null
        assignment.courseId = null
        assignment.courseStartDate = null
        assignment.employeeId = null
        assignment.finalGrade = null
        assignment.isMaximumAttemptsExhausted = null
        assignment.learnerProgram = null
        assignment.longCourseId = null
        assignment.numberOfWeeksCompleted = null
        assignment.pccpId = null
        assignment.targetDateEmpty = null
        assignment.userEmailId = null
        assignment.userFirstName = null
        assignment.userId = null
        assignment.userLastName = null

        Assert.assertNull(assignment.IsActivityCompleted )
        Assert.assertNull(assignment.assignments )
        Assert.assertNull(assignment.courseEndDate )
        Assert.assertNull(assignment.courseId )
        Assert.assertNull(assignment.courseStartDate )
        Assert.assertNull(assignment.employeeId )
        Assert.assertNull(assignment.finalGrade )
        Assert.assertNull(assignment.isMaximumAttemptsExhausted )
        Assert.assertNull(assignment.learnerProgram )
        Assert.assertNull(assignment.longCourseId )
        Assert.assertNull(assignment.numberOfWeeksCompleted )
        Assert.assertNull(assignment.pccpId )
        Assert.assertNull(assignment.targetDateEmpty )
        Assert.assertNull(assignment.userEmailId )
        Assert.assertNull(assignment.userFirstName )
        Assert.assertNull(assignment.userId )
        Assert.assertNull(assignment.userLastName )
    }

    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(pccpDetailBean.toString())
    }

    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(pccpDetailBean.hashCode())
    }

    @Test
    fun check_whether_bean_is_gson_supported()
    {
        pccpDetailBean.type ="Dummy tracking id"
        var gson = Gson( )
        var json = gson.toJson(pccpDetailBean)

        var newBean = gson.fromJson(json , PccpDetailBean::class.java )

        Assert.assertNotNull( newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        pccpDetailBean.type ="Dummy tracking id"
        var gson = Gson( )
        var json = gson.toJson(pccpDetailBean)

        var newBean = gson.fromJson(json , PccpDetailBean::class.java )
        newBean.type = "New tracking id"

        Assert.assertNotEquals(pccpDetailBean, newBean)

    }
}
