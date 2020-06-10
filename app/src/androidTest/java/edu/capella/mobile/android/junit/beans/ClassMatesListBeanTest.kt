package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.ClassmatesListBean
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test


class ClassMatesListTest {

   var classmatesListBean  =  ClassmatesListBean()

    @Before
    fun setFakeData()
    {
        classmatesListBean  =  ClassmatesListBean()

    }
    @Test
    fun check_for_getter_and_setter()
    {
        classmatesListBean.alertsData = null
        classmatesListBean.areaPeopleData = null
        classmatesListBean.authData = null
        classmatesListBean.classmatesData = null
        classmatesListBean.contactsData = null
        classmatesListBean.courseroomData = null
        classmatesListBean.dashboardData = null
        classmatesListBean.degreeCompletionPlanData = null
        classmatesListBean.discussionData = null
        classmatesListBean.errorData = null
        classmatesListBean.feedname = null
        classmatesListBean.flexpathAssessmentsAndStatusResponse = null
        classmatesListBean.gmailUnreadCountData = null
        classmatesListBean.homeData = null
        classmatesListBean.isAuthenticated = null
        classmatesListBean.logoutSuccessful = null
        classmatesListBean.mobileFeedServiceResponse = null
        classmatesListBean.newCourseroomData = null
        classmatesListBean.newDiscussionData = null
        classmatesListBean.newsData = null
        classmatesListBean.profileData = null
        classmatesListBean.profileLearnerData = null
        classmatesListBean.registerData = null
        classmatesListBean.socialConnectionsData = null
        classmatesListBean.viewportsData = null
        classmatesListBean.vistaDiscussionData = null

        Assert.assertNull (classmatesListBean.alertsData)
        Assert.assertNull (classmatesListBean.areaPeopleData)
        Assert.assertNull (classmatesListBean.authData)
        Assert.assertNull (classmatesListBean.classmatesData)
        Assert.assertNull (classmatesListBean.contactsData)
        Assert.assertNull (classmatesListBean.courseroomData)
        Assert.assertNull (classmatesListBean.dashboardData)
        Assert.assertNull (classmatesListBean.degreeCompletionPlanData)
        Assert.assertNull (classmatesListBean.discussionData)
        Assert.assertNull (classmatesListBean.errorData)
        Assert.assertNull (classmatesListBean.feedname)
        Assert.assertNull (classmatesListBean.flexpathAssessmentsAndStatusResponse)
        Assert.assertNull (classmatesListBean.gmailUnreadCountData)
        Assert.assertNull (classmatesListBean.homeData)
        Assert.assertNull (classmatesListBean.isAuthenticated)
        Assert.assertNull (classmatesListBean.logoutSuccessful)
        Assert.assertNull (classmatesListBean.mobileFeedServiceResponse)
        Assert.assertNull (classmatesListBean.newCourseroomData)
        Assert.assertNull (classmatesListBean.newDiscussionData)
        Assert.assertNull (classmatesListBean.newsData)
        Assert.assertNull (classmatesListBean.profileData)
        Assert.assertNull (classmatesListBean.profileLearnerData)
        Assert.assertNull (classmatesListBean.registerData)
        Assert.assertNull (classmatesListBean.socialConnectionsData)
        Assert.assertNull (classmatesListBean.viewportsData)
        Assert.assertNull (classmatesListBean.vistaDiscussionData)
    }
    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(classmatesListBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(classmatesListBean.hashCode())
    }
    @Test
    fun check_whether_bean_is_gson_supported()
    {
        val auth = ClassmatesListBean.AuthData()

        val employeeId = ClassmatesListBean.AuthData.EmployeeId()
        employeeId.value =   "2400217"

        auth.employeeId = employeeId

        classmatesListBean.authData   = auth

        var gson = Gson( )
        var json = gson.toJson(classmatesListBean)

        var newBean = gson.fromJson(json , ClassmatesListBean::class.java )

        assertEquals(classmatesListBean,newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        val auth = ClassmatesListBean.AuthData()

        val employeeId = ClassmatesListBean.AuthData.EmployeeId()
        employeeId.value =   "2400217"
        auth.employeeId = employeeId
        classmatesListBean.authData   = auth

        var gson = Gson( )
        var json = gson.toJson(classmatesListBean)
        var newBean = gson.fromJson(json , ClassmatesListBean::class.java )

        val nauth = ClassmatesListBean.AuthData()

        val nemployeeId = ClassmatesListBean.AuthData.EmployeeId()
        nemployeeId.value =   "3400217"
        nauth.employeeId = nemployeeId
        newBean.authData   = nauth

        assertNotEquals(classmatesListBean,newBean)
    }




}
