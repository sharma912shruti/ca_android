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
class LoginBeanTest {

   var loginBean  =  LoginBean()

    @Before
    fun setFakeData()
    {
        loginBean  =  LoginBean()

    }
    @Test
    fun check_for_getter_and_setter()
    {

        loginBean.areaPeopleData = null
        loginBean.authData = null
        loginBean.classmatesData = null
        loginBean.contactsData = null
        loginBean.courseroomData = null
        loginBean.dashboardData = null
        loginBean.degreeCompletionPlanData = null
        loginBean.discussionData = null
        loginBean.errorData = null
        loginBean.feedname = null
        loginBean.flexpathAssessmentsAndStatusResponse = null
        loginBean.gmailUnreadCountData = null
        loginBean.homeData = null
        loginBean.isAuthenticated = null
        loginBean.logoutSuccessful = null
        loginBean.mobileFeedServiceResponse = null
        loginBean.newCourseroomData = null
        loginBean.newDiscussionData = null
        loginBean.newsData = null
        loginBean.profileData = null
        loginBean.profileLearnerData = null
        loginBean.registerData = null
        loginBean.socialConnectionsData = null
        loginBean.viewportsData = null
        loginBean.vistaDiscussionData = null

        Assert.assertNull(loginBean.alertsData)
        Assert.assertNull(loginBean.areaPeopleData)
        Assert.assertNull(loginBean.authData)
        Assert.assertNull(loginBean.classmatesData)
        Assert.assertNull(loginBean.contactsData)
        Assert.assertNull(loginBean.courseroomData)
        Assert.assertNull(loginBean.dashboardData)
        Assert.assertNull(loginBean.degreeCompletionPlanData)
        Assert.assertNull(loginBean.discussionData)
        Assert.assertNull(loginBean.errorData)
        Assert.assertNull(loginBean.feedname)
        Assert.assertNull(loginBean.flexpathAssessmentsAndStatusResponse)
        Assert.assertNull(loginBean.gmailUnreadCountData)
        Assert.assertNull(loginBean.homeData)
        Assert.assertNull(loginBean.isAuthenticated)
        Assert.assertNull(loginBean.logoutSuccessful)
        Assert.assertNull(loginBean.mobileFeedServiceResponse)
        Assert.assertNull(loginBean.newCourseroomData)
        Assert.assertNull(loginBean.newDiscussionData)
        Assert.assertNull(loginBean.newsData)
        Assert.assertNull(loginBean.profileData)
        Assert.assertNull(loginBean.profileLearnerData)
        Assert.assertNull(loginBean.registerData)
        Assert.assertNull(loginBean.socialConnectionsData)
        Assert.assertNull(loginBean.viewportsData)
        Assert.assertNull(loginBean.vistaDiscussionData)
    }

    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(loginBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(loginBean.hashCode())
    }

    @Test
    fun check_whether_bean_is_gson_supported()
    {
        val auth = LoginBean.AuthData()
        auth.token ="FAKE TOKEN"
        loginBean.authData = auth
        var gson = Gson( )
        var json = gson.toJson(loginBean)

        var newBean = gson.fromJson(json , LoginBean::class.java )

        assertEquals(loginBean,newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        val auth = LoginBean.AuthData()
        auth.token ="FAKE TOKEN"
        loginBean.authData = auth
        var gson = Gson( )
        var json = gson.toJson(loginBean)
        var newBean = gson.fromJson(json , LoginBean::class.java )
        newBean.authData?.token   = "New Fake Token"
        assertNotEquals(loginBean,newBean)
    }




}
