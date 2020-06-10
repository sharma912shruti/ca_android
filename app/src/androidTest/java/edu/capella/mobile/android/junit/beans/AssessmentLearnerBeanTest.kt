package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.AnnouncementsBean
import edu.capella.mobile.android.bean.AssessmentLearnerBean
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  22-04-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
class AssessmentLearnerBeanTest {

    var assessmentLearnerBean  =  AssessmentLearnerBean()

    @Before
    fun setFakeData()
    {
        assessmentLearnerBean  =  AssessmentLearnerBean()

    }

    @Test
    fun check_for_getter_and_setter()
    {

        assessmentLearnerBean.alertsData = null
        assessmentLearnerBean.areaPeopleData = null
        assessmentLearnerBean.authData   = null
        assessmentLearnerBean.classmatesData = null
        assessmentLearnerBean.contactsData = null
        assessmentLearnerBean.courseroomData = null
        assessmentLearnerBean.dashboardData = null
        assessmentLearnerBean.degreeCompletionPlanData = null
        assessmentLearnerBean.discussionData = null
        assessmentLearnerBean.errorData = null
        assessmentLearnerBean.feedname = null
        assessmentLearnerBean.flexpathAssessmentsAndStatusResponse = null
        assessmentLearnerBean.gmailUnreadCountData = null
        assessmentLearnerBean.homeData = null
        assessmentLearnerBean.isAuthenticated = null
        assessmentLearnerBean.logoutSuccessful = null
        assessmentLearnerBean.mobileFeedServiceResponse = null
        assessmentLearnerBean.newCourseroomData = null
        assessmentLearnerBean.newDiscussionData = null
        assessmentLearnerBean.newsData = null
        assessmentLearnerBean.profileData = null
        assessmentLearnerBean.profileLearnerData = null
        assessmentLearnerBean.registerData = null
        assessmentLearnerBean.socialConnectionsData = null
        assessmentLearnerBean.viewportsData = null
        assessmentLearnerBean.vistaDiscussionData = null

        Assert.assertNull(assessmentLearnerBean.alertsData)
        Assert.assertNull(assessmentLearnerBean.areaPeopleData)
        Assert.assertNull(assessmentLearnerBean.authData)
        Assert.assertNull(assessmentLearnerBean.classmatesData)
        Assert.assertNull(assessmentLearnerBean.contactsData)
        Assert.assertNull(assessmentLearnerBean.courseroomData)
        Assert.assertNull(assessmentLearnerBean.dashboardData)
        Assert.assertNull(assessmentLearnerBean.degreeCompletionPlanData)
        Assert.assertNull(assessmentLearnerBean.discussionData)
        Assert.assertNull(assessmentLearnerBean.errorData)
        Assert.assertNull(assessmentLearnerBean.feedname)
        Assert.assertNull(assessmentLearnerBean.flexpathAssessmentsAndStatusResponse)
        Assert.assertNull(assessmentLearnerBean.gmailUnreadCountData)
        Assert.assertNull(assessmentLearnerBean.homeData)
        Assert.assertNull(assessmentLearnerBean.isAuthenticated)
        Assert.assertNull(assessmentLearnerBean.logoutSuccessful)
        Assert.assertNull(assessmentLearnerBean.mobileFeedServiceResponse)
        Assert.assertNull(assessmentLearnerBean.newCourseroomData)
        Assert.assertNull(assessmentLearnerBean.newDiscussionData)
        Assert.assertNull(assessmentLearnerBean.newsData)
        Assert.assertNull(assessmentLearnerBean.profileData)
        Assert.assertNull(assessmentLearnerBean.profileLearnerData)
        Assert.assertNull(assessmentLearnerBean.registerData)
        Assert.assertNull(assessmentLearnerBean.socialConnectionsData)
        Assert.assertNull(assessmentLearnerBean.viewportsData)
        Assert.assertNull(assessmentLearnerBean.vistaDiscussionData)

        var flexPathAssessmentAndStatusResponse = AssessmentLearnerBean.FlexpathAssessmentsAndStatus()

        flexPathAssessmentAndStatusResponse.assessmentCount = null
        flexPathAssessmentAndStatusResponse.assignmentTitle = null
        flexPathAssessmentAndStatusResponse.gradedDateTime = null
        flexPathAssessmentAndStatusResponse.pccpTargetDate = null
        flexPathAssessmentAndStatusResponse.attempts = null
        flexPathAssessmentAndStatusResponse.assignmentLink = null
        flexPathAssessmentAndStatusResponse.due = null
        flexPathAssessmentAndStatusResponse.id = null
        flexPathAssessmentAndStatusResponse.instructorViewedAssignment = null
        flexPathAssessmentAndStatusResponse.learnerFullName = null
        flexPathAssessmentAndStatusResponse.learnerId = null
        flexPathAssessmentAndStatusResponse.pccpAssignmentId = null
        flexPathAssessmentAndStatusResponse.pccpAssignmentName = null
        flexPathAssessmentAndStatusResponse.pccpAttemptsCount = null
        flexPathAssessmentAndStatusResponse.pccpEvaluatedDate = null
        flexPathAssessmentAndStatusResponse.pccpEvaruatedDate = null
        flexPathAssessmentAndStatusResponse.pccpStatus = null
        flexPathAssessmentAndStatusResponse.pccpSubmittedDate = null
        flexPathAssessmentAndStatusResponse.status = null
        flexPathAssessmentAndStatusResponse.statusDateTime = null
        flexPathAssessmentAndStatusResponse.submittedDateTime = null

        Assert.assertNull(flexPathAssessmentAndStatusResponse.assessmentCount)
        Assert.assertNull(flexPathAssessmentAndStatusResponse.assignmentTitle)
        Assert.assertNull(flexPathAssessmentAndStatusResponse.gradedDateTime)
        Assert.assertNull(flexPathAssessmentAndStatusResponse.pccpTargetDate)
        Assert.assertNull(flexPathAssessmentAndStatusResponse.attempts)
        Assert.assertNull(flexPathAssessmentAndStatusResponse.assignmentLink)
        Assert.assertNull(flexPathAssessmentAndStatusResponse.due)
        Assert.assertNull(flexPathAssessmentAndStatusResponse.id)
        Assert.assertNull(flexPathAssessmentAndStatusResponse.instructorViewedAssignment)
        Assert.assertNull(flexPathAssessmentAndStatusResponse.learnerFullName)
        Assert.assertNull(flexPathAssessmentAndStatusResponse.learnerId)
        Assert.assertNull(flexPathAssessmentAndStatusResponse.pccpAssignmentId )
        Assert.assertNull(flexPathAssessmentAndStatusResponse.pccpAssignmentName)
        Assert.assertNull(flexPathAssessmentAndStatusResponse.pccpAttemptsCount)
        Assert.assertNull(flexPathAssessmentAndStatusResponse.pccpEvaluatedDate)
        Assert.assertNull(flexPathAssessmentAndStatusResponse.pccpEvaruatedDate)
        Assert.assertNull(flexPathAssessmentAndStatusResponse.pccpStatus)
        Assert.assertNull(flexPathAssessmentAndStatusResponse.pccpSubmittedDate )
        Assert.assertNull(flexPathAssessmentAndStatusResponse.status)
        Assert.assertNull(flexPathAssessmentAndStatusResponse.statusDateTime)
        Assert.assertNull(flexPathAssessmentAndStatusResponse.submittedDateTime)

        var attempts = AssessmentLearnerBean.Attempt()
        attempts.status = null
        attempts.submittedDate = null
        attempts.score = null
        attempts.gradedDate = null
        attempts.attemptName = null
        attempts.attemptId = null
        attempts.instructorComments = null

        Assert.assertNull(attempts.status)
        Assert.assertNull(attempts.instructorComments)
        Assert.assertNull(attempts.submittedDate)
        Assert.assertNull(attempts.score)
        Assert.assertNull(attempts.gradedDate)
        Assert.assertNull(attempts.attemptName)
        Assert.assertNull(attempts.attemptId)

    }

    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(assessmentLearnerBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(assessmentLearnerBean.hashCode())
    }



    @Test
    fun check_whether_bean_is_gson_supported()
    {
        var gson = Gson( )
        var json = gson.toJson(assessmentLearnerBean)

        var newBean = gson.fromJson(json , AssessmentLearnerBean::class.java )

        Assert.assertEquals(assessmentLearnerBean, newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        var gson = Gson( )
        var json = gson.toJson(assessmentLearnerBean)

        var newBean = gson.fromJson(json , AssessmentLearnerBean::class.java )
        newBean.feedname = "New feed name"

        Assert.assertNotEquals(assessmentLearnerBean, newBean)

    }
}