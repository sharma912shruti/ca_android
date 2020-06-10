package edu.capella.mobile.android.bean

import java.io.Serializable

data class CourseBasicInfoBean(
    var basicCourseInfo: ArrayList<BasicCourseInfo>? = null,
    var serverName: String? = null,
    var trackingId: String? = null
):Serializable {

    data class BasicCourseInfo(
        var announcementLink: String? = null,
        var courseId: String? = null,
        var courseIdentifier: CourseIdentifier? = null,
        var courseLink: String? = null,
        var courseMessageCount: String? = null,
        var discussionLink: String? = null,
        var messageLink: String? = null,
        var messaging: Boolean? = null,
        var studyInstructions: StudyInstructions? = null,
        var userRole: String? = null
    ):Serializable

    data class CourseIdentifier(
        var catalogNumber: String? = null,
        var catalogSubject: String? = null,
        var section: String? = null,
        var sessionCode: String? = null,
        var termCode: String? = null
    ):Serializable

    data class StudyInstructions(
        var studyInstruction: ArrayList<StudyInstruction>? = null
    ):Serializable

    data class StudyInstruction(
        var content: String? = null,
        var title: String? = null
    ):Serializable
}