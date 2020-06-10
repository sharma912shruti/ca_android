package edu.capella.mobile.android.bean

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  09-04-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
class PccpDetailBean(
    var assignment: Assignment? = null,
    var serverName: String? = null,
    var trackingId: String? = null,
    var type: String? = null
) {

    data class Assignment(
        var IsActivityCompleted: Boolean? = null,
        var assignments: ArrayList<AssignmentX>? = null,
        var courseEndDate: String? = null,
        var courseId: String? = null,
        var courseStartDate: String? = null,
        var employeeId: String? = null,
        var finalGrade: String? = null,
        var isMaximumAttemptsExhausted: Boolean? = null,
        var learnerProgram: String? = null,
        var longCourseId: String? = null,
        var numberOfWeeksCompleted: String? = null,
        var pccpId: String? = null,
        var targetDateEmpty: Boolean? = null,
        var userEmailId: String? = null,
        var userFirstName: String? = null,
        var userId: String? = null,
        var userLastName: String? = null
    )

    data class AssignmentX(
        var Id: String? = null,
        var assignmentName: String? = null,
        var targetDate: String? = null,
        var targetNewDate: String? = null
    )
}