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
class DiscussionForumBeanTest {

   var discussionForumBean  =  DiscussionForumBean()

    @Before
    fun setFakeData()
    {
        discussionForumBean  =  DiscussionForumBean()

    }
    @Test
    fun check_for_getter_and_setter()
    {
        discussionForumBean.alertsData = null
        discussionForumBean.areaPeopleData = null
        discussionForumBean.authData = null
        discussionForumBean.classmatesData = null
        discussionForumBean.contactsData = null
        discussionForumBean.courseroomData = null
        discussionForumBean.dashboardData = null
        discussionForumBean.degreeCompletionPlanData = null
        discussionForumBean.discussionData = null
        discussionForumBean.errorData = null
        discussionForumBean.feedname = null
        discussionForumBean.flexpathAssessmentsAndStatusResponse = null
        discussionForumBean.gmailUnreadCountData = null
        discussionForumBean.homeData = null
        discussionForumBean.isAuthenticated = null
        discussionForumBean.logoutSuccessful = null
        discussionForumBean.mobileFeedServiceResponse = null
        discussionForumBean.newCourseroomData = null
        discussionForumBean.newDiscussionData= null
        discussionForumBean.newsData = null
        discussionForumBean.profileData = null
        discussionForumBean.profileLearnerData = null
        discussionForumBean.registerData = null
        discussionForumBean.socialConnectionsData = null
        discussionForumBean.viewportsData = null
        discussionForumBean.vistaDiscussionData = null

        Assert.assertNull(discussionForumBean.alertsData)
        Assert.assertNull(discussionForumBean.areaPeopleData)
        Assert.assertNull(discussionForumBean.authData)
        Assert.assertNull(discussionForumBean.classmatesData)
        Assert.assertNull(discussionForumBean.contactsData)
        Assert.assertNull(discussionForumBean.courseroomData)
        Assert.assertNull(discussionForumBean.dashboardData)
        Assert.assertNull(discussionForumBean.degreeCompletionPlanData)
        Assert.assertNull(discussionForumBean.discussionData)
        Assert.assertNull(discussionForumBean.errorData)
        Assert.assertNull(discussionForumBean.feedname)
        Assert.assertNull(discussionForumBean.flexpathAssessmentsAndStatusResponse)
        Assert.assertNull(discussionForumBean.gmailUnreadCountData)
        Assert.assertNull(discussionForumBean.homeData)
        Assert.assertNull(discussionForumBean.isAuthenticated)
        Assert.assertNull(discussionForumBean.logoutSuccessful)
        Assert.assertNull(discussionForumBean.mobileFeedServiceResponse)
        Assert.assertNull(discussionForumBean.newCourseroomData)
        Assert.assertNull(discussionForumBean.newDiscussionData)
        Assert.assertNull(discussionForumBean.newsData)
        Assert.assertNull(discussionForumBean.profileData)
        Assert.assertNull(discussionForumBean.profileLearnerData)
        Assert.assertNull(discussionForumBean.registerData)
        Assert.assertNull(discussionForumBean.socialConnectionsData)
        Assert.assertNull(discussionForumBean.viewportsData)
        Assert.assertNull(discussionForumBean.vistaDiscussionData)
    }
    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(discussionForumBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(discussionForumBean.hashCode())
    }
    @Test
    fun check_whether_bean_is_gson_supported()
    {

        var gson = Gson( )
        var json = gson.toJson(discussionForumBean)

        var newBean = gson.fromJson(json , DiscussionForumBean::class.java )

        assertEquals(discussionForumBean,newBean)

    }






}
