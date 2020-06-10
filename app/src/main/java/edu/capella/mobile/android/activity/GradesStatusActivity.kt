package edu.capella.mobile.android.activity

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import com.google.gson.Gson
import edu.capella.mobile.android.R
import edu.capella.mobile.android.adapters.GradeAssignmentListAdapter
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.bean.GradeFacultyBean
import edu.capella.mobile.android.bean.GradeStudentBean
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.HubbleNetworkConstants
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler

import kotlinx.android.synthetic.main.activity_grades_status.*
import kotlinx.android.synthetic.main.row_simple_text.view.*
import kotlinx.android.synthetic.main.toolbar_generic.*
import kotlinx.android.synthetic.main.toolbar_generic.view.*
import kotlin.collections.HashMap


/**
 * ProfileActivity.kt : Screen responsible for showing Profile and ID Card tabs detail over screen.
 *
 * Created by Jayesh Lahare on 03/02/20.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 03/02/20.
 *
 */
class GradesStatusActivity : MenuActivity() /*BaseActivity()*/ ,  ConnectivityReceiver.ConnectivityReceiverListener {

    var isStudent:Boolean? = false
    var course_Id:String = ""
    var course_pk = "";

    var studentAssignmentStatusRecord : GradeFacultyBean? = null
    var gradeStudentBean : GradeStudentBean? = null

    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    private  var isInternetConnection: Boolean=false

    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null


    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_grades_status)
        setContentChildView(R.layout.activity_grades_status , true)

        toolbarGeneric.genericTitleTxt.text = getString(R.string.grade_and_status)

        backButtonLayout.contentDescription =   getString(R.string.ada_back_button) + getString(R.string.back)



    }
    private fun initUi()
    {
        isStudent = intent.getBooleanExtra(Constants.IS_STUDENT,false)
        course_Id = intent.getStringExtra(Constants.COURSE_ID)

        Preferences.addValue(PreferenceKeys.SELECTED_COURSE_IDENTIFIER , course_Id)

        try {
            course_pk = intent.getStringExtra(Constants.COURSE_PK)
        }catch (t:Throwable){}


        if(isStudent == true)
        {
            OmnitureTrack.trackState("course:grades-and-status:student-view")
            callStudentAssignmentsApi()
            studentView.setColorSchemeColors( getColor(R.color.checkBoxColor))
            studentView.setOnRefreshListener {
                callStudentAssignmentsApi()
                studentView.isRefreshing = false

            }
        }else
        {
            OmnitureTrack.trackState("course:grades-and-status-faculty-view")
            callFacultyAssignmentApi()

            facultyView.setColorSchemeColors( getColor(R.color.checkBoxColor))

            facultyView.setOnRefreshListener {
                callFacultyAssignmentApi()
                facultyView.isRefreshing = false

            }

        }
    }

    private fun callFacultyAssignmentApi()
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

       assignmentsUrl =
            HubbleNetworkConstants.getUrl(assignmentsUrl, "{{courseId}}", "" + course_Id)

        Util.trace("Faculty Assignments URL  :$assignmentsUrl")


        val networkHandler = NetworkHandler(
            this,
            assignmentsUrl,
            params,
            NetworkHandler.METHOD_GET,
            facultyNetworkListener,
            finalHeaders
        )

        networkHandler.execute()

    }

    /** This api for list of assignment who are submitted or graded self submitted assignment list */
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

        assignmentsUrl =
            HubbleNetworkConstants.getUrl(assignmentsUrl, "{{courseId}}", "" + course_Id)

        Util.trace("Student Assignments URL  :$assignmentsUrl")


        val networkHandler = NetworkHandler(
            this,
            assignmentsUrl,
            params,
            NetworkHandler.METHOD_GET,
            studentAssignmentListNetworkListener,
            finalHeaders
        )

        networkHandler.execute()

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
            this,
            assignmentsUrl,
            params,
            NetworkHandler.METHOD_GET,
            studentNetworkListener,
            finalHeaders
        )

        networkHandler.execute()

    }

    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun init()
    {
        ConnectivityReceiver.connectivityReceiverListener = this@GradesStatusActivity
        connectivityReceiver = ConnectivityReceiver(networkLayout)
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(connectivityReceiver, intentFilter)
    }

    /**
     *  Factory method of activity, executes when application comes in resume state from pause state.
     *
     */
    override fun onResume() {
        super.onResume()
        init()
        initUi()

    }

    /**
     * Factory method of activity, executes when activity goes in paused state.
     *
     */
    override fun onPause() {
        super.onPause()
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver)
            ConnectivityReceiver.connectivityReceiverListener=null
        }
    }

    /**
     * Method triggered by broadcast receiver which is listening for Network state.
     *
     * @param isConnected : Tells whether internet is available or not
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        isInternetConnection=isConnected

       if(isConnected == true && isNetworkFailedDueToConnectivity!=null && isNetworkFailedDueToConnectivity==true)
        {
            isNetworkFailedDueToConnectivity = null
            if(this.getCurrentNetworkQueueSize()==0) {
                DialogUtils.releaseGenericDialog()
                if(isStudent == true)
                {
                    callStudentAssignmentsApi()
                }
                else
                {
                    callFacultyAssignmentApi()
                }
            }else
            {
                Util.trace("Can not reload")
            }

        }else
        {
            //NETWORK GONE
            isNetworkFailedDueToConnectivity = true
        }
    }



    private val studentAssignmentListNetworkListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: android.util.Pair<String, Any>)
        {
            Util.trace("Student Assignment " + response.second.toString())
            try {


                if(response.first == NetworkConstants.SUCCESS)
                {
                    val gson = Gson()
                    studentAssignmentStatusRecord = gson.fromJson<GradeFacultyBean>(response.second.toString(), GradeFacultyBean::class.java)
                }else
                {
                }

            }catch (t: Throwable){

            }
            callStudentGradeApi()

        }
    }


    private val facultyNetworkListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: android.util.Pair<String, Any>)
        {
            Util.trace("Faculty " + response.second.toString())
            try {
                studentView.visibility = View.GONE
                facultyView.visibility = View.VISIBLE

                if(response.first == NetworkConstants.SUCCESS)
                {
                    val gson = Gson()
                    val gradeFacultyBean = gson.fromJson<GradeFacultyBean>(response.second.toString(), GradeFacultyBean::class.java)

                    if(gradeFacultyBean.courseAssignment!=null && gradeFacultyBean.courseAssignment?.isNotEmpty()!!)
                    {
                        noAssignmentsStatusesTxt.visibility = View.GONE

                        facultyContainer.removeAllViews()
                        for( (index,assignment) in gradeFacultyBean.courseAssignment!!.withIndex())
                        {
                            if(assignment?.status!!.contains("has been submitted", true))
                            {
                                var text = Html.fromHtml(
                                    Util.str(assignment?.title),
                                    Html.FROM_HTML_MODE_LEGACY
                                ).toString().trim()
                                text = text + " from " + assignment?.learnerFullName + " has been submitted on " + Util.getDateMMDDYYYY(
                                    assignment?.submittedDateTime.toString()
                                ) + "."

                                val inflater = LayoutInflater.from(this@GradesStatusActivity)
                                val simpleView = inflater.inflate(R.layout.row_simple_text, null)
                                simpleView.title.text = text
                                if(assignment?.submittedDateTime!=null) {


                                    var diff = Util.getDateAgo(assignment?.submittedDateTime!!)
                                    var isUnread = GradesBlueDotUtil.isUnRead(
                                        assignment?.id,
                                        null,
                                        assignment?.submittedDateTime,
                                        null
                                    )
                                    if (diff <= 3 && isUnread) {
                                        simpleView.notificationLayout.visibility = View.VISIBLE
                                    }else
                                    {
                                        simpleView.notificationLayout.visibility = View.GONE
                                    }
                                }

                                Util.trace("Marking $index id = " + assignment?.id)

                                GradesBlueDotUtil.markAsReadFaculty(assignment?.id , null , assignment?.submittedDateTime , null)

                                facultyContainer.addView(simpleView)

                            }else
                            {
                                Util.trace("Ignoring for " + assignment?.status)
                            }
                        }

                    }else
                    {
                        noAssignmentsStatusesTxt.visibility = View.VISIBLE
                    }

                }else
                {
                    DialogUtils.showGenericErrorDialog(this@GradesStatusActivity)
                }

            }catch (t: Throwable){
                DialogUtils.showGenericErrorDialog(this@GradesStatusActivity)
            }
        }
    }

  // FOR ONLY CURRENT GRADE AND SCORING
    private val studentNetworkListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: android.util.Pair<String, Any>)
        {
          //  Util.trace("Student " + response.second.toString())
            try {
                studentView.visibility = View.VISIBLE
                facultyView.visibility = View.GONE

                if(response.first == NetworkConstants.SUCCESS)
                {
                    val gson = Gson()
                    val gradeStudentBean = gson.fromJson<GradeStudentBean>(response.second.toString(), GradeStudentBean::class.java)
                    this@GradesStatusActivity.gradeStudentBean = gradeStudentBean
                    if(gradeStudentBean.courseActivityGrade?.currentGrade == null)
                    {
                        noCurrentCourseGradeTxt.visibility  = View.VISIBLE
                    }else
                    {
                        noCurrentCourseGradeTxt.visibility  = View.GONE

                        setCurrentGraddingScreen(gradeStudentBean)

                    }

                    //setGradedItems(gradeStudentBean)
                    setGradedItems(studentAssignmentStatusRecord)
                    Util.trace("Student Grade" + gradeStudentBean.courseActivityGrade?.currentGrade)

                }else
                {
                    DialogUtils.showGenericErrorDialog(this@GradesStatusActivity)
                }

            }catch (t: Throwable){
                DialogUtils.showGenericErrorDialog(this@GradesStatusActivity)
            }
        }
    }


    private fun setCurrentGraddingScreen(gradeStudentBean: GradeStudentBean?)
    {
        currentGradeBlockLayout.visibility = View.VISIBLE
        currentGradePercentTxt.text = Util.getTwoDigitNumber(gradeStudentBean?.courseActivityGrade?.currentGrade.toString()) + "%"

        val lastUpdatedDate:String = getLastUpdatedGradedDate(gradeStudentBean)

        if(lastUpdatedDate != "") {
            gradeUpdateDateTxt.text =
                Util.formatDateTimeNew(lastUpdatedDate, false, true)
        }



    }

    private fun isAssignmentItemsAvailable(courseAssignment: List<GradeFacultyBean.CourseAssignment?>?): Boolean
    {
        if(courseAssignment == null || courseAssignment.size ==0)
        {
            return false
        }

        for(item in  courseAssignment!!) {
            var row = GradeAssignmentListAdapter.GradeCollector()
            row.id = item?.id
            row.forumWebLink = null // item?.forumWebLink

            row.instructions = item?.instructions
            row.link = item?.link
            row.status = Util.replaceFullStop(item?.status!!)
            row.title = item?.title
            row.score = item?.score.toString()
            row.gradedDate = item?.gradedDateTime
            row.statusDate = item?.statusDateTime
            row.submittedDate = item?.submittedDateTime
            row.totalPossibleScore = item?.totalPossibleScore.toString()
            row.webLink = null

            try {
                for (attemps in item?.attempts?.attempt!!) {
                    if (attemps?.submittedDate!!.contains(item?.submittedDateTime!!, true)) {
                        row.gradedDate = attemps?.gradedDate

                        if (attemps?.statusDateTime != null) row.statusDate = attemps?.statusDateTime // NOT SURE HERE
                    }
                }
            } catch (t: Throwable) {
            }




            if (Util.isDesiredStatus(row.status) && Util.isDesiredStatusWithDate(row.status, row)) {
                return true
            }
        }

            return false

    }

    private fun setGradedItems(gradeBean: GradeFacultyBean?)
    {

        // if(gradeStudentBean?.courseActivityGrade?.assignmentGrades !=null && gradeStudentBean?.courseActivityGrade?.assignmentGrades!!.size > 0)
       // if(studentAssignmentStatusRecord?.courseAssignment!=null && studentAssignmentStatusRecord?.courseAssignment!!.isNotEmpty())
        if(isAssignmentItemsAvailable(studentAssignmentStatusRecord?.courseAssignment))
        {
            noAssignmentGradeLayout.visibility = View.GONE
            assignmentGradesPopperLayout.visibility = View.VISIBLE

            if(isAnyAssignmentGraded(gradeBean))
            {
                assignmentGradesNotificationLayout.visibility = View.VISIBLE
            }else
            {
                assignmentGradesNotificationLayout.visibility = View.GONE
            }

            assignmentGradesPopperLayout.setOnClickListener{


                OmnitureTrack.trackAction("course:grades-and-status:assignments")

                val gradAssIntent = Intent(this@GradesStatusActivity, GradeAssignmentsListActivity::class.java)
                gradAssIntent.putExtra(Constants.BACK_TITLE, getString(R.string.grade_and_status))
               // gradAssIntent.putExtra(Constants.GRADE_ASSIGNMENT, gradeStudentBean)
                gradAssIntent.putExtra(Constants.STUDENT_GRADE_RECORD, studentAssignmentStatusRecord)
                gradAssIntent.putExtra(Constants.COURSE_PK,course_pk)
                startActivity(gradAssIntent)
            }

        }else
        {
            noAssignmentGradeLayout.visibility = View.VISIBLE
            assignmentGradesPopperLayout.visibility = View.GONE
        }

        /*var discussionGradeSize = listSize(gradeStudentBean?.courseActivityGrade?.discussionsGrades)
        var blogsGradesSize = listSize(gradeStudentBean?.courseActivityGrade?.blogsGrades)
        var journalsGradesSize = listSize(gradeStudentBean?.courseActivityGrade?.journalsGrades)
        var quizzesGradesSize = listSize(gradeStudentBean?.courseActivityGrade?.quizzesGrades)
*/
        if(isOtherItemsAvailableWithGrade(gradeStudentBean))
        {
            otherGradesPopperLayout.visibility = View.VISIBLE
            noOtherGradeLayout.visibility  = View.GONE

            if(isNotificationForOtherItems(gradeStudentBean))
            {
                otherGradesNotificationLayout.visibility = View.VISIBLE
            }else
            {
                otherGradesNotificationLayout.visibility = View.GONE
            }

            otherGradesPopperLayout.setOnClickListener{
                OmnitureTrack.trackAction("course:grades-and-status:other-grades")

                val gradAssIntent = Intent(this@GradesStatusActivity, GradeAssignmentsOtherListActivity::class.java)

                //gradAssIntent.putExtra(Constants.BACK_TITLE, "Grades...")
                gradAssIntent.putExtra(Constants.BACK_TITLE, getString(R.string.grade_and_status))
                gradAssIntent.putExtra(Constants.GRADE_ASSIGNMENT, gradeStudentBean)
                gradAssIntent.putExtra(Constants.STUDENT_GRADE_RECORD, studentAssignmentStatusRecord)
                gradAssIntent.putExtra(Constants.COURSE_PK,course_pk)
                startActivity(gradAssIntent)
            }

        }else
        {
            otherGradesPopperLayout.visibility = View.GONE
            noOtherGradeLayout.visibility  = View.VISIBLE
        }

    }

   /* private fun setGradedItems(gradeStudentBean: GradeStudentBean?)
    {

           // if(gradeStudentBean?.courseActivityGrade?.assignmentGrades !=null && gradeStudentBean?.courseActivityGrade?.assignmentGrades!!.size > 0)
            if(studentAssignmentStatusRecord?.courseAssignment!=null && studentAssignmentStatusRecord?.courseAssignment!!.isNotEmpty())
            {
                noAssignmentGradeLayout.visibility = View.GONE
                assignmentGradesPopperLayout.visibility = View.VISIBLE

                if(isAssignmentGrade(gradeStudentBean))
                {
                    assignmentGradesNotificationLayout.visibility = View.VISIBLE
                }else
                {
                    assignmentGradesNotificationLayout.visibility = View.GONE
                }

                assignmentGradesPopperLayout.setOnClickListener{

                    OmnitureTrack.trackAction("GradesStatus:Assignment-grades-click")
                    val gradAssIntent = Intent(this@GradesStatusActivity, GradeAssignmentsListActivity::class.java)
                    gradAssIntent.putExtra(Constants.BACK_TITLE, "Grades..")
                    gradAssIntent.putExtra(Constants.GRADE_ASSIGNMENT, gradeStudentBean)
                    gradAssIntent.putExtra(Constants.STUDENT_GRADE_RECORD, studentAssignmentStatusRecord)
                    gradAssIntent.putExtra(Constants.COURSE_PK,course_pk)
                    startActivity(gradAssIntent)
                }

            }else
            {
                noAssignmentGradeLayout.visibility = View.VISIBLE
                assignmentGradesPopperLayout.visibility = View.GONE
            }

            *//*var discussionGradeSize = listSize(gradeStudentBean?.courseActivityGrade?.discussionsGrades)
            var blogsGradesSize = listSize(gradeStudentBean?.courseActivityGrade?.blogsGrades)
            var journalsGradesSize = listSize(gradeStudentBean?.courseActivityGrade?.journalsGrades)
            var quizzesGradesSize = listSize(gradeStudentBean?.courseActivityGrade?.quizzesGrades)
*//*
           if(isAnyOtherItemHaveGrades(gradeStudentBean))
           {
               otherGradesPopperLayout.visibility = View.VISIBLE
               noOtherGradeLayout.visibility  = View.GONE

               if(isOtherGrade(gradeStudentBean))
               {
                   otherGradesNotificationLayout.visibility = View.VISIBLE
               }else
               {
                   otherGradesNotificationLayout.visibility = View.GONE
               }

               otherGradesPopperLayout.setOnClickListener{

                   OmnitureTrack.trackAction("GradesStatus:Other-grades-click")
                   val gradAssIntent = Intent(this@GradesStatusActivity, GradeAssignmentsOtherListActivity::class.java)

                   gradAssIntent.putExtra(Constants.BACK_TITLE, "Grades..")
                   gradAssIntent.putExtra(Constants.GRADE_ASSIGNMENT, gradeStudentBean)
                   gradAssIntent.putExtra(Constants.STUDENT_GRADE_RECORD, studentAssignmentStatusRecord)
                   gradAssIntent.putExtra(Constants.COURSE_PK,course_pk)
                   startActivity(gradAssIntent)
               }

           }else
           {
               otherGradesPopperLayout.visibility = View.GONE
               noOtherGradeLayout.visibility  = View.VISIBLE
           }

    }*/
    private fun listSize(list : List<Any?>?): Int
    {
        if(list == null){ return 0}

        return list.size
    }


    private fun isOtherItemsAvailableWithGrade(gradeStudentBean: GradeStudentBean?): Boolean
    {
        try
        {

            if(gradeStudentBean?.courseActivityGrade == null)
            {
                return false
            }

            if(gradeStudentBean?.courseActivityGrade?.discussionsGrades != null) {
                for (item in gradeStudentBean?.courseActivityGrade?.discussionsGrades!!) {
                    /*if(item?.status != null && item?.status!!.contains("GRADED" , true))  return true*/
                    if (item?.status != null && item?.status!!.contains("GRADED", true)) {
                        return true
                    }
                }
            }

            if(gradeStudentBean?.courseActivityGrade?.quizzesGrades != null) {
                for (item in gradeStudentBean?.courseActivityGrade?.quizzesGrades!!) {
                    /* if(item?.status != null && item?.status!!.contains("GRADED" , true))  return true*/
                    if (item?.status != null && item?.status!!.contains("GRADED", true)) {
                        return true
                    }
                }
            }

            if(gradeStudentBean?.courseActivityGrade?.journalsGrades != null) {
                for (item in gradeStudentBean?.courseActivityGrade?.journalsGrades!!) {
                    /* if(item?.status != null && item?.status!!.contains("GRADED" , true))  return true*/
                    if (item?.status != null && item?.status!!.contains("GRADED", true)) {
                        return true
                    }
                }
            }

            if(gradeStudentBean?.courseActivityGrade?.blogsGrades != null) {
                for (item in gradeStudentBean?.courseActivityGrade?.blogsGrades!!) {
                    /*if(item?.status != null && item?.status!!.contains("GRADED" , true))  return true*/

                    if (item?.status != null && item?.status!!.contains("GRADED", true)) {
                        return true
                    }
                }
            }

        }catch (t: Throwable)
        {
            Util.trace("2 isOtherGrade error $t")
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
            Util.trace("isOther Item Grade error $t")
        }
        return false
    }

    /*private fun isAssignmentGrade(gradeStudentBean: GradeStudentBean?): Boolean
    {
        try
        {
                for( item in gradeStudentBean?.courseActivityGrade?.assignmentGrades!!)
                {
                    if(item?.status != null && item?.status!!.contains("GRADED" , true))
                    {
                            var diff  = Util.getDateAgo(item?.gradedDate!!)
                            Util.trace("Day Diff : $diff   for " + item?.gradedDate!!)

                        *//*try {
                            for (attemps in item?.attempts?.attempt!!) {
                                if (attemps?.submittedDate!!.contains(item?.submittedDateTime!!, true)) {
                                    row.gradedDate = attemps?.gradedDate

                                    if(attemps?.statusDateTime !=null)
                                        row.statusDate = attemps?.statusDateTime // NOT SURE HERE
                                }
                            }
                        }catch (t: Throwable){}*//*


                          if (diff <= 3 )
                          {
                              return true
                          }
                    }
                }


        }catch (t: Throwable)
        {
                Util.trace("isAssignmentGrade error $t")
        }
        return false
    }*/


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
                                    statusDate = attemps?.statusDateTime
                            }
                        }
                    }catch (t: Throwable){}

                    if(gradedDate!=null) {
                        var diff = Util.getDateAgo(gradedDate!!)
                        var isUnread =
                            GradesBlueDotUtil.isUnRead(item?.id, gradedDate, submitDate, statusDate)
                        Util.trace("Day Diff 1  : $diff   for " + gradedDate + " isUnread = $isUnread")
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

                    if(submitDate!= null) {

                        var diff = Util.getDateAgo(submitDate!!)
                        var isUnread =
                            GradesBlueDotUtil.isUnRead(item?.id, gradedDate, submitDate, statusDate)
                        Util.trace("Day Diff 2 - : $diff   for " + submitDate + " isUnread = $isUnread")
                        if (diff <= 3 && isUnread) {
                            return true
                        }
                    }
                //}else if(item?.status != null && item?.statusDateTime!=null && Util.isDesiredStatus(item?.status))
                }else if(item?.status != null  && Util.isDesiredStatus(item?.status))
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



    private fun getLastUpdatedGradedDate(gradeStudentBean: GradeStudentBean?): String
    {
        try
        {
            var lastUpdatedHrs = Long.MAX_VALUE
            var lastUpdatedDate = ""

            if(gradeStudentBean?.courseActivityGrade != null && gradeStudentBean?.courseActivityGrade?.assignmentGrades !=null) {
                for (item in gradeStudentBean?.courseActivityGrade?.assignmentGrades!!) {

                    if (item?.status != null && item?.status!!.contains("GRADED", true))
                    {
                        if(item?.gradedDate!=null) {
                            var diff = Util.getDateAgoMins(item?.gradedDate!!)
                            Util.trace("Last Diff $diff")
                            if (diff < lastUpdatedHrs) {

                                lastUpdatedHrs = diff
                                lastUpdatedDate = item?.gradedDate!!
                            }
                        }
                    }
                }
            }

            if(gradeStudentBean?.courseActivityGrade != null && gradeStudentBean?.courseActivityGrade?.discussionsGrades !=null) {
                for (item in gradeStudentBean?.courseActivityGrade?.discussionsGrades!!) {

                    if (item?.status != null && item?.status!!.contains("GRADED", true)) {

                        if(item?.gradedDate!=null) {
                            var diff = Util.getDateAgoMins(item?.gradedDate!!)
                            Util.trace("Last Diff $diff")
                            if (diff < lastUpdatedHrs) {

                                lastUpdatedHrs = diff
                                lastUpdatedDate = item?.gradedDate!!
                            }
                        }
                    }
                }
            }

            if(gradeStudentBean?.courseActivityGrade != null && gradeStudentBean?.courseActivityGrade?.quizzesGrades !=null) {

                for (item in gradeStudentBean?.courseActivityGrade?.quizzesGrades!!) {

                    if (item?.status != null && item?.status!!.contains("GRADED", true)) {

                        if(item?.gradedDate!=null) {
                            var diff = Util.getDateAgoMins(item?.gradedDate!!)
                            Util.trace("Last Diff $diff")
                            if (diff < lastUpdatedHrs) {

                                lastUpdatedHrs = diff
                                lastUpdatedDate = item?.gradedDate!!
                            }
                        }
                    }
                }
            }

            if(gradeStudentBean?.courseActivityGrade != null && gradeStudentBean?.courseActivityGrade?.journalsGrades !=null) {
                for (item in gradeStudentBean?.courseActivityGrade?.journalsGrades!!) {

                    if (item?.status != null && item?.status!!.contains("GRADED", true)) {

                        if(item?.gradedDate!=null) {
                            var diff = Util.getDateAgoMins(item?.gradedDate!!)
                            Util.trace("Last Diff $diff")
                            if (diff < lastUpdatedHrs) {

                                lastUpdatedHrs = diff
                                lastUpdatedDate = item?.gradedDate!!
                            }
                        }
                    }
                }
            }

            if(gradeStudentBean?.courseActivityGrade != null && gradeStudentBean?.courseActivityGrade?.blogsGrades !=null)
            {
                for (item in gradeStudentBean?.courseActivityGrade?.blogsGrades!!) {


                    if (item?.status != null && item?.status!!.contains("GRADED", true)) {

                        if(item?.gradedDate!=null) {
                            var diff = Util.getDateAgoMins(item?.gradedDate!!)
                            if (diff < lastUpdatedHrs) {
                                Util.trace("Last Diff $diff")
                                lastUpdatedHrs = diff
                                lastUpdatedDate = item?.gradedDate!!
                            }
                        }
                    }
                }
            }

            return lastUpdatedDate
        }catch (t: Throwable)
        {
            Util.trace("1 isOtherGrade error $t")
        }
        return ""
    }

}
