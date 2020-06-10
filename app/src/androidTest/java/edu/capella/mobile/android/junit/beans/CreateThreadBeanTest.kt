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
class CreateThreadBeanTest {

   var createThreadBean  =  CreateThreadBean()

    @Before
    fun setFakeData()
    {
        createThreadBean  =  CreateThreadBean()

    }
    @Test
    fun check_for_getter_and_setter()
    {
        createThreadBean.alertsData = null
        createThreadBean.areaPeopleData = null
        createThreadBean.authData = null
        createThreadBean.classmatesData = null
        createThreadBean.contactsData = null
        createThreadBean.courseroomData = null
        createThreadBean.dashboardData = null
        createThreadBean.degreeCompletionPlanData = null
        createThreadBean.discussionData = null
        createThreadBean.errorData = null
        createThreadBean.feedname = null
        createThreadBean.flexpathAssessmentsAndStatusResponse = null
        createThreadBean.gmailUnreadCountData = null
        createThreadBean.homeData = null
        createThreadBean.isAuthenticated = null
        createThreadBean.logoutSuccessful = null
        createThreadBean.mobileFeedServiceResponse = null
        createThreadBean.newCourseroomData = null
        createThreadBean.newDiscussionData = null
        createThreadBean.newsData = null
        createThreadBean.profileData = null
        createThreadBean.profileLearnerData = null
        createThreadBean.registerData = null
        createThreadBean.socialConnectionsData = null
        createThreadBean.viewportsData = null
        createThreadBean.vistaDiscussionData = null

        Assert.assertNull(createThreadBean.alertsData)
        Assert.assertNull(createThreadBean.areaPeopleData)
        Assert.assertNull(createThreadBean.authData)
        Assert.assertNull(createThreadBean.classmatesData)
        Assert.assertNull(createThreadBean.contactsData)
        Assert.assertNull(createThreadBean.courseroomData)
        Assert.assertNull(createThreadBean.dashboardData)
        Assert.assertNull(createThreadBean.degreeCompletionPlanData)
        Assert.assertNull(createThreadBean.discussionData)
        Assert.assertNull(createThreadBean.errorData)
        Assert.assertNull(createThreadBean.feedname)
        Assert.assertNull(createThreadBean.flexpathAssessmentsAndStatusResponse)
        Assert.assertNull(createThreadBean.gmailUnreadCountData)
        Assert.assertNull(createThreadBean.homeData)
        Assert.assertNull(createThreadBean.isAuthenticated)
        Assert.assertNull(createThreadBean.logoutSuccessful)
        Assert.assertNull(createThreadBean.mobileFeedServiceResponse)
        Assert.assertNull(createThreadBean.newCourseroomData)
        Assert.assertNull(createThreadBean.newDiscussionData)
        Assert.assertNull(createThreadBean.newsData)
        Assert.assertNull(createThreadBean.profileData)
        Assert.assertNull(createThreadBean.profileLearnerData)
        Assert.assertNull(createThreadBean.registerData)
        Assert.assertNull(createThreadBean.socialConnectionsData)
        Assert.assertNull(createThreadBean.viewportsData)
        Assert.assertNull(createThreadBean.vistaDiscussionData)

        var discussionData = CreateThreadBean.DiscussionData()
        discussionData.collectedPostData = null
        discussionData.collectedPosts = null
        discussionData.collectedPostsByCategory = null
        discussionData.courses = null
        discussionData.draftData = null
        discussionData.drafts = null
        discussionData.draftsByCategory = null
        discussionData.forums = null
        discussionData.myPostData = null
        discussionData.myPosts = null
        discussionData.myPostsByCategory = null
        discussionData.replies = null
        discussionData.repliesToMe = null
        discussionData.repliesToMeByCategory = null
        discussionData.repliesToMeData = null
        discussionData.requestSuccessful = null
        discussionData.topicWithReplies = null
        discussionData.topics = null
        discussionData.unreadMessageData = null

        Assert.assertNull(discussionData.collectedPostData)
        Assert.assertNull(discussionData.collectedPosts)
        Assert.assertNull(discussionData.collectedPostsByCategory)
        Assert.assertNull(discussionData.courses)
        Assert.assertNull(discussionData.draftData)
        Assert.assertNull(discussionData.drafts)
        Assert.assertNull(discussionData.draftsByCategory)
        Assert.assertNull(discussionData.forums)
        Assert.assertNull(discussionData.myPostData)
        Assert.assertNull(discussionData.myPosts)
        Assert.assertNull(discussionData.myPostsByCategory)
        Assert.assertNull(discussionData.replies)
        Assert.assertNull(discussionData.repliesToMe)
        Assert.assertNull(discussionData.repliesToMeByCategory)
        Assert.assertNull(discussionData.repliesToMeData)
        Assert.assertNull(discussionData.requestSuccessful)
        Assert.assertNull(discussionData.topicWithReplies)
        Assert.assertNull(discussionData.topics)
        Assert.assertNull(discussionData.unreadMessageData)

        var authData = CreateThreadBean.AuthData()

        authData.emanxh = null
        authData.employeeId = null
        authData.hxdowssap = null
        authData.token = null
        authData.username = null

        Assert.assertNull(authData.emanxh)
        Assert.assertNull(authData.employeeId)
        Assert.assertNull(authData.hxdowssap)
        Assert.assertNull(authData.token)
        Assert.assertNull(authData.username)



    }
    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(createThreadBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(createThreadBean.hashCode())
    }
    @Test
    fun check_whether_bean_is_gson_supported()
    {

        var gson = Gson( )
        var json = gson.toJson(createThreadBean)

        var newBean = gson.fromJson(json , CreateThreadBean::class.java )

        assertEquals(createThreadBean,newBean)

    }






}
