package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.AnnouncementsBean
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test


class AnnouncementBeanTest {

   var announcementsBean  =  AnnouncementsBean()

    @Before
    fun setFakeData()
    {
        announcementsBean  =  AnnouncementsBean()

    }

    @Test
    fun check_for_getter_and_setter()
    {

        announcementsBean.alertsData = null
        announcementsBean.areaPeopleData = null
        announcementsBean.authData   = null
        announcementsBean.classmatesData = null
        announcementsBean.contactsData = null
        announcementsBean.courseroomData = null
        announcementsBean.dashboardData = null
        announcementsBean.degreeCompletionPlanData = null
        announcementsBean.discussionData = null
        announcementsBean.errorData = null
        announcementsBean.feedname = null
        announcementsBean.flexpathAssessmentsAndStatusResponse = null
        announcementsBean.gmailUnreadCountData = null
        announcementsBean.homeData = null
        announcementsBean.isAuthenticated = null
        announcementsBean.logoutSuccessful = null
        announcementsBean.mobileFeedServiceResponse = null
        announcementsBean.newCourseroomData = null
        announcementsBean.newDiscussionData = null
        announcementsBean.newsData = null
        announcementsBean.profileData = null
        announcementsBean.profileLearnerData = null
        announcementsBean.registerData = null
        announcementsBean.socialConnectionsData = null
        announcementsBean.viewportsData = null
        announcementsBean.vistaDiscussionData = null

        assertNull (announcementsBean.alertsData)
        assertNull (announcementsBean.areaPeopleData)
        assertNull (announcementsBean.authData  )
        assertNull (announcementsBean.classmatesData)
        assertNull (announcementsBean.contactsData)
        assertNull (announcementsBean.courseroomData)
        assertNull (announcementsBean.dashboardData)
        assertNull (announcementsBean.degreeCompletionPlanData)
        assertNull (announcementsBean.discussionData)
        assertNull (announcementsBean.errorData)
        assertNull (announcementsBean.feedname)
        assertNull (announcementsBean.flexpathAssessmentsAndStatusResponse)
        assertNull (announcementsBean.gmailUnreadCountData)
        assertNull (announcementsBean.homeData)
        assertNull (announcementsBean.isAuthenticated)
        assertNull (announcementsBean.logoutSuccessful)
        assertNull (announcementsBean.mobileFeedServiceResponse)
        assertNull (announcementsBean.newCourseroomData)
        assertNull (announcementsBean.newDiscussionData)
        assertNull (announcementsBean.newsData)
        assertNull (announcementsBean.profileData)
        assertNull (announcementsBean.profileLearnerData)
        assertNull (announcementsBean.registerData)
        assertNull (announcementsBean.socialConnectionsData)
        assertNull (announcementsBean.viewportsData)
        assertNull (announcementsBean.vistaDiscussionData)
    }

    @Test
    fun check_for_toString()
    {
        assertNotNull(announcementsBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        assertNotNull(announcementsBean.hashCode())
    }



    @Test
    fun check_whether_bean_is_gson_supported()
    {
        var gson = Gson( )
        var json = gson.toJson(announcementsBean)

        var newBean = gson.fromJson(json , AnnouncementsBean::class.java )

        assertEquals(announcementsBean,newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        var gson = Gson( )
        var json = gson.toJson(announcementsBean)

        var newBean = gson.fromJson(json , AnnouncementsBean::class.java )
        newBean.feedname = "New feed name"

        assertNotEquals(announcementsBean,newBean)

    }




}
