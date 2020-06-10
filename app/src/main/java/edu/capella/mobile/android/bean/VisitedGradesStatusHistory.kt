package edu.capella.mobile.android.bean

data class VisitedGradesStatusHistory(
    var course_identifiers: ArrayList<CourseIdentifier?>? = null,
    var employeeId: String? = null,
    var visited_assignment_items: VisitedAssignmentItems? = null
) {
    data class CourseIdentifier(
        var course_identifier: String? = null
    )

    data class VisitedAssignmentItems(
        var assignments: ArrayList<Assignment?>? = null,
        var blogs: ArrayList<Blog?>? = null,
        var discussions: ArrayList<Discussion?>? = null,
        var journal: ArrayList<Journal?>? = null,
        var quiz: ArrayList<Quiz?>? = null
    ) {
        data class Assignment(
            var id: String? = null,
            var gradeDate: String? = null,
            var submitDate: String? = null,
            var statusDate: String? = null
        )

        data class Blog(
            var id: String? = null
        )

        data class Discussion(
            var id: String? = null
        )

        data class Journal(
            var id: String? = null
        )

        data class Quiz(
            var id: String? = null
        )
    }
}