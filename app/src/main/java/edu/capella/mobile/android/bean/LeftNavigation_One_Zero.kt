package edu.capella.mobile.android.bean

data class LeftNavigation_One_Zero(
    var assessmentCount: Int? = null,
    var courseBatchUid: String? = null,
    var courseId: String? = null,
    var courseName: String? = null,
    var coursePk1: String? = null,
    var courseUrl: String? = null,
    var leftNavigationItems: List<LeftNavigationItem?>? = null,
    var newAnnouncements: Int? = null
) {
    data class LeftNavigationItem(
        var courseContentsPk1: Int? = null,
        var id: Int? = null,
        var isEntryPoint: Boolean? = null,
        var name: String? = null,
        var type: Int? = null,
        var url: String? = null
    )
}