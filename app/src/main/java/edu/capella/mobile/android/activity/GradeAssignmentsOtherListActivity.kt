package edu.capella.mobile.android.activity

import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*

import edu.capella.mobile.android.adapters.GradeAssignmentListAdapter
import edu.capella.mobile.android.adapters.GradeAssignmentOtherListAdapter
import edu.capella.mobile.android.bean.GradeFacultyBean

import edu.capella.mobile.android.bean.GradeStudentBean
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.HubbleNetworkConstants
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler

import edu.capella.mobile.android.task.StickyInfoGrabber
import kotlinx.android.synthetic.main.activity_grade_assignments.*


import kotlinx.android.synthetic.main.toolbar_generic.*
import kotlinx.android.synthetic.main.toolbar_generic.view.*
import java.util.*


/**
 * AssignmentsActivity.kt :  Screen responsible for showing the list of Assignments list
 *
 * @author  :  Jayesh.lahare
 * @version :  1.0
 * @since   :  01-27-2020
 * @created : 04-04-20
 *
 */

class GradeAssignmentsOtherListActivity: MenuActivity()/*BaseActivity()*/, ConnectivityReceiver.ConnectivityReceiverListener  {


    var assignmentsList: ArrayList<GradeAssignmentListAdapter.GradeCollector?> = ArrayList()
    var assignmentsListAdapter: GradeAssignmentOtherListAdapter? = null


    var gradeStudentBean:  GradeStudentBean? = null

    var isOtherAssignments:Boolean = true

    var course_pk = "";

    var commonWebLink: String? = null

    var studentAssignmentStatusRecord: GradeFacultyBean? = null

    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    private var isInternetConnection: Boolean = false


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
       // setContentView(R.layout.activity_grade_assignments)
        setContentChildView(R.layout.activity_grade_assignments , true)
        gradeAssignmentsToolbar.genericTitleTxt.text = getString(R.string.other_grades)

        if( intent.getStringExtra(Constants.BACK_TITLE).length>6)
        {
            gradeAssignmentsToolbar.backHeaderTxt.text = intent.getStringExtra(Constants.BACK_TITLE).toString().substring(0,6)+"..."
        }

        backButtonLayout.contentDescription =   getString(R.string.ada_back_button) +intent.getStringExtra(Constants.BACK_TITLE).toString()

        gradeStudentBean = intent.getSerializableExtra(Constants.GRADE_ASSIGNMENT) as GradeStudentBean


        if(intent.getSerializableExtra(Constants.STUDENT_GRADE_RECORD) != null) {
            studentAssignmentStatusRecord =
                intent.getSerializableExtra(Constants.STUDENT_GRADE_RECORD) as GradeFacultyBean
        }


        try {
            course_pk = intent.getStringExtra(Constants.COURSE_PK)
        }catch (t:Throwable){}
        initUi()

