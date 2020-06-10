package edu.capella.mobile.android.bean

data class DiscussionForumBean(
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
    var flexpathAssessmentsAndStatusResponse: Any? = null,
    var gmailUnreadCountData: Any? = null,
    var homeData: Any? = null,
    var isAuthenticated: Any? = null,
    var logoutSuccessful: Any? = null,
    var mobileFeedServiceResponse: Any? = null,
    var newCourseroomData: Any? = null,
    var newDiscussionData: NewDiscussionData? = null,
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

    data class NewDiscussionData(
        var collectedPostData: Any? = null,
        var collectedPosts: Any? = null,
        var collectedPostsByCategory: Any? = null,
        var courses: Any? = null,
        var discussionsCountByCourseIdResponse: Any? = null,
        var draftData: Any? = null,
        var drafts: Any? = null,
        var draftsByCategory: Any? = null,
        var forums: Any? = null,
        var groupDiscussions: List<GroupDiscussion?>? = null,
        var myPostData: Any? = null,
        var myPosts: Any? = null,
        var myPostsByCategory: Any? = null,
        var replies: Any? = null,
        var repliesToMe: Any? = null,
        var repliesToMeByCategory: Any? = null,
        var repliesToMeData: Any? = null,
        var requestSuccessful: Any? = null,
        var topicWithReplies: Any? = null,
        var topics: Any? = null,
        var unreadMessageData: Any? = null
    ) {
        data class GroupDiscussion(
            var forums: List<Forum?>? = null,
            var groupId: String? = null,
            var groupTitle: String? = null
        ) {
            data class Forum(
                var collectedPostCount: String? = null,
                var descriptionFormattedText: String? = null,
                var descriptionText: String? = null,
                var draftCount: String? = null,
                var endDate: Any? = null,
                var forumRole: String? = null,
                var id: String? = null,
                var isAvailable: Boolean? = null,
                var isAvailableWithinDateRestrictions: Boolean? = null,
                var messageCount: Any? = null,
                var myPostCount: String? = null,
                var newMessage: Any? = null,
                var position: Any? = null,
                var replies: Any? = null,
                var repliesToMeCount: Int? = null,
                var startDate: Any? = null,
                var threadCount: Any? = null,
                var title: String? = null,
                var unreadMessageCount: Int? = null,
                var unreadThreadCount: Int? = null
            )
        }
    }
}