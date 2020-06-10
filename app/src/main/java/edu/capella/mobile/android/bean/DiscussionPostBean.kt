package edu.capella.mobile.android.bean

data class DiscussionPostBean(
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
        var groupDiscussions: Any? = null,
        var myPostData: Any? = null,
        var myPosts: Any? = null,
        var myPostsByCategory: Any? = null,
        var replies: Any? = null,
        var repliesToMe: Any? = null,
        var repliesToMeByCategory: Any? = null,
        var repliesToMeData: Any? = null,
        var requestSuccessful: Any? = null,
        var topicWithReplies: TopicWithReplies? = null,
        var topics: Any? = null,
        var unreadMessageData: Any? = null
    ) {
        data class TopicWithReplies(
            var attachmentFileSize: Any? = null,
            var attachmentLink: Any? = null,
            var authorEmployeeId: String? = null,
            var authorVistaId: Any? = null,
            var bodyFormattedText: String? = null,
            var bodyText: String? = null,
            var editDate: Long? = null,
            var forumId: String? = null,
            var hitCount: Int? = null,
            var id: String? = null,
            var isCollected: Any? = null,
            var isThread: Any? = null,
            var isThreadLocked: Boolean? = null,
            var isUnread: Boolean? = null,
            var modifiedDate: Long? = null,
            var newMessage: Any? = null,
            var parentId: String? = null,
            var postDate: Long? = null,
            var postedName: String? = null,
            var replies: Any? = null,
            var repliesToMeCount: String? = null,
            var replyCount: Any? = null,
            var responses: Responses? = null,
            var subject: String? = null,
            var unReadThreadCount: Any? = null,
            var unreadMessageCount: String? = null,
            var unreadReplyCount: Any? = null,
            var userFamilyName: String? = null,
            var userGivenName: String? = null,
            var userId: String? = null
        ) {
            data class Responses(
                var discussionMessage: List<DiscussionMessage?>? = null
            ) {
                data class DiscussionMessage(
                    var attachmentFileSize: Any? = null,
                    var attachmentLink: Any? = null,
                    var authorEmployeeId: String? = null,
                    var authorVistaId: Any? = null,
                    var bodyFormattedText: String? = null,
                    var bodyText: String? = null,
                    var editDate: Long? = null,
                    var forumId: String? = null,
                    var hitCount: Int? = null,
                    var id: String? = null,
                    var isCollected: Any? = null,
                    var isThread: Boolean? = null,
                    var isThreadLocked: Boolean? = null,
                    var isUnread: Boolean? = null,
                    var modifiedDate: Long? = null,
                    var newMessage: Any? = null,
                    var parentId: String? = null,
                    var postDate: Long? = null,
                    var postedName: String? = null,
                    var replies: Any? = null,
                    var repliesToMeCount: String? = null,
                    var replyCount: Any? = null,
                    var responses: Responses? = null,
                    var subject: String? = null,
                    var unReadThreadCount: Any? = null,
                    var unreadMessageCount: String? = null,
                    var unreadReplyCount: Any? = null,
                    var userFamilyName: String? = null,
                    var userGivenName: String? = null,
                    var userId: String? = null
                )
            }
        }
    }
}