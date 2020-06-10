package edu.capella.mobile.android.activity

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*

import edu.capella.mobile.android.adapters.GradeAssignmentListAdapter
import edu.capella.mobile.android.bean.GradeFacultyBean

import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.HubbleNetworkConstants
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler


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

class GradeAssignmentsListActivity:  MenuActivity()/*BaseActivity()*/, ConnectivityReceiver.ConnectivityReceiverListener  {


    var assignmentsList: ArrayList<GradeAssignmentListAdapter.GradeCollector?> = ArrayList()
    var assignmentsListAdapter: GradeAssignmentListAdapter? = null


    //var gradeStudentBean:  GradeStudentBean? = null



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
        //setContentView(R.layout.activity_grade_assignments)
        setContentChildView(R.layout.activity_grade_assignments , true)
        gradeAssignmentsToolbar.genericTitleTxt.text = getString(R.string.assignments)

        gradeAssignmentsToolbar.backHeaderTxt.text = intent.getStringExtra(Constants.BACK_TITLE)

        if( intent.getStringExtra(Constants.BACK_TITLE).length>6)
        {
            gradeAssignmentsToolbar.backHeaderTxt.text = intent.getStringExtra(Constants.BACK_TITLE).toString().substring(0,6)+"..."
        }


        backButtonLayout.contentDescription =   getString(R.string.ada_back_button) +intent.getStringExtra(Constants.BACK_TITLE).toString()

       // gradeStudentBean = intent.getSerializableExtra(Constants.GRADE_ASSIGNMENT) as GradeStudentBean

         if(intent.getSerializableExtra(Constants.STUDENT_GRADE_RECORD) !=null) {
             studentAssignmentStatusRecord =
                 intent.getSerializableExtra(Constants.STUDENT_GRADE_RECORD) as GradeFacultyBean
         }


        try {
            course_pk = intent.getStringExtra(Constants.COURSE_PK)
        }catch (t:Throwable){}
//        initUi()

