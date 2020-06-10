package edu.capella.mobile.android.task

import android.content.Context

import com.google.gson.Gson
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.bean.GradeFacultyBean
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.GradeBlueDotListener
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.HubbleNetworkConstants
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler


/**
 * Class Name.kt : class description goes here
 *
 * @author  :  jayesh.lahare
 * @version :  1.0
 * @since   :  4/24/2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
class FacultyGradeBlueDotHandler {
    var context: Context? = null
    var course_Id: String = ""


    var gradeBlueDotListener: GradeBlueDotListener? = null

    constructor(ctx: Context, cid: String, _gradeBlueDotListener: GradeBlueDotListener?) {
        context = ctx
        course_Id = cid
        gradeBlueDotListener = _gradeBlueDotListener
    }

    fun start() {
        callFacultyAssignmentApi()
    }

    private fun callFacultyAssignmentApi() {

        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val authHeader = NetworkHandler.getAuthorizationHeader()
        val hubbleHeaders = NetworkHandler.getHeadersForHubbleRequest(
            loginBean?.authData?.token,
            HubbleNetworkConstants.getAcceptHeaderValue(HubbleNetworkConstants.FACULTY_COURSE_ASSIGNMENTS)
        )

        val finalHeaders = NetworkHandler.meargeHeaders(authHeader, hubbleHeaders)

        val params = HashMap<String, Any>()


        var assignmentsUrl = HubbleNetworkConstants.getUrl(
            HubbleNetworkConstants.FACULTY_COURSE_ASSIGNMENTS, "{{employeeId}}",
            "" + loginBean?.authData?.employeeId!!.value
        )

        assignmentsUrl =
            HubbleNetworkConstants.getUrl(assignmentsUrl, "{{courseId}}", "" + course_Id)

        Util.trace("Faculty Assignments URL  :$assignmentsUrl")

        val networkHandler = NetworkHandler(
            context!!,
            assignmentsUrl,
            params,
            NetworkHandler.METHOD_GET,
            facultyNetworkListener,
            finalHeaders
        )
        networkHandler.setSilentMode(true)
        networkHandler.execute()

    }


    private val facultyNetworkListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: android.util.Pair<String, Any>) {
            Util.trace("Faculty First" + response.first.toString())
            try {


                if (response.first == NetworkConstants.SUCCESS) {
                    val gson = Gson()
                    val gradeFacultyBean = gson.fromJson<GradeFacultyBean>(
                        response.second.toString(),
                        GradeFacultyBean::class.java
                    )

                    if (gradeFacultyBean.courseAssignment != null && gradeFacultyBean.courseAssignment?.isNotEmpty()!!)
                    {
                        var isNewUnRead = false
                        for (assignment in gradeFacultyBean.courseAssignment!!)
                        {
                            if (assignment?.status!!.contains("has been submitted", true))
                            {

                                if (assignment?.submittedDateTime != null)
                                {

                                    var diff = Util.getDateAgo(assignment?.submittedDateTime!!)
                                    var isUnread = GradesBlueDotUtil.isUnRead(
                                        assignment?.id,
                                        null,
                                        assignment?.submittedDateTime,
                                        null
                                    )
                                    if (diff <= 3 && isUnread)
                                    {
                                        isNewUnRead = true
                                    }
                                }
                            }
                        }
                        Util.trace("Faculty Blue Dot : " + isNewUnRead)

                        gradeBlueDotListener?.onGradeBlueDot(isNewUnRead)

                    } else {
                        gradeBlueDotListener?.onGradeBlueDotFail("")
                    }

                } else {
                    gradeBlueDotListener?.onGradeBlueDotFail(response.second.toString())
                }

            } catch (t: Throwable) {
                gradeBlueDotListener?.onGradeBlueDotFail(t.toString())
            }
        }
    }

}
