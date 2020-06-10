package edu.capella.mobile.android.bean

import java.io.Serializable

data class GradeFacultyBean(
    var courseAssignment: List<CourseAssignment?>? = null,
    var serverName: String? = null,
    var trackingId: String? = null
): Serializable
{
    data class CourseAssignment(
        var attempts: Attempts? = null,
        var id: String? = null,
        var instructions: String? = null,
        var instructorViewedAssignment: Boolean? = null,
        var learnerFullName: String? = null,
        var link: String? = null,
        var score: Double? = null,
        var status: String? = null,
        var statusDateTime: String? = null,
        var submittedDateTime: String? = null,
        var gradedDateTime: String? = null,
        var title: String? = null,
        var totalPossibleScore: Double? = null
    ) :Serializable{
        data class Attempts(
            var attempt: List<Attempt?>? = null
        ):Serializable {
            data class Attempt(
                var attemptId: String? = null,
                var attemptName: String? = null,

                var statusDateTime: String? = null,
                var gradedDate: String? = null,
                var score: String? = null,
                var status: String? = null,
                var submittedDate: String? = null,
                var instructorComments: String? = null

            ):Serializable
        }
    }
}