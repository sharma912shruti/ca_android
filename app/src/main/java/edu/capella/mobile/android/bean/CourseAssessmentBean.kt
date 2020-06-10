package edu.capella.mobile.android.bean

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  15-04-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
data class CourseAssessmentBean(
    var courseUnits: CourseUnits? = null,
    var serverName: String? = null,
    var trackingId: String? = null
) {

    data class CourseUnits(
        var courseUnit: ArrayList<CourseUnit>? = null
    )

    data class CourseUnit(
        var name: String? = null,
        var studyInstructions: StudyInstructions? = null
    )

    data class StudyInstructions(
        var studyInstruction: List<StudyInstruction>? = null
    )

    data class StudyInstruction(
        var content: String? = null,
        var title: String? = null
    )
}