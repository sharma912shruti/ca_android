package edu.capella.mobile.android.bean

data class CreateThreadBean(
    var alertsData: Any? = null,
    var areaPeopleData: Any? = null,
    var authData: AuthData? = null,
    var classmatesData: Any? = null,
    var contactsData: Any? = null,
    var courseroomData: Any? = null,
    var dashboardData: Any? = null,
    var degreeCompletionPlanData: Any? = null,
    var discussionData: DiscussionData? = null,
    var errorData: Any? = null,
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
) {
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

    data class DiscussionData(
        var collectedPostData: Any? = null,
        var collectedPosts: Any? = null,
        var collectedPostsByCategory: Any? = null,
        var courses: Any? = null,
        var draftData: Any? = null,
        var drafts: Any? = null,
        var draftsByCategory: Any? = null,
        var forums: Any? = null,
        var myPostData: Any? = null,
        var myPosts: Any? = null,
        var myPostsByCategory: Any? = null,
        var replies: Any? = null,
        var repliesToMe: Any? = null,
        var repliesToMeByCategory: Any? = null,
        var repliesToMeData: Any? = null,
        var requestSuccessful: Boolean? = null,
        var topicWithReplies: Any? = null,
        var topics: Any? = null,
        var unreadMessageData: Any? = null
    )
}