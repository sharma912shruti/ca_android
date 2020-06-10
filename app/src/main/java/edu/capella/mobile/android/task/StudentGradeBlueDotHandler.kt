package edu.capella.mobile.android.task

import android.content.Context

import com.google.gson.Gson
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.bean.GradeFacultyBean
import edu.capella.mobile.android.bean.GradeStudentBean
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
class StudentGradeBlueDotHandler
{
    var context : Context? = null
    var course_Id: String = ""
    var studentAssignmentStatusRecord: GradeFacultyBean? = null
    var isAssignmentBlueDot : Boolean = false
    var isOtherBlueDot: Boolean = false
    var gradeBlueDotListener: GradeBlueDotListener? = null

    constructor(ctx : Context, cid: String , _gradeBlueDotListener : GradeBlueDotListener?)
    {
        context = ctx
        course_Id = cid
        gradeBlueDotListener = _gradeBlueDotListener
    }
    fun start()
    {
        callStudentAssignmentsApi()
    }
    private fun callStudentAssignmentsApi()
    {
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val authHeader = NetworkHandler.getAuthorizationHeader()
        val hubbleHeaders = NetworkHandler.getHeadersForHubbleRequest(loginBean?.authData?.token,
            HubbleNetworkConstants.getAcceptHeaderValue(HubbleNetworkConstants.FACULTY_COURSE_ASSIGNMENTS))

        val finalHeaders = NetworkHandler.meargeHeaders(authHeader, hubbleHeaders)

        val params = HashMap<String, Any>()



        var assignmentsUrl = HubbleNetworkConstants.getUrl(
                HubbleNetworkConstants.FACULTY_COURSE_ASSIGNMENTS, "{{employeeId}}",
                "" + loginBean?.authData?.employeeId!!.value
        )

        assignmentsUrl = HubbleNetworkConstants.getUrl(assignmentsUrl, "{{courseId}}", "" + course_Id)

        Util.trace("Student Assignments URL  :$assignmentsUrl")


        val networkHandler = NetworkHandler(
                context!!,
                assignmentsUrl,
                params,
                NetworkHandler.METHOD_GET,
                studentAssignmentListNetworkListener,
                finalHeaders
        )
        networkHandler.setSilentMode(true)
        networkHandler.execute()
    }


    private val studentAssignmentListNetworkListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: android.util.Pair<String, Any>) {
            Util.trace("Student Assignment First " + response.first.toString())
            try {
                if (response.first == NetworkConstants.SUCCESS) {
                    val gson = Gson()
                    studentAssignmentStatusRecord = gson.fromJson<GradeFacultyBean>(
                        response.second.toString(),
                        GradeFacultyBean::class.java
                    )



                }

            } catch (t: Throwable) {

            }
            callStudentGradeApi()
        }
    }

    private fun callStudentGradeApi()
    {

        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val authHeader = NetworkHandler.getAuthorizationHeader()
        val hubbleHeaders = NetworkHandler.getHeadersForHubbleRequest(loginBean?.authData?.token,
            HubbleNetworkConstants.getAcceptHeaderValue(HubbleNetworkConstants.STUDENT_COURSE_GRADE))

        val finalHeaders = NetworkHandler.meargeHeaders(authHeader, hubbleHeaders)

        val params = HashMap<String, Any>()

        var assignmentsUrl = HubbleNetworkConstants.getUrl(
            HubbleNetworkConstants.STUDENT_COURSE_GRADE, "{{employeeId}}",
            "" + loginBean?.authData?.employeeId!!.value
        )

        assignmentsUrl =
            HubbleNetworkConstants.getUrl(assignmentsUrl, "{{courseId}}", "" + course_Id)

        Util.trace("Student Grade URL  :$assignmentsUrl")


        val networkHandler = NetworkHandler(
            context!!,
            assignmentsUrl,
            params,
            NetworkHandler.METHOD_GET,
            studentNetworkListener,
            finalHeaders
        )
        networkHandler.setSilentMode(true)
        networkHandler.execute()

    }

    private val studentNetworkListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: android.util.Pair<String, Any>)
        {
            Util.trace("Student first" + response.first.toString())
            try {

                if(response.first == NetworkConstants.SUCCESS)
                {
                    val gson = Gson()
                    val gradeStudentBean = gson.fromJson<GradeStudentBean>(response.second.toString(), GradeStudentBean::class.java)
                    setGradedItems(gradeStudentBean)
                }else
                {
                    gradeBlueDotListener?.onGradeBlueDotFail(response.second.toString())
                }
            }catch (t: Throwable){
                gradeBlueDotListener?.onGradeBlueDotFail(t.toString())
            }
        }
    }


    private fun setGradedItems(gradeBean: GradeStudentBean?)
    {

        if(studentAssignmentStatusRecord?.courseAssignment!=null && studentAssignmentStatusRecord?.courseAssignment!!.isNotEmpty())
        {
            isAssignmentBlueDot = isAnyAssignmentGraded(studentAssignmentStatusRecord)
        }

        if(isOtherItemsAvailableWithGrade(gradeBean))
        {
             isOtherBlueDot = isNotificationForOtherItems(gradeBean)
        }
        gradeBlueDotListener?.onGradeBlueDot(isAssignmentBlueDot || isOtherBlueDot)


    }



    private fun isAnyAssignmentGraded(greadeBean: GradeFacultyBean?): Boolean
    {
        try
        {
            for( item in greadeBean?.courseAssignment!!)
            {
                if(item?.status != null && item?.status!!.contains("GRADED" , true))
                {
                    var gradedDate = item?.gradedDateTime
                    var submitDate = item?.submittedDateTime
                    var statusDate = item?.statusDateTime
                    try {
                        for (attemps in item?.attempts?.attempt!!) {
                            if (attemps?.submittedDate!!.contains(item?.submittedDateTime!!, true)) {
                                gradedDate = attemps?.gradedDate

                                if(attemps?.statusDateTime !=null)
                                    statusDate = attemps?.statusDateTime // NOT SURE HERE
                            }
                        }
                    }catch (t: Throwable){}

                    if(gradedDate!=null) {
                        var diff = Util.getDateAgo(gradedDate!!)
                        var isUnread =
                            GradesBlueDotUtil.isUnRead(item?.id, gradedDate, submitDate, statusDate)
                        Util.trace("Day Diff 1- : $diff   for " + gradedDate + " isUnread = $isUnread")
                        if (diff <= 3 && isUnread) {
                            return true
                        }
                    }
                }else if(item?.status != null && item?.status!!.contains("SUBMITTED" , true))
                {
                    var gradedDate = item?.gradedDateTime
                    var submitDate = item?.submittedDateTime
                    var statusDate = item?.statusDateTime

                    try {
                        for (attemps in item?.attempts?.attempt!!) {
                            if (attemps?.submittedDate!!.contains(item?.submittedDateTime!!, true)) {
                                gradedDate = attemps?.gradedDate

                                if(attemps?.statusDateTime !=null)
                                    statusDate = attemps?.statusDateTime // NOT SURE HERE
                            }
                        }
                    }catch (t: Throwable){}


                    if(submitDate!=null) {
                        var diff = Util.getDateAgo(submitDate!!)
                        var isUnread =
                            GradesBlueDotUtil.isUnRead(item?.id, gradedDate, submitDate, statusDate)
                        Util.trace("Day Diff 2 - : $diff   for " + submitDate + " isUnread = $isUnread")
                        if (diff <= 3 && isUnread) {
                            return true
                        }
                    }
                }else if(item?.status != null  && Util.isDesiredStatus(item?.status))
               //if(item?.status != null && item?.statusDateTime!=null && Util.isDesiredStatus(item?.status))
                {
                    var gradedDate = item?.gradedDateTime
                    var submitDate = item?.submittedDateTime
                    var statusDate = item?.statusDateTime

                    try {
                        for (attemps in item?.attempts?.attempt!!) {
                            if (attemps?.submittedDate!!.contains(item?.submittedDateTime!!, true)) {
                                gradedDate = attemps?.gradedDate

                                if(attemps?.statusDateTime !=null)
                                    statusDate = attemps?.statusDateTime // NOT SURE HERE
                            }
                        }
                    }catch (t: Throwable){}

                    if(statusDate!=null) {
                        var diff = Util.getDateAgo(statusDate!!)
                        var isUnread =
                            GradesBlueDotUtil.isUnRead(item?.id, gradedDate, submitDate, statusDate)
                        Util.trace("Day Diff 3 - : $diff   for " + statusDate + " isUnread = $isUnread")
                        if (diff <= 3 && isUnread) {
                            return true
                        }
                    }
                }
            }


        }catch (t: Throwable)
        {
            Util.trace("isAssignmentGrade error $t")
        }
        return false
    }

    private fun isOtherItemsAvailableWithGrade(gradeStudentBean: GradeStudentBean?): Boolean
    {
        try
        {
            try {
                if (gradeStudentBean?.courseActivityGrade != null && gradeStudentBean?.courseActivityGrade?.discussionsGrades != null) {
                    for (item in gradeStudentBean?.courseActivityGrade?.discussionsGrades!!) {
                        /*if(item?.status != null && item?.status!!.contains("GRADED" , true))  return true*/
                        if (item?.status != null && item?.status!!.contains("GRADED", true)) {
                            return true
                        }
                    }
                }
            }catch (t:Throwable){}

            try{
            if(gradeStudentBean?.courseActivityGrade!=null && gradeStudentBean?.courseActivityGrade?.quizzesGrades!=null) {
                for (item in gradeStudentBean?.courseActivityGrade?.quizzesGrades!!) {
                    /* if(item?.status != null && item?.status!!.contains("GRADED" , true))  return true*/
                    if (item?.status != null && item?.status!!.contains("GRADED", true)) {
                        return true
                    }
                }
            }
            }catch (t:Throwable){}

            try {
                if (gradeStudentBean?.courseActivityGrade != null && gradeStudentBean?.courseActivityGrade?.journalsGrades != null) {
                    for (item in gradeStudentBean?.courseActivityGrade?.journalsGrades!!) {
                        /* if(item?.status != null && item?.status!!.contains("GRADED" , true))  return true*/
                        if (item?.status != null && item?.status!!.contains("GRADED", true)) {
                            return true
                        }
                    }
                }
            }catch (t:Throwable){}

            try{
            if(gradeStudentBean?.courseActivityGrade!=null && gradeStudentBean?.courseActivityGrade?.blogsGrades!=null) {
                for (item in gradeStudentBean?.courseActivityGrade?.blogsGrades!!) {
                    /*if(item?.status != null && item?.status!!.contains("GRADED" , true))  return true*/
                    if (item?.status != null && item?.status!!.contains("GRADED", true)) {
                        return true
                    }
                }
            }
            }catch (t:Throwable){}

        }catch (t: Throwable)
        {
            Util.trace("bluedot isOtherGrade error $t")
            t.printStackTrace()
        }
        return false
    }

    private fun isNotificationForOtherItems(gradeStudentBean: GradeStudentBean?): Boolean
    {
        try
        {
            for( item in gradeStudentBean?.courseActivityGrade?.discussionsGrades!!)
            {

                if(item?.status != null && item?.status!!.contains("GRADED" , true)) {
                    var gradedDate = item?.gradedDate
                    var submitDate = item?.submittedDate
                    var statusDate = item?.statusDateTime

                    /**
                     * IN FUTURE CHECK IF ANY BUG COMES AS I M NOT ABLE TO CHECK ALL SCENERIOS DUE
                     * TO LACK OF DATA AT CAPELLA END.
                     * Check above code written for assignment using attempts
                     * -JAYESH
                     * */
                    if(gradedDate!=null) {
                        var diff = Util.getDateAgo(gradedDate!!)
                        var isUnread =
                            GradesBlueDotUtil.isUnRead(item?.id, gradedDate, submitDate, statusDate)
                        if (diff <= 3 && isUnread) {
                            return true
                        }
                    }
                }
            }
            for( item in gradeStudentBean?.courseActivityGrade?.quizzesGrades!!)
            {
                if(item?.status != null && item?.status!!.contains("GRADED" , true)) {
                    var gradedDate = item?.gradedDate
                    var submitDate = item?.submittedDate
                    var statusDate = item?.statusDateTime

                    /**
                     * IN FUTURE CHECK IF ANY BUG COMES AS I M NOT ABLE TO CHECK ALL SCENERIOS DUE
                     * TO LACK OF DATA AT CAPELLA END.
                     * Check above code written for assignment using attempts
                     * -JAYESH
                     * */

                    if(gradedDate!=null) {
                        var diff = Util.getDateAgo(gradedDate!!)
                        var isUnread =
                            GradesBlueDotUtil.isUnRead(item?.id, gradedDate, submitDate, statusDate)
                        if (diff <= 3 && isUnread) {
                            return true
                        }
                    }
                }
            }
            for( item in gradeStudentBean?.courseActivityGrade?.journalsGrades!!)
            {
                if(item?.status != null && item?.status!!.contains("GRADED" , true)) {
                    var gradedDate = item?.gradedDate
                    var submitDate = item?.submittedDate
                    var statusDate = item?.statusDateTime

                    /**
                     * IN FUTURE CHECK IF ANY BUG COMES AS I M NOT ABLE TO CHECK ALL SCENERIOS DUE
                     * TO LACK OF DATA AT CAPELLA END.
                     * Check above code written for assignment using attempts
                     * -JAYESH
                     * */
                    if(gradedDate!=null) {
                        var diff = Util.getDateAgo(gradedDate!!)
                        var isUnread =
                            GradesBlueDotUtil.isUnRead(item?.id, gradedDate, submitDate, statusDate)
                        if (diff <= 3 && isUnread) {
                            return true
                        }
                    }
                }
            }
            for( item in gradeStudentBean?.courseActivityGrade?.blogsGrades!!)
            {
                if(item?.status != null && item?.status!!.contains("GRADED" , true)) {
                    var gradedDate = item?.gradedDate
                    var submitDate = item?.submittedDate
                    var statusDate = item?.statusDateTime

                    /**
                     * IN FUTURE CHECK IF ANY BUG COMES AS I M NOT ABLE TO CHECK ALL SCENERIOS DUE
                     * TO LACK OF DATA AT CAPELLA END.
                     * Check above code written for assignment using attempts
                     * -JAYESH
                     * */

                    if(gradedDate!=null) {
                        var diff = Util.getDateAgo(gradedDate!!)
                        var isUnread =
                            GradesBlueDotUtil.isUnRead(item?.id, gradedDate, submitDate, statusDate)
                        if (diff <= 3 && isUnread) {
                            return true
                        }
                    }
                }
            }

        }catch (t: Throwable)
        {
            Util.trace("Blue dot isOther Item Grade error $t")
        }
        return false
    }



}
