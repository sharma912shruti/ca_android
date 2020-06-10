package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.*
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CourseDetailBeanTest {

   var courseDetailBean  =  CourseDetailBean()

    @Before
    fun setFakeData()
    {
        courseDetailBean  =  CourseDetailBean()

    }
    @Test
    fun check_for_getter_and_setter()
    {
        courseDetailBean.alertsData = null
        courseDetailBean.areaPeopleData = null
        courseDetailBean.authData = null
        courseDetailBean.classmatesData = null
        courseDetailBean.contactsData = null
        courseDetailBean.courseroomData = null
        courseDetailBean.dashboardData = null
        courseDetailBean.degreeCompletionPlanData = null
        courseDetailBean.discussionData = null
        courseDetailBean.errorData = null
        courseDetailBean.feedname = null
        courseDetailBean.flexpathAssessmentsAndStatusResponse = null
        courseDetailBean.gmailUnreadCountData = null
        courseDetailBean.homeData = null
        courseDetailBean.isAuthenticated = null
        courseDetailBean.logoutSuccessful = null
        courseDetailBean.mobileFeedServiceResponse = null
        courseDetailBean.newCourseroomData = null
        courseDetailBean.newDiscussionData = null
        courseDetailBean.newsData = null
        courseDetailBean.profileData = null
        courseDetailBean.profileLearnerData = null
        courseDetailBean.registerData = null
        courseDetailBean.socialConnectionsData = null
        courseDetailBean.viewportsData = null
        courseDetailBean.vistaDiscussionData = null

        Assert.assertNull(courseDetailBean.alertsData)
        Assert.assertNull(courseDetailBean.areaPeopleData)
        Assert.assertNull(courseDetailBean.authData)
        Assert.assertNull(courseDetailBean.classmatesData)
        Assert.assertNull(courseDetailBean.contactsData)
        Assert.assertNull(courseDetailBean.courseroomData)
        Assert.assertNull(courseDetailBean.dashboardData)
        Assert.assertNull(courseDetailBean.degreeCompletionPlanData)
        Assert.assertNull(courseDetailBean.discussionData)
        Assert.assertNull(courseDetailBean.errorData)
        Assert.assertNull(courseDetailBean.feedname)
        Assert.assertNull(courseDetailBean.flexpathAssessmentsAndStatusResponse)
        Assert.assertNull(courseDetailBean.gmailUnreadCountData)
        Assert.assertNull(courseDetailBean.homeData)
        Assert.assertNull(courseDetailBean.isAuthenticated)
        Assert.assertNull(courseDetailBean.logoutSuccessful)
        Assert.assertNull(courseDetailBean.mobileFeedServiceResponse)
        Assert.assertNull(courseDetailBean.newCourseroomData)
        Assert.assertNull(courseDetailBean.newDiscussionData)
        Assert.assertNull(courseDetailBean.newsData)
        Assert.assertNull(courseDetailBean.profileData)
        Assert.assertNull(courseDetailBean.profileLearnerData)
        Assert.assertNull(courseDetailBean.registerData)
        Assert.assertNull(courseDetailBean.socialConnectionsData)
        Assert.assertNull(courseDetailBean.viewportsData)
        Assert.assertNull(courseDetailBean.vistaDiscussionData)

        var mobileFeedServiceResponse = CourseDetailBean.MobileFeedServiceResponse()
        mobileFeedServiceResponse.serverName = null
        mobileFeedServiceResponse.trackingId = null

        Assert.assertNull(mobileFeedServiceResponse.serverName)
        Assert.assertNull(mobileFeedServiceResponse.trackingId)

        var newCourseroomData = CourseDetailBean.NewCourseroomData()
        newCourseroomData.courseDetails = null
        newCourseroomData.courseEnrollments = null
        newCourseroomData.currentCourseEnrollments = null
        newCourseroomData.timeZoneVal = null
        newCourseroomData.toolInformationForGrades = null

        Assert.assertNull(newCourseroomData.courseDetails )
        Assert.assertNull(newCourseroomData.courseEnrollments)
        Assert.assertNull(newCourseroomData.currentCourseEnrollments )
        Assert.assertNull(newCourseroomData.timeZoneVal )
        Assert.assertNull(newCourseroomData.toolInformationForGrades )

        var courseDetails = CourseDetailBean.CourseDetails()
        courseDetails.courseAnnouncements = null
        courseDetails.academicEngagementAlert = null
        courseDetails.announcementLink = null
        courseDetails.courseActivity = null
        courseDetails.courseAssignmentNotifications = null
        courseDetails.courseGradeStatusNotifications = null
        courseDetails.courseIdentifier = null
        courseDetails.courseLink = null
        courseDetails.courseMessageCount = null
        courseDetails.courseSection = null
        courseDetails.discussionLink = null
        courseDetails.discussionsCountByCourseIdResponse = null
        courseDetails.enrolled = null
        courseDetails.faculty = null
        courseDetails.facultyRole = null
        courseDetails.flexpath2 = null
        courseDetails.flexpathCourse = null
        courseDetails.messageLink = null
        courseDetails.messaging =  null
        courseDetails.pccpAssignment = null
        courseDetails.westworld = null

        Assert.assertNull(courseDetails.courseAnnouncements)
        Assert.assertNull(courseDetails.academicEngagementAlert)
        Assert.assertNull(courseDetails.announcementLink)
        Assert.assertNull(courseDetails.courseActivity)
        Assert.assertNull(courseDetails.courseAssignmentNotifications)
        Assert.assertNull(courseDetails.courseGradeStatusNotifications)
        Assert.assertNull(courseDetails.courseIdentifier)
        Assert.assertNull(courseDetails.courseMessageCount)
        Assert.assertNull(courseDetails.courseSection)
        Assert.assertNull(courseDetails.discussionLink)
        Assert.assertNull(courseDetails.discussionsCountByCourseIdResponse)
        Assert.assertNull(courseDetails.enrolled)
        Assert.assertNull(courseDetails.faculty)
        Assert.assertNull(courseDetails.facultyRole)
        Assert.assertNull(courseDetails.flexpath2)
        Assert.assertNull(courseDetails.flexpathCourse)
        Assert.assertNull(courseDetails.messageLink)
        Assert.assertNull(courseDetails.messaging)
        Assert.assertNull(courseDetails.pccpAssignment)
        Assert.assertNull(courseDetails.westworld)

        var academicEngagementAlert = CourseDetailBean.AcademicEngagementAlert()
        academicEngagementAlert.fpocourse = null
        academicEngagementAlert.graded = null
        academicEngagementAlert.hasPCCPActivity = null
        academicEngagementAlert.hasPostingActivity = null
        academicEngagementAlert.lastPostingDate = null

        Assert.assertNull(academicEngagementAlert.fpocourse )
        Assert.assertNull(academicEngagementAlert.graded )
        Assert.assertNull(academicEngagementAlert.hasPCCPActivity )
        Assert.assertNull(academicEngagementAlert.hasPostingActivity )
        Assert.assertNull(academicEngagementAlert.lastPostingDate )

        var learnerActivity = CourseDetailBean.LearnerActivity()
        learnerActivity.employeeId = null
        learnerActivity.lastAcademicEngagement = null
        learnerActivity.lastCourseroomAccess  = null

        Assert.assertNull(learnerActivity.employeeId)
        Assert.assertNull(learnerActivity.lastAcademicEngagement)
        Assert.assertNull(learnerActivity.lastCourseroomAccess)

        var courseGradeStatusNotifications = CourseDetailBean.CourseGradeStatusNotifications()
        courseGradeStatusNotifications .assignmentGrades = null
        courseGradeStatusNotifications.blogsGrades = null
        courseGradeStatusNotifications.currentGrade = null
        courseGradeStatusNotifications.discussionParticipation = null
        courseGradeStatusNotifications.discussionsGrades = null
        courseGradeStatusNotifications.journalsGrades = null
        courseGradeStatusNotifications.quizzesGrades = null

        Assert.assertNull(courseGradeStatusNotifications .assignmentGrades)
        Assert.assertNull(courseGradeStatusNotifications .blogsGrades)
        Assert.assertNull(courseGradeStatusNotifications .currentGrade)
        Assert.assertNull(courseGradeStatusNotifications .discussionParticipation)
        Assert.assertNull(courseGradeStatusNotifications .discussionsGrades)
        Assert.assertNull(courseGradeStatusNotifications .journalsGrades)
        Assert.assertNull(courseGradeStatusNotifications .quizzesGrades)

        var discussionsCountByCourseIdResponse =
            CourseDetailBean.DiscussionsCountByCourseIdResponse()
        discussionsCountByCourseIdResponse.coursePk = null
        discussionsCountByCourseIdResponse.new = null
        discussionsCountByCourseIdResponse.newMessages = null
        discussionsCountByCourseIdResponse.replies = null
        discussionsCountByCourseIdResponse.unReadThreadCount = null

        Assert.assertNull(discussionsCountByCourseIdResponse.unReadThreadCount)
        Assert.assertNull(discussionsCountByCourseIdResponse.coursePk)
        Assert.assertNull(discussionsCountByCourseIdResponse.newMessages)
        Assert.assertNull(discussionsCountByCourseIdResponse.new)
        Assert.assertNull(discussionsCountByCourseIdResponse.replies)

        var attempt = CourseDetailBean.Attempt()
        attempt.attemptId = null
        attempt.attemptName = null
        attempt.gradedDate = null
        attempt.score = null
        attempt.status = null
        attempt.submittedDate = null

        Assert.assertNull(attempt.attemptId)
        Assert.assertNull(attempt.attemptName)
        Assert.assertNull(attempt.gradedDate)
        Assert.assertNull(attempt.score)
        Assert.assertNull(attempt.status)
        Assert.assertNull(attempt.submittedDate)

    }
    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(courseDetailBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(courseDetailBean.hashCode())
    }
    @Test
    fun check_whether_bean_is_gson_supported()
    {
        val auth = CourseDetailBean.AuthData()

        val employeeId = CourseDetailBean.AuthData.EmployeeId()
        employeeId!!.value =   "2400217"

        auth.employeeId = employeeId

        courseDetailBean.authData   = auth

        var gson = Gson( )
        var json = gson.toJson(courseDetailBean)

        var newBean = gson.fromJson(json , CourseDetailBean::class.java )

        assertEquals(courseDetailBean,newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        val auth = CourseDetailBean.AuthData()

        val employeeId = CourseDetailBean.AuthData.EmployeeId()
        employeeId!!.value =   "2400217"

        auth.employeeId = employeeId

        courseDetailBean.authData   = auth
        var gson = Gson( )
        var json = gson.toJson(courseDetailBean)
        var newBean = gson.fromJson(json , CourseDetailBean::class.java )

        val nauth = CourseDetailBean.AuthData()

        val nemployeeId = CourseDetailBean.AuthData.EmployeeId()
        nemployeeId!!.value =   "3400217"

        nauth.employeeId = nemployeeId

        newBean.authData   = nauth

        assertNotEquals(courseDetailBean,newBean)
    }




}
