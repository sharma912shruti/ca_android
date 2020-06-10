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
class CourseListTest {

   var courseListBean  =  CourseListBean()

    @Before
    fun setFakeData()
    {
        courseListBean  =  CourseListBean()

    }

    @Test
    fun check_for_getter_and_setter()
    {
        courseListBean.alertsData = null
        courseListBean.areaPeopleData = null
        courseListBean.authData = null
        courseListBean.classmatesData = null
        courseListBean.contactsData = null
        courseListBean.courseroomData = null
        courseListBean.dashboardData = null
        courseListBean.degreeCompletionPlanData = null
        courseListBean.discussionData = null
        courseListBean.errorData = null
        courseListBean.feedname = null
        courseListBean.flexpathAssessmentsAndStatusResponse = null
        courseListBean.gmailUnreadCountData = null
        courseListBean.homeData = null
        courseListBean.isAuthenticated = null
        courseListBean.logoutSuccessful = null
        courseListBean.mobileFeedServiceResponse = null
        courseListBean.newCourseroomData = null
        courseListBean.newDiscussionData = null
        courseListBean.newsData = null
        courseListBean.profileData = null
        courseListBean.profileLearnerData = null
        courseListBean.registerData = null
        courseListBean.socialConnectionsData = null
        courseListBean.viewportsData = null
        courseListBean.vistaDiscussionData = null

        Assert.assertNull(courseListBean.alertsData)
        Assert.assertNull(courseListBean.areaPeopleData)
        Assert.assertNull(courseListBean.authData)
        Assert.assertNull(courseListBean.classmatesData)
        Assert.assertNull(courseListBean.contactsData)
        Assert.assertNull(courseListBean.courseroomData)
        Assert.assertNull(courseListBean.dashboardData)
        Assert.assertNull(courseListBean.degreeCompletionPlanData)
        Assert.assertNull(courseListBean.discussionData)
        Assert.assertNull(courseListBean.errorData)
        Assert.assertNull(courseListBean.feedname)
        Assert.assertNull(courseListBean.flexpathAssessmentsAndStatusResponse)
        Assert.assertNull(courseListBean.gmailUnreadCountData)
        Assert.assertNull(courseListBean.homeData)
        Assert.assertNull(courseListBean.isAuthenticated)
        Assert.assertNull(courseListBean.logoutSuccessful)
        Assert.assertNull(courseListBean.mobileFeedServiceResponse)
        Assert.assertNull(courseListBean.newCourseroomData)
        Assert.assertNull(courseListBean.newDiscussionData)
        Assert.assertNull(courseListBean.newsData)
        Assert.assertNull(courseListBean.profileData)
        Assert.assertNull(courseListBean.profileLearnerData)
        Assert.assertNull(courseListBean.registerData)
        Assert.assertNull(courseListBean.socialConnectionsData)
        Assert.assertNull(courseListBean.viewportsData)
        Assert.assertNull(courseListBean.vistaDiscussionData)
    }
    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(courseListBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(courseListBean.hashCode())
    }
    @Test
    fun check_whether_bean_is_gson_supported()
    {
        val auth = CourseListBean.AuthData()

        val employeeId = CourseListBean.AuthData.EmployeeId()
        employeeId.value =   "2400217"

        auth.employeeId = employeeId

        courseListBean.authData   = auth
        var gson = Gson( )
        var json = gson.toJson(courseListBean)

        var newBean = gson.fromJson(json , CourseListBean::class.java )

        assertEquals(courseListBean,newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        val auth = CourseListBean.AuthData()

        val employeeId = CourseListBean.AuthData.EmployeeId()
        employeeId.value =   "2400217"

        auth.employeeId = employeeId

        courseListBean.authData   = auth

        var gson = Gson( )
        var json = gson.toJson(courseListBean)
        var newBean = gson.fromJson(json , CourseListBean::class.java )

        val nauth = CourseListBean.AuthData()
        val nemployeeId = CourseListBean.AuthData.EmployeeId()
        nemployeeId.value =   "3400217"
        nauth.employeeId = nemployeeId

        newBean.authData   = nauth

        assertNotEquals(courseListBean,newBean)
    }




}
