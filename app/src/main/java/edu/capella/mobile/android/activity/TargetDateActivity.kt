package edu.capella.mobile.android.activity

import android.app.DatePickerDialog
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.bean.AssignmentSubmitBean
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.bean.PccpDetailBean
import edu.capella.mobile.android.interfaces.EventListener
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.HubbleNetworkConstants
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import edu.capella.mobile.android.widgets.CPEditText
import edu.capella.mobile.android.widgets.CPTextView
import kotlinx.android.synthetic.main.activity_target_dates.*
import kotlinx.android.synthetic.main.activity_target_dates.parentLayout
import kotlinx.android.synthetic.main.internet_connection_layout.*
import kotlinx.android.synthetic.main.toolbar_announcement.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  08-04-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */

class TargetDateActivity : MenuActivity()/*BaseActivity()*/,
    ConnectivityReceiver.ConnectivityReceiverListener {

    var courseId: String? = null
    var targetDateList: ArrayList<PccpDetailBean.AssignmentX> = ArrayList()
    //    var selectedDateMap: ArrayList<AssignmentSubmitBean> = ArrayList()
    private var connectivityReceiver: ConnectivityReceiver? = null
    var dynamicRelative: ArrayList<RelativeLayout> = ArrayList()
    var checkSelectedDate: Boolean = false
    //    var priviousDateList : ArrayList<String> = ArrayList()
//    var selectDateList : ArrayList<String> = ArrayList()
    var pccpDetailBeanDetail: PccpDetailBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_target_dates)
        setContentChildView(R.layout.activity_target_dates, true)
        initValues()
        initListeners()
        callApiToGetPccpDetail()
        OmnitureTrack.trackState("course:set-target-dates")
    }

    fun getDateListSize(): Int {
        try {
            return targetDateList.size
        } catch (t: Throwable) {
            return 0
        }
    }

    private fun initValues() {
        discriptionNetworkTxt.text = resources.getString(R.string.please_check_setting)
        headerTxt.text = resources.getString(R.string.set_target_dates)
//        backHeaderTxt.setOnClickListener { onBackPressed() }
//        backHeaderTxt.contentDescription=resources.getString(R.string.ada_back_button)+getString(R.string.back)
//        backButtonImg.contentDescription=resources.getString(R.string.ada_back_button)+getString(R.string.back)

        this.backButtonLl.setOnClickListener {
            this.finishActivity()
        }
        backButtonLl.contentDescription =
            resources.getString(R.string.ada_back_button) + getString(R.string.back)
    }

    /**
     * init() method used to initialize value of variables
     */
    private fun init() {
        ConnectivityReceiver.connectivityReceiverListener = this@TargetDateActivity
        connectivityReceiver = ConnectivityReceiver(networkLayout)
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(connectivityReceiver, intentFilter)
    }


    private fun initListeners() {
        var checkSubmit=false;

        saveDates.setOnClickListener {

            for( value in targetDateList)
            {
                if(value.targetDate!=null)
                {
                    checkSubmit=true;
                }
                else
                {
                    checkSubmit=false;
                    break
                }

            }

            if (checkSubmit) {
                datesAlert.visibility = View.GONE
                callAPiToSubmitPccpDetail()
            } else {
                checkDateFieldEmpty()
//                Toast.makeText(this,resources.getString(R.string.plz_select_all_dates_to_save),Toast.LENGTH_SHORT).show()
            }
        }

//        saveDates.setOnClickListener {
//            if (targetDateList.size == pccpDetailBeanDetail?.assignment?.assignments?.size) {
//                datesAlert.visibility = View.GONE
//                callAPiToSubmitPccpDetail()
//            } else {
//                checkDateFieldEmpty()
////                Toast.makeText(this,resources.getString(R.string.plz_select_all_dates_to_save),Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun generateTargetList() {
        for (index in 0 until targetDateList.size) {
            val inflater = LayoutInflater.from(this)
            val targetDateView = inflater.inflate(R.layout.row_item_target_date, null)


            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, resources.getDimension(R.dimen._10dp).toInt(), 0, 0)
            targetDateView.setLayoutParams(params)

            val relativeLayout =
                targetDateView.findViewById<RelativeLayout>(R.id.relativeParentLayout)
            dynamicRelative.add(relativeLayout)
            val assessmentTitle = targetDateView.findViewById<TextView>(R.id.assessment)
            val assessmentDate = targetDateView.findViewById<CPEditText>(R.id.dateBox)

            val fontLight =
                Typeface.createFromAsset(assets, "fonts/roboto_light.ttf")
//            assessmentTitle.typeface = font

            val fontMedium =
                Typeface.createFromAsset(assets, "fonts/roboto_medium.ttf")


            var title = HtmlCompat.fromHtml(
                targetDateList[index].assignmentName!!,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            ).toString()
            assessmentDate.tag = index
            if (targetDateList[index].targetDate != null) {

                assessmentDate.setText(targetDateList[index].targetDate)
                val assignmentToSubmit = AssignmentSubmitBean()
                assignmentToSubmit.id = targetDateList[index].Id!!
                assignmentToSubmit.targetDate = targetDateList[index].targetDate
                assignmentToSubmit.targetNewDate = targetDateList[index].targetDate
//                selectedDateMap.add(assignmentToSubmit)
            }

            var finalString =
                resources.getString(R.string.assessmenet) + " " + (index + 1) + ":" + title.subSequence(
                    title.lastIndexOf("]") + 1,
                    title.length
                )

            val firstString=resources.getString(R.string.assessmenet) + " " + (index + 1) + ":"
            val lastString= title.subSequence(
                title.lastIndexOf("]") + 1,
                title.length)
//            val spannableString = SpannableString(finalString)
//            spannableString.setSpan(
//                StyleSpan(Typeface.BOLD),
//                0,
//                finalString.indexOf(":"),
//                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
//            )
//            assessmentTitle.text = spannableString


            val spannable: Spannable = SpannableString(firstString +lastString)
            spannable.setSpan(
                CustomTypefaceSpan("fonts/roboto_medium.ttf", fontMedium),
                0,
                firstString.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                CustomTypefaceSpan("fonts/roboto_light.ttf", fontLight),
                firstString.length,
                firstString.length + lastString.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            assessmentTitle.text = spannable
//            text.setText(spannable)
            assessmentDate.setOnClickListener {

                var tagValue = it.tag

                var mYear = 0
                var mMonth = 0
                var mDay = 0


                if (targetDateList[index].targetDate != null) {
                    var userSelectedDate = assessmentDate.text
                    var splitDate = userSelectedDate!!.split("/")
                    mMonth = splitDate[0].toInt()
                    mDay = splitDate[1].toInt()
                    mYear = splitDate[2].toInt()
                    if (mMonth > 0)
                        mMonth -= 1
                } else {
                    val c: Calendar = Calendar.getInstance()
                    mYear = c.get(Calendar.YEAR)
                    mMonth = c.get(Calendar.MONTH)
                    mDay = c.get(Calendar.DAY_OF_MONTH)
                }

//                // Get Current Date
//                val c: Calendar = Calendar.getInstance()
//                var mYear = c.get(Calendar.YEAR)
//                var mMonth = c.get(Calendar.MONTH)
//                var mDay = c.get(Calendar.DAY_OF_MONTH)


                val datePickerDialog = DatePickerDialog(this,
                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                        // date selected
                        val selectedDate =
                            (month + 1).toString() + "/" + dayOfMonth.toString() + "/" + year
                        assessmentDate.setText(selectedDate)
                        checkSelectedDate = true
                        checkDateField()

//                        if (tagValue != null) {
//                            var indexValue = tagValue.toString()
//                            selectedDateMap.removeAt(indexValue.toInt())
//                        }
                        val assignmentToSubmit = AssignmentSubmitBean()
                        assignmentToSubmit.id = targetDateList[index].Id!!
                        assignmentToSubmit.targetDate = selectedDate
                        datesAlert.visibility = View.GONE
                        assignmentToSubmit.targetNewDate = selectedDate
//                        selectedDateMap.add(assignmentToSubmit)
                        var indexValue = tagValue.toString()
                        targetDateList[indexValue.toInt()].targetDate=selectedDate
                        targetDateList[indexValue.toInt()].targetNewDate=selectedDate

                    }, mYear, mMonth, mDay
                )

                if (Util.getDateInMiliSeconds(pccpDetailBeanDetail?.assignment?.courseStartDate!!) != null && Util.getDateInMiliSeconds(
                        pccpDetailBeanDetail?.assignment?.courseEndDate!!
                    ) != null
                ) {
                    datePickerDialog.datePicker.minDate =
                        Util.getDateInMiliSeconds(pccpDetailBeanDetail?.assignment?.courseStartDate!!)
                    datePickerDialog.datePicker.maxDate =
                        Util.getDateInMiliSeconds(pccpDetailBeanDetail?.assignment?.courseEndDate!!)
                }
                datePickerDialog.show()
            }

            assessmentListLayout.addView(targetDateView)
        }
    }


    private fun checkDateField() {
        if (dynamicRelative.size > 0) {
            for (value in dynamicRelative) {
                var relativeLayout = value.getChildAt(1) as RelativeLayout
                var textView = value.getChildAt(2) as CPTextView
                var editText = relativeLayout.getChildAt(0) as CPEditText
                var imageView = relativeLayout.getChildAt(1) as ImageView
                if (editText.text.toString() != "") {
                    imageView.visibility = View.GONE
                    textView.visibility = View.GONE
                    relativeLayout.setBackgroundResource(R.drawable.white_filled_rect_gray_target)
                }

            }
        }
    }

    private fun checkDateFieldEmpty() {
        if (dynamicRelative.size > 0) {
            for (value in dynamicRelative) {
                var relativeLayout = value.getChildAt(1) as RelativeLayout
                var editText = relativeLayout.getChildAt(0) as CPEditText
                var textView = value.getChildAt(2) as CPTextView
                var imageView = relativeLayout.getChildAt(1) as ImageView
                if (editText.text.toString() == "") {
                    datesAlert.visibility = View.VISIBLE
                    descrptionTxt.text = resources.getString(R.string.see_error)
                    imageView.visibility = View.VISIBLE
                    textView.visibility = View.VISIBLE
                    relativeLayout.setBackgroundResource(R.drawable.white_filled_rectangle_error)
                    scrollView.scrollTo(0, 0);
//                    break
                }

            }
        }
    }


    //call api to get PCCP detail
    private fun callApiToGetPccpDetail() {
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        courseId = intent.extras!!.getString(Constants.COURSE_ID).toString()
        val authHeader = NetworkHandler.getAuthorizationHeader()
        val hubbleHeaders = NetworkHandler.getHeadersForHubbleRequest(
            loginBean?.authData?.token,
            HubbleNetworkConstants.getAcceptHeaderValue(HubbleNetworkConstants.PCCP_DETAIL_API)
        )

        val finalHeaders = NetworkHandler.meargeHeaders(authHeader, hubbleHeaders)

        val params = HashMap<String, Any>()

        val url = HubbleNetworkConstants.getUrl(
            HubbleNetworkConstants.PCCP_DETAIL_API,
            "{employeeId}",
            "" + loginBean?.authData?.employeeId!!.value
        )


        val targetDateUrl =
            HubbleNetworkConstants.getUrl(url, "{courseId}", "" + courseId)

        Util.trace("target date url", targetDateUrl)
        val networkHandler = NetworkHandler(
            this,
            targetDateUrl,
            params,
            NetworkHandler.METHOD_GET,
            pccpDetailNetworkListener,
            finalHeaders
        )
        networkHandler.setSilentMode(false)
        networkHandler.isPostTypeSubmitting = true
        networkHandler.submitMessage = ""
        networkHandler.execute()
    }

    private val pccpDetailNetworkListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {
            try {


                Util.trace("pccp detail first :  " + response.first)
               // Util.trace("pccp detai second :  " + response.second)

                if (response.first == NetworkConstants.SUCCESS) {
                    val gson = Gson()
                    pccpDetailBeanDetail = gson.fromJson<PccpDetailBean>(
                        response.second.toString(),
                        PccpDetailBean::class.java
                    )

//                targetDateList = pccpDetailBeanDetail?.assignment?.assignments!!

                    targetDateList.clear()
                    targetDateList.addAll(pccpDetailBeanDetail?.assignment?.assignments!!)
//                    selectedDateMap.clear()
                    assessmentListLayout.removeAllViews()
                    generateTargetList()
                } else {
                    DialogUtils.showGenericErrorDialog(this@TargetDateActivity)
                }
            }catch (e:Exception)
            {
                DialogUtils.showGenericErrorDialog(this@TargetDateActivity)
                e.printStackTrace()
            }
        }
    }

    private fun callAPiToSubmitPccpDetail() {
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val authHeader = NetworkHandler.getAuthorizationHeader()
        val hubbleHeaders = NetworkHandler.getHeadersForHubbleRequest(
            loginBean?.authData?.token,
            HubbleNetworkConstants.getAcceptHeaderValue(HubbleNetworkConstants.SUBMIT_PCCP_DETAIL_API)
        )

        val finalHeaders = NetworkHandler.meargeHeaders(authHeader, hubbleHeaders)

//        var params = HashMap<String, Any>()
//        params =

        val url = HubbleNetworkConstants.getUrl(
            HubbleNetworkConstants.SUBMIT_PCCP_DETAIL_API,
            "{employeeId}",
            "" + loginBean?.authData?.employeeId!!.value
        )


        Util.trace("submit pccp detail url", url)
        val networkHandler = NetworkHandler(
            this,
            url,
            getHashMapToSubmit(),
            NetworkHandler.METHOD_POST_JSON,
            submitPccpDetailNetworkListener,
            finalHeaders
        )
        networkHandler.setSilentMode(false)
        networkHandler.isPostTypeSubmitting = true

        networkHandler.submitMessage = getString(R.string.saving_target_dates)

        networkHandler.execute()
    }

    private val submitPccpDetailNetworkListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {
            try {


                Util.trace("pccp detail first :  " + response.first)
                Util.trace("pccp detai second :  " + response.second)

                if (response.first == NetworkConstants.SUCCESS) {
//                val gson = Gson()
//                var pccpDetailBeanDetail = gson.fromJson<PccpDetailBean>(
//                    response.second.toString(),
//                    PccpDetailBean::class.java
//                )
//
//                targetDateList = pccpDetailBeanDetail?.assignment?.assignments!!
//                generateTargetList()
                    checkSelectedDate = false
                    datesAlert.visibility = View.GONE
                    val message = resources.getString(R.string.target_dates_saved)
//                    Util.showSnakeBar(message, parentLayout, 3000)
                    Util.showCustomSnakeBar(this@TargetDateActivity,message, parentLayout, 3000)
                    scrollView.scrollTo(0, 0);

//                    callApiToGetPccpDetail()
                    OmnitureTrack.trackAction("course:set-target-dates:confirm")


                } else {
                    datesAlert.visibility = View.VISIBLE
                    descrptionTxt.text = resources.getString(R.string.please_try_again)
                    scrollView.scrollTo(0, 0);
                    Util.announceAda(getString(R.string.ada_your_targets_dates_were_not_saved) + getString(R.string.ada_please_try_again), this@TargetDateActivity)

//                DialogUtils.showGenericErrorDialog(this@TargetDateActivity)
                }
            }catch (e:Exception)
            {
                e.printStackTrace()
                DialogUtils.showGenericErrorDialog(this@TargetDateActivity)
            }
        }
    }


    private fun finishActivity() {
        if (checkSelectedDate) {
            DialogUtils.showDialogOnGoOutTarget(this, object : EventListener {
                override fun cancel() {
                    super.cancel()
                    finish()
                    OmnitureTrack.trackState("course:set-target-dates:cancel")
                }
            })
        } else {
            finish()
            OmnitureTrack.trackState("course:set-target-dates:cancel")
        }
    }

    override fun onBackPressed() {
        finishActivity()
    }

    private fun getHashMapToSubmit(): String {
        val mapToReturn = HashMap<String, Any>()

        val jsonData = JSONObject()
        jsonData.put(
            HubbleNetworkConstants.PCCP_ID,
            pccpDetailBeanDetail?.assignment?.pccpId.toString()
        )
        jsonData.put(
            HubbleNetworkConstants.COURSE_ID,
            pccpDetailBeanDetail?.assignment?.courseId.toString()
        )

        jsonData.put(
            HubbleNetworkConstants.EMPLOYEE_ID,
            pccpDetailBeanDetail?.assignment?.employeeId.toString()
        )
        jsonData.put(
            HubbleNetworkConstants.USER_ID,
            pccpDetailBeanDetail?.assignment?.userId.toString()
        )
        jsonData.put(
            HubbleNetworkConstants.LONG_COURSE_ID,
            pccpDetailBeanDetail?.assignment?.longCourseId.toString()
        )
        val jsonArray = JSONArray()


        for (position in 0 until targetDateList.size) {
            val innerJsonData = JSONObject()
            innerJsonData.put(HubbleNetworkConstants.ID, targetDateList[position].Id)
            innerJsonData.put(
                HubbleNetworkConstants.TARGET_DATE,
                targetDateList[position].targetDate
            )
            innerJsonData.put(
                HubbleNetworkConstants.TARGET_NEW_DATE,
                targetDateList[position].targetNewDate
            )
            jsonArray.put(innerJsonData)
        }

        jsonData.put(HubbleNetworkConstants.ASSIGNMENTS, jsonArray)

        return jsonData.toString()
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

    }

    /**
     *  Factory method of activity, used to handler certain functionality as par need
     *  here its checking if LoginActivity is opening and IS_TIMEOUT flag is true in shared preference
     *  then, show Timeout message over top of the activity
     */
    override fun onResume() {
        super.onResume()
        init()
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

}
