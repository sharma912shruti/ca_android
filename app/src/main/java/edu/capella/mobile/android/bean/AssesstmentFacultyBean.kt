package edu.capella.mobile.android.bean
import java.io.Serializable

 class AssesstmentFacultyBean(
    var courseAssignment: List<CourseAssignment>?= null,
    var serverName: String?= null,
    var trackingId: String?= null
):Serializable
{

    data class CourseAssignment(
        var attempts: Attempts?= null,
        var id: String?= null,
        var instructions: String?= null,
        var instructorViewedAssignment: Boolean?= null,
        var learnerFullName: String?= null,
        var learnerId: String?= null,
        var link: String?= null,
        var status: String?= null,
        var statusDateTime: String?= null,
        var submittedDateTime: String?= null,
        var title: String?= null
    )
    {
        private var read: String? = "Unread"

        fun setRead(value: String) {
            this.read = value
        }

        fun getRead(): String? {
            return read
        }
    }

    data class Attempt(
        var attemptId: String?= null,
        var attemptName: String?= null,
        var gradedDate: String?= null,
        var score: String?= null,
        var status: String?= null,
        var submittedDate: String?= null
    )

    data class Attempts(
        var attempt: List<Attempt>?= null
    )
}