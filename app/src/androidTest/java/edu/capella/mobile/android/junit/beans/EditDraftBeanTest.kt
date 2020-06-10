package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.EditDraftBean
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test


class EditDraftBeanTest {

   var editDraftBean  =  EditDraftBean()

    @Before
    fun setFakeData()
    {
        editDraftBean  =  EditDraftBean()

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

        editDraftBean.alertsData = null
        editDraftBean.areaPeopleData = null
        editDraftBean.authData   = null
        editDraftBean.classmatesData = null
        editDraftBean.contactsData = null
        editDraftBean.courseroomData = null
        editDraftBean.dashboardData = null
        editDraftBean.degreeCompletionPlanData = null
        editDraftBean.discussionData = null
        editDraftBean.errorData = null
        editDraftBean.feedname = null
        editDraftBean.flexpathAssessmentsAndStatusResponse = null
        editDraftBean.gmailUnreadCountData = null
        editDraftBean.homeData = null
        editDraftBean.isAuthenticated = null
        editDraftBean.logoutSuccessful = null
        editDraftBean.mobileFeedServiceResponse = null
        editDraftBean.newCourseroomData = null
        editDraftBean.newDiscussionData = null
        editDraftBean.newsData = null
        editDraftBean.profileData = null
        editDraftBean.profileLearnerData = null
        editDraftBean.registerData = null
        editDraftBean.socialConnectionsData = null
        editDraftBean.viewportsData = null
        editDraftBean.vistaDiscussionData = null

        assertNull (editDraftBean.alertsData)
        assertNull (editDraftBean.areaPeopleData)
        assertNull (editDraftBean.authData  )
        assertNull (editDraftBean.classmatesData)
        assertNull (editDraftBean.contactsData)
        assertNull (editDraftBean.courseroomData)
        assertNull (editDraftBean.dashboardData)
        assertNull (editDraftBean.degreeCompletionPlanData)
        assertNull (editDraftBean.discussionData)
        assertNull (editDraftBean.errorData)
        assertNull (editDraftBean.feedname)
        assertNull (editDraftBean.flexpathAssessmentsAndStatusResponse)
        assertNull (editDraftBean.gmailUnreadCountData)
        assertNull (editDraftBean.homeData)
        assertNull (editDraftBean.isAuthenticated)
        assertNull (editDraftBean.logoutSuccessful)
        assertNull (editDraftBean.mobileFeedServiceResponse)
        assertNull (editDraftBean.newCourseroomData)
        assertNull (editDraftBean.newDiscussionData)
        assertNull (editDraftBean.newsData)
        assertNull (editDraftBean.profileData)
        assertNull (editDraftBean.profileLearnerData)
        assertNull (editDraftBean.registerData)
        assertNull (editDraftBean.socialConnectionsData)
        assertNull (editDraftBean.viewportsData)
        assertNull (editDraftBean.vistaDiscussionData)
    }

    @Test
    fun check_for_toString()
    {
        assertNotNull(editDraftBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        assertNotNull(editDraftBean.hashCode())
    }



    @Test
    fun check_whether_bean_is_gson_supported()
    {
        var gson = Gson( )
        var json = gson.toJson(editDraftBean)

        var newBean = gson.fromJson(json , EditDraftBean::class.java )

        assertEquals(editDraftBean,newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        var gson = Gson( )
        var json = gson.toJson(editDraftBean)

        var newBean = gson.fromJson(json , editDraftBean::class.java )
        newBean.feedname = "New feed name"

        assertNotEquals(editDraftBean,newBean)

    }




}
