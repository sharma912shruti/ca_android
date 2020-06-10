package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson

import edu.capella.mobile.android.bean.CampusNewsBean
import org.junit.Assert

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test


class CampuseNewsBeanTest {

   var campusNewsBean  =  CampusNewsBean()

    @Before
    fun setFakeData()
    {
        campusNewsBean  =  CampusNewsBean()
    }

    @Test
    fun check_for_getter_and_setter()
    {
        campusNewsBean.alertsData = null
        campusNewsBean.areaPeopleData = null
        campusNewsBean.authData = null
        campusNewsBean.classmatesData = null
        campusNewsBean.contactsData = null
        campusNewsBean.courseroomData = null
        campusNewsBean.dashboardData = null
        campusNewsBean.degreeCompletionPlanData = null
        campusNewsBean.discussionData = null
        campusNewsBean.errorData = null
        campusNewsBean.feedname = null
        campusNewsBean.flexpathAssessmentsAndStatusResponse = null
        campusNewsBean.gmailUnreadCountData = null
        campusNewsBean.homeData = null
        campusNewsBean.isAuthenticated = null
        campusNewsBean.logoutSuccessful = null
        campusNewsBean.mobileFeedServiceResponse = null
        campusNewsBean.newCourseroomData = null
        campusNewsBean.newDiscussionData = null
        campusNewsBean.newsData = null
        campusNewsBean.profileData = null
        campusNewsBean.profileLearnerData = null
        campusNewsBean.registerData = null
        campusNewsBean.socialConnectionsData = null
        campusNewsBean.viewportsData = null
        campusNewsBean.vistaDiscussionData = null

        Assert.assertNull ( campusNewsBean.alertsData)
        Assert.assertNull ( campusNewsBean.areaPeopleData)
        Assert.assertNull ( campusNewsBean.authData)
        Assert.assertNull ( campusNewsBean.classmatesData)
        Assert.assertNull ( campusNewsBean.contactsData)
        Assert.assertNull ( campusNewsBean.courseroomData)
        Assert.assertNull ( campusNewsBean.dashboardData)
        Assert.assertNull ( campusNewsBean.degreeCompletionPlanData)
        Assert.assertNull ( campusNewsBean.discussionData)
        Assert.assertNull ( campusNewsBean.errorData)
        Assert.assertNull ( campusNewsBean.feedname)
        Assert.assertNull ( campusNewsBean.flexpathAssessmentsAndStatusResponse)
        Assert.assertNull ( campusNewsBean.gmailUnreadCountData)
        Assert.assertNull ( campusNewsBean.homeData)
        Assert.assertNull ( campusNewsBean.isAuthenticated)
        Assert.assertNull ( campusNewsBean.logoutSuccessful)
        Assert.assertNull ( campusNewsBean.mobileFeedServiceResponse)
        Assert.assertNull ( campusNewsBean.newCourseroomData)
        Assert.assertNull ( campusNewsBean.newDiscussionData)
        Assert.assertNull ( campusNewsBean.newsData)
        Assert.assertNull ( campusNewsBean.profileData)
        Assert.assertNull ( campusNewsBean.profileLearnerData)
        Assert.assertNull ( campusNewsBean.registerData)
        Assert.assertNull ( campusNewsBean.socialConnectionsData)
        Assert.assertNull ( campusNewsBean.viewportsData)
        Assert.assertNull ( campusNewsBean.vistaDiscussionData)
    }

    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(campusNewsBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(campusNewsBean.hashCode())
    }

    @Test
    fun check_whether_bean_is_gson_supported()
    {
        val auth = CampusNewsBean.AuthData()
        val employeeId = CampusNewsBean.EmployeeId()
        employeeId.value =   "2400217"
        auth.employeeId = employeeId
        campusNewsBean.authData = auth

        var gson = Gson( )
        var json = gson.toJson(campusNewsBean)

        var newBean = gson.fromJson(json , CampusNewsBean::class.java )

        assertEquals(campusNewsBean,newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        val auth = CampusNewsBean.AuthData()
        val employeeId = CampusNewsBean.EmployeeId()
        employeeId.value =   "2400217"
        auth.employeeId = employeeId
        campusNewsBean.authData = auth
        var gson = Gson( )
        var json = gson.toJson(campusNewsBean)
        var newBean = gson.fromJson(json , CampusNewsBean::class.java )

        val nauth = CampusNewsBean.AuthData()
        val nemployeeId = CampusNewsBean.EmployeeId()
        nemployeeId.value =   "3400217"
        nauth.employeeId = nemployeeId
        newBean.authData = nauth


        assertNotEquals(campusNewsBean,newBean)
    }




}
