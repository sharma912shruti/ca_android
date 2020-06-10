package edu.capella.mobile.android.activity

import android.content.IntentFilter
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.utils.Constants.ASSESSTMENT
import edu.capella.mobile.android.utils.Constants.BLACK_BOARD_DOMAIN
import edu.capella.mobile.android.utils.Constants.DATE_VALUE
import edu.capella.mobile.android.utils.Constants.DAYS_COUNT
import edu.capella.mobile.android.utils.Constants.DESCRIPTION
import edu.capella.mobile.android.utils.Constants.THREE_DAY_AGO
import edu.capella.mobile.android.utils.Constants.TWO_DAY_AGO
import edu.capella.mobile.android.utils.Constants.VIEW_NEED_GRADING
import edu.capella.mobile.android.utils.Constants.WITH_IN_AGO
import edu.capella.mobile.android.utils.Util.getDateAgo
import edu.capella.mobile.android.bean.AssesstmentFacultyBean
import edu.capella.mobile.android.bean.CourseDetailBean
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.HubbleNetworkConstants
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import edu.capella.mobile.android.task.StickyInfoGrabber
import kotlinx.android.synthetic.main.activity_assessment_for_faculty.*
import kotlinx.android.synthetic.main.toolbar_announcement.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * Class Name.kt : class description goes here
 *
 * @author  :  SSHARMA45
 * @version :  1.0
 * @since   :  4/13/2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
