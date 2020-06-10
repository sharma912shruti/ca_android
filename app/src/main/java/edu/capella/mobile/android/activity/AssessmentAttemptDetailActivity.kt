package edu.capella.mobile.android.activity

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Pair
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.adapters.AttemptAdapter
import edu.capella.mobile.android.bean.AssessmentLearnerBean
import edu.capella.mobile.android.bean.MuleSoftSession
import edu.capella.mobile.android.interfaces.EventListener
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.task.StickyInfoGrabber
import kotlinx.android.synthetic.main.activity_assessment_attempts_detail.*
import kotlinx.android.synthetic.main.activity_assessment_attempts_detail.networkLayout
import kotlinx.android.synthetic.main.activity_common_webview.*
import kotlinx.android.synthetic.main.toolbar_generic.*
import java.io.File
import java.lang.Exception


/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  14-04-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */

class AssessmentAttemptDetailActivity : MenuActivity()/*BaseActivity()*/,
    ConnectivityReceiver.ConnectivityReceiverListener {

    var attemptAdapter: AttemptAdapter? = null
    var attemptDate: AssessmentLearnerBean.Attempt? = null
    var attemptList: ArrayList<AssessmentLearnerBean.Attempt>? = null
    var courseId: String? = null
    var assessmentAndStatus: AssessmentLearnerBean.FlexpathAssessmentsAndStatus? = null
    var targetDate: String? = null
    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    private var isInternetConnection: Boolean = false

    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_assessment_attempts_detail)
        setContentChildView(R.layout.activity_assessment_attempts_detail,true)
        initValues()
        initRecyclerView()
        initListeners()
        OmnitureTrack.trackState("course:assessment:content-detail")
    }

    private fun initValues() {

        genericTitleTxt.text =
            resources.getString(R.string.assessment) + " " + intent.extras?.getString(Constants.COUNT)?.toInt()?.plus(
                1
            )
        if (resources.getString(R.string.assessment).length > 6) {
            backHeaderTxt.text =
                Util.getTrucatedString(resources.getString(R.string.assessments), 6)

        }

        backButtonLayout.contentDescription = getString(R.string.ada_back_button) + resources.getString(R.string.assessment)

        courseId = intent.extras?.getString(Constants.COURSE_ID)
        assessmentAndStatus =
            intent.extras?.getSerializable(Constants.ASSESMENT_AND_STATUS)!! as AssessmentLearnerBean.FlexpathAssessmentsAndStatus
        targetDate = assessmentAndStatus?.pccpTargetDate
    }

