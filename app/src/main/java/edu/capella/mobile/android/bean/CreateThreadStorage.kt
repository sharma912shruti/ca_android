package edu.capella.mobile.android.bean

data class CreateThreadStorage(
    var back_title: String? = null,
    var courseId: String? = null,
    var forumId: String? = null,
    var is_reply_screen: Boolean? = false,
    var parentMessageId: String? = null,
    var post_title: String? = null,
    var subjectLine: String? = null,
    var user_given_name: String? = null
)