package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.GradeFacultyBean
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test


class GradeFacultyBeanTest {

   var gradeFacultyBean  =  GradeFacultyBean()

    @Before
    fun setFakeData()
    {
        gradeFacultyBean  =  GradeFacultyBean()

    }

    @Test
    fun check_for_getter_and_setter()
    {



        gradeFacultyBean.courseAssignment = null
        gradeFacultyBean.serverName= null
        gradeFacultyBean.trackingId= null

        var courseAssignment = GradeFacultyBean.CourseAssignment()
        courseAssignment.attempts = null
        courseAssignment.id = null
        courseAssignment.instructions = null
        courseAssignment.instructorViewedAssignment = null
        courseAssignment.learnerFullName = null
        courseAssignment.link = null
        courseAssignment.score = null
        courseAssignment.status = null
        courseAssignment.statusDateTime = null
        courseAssignment.submittedDateTime = null
        courseAssignment.gradedDateTime = null
        courseAssignment.title = null
        courseAssignment.totalPossibleScore = null

        assertNull (gradeFacultyBean.courseAssignment )
        assertNull (gradeFacultyBean.serverName  )
        assertNull (gradeFacultyBean.trackingId  )


        assertNull(courseAssignment.attempts)
        assertNull(courseAssignment.id)
        assertNull(courseAssignment.instructions)
        assertNull(courseAssignment.instructorViewedAssignment)
        assertNull(courseAssignment.learnerFullName)
        assertNull(courseAssignment.link)
        assertNull(courseAssignment.score)
        assertNull(courseAssignment.status)
        assertNull(courseAssignment.statusDateTime)
        assertNull(courseAssignment.submittedDateTime)
        assertNull(courseAssignment.gradedDateTime)
        assertNull(courseAssignment.title)
        assertNull(courseAssignment.totalPossibleScore)

    }

    @Test
    fun check_for_toString()
    {
        assertNotNull(gradeFacultyBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        assertNotNull(gradeFacultyBean.hashCode())
    }



    @Test
    fun check_whether_bean_is_gson_supported()
    {
        var gson = Gson( )
        var json = gson.toJson(gradeFacultyBean)

        var newBean = gson.fromJson(json , GradeFacultyBean::class.java )

        assertEquals(gradeFacultyBean,newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        var gson = Gson( )
        var json = gson.toJson(gradeFacultyBean)

        var newBean = gson.fromJson(json , GradeFacultyBean::class.java )
        newBean.serverName = "New server name"

        assertNotEquals(gradeFacultyBean,newBean)

    }




}
