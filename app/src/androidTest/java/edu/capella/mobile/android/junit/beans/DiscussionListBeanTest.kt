package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson

import edu.capella.mobile.android.bean.DiscussionDraftBean
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test


class DiscussionListBeanTest {

   var discussionDraftBean  =  DiscussionDraftBean()

    @Before
    fun setFakeData()
    {
        discussionDraftBean  =  DiscussionDraftBean()

    }

    @Test
    fun check_for_getter_and_setter()
    {


//        val alertsData: Any,
//        val areaPeopleData: Any,
//        val authData: AuthData,
//        val classmatesData: Any,
//        val contactsData: Any,
//        val courseroomData: Any,
//        val dashboardData: Any,
//        val degreeCompletionPlanData: Any,
//        val discussionData: Any,
//        val errorData: Any,
//        val feedname: String,
//        val flexpathAssessmentsAndStatusResponse: Any,
//        val gmailUnreadCountData: Any,
//        val homeData: Any,
//        val isAuthenticated: Any,
//        val logoutSuccessful: Any,
//        val mobileFeedServiceResponse: Any,
//        val newCourseroomData: Any,
//        val newDiscussionData: NewDiscussionData,
//        val newsData: Any,
//        val profileData: Any,
//        val profileLearnerData: Any,
//        val registerData: Any,
//        val socialConnectionsData: Any,
//        val viewportsData: Any,
//        val vistaDiscussionData: Any

        discussionDraftBean.alertsData = null
        discussionDraftBean.areaPeopleData = null
        discussionDraftBean.authData   = null
        discussionDraftBean.classmatesData = null
        discussionDraftBean.contactsData = null
        discussionDraftBean.courseroomData = null
        discussionDraftBean.dashboardData = null
        discussionDraftBean.degreeCompletionPlanData = null
        discussionDraftBean.discussionData = null
        discussionDraftBean.errorData = null
        discussionDraftBean.feedname = null
        discussionDraftBean.flexpathAssessmentsAndStatusResponse = null
        discussionDraftBean.gmailUnreadCountData = null
        discussionDraftBean.homeData = null
        discussionDraftBean.isAuthenticated = null
        discussionDraftBean.logoutSuccessful = null
        discussionDraftBean.mobileFeedServiceResponse = null
        discussionDraftBean.newCourseroomData = null
        discussionDraftBean.newDiscussionData = null
        discussionDraftBean.newsData = null
        discussionDraftBean.profileData = null
        discussionDraftBean.profileLearnerData = null
        discussionDraftBean.registerData = null
        discussionDraftBean.socialConnectionsData = null
        discussionDraftBean.viewportsData = null
        discussionDraftBean.vistaDiscussionData = null

        assertNull (discussionDraftBean.alertsData)
        assertNull (discussionDraftBean.areaPeopleData)
        assertNull (discussionDraftBean.authData  )
        assertNull (discussionDraftBean.classmatesData)
        assertNull (discussionDraftBean.contactsData)
        assertNull (discussionDraftBean.courseroomData)
        assertNull (discussionDraftBean.dashboardData)
        assertNull (discussionDraftBean.degreeCompletionPlanData)
        assertNull (discussionDraftBean.discussionData)
        assertNull (discussionDraftBean.errorData)
        assertNull (discussionDraftBean.feedname)
        assertNull (discussionDraftBean.flexpathAssessmentsAndStatusResponse)
        assertNull (discussionDraftBean.gmailUnreadCountData)
        assertNull (discussionDraftBean.homeData)
        assertNull (discussionDraftBean.isAuthenticated)
        assertNull (discussionDraftBean.logoutSuccessful)
        assertNull (discussionDraftBean.mobileFeedServiceResponse)
        assertNull (discussionDraftBean.newCourseroomData)
        assertNull (discussionDraftBean.newDiscussionData)
        assertNull (discussionDraftBean.newsData)
        assertNull (discussionDraftBean.profileData)
        assertNull (discussionDraftBean.profileLearnerData)
        assertNull (discussionDraftBean.registerData)
        assertNull (discussionDraftBean.socialConnectionsData)
        assertNull (discussionDraftBean.viewportsData)
        assertNull (discussionDraftBean.vistaDiscussionData)
    }

    @Test
    fun check_for_toString()
    {
        assertNotNull(discussionDraftBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        assertNotNull(discussionDraftBean.hashCode())
    }



    @Test
    fun check_whether_bean_is_gson_supported()
    {
        var gson = Gson( )
        var json = gson.toJson(discussionDraftBean)

        var newBean = gson.fromJson(json , DiscussionDraftBean::class.java )

        assertEquals(discussionDraftBean,newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        var gson = Gson( )
        var json = gson.toJson(discussionDraftBean)

        var newBean = gson.fromJson(json , DiscussionDraftBean::class.java )
        newBean.feedname = "New feed name"

        assertNotEquals(discussionDraftBean,newBean)

    }




}
