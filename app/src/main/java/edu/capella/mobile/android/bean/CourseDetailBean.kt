package edu.capella.mobile.android.bean

import java.io.Serializable

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  12-03-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
data class CourseDetailBean(
    var alertsData: Any?=null,
    var areaPeopleData: Any?=null,
    var authData: AuthData?=null,
    var classmatesData: Any?=null,
    var contactsData: Any?=null,
    var courseroomData: NewCourseroomData?=null,
    var dashboardData: Any?=null,
    var degreeCompletionPlanData: Any?=null,
    var discussionData: Any?=null,
    var errorData: Any?=null,
    var feedname: String?=null,
    var flexpathAssessmentsAndStatusResponse: Any?=null,
    var gmailUnreadCountData: Any?=null,
    var homeData: Any?=null,
    var isAuthenticated: Any?=null,
    var logoutSuccessful: Any?=null,
    var mobileFeedServiceResponse: MobileFeedServiceResponse?=null,
    var newCourseroomData: NewCourseroomData?=null,
    var newDiscussionData: Any?=null,
    var newsData: Any?=null,
    var profileData: Any?=null,
    var profileLearnerData: Any?=null,
    var registerData: Any?=null,
    var socialConnectionsData: Any?=null,
    var viewportsData: Any?=null,
    var vistaDiscussionData: Any?=null
):Serializable {
    override fun toString(): String {
        return "CourseDetailBean(alertsData=$alertsData, areaPeopleData=$areaPeopleData, authData=$authData, classmatesData=$classmatesData, contactsData=$contactsData, courseroomData=$courseroomData, dashboardData=$dashboardData, degreeCompletionPlanData=$degreeCompletionPlanData, discussionData=$discussionData, errorData=$errorData, feedname='$feedname', flexpathAssessmentsAndStatusResponse=$flexpathAssessmentsAndStatusResponse, gmailUnreadCountData=$gmailUnreadCountData, homeData=$homeData, isAuthenticated=$isAuthenticated, logoutSuccessful=$logoutSuccessful, mobileFeedServiceResponse=$mobileFeedServiceResponse, newCourseroomData=$newCourseroomData, newDiscussionData=$newDiscussionData, newsData=$newsData, profileData=$profileData, profileLearnerData=$profileLearnerData, registerData=$registerData, socialConnectionsData=$socialConnectionsData, viewportsData=$viewportsData, vistaDiscussionData=$vistaDiscussionData)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CourseDetailBean

        if (alertsData != other.alertsData) return false
        if (areaPeopleData != other.areaPeopleData) return false
        if (authData != other.authData) return false
        if (classmatesData != other.classmatesData) return false
        if (contactsData != other.contactsData) return false
        if (courseroomData != other.courseroomData) return false
        if (dashboardData != other.dashboardData) return false
        if (degreeCompletionPlanData != other.degreeCompletionPlanData) return false
        if (discussionData != other.discussionData) return false
        if (errorData != other.errorData) return false
        if (feedname != other.feedname) return false
        if (flexpathAssessmentsAndStatusResponse != other.flexpathAssessmentsAndStatusResponse) return false
        if (gmailUnreadCountData != other.gmailUnreadCountData) return false
        if (homeData != other.homeData) return false
        if (isAuthenticated != other.isAuthenticated) return false
        if (logoutSuccessful != other.logoutSuccessful) return false
        if (mobileFeedServiceResponse != other.mobileFeedServiceResponse) return false
        if (newCourseroomData != other.newCourseroomData) return false
        if (newDiscussionData != other.newDiscussionData) return false
        if (newsData != other.newsData) return false
        if (profileData != other.profileData) return false
        if (profileLearnerData != other.profileLearnerData) return false
        if (registerData != other.registerData) return false
        if (socialConnectionsData != other.socialConnectionsData) return false
        if (viewportsData != other.viewportsData) return false
        if (vistaDiscussionData != other.vistaDiscussionData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = alertsData.hashCode()
        result = 31 * result + areaPeopleData.hashCode()
        result = 31 * result + authData.hashCode()
        result = 31 * result + classmatesData.hashCode()
        result = 31 * result + contactsData.hashCode()
        result = 31 * result + courseroomData.hashCode()
        result = 31 * result + dashboardData.hashCode()
        result = 31 * result + degreeCompletionPlanData.hashCode()
        result = 31 * result + discussionData.hashCode()
        result = 31 * result + errorData.hashCode()
        result = 31 * result + feedname.hashCode()
        result = 31 * result + flexpathAssessmentsAndStatusResponse.hashCode()
        result = 31 * result + gmailUnreadCountData.hashCode()
        result = 31 * result + homeData.hashCode()
        result = 31 * result + isAuthenticated.hashCode()
        result = 31 * result + logoutSuccessful.hashCode()
        result = 31 * result + mobileFeedServiceResponse.hashCode()
        result = 31 * result + newCourseroomData.hashCode()
        result = 31 * result + newDiscussionData.hashCode()
        result = 31 * result + newsData.hashCode()
        result = 31 * result + profileData.hashCode()
        result = 31 * result + profileLearnerData.hashCode()
        result = 31 * result + registerData.hashCode()
        result = 31 * result + socialConnectionsData.hashCode()
        result = 31 * result + viewportsData.hashCode()
        result = 31 * result + vistaDiscussionData.hashCode()
        return result
    }


    data class MobileFeedServiceResponse(
        var serverName: String? = null,
        var trackingId: String? = null
    ) : Serializable

    data class NewCourseroomData(
        var courseDetails: CourseDetails? = null,
        var courseEnrollments: CourseDetails? = null,
        var currentCourseEnrollments: CourseDetails? = null,
        var timeZoneVal: Int? = null,
        var toolInformationForGrades: String? = null
    ) : Serializable

    data class CourseDetails(
        var academicEngagementAlert: AcademicEngagementAlert? = null,
        var announcementLink: Any? = null,
        var courseActivity: CourseActivity? = null,
        var courseAnnouncements: List<CourseAnnouncement>? = null,
        var courseAssignmentNotifications: List<Any>? = null,
        var courseGradeStatusNotifications: CourseGradeStatusNotifications? = null,
        var courseIdentifier: String? = null,
        var courseLink: String? = null,
        var courseMessageCount: String? = null,
        var courseSection: Any? = null,
        var discussionLink: Any? = null,
        var discussionsCountByCourseIdResponse: DiscussionsCountByCourseIdResponse? = null,
        var enrolled: Boolean? = null,
        var faculty: Boolean? = null,
        var facultyRole: Any? = null,
        var westworld:String? = null,
        var flexpath2: Boolean? = null,
        var flexpathCourse: Boolean? = null,
        var messageLink: String? = null,
        var messaging: Boolean? = null,
        var pccpAssignment: PccpAssignment? = null
    ) : Serializable

    data class AcademicEngagementAlert(
        var fpocourse: Boolean? = null,
        var graded: Boolean? = null,
        var hasPCCPActivity: Boolean? = null,
        var hasPostingActivity: Boolean? = null,
        var lastPostingDate: Any? = null
    ) : Serializable

    data class CourseActivity(
        var learnerActivity: List<LearnerActivity>? = null
    ) : Serializable

    data class LearnerActivity(
        var employeeId: String? = null,
        var lastAcademicEngagement: Any? = null,
        var lastCourseroomAccess: Any? = null
    ) : Serializable

    data class CourseGradeStatusNotifications(
        var assignmentGrades: List<Any>? = null,
        var blogsGrades: List<Any>? = null,
        var currentGrade: Any? = null,
        var discussionParticipation: Any? = null,
        var discussionsGrades: List<Any>? = null,
        var journalsGrades: List<Any>? = null,
        var quizzesGrades: List<Any>? = null
    ) : Serializable

    data class DiscussionsCountByCourseIdResponse(
        var coursePk: Any? = null,
        var new: Any? = null,
        var newMessages: Any? = null,
        var replies: Any? = null,
        var unReadThreadCount: Any? = null
    ) : Serializable

    data class PccpAssignment(
        var pccpAssignments: Any
    ) : Serializable

    data class CourseAssignmentNotifications(
        var assignmentTitle: String? = null,
        var notificationDescription: String? = null,
        var notificationStatus: String? = null,
        var notificationDate: Long? = null,
        var attempts: Attempts? = null
    ) : Serializable {

        private var read: String? = "Unread"

        fun setRead(value: String) {
            this.read = value
        }

        fun getRead(): String? {
            return read
        }

    }

    data class Attempt(
        var attemptId: String? = null,
        var attemptName: String? = null,
        var gradedDate: String? = null,
        var score: String? = null,
        var status: String? = null,
        var submittedDate: String? = null
    ):Serializable {

        private var read: String? = "Unread"

        fun setRead(value: String) {
            this.read = value
        }

        fun getRead(): String? {
            return read
        }

    }


    data class Attempts(
        var attempts: List<Attempt>? = null
    )


        data class CourseAnnouncement(
        var courseIdentifier: String? = null,
        var title: String? = null,
        var startDate: Long? = null,
        var endDate: Long? = null,
        var content: String? = null
    ):Serializable{

        private var read: String? = "Unread"

        fun setRead(value: String) {
            this.read = value
        }

        fun getRead(): String? {
            return read
        }

    }


    data class AuthData(
        var emanxh: String? = null,
        var employeeId: EmployeeId? = null,
        var hxdowssap: String? = null,
        var token: String? = null,
        var username: String? = null
    ):Serializable
    {
        data class EmployeeId(
            var value: String? = null
        ):Serializable
    }



    data class ErrorData(
        var message: String? = null,
        var details: String? = null,
        var code: String? = null
    ):Serializable
}
