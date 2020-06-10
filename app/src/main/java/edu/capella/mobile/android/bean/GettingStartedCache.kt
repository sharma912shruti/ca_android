package edu.capella.mobile.android.bean

data class GettingStartedCache(
    var course_ids: ArrayList<CourseId?>? = null,
    var empid: String? = null
) {
    data class CourseId(
        var course_id: String? = null,
        var visibile: Boolean? = null
    )
}