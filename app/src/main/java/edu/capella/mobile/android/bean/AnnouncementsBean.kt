package edu.capella.mobile.android.bean

data class AnnouncementsBean(
    var alertsData: Any? = null,
    var areaPeopleData: Any? = null,
    var authData: AuthData? = null,
    var classmatesData: Any? = null,
    var contactsData: Any? = null,
    var courseroomData: CourseroomData? = null,
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
    )
    data class Course(
        var catalogNumber: String? = null,
        var longDescription: Any? = null,
        var number: String? = null,
        var subject: String? = null,
        var title: String? = null,
        var titleLong: Any? = null
    )

    data class CourseroomData(
        var courseEnrollments: Any? = null,
        var currentCourseEnrollments: List<CurrentCourseEnrollment?>? = null
    )

    data class CourseSection(
        var course: Course? = null,
        var endDate: Long? = null,
        var instructor: Instructor? = null,
        var section: String? = null,
        var sectionNumber: String? = null,
        var startDate: Long? = null
    )

    data class CurrentCourseEnrollment(
        var active: Boolean? = null,
        var announcementLink: String? = null,
        var blackboardEnrollment: Boolean? = null,
        var courseAnnouncements: ArrayList<CourseDetailBean.CourseAnnouncement?>? = null,
        var courseAssignmentNotifications: ArrayList<Any>? = null,
        var courseIdentifier: String? = null,
        var courseLink: String? = null,
        var courseMails: ArrayList<Any>? = null,
        var courseSection: CourseSection? = null,
        var discussionLink: String? = null,
        var discussionSummary: DiscussionSummary? = null,
        var instructorProfile: Any? = null,
        var mailCount: Int? = null,
        var mailLink: Any? = null,
        var recentDiscussions: List<Any>? = null
    )


    data class DiscussionSummary(
        var courseNumber: Any? = null,
        var discussionLink: Any? = null,
        var newMessages: Int? = null,
        var newReplies: Int? = null
    )

    data class EmployeeId(
        var varue: String? = null
    )

    data class Instructor(
        var employeeId: String? = null,
        var fullName: String? = null,
        var role: String? = null
    )

    override fun toString(): String {
        return "AnnouncementsBean(alertsData=$alertsData, areaPeopleData=$areaPeopleData, authData=$authData, classmatesData=$classmatesData, contactsData=$contactsData, courseroomData=$courseroomData, dashboardData=$dashboardData, degreeCompletionPlanData=$degreeCompletionPlanData, discussionData=$discussionData, errorData=$errorData, feedname=$feedname, flexpathAssessmentsAndStatusResponse=$flexpathAssessmentsAndStatusResponse, gmailUnreadCountData=$gmailUnreadCountData, homeData=$homeData, isAuthenticated=$isAuthenticated, logoutSuccessful=$logoutSuccessful, mobileFeedServiceResponse=$mobileFeedServiceResponse, newCourseroomData=$newCourseroomData, newDiscussionData=$newDiscussionData, newsData=$newsData, profileData=$profileData, profileLearnerData=$profileLearnerData, registerData=$registerData, socialConnectionsData=$socialConnectionsData, viewportsData=$viewportsData, vistaDiscussionData=$vistaDiscussionData)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AnnouncementsBean

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
        var result = alertsData?.hashCode() ?: 0
        result = 31 * result + (areaPeopleData?.hashCode() ?: 0)
        result = 31 * result + (authData?.hashCode() ?: 0)
        result = 31 * result + (classmatesData?.hashCode() ?: 0)
        result = 31 * result + (contactsData?.hashCode() ?: 0)
        result = 31 * result + (courseroomData?.hashCode() ?: 0)
        result = 31 * result + (dashboardData?.hashCode() ?: 0)
        result = 31 * result + (degreeCompletionPlanData?.hashCode() ?: 0)
        result = 31 * result + (discussionData?.hashCode() ?: 0)
        result = 31 * result + (errorData?.hashCode() ?: 0)
        result = 31 * result + (feedname?.hashCode() ?: 0)
        result = 31 * result + (flexpathAssessmentsAndStatusResponse?.hashCode() ?: 0)
        result = 31 * result + (gmailUnreadCountData?.hashCode() ?: 0)
        result = 31 * result + (homeData?.hashCode() ?: 0)
        result = 31 * result + (isAuthenticated?.hashCode() ?: 0)
        result = 31 * result + (logoutSuccessful?.hashCode() ?: 0)
        result = 31 * result + (mobileFeedServiceResponse?.hashCode() ?: 0)
        result = 31 * result + (newCourseroomData?.hashCode() ?: 0)
        result = 31 * result + (newDiscussionData?.hashCode() ?: 0)
        result = 31 * result + (newsData?.hashCode() ?: 0)
        result = 31 * result + (profileData?.hashCode() ?: 0)
        result = 31 * result + (profileLearnerData?.hashCode() ?: 0)
        result = 31 * result + (registerData?.hashCode() ?: 0)
        result = 31 * result + (socialConnectionsData?.hashCode() ?: 0)
        result = 31 * result + (viewportsData?.hashCode() ?: 0)
        result = 31 * result + (vistaDiscussionData?.hashCode() ?: 0)
        return result
    }

    data class ErrorData(
        var message: String? = null,
        var details: String? = null,
        var code: String? = null
    )

}