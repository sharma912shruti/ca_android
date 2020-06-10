package edu.capella.mobile.android.activity

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Html
import android.util.Base64
import android.view.View
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.adapters.GradeAssignmentListAdapter
import edu.capella.mobile.android.bean.GradeFacultyBean
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.HubbleNetworkConstants
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import edu.capella.mobile.android.task.StickyInfoGrabber

import kotlinx.android.synthetic.main.activity_grade_history.*

import kotlinx.android.synthetic.main.toolbar_generic.*
import kotlinx.android.synthetic.main.toolbar_generic.view.*



class GradeSubmissionHistoryActivity : MenuActivity() /*BaseActivity() */,  ConnectivityReceiver.ConnectivityReceiverListener{


   /* var courseId = ""
    var forumId  = ""*/
    var unitTitle = ""
    var STATUS = "";
    var gradeBlock : GradeAssignmentListAdapter.GradeCollector? = null

    var studentAssignmentStatusRecord: GradeFacultyBean? = null
    var coursePk = ""

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
        //setContentView(R.layout.activity_grade_history)
        setContentChildView(R.layout.activity_grade_history , true)

        gradeHistoryToolbar.genericTitleTxt.text= getString(R.string.submission_history)
        gradeHistoryToolbar.backHeaderTxt.text=intent.getStringExtra(Constants.BACK_TITLE).toString()

        backButtonLayout.contentDescription =   getString(R.string.ada_back_button) +intent.getStringExtra(Constants.BACK_TITLE).toString()
        backButtonImg.setOnClickListener { finish() }

        try {
        unitTitle =   intent.getStringExtra(Constants.UNIT_TITLE)
        STATUS =   intent.getStringExtra(Constants.GRADE_ASSIGNMENT_STATUS)
        gradeBlock = intent.getSerializableExtra(Constants.GRADE_ASSIGNMENT_BLOCK) as GradeAssignmentListAdapter.GradeCollector
        unitNameTxt.text = unitTitle
           coursePk = intent.getStringExtra(Constants.COURSE_PK)
       }catch (t:Throwable){}



        viewUnitTxt.setOnClickListener{
            openViewDescirption()


        }

        OmnitureTrack.trackAction("course:grades-and-status:assignment-submission")