//    fun getTargetDateToCompare(): String {
//        return targetDate!!
//    }

    fun getTargetDateToCompare(): String {

        if(targetDate!=null)
        {
            return targetDate!!
        }
        else
        {
            return ""
        }

    }

    fun getAttemptList():Int{
        try{
            return attemptList?.size!!
        }catch (e:Exception){
            return 0
        }
    }

    fun isGradedAttempt():Boolean{
        var isGraded = false
        for(index in 0 until attemptList?.size!!){
            if (attemptList!![index].status.equals(Constants.GRADED)){
                isGraded = true
                break
            }
        }

        return isGraded
    }

    fun getGradedAttemptIndex():Int{
        var gradedIndex = 0
        for(index in 0 until attemptList?.size!!){
            if (attemptList!![index].status.equals(Constants.GRADED)){
                gradedIndex = index
                break
            }
        }

        return gradedIndex
    }

    private fun fillAttemptData() {
        if (assessmentAndStatus?.attempts != null) {
            attemptList!!.clear()
            addOverDueAndFillList()

        }

        if (attemptList?.size!! < 4) {
            for (index in attemptList?.size!! until 3) {
                var attemptObject = AssessmentLearnerBean.Attempt()
                attemptObject.attemptId = null
                attemptObject.attemptName = index.plus(1).toString()
                attemptObject.gradedDate = null
                attemptObject.instructorComments = null
                attemptObject.score = null

//                if(getTargetDateToCompare() != null && getTargetDateToCompare() != "") {
//                    val targetDateInMilis = Util.getDateInMiliSeconds(
//                        Util.formatDateTimeNew(
//                            getTargetDateToCompare(),
//                            false,
//                            false
//                        )
//                    )
//                    if ((System.currentTimeMillis()) > targetDateInMilis) {
//                        if (index > 0) {
//                            if (isEvaluated) {
//                                attempt.overdue = false
//                            }
//                        } else {
//                            attempt.overdue = true
//                        }
//                    } else {
//                        attempt.overdue = false
//                    }
//                }else{
//                    attempt.overdue = false
//                }

                attemptObject.overdue = false
                if (index != 0 && attemptList!![index - 1].status == Constants.GRADED) {
                    attemptObject.status = getString(R.string.not_submitted)
                } else if (index == 0/* && (attemptList!![index - 1].status == Constants.GRADED || attemptList!![index - 1].status == Constants.SUBMITTED)*/) {
                    attemptObject.status = getString(R.string.not_submitted)
                } else {
                    attemptObject.status = resources.getString(R.string.not_yet_available)
                }
                attemptObject.submittedDate = null
                attemptList?.add(attemptObject)
            }
        }

    }

    private fun addOverDueAndFillList(){
        val attemptListTemp = assessmentAndStatus?.attempts?.attempts!!
        var isEvaluated : Boolean = false
        for(index in 0 until attemptListTemp.size){
            var attempt: AssessmentLearnerBean.Attempt = AssessmentLearnerBean.Attempt()
            attempt = attemptListTemp[index]

            if(getTargetDateToCompare() != null && getTargetDateToCompare() != "") {
                val targetDateInMilis = Util.getDateInMiliSeconds(
                    Util.formatDateTimeNew(
                        getTargetDateToCompare(),
                        false,
                        false
                    )
                )
                if ((System.currentTimeMillis()) > targetDateInMilis) {
                    if (index > 0) {
                        if (isEvaluated) {
                            attempt.overdue = false
                        }
                    } else {
                        attempt.overdue = true
                    }
                } else {
                    attempt.overdue = false
                }
            }else{
                attempt.overdue = false
            }
            attemptList?.add(attemptListTemp[index])
            isEvaluated = attemptListTemp[index].status.equals(Constants.GRADED)
        }

    }

    private fun initListeners() {
        assessmentDetailView.setOnClickListener {
            val assessmentIntent = Intent(
                this@AssessmentAttemptDetailActivity,
                AssessmentDetailListActivity::class.java
            )
            assessmentIntent.putExtra(Constants.COURSE_ID, courseId)
            val count = intent.extras?.getString(Constants.COUNT)
            assessmentIntent.putExtra(Constants.COUNT, count)
            assessmentIntent.putExtra(Constants.COURSE_MESSAGE_LINK,intent?.extras?.getString(Constants.COURSE_MESSAGE_LINK))
            assessmentIntent.putExtra(Constants.TITLE, assessmentAndStatus?.assignmentTitle)
            assessmentIntent.putExtra(Constants.ASSESMENT_AND_STATUS,assessmentAndStatus)
            startActivity(assessmentIntent)
        }
    }

    private fun initRecyclerView() {
        attemptList = ArrayList()
        fillAttemptData()
        attempt_list.layoutManager = LinearLayoutManager(this)
        attemptAdapter = AttemptAdapter(this, attemptList!!, object :
            AttemptAdapter.OnAttemptItemClickListener {
            override fun onItemClicked(value: AssessmentLearnerBean.Attempt) {
                handleAttemptCLick(value)
                OmnitureTrack.trackState("course:assessment:target-date-linkout")
            }

            override fun onUrlClick() {

                prepareLinkAndOpen()

            }
        })
        attempt_list.adapter = attemptAdapter

    }

    private fun handleAttemptCLick(value: AssessmentLearnerBean.Attempt) {
        attemptDate=value
        if (value.status.equals(Constants.GRADED)) {
            val attemptIntent =
                Intent(this@AssessmentAttemptDetailActivity, AttemptDetailActivity::class.java)
            attemptIntent.putExtra(Constants.COUNT, genericTitleTxt.text)
            attemptIntent.putExtra(Constants.ATTEMPT_DETAIL, value)
            attemptIntent.putExtra(Constants.COURSE_ID, courseId)
            attemptIntent.putExtra(Constants.USER_EMPLOYE_ID,intent.extras?.getString(Constants.USER_EMPLOYE_ID))
            attemptIntent.putExtra(Constants.COURSE_PK, intent.getStringExtra(Constants.COURSE_PK))
            attemptIntent.putExtra(Constants.ITEM_LINK, assessmentAndStatus?.assignmentLink!!)
            attemptIntent.putExtra(Constants.ASSESMENT_AND_STATUS,assessmentAndStatus)
            startActivityForResult(attemptIntent, Constants.ACTIVITY_REQUEST)
        } else if (!value.status.equals(getString(R.string.not_yet_available))) {

            prepareLinkAndOpen()

        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.ACTIVITY_REQUEST) {
            if(data!=null) {
                assessmentAndStatus=    data.getSerializableExtra(Constants.ASSESMENT_AND_STATUS)!! as AssessmentLearnerBean.FlexpathAssessmentsAndStatus
                fillAttemptData()
                if (attemptAdapter != null) {
                    attemptAdapter!!.notifyDataSetChanged()
                }
            }

        }

    }

    private fun prepareLinkAndOpen() {

        val id = intent.extras?.getString(Constants.ID)
        Util.trace("sharePref", Preferences.getValue(PreferenceKeys.COURSE_ROOM_DISCUSSION_LINK))
        val headerLink = Preferences.getValue(PreferenceKeys.COURSE_ROOM_DISCUSSION_LINK)
         val courseId = headerLink?.subSequence(headerLink?.indexOf("course_id=")+10 ,headerLink.length)
        Util.trace("courseId", courseId.toString())

        var urlWithoutDomainName = headerLink?.substring(8, headerLink.length);
        var schoolName = urlWithoutDomainName?.substring(0, urlWithoutDomainName.indexOf("/"))
        val finalUrl =
            "https://" + schoolName + "/" + "webapps/blackboard/content/listContent.jsp?course_id=" + courseId + "&content_id=" + id + "&mode=reset"

        Util.trace("onAttempt click",finalUrl)

        val stickyWork  = StickyInfoGrabber(this)
//        stickyWork.generateMuleSoftStickySessionForTargetUrl(finalUrl, BuildConfig.STICKY_FORWARD_URL)
//        checkReadAttemp()

        Util.openBrowserWithConfirmationPopupNew(this@AssessmentAttemptDetailActivity , object : EventListener {
            override fun confirm() {
                super.confirm()


                stickyWork.generateReturnMuleSoftStickySessionForTargetUrl(finalUrl.toString(),
                    object : NetworkListener {
                        override fun onNetworkResponse(response: Pair<String, Any>) {

                            if (response.first == NetworkConstants.SUCCESS) {
                                Util.trace("Token Is : " + response.second)

                                val gson = Gson()
                                val muleSoftSession = gson.fromJson<MuleSoftSession>(
                                    response.second.toString(),
                                    MuleSoftSession::class.java
                                )

                                var finalUrlToOpen =
                                    BuildConfig.STICKY_FORWARD_URL + File.separator + muleSoftSession.token

                                checkReadAttemp()
                                Util.openExternalBrowser(this@AssessmentAttemptDetailActivity, finalUrlToOpen)
                                Util.trace("Final Url : " + finalUrlToOpen)
                            }else{
                            }


                        }

                    })
            }

            override fun cancel() {
                super.cancel()
            }

        })




    }

    private fun checkReadAttemp()
    {
        val employeeId=intent.extras?.getString(Constants.USER_EMPLOYE_ID)
        var SHARED_COURSE_ID = courseId+""+employeeId
        if(assessmentAndStatus!!.pccpSubmittedDate!=null) {

            var longgDate = Util.getDateToLong(
                assessmentAndStatus!!.pccpSubmittedDate!!
            )
//            var checkdate =
//                Util.getDate(
//                    longgDate!!,
//                    Constants.DATE_FORMAT_SEC
//                )!!

            var checkdate=assessmentAndStatus!!.id


            val SHARRED_ATTEMP_READ =
                SHARED_COURSE_ID + "" + checkdate + "" + PreferenceKeys.FP_LEARNER_ATTEMP_READ

            var attempsReadlist = ArrayList<String>()


            val ateempList = assessmentAndStatus!!.attempts!!.attempts

            for (i in ateempList!!.indices) {
                if (attemptDate != null) {
                    if (ateempList[i].attemptId.toString().trim() == attemptDate!!.attemptId.toString().trim()) {
                        ateempList[i].setRead(Constants.READ)
                    }
                    attempsReadlist.add(ateempList[i].getRead()!!)
                }

            }
            Preferences.addArrayList(SHARRED_ATTEMP_READ, attempsReadlist.toString())

            fillAttemptData()
            if (attemptAdapter != null) {
                attemptAdapter!!.notifyDataSetChanged()
            }
        }

    }



    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        isInternetConnection = isConnected
    }

    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun initNetworkBroadcastReceiver() {

        ConnectivityReceiver.connectivityReceiverListener = this@AssessmentAttemptDetailActivity
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
