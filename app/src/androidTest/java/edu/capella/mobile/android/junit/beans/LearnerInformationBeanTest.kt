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
class LearnerInformationTest {

   var learnerInformation  =  LearnerInformation()

    @Before
    fun setFakeData()
    {
        learnerInformation  =  LearnerInformation()



    }

    @Test
    fun check_for_getter_and_setter()
    {
        learnerInformation.alertsData = null
        learnerInformation.areaPeopleData = null
        learnerInformation.authData = null
        learnerInformation.classmatesData = null
        learnerInformation.contactsData = null
        learnerInformation.courseroomData = null
        learnerInformation.dashboardData = null
        learnerInformation.degreeCompletionPlanData = null
        learnerInformation.discussionData = null
        learnerInformation.errorData = null
        learnerInformation.feedname = null
        learnerInformation.flexpathAssessmentsAndStatusResponse = null
        learnerInformation.gmailUnreadCountData = null
        learnerInformation.homeData = null
        learnerInformation.isAuthenticated = null
        learnerInformation.logoutSuccessful = null
        learnerInformation.mobileFeedServiceResponse = null
        learnerInformation.newCourseroomData = null
        learnerInformation.newDiscussionData = null
        learnerInformation.newsData = null
        learnerInformation.profileData = null
        learnerInformation.profileLearnerData = null
        learnerInformation.registerData = null
        learnerInformation.socialConnectionsData = null
        learnerInformation.viewportsData = null
        learnerInformation.vistaDiscussionData = null

        Assert.assertNull(learnerInformation.alertsData)
        Assert.assertNull(learnerInformation.areaPeopleData)
        Assert.assertNull(learnerInformation.authData)
        Assert.assertNull(learnerInformation.classmatesData)
        Assert.assertNull(learnerInformation.contactsData)
        Assert.assertNull(learnerInformation.courseroomData)
        Assert.assertNull(learnerInformation.dashboardData)
        Assert.assertNull(learnerInformation.degreeCompletionPlanData)
        Assert.assertNull(learnerInformation.discussionData)
        Assert.assertNull(learnerInformation.errorData)
        Assert.assertNull(learnerInformation.feedname)
        Assert.assertNull(learnerInformation.flexpathAssessmentsAndStatusResponse)
        Assert.assertNull(learnerInformation.gmailUnreadCountData)
        Assert.assertNull(learnerInformation.homeData)
        Assert.assertNull(learnerInformation.isAuthenticated)
        Assert.assertNull(learnerInformation.logoutSuccessful)
        Assert.assertNull(learnerInformation.mobileFeedServiceResponse)
        Assert.assertNull(learnerInformation.newCourseroomData)
        Assert.assertNull(learnerInformation.newDiscussionData)
        Assert.assertNull(learnerInformation.newsData)
        Assert.assertNull(learnerInformation.profileData)
        Assert.assertNull(learnerInformation.profileLearnerData)
        Assert.assertNull(learnerInformation.registerData)
        Assert.assertNull(learnerInformation.socialConnectionsData)
        Assert.assertNull(learnerInformation.viewportsData)
        Assert.assertNull(learnerInformation.vistaDiscussionData)
    }
    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(learnerInformation.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(learnerInformation.hashCode())
    }

    @Test
    fun check_whether_bean_is_gson_supported()
    {
        val auth = LearnerInformation.AuthData()
        auth.token ="FAKE TOKEN"
        learnerInformation.authData = auth
        var gson = Gson( )
        var json = gson.toJson(learnerInformation)

        var newBean = gson.fromJson(json , LearnerInformation::class.java )

        assertEquals(learnerInformation,newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        val auth = LearnerInformation.AuthData()
        auth.token ="FAKE TOKEN"
        learnerInformation.authData = auth

        var gson = Gson( )
        var json = gson.toJson(learnerInformation)
        var newBean = gson.fromJson(json , LearnerInformation::class.java )
        newBean.authData?.token   = "New Fake Token"
        assertNotEquals(learnerInformation,newBean)
    }




}