        OmnitureTrack.trackState("course:grades-and-status:other-grades")
    }


    /**
     * Method initializes tabs with container data and Look and fill.
     *
     */
    private fun initUi() {


        initializeAdapter()

        gradeListSwipeToRefresh.setColorSchemeColors( getColor(R.color.checkBoxColor))


        gradeListSwipeToRefresh.setOnRefreshListener {

            gradeListSwipeToRefresh.isRefreshing = false
            callStudentGradeApi()
        }

            otherGradesLabelTxt.visibility = View.VISIBLE
            loadOtherData(gradeStudentBean!!)

           //otherSeeAllGradesTxt.contentDescription = getString(R.string.see_all_grade_and_instr_feedback) + getString(R.string.ada_link_will_open_browser)



    }
    private fun openSeeAllGradeLink()
    {
        if(commonWebLink != null)
        {
            OmnitureTrack.trackState("course:grades-and-status:other-grades:see-other-grades-link")
            var blackboardDomain = commonWebLink
            blackboardDomain =blackboardDomain!!.replace("https://" , "")
            blackboardDomain =blackboardDomain!!.substring(0, blackboardDomain.indexOf("/"))

            var urlforall = "https://" + blackboardDomain + "/webapps/bb-mygrades-BBLEARN/myGrades?course_id=" + course_pk + "&stream_name=mygrades&is_stream=false"

            Util.trace("urlforall is $urlforall")
            val stickyWork  = StickyInfoGrabber(this)
            stickyWork.generateMuleSoftStickySessionForTargetUrl(urlforall , BuildConfig.STICKY_FORWARD_URL)

        }
    }

    private fun initializeAdapter() {
        gradeAssignmentsListView.layoutManager = LinearLayoutManager(this)

        assignmentsListAdapter = GradeAssignmentOtherListAdapter(this,isOtherAssignments, assignmentsList , object :
            GradeAssignmentOtherListAdapter.GradeAssignmentItemListener
        {
            override fun onItemClicked(value: GradeAssignmentListAdapter.GradeCollector?)
            {
                if(value!!.isFooter!=null && value.isFooter==true)
                {
                    openSeeAllGradeLink()
                }


            }

        })

        assignmentsListAdapter?.setStatusRecord(studentAssignmentStatusRecord)


        gradeAssignmentsListView.adapter = assignmentsListAdapter
    }

   private fun markAllRead()
   {
       try {
           if(gradeStudentBean?.courseActivityGrade?.quizzesGrades!=null)
           {
               for (item in gradeStudentBean?.courseActivityGrade?.quizzesGrades!!) {
                   GradesBlueDotUtil.markAsRead(
                       item?.id,
                       item?.gradedDate,
                       item?.submittedDate,
                       item?.statusDateTime
                   )
               }
           }

           if(gradeStudentBean?.courseActivityGrade?.discussionsGrades!=null) {
               for (item in gradeStudentBean?.courseActivityGrade?.discussionsGrades!!) {
                   GradesBlueDotUtil.markAsRead(
                       item?.id,
                       item?.gradedDate,
                       item?.submittedDate,
                       item?.statusDateTime
                   )
               }
           }


           if(gradeStudentBean?.courseActivityGrade?.journalsGrades!=null) {
               for (item in gradeStudentBean?.courseActivityGrade?.journalsGrades!!) {
                   GradesBlueDotUtil.markAsRead(
                       item?.id,
                       item?.gradedDate,
                       item?.submittedDate,
                       item?.statusDateTime
                   )
               }
           }

           if(gradeStudentBean?.courseActivityGrade?.blogsGrades!=null) {
               for (item in gradeStudentBean?.courseActivityGrade?.blogsGrades!!) {
                   GradesBlueDotUtil.markAsRead(
                       item?.id,
                       item?.gradedDate,
                       item?.submittedDate,
                       item?.statusDateTime
                   )
               }
           }
       }catch (t:Throwable){
           Util.trace("Other Marking error $t")
           t.printStackTrace()
       }
   }



    /**
     *  Factory method of activity, executes when application comes in resume state from pause state.
     *
     */
    override fun onResume() {
        super.onResume()
        initNetworkBroadcastReceiver()
    }

    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun initNetworkBroadcastReceiver() {

        ConnectivityReceiver.connectivityReceiverListener = this@GradeAssignmentsOtherListActivity
        connectivityReceiver = ConnectivityReceiver(networkLayout)
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(connectivityReceiver, intentFilter)
    }


    /**
     * Factory method of activity, executes when activity goes in paused state.
     *
     */
    override fun onPause() {
        super.onPause()
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver)
            ConnectivityReceiver.connectivityReceiverListener = null
        }
    }






    /**
     * Method triggered by broadcast receiver which is listening for Network state.
     *
     * @param isConnected : Tells whether internet is available or not
     */

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        if(isConnected == true && isNetworkFailedDueToConnectivity!=null && isNetworkFailedDueToConnectivity==true)
        {
            isNetworkFailedDueToConnectivity = null
            if(this.getCurrentNetworkQueueSize()==0) {
                DialogUtils.releaseGenericDialog()
                callStudentGradeApi()
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








    private fun loadOtherData(gradeStudentBeanData: GradeStudentBean)
    {

        try {
            assignmentsList.clear()

                try {
                    for (item in gradeStudentBeanData?.courseActivityGrade?.discussionsGrades!!) {
                        var row = GradeAssignmentListAdapter.GradeCollector()
                        row.id = item?.id
                        row.forumWebLink = item?.forumWebLink
                        row.instructionLink = item?.instructionLink
                        row.gradedDate = item?.gradedDate
                        row.status = item?.status
                        row.title = item?.title
                        row.score = item?.score.toString()
                        row.submittedDate = item?.submittedDate
                        row.totalPossibleScore = item?.totalPossibleScore.toString()
                        row.webLink = null

                        if (item?.forumWebLink != null) {
                            this.commonWebLink = item?.forumWebLink
                        }
                        if (item?.status!!.contains("GRADED", true)) {
                            assignmentsList.add(row)
                        }
                    }
                }catch (t:Throwable){}

            try
            {
                        for(item in gradeStudentBeanData?.courseActivityGrade?.blogsGrades!!)
                        {
                            var row = GradeAssignmentListAdapter.GradeCollector()
                            row.id = item?.id
                            row.forumWebLink =  null //item?.forumWebLink
                            row.instructionLink = item?.instructionLink
                            row.gradedDate = item?.gradedDate
                            row.status = item?.status
                            row.title = item?.title
                            row.score = item?.score.toString()
                            row.submittedDate = item?.submittedDate
                            row.totalPossibleScore = item?.totalPossibleScore.toString()
                            row.webLink = item?.webLink

                            if(item?.webLink != null)
                            {
                                this.commonWebLink = item?.webLink
                            }

                            if(item?.status!!.contains("GRADED" , true))
                            {
                                assignmentsList.add(row)
                            }
                        }
            }catch (t:Throwable){}


            try {
                for (item in gradeStudentBeanData?.courseActivityGrade?.journalsGrades!!) {
                    var row = GradeAssignmentListAdapter.GradeCollector()
                    row.id = item?.id
                    row.forumWebLink = null //item?.forumWebLink
                    row.instructionLink = item?.instructionLink
                    row.gradedDate = item?.gradedDate
                    row.status = item?.status
                    row.title = item?.title
                    row.score = item?.score.toString()
                    row.submittedDate = item?.submittedDate
                    row.totalPossibleScore = item?.totalPossibleScore.toString()
                    row.webLink = item?.webLink

                    if (item?.webLink != null) {
                        this.commonWebLink = item?.webLink
                    }

                    if (item?.status!!.contains("GRADED", true)) {
                        assignmentsList.add(row)
                    }
                }
            }catch (t:Throwable){}

            try {
                for (item in gradeStudentBeanData?.courseActivityGrade?.quizzesGrades!!) {
                    var row = GradeAssignmentListAdapter.GradeCollector()
                    row.id = item?.id
                    row.forumWebLink = null //item?.forumWebLink
                    row.instructionLink = item?.instructionLink
                    row.gradedDate = item?.gradedDate
                    row.status = item?.status
                    row.title = item?.title
                    row.score = item?.score.toString()
                    row.submittedDate = item?.submittedDate
                    row.totalPossibleScore = item?.totalPossibleScore.toString()
                    row.webLink = item?.webLink
                    if (item?.webLink != null) {
                        this.commonWebLink = item?.webLink
                    }
                    if (item?.status!!.contains("GRADED", true)) {
                        assignmentsList.add(row)
                    }
                }
            }catch (t:Throwable){}



            if (assignmentsList.size > 0)
            {

                gradeAssignmentsListLayout.visibility = View.VISIBLE

                try {
                    orderNew(assignmentsList)
                }catch (t:Throwable){}

                var footerRow = GradeAssignmentListAdapter.GradeCollector()
                footerRow.isFooter = true
                assignmentsList.add(footerRow)



                if (assignmentsListAdapter != null) {
                    assignmentsListAdapter!!.notifyDataSetChanged()
                    gradeAssignmentsListView.adapter = assignmentsListAdapter

                }
            }

        }catch (t: Throwable)
        {
            Util.trace("1 Other Assignments error : $t")
            t.printStackTrace()
        }

        //markAllRead()
    }

    private fun order(assgnList: List<GradeAssignmentListAdapter.GradeCollector?>) {

        Collections.sort(assgnList,object :Comparator<GradeAssignmentListAdapter.GradeCollector?>{

            override fun compare(firstItem: GradeAssignmentListAdapter.GradeCollector?,
                                 secondItem: GradeAssignmentListAdapter.GradeCollector?): Int {

                var x1: Long = 1//Long.MAX_VALUE
                var x2: Long = 1//Long.MAX_VALUE

                if(firstItem?.gradedDate  != null)
                {
                    x1 = Util.getDateToLong(firstItem?.gradedDate!!)!!
                }

                if(secondItem?.gradedDate != null)
                {
                    x2 = Util.getDateToLong(secondItem?.gradedDate!!)!!
                }

                val sComp =  x2.compareTo(x1)
                Util.trace("sComp $sComp")
                return sComp

            }
        })
    }

    private fun orderNew(assgnList: List<GradeAssignmentListAdapter.GradeCollector?>) {

        Collections.sort(assgnList,object :Comparator<GradeAssignmentListAdapter.GradeCollector?>{

            override fun compare(firstItem: GradeAssignmentListAdapter.GradeCollector?,
                                 secondItem: GradeAssignmentListAdapter.GradeCollector?): Int {

                /*var x1: Long =  1 //Long.MAX_VALUE
                var x2: Long =  1 //Long.MAX_VALUE

                if(firstItem?.gradedDate  != null)
                {
                     x1 = Util.getDateToLong(firstItem?.gradedDate!!)!!

                }else
                    x1 = -1

                if(secondItem?.gradedDate != null)
                {
                    x2 = Util.getDateToLong(secondItem?.gradedDate!!)!!
                }else
                    x2= -1*/

                var date1 = Util.getDateToLong(firstItem?.gradedDate!!)!!
                var date2 = Util.getDateToLong(secondItem?.gradedDate!!)!!

                Util.trace("Date 1 $date1")
                Util.trace("Date 2 $date2")

                //val sComp =  x2.compareTo(x1)
                val sComp =  date2.compareTo(date1)
                Util.trace("sComp $sComp")
                return sComp

            }

        })
    }

    override fun onDestroy()
    {
        markAllRead()
        super.onDestroy()
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
            HubbleNetworkConstants.getUrl(assignmentsUrl, "{{courseId}}", "" +  Preferences.getValue(PreferenceKeys.SELECTED_COURSE_IDENTIFIER))

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


    private val studentNetworkListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: android.util.Pair<String, Any>)
        {
            Util.trace("Student " + response.second.toString())
            try {

                if(response.first == NetworkConstants.SUCCESS)
                {
                    val gson = Gson()
                    gradeStudentBean = gson.fromJson<GradeStudentBean>(response.second.toString(), GradeStudentBean::class.java)
                    initUi()

                }else
                {
                    DialogUtils.showGenericErrorDialog(this@GradeAssignmentsOtherListActivity)
                }

            }catch (t: Throwable){
                DialogUtils.showGenericErrorDialog(this@GradeAssignmentsOtherListActivity)
            }
        }
    }



}
