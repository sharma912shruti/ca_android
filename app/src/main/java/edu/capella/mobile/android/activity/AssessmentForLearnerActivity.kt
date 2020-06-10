package edu.capella.mobile.android.activity

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Pair
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.adapters.AssessmentLearnerAdapter
import edu.capella.mobile.android.bean.AssessmentLearnerBean
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import edu.capella.mobile.android.network.NetworkService
import kotlinx.android.synthetic.main.activity_assessment_for_learner.*
import kotlinx.android.synthetic.main.activity_assessment_for_learner.networkLayout
import kotlinx.android.synthetic.main.toolbar_generic.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
class AssessmentForLearnerActivity: MenuActivity() /*BaseActivity()*/ ,  ConnectivityReceiver.ConnectivityReceiverListener{

    var assessmentLearnerBean:AssessmentLearnerBean? = null
    var assessmentAdapter : AssessmentLearnerAdapter? = null
    var assessmentList: ArrayList<AssessmentLearnerBean.FlexpathAssessmentsAndStatus>? = null

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
//        setContentView(R.layout.activity_assessment_for_learner)
        setContentChildView(R.layout.activity_assessment_for_learner,true)


        initValues()
        initialiseAdapter()
        OmnitureTrack.trackState("course:assessment:learner")
//        callApiToGetAssessments()
    }

    fun getAssessmentListSize(): Int {
        try {
            return assessmentList?.size!!
        } catch (t: Throwable) {
            return 0
        }
    }

    fun isGradedAssessment():Boolean {
        var isGraded = false
        for(index in 0 until assessmentList?.size!!){
            if (assessmentList!![index].status.equals(getString(R.string.evaluated))){
                isGraded = true
                break
            }
        }
        return isGraded
    }

    fun getGradedAssessmentIndex():Int{
        var gradedIndex = 0
        for(index in 0 until assessmentList?.size!!){
            if (assessmentList!![index].status.equals(getString(R.string.evaluated))){
                gradedIndex = index
                break
            }
        }
        return gradedIndex
    }

    private fun initValues(){
        genericTitleTxt.text = resources.getString(R.string.assessments)
        backButtonLayout.contentDescription = getString(R.string.ada_back_button) + getString(R.string.back)
        val swipeRefreshColor =  ContextCompat.getColor(this,R.color.checkBoxColor)
        this.assessmentPullToRefresh.setColorSchemeColors(swipeRefreshColor)
        assessmentPullToRefresh.setOnRefreshListener {
            assessmentPullToRefresh.isRefreshing = false
            callApiToGetAssessments()
        }
    }

    private fun initialiseAdapter(){
        assessmentList = ArrayList()
        assessment_list.layoutManager = LinearLayoutManager(this)
        assessmentAdapter = AssessmentLearnerAdapter(this,assessmentList!!,object :
            AssessmentLearnerAdapter.OnItemClickListener{
            override fun onItemClicked(value: AssessmentLearnerBean.FlexpathAssessmentsAndStatus,count:Int) {

                if(value.status.equals(Constants.HAS_BEEN_SUBMITTED) || value.status.equals(Constants.HAS_BEEN_SUBMITTED)){
                    OmnitureTrack.trackState("course:assessment:learner:link-to-CR")
                }
                val assessmentIntent = Intent(this@AssessmentForLearnerActivity, AssessmentAttemptDetailActivity::class.java)
                assessmentIntent.putExtra(Constants.COURSE_ID, intent.extras?.getString(Constants.COURSE_ID))
                assessmentIntent.putExtra(Constants.COUNT,count.toString())
                assessmentIntent.putExtra(Constants.USER_EMPLOYE_ID,intent.extras?.getString(Constants.USER_EMPLOYE_ID))
                assessmentIntent.putExtra(Constants.ID,value.id.toString())
                assessmentIntent.putExtra(Constants.COURSE_NUMBER_ID,intent.extras?.getString(Constants.COURSE_NUMBER_ID))
                assessmentIntent.putExtra(Constants.COURSE_PK,intent.getStringExtra(Constants.COURSE_PK))
                assessmentIntent.putExtra(Constants.COURSE_MESSAGE_LINK,intent.extras?.getString(Constants.COURSE_MESSAGE_LINK))
                assessmentIntent.putExtra(Constants.ASSESMENT_AND_STATUS,value)
                startActivity(assessmentIntent)
            }

        })
        assessment_list.adapter = assessmentAdapter
    }

    private fun callApiToGetAssessments(){
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val params = HashMap<String, Any>()
        params[NetworkConstants.TOKEN] = loginBean?.authData?.token!!

        val qStringParams = HashMap<String, Any>()
        qStringParams[NetworkConstants.COURSE_ID] = intent.extras?.getString(Constants.COURSE_ID)!!
        qStringParams[NetworkConstants.ACTION] = NetworkConstants.ACTION_FLEX_PATH_ASSESSMENT

        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.FLEX_PATH_ASSESSMENT_LIST + NetworkService.getQueryString(qStringParams),
            params,
            NetworkHandler.METHOD_POST,
            assessmentListListener,
            null
        )

        networkHandler.setSilentMode(false)
        networkHandler.execute()
    }

    private val assessmentListListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {
            try {

                Util.trace("flex path assessment first :  " + response.first)
                Util.trace("flex path assessment second :  " + response.second)
                // TODO handle course detail api responseset
                if (response.first == NetworkConstants.SUCCESS) {
                    val gson = Gson()
                    assessmentLearnerBean = gson.fromJson<AssessmentLearnerBean>(
                        response.second.toString(),
                        AssessmentLearnerBean::class.java)

                    assessmentList?.clear()

                    assessmentLearnerBean?.flexpathAssessmentsAndStatusResponse?.flexpathAssessmentsAndStatuss?.let { assessmentList?.addAll(it) }

                    setAssessmentCount()
                    assessmentList?.let { order(it) }
//                    assessmentList?.clear()
//                    assessmentList?.sortedWith(compareBy(AssessmentLearnerBean.FlexpathAssessmentsAndStatus::assessmentCount))
                    if(assessmentList?.size!! == 0){
                        no_data.visibility = View.VISIBLE
                        assessment_list.visibility =View.GONE
                    }else{

                        OmnitureTrack.trackAction("course:assessment:learner:list")
                        no_data.visibility = View.GONE
                        assessment_list.visibility =View.VISIBLE
                    }
                    saveFacultyLearner(assessmentList!!)
                    assessmentAdapter?.notifyDataSetChanged()
                } else {
                    DialogUtils.showGenericErrorDialog(this@AssessmentForLearnerActivity)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                DialogUtils.showGenericErrorDialog(this@AssessmentForLearnerActivity)
            }
        }
    }

    private fun setAssessmentCount(){
        for (index in 0 until assessmentList?.size!!){
            val title = assessmentList!![index].assignmentTitle?.let {
                HtmlCompat.fromHtml(
                    it,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
            val count =  title?.subSequence(title.indexOf("u")+1,title.indexOf("a"))
            val firstLettr = count?.indexOf("0")
            if(firstLettr == 0 ){
                val count :Int  = Integer.parseInt(title?.subSequence(title.indexOf("u")+2,title.indexOf("a")).toString())
                assessmentList!![index].assessmentCount = count

            }else{
                val count :Int  = Integer.parseInt(title?.subSequence(title.indexOf("u")+1,title.indexOf("a")).toString())
                assessmentList!![index].assessmentCount = count
            }


        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        isInternetConnection = isConnected
        if(isConnected == true && isNetworkFailedDueToConnectivity!=null && isNetworkFailedDueToConnectivity==true)
        {
            isNetworkFailedDueToConnectivity = null
            if(this.getCurrentNetworkQueueSize()==0) {
                DialogUtils.releaseGenericDialog()
                callApiToGetAssessments()
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

        ConnectivityReceiver.connectivityReceiverListener = this@AssessmentForLearnerActivity
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
        callApiToGetAssessments()

    }
    private fun saveFacultyLearner(responselist :ArrayList<AssessmentLearnerBean.FlexpathAssessmentsAndStatus>) {
        try {
            val   couseID=  intent.extras?.getString(Constants.COURSE_ID)
            val employeeId=intent.extras?.getString(Constants.USER_EMPLOYE_ID)
            if(couseID!=null) {

                if (responselist.isNotEmpty()) {

                    var SHARED_COURSE_ID =
                        couseID +""+ employeeId
                    val SHARRED_LEARNER_DATE =
                        SHARED_COURSE_ID + "" + PreferenceKeys.FP_LEARNER_FACULTY_DATE
                    val SHARRED_LEARNER_READ =
                        SHARED_COURSE_ID + "" + PreferenceKeys.FP_LEARNER_FACULTY_READ


                    var Datelist =
                        Util.stringToWords(Preferences.getArrayList(SHARRED_LEARNER_DATE)!!)

                    var fpLearnerDatelist: ArrayList<String>? = null
                    if (Datelist.isEmpty()) {
                        fpLearnerDatelist = ArrayList<String>()
                    } else {
                        fpLearnerDatelist = Datelist as ArrayList
                    }

                    var Readlist =
                        Util.stringToWords(Preferences.getArrayList(SHARRED_LEARNER_READ)!!)


                    var fpLearnerReadlist: ArrayList<String>? = null

                    if (Readlist.isEmpty()) {
                        fpLearnerReadlist = ArrayList<String>()
                    } else {
                        fpLearnerReadlist = Readlist as ArrayList
                    }

                    if (fpLearnerDatelist.size > 0) {

                        var firstDate = fpLearnerDatelist[0]
                        var lastDate = fpLearnerDatelist[fpLearnerDatelist.size - 1]

                        firstDate = firstDate.replace("[", "")
                        lastDate = lastDate.replace("]", "")
                        lastDate = lastDate.replace("[", "")

                        fpLearnerDatelist.set(0, firstDate)
                        fpLearnerDatelist.set(fpLearnerDatelist.size - 1, lastDate)
                    }

                    if (fpLearnerReadlist!!.size > 0) {
                        var firstRead = fpLearnerReadlist?.get(0)!!
                        var lastRead = fpLearnerReadlist[fpLearnerReadlist.size - 1]


                        firstRead = firstRead.replace("[", "")
                        lastRead = lastRead.replace("]", "")
                        lastRead = lastRead.replace("[", "")

                        fpLearnerReadlist[0] = firstRead
                        fpLearnerReadlist[fpLearnerReadlist.size - 1] = lastRead

                    }

                    val learnerlistDate = ArrayList<String>()
                    val learnerlistRead = ArrayList<String>()

                    var difference = responselist.size - fpLearnerDatelist.size

                    for (i in responselist.indices) {
                        var date: String = ""

                        if (responselist[i].pccpEvaluatedDate != null) {
                            var longg = Util.getDateToLong(
                                responselist[i].pccpEvaluatedDate!!
                            )


//                            date = Util.getDate(longg!!, Constants.DATE_FORMAT_SEC)!!
                            date=responselist[i].id.toString()
                        }
                        else if (responselist[i].pccpSubmittedDate != null)
                        {
                            var longg = Util.getDateToLong(
                                responselist[i].pccpSubmittedDate!!
                            )


//                            date = Util.getDate(longg!!, Constants.DATE_FORMAT_SEC)!!
                            date=responselist[i].id.toString()
                        }
                        if (responselist.size > i + difference) {

                            if (responselist[i + difference].pccpEvaluatedDate != null) {

//                                var longgDate = Util.getDateToLong(
//                                    responselist[i + difference].pccpEvaluatedDate!!
//                                )
//                                var checkdate =
//                                    Util.getDate(
//                                        longgDate!!,
//                                        Constants.DATE_FORMAT_SEC
//                                    )!!


                                if (fpLearnerDatelist!!.size > 0) {
//
                                    if (fpLearnerDatelist!!.size > i) {


                                        for( serverListValue in responselist)
                                        {

                                            if(serverListValue.pccpEvaluatedDate!=null)
                                            {

                                                var longgDate = Util.getDateToLong(
                                                    serverListValue.pccpEvaluatedDate!!
                                                )
                                                var checkdateServer =
                                                    Util.getDate(
                                                        longgDate!!,
                                                        Constants.DATE_FORMAT_SEC
                                                    )!!

                                                checkdateServer=serverListValue.id!!
                                                if ((fpLearnerDatelist[i].trim()) == checkdateServer.trim()) {

                                                    if ((fpLearnerReadlist[i].trim()) == Constants.READ.trim()) {
                                                        serverListValue.setRead(Constants.READ)
                                                    }
                                                }

                                                if(serverListValue.attempts!=null && serverListValue!!.attempts!!.attempts!=null)
                                                {



                                                    val localLearnerAttempsRead = ArrayList<String>()

                                                    val localLearnerAttempsDate = ArrayList<String>()

                                                    val SHARRED_ATTEMP_READ =
                                                        SHARED_COURSE_ID + "" + checkdateServer + "" + PreferenceKeys.FP_LEARNER_ATTEMP_READ

                                                    val SHARRED_ATTEMP_DATE =
                                                        SHARED_COURSE_ID + "" + checkdateServer + "" + PreferenceKeys.FP_LEARNER_ATTEMP_DATA

                                                    var attempListRead =
                                                        Util.stringToWords(
                                                            Preferences.getArrayList(
                                                                SHARRED_ATTEMP_READ
                                                            )!!
                                                        )


                                                    var fpLearnerAttemplist: ArrayList<String>? = null
                                                    if (attempListRead.isEmpty()) {
                                                        fpLearnerAttemplist = ArrayList<String>()
                                                    } else {
                                                        fpLearnerAttemplist = attempListRead as ArrayList
                                                    }

                                                    if (fpLearnerAttemplist!!.size > 0) {
                                                        var firstRead = fpLearnerAttemplist?.get(0)!!
                                                        var lastRead =
                                                            fpLearnerAttemplist[fpLearnerAttemplist.size - 1]

                                                        firstRead = firstRead.replace("[", "")
                                                        lastRead = lastRead.replace("]", "")
                                                        lastRead = lastRead.replace("[", "")

                                                        fpLearnerAttemplist[0] = firstRead
                                                        fpLearnerAttemplist[fpLearnerAttemplist.size - 1] =
                                                            lastRead
                                                    }

                                                    var attempListDate =
                                                        Util.stringToWords(
                                                            Preferences.getArrayList(
                                                                SHARRED_ATTEMP_DATE
                                                            )!!
                                                        )


                                                    var fpDateAttemplist: ArrayList<String>? = null
                                                    if (attempListDate.isEmpty()) {
                                                        fpDateAttemplist = ArrayList<String>()
                                                    } else {
                                                        fpDateAttemplist = attempListDate as ArrayList
                                                    }

                                                    if (fpDateAttemplist!!.size > 0) {
                                                        var firstDate = fpDateAttemplist?.get(0)!!
                                                        var lastDate =
                                                            fpDateAttemplist[fpDateAttemplist.size - 1]

                                                        firstDate = firstDate.replace("[", "")
                                                        lastDate = lastDate.replace("]", "")
                                                        lastDate = lastDate.replace("[", "")

                                                        fpDateAttemplist[0] = firstDate
                                                        fpDateAttemplist[fpDateAttemplist.size - 1] =
                                                            lastDate
                                                    }


                                                    val serverAttempsList =
                                                        serverListValue.attempts!!.attempts

                                                    var attempsDifference =
                                                        serverAttempsList!!.size - fpLearnerAttemplist.size

                                                    if(attempsDifference>=0) {
                                                        for (m in serverAttempsList.indices) {

                                                            var serverSatusDate=""


                                                            if(serverAttempsList[m].status.equals(Constants.GRADED))
                                                            {
                                                                var longgDate = Util.getDateToLong(
                                                                    serverAttempsList[m].gradedDate!!
                                                                )
                                                                serverSatusDate  =
                                                                    Util.getDate(
                                                                        longgDate!!,
                                                                        Constants.DATE_FORMAT_SEC
                                                                    )!!
                                                            }
                                                            else if(serverAttempsList[m].status.equals(Constants.SUBMITTED))
                                                            {
                                                                var longgDate = Util.getDateToLong(
                                                                    serverAttempsList[m].submittedDate!!
                                                                )
                                                                serverSatusDate  =
                                                                    Util.getDate(
                                                                        longgDate!!,
                                                                        Constants.DATE_FORMAT_SEC
                                                                    )!!

                                                            }

                                                            if (serverAttempsList.size > m + attempsDifference) {

                                                                if(serverSatusDate.toString().trim() == fpDateAttemplist[m].toString().trim()) {
                                                                    if (fpLearnerAttemplist[m].trim() == Constants.READ.trim()) {
                                                                        serverListValue.setRead(
                                                                            Constants.READ
                                                                        )
                                                                        serverAttempsList[m].setRead(
                                                                            Constants.READ
                                                                        )

                                                                    } else {
                                                                        serverListValue.setRead(
                                                                            Constants.UNREAD
                                                                        )


                                                                    }
                                                                }else {
                                                                    serverListValue.setRead(
                                                                        Constants.UNREAD
                                                                    )


                                                                }

                                                            }

                                                            localLearnerAttempsRead.add(
                                                                serverAttempsList[m].getRead()!!
                                                            )
                                                            localLearnerAttempsDate.add(serverSatusDate)


                                                        }
                                                    }


                                                    if (attempsDifference != 0) {
                                                        serverListValue.setRead(Constants.UNREAD)
                                                    }
                                                    if(localLearnerAttempsRead.contains(Constants.UNREAD))
                                                    {
                                                        serverListValue.setRead(
                                                            Constants.UNREAD
                                                        )
                                                    }

                                                    Preferences.addArrayList(
                                                        SHARRED_ATTEMP_READ,
                                                        localLearnerAttempsRead.toString()
                                                    )

                                                    Preferences.addArrayList(
                                                        SHARRED_ATTEMP_DATE,
                                                        localLearnerAttempsDate.toString()
                                                    )

                                                }
                                            }

                                        }
                                    }

                                }
                            }

                            else if (responselist[i + difference].pccpSubmittedDate != null) {

//                                var longgDate = Util.getDateToLong(
//                                    responselist[i + difference].pccpSubmittedDate!!
//                                )
//                                var checkdate =
//                                    Util.getDate(
//                                        longgDate!!,
//                                        Constants.DATE_FORMAT_SEC
//                                    )!!


                                if (fpLearnerDatelist!!.size > 0) {
//
                                    if (fpLearnerDatelist!!.size > i) {

                                        for( serverListValue in responselist) {

                                            if (serverListValue.pccpSubmittedDate != null) {

                                                var longgDate = Util.getDateToLong(
                                                    serverListValue.pccpSubmittedDate!!
                                                )
//                                                    var checkdateServer =
//                                                        Util.getDate(
//                                                            longgDate!!,
//                                                            Constants.DATE_FORMAT_SEC
//                                                        )!!

                                                var checkdateServer=serverListValue.id!!
                                                if ((fpLearnerDatelist[i].trim()) == checkdateServer.trim()) {

                                                    if ((fpLearnerReadlist[i].trim()) == Constants.READ.trim()) {
                                                        serverListValue.setRead(Constants.READ)
                                                    }
                                                }

                                                if(serverListValue.attempts!=null && serverListValue!!.attempts!!.attempts!=null) {


                                                    val localLearnerAttempsRead =
                                                        ArrayList<String>()

                                                    val localLearnerAttempsDate =
                                                        ArrayList<String>()

                                                    val SHARRED_ATTEMP_READ =
                                                        SHARED_COURSE_ID + "" + checkdateServer + "" + PreferenceKeys.FP_LEARNER_ATTEMP_READ

                                                    val SHARRED_ATTEMP_DATE =
                                                        SHARED_COURSE_ID + "" + checkdateServer + "" + PreferenceKeys.FP_LEARNER_ATTEMP_DATA



                                                    var attempListRead =
                                                        Util.stringToWords(
                                                            Preferences.getArrayList(
                                                                SHARRED_ATTEMP_READ
                                                            )!!
                                                        )


                                                    var fpLearnerAttemplist: ArrayList<String>? =
                                                        null
                                                    if (attempListRead.isEmpty()) {
                                                        fpLearnerAttemplist =
                                                            ArrayList<String>()
                                                    } else {
                                                        fpLearnerAttemplist =
                                                            attempListRead as ArrayList
                                                    }

                                                    if (fpLearnerAttemplist!!.size > 0) {
                                                        var firstRead =
                                                            fpLearnerAttemplist?.get(0)!!
                                                        var lastRead =
                                                            fpLearnerAttemplist[fpLearnerAttemplist.size - 1]

                                                        firstRead = firstRead.replace("[", "")
                                                        lastRead = lastRead.replace("]", "")
                                                        lastRead = lastRead.replace("[", "")

                                                        fpLearnerAttemplist[0] = firstRead
                                                        fpLearnerAttemplist[fpLearnerAttemplist.size - 1] =
                                                            lastRead
                                                    }

                                                    var attempListDate =
                                                        Util.stringToWords(
                                                            Preferences.getArrayList(
                                                                SHARRED_ATTEMP_DATE
                                                            )!!
                                                        )

                                                    var fpDateAttemplist: ArrayList<String>? = null
                                                    if (attempListDate.isEmpty()) {
                                                        fpDateAttemplist = ArrayList<String>()
                                                    } else {
                                                        fpDateAttemplist = attempListDate as ArrayList
                                                    }

                                                    if (fpDateAttemplist!!.size > 0) {
                                                        var firstDate = fpDateAttemplist?.get(0)!!
                                                        var lastDate =
                                                            fpDateAttemplist[fpDateAttemplist.size - 1]

                                                        firstDate = firstDate.replace("[", "")
                                                        lastDate = lastDate.replace("]", "")
                                                        lastDate = lastDate.replace("[", "")

                                                        fpDateAttemplist[0] = firstDate
                                                        fpDateAttemplist[fpDateAttemplist.size - 1] =
                                                            lastDate
                                                    }

                                                    val serverAttempsList =
                                                        serverListValue.attempts!!.attempts

                                                    var attempsDifference =
                                                        serverAttempsList!!.size - fpLearnerAttemplist.size

                                                    if(attempsDifference>=0) {

                                                        for (m in serverAttempsList.indices) {

                                                            var serverSatusDate=""


                                                            if(serverAttempsList[m].status.equals(Constants.GRADED))
                                                            {
                                                                var longgDate = Util.getDateToLong(
                                                                    serverAttempsList[m].gradedDate!!
                                                                )
                                                                serverSatusDate  =
                                                                    Util.getDate(
                                                                        longgDate!!,
                                                                        Constants.DATE_FORMAT_SEC
                                                                    )!!
                                                            }
                                                            else if(serverAttempsList[m].status.equals(Constants.SUBMITTED))
                                                            {
                                                                var longgDate = Util.getDateToLong(
                                                                    serverAttempsList[m].submittedDate!!
                                                                )
                                                                serverSatusDate  =
                                                                    Util.getDate(
                                                                        longgDate!!,
                                                                        Constants.DATE_FORMAT_SEC
                                                                    )!!

                                                            }

                                                            if (serverAttempsList.size > m + attempsDifference) {
                                                                if(serverSatusDate.toString().trim() == fpDateAttemplist[m].toString().trim()) {
                                                                    if (fpLearnerAttemplist[m].trim() == Constants.READ.trim()) {
                                                                        serverListValue.setRead(
                                                                            Constants.READ
                                                                        )
                                                                        serverAttempsList[m].setRead(
                                                                            Constants.READ
                                                                        )

                                                                    } else {
                                                                        serverListValue.setRead(
                                                                            Constants.UNREAD
                                                                        )

                                                                    }
                                                                }
                                                                else {
                                                                    serverListValue.setRead(
                                                                        Constants.UNREAD
                                                                    )

                                                                }

                                                            }

                                                            localLearnerAttempsRead.add(
                                                                serverAttempsList[m].getRead()!!
                                                            )

                                                            localLearnerAttempsDate.add(serverSatusDate)

                                                        }
                                                    }

                                                    if (attempsDifference != 0) {
                                                        serverListValue.setRead(Constants.UNREAD)
                                                    }
                                                    if(localLearnerAttempsRead.contains(Constants.UNREAD))
                                                    {
                                                        serverListValue.setRead(
                                                            Constants.UNREAD
                                                        )
                                                    }


                                                    Preferences.addArrayList(
                                                        SHARRED_ATTEMP_READ,
                                                        localLearnerAttempsRead.toString()
                                                    )

                                                    Preferences.addArrayList(
                                                        SHARRED_ATTEMP_DATE,
                                                        localLearnerAttempsDate.toString()
                                                    )
                                                }

                                            }
                                        }
                                    }

                                }
                            }
                        }
                        learnerlistDate.add(date)
                        learnerlistRead.add(responselist[i].getRead()!!.toString().trim())
                    }

                    Preferences.addArrayList(
                        SHARRED_LEARNER_DATE,
                        learnerlistDate.toString()
                    )
                    Preferences.addArrayList(
                        SHARRED_LEARNER_READ,
                        learnerlistRead.toString()
                    )

                }


            }

        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }

    }

    private fun order(assessmnetList : List<AssessmentLearnerBean.FlexpathAssessmentsAndStatus?>) {

        Collections.sort(assessmnetList,object :Comparator<AssessmentLearnerBean.FlexpathAssessmentsAndStatus?>{

            override fun compare(firstBean: AssessmentLearnerBean.FlexpathAssessmentsAndStatus?,
                                 secondBean: AssessmentLearnerBean.FlexpathAssessmentsAndStatus?): Int {

                val x1: Int = firstBean?.assessmentCount!!
                val x2: Int = secondBean?.assessmentCount!!

                val sComp =  x1.compareTo(x2)
                return sComp

            }

        })
    }
}
