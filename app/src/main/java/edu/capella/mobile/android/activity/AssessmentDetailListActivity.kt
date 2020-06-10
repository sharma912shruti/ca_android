package edu.capella.mobile.android.activity

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Pair
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.adapters.AssessmentDetailListAdapter
import edu.capella.mobile.android.bean.AssessmentLearnerBean
import edu.capella.mobile.android.bean.CourseAssessmentBean
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.HubbleNetworkConstants
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import kotlinx.android.synthetic.main.activity_assessment_detail_list.*
import kotlinx.android.synthetic.main.activity_assessment_detail_list.networkLayout
import kotlinx.android.synthetic.main.toolbar_generic.*

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
 */

class AssessmentDetailListActivity : MenuActivity()/*BaseActivity()*/,  ConnectivityReceiver.ConnectivityReceiverListener {

    var courseId: String? = null
    var courseAssesmentList: ArrayList<CourseAssessmentBean.StudyInstruction>? = null
    var assessmentDetailListAdapter: AssessmentDetailListAdapter? = null
    var assessmentAndStatus: AssessmentLearnerBean.FlexpathAssessmentsAndStatus? = null
    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    private  var isInternetConnection: Boolean=false

    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_assessment_detail_list)
        setContentChildView(R.layout.activity_assessment_detail_list,true)
        initValues()
        initRecycler()
        initialiseListener()
        callApiToCourseAssessments()
        OmnitureTrack.trackAction("course:assessment:contents")
    }

    fun getAssessmentDetailListSize(): Int {
        try {
            return courseAssesmentList?.size!!
        } catch (t: Throwable) {
            return 0
        }
    }

    private fun initialiseListener() {
        val swipeRefreshColor =  ContextCompat.getColor(this,R.color.checkBoxColor)
        detailPullToRefresh.setColorSchemeColors(swipeRefreshColor)
        detailPullToRefresh.setOnRefreshListener {
            callApiToCourseAssessments()
            detailPullToRefresh.isRefreshing = false
        }
    }

    private fun initRecycler() {
        courseAssesmentList = ArrayList()
        detailList.layoutManager = LinearLayoutManager(this)



        assessmentDetailListAdapter = AssessmentDetailListAdapter(
            this,
            courseAssesmentList!!,
            object : AssessmentDetailListAdapter.OnItemClickListener {
                override fun onItemClicked(value: CourseAssessmentBean.StudyInstruction) {
                    val detailIntent =
                        Intent(this@AssessmentDetailListActivity, AssessmentDetailActivity::class.java)
                    detailIntent.putExtra(Constants.CONTENT, value?.content)
                    detailIntent.putExtra(Constants.CONTENT_TITLE, headerText.text.toString())
                    detailIntent.putExtra(Constants.TITLE, value?.title)
                    detailIntent.putExtra(Constants.COURSE_MESSAGE_LINK,intent.extras?.getString(Constants.COURSE_MESSAGE_LINK))
                    detailIntent.putExtra(Constants.SHOW_WARING, true)
                    this@AssessmentDetailListActivity.startActivity(detailIntent)
                }

            })
        detailList.adapter = assessmentDetailListAdapter
    }

    private fun initValues() {

        assessmentAndStatus =
            intent.extras?.getSerializable(Constants.ASSESMENT_AND_STATUS)!! as AssessmentLearnerBean.FlexpathAssessmentsAndStatus
        genericTitleTxt.text = resources.getString(R.string.details)

         val sideHeader =    resources.getString(R.string.assessment) + " " + intent.extras?.getString(Constants.COUNT)?.toInt()?.plus(1)


        if (sideHeader.length > 6) {
            backHeaderTxt.text =
                Util.getTrucatedString(sideHeader, 6)

        }

        backButtonLayout.contentDescription = getString(R.string.ada_back_button) + sideHeader

        val tittleText = HtmlCompat.fromHtml(
            intent.extras?.getString(Constants.TITLE)!!,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        headerText.text =
            tittleText.subSequence(tittleText.lastIndexOf("]").plus(1), tittleText.length)
        courseId = intent.extras?.getString(Constants.COURSE_ID)!!
    }


    private fun callApiToCourseAssessments() {
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val authHeader = NetworkHandler.getAuthorizationHeader()
        val hubbleHeaders = NetworkHandler.getHeadersForHubbleRequest(
            loginBean?.authData?.token,
            HubbleNetworkConstants.getAcceptHeaderValue(HubbleNetworkConstants.COURSE_ASESSMENT)
        )

        val finalHeaders = NetworkHandler.meargeHeaders(authHeader, hubbleHeaders)

        val params = HashMap<String, Any>()

        val url = HubbleNetworkConstants.getUrl(
            HubbleNetworkConstants.COURSE_ASESSMENT,
            "{{employeeId}}",
            "" + loginBean?.authData?.employeeId!!.value
        )


        val targetDateUrl =
            HubbleNetworkConstants.getUrl(url, "{{courseId}}", "" + courseId)

        Util.trace("course assessment url", targetDateUrl)
        val networkHandler = NetworkHandler(
            this,
            targetDateUrl,
            params,
            NetworkHandler.METHOD_GET,
            courseAssessmentNetworkListener,
            finalHeaders
        )
        networkHandler.setSilentMode(false)
        networkHandler.execute()
    }

    private val courseAssessmentNetworkListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {
            try {
                Util.trace("course assessment first :  " + response.first)
                //Util.trace("course assessment second :  " + response.second)

                if (response.first == NetworkConstants.SUCCESS) {
                    val gson = Gson()
                    val courseAssessmentBean = gson.fromJson<CourseAssessmentBean>(
                        response.second.toString(),
                        CourseAssessmentBean::class.java
                    )
                    filterAndSetListValue(courseAssessmentBean)

                } else {
                    DialogUtils.showGenericErrorDialog(this@AssessmentDetailListActivity)
                }
            }catch (e:Exception){
                e.printStackTrace()
                DialogUtils.showGenericErrorDialog(this@AssessmentDetailListActivity)
            }
        }
    }

    private fun filterAndSetListValue(courseAssessmentBean: CourseAssessmentBean) {
        courseAssesmentList?.clear()
        var isFp1 = false
        var courseUnitList = ArrayList<CourseAssessmentBean.CourseUnit>()
        if (courseAssessmentBean.courseUnits?.courseUnit!![0].name != null && courseAssessmentBean.courseUnits?.courseUnit!![0].name?.length!! <= 13) {
            // for FP 1
            isFp1 = true
            for (index in 0 until courseAssessmentBean.courseUnits?.courseUnit?.size!!) {
                if (courseAssessmentBean.courseUnits?.courseUnit!![index].name.equals(resources.getString(R.string.assessment) + " " + intent.extras?.getString(Constants.COUNT)?.toInt()?.plus(1))) {
                    courseUnitList.add(courseAssessmentBean.courseUnits?.courseUnit!![index])
                }

            }
        } else {
            // for FP 2
            isFp1 = false
            for (index in 0 until courseAssessmentBean.courseUnits?.courseUnit?.size!!) {
                for (position in 0 until courseAssessmentBean.courseUnits?.courseUnit!![index].studyInstructions?.studyInstruction?.size!!) {

                    var findString = resources.getString(R.string.assessment) + " " + intent.extras?.getString(Constants.COUNT)?.toInt()?.plus(1) + " - Resources"
                    var assessmentTitle =
                        courseAssessmentBean.courseUnits?.courseUnit!![index].studyInstructions?.studyInstruction?.get(
                            position
                        )?.title
                    var isAssessmentResources = assessmentTitle?.indexOf(findString)
                    var isResources = assessmentTitle?.indexOf("Resources")

                    if (isAssessmentResources == 0 || isResources == 0) {
                        courseUnitList.add(courseAssessmentBean.courseUnits?.courseUnit!![index])
                    }

                    var findInstructionString = resources.getString(R.string.assessment) + " " + intent.extras?.getString(Constants.COUNT)?.toInt()?.plus(1) + " Instructions";
                    var assessmentTitleInstruction =
                        courseAssessmentBean.courseUnits?.courseUnit!![index].studyInstructions?.studyInstruction?.get(
                            position
                        )?.title
                    if (findInstructionString == assessmentTitleInstruction || assessmentTitleInstruction == "Assessment Instructions") {
                        courseUnitList.add(courseAssessmentBean.courseUnits?.courseUnit!![index])
                    }

                }
            }

        }

        // here we get filtered arrayList of course units now iterate study instruction list and remove if title content "["

        if(isFp1){

            for (index in 0 until courseUnitList.size) {
                for (position in 0 until courseUnitList[index]?.studyInstructions?.studyInstruction?.size!!) {
                    if (courseUnitList[index]?.studyInstructions?.studyInstruction?.get(position)?.title?.trim()?.get(0) != '[') {
                        courseAssesmentList?.add(courseUnitList[index]?.studyInstructions?.studyInstruction?.get(position)!!)
                    }
                }
            }
        }else {
            for (index in 0 until courseUnitList.size) {
                for (position in 0 until courseUnitList[index]?.studyInstructions?.studyInstruction?.size!!) {
                    if (courseUnitList[index]?.studyInstructions?.studyInstruction?.get(position)?.title?.trim()?.get(
                            0
                        ) != '['
                    ) {
                        val studyInstruction = CourseAssessmentBean.StudyInstruction()
                        studyInstruction.title = courseUnitList[index].name
                        studyInstruction.content =
                            courseUnitList[index]?.studyInstructions?.studyInstruction?.get(0)
                                ?.content
                        courseAssesmentList?.add(studyInstruction)
                    }
                }
            }
        }

        courseAssesmentList?.sortedWith(compareBy(CourseAssessmentBean.StudyInstruction::title))
        assessmentDetailListAdapter?.notifyDataSetChanged()
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        isInternetConnection = isConnected
        if(isConnected == true && isNetworkFailedDueToConnectivity!=null && isNetworkFailedDueToConnectivity==true)
        {
            isNetworkFailedDueToConnectivity = null
            if(this.getCurrentNetworkQueueSize()==0) {
                DialogUtils.releaseGenericDialog()
                callApiToCourseAssessments()
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

    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun initNetworkBroadcastReceiver()
    {

        ConnectivityReceiver.connectivityReceiverListener = this@AssessmentDetailListActivity
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
