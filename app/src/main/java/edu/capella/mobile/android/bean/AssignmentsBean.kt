package edu.capella.mobile.android.bean

data class AssignmentsBean(
    var courseAssignment: List<CourseAssignment?>? = null,
    var serverName: String? = null,
    var trackingId: String? = null
) {
    data class CourseAssignment(
        var id: String?= null,
        var instructions: String?= null,
        var instructorViewedAssignment: Boolean?= null,
        var learnerFullName: String?= null,
        var learnerId: String?= null,
        var link: String?= null,
        var score: Double?= null,
        var status: String?= null,
        var title: String?= null,
        var totalPossibleScore: Double?= null
    )
}