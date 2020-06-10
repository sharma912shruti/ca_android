package edu.capella.mobile.android.bean

data class EditDraftBean(
    var alertsData: Any? = null,
    var areaPeopleData: Any? = null,
    var authData: AuthData?=null,
    var classmatesData: Any? = null,
    var contactsData: Any? = null,
    var courseroomData: Any? = null,
    var dashboardData: Any? = null,
    var degreeCompletionPlanData: Any? = null,
    var discussionData: DiscussionData?=null,
    var errorData: Any? = null,
    var feedname: String?=null,
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
        var emanxh: Any,
        var employeeId: EmployeeId,
        var hxdowssap: Any,
        var token: String,
        var username: String
    )

    data class EmployeeId(
        var varue: String
    )

    data class DiscussionData(
        var collectedPostData: Any,
        var collectedPosts: Any,
        var collectedPostsByCategory: Any,
        var courses: Any,
        var draftData: Any,
        var drafts: Any,
        var draftsByCategory: Any,
        var forums: Any,
        var myPostData: Any,
        var myPosts: Any,
        var myPostsByCategory: Any,
        var replies: Any,
        var repliesToMe: Any,
        var repliesToMeByCategory: Any,
        var repliesToMeData: Any,
        var requestSuccessful: Boolean,
        var topicWithReplies: Any,
        var topics: Any,
        var unreadMessageData: Any
    )
}