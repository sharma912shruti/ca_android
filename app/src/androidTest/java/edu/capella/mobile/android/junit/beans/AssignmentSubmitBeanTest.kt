package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.AssignmentSubmitBean
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
class AssignmentSubmitBeanTest {

    var assignmentSubmitBean  =  AssignmentSubmitBean()

    @Before
    fun setFakeData()
    {
        assignmentSubmitBean  =  AssignmentSubmitBean()

    }

    @Test
    fun check_for_getter_and_setter()
    {

        assignmentSubmitBean.targetNewDate = null
        assignmentSubmitBean.targetDate = null
        assignmentSubmitBean.id = null

        Assert.assertNull(assignmentSubmitBean.targetNewDate)
        Assert.assertNull(assignmentSubmitBean.targetDate)
        Assert.assertNull(assignmentSubmitBean.id)
    }

    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(assignmentSubmitBean.toString())
    }

    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(assignmentSubmitBean.hashCode())
    }

//    @Test
//    fun check_whether_bean_is_gson_supported()
//    {
//        var gson = Gson( )
//        var json = gson.toJson(assignmentSubmitBean)
//
//        var newBean = gson.fromJson(json , AssignmentSubmitBean::class.java )
//
//        Assert.assertEquals(assignmentSubmitBean, newBean)
//
//    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        var gson = Gson( )
        var json = gson.toJson(assignmentSubmitBean)

        var newBean = gson.fromJson(json , AssignmentSubmitBean::class.java )
        newBean.targetNewDate = "New date name"

        Assert.assertNotEquals(assignmentSubmitBean, newBean)

    }
}