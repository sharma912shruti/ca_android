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
class DiscussionPostBeanTest {

   var discussionPostBean  =  DiscussionPostBean()

    @Before
    fun setFakeData()
    {
        discussionPostBean  =  DiscussionPostBean()

    }
    @Test
    fun check_for_getter_and_setter()
    {
        discussionPostBean.alertsData = null
        discussionPostBean.areaPeopleData = null
        discussionPostBean.authData = null
        discussionPostBean.classmatesData = null
        discussionPostBean.contactsData = null
        discussionPostBean.courseroomData = null
        discussionPostBean.dashboardData = null
        discussionPostBean.degreeCompletionPlanData = null
        discussionPostBean.discussionData = null
        discussionPostBean.errorData = null
        discussionPostBean.feedname = null
        discussionPostBean.flexpathAssessmentsAndStatusResponse = null
        discussionPostBean.gmailUnreadCountData = null
        discussionPostBean.homeData = null
        discussionPostBean.isAuthenticated = null
        discussionPostBean.logoutSuccessful = null
        discussionPostBean.mobileFeedServiceResponse = null
        discussionPostBean.newCourseroomData = null
        discussionPostBean.newDiscussionData = null
        discussionPostBean.newsData = null
        discussionPostBean.profileData = null
        discussionPostBean.profileLearnerData = null
        discussionPostBean.registerData = null
        discussionPostBean.socialConnectionsData = null
        discussionPostBean.viewportsData = null
        discussionPostBean.vistaDiscussionData = null

        Assert.assertNull(discussionPostBean.alertsData)
        Assert.assertNull(discussionPostBean.areaPeopleData)
        Assert.assertNull(discussionPostBean.authData)
        Assert.assertNull(discussionPostBean.classmatesData)
        Assert.assertNull(discussionPostBean.contactsData)
        Assert.assertNull(discussionPostBean.courseroomData)
        Assert.assertNull(discussionPostBean.dashboardData)
        Assert.assertNull(discussionPostBean.degreeCompletionPlanData)
        Assert.assertNull(discussionPostBean.discussionData)
        Assert.assertNull(discussionPostBean.errorData)
        Assert.assertNull(discussionPostBean.feedname)
        Assert.assertNull(discussionPostBean.flexpathAssessmentsAndStatusResponse)
        Assert.assertNull(discussionPostBean.gmailUnreadCountData)
        Assert.assertNull(discussionPostBean.homeData)
        Assert.assertNull(discussionPostBean.isAuthenticated)
        Assert.assertNull(discussionPostBean.logoutSuccessful)
        Assert.assertNull(discussionPostBean.mobileFeedServiceResponse)
        Assert.assertNull(discussionPostBean.newCourseroomData)
        Assert.assertNull(discussionPostBean.newDiscussionData)
        Assert.assertNull(discussionPostBean.newsData)
        Assert.assertNull(discussionPostBean.profileData)
        Assert.assertNull(discussionPostBean.profileLearnerData)
        Assert.assertNull(discussionPostBean.registerData)
        Assert.assertNull(discussionPostBean.socialConnectionsData)
        Assert.assertNull(discussionPostBean.viewportsData)
        Assert.assertNull(discussionPostBean.vistaDiscussionData)
    }
    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(discussionPostBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(discussionPostBean.hashCode())
    }
    @Test
    fun check_whether_bean_is_gson_supported()
    {

        var gson = Gson( )
        var json = gson.toJson(discussionPostBean)

        var newBean = gson.fromJson(json , DiscussionPostBean::class.java )

        assertEquals(discussionPostBean,newBean)

    }






}
