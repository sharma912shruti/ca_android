package edu.capella.mobile.android.bean

/**
 * LoginBean.kt : Bean / Pojo class to store response of JSON received from API.
 *
 * Created by Jayesh Lahare on 03/02/20.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 03/02/20.
 *
 */

data class LoginBean(
    var alertsData: Any? = null,
    var areaPeopleData: Any? = null,
    var authData: AuthData? = null,
    var classmatesData: Any? = null,
    var contactsData: Any? = null,
    var courseroomData: Any? = null,
    var dashboardData: Any? = null,
    var degreeCompletionPlanData: Any? = null,
    var discussionData: Any? = null,
    var errorData: ErrorData? = null,
    var feedname: Any? = null,
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


) {
    override fun toString(): String {
        return "LoginBean(alertsData=$alertsData, areaPeopleData=$areaPeopleData, authData=$authData, classmatesData=$classmatesData, contactsData=$contactsData, courseroomData=$courseroomData, dashboardData=$dashboardData, degreeCompletionPlanData=$degreeCompletionPlanData, discussionData=$discussionData, errorData=$errorData, feedname=$feedname, flexpathAssessmentsAndStatusResponse=$flexpathAssessmentsAndStatusResponse, gmailUnreadCountData=$gmailUnreadCountData, homeData=$homeData, isAuthenticated=$isAuthenticated, logoutSuccessful=$logoutSuccessful, mobileFeedServiceResponse=$mobileFeedServiceResponse, newCourseroomData=$newCourseroomData, newDiscussionData=$newDiscussionData, newsData=$newsData, profileData=$profileData, profileLearnerData=$profileLearnerData, registerData=$registerData, socialConnectionsData=$socialConnectionsData, viewportsData=$viewportsData, vistaDiscussionData=$vistaDiscussionData)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoginBean

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


    data class AuthData(
        var emanxh: String? = null,
        var employeeId: EmployeeId? = null,
        var hxdowssap: String? = null,
        var token: String? = null,
        var username: String? = null
    )

    data class EmployeeId(
        var value: String? = null
    )

    data class ErrorData(
        var message: String? = null,
        var details: String? = null,
        var code: String? = null
    )

}