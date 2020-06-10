package edu.capella.mobile.android.bean

import java.io.Serializable

/**
 * Class Name.kt : class description goes here
 *
 * @author  :  SSHARMA45
 * @version :  1.0
 * @since   :  4/27/2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */

data class GP2Bean(
    var assessmentCount: Int? = null,
    var courseBatchUid: String? = null,
    var courseId: String? = null,
    var courseName: String? = null,
    var coursePk1: String? = null,
    var courseUrl: String? = null,
    var leftNavigationItems: ArrayList<LeftNavigationItem>? = null,
    var newAnnouncements: Int? = null
):Serializable {

    data class LeftNavigationItem(
        var courseContentsPk1: Int? = null,
        var id: Int? = null,
        var isEntryPoint: Boolean? = null,
        var name: String? = null,
        var type: Int? = null,
        var url: String? = null
    ):Serializable
}
