package edu.capella.mobile.android.activity

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Base64
import android.view.View
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.utils.Constants.ASSESMENT_AND_STATUS
import edu.capella.mobile.android.utils.Constants.READ
import edu.capella.mobile.android.bean.AssessmentLearnerBean
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.task.StickyInfoGrabber
import kotlinx.android.synthetic.main.activity_attempt_detail.*
import kotlinx.android.synthetic.main.activity_attempt_detail.networkLayout
import kotlinx.android.synthetic.main.toolbar_announcement.*

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  15-04-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */class AttemptDetailActivity : MenuActivity()/* BaseActivity()*/,  ConnectivityReceiver.ConnectivityReceiverListener {

    var link :String? = null
    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    private  var isInternetConnection: Boolean=false

    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null
    var courseId: String? = null

    var assessmentAndStatus: AssessmentLearnerBean.FlexpathAssessmentsAndStatus? = null

    private var attemptData: AssessmentLearnerBean.Attempt? = null
    var coursePk = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*setContentView(R.layout.activity_attempt_detail)*/
        setContentChildView(R.layout.activity_attempt_detail,true)
        assessmentAndStatus =
            intent.extras?.getSerializable(Constants.ASSESMENT_AND_STATUS)!! as AssessmentLearnerBean.FlexpathAssessmentsAndStatus

        courseId = intent.extras?.getString(Constants.COURSE_ID)
        initValue()
        listener()
        OmnitureTrack.trackState("course:assessment:submission")
    }

    private fun initValue() {
        attemptData = intent.extras?.getSerializable(Constants.ATTEMPT_DETAIL) as  AssessmentLearnerBean.Attempt
        status_and_date.text = resources.getString(R.string.evaluated) + " "+ Util.formatDateNew(attemptData?.gradedDate!!)
        status_and_date.contentDescription = resources.getString(R.string.evaluated) + " "+ Util.formatDateNew(attemptData?.gradedDate!!)
        if (intent.extras?.getString(Constants.COUNT)?.length!! > 6) {
            backTxt.text =
                intent.extras?.getString(Constants.COUNT)?.let { Util.getTrucatedString(it, 6) }
        }

        if(attemptData!=null)
        {
            if(attemptData!!.getRead()==Constants.READ)
            {
                updateTime.visibility=View.GONE
            }
        }

        headerTxt.text = resources.getString(R.string.attempt)+ " "+attemptData?.attemptName
        link = intent.extras?.getString(Constants.ITEM_LINK)
        coursePk = intent.getStringExtra(Constants.COURSE_PK)
        backButtonLl.setOnClickListener{onBackPressed()}

    }



    override fun onBackPressed() {
        finishActivity()
    }

    private fun finishActivity()
    {
        try {
            val employeeId = intent.extras?.getString(Constants.USER_EMPLOYE_ID)
            var SHARED_COURSE_ID = courseId + "" + employeeId

            if (assessmentAndStatus!!.pccpEvaluatedDate != null) {

                var longgDate = Util.getDateToLong(
                    assessmentAndStatus!!.pccpEvaluatedDate!!
                )
//                var checkdate =
//                    Util.getDate(
//                        longgDate!!,
//                        Constants.DATE_FORMAT_SEC
//                    )!!

                var checkdate=assessmentAndStatus!!.id

                val SHARRED_ATTEMP_READ =
                    SHARED_COURSE_ID + "" + checkdate + "" + PreferenceKeys.FP_LEARNER_ATTEMP_READ

                var attempsReadlist = ArrayList<String>()


                val ateempList = assessmentAndStatus!!.attempts!!.attempts

                for (i in ateempList!!.indices) {
                    if (ateempList[i].attemptId.toString().trim() == attemptData!!.attemptId.toString().trim()) {
                        ateempList[i].setRead(READ)

                    }
                    attempsReadlist.add(ateempList[i].getRead()!!)

                }
                Preferences.addArrayList(SHARRED_ATTEMP_READ, attempsReadlist.toString())
            } else if (assessmentAndStatus!!.pccpSubmittedDate != null) {

                var longgDate = Util.getDateToLong(
                    assessmentAndStatus!!.pccpSubmittedDate!!
                )
//                var checkdate =
//                    Util.getDate(
//                        longgDate!!,
//                        Constants.DATE_FORMAT_SEC
//                    )!!

                var checkdate=assessmentAndStatus!!.id

                val SHARRED_ATTEMP_READ =
                    SHARED_COURSE_ID + "" + checkdate + "" + PreferenceKeys.FP_LEARNER_ATTEMP_READ

                var attempsReadlist = ArrayList<String>()


                val ateempList = assessmentAndStatus!!.attempts!!.attempts

                for (i in ateempList!!.indices) {
                    if (ateempList[i].attemptId.toString().trim() == attemptData!!.attemptId.toString().trim()) {
                        ateempList[i].setRead(READ)

                    }
                    attempsReadlist.add(ateempList[i].getRead()!!)

                }
                Preferences.addArrayList(SHARRED_ATTEMP_READ, attempsReadlist.toString())
            }
        }catch (e:Exception)
        {
            e.printStackTrace()
        }

        val intent = Intent()
        intent.putExtra(ASSESMENT_AND_STATUS, assessmentAndStatus)
        setResult(Activity.RESULT_OK, intent)
        finish() //finishing activity

    }

    private fun listener(){
        view_faculty_feedback.setOnClickListener {
//            val stickyWork = StickyInfoGrabber(this@AttemptDetailActivity)
//            stickyWork.generateMuleSoftStickySessionForTargetUrl(
//                link!!,
//                BuildConfig.STICKY_FORWARD_URL
//            )


            try {

                val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
                val gson = Gson()
                var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

                val instructorComments = attemptData?.instructorComments
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
                    Util.findDomainByMessageLink(link!!)
                var launchPageURL = Util.removeHostName(blackboardDomain!!)

                var enlaunchPageURL =
                    Base64.encodeToString(launchPageURL.toByteArray(), Base64.NO_WRAP)

                var urlSGTLast =   "&courseId=$enCoursePk&currentUserId=$enEmployeeId&launchPageURL=$enlaunchPageURL";

                var scoringGuideUrl =
                    initialLink + "&access_token=" + Preferences.getValue(PreferenceKeys.MULESOFT_ACCESS_TOKEN) + urlSGTLast
                //var scoringGuideUrl = initialLink  + urlSGTLast

                Util.trace("Url is $scoringGuideUrl")
                DialogUtils.screenNamePrefix = "view:faculty:feedback:linkout"
                val stickyWork  = StickyInfoGrabber(this)
                stickyWork.generateMuleSoftStickySessionForTargetUrl(scoringGuideUrl , BuildConfig.STICKY_FORWARD_URL)

                OmnitureTrack.trackState("course:assessment:submission:link-to-CR")
            }catch (t:Throwable){
                Util.trace("SGU error : $t")
                t.printStackTrace()
            }

        }

        additional_feedback.setOnClickListener {

            val stickyWork = StickyInfoGrabber(this@AttemptDetailActivity)
            stickyWork.generateMuleSoftStickySessionForTargetUrl(
                link!!,
                BuildConfig.STICKY_FORWARD_URL
            )
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

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        isInternetConnection = isConnected
    }

    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun initNetworkBroadcastReceiver()
    {

        ConnectivityReceiver.connectivityReceiverListener = this@AttemptDetailActivity
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
}
