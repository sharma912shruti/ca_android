package edu.capella.mobile.android.bean

import java.io.Serializable

data class DiscussionDraftBean(
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
    var newDiscussionData: NewDiscussionData?=null,
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

     class Draft(
        var attachmentFileSize: Any,
        var attachmentLink: Any,
        var authorEmployeeId: String,
        var authorVistaId: Any,
        var bodyFormattedText: String,
        var bodyText: String,
        var editDate: Long,
        var forumId: String,
        var forumTitle: String,
        var groupId: Any,
        var groupTitle: Any,
        var hitCount: Int,
        var id: String,
        var isCollected: Any,
        var isThread: Any,
        var isThreadLocked: Boolean,
        var isUnread: Boolean,
        var listOfFacultyRoles: Any,
        var modifiedDate: Long,
        var newMessage: Any,
        var parentId: String,
        var parentMessageAuthorName: Any,
        var postDate: Long,
        var postedEmployeeRole: Any,
        var postedName: String,
        var replies: Any,
        var repliesToMeCount: Any,
        var replyCount: Any,
        var responses: Any,
        var subject: String,
        var unReadThreadCount: Any,
        var unreadMessageCount: Any,
        var unreadReplyCount: Any,
        var userFamilyName: String,
        var userGivenName: String,
        var userId: String
    ):Serializable


    data class EmployeeId(
        var varue: String
    )

    data class NewDiscussionData(
        var collectedPostData: Any,
        var collectedPosts: Any,
        var collectedPostsByCategory: Any,
        var courses: Any,
        var discussionsCountByCourseIdResponse: Any,
        var draftData: Any,
        var drafts: List<Draft>,
        var draftsByCategory: Any,
        var forums: Any,
        var groupDiscussions: Any,
        var myPostData: Any,
        var myPosts: Any,
        var myPostsByCategory: Any,
        var replies: Any,
        var repliesToMe: Any,
        var repliesToMeByCategory: Any,
        var repliesToMeData: Any,
        var requestSuccessful: Any,
        var topicWithReplies: Any,
        var topics: Any,
        var unreadMessageData: Any
    )
}
