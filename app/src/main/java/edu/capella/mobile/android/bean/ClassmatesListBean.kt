package edu.capella.mobile.android.bean

/**
 * ClassmatesListBean.kt : Bean / Pojo class to store response of JSON received from API.
 *
 * Created by Jayesh Lahare on 09/03/20.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 09/03/20.
 *
 */
data class ClassmatesListBean(
    var alertsData: Any? = null,
    var areaPeopleData: Any? = null,
    var authData: AuthData? = null,
    var classmatesData: ClassmatesData? = null,
    var contactsData: Any? = null,
    var courseroomData: Any? = null,
    var dashboardData: Any? = null,
    var degreeCompletionPlanData: Any? = null,
    var discussionData: Any? = null,
    var errorData: ErrorData? = null,
    var feedname: String? = null,
    var flexpathAssessmentsAndStatusResponse: Any? = null,
    var gmailUnreadCountData: Any? = null,
    var homeData: Any? = null,
    var isAuthenticated: Any? = null,
    var logoutSuccessful: Any? = null,
    var mobileFeedServiceResponse: Any? = null,
    var newCourseroomData: Any? = null,
    var newDiscussionData: Any? = null,
    var newsData: Any? = null,
    var profileData: Any? = null,
    var profileLearnerData: Any? = null,
    var registerData: Any? = null,
    var socialConnectionsData: Any? = null,
    var viewportsData: Any? = null,
    var vistaDiscussionData: Any? = null
)
{
    data class AuthData(
        var emanxh: Any? = null,
        var employeeId: EmployeeId? = null,
        var hxdowssap: Any? = null,
        var token: String? = null,
        var username: String? = null
    ) {
        data class EmployeeId(
            var value: String? = null
        )
    }

    data class ClassmatesData(
        var courseMembers: List<CourseMember?>? = null
    ) {
        data class CourseMember(
            var courseId: String? = null,
            var member: List<Member?>? = null
        ) {
            data class Member(
                var emailAddress: String? = null,
                var employeeId: String? = null,
                var enrollmentStatus: String? = null,
                var faculty: Boolean? = null,
                var facultyRole: String? = null,
                var ferpa: Boolean? = null,
                var firstName: String? = null,
                var lastName: String? = null,
                var phoneNumber: String? = null,
                var profileImage: String? = null
            )
        }
    }


    data class ErrorData(
        var message: String? = null,
        var details: String? = null,
        var code: String? = null
    )
}