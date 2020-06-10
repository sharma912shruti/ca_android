package edu.capella.mobile.android.bean


/**
 * GettingStartedBean.kt : Bean / Pojo class to store response of JSON data received from the API.
 *
 * Created by Didarul Khan on 04/02/20.
 *
 * @author Didarul.Khan
 * @version 1.0
 * @since 01/27/20.
 *
 */


data class GettingStartedBean(
    var gettingstarted: GettingStarted? = null
) {
    data class GettingStarted(
        var contentId: String? = null,
        var gettingStartedContent: List<GettingStartedContent?>? = null,
        var longCourseId: String? = null
    ) {
        data class GettingStartedContent(
            var content: String? = null,
            var contentType: String? = null,
            var forumId: String? = null,
            var title: String? = null
        )
    }
}