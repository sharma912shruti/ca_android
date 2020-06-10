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
class DiscussionTopicBeanTest {

   var disucssionTopicBean  =  DiscussionTopicBean()

    @Before
    fun setFakeData()
    {
        disucssionTopicBean  =  DiscussionTopicBean()

    }
    @Test
    fun check_for_getter_and_setter()
    {
        disucssionTopicBean.alertsData = null
        disucssionTopicBean.areaPeopleData = null
        disucssionTopicBean.authData = null
        disucssionTopicBean.classmatesData = null
        disucssionTopicBean.contactsData = null
        disucssionTopicBean.courseroomData = null
        disucssionTopicBean.dashboardData = null
        disucssionTopicBean.degreeCompletionPlanData = null
        disucssionTopicBean.discussionData = null
        disucssionTopicBean.errorData = null
        disucssionTopicBean.feedname = null
        disucssionTopicBean.flexpathAssessmentsAndStatusResponse = null
        disucssionTopicBean.gmailUnreadCountData = null
        disucssionTopicBean.homeData = null
        disucssionTopicBean.isAuthenticated = null
        disucssionTopicBean.logoutSuccessful = null
        disucssionTopicBean.mobileFeedServiceResponse = null
        disucssionTopicBean.newCourseroomData = null
        disucssionTopicBean.newDiscussionData = null
        disucssionTopicBean.newsData = null
        disucssionTopicBean.profileData = null
        disucssionTopicBean.profileLearnerData = null
        disucssionTopicBean.registerData = null
        disucssionTopicBean.socialConnectionsData = null
        disucssionTopicBean.viewportsData = null
        disucssionTopicBean.vistaDiscussionData = null

        Assert.assertNull(disucssionTopicBean.alertsData)
        Assert.assertNull(disucssionTopicBean.areaPeopleData)
        Assert.assertNull(disucssionTopicBean.authData)
        Assert.assertNull(disucssionTopicBean.classmatesData)
        Assert.assertNull(disucssionTopicBean.contactsData)
        Assert.assertNull(disucssionTopicBean.courseroomData)
        Assert.assertNull(disucssionTopicBean.dashboardData)
        Assert.assertNull(disucssionTopicBean.degreeCompletionPlanData)
        Assert.assertNull(disucssionTopicBean.discussionData)
        Assert.assertNull(disucssionTopicBean.errorData)
        Assert.assertNull(disucssionTopicBean.feedname)
        Assert.assertNull(disucssionTopicBean.flexpathAssessmentsAndStatusResponse)
        Assert.assertNull(disucssionTopicBean.gmailUnreadCountData)
        Assert.assertNull(disucssionTopicBean.homeData)
        Assert.assertNull(disucssionTopicBean.isAuthenticated)
        Assert.assertNull(disucssionTopicBean.logoutSuccessful)
        Assert.assertNull(disucssionTopicBean.mobileFeedServiceResponse)
        Assert.assertNull(disucssionTopicBean.newCourseroomData)
        Assert.assertNull(disucssionTopicBean.newDiscussionData)
        Assert.assertNull(disucssionTopicBean.newsData)
        Assert.assertNull(disucssionTopicBean.profileData)
        Assert.assertNull(disucssionTopicBean.profileLearnerData)
        Assert.assertNull(disucssionTopicBean.registerData)
        Assert.assertNull(disucssionTopicBean.socialConnectionsData)
        Assert.assertNull(disucssionTopicBean.viewportsData)
        Assert.assertNull(disucssionTopicBean.vistaDiscussionData)
    }
    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(disucssionTopicBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(disucssionTopicBean.hashCode())
    }
    @Test
    fun check_whether_bean_is_gson_supported()
    {

        var gson = Gson( )
        var json = gson.toJson(disucssionTopicBean)

        var newBean = gson.fromJson(json , DiscussionTopicBean::class.java )

        assertEquals(disucssionTopicBean,newBean)

    }






}
