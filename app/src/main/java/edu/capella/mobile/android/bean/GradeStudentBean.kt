package edu.capella.mobile.android.bean

import java.io.Serializable

data class GradeStudentBean(
    var courseActivityGrade: CourseActivityGrade? = null,
    var serverName: String? = null,
    var trackingId: String? = null
) :Serializable{
    data class CourseActivityGrade(
        var assignmentGrades: List<AssignmentGrade?>? = null,
        var blogsGrades: List<BlogsGrade?>? = null,
        var currentGrade: String? = null,
        var discussionParticipation: String? = null,
        var discussionsGrades: List<DiscussionsGrade?>? = null,
        var journalsGrades: List<JournalsGrade?>? = null,
        var quizzesGrades: List<QuizzesGrade?>? = null
    )  :Serializable{
        data class AssignmentGrade(
            var facultyComments: String? = null,
            var gradedDate: String? = null,
            var id: String? = null,
            var instructionLink: String? = null,
            var learnerComments: String? = null,
            var score: Double? = null,
            var status: String? = null,
            var submittedDate: String? = null,
            var title: String? = null,
            var totalPossibleScore: Double? = null,
            var webLink: String? = null
        ) :Serializable

        data class BlogsGrade(
            var gradedDate: String? = null,
            var id: String? = null,
            var instructionLink: String? = null,
            var score: Double? = null,
            var status: String? = null,
            var statusDateTime: String? = null,
            var submittedDate: String? = null,
            var title: String? = null,
            var totalPossibleScore: Double? = null,
            var webLink: String? = null
        ) :Serializable

        data class DiscussionsGrade(
            var forumWebLink: String? = null,
            var statusDateTime: String? = null,
            var id: String? = null,
            var instructionLink: String? = null,
            var score: Double? = null,
            var status: String? = null,
            var gradedDate: String? = null,
            var submittedDate: String? = null,
            var title: String? = null,
            var totalPossibleScore: Double? = null
        ) :Serializable

        data class JournalsGrade(
            var id: String? = null,
            var instructionLink: String? = null,
            var score: Double? = null,
            var status: String? = null,
            var title: String? = null,
            var gradedDate: String? = null,
            var statusDateTime: String? = null,
            var submittedDate: String? = null,
            var totalPossibleScore: Double? = null,
            var webLink: String? = null
        ) :Serializable

        data class QuizzesGrade(
            var facultyComments: String? = null,
            var gradedDate: String? = null,
            var id: String? = null,
            var instructionLink: String? = null,
            var score: Double? = null,
            var status: String? = null,
            var statusDateTime: String? = null,
            var submittedDate: String? = null,
            var title: String? = null,
            var totalPossibleScore: Double? = null,
            var webLink: String? = null
        ) :Serializable
    }
}