        OmnitureTrack.trackState("course-grade-assignment-list")
    }


    /**
     * Method initializes tabs with container data and Look and fill.
     *
     */
    private fun initUi() {

        initializeAdapter()
        loadData(studentAssignmentStatusRecord!!)

        gradeListSwipeToRefresh.setColorSchemeColors( getColor(R.color.checkBoxColor))


        gradeListSwipeToRefresh.setOnRefreshListener {

            gradeListSwipeToRefresh.isRefreshing = false
            callStudentAssignmentsApi()
        }
    }

    private fun initializeAdapter() {
        gradeAssignmentsListView.layoutManager = LinearLayoutManager(this)

        assignmentsListAdapter = GradeAssignmentListAdapter(this,false, assignmentsList , object :
            GradeAssignmentListAdapter .GradeAssignmentItemListener
        {
            override fun onItemClicked(value: GradeAssignmentListAdapter.GradeCollector?)
            {



                     Util.trace("Clicked item...")
                     val gradAssIntent = Intent(this@GradeAssignmentsListActivity, GradeSubmissionHistoryActivity::class.java)

                     gradAssIntent.putExtra(Constants.BACK_TITLE, getString(R.string.back))
                     gradAssIntent.putExtra(Constants.GRADE_ASSIGNMENT_STATUS, value?.status)
                     gradAssIntent.putExtra(Constants.GRADE_ASSIGNMENT_BLOCK, value)
                     gradAssIntent.putExtra(Constants.STUDENT_GRADE_RECORD, studentAssignmentStatusRecord)
                     gradAssIntent.putExtra(Constants.UNIT_TITLE,  Html.fromHtml( Util.str(value!!.title) , Html.FROM_HTML_MODE_LEGACY).toString().trim())

                     gradAssIntent.putExtra(Constants.COURSE_PK,course_pk)

                     //startActivity(gradAssIntent)
                      startActivityForResult(gradAssIntent, 4)

            }

        })

        assignmentsListAdapter?.setStatusRecord(studentAssignmentStatusRecord)
        gradeAssignmentsListView.adapter = assignmentsListAdapter
    }

    /**
     *  Factory method of activity, executes when application comes in resume state from pause state.
     *
     */
    override fun onResume() {
        super.onResume()
        initNetworkBroadcastReceiver()
        initUi()
    }

    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun initNetworkBroadcastReceiver() {

        ConnectivityReceiver.connectivityReceiverListener = this@GradeAssignmentsListActivity
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
                callStudentAssignmentsApi()
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






    //private fun loadData(gradeStudentBeanData: GradeStudentBean)
    private fun loadData(studentAssignmentRecord:GradeFacultyBean)
    {

        try {
            assignmentsList.clear()


            for(item in studentAssignmentRecord?.courseAssignment!!)
            {
                var row = GradeAssignmentListAdapter.GradeCollector()
                row.id = item?.id
                row.forumWebLink = null // item?.forumWebLink
                //row.instructionLink = item?.instructionLink
                //row.gradedDate = item?.
                row.instructions =   item?.instructions
                row.link = item?.link
                row.status =  Util.replaceFullStop(item?.status!!)
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

                            if(attemps?.statusDateTime !=null)
                                row.statusDate = attemps?.statusDateTime // NOT SURE HERE
                        }
                    }
                }catch (t: Throwable){}

                row.isUnRead = GradesBlueDotUtil.isUnRead(row?.id , row?.gradedDate , row?.submittedDate , row?.statusDate)

                Util.trace("isUnRead ${row.isUnRead} for ${item?.id}")

               // if(Util.isDesiredStatus(row.status)    ) {

                // IF DATE GRAD / SUBMITTED / STATUS is null then avoid that item
                if(Util.isDesiredStatus(row.status)  && Util.isDesiredStatusWithDate(row.status , row) ) {
                    assignmentsList.add(row)
                }
            }



            if (assignmentsList.size > 0) {

                gradeAssignmentsListLayout.visibility = View.VISIBLE
                order(assignmentsList)
                if (assignmentsListAdapter != null) {
                    assignmentsListAdapter!!.notifyDataSetChanged()
                    gradeAssignmentsListView.adapter = assignmentsListAdapter

                }
            }
        }catch (t: Throwable)
        {
            Util.trace("Assignments error : $t")
            t.printStackTrace()
        }
    }
    /*fun isDesiredStatusWithDate(status:String?  , row :GradeAssignmentListAdapter.GradeCollector): Boolean
    {
        if(status == null)
            return false

        if (status.contains("GRADED", true))
        {
            return row.gradedDate!=null
        }
        else if (status.contains("LATE", true))
        {
            return row.statusDate!=null
        }
        else if (status.contains("SUBMITTED", true))
        {
            return row.submittedDate!=null
        }else if ( status.contains("is due this week", true))
        {
            return row.statusDate!=null
        }
        return false
    }*/





    private fun order(assgnList: List<GradeAssignmentListAdapter.GradeCollector?>) {

        Collections.sort(assgnList,object :Comparator<GradeAssignmentListAdapter.GradeCollector?>{

            override fun compare(firstItem: GradeAssignmentListAdapter.GradeCollector?,
                                 secondItem: GradeAssignmentListAdapter.GradeCollector?): Int {

                var x1: Long =  1 //Long.MAX_VALUE
                var x2: Long =  1 //Long.MAX_VALUE

                /*if(firstItem?.gradedDate  != null)
                {
                        x1 = Util.getDateToLong(firstItem?.gradedDate!!)!!
                }

                if(secondItem?.gradedDate != null)
                {
                    x2 = Util.getDateToLong(secondItem?.gradedDate!!)!!
                }

                if(firstItem?.gradedDate == null)
                {
                    if(firstItem?.submittedDate != null)
                        x1 = Util.getDateToLong(firstItem?.submittedDate!!)!!
                }

                if(firstItem?.gradedDate == null &&  firstItem?.submittedDate == null)
                {
                    if(firstItem?.statusDate != null)
                        x1 = Util.getDateToLong(firstItem?.statusDate!!)!!
                }


                if(firstItem?.gradedDate == null &&  firstItem?.submittedDate == null && firstItem?.statusDate==null)
                {
                    x1 = -1
                }


                if(secondItem?.gradedDate == null)
                {
                    if(secondItem?.submittedDate != null)
                        x2 = Util.getDateToLong(secondItem?.submittedDate!!)!!

                }


                if(secondItem?.gradedDate == null &&  secondItem?.submittedDate == null)
                {
                    if(secondItem?.statusDate != null)
                        x2 = Util.getDateToLong(secondItem?.statusDate!!)!!
                }


               // if(secondItem?.gradedDate == null &&  secondItem?.submittedDate == null && secondItem?.statusDate==null)
                if(secondItem?.gradedDate == null &&  secondItem?.submittedDate == null )
                {
                    x2 = -1
                }*/

                var date1 = getDate(firstItem)
                var date2 = getDate(secondItem)

                if(date1==null)
                    x1 = -1
                else
                    x1 = Util.getDateToLong(date1)!!

                if(date2 == null)
                    x2 = -1
                else
                    x2 = Util.getDateToLong(date2)!!






                val sComp =  x2.compareTo(x1)
                Util.trace("sComp $sComp")
                return sComp
               /* if (sComp != 0) {
                    return sComp
                }

                val list1: String = firstItem?.courseSection?.course?.title!!
                val list2: String = secondItem?.courseSection?.course?.title!!
                return list1.compareTo(list2)*/
            }

        })
    }

    fun getDate(item: GradeAssignmentListAdapter.GradeCollector?):String?
    {
        if(item?.status!!.contains("GRADED" , true))
        {
            return item.gradedDate
        }
        if(item?.status!!.contains("SUBMITTED" , true))
        {
            return item.submittedDate
        }
        if(item?.status!!.contains("is due this week" , true))
        {
            return item.statusDate
        }
        if(item?.status!!.contains("LATE" , true))
        {
            return item.statusDate
        }
        return item.statusDate
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

        assignmentsUrl = HubbleNetworkConstants.getUrl(assignmentsUrl, "{{courseId}}", "" + Preferences.getValue(PreferenceKeys.SELECTED_COURSE_IDENTIFIER ))

        Util.trace("Student Assignments URL  :$assignmentsUrl")


        val networkHandler = NetworkHandler(
            this@GradeAssignmentsListActivity,
            assignmentsUrl,
            params,
            NetworkHandler.METHOD_GET,
            studentAssignmentListNetworkListener,
            finalHeaders
        )

        networkHandler.execute()

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
                    loadData(studentAssignmentStatusRecord!!)
                }else
                {
                }

            }catch (t: Throwable){

            }


        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK)
        {
            if (requestCode == 4)
            {
                callStudentAssignmentsApi()
            }

        }

    }

}
