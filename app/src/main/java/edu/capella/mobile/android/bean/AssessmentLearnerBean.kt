package edu.capella.mobile.android.bean

import java.io.Serializable

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  14-04-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */

data class AssessmentLearnerBean(
    var alertsData: Any? = null,
    var areaPeopleData: Any? = null,
    var authData: AuthData? = null,
    var classmatesData: Any? = null,
    var contactsData: Any? = null,
    var courseroomData: Any? = null,
    var dashboardData: Any? = null,
    var degreeCompletionPlanData: Any? = null,
    var discussionData: Any? = null,
    var errorData: Any? = null,
    var feedname: String? = null,
    var flexpathAssessmentsAndStatusResponse: FlexpathAssessmentsAndStatusResponse? = null,
    var gmailUnreadCountData: Any? = null,
    var homeData: Any? = null,
    var isAuthenticated: Any? = null,
    var logoutSuccessful: Any? = null,
    var mobileFeedServiceResponse: MobileFeedServiceResponse? = null,
    var newCourseroomData: Any? = null,
    var newDiscussionData: Any? = null,
    var newsData: Any? = null,
    var profileData: Any? = null,
    var profileLearnerData: Any? = null,
    var registerData: Any? = null,
    var socialConnectionsData: Any? = null,
    var viewportsData: Any? = null,
    var vistaDiscussionData: Any? = null
) :Serializable{

    data class AuthData(
        var emanxh: Any? = null,
        var employeeId: EmployeeId? = null,
        var hxdowssap: Any? = null,
        var profileImage: Any? = null,
        var token: String? = null,
        var username: String? = null
    ):Serializable

    data class FlexpathAssessmentsAndStatusResponse(
        var flexpathAssessmentsAndStatuss: List<FlexpathAssessmentsAndStatus>? = null
    ):Serializable

    data class MobileFeedServiceResponse(
        var serverName: String? = null,
        var trackingId: String? = null
    ):Serializable

    data class EmployeeId(
        var varue: String? = null
    ):Serializable

    data class FlexpathAssessmentsAndStatus(
        var assessmentCount: Int? = null,
        var assignmentLink: String? = null,
        var assignmentTitle: String? = null,
        var attempts: Attempts? = null,
        var due: Boolean? = null,
        var gradedDateTime: String? = null,
        var id: String? = null,
        var instructorViewedAssignment: String? = null,
        var learnerFullName: String? = null,
        var learnerId: String? = null,
        var pccpAssignmentId: String? = null,
        var pccpAssignmentName: String? = null,
        var pccpAttemptsCount: String? = null,
        var pccpEvaluatedDate:String?=null,
        var pccpEvaruatedDate: String? = null,
        var pccpStatus: String? = null,
        var pccpSubmittedDate: String? = null,
        var pccpTargetDate: String? = null,
        var status: String? = null,
        var statusDateTime: String? = null,
        var submittedDateTime: String? = null
    ):Serializable
    {

        private var read: String? = "Unread"

        fun setRead(value: String) {
            this.read = value
        }

        fun getRead(): String? {
            return read
        }

    }

    data class Attempts(
        var attempts: ArrayList<Attempt>? = null
    ):Serializable

    data class Attempt(
        var attemptId: String? = null,
        var attemptName: String? = null,
        var gradedDate: String? = null,
        var instructorComments: String? = null,
        var score: String? = null,
        var status: String? = null,
        var overdue:Boolean? = null,
        var submittedDate: String? = null
    ):Serializable
    {

        private var read: String? = "Unread"

        fun setRead(value: String) {
            this.read = value
        }

        fun getRead(): String? {
            return read
        }

    }
}