        if(STATUS!= null && STATUS.contains("GRADED" , true))
        {
            studentAssignmentStatusRecord =  intent.getSerializableExtra(Constants.STUDENT_GRADE_RECORD) as GradeFacultyBean
            initUiForGraded()
        } //else if(STATUS!= null && STATUS.contains("SUBMITTED" , true))
        else
        {
            initUiForRest()
        }
        refreshUI()
        GradesBlueDotUtil.markAsRead(gradeBlock?.id ,gradeBlock?.gradedDate,gradeBlock?.submittedDate , gradeBlock?.statusDate)
    }
    private fun refreshUI()
    {
        if(STATUS!= null && STATUS.contains("GRADED" , true))
        {

            initUiForGraded()
        } //else if(STATUS!= null && STATUS.contains("SUBMITTED" , true))
        else
        {
            initUiForRest()
        }


        gradeHistorySwipeToRefresh.setColorSchemeColors( getColor(R.color.checkBoxColor))

        gradeHistorySwipeToRefresh.setOnRefreshListener {

            gradeHistorySwipeToRefresh.isRefreshing = false
            callStudentAssignmentsApi()
        }

        GradesBlueDotUtil.markAsRead(gradeBlock?.id ,gradeBlock?.gradedDate,gradeBlock?.submittedDate , gradeBlock?.statusDate)
    }

    override fun finish()
    {
        val resultIntent = Intent()
        setResult(Activity.RESULT_OK, resultIntent)
        super.finish()
    }

    override fun onDestroy()
    {
        val resultIntent = Intent()
        setResult(Activity.RESULT_OK, resultIntent)

        super.onDestroy()

    }



   /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK)
        {
            if (requestCode == Constants.ACTIVITY_REQUEST_CODE)
            {
                val snakeBarMessage = data!!.getStringExtra(Constants.SNAKEBAR_MESSAGE)
                val snakeBarMessageDuration = data!!.getIntExtra(Constants.SNAKEBAR_MESSAGE_DURATION,3000)
                Util.showSnakeBar(snakeBarMessage, disTopicParent , snakeBarMessageDuration)
            }

        }

    }*/

    /**
     * Method initializes tabs with container data and Look and fill.
     *
     */
    private fun initUiForGraded()
    {

      /*  courseId = intent.getStringExtra(Constants.COURSE_ID)
        forumId = intent.getStringExtra(Constants.FORUM_ID)*/
        gradedLearnerLayout.visibility   = View.VISIBLE
        scoringLayout.visibility  = View.VISIBLE

        submittedLearnerLayout.visibility = View.GONE
        unitBioTextLayout.visibility  = View.GONE

        currentScoreTxt.text =   Util.getTwoDigitNumber(gradeBlock?.score!!)
        totalPossibleScoreTxt.text = "/" +  Util.getNonDecimal(gradeBlock?.totalPossibleScore!!.toString())
        if(gradeBlock?.gradedDate!=null)
        {
            dateTimeTxt.text = Util.formatDateTimeNew(gradeBlock?.gradedDate!!, false, false)

            var diff = Util.getDateAgo(gradeBlock?.gradedDate!!)

            if (diff <= 3 && gradeBlock!!.isUnRead) {
                notificationLayout.visibility = View.VISIBLE
                notificationText.text = getString(R.string.notification_new_or_updated_past_three_days)
            }else
            {
                notificationLayout.visibility = View.GONE
            }
        }else
        {
            dateTimeTxt.visibility = View.GONE
        }

         if(Util.findURLinHREF(getInstructorLinkForGrade(gradeBlock?.id!!)))
         {
             viewFacultyFeedbackBtnTxt.visibility = View.VISIBLE
         }else
         {
             noFeedForThisAssignment.visibility = View.VISIBLE
             watchScoreFeedback.visibility = View.GONE
             viewFacultyFeedbackBtnTxt.visibility = View.GONE
         }

        viewFacultyFeedbackBtnTxt.setOnClickListener{

            try {

                val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
                val gson = Gson()
                var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

                val instructorComments = getInstructorLinkForGrade(gradeBlock?.id!!)
                val domainUrl = extractUrlFromHref(instructorComments!!)

                var initialLink = ""
                if(domainUrl?.contains("target=_blank>Scoring")!!){
                    initialLink = domainUrl.replace("target=_blank>Scoring","")
                }else {
                    initialLink = domainUrl
                }

                var enCoursePk = Base64.encodeToString(coursePk.toByteArray(), Base64.NO_WRAP)
                var enEmployeeId = Base64.encodeToString(
                    loginBean?.authData?.employeeId?.value!!.toByteArray(),
                    Base64.NO_WRAP
                )

                var blackboardDomain =
                    Util.findDomainByMessageLink(getLinkForGrade(gradeBlock?.id!!)!!)
                var launchPageURL = Util.removeHostName(blackboardDomain!!)

                var enlaunchPageURL =
                    Base64.encodeToString(launchPageURL.toByteArray(), Base64.NO_WRAP)

                var urlSGTLast =   "&courseId=$enCoursePk&currentUserId=$enEmployeeId&launchPageURL=$enlaunchPageURL";

                var scoringGuideUrl =
                    initialLink + "&access_token=" + Preferences.getValue(PreferenceKeys.MULESOFT_ACCESS_TOKEN) + urlSGTLast
                //var scoringGuideUrl = initialLink  + urlSGTLast

                Util.trace("Url is $scoringGuideUrl")
              /* val stickyWork = StickyInfoGrabber(this)
                stickyWork.openScoringGuide(scoringGuideUrl, BuildConfig.STICKY_FORWARD_URL)*/

                /**
                 * As par Micaela i am commenting this, and uncommeting previous code of opening in
                 * external browser.
                 */

                val intent = Intent(this@GradeSubmissionHistoryActivity, CommonWebViewActivity::class.java)
                intent.putExtra(Constants.URL_FOR_IN_APP, scoringGuideUrl)
                intent.putExtra(Constants.IN_APP_TITLE,getString(R.string.scoring_guide))
                intent.putExtra(Constants.FOR_SCORING_GUIDE,true)
                startActivity(intent)
                overridePendingTransition( R.anim.slide_in_up, R.anim.no_anim)

            }catch (t:Throwable){
                Util.trace("SGU error : $t")
                t.printStackTrace()
            }


        }

        checkAdditionalFeedbackTxt.setOnClickListener{

           openAdditionalComments()
        }
    }
    private fun openViewDescirption()
    {
        OmnitureTrack.trackAction("course:grades-and-status:assignment-submission:view-description")

          var ins:String? =  gradeBlock?.instructions
          var link:String?  = gradeBlock?.link

        if(link == null || ins == null)
            return

//          var capellaDomain = ".capella.edu/"
//
//          var domain =  link.substring(0, link.indexOf(capellaDomain) + capellaDomain.length)
//          var uri = Util.extractHref_recursive(ins)
//        if(uri?.indexOf('@')!! > -1)
//        {
//            uri = uri?.substring(uri.lastIndexOf("@", 0, true), uri.length)
//        }else if (uri?.indexOf("bbcswebdav")!! > -1)
//        {
//            uri = uri.substring(uri.indexOf("bbcswebdav"), uri.length)
//        }
//
//        var finalUrl =  domain + uri

        val url = getFinalUrlTOLoad(ins)

        if(url == null)
            return

        val intent = Intent(this, UnitWebViewActivity::class.java)
        intent.putExtra(Constants.PAGE_WARNING_HIDE , true)

        intent.putExtra(Constants.URL_FOR_IN_APP, url)
        intent.putExtra(Constants.ORANGE_TITLE, unitTitle)
        intent.putExtra(Constants.IN_APP_TITLE, "")
        startActivity(intent)
        //overridePendingTransition( R.anim.slide_in_up, R.anim.no_anim)

    }


    private fun getFinalUrlTOLoad(ins: String):String{
        var substringContent = ins

        Util.trace("sharePref",Preferences.getValue(PreferenceKeys.COURSE_ROOM_DISCUSSION_LINK))
        val headerLink = Preferences.getValue(PreferenceKeys.COURSE_ROOM_DISCUSSION_LINK)
        var urlWithoutDomainName = headerLink?.substring(8, headerLink.length);
        var schoolName =urlWithoutDomainName?.substring(0,  urlWithoutDomainName.indexOf("/"))

        var finalContent = ""
        if(substringContent?.contains("@")!!) {
            finalContent  = "https://" + schoolName + "/" + substringContent?.subSequence(
                getFirstIndex(substringContent), substringContent?.length
            ).toString()
        }else{

            finalContent = "https://" + schoolName + "/" + substringContent?.subSequence(
                substringContent?.lastIndexOf(".edu/")+5, substringContent?.length
            ).toString()
        }
        Util.trace("common content substring",finalContent.subSequence(0,finalContent.indexOf("\"")).toString())
        return finalContent.subSequence(0,finalContent.indexOf("\"")).toString()
    }

    private fun getFirstIndex(subString:String):Int{
        val stringChar = subString.toCharArray()
        var count = 0
        var charIndex = 0
        for(index in 0 until stringChar.size){
            if(stringChar[index] == '@'){
                count +=  1
            }
            if(count == 4){
                charIndex = index+1
                break
            }
        }
        return charIndex
    }

    private fun openAdditionalComments()
    {
        try
        {
            OmnitureTrack.trackAction("course:grades-and-status:assignment-submission:link-to-CR")


          //  var blackboardDomain = Util.getHostName(getLinkForGrade(gradeBlock?.id!!)!!)
            var blackboardDomain = Util.getHostName(gradeBlock?.link!!)

            var finalUrl = blackboardDomain + "/webapps/assignment/uploadAssignment?content_id=" + gradeBlock?.id + "&course_id=" + coursePk + "&assign_group_id=&mode=view"

            Util.trace("Finale url $finalUrl")
            val stickyWork  = StickyInfoGrabber(this)

            stickyWork.generateMuleSoftStickySessionForTargetUrl(finalUrl , BuildConfig.STICKY_FORWARD_URL)

        }catch (t: Throwable)
        {
            Util.trace("Opening error : $t")
            t.printStackTrace()
        }
    }
    private fun extractUrlFromHref(string:String):String?
    {
        var data = string.substring(string.lastIndexOf("href=") , string.length)
        var parts = data.split(" ")
        for(p in parts)
        {
            if(p.contains("href="))
            {
                data = p.replace("\"" ,"")
                data= data.replace("href=","")
               // data = data.replace("https://" , "")
               // data = data.substring(0,data.indexOf("/"))
                return data
            }
        }
        return null
    }

    private fun initUiForRest()
    {
        notificationLayout.visibility = View.GONE

        OmnitureTrack.trackAction("course:assessment:submission")
        /*courseId = intent.getStringExtra(Constants.COURSE_ID)
          forumId = intent.getStringExtra(Constants.FORUM_ID)*/
        submittedLearnerLayout.visibility = View.VISIBLE
        unitBioTextLayout.visibility  = View.VISIBLE

        gradedLearnerLayout.visibility   = View.GONE
        scoringLayout.visibility  = View.GONE

        unitBioNameTxt.text = Html.fromHtml( Util.str(gradeBlock?.title) , Html.FROM_HTML_MODE_LEGACY).toString().trim()
        unitBioStatusTxt.text = STATUS

        var scenerioDate =  getDateForScenerios(gradeBlock , STATUS)

        if(scenerioDate !=null) {
            dateTimeTxt.text = Util.formatDateTimeNew(scenerioDate, false, false)
        }else
        {
            dateTimeTxt.visibility = View.GONE
        }

        if(STATUS.contains("Late" , true))
        {
            lateWarningImg.visibility = View.VISIBLE

            if(scenerioDate !=null)
            {
                dateTimeTxt.text = "As of: "+ Util.formatDateTimeNew(scenerioDate, false, false)

                var diff = Util.getDateAgo(scenerioDate)
                if (diff <= 3 && gradeBlock!!.isUnRead) {
                    notificationLayout.visibility = View.VISIBLE
                    notificationText.text = getString(R.string.late)
                }else
                {
                    notificationLayout.visibility = View.GONE
                }

            }else
            {
                dateTimeTxt.visibility = View.GONE
            }
        } else if(STATUS.contains("SUBMITTED" , true))
        {
            if(scenerioDate !=null)
            {
                dateTimeTxt.text =  Util.formatDateTimeNew(scenerioDate, false, false)

                var diff = Util.getDateAgo(scenerioDate)
                if (diff <= 3 && gradeBlock!!.isUnRead) {
                    notificationLayout.visibility = View.VISIBLE
                    notificationText.text = getString(R.string.new_or_updated_in_past_3_days)
                }else
                {
                    notificationLayout.visibility = View.GONE
                }

            }else
            {
                dateTimeTxt.visibility = View.GONE
            }
        }else if(STATUS.contains("due this week" , true))
        {
            if(scenerioDate !=null)
            {
                dateTimeTxt.text =  "Submit by: " + Util.formatDateTimeNew(scenerioDate, false, false)

                var diff = Util.getDateAgo(scenerioDate)
                if (diff <= 3 && gradeBlock!!.isUnRead)
                {
                    notificationLayout.visibility = View.VISIBLE
                    notificationText.text = getString(R.string.due_soon)
                    var days: Long = Util.getDateAgo(scenerioDate)
                    if (days.compareTo(0) == 0)
                    {
                        unitBioStatusTxt.text =   getString(R.string.is_due_today)
                        getString(R.string.due_soon)
                        notificationText.text = getString(R.string.due_today)
                    }
                }else
                {
                    notificationLayout.visibility = View.GONE
                }
                var days: Long = Util.getDateAgo(scenerioDate)
                if (days.compareTo(0) == 0) {
                    unitBioStatusTxt.text = getString(R.string.is_due_today)
                }
            }else
            {
                dateTimeTxt.visibility = View.GONE
            }
        }



        viewAllFeedbackClassRoomTxt.setOnClickListener{
                openAdditionalComments()

        }
    }

    private fun getDateForScenerios(gradeBlock: GradeAssignmentListAdapter.GradeCollector?, status: String): String?
    {
            if(status.contains("Late"))
            {
                return gradeBlock?.statusDate
            }
            if(status.contains("submitted"))
            {
                return gradeBlock?.submittedDate
            }

           if(gradeBlock?.statusDate != null)
           {
               return gradeBlock?.statusDate
           }
        return null
    }


    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun initNetworkBroadcastReceiver()
    {

        ConnectivityReceiver.connectivityReceiverListener = this@GradeSubmissionHistoryActivity
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
        initNetworkBroadcastReceiver()

    }

    /**
     * Factory method of activity, executes when activity goes in paused state.
     *
     */
    override fun onPause() {
        super.onPause()
        releaseConnectivityReceiver()
    }



    private fun releaseConnectivityReceiver()
    {
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



    private fun extractHrefFromInstructorLink(string:String?) : String?
    {

        try{
            var str = string
            var parts = str?.split(" ")
            var view_description_link : String? = null

            if (parts != null) {
                for(p in parts) {
                    if(p.indexOf("href",0 , true) != -1)
                    {
                        Util.trace("HREF : " + p)

                        var  discussionRoomLink = Preferences.getValue(PreferenceKeys.COURSE_ROOM_DISCUSSION_LINK)

                        var urlWithoutDomainName = discussionRoomLink?.substring(8, discussionRoomLink.length);
                        var schoolName =urlWithoutDomainName?.substring(0,  urlWithoutDomainName.indexOf("/"))

                         view_description_link = "https://" + schoolName + "/" + p.substring(p.lastIndexOf("@")+1 , p.length-1 )

                        Util.trace("view_description_link : " + view_description_link)
                    }
                }
            }

           return view_description_link

        }catch (t:Throwable){
            Util.trace(" setting VIEW_DESCRIPTION_LINK_PATH error $t")
        }
        return null
    }

    private fun getInstructorLinkForGrade(id: String  ) : String?
    {
        try
        {
            for(item in studentAssignmentStatusRecord?.courseAssignment!!)
            {
                if(item?.id == id)
                {
                    var attempts = item?.attempts
                    for( attempt in attempts?.attempt!!)
                    {
                        if(attempt?.status!!.contains("GRADED", true) && (item?.submittedDateTime!!.contains(attempt?.submittedDate!! , true)))
                        {
                            return attempt?.instructorComments
                        }
                    }
                }
            }
        }catch (t:Throwable)
        {
            Util.trace("getInstructorLink error $t")
        }
        return null
    }

    private fun getLinkForGrade(id: String  ) : String?
    {
        try
        {
            for(item in studentAssignmentStatusRecord?.courseAssignment!!)
            {
                if(item?.id == id)
                {
                    var attempts = item?.attempts
                    for( attempt in attempts?.attempt!!)
                    {
                        if(attempt?.status!!.contains("GRADED", true) && (item?.submittedDateTime!!.contains(attempt?.submittedDate!! , true)))
                        {
                            return item?.link
                        }
                    }
                }
            }
        }catch (t:Throwable)
        {
            Util.trace("getInstructorLink error $t")
        }
        return null
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
            this@GradeSubmissionHistoryActivity,
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
                    createGradeBlock()
                }else
                {
                }

            }catch (t: Throwable){

            }


        }
    }

    private fun createGradeBlock()
    {
        for(item in studentAssignmentStatusRecord?.courseAssignment!!)
        {
            if(gradeBlock?.id == item?.id) {
                var row = GradeAssignmentListAdapter.GradeCollector()
                row.id = item?.id
                row.forumWebLink = null // item?.forumWebLink
                //row.instructionLink = item?.instructionLink
                //row.gradedDate = item?.
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

                            if (attemps?.statusDateTime != null)
                                row.statusDate = attemps?.statusDateTime // NOT SURE HERE
                        }
                    }
                } catch (t: Throwable) {
                }

                row.isUnRead = GradesBlueDotUtil.isUnRead(
                    row?.id,
                    row?.gradedDate,
                    row?.submittedDate,
                    row?.statusDate
                )

                gradeBlock = row
                STATUS = row.status!!
                refreshUI()

                Util.trace("found item in new list")
                break
            }


        }
    }

}
