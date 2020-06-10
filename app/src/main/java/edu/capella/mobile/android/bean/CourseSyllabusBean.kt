package edu.capella.mobile.android.bean

/**
 * CourseSyllabusBean.kt : Bean / Pojo class to store response of JSON received from API.
 *
 * Created by Didarul Khan on 03/17/20.
 *
 * @author Didarul Khan
 * @version 1.0
 * @since 01/27/20.
 *
 */

data class CourseSyllabusBean(
    var courseSyllabus: CourseSyllabus? = null,
    var serverName: String? = null,
    var trackingId: String? = null
)
{
    data class CourseSyllabus(
        var contentId: String? = null,
        var courseSyllabusContents: CourseSyllabusContents? = null
    ) {
        data class CourseSyllabusContents(
            var courseSyllabusContent: List<CourseSyllabusContent?>? = null
        ) {
            data class CourseSyllabusContent(
                var content: String? = null,
                var title: String? = null
            )
        }
    }
}