class AssessmentForFacultyActivity : MenuActivity()/*BaseActivity()*/, NetworkListener,
    ConnectivityReceiver.ConnectivityReceiverListener, View.OnClickListener {

    var courseID = "";
    var userEmployeeId = "";

    private var countThreeDay: Int = 0
    private var countTwoDay: Int = 0
    private var assignmentsBean: AssesstmentFacultyBean? = null
    private var courseDetailData: CourseDetailBean? = null
    private var countOneDay: Int = 0
    private var checkTwoDay: Boolean = true
    private var checkMoreDay: Boolean = true
    private var checkOneDay: Boolean = true
    private var connectivityReceiver: ConnectivityReceiver? = null
    var assignmentsList: ArrayList<AssesstmentFacultyBean.CourseAssignment> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setContentView(R.layout.activity_assessment_for_faculty)
        setContentChildView(R.layout.activity_assessment_for_faculty,true)

        this.courseID = intent.getStringExtra(Constants.COURSE_ID)
        userEmployeeId=intent.extras?.getString(Constants.USER_EMPLOYE_ID)!!
//        courseDetailData = intent.getSerializableExtra(Constants.ANNOUCEMENT_LIST_DATA) as CourseDetailBean

        val   couseID=  intent.extras?.getString(Constants.COURSE_ID)
        initValues()
        OmnitureTrack.trackState("course:assessments-faculty-view")
//        showAssessmentData()
    }

    private fun initValues() {
        headerTxt.text = resources.getString(R.string.assessment_status)
        view_grades_and_assessment.setOnClickListener(this)
        callAssignmentsApi(this.courseID)
        backButtonLl.setOnClickListener{finishActivity()}
        backButtonLl.contentDescription = getString(R.string.ada_back_button) +  getString(R.string.back)

        val swipeRefreshColor =  ContextCompat.getColor(this@AssessmentForFacultyActivity,R.color.checkBoxColor)
        this.facultySwipeToRefresh.setColorSchemeColors(swipeRefreshColor)
        facultySwipeToRefresh.setOnRefreshListener {
            checkTwoDay = true
            checkMoreDay = true
            checkOneDay = true
            countThreeDay = 0
            countTwoDay = 0
            countOneDay = 0
            addAssessment.removeAllViews()
            callAssignmentsApi(this.courseID)
            facultySwipeToRefresh.isRefreshing = false;
        }
    }

    fun getAssessmentListSize(): Int {
        try {
            return assignmentsList.size
        } catch (t: Throwable) {
            return 0
        }
    }

    private fun callAssignmentsApi(courseId: String) {
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val authHeader = NetworkHandler.getAuthorizationHeader()
        val hubbleHeaders = NetworkHandler.getHeadersForHubbleRequest(
            loginBean?.authData?.token,
            HubbleNetworkConstants.getAcceptHeaderValue(HubbleNetworkConstants.ASSESSMENT_FACULTY_LIST)
        )
        val finalHeaders = NetworkHandler.meargeHeaders(authHeader, hubbleHeaders)

        val params = HashMap<String, Any>()

        //val stickyHeader : java.util.HashMap<String, Any?>? = NetworkHandler.getStickySessionHeader(
        //   Preferences.getValue(PreferenceKeys.MULESOFT_ACCESS_TOKEN))

        var assignmentsUrl = HubbleNetworkConstants.getUrl(
            HubbleNetworkConstants.ASSESSMENT_FACULTY_LIST, "{{employeeId}}",
            "" + loginBean?.authData?.employeeId!!.value
        )

        val assignmentsListUrl =
            HubbleNetworkConstants.getUrl(assignmentsUrl, "{{courseId}}", "" + courseID)
        Util.trace("Assignments URL  :" + assignmentsUrl)


        Util.trace("Assignments Url", assignmentsListUrl)

        val networkHandler = NetworkHandler(
            this,
            assignmentsListUrl,
            params,
            NetworkHandler.METHOD_GET,
            this,
            finalHeaders
        )

        networkHandler.execute()

    }
    override fun onNetworkResponse(response: Pair<String, Any>) {

        try {

        Util.trace("Assignments : " + response.second)

        if (response.first == NetworkConstants.SUCCESS) {
            val gson = Gson()
            assignmentsBean = gson.fromJson<AssesstmentFacultyBean>(
                response.second.toString(),
                AssesstmentFacultyBean::class.java
            )
            updateAssignmentsList(assignmentsBean)

        } else {
            DialogUtils.showGenericErrorDialog(this)
        }
        }catch (e:Exception)
        {
            e.printStackTrace()
            DialogUtils.showGenericErrorDialog(this)
        }
    }


    private fun updateAssignmentsList(assignmentsBean: AssesstmentFacultyBean?) {

        try {
            Util.trace("Update Assignments List method started")

            val assignmentsListBean = assignmentsBean

            var listNew =
                assignmentsListBean?.courseAssignment as ArrayList<AssesstmentFacultyBean.CourseAssignment>

//            var newSortedList = listNew.sortedWith(compareBy(AssesstmentFacultyBean.CourseAssignment::submittedDateTime, AssesstmentFacultyBean.CourseAssignment::submittedDateTime))

            assignmentsList.clear()
            assignmentsList.addAll(listNew)

            checkReadStatus(assignmentsList)

            showAssessmentData(assignmentsList)



        } catch (t: Throwable) {
            Util.trace("Assignments error : $t")
            t.printStackTrace()
        }
    }

    private fun showAssessmentData(list: ArrayList<AssesstmentFacultyBean.CourseAssignment>) {
        val titleList = ArrayList<String>()

        val threeMoreList = ArrayList<HashMap<String, Any>>()
        val twoAgoList = ArrayList<HashMap<String, Any>>()
        val withInList = ArrayList<HashMap<String, Any>>()

        val threeMoreTxt = THREE_DAY_AGO
        val twoMoreTxt = TWO_DAY_AGO
        val withInTxt = WITH_IN_AGO

        if (list.size > 0) {
            for (value in list) {
                var days = getDateAgo(value.submittedDateTime!!)
                if (days <= 1) {
                    if (checkOneDay) {
                        titleList.add(WITH_IN_AGO)
                        checkOneDay = false
                    }

                    if(withInList.size>0)
                    {
                        var map=   withInList[withInList.size-1]
                        if( map[DESCRIPTION]!=value.learnerFullName)
                        {
                            countOneDay=0
                        }
                    }

                    countOneDay += 1;

                    val assessmentCount=value.title!!.substring(2,4)
                    countOneDay=assessmentCount.toInt()

//                    val finalString= assessmentCount.toString()
//
//                    val checkFirstChar=finalString.toString().get(0)
//
//                    if(checkFirstChar!=null)
//                    {
//                        try {
//
//                            if(checkFirstChar.toString().toInt()==0)
//                            {
//                                if(assessmentCount!=null)
//                                {
//                                    countOneDay=assessmentCount.toInt()
//                                }
//
//                            }
//                            else
//                            {
//                                if(assessmentCount.get(1).toString()!=null)
//                                {
//                                    val charString=assessmentCount.get(1)
//                                    countOneDay=assessmentCount.toInt()
//                                }
//                            }
//                        }catch (e: Exception)
//                        {
//
//                        }
//                    }



                    var assessetment =
                        resources.getString(R.string.assessment) + " " + countOneDay + ", " + resources.getString(
                            R.string.attempt
                        )+" "+ value!!.attempts!!.attempt?.get(value!!.attempts!!.attempt?.size!!-1)!!.attemptName
                    var longg = Util.getDateToLong(
                        value.submittedDateTime!!
                    )

//                    var date=""
//
//                    date = Util.getDate(
//                        longg!!,
//                        Constants.DATE_FORMAT_AM_PM
//                    ) + " " +resources.getString(R.string.central)


                    var dateValue=""
//                    if(resources.getString(R.string.america_chicago) == TimeZone.getDefault().id) {
                    dateValue = Util.getDate(
                        longg!!,
                        Constants.DATE_FORMAT_AM_PM
                    ) + " " +resources.getString(R.string.central)

                    val finaldate= dateValue.split("\\s".toRegex())

                    val date=finaldate[0]+"    "+finaldate[1]+" "+finaldate[2]+" "+finaldate[3]

                    val hashMap: HashMap<String, Any> =
                        HashMap<String, Any>() //define empty hashmap
                    hashMap.put(DESCRIPTION, value.learnerFullName.toString())
                    hashMap.put(ASSESSTMENT, assessetment)
                    hashMap.put(DATE_VALUE, date)
                    hashMap.put(DAYS_COUNT, getDateAgo(value.submittedDateTime!!))
                    hashMap.put(Constants.READ_VALUE,value.getRead()!!)

                    withInList.add(hashMap)

                } else if (days <= 2) {
                    if (checkTwoDay) {
                        titleList.add(TWO_DAY_AGO)
                        checkTwoDay = false
                    }

                    if(twoAgoList.size>0)
                    {
                        var map=   twoAgoList[twoAgoList.size-1]
                        if( map[DESCRIPTION]!=value.learnerFullName)
                        {
                            countTwoDay=0
                        }
                    }

                    countTwoDay += 1;

                    val assessmentCount=value.title!!.substring(2,4)
                    countTwoDay=assessmentCount.toInt()

//                    val finalString= assessmentCount.toString()
//
//                    val checkFirstChar=finalString.toString().get(0)
//
//                    if(checkFirstChar!=null)
//                    {
//                        try {
//
//                            if(checkFirstChar.toString().toInt()==0)
//                            {
//                                if(assessmentCount!=null)
//                                {
//                                    countTwoDay=assessmentCount.toInt()
//                                }
//
//                            }
//                            else
//                            {
//                                if(assessmentCount.get(1).toString()!=null)
//                                {
//                                    val charString=assessmentCount.get(1)
//                                    countTwoDay=assessmentCount.toInt()
//                                }
//                            }
//                        }catch (e: Exception)
//                        {
//
//                        }
//                    }

                    var assessetment =
                        resources.getString(R.string.assessment) + " " + countTwoDay + ", " + resources.getString(
                            R.string.attempt
                        )+" " + value.attempts!!.attempt?.get(value!!.attempts!!.attempt?.size!!-1)!!.attemptName
                    var longg = Util.getDateToLong(
                        value.submittedDateTime!!
                    )

//                    var date=""
//                    date = Util.getDate(
//                        longg!!,
//                        Constants.DATE_FORMAT_AM_PM
//                    ) + " " +resources.getString(R.string.central)


                    var dateValue=""
//                    if(resources.getString(R.string.america_chicago) == TimeZone.getDefault().id) {
                    dateValue = Util.getDate(
                        longg!!,
                        Constants.DATE_FORMAT_AM_PM
                    ) + " " +resources.getString(R.string.central)

                    val finaldate= dateValue.split("\\s".toRegex())

                    val date=finaldate[0]+"    "+finaldate[1]+" "+finaldate[2]+" "+finaldate[3]


                    val hashMap: HashMap<String, Any> =
                        HashMap<String, Any>() //define empty hashmap
                    hashMap.put(DESCRIPTION, value.learnerFullName.toString())
                    hashMap.put(ASSESSTMENT, assessetment)
                    hashMap.put(DATE_VALUE, date)
                    hashMap.put(DAYS_COUNT, getDateAgo(value.submittedDateTime!!))
                    hashMap.put(Constants.READ_VALUE,value.getRead()!!)
                    twoAgoList.add(hashMap)


                } else {
                    if (checkMoreDay) {
                        titleList.add(THREE_DAY_AGO)
                        checkMoreDay = false
                    }

                    if(threeMoreList.size>0)
                    {
                        var map=   threeMoreList[threeMoreList.size-1]
                        if( map[DESCRIPTION]!=value.learnerFullName)
                        {
                            countThreeDay=0
                        }
                    }

                    countThreeDay += 1;



                    val assessmentCount=value.title!!.substring(2,4)

                    countThreeDay=assessmentCount.toInt()


//                    val finalString= assessmentCount.toString()
//
//                    val checkFirstChar=finalString.toString().get(0)
//
//                    if(checkFirstChar!=null)
//                    {
//                        try {
//
//                            if(checkFirstChar.toString().toInt()==0)
//                            {
//                                if(assessmentCount!=null)
//                                {
//                                    countThreeDay=assessmentCount.toInt()
//                                }
//
//                            }
//                            else
//                            {
//                                if(assessmentCount.get(1).toString()!=null)
//                                {
//                                    val charString=assessmentCount.get(1)
//                                    countThreeDay=assessmentCount.toInt()
//                                }
//                            }
//                        }catch (e: Exception)
//                        {
//
//                        }
//                    }

                    var assessetment =
                        resources.getString(R.string.assessment) + " " + countThreeDay + ", " + resources.getString(
                            R.string.attempt
                        )+" " + value.attempts!!.attempt?.get(value!!.attempts!!.attempt?.size!!-1)!!.attemptName
                    var longg = Util.getDateToLong(
                        value.submittedDateTime!!
                    )
                    var dateValue=""
//                    if(resources.getString(R.string.america_chicago) == TimeZone.getDefault().id) {
                    dateValue = Util.getDate(
                        longg!!,
                        Constants.DATE_FORMAT_AM_PM
                    ) + " " +resources.getString(R.string.central)

                    val finaldate= dateValue.split("\\s".toRegex())

                    val date=finaldate[0]+"    "+finaldate[1]+" "+finaldate[2]+" "+finaldate[3]


                    val hashMap: HashMap<String, Any> =
                        HashMap<String, Any>() //define empty hashmap
                    hashMap.put(DESCRIPTION, value.learnerFullName.toString())
                    hashMap.put(ASSESSTMENT, assessetment)
                    hashMap.put(DATE_VALUE, date)
                    hashMap.put(DAYS_COUNT, getDateAgo(value.submittedDateTime!!))
                    hashMap.put(Constants.READ_VALUE,value.getRead()!!)

                    threeMoreList.add(hashMap)
                }

            }
        } else {
            headerMessageTxt.text = resources.getString(R.string.assessment_submitted)
        }


        val inflater = LayoutInflater.from(this)
//        for (value in titleList) {
//            when (value) {
//                THREE_DAY_AGO -> {
//                    for (x in threeMoreList.indices) {
//                        var headerView: View? = null
//                        var hashMapValue = threeMoreList[x]
//
//                        if (x == 0) {
//                            headerView =
//                                inflater.inflate(R.layout.row_assessment_faculty_header, null)
//                            val titleTxt = headerView!!.findViewById(R.id.titleTxt) as TextView
//
//                            titleTxt.text = THREE_DAY_AGO
//
//
//                        }
//                        val childInflater = LayoutInflater.from(this)
//                        val childView =
//                            childInflater.inflate(R.layout.row_assessment_for_faculty, null)
//                        val learner_name = childView!!.findViewById(R.id.learner_name) as TextView
//                        val assessment_and_attempt_detail =
//                            childView!!.findViewById(R.id.assessment_and_attempt_detail) as TextView
//                        val date_and_time = childView!!.findViewById(R.id.date_and_time) as TextView
//                        val pastThreeDayLayout=childView.findViewById(R.id.pastThreeDayLayout) as LinearLayout
//                        if(hashMapValue[DAYS_COUNT].toString().toInt()>3)
//                        {
//                            pastThreeDayLayout.visibility=View.GONE
//                        }
//                        else
//                        {
//                            if(hashMapValue[Constants.READ_VALUE]==Constants.READ)
//                            {
//                                pastThreeDayLayout.visibility=View.GONE
//                            }
//                        }
//
//                        learner_name.text = hashMapValue.get(DESCRIPTION).toString()
//                        assessment_and_attempt_detail.text =
//                            hashMapValue.get(ASSESSTMENT).toString()
//
//                        assessment_and_attempt_detail.contentDescription=hashMapValue.get(ASSESSTMENT).toString()
//
//                        date_and_time.text = hashMapValue.get(DATE_VALUE).toString()
//
//
//                        if (x == 0) {
//
//                            if (headerView != null)
//                                addAssessment.addView(headerView)
//
//                        }
//                        addAssessment.addView(childView)
//                    }
//
//                }
//                TWO_DAY_AGO -> {
//
//                    for (x in twoAgoList.indices) {
//                        var headerView: View? = null
//                        var hashMapValue = twoAgoList[x]
//
//                        if (x == 0) {
//                            headerView =
//                                inflater.inflate(R.layout.row_assessment_faculty_header, null)
//                            val titleTxt = headerView!!.findViewById(R.id.titleTxt) as TextView
//
//                            titleTxt.text = TWO_DAY_AGO
//
//
//                        }
//                        val childInflater = LayoutInflater.from(this)
//                        val childView =
//                            childInflater.inflate(R.layout.row_assessment_for_faculty, null)
//                        val learner_name = childView!!.findViewById(R.id.learner_name) as TextView
//                        val assessment_and_attempt_detail =
//                            childView!!.findViewById(R.id.assessment_and_attempt_detail) as TextView
//                        val date_and_time = childView!!.findViewById(R.id.date_and_time) as TextView
//                        val pastThreeDayLayout=childView.findViewById(R.id.pastThreeDayLayout) as LinearLayout
//                        if(hashMapValue[DAYS_COUNT].toString().toInt()>3)
//                        {
//                            pastThreeDayLayout.visibility=View.GONE
//                        }
//                        else
//                        {
//                            if(hashMapValue[Constants.READ_VALUE]==Constants.READ)
//                            {
//                                pastThreeDayLayout.visibility=View.GONE
//                            }
//                        }
//                        learner_name.text = hashMapValue.get(DESCRIPTION).toString()
//                        assessment_and_attempt_detail.text =
//                            hashMapValue.get(ASSESSTMENT).toString()
//                        assessment_and_attempt_detail.contentDescription=hashMapValue.get(ASSESSTMENT).toString()
//
//                        date_and_time.text = hashMapValue.get(DATE_VALUE).toString()
//
//                        if (x == 0) {
//                            if (headerView != null)
//                                addAssessment.addView(headerView)
//                        }
//                        addAssessment.addView(childView)
//                    }
//
//                }
//                WITH_IN_AGO -> {
//
//                    for (x in withInList.indices) {
//                        var headerView: View? = null
//                        var hashMapValue = withInList[x]
//
//                        if (x == 0) {
//                            headerView =
//                                inflater.inflate(R.layout.row_assessment_faculty_header, null)
//                            val titleTxt = headerView!!.findViewById(R.id.titleTxt) as TextView
//                            titleTxt.text = WITH_IN_AGO
//
//                        }
//                        val childInflater = LayoutInflater.from(this)
//                        val childView =
//                            childInflater.inflate(R.layout.row_assessment_for_faculty, null)
//                        val learner_name = childView!!.findViewById(R.id.learner_name) as TextView
//                        val assessment_and_attempt_detail =
//                            childView!!.findViewById(R.id.assessment_and_attempt_detail) as TextView
//                        val date_and_time = childView!!.findViewById(R.id.date_and_time) as TextView
//                        val pastThreeDayLayout=childView.findViewById(R.id.pastThreeDayLayout) as LinearLayout
//                        if(hashMapValue[DAYS_COUNT].toString().toInt()>3)
//                        {
//                            pastThreeDayLayout.visibility=View.GONE
//                        }
//                        else
//                        {
//                            if(hashMapValue[Constants.READ_VALUE]==Constants.READ)
//                            {
//                                pastThreeDayLayout.visibility=View.GONE
//                            }
//                        }
//
//                        learner_name.text = hashMapValue.get(DESCRIPTION).toString()
//                        assessment_and_attempt_detail.text =
//                            hashMapValue.get(ASSESSTMENT).toString()
//                        assessment_and_attempt_detail.contentDescription=hashMapValue.get(ASSESSTMENT).toString()
//                        date_and_time.text = hashMapValue.get(DATE_VALUE).toString()
//
//                        if (x == 0) {
//
//                            if (headerView != null)
//                                addAssessment.addView(headerView)
//
//                        }
//                        addAssessment.addView(childView)
//                    }
//
//                }
//                else -> {
//
//                }
//            }
//        }

        createThreeDays(threeMoreList)
        createTwoDays(twoAgoList)
        createOneDays(withInList)




    }

    private fun createThreeDays(threeMoreList: ArrayList<HashMap<String, Any>>)
    {

        val inflater = LayoutInflater.from(this)

        for (x in threeMoreList.indices) {
            var headerView: View? = null
            var hashMapValue = threeMoreList[x]

            if (x == 0) {
                headerView =
                    inflater.inflate(R.layout.row_assessment_faculty_header, null)
                val titleTxt = headerView!!.findViewById(R.id.titleTxt) as TextView

                titleTxt.text = THREE_DAY_AGO


            }
            val childInflater = LayoutInflater.from(this)
            val childView =
                childInflater.inflate(R.layout.row_assessment_for_faculty, null)
            val learner_name = childView!!.findViewById(R.id.learner_name) as TextView
            val assessment_and_attempt_detail =
                childView!!.findViewById(R.id.assessment_and_attempt_detail) as TextView
            val date_and_time = childView!!.findViewById(R.id.date_and_time) as TextView
            val pastThreeDayLayout=childView.findViewById(R.id.pastThreeDayLayout) as LinearLayout
            if(hashMapValue[DAYS_COUNT].toString().toInt()>3)
            {
                pastThreeDayLayout.visibility=View.GONE
            }
            else
            {
                if(hashMapValue[Constants.READ_VALUE]==Constants.READ)
                {
                    pastThreeDayLayout.visibility=View.GONE
                }
            }

            learner_name.text = hashMapValue.get(DESCRIPTION).toString()
            assessment_and_attempt_detail.text =
                hashMapValue.get(ASSESSTMENT).toString()

            assessment_and_attempt_detail.contentDescription=hashMapValue.get(ASSESSTMENT).toString()

            date_and_time.text = hashMapValue.get(DATE_VALUE).toString()


            if (x == 0) {

                if (headerView != null)
                    addAssessment.addView(headerView)

            }
            addAssessment.addView(childView)
        }

    }


    private fun createTwoDays(twoAgoList: ArrayList<HashMap<String, Any>>)
    {

        val inflater = LayoutInflater.from(this)

        for (x in twoAgoList.indices) {
            var headerView: View? = null
            var hashMapValue = twoAgoList[x]

            if (x == 0) {
                headerView =
                    inflater.inflate(R.layout.row_assessment_faculty_header, null)
                val titleTxt = headerView!!.findViewById(R.id.titleTxt) as TextView

                titleTxt.text = TWO_DAY_AGO


            }
            val childInflater = LayoutInflater.from(this)
            val childView =
                childInflater.inflate(R.layout.row_assessment_for_faculty, null)
            val learner_name = childView!!.findViewById(R.id.learner_name) as TextView
            val assessment_and_attempt_detail =
                childView!!.findViewById(R.id.assessment_and_attempt_detail) as TextView
            val date_and_time = childView!!.findViewById(R.id.date_and_time) as TextView
            val pastThreeDayLayout=childView.findViewById(R.id.pastThreeDayLayout) as LinearLayout
            if(hashMapValue[DAYS_COUNT].toString().toInt()>3)
            {
                pastThreeDayLayout.visibility=View.GONE
            }
            else
            {
                if(hashMapValue[Constants.READ_VALUE]==Constants.READ)
                {
                    pastThreeDayLayout.visibility=View.GONE
                }
            }
            learner_name.text = hashMapValue.get(DESCRIPTION).toString()
            assessment_and_attempt_detail.text =
                hashMapValue.get(ASSESSTMENT).toString()
            assessment_and_attempt_detail.contentDescription=hashMapValue.get(ASSESSTMENT).toString()

            date_and_time.text = hashMapValue.get(DATE_VALUE).toString()

            if (x == 0) {
                if (headerView != null)
                    addAssessment.addView(headerView)
            }
            addAssessment.addView(childView)
        }

    }


    private fun createOneDays(withInList: ArrayList<HashMap<String, Any>>)
    {

        val inflater = LayoutInflater.from(this)

        for (x in withInList.indices) {
            var headerView: View? = null
            var hashMapValue = withInList[x]

            if (x == 0) {
                headerView =
                    inflater.inflate(R.layout.row_assessment_faculty_header, null)
                val titleTxt = headerView!!.findViewById(R.id.titleTxt) as TextView
                titleTxt.text = WITH_IN_AGO

            }
            val childInflater = LayoutInflater.from(this)
            val childView =
                childInflater.inflate(R.layout.row_assessment_for_faculty, null)
            val learner_name = childView!!.findViewById(R.id.learner_name) as TextView
            val assessment_and_attempt_detail =
                childView!!.findViewById(R.id.assessment_and_attempt_detail) as TextView
            val date_and_time = childView!!.findViewById(R.id.date_and_time) as TextView
            val pastThreeDayLayout=childView.findViewById(R.id.pastThreeDayLayout) as LinearLayout
            if(hashMapValue[DAYS_COUNT].toString().toInt()>3)
            {
                pastThreeDayLayout.visibility=View.GONE
            }
            else
            {
                if(hashMapValue[Constants.READ_VALUE]==Constants.READ)
                {
                    pastThreeDayLayout.visibility=View.GONE
                }
            }

            learner_name.text = hashMapValue.get(DESCRIPTION).toString()
            assessment_and_attempt_detail.text =
                hashMapValue.get(ASSESSTMENT).toString()
            assessment_and_attempt_detail.contentDescription=hashMapValue.get(ASSESSTMENT).toString()
            date_and_time.text = hashMapValue.get(DATE_VALUE).toString()

            if (x == 0) {

                if (headerView != null)
                    addAssessment.addView(headerView)

            }
            addAssessment.addView(childView)
        }

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.view_grades_and_assessment -> {
//                if (assignmentsBean != null) {
//                    if (assignmentsBean != null && assignmentsBean!!.courseAssignment!!.size > 0) {

                val messageLink=  intent.extras?.getString(Constants.COURSE_MESSAGE_LINK)
                val coursID = messageLink!!.split("=")[2]
                var link=BLACK_BOARD_DOMAIN+""+VIEW_NEED_GRADING+""+coursID
                DialogUtils.screenNamePrefix="course:assessments-faculty-view:gradecenter-linkout"
//                        var link = assignmentsBean!!.courseAssignment[0].link
                val stickyWork = StickyInfoGrabber(this@AssessmentForFacultyActivity)
                stickyWork.generateMuleSoftStickySessionForTargetUrl(
                    link,
                    BuildConfig.STICKY_FORWARD_URL
                )

                OmnitureTrack.trackAction("course:assessments-faculty-view:gradecenter-linkout")

//                    }
//
//                }

            }
        }
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
     *  Factory method of activity, executes when application comes in resume state from pause state.
     *
     */
    override fun onResume() {
        super.onResume()
        init()
        //No needs to write here , we are adding PullTORefresh
    }


    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun init() {
        ConnectivityReceiver.connectivityReceiverListener = this@AssessmentForFacultyActivity
        connectivityReceiver = ConnectivityReceiver(networkLayout)
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(connectivityReceiver, intentFilter)
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
                //NETWORK AVAILABLE
                checkTwoDay = true
                checkMoreDay = true
                checkOneDay = true
                countThreeDay = 0
                countTwoDay = 0
                countOneDay = 0
                addAssessment.removeAllViews()
                callAssignmentsApi(this.courseID)
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

    private fun checkReadStatus(assignmentsList: ArrayList<AssesstmentFacultyBean.CourseAssignment>)
    {


        val  responselist=assignmentsList

        if (responselist.isNotEmpty()) {


            var SHARED_COURSE_ID =
                courseID + "" + userEmployeeId

            val SHARRED_INTRUCTOR_DATE =
                SHARED_COURSE_ID + "" + PreferenceKeys.FP_INTRUCTOR_FACULTY_DATE
            val SHARRED_INTRUCTOR_READ =
                SHARED_COURSE_ID + "" + PreferenceKeys.FP_INTRUCTOR_FACULTY_READ


            var Datelist =
                Util.stringToWords(Preferences.getArrayList(SHARRED_INTRUCTOR_DATE)!!)

            var fpLearnerDatelist: ArrayList<String>? = null
            if (Datelist.isEmpty()) {
                fpLearnerDatelist = ArrayList<String>()
            } else {
                fpLearnerDatelist = Datelist as ArrayList
            }

            var Readlist =
                Util.stringToWords(Preferences.getArrayList(SHARRED_INTRUCTOR_READ)!!)


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

                if (responselist[i].submittedDateTime != null) {
                    var longg = Util.getDateToLong(
                        responselist[i].submittedDateTime!!
                    )


                    date = Util.getDate(longg!!, Constants.DATE_FORMAT_SEC)!!
                }
                if (responselist.size > i + difference) {
                    if (responselist[i + difference].submittedDateTime != null) {

                        var longgDate = Util.getDateToLong(
                            responselist[i + difference].submittedDateTime!!
                        )
                        var checkdate =
                            Util.getDate(
                                longgDate!!,
                                Constants.DATE_FORMAT_SEC
                            )!!

                        if (fpLearnerDatelist!!.size > 0) {
//
                            if (fpLearnerDatelist!!.size > i) {
                                if ((fpLearnerDatelist[i].trim()) == checkdate!!.trim()) {

                                    if ((fpLearnerReadlist[i].trim()) == Constants.READ.trim()) {
                                        responselist[i + difference].setRead(Constants.READ)
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
                SHARRED_INTRUCTOR_DATE,
                learnerlistDate.toString()
            )
            Preferences.addArrayList(
                SHARRED_INTRUCTOR_READ,
                learnerlistRead.toString()
            )

        }
    }

    override fun onBackPressed() {
        finishActivity()
    }

    private fun finishActivity()
    {
        setReadStatus(assignmentsList)
        finish()
    }


    private fun setReadStatus(assignmentsList: ArrayList<AssesstmentFacultyBean.CourseAssignment>)
    {


        val  responselist=assignmentsList

        if (responselist.isNotEmpty()) {


            var SHARED_COURSE_ID =
                courseID + "" + userEmployeeId

            val SHARRED_INTRUCTOR_DATE =
                SHARED_COURSE_ID + "" + PreferenceKeys.FP_INTRUCTOR_FACULTY_DATE
            val SHARRED_INTRUCTOR_READ =
                SHARED_COURSE_ID + "" + PreferenceKeys.FP_INTRUCTOR_FACULTY_READ


            var Datelist =
                Util.stringToWords(Preferences.getArrayList(SHARRED_INTRUCTOR_DATE)!!)

            var fpLearnerDatelist: ArrayList<String>? = null
            if (Datelist.isEmpty()) {
                fpLearnerDatelist = ArrayList<String>()
            } else {
                fpLearnerDatelist = Datelist as ArrayList
            }

            var Readlist =
                Util.stringToWords(Preferences.getArrayList(SHARRED_INTRUCTOR_READ)!!)


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

                if (responselist[i].submittedDateTime != null) {
                    var longg = Util.getDateToLong(
                        responselist[i].submittedDateTime!!
                    )


                    date = Util.getDate(longg!!, Constants.DATE_FORMAT_SEC)!!
                }
                if (responselist.size > i + difference) {
                    if (responselist[i + difference].submittedDateTime != null) {

                        var longgDate = Util.getDateToLong(
                            responselist[i + difference].submittedDateTime!!
                        )
                        var checkdate =
                            Util.getDate(
                                longgDate!!,
                                Constants.DATE_FORMAT_SEC
                            )!!

                        if (fpLearnerDatelist!!.size > 0) {
//
                            if (fpLearnerDatelist!!.size > i) {
                                if ((fpLearnerDatelist[i].trim()) == checkdate!!.trim()) {

//                                    if ((fpLearnerReadlist[i].trim()) == Constants.READ.trim()) {
                                    responselist[i + difference].setRead(Constants.READ)
//                                    }
                                }
                            }

                        }
                    }
                }
                learnerlistDate.add(date)
                learnerlistRead.add(responselist[i].getRead()!!.toString().trim())
            }
            Preferences.addArrayList(
                SHARRED_INTRUCTOR_DATE,
                learnerlistDate.toString()
            )
            Preferences.addArrayList(
                SHARRED_INTRUCTOR_READ,
                learnerlistRead.toString()
            )

        }
    }

}
