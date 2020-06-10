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
class VisitedGradeHistoryBeanTest {

   var visitedGradeHistoryBeanTest  =  VisitedGradesStatusHistory()

    @Before
    fun setFakeData()
    {
        visitedGradeHistoryBeanTest  =  VisitedGradesStatusHistory()

    }
    @Test
    fun check_for_getter_and_setter()
    {
        visitedGradeHistoryBeanTest.course_identifiers = null
        visitedGradeHistoryBeanTest.employeeId = null
        visitedGradeHistoryBeanTest.visited_assignment_items= null


        Assert.assertNull(visitedGradeHistoryBeanTest.course_identifiers)
        Assert.assertNull(visitedGradeHistoryBeanTest.employeeId)
        Assert.assertNull(visitedGradeHistoryBeanTest.visited_assignment_items)

    }
    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(visitedGradeHistoryBeanTest.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(visitedGradeHistoryBeanTest.hashCode())
    }
    @Test
    fun check_whether_bean_is_gson_supported()
    {

        var gson = Gson( )
        var json = gson.toJson(visitedGradeHistoryBeanTest)

        var newBean = gson.fromJson(json , VisitedGradesStatusHistory::class.java )

        assertEquals(visitedGradeHistoryBeanTest,newBean)

    }






}
