package edu.capella.mobile.android.activity

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Html
import android.util.Pair
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.utils.Constants.COURSE_ID
import edu.capella.mobile.android.adapters.AssignmentsListAdapter
import edu.capella.mobile.android.bean.AssignmentsBean
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.HubbleNetworkConstants
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import kotlinx.android.synthetic.main.activity_assignments.*
import kotlinx.android.synthetic.main.activity_library.networkLayout
import kotlinx.android.synthetic.main.toolbar_generic.*
import kotlinx.android.synthetic.main.toolbar_generic.view.*


/**
 * AssignmentsActivity.kt :  Screen responsible for showing the list of Assignments list
 *
 * @author  :  Didarul.Khan
 * @version :  1.0
 * @since   :  01-27-2020
 * @created : 04-04-20
 *
 */

class AssignmentsActivity: MenuActivity()/*BaseActivity()*/, ConnectivityReceiver.ConnectivityReceiverListener, NetworkListener {

    var courseID = "";
    var assignmentsList: ArrayList<AssignmentsBean.CourseAssignment?> = ArrayList()
    var assignmentsListAdapter: AssignmentsListAdapter? = null



    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    private var isInternetConnection: Boolean = false


    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null

    fun getAssignmentListSize():Int
    {
        return assignmentsList.size
    }

    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_assignments)
        setContentChildView(R.layout.activity_assignments,true)
        assignmentsToolbar.genericTitleTxt.text = getString(R.string.assignments)
        backButtonLayout.contentDescription = getString(R.string.ada_back_button) + getString(R.string.back)

        this.courseID = intent.getStringExtra(COURSE_ID)
        //this.courseID = "TEST-FP6001_007575_1_1197_OEE_02"
        initUi()

        OmnitureTrack.trackState("assignments")

    }


    /**
     * Method initializes tabs with container data and Look and fill.
     *
     */
    private fun initUi() {


            initializeAdapter()

        //callSyllabusListApi("TEST-FP6001_007575_1_1201_OEE_03")
        callAssignmentsApi(this.courseID)


        this.assignmentsListSwipeToRefresh.setColorSchemeColors( getColor(R.color.checkBoxColor))


        assignmentsListSwipeToRefresh.setOnRefreshListener {
            callAssignmentsApi(this.courseID)
            assignmentsListSwipeToRefresh.isRefreshing = false
        }
    }

    private fun initializeAdapter() {
        assignmentsListView.layoutManager = LinearLayoutManager(this)
        OmnitureTrack.trackAction("course:assignment-detail")
        assignmentsListAdapter = AssignmentsListAdapter(this, assignmentsList , object :
            AssignmentsListAdapter.AssignmentsItemListener {
            override fun onItemClicked(value: AssignmentsBean.CourseAssignment?)
            {
                    try {
                        OmnitureTrack.trackAction("assignment:detail-linkout")

                        var linkToOpen = Util.extractLastHref(value?.instructions)

                        if (linkToOpen != null) {
                            val intent =
                                Intent(this@AssignmentsActivity, UnitWebViewActivity::class.java)


                            intent.putExtra(Constants.URL_FOR_IN_APP, linkToOpen)
                            intent.putExtra(Constants.ORANGE_TITLE,
                                Html.fromHtml(Util.str(value?.title), Html.FROM_HTML_MODE_LEGACY)
                                    .toString().trim()
                            )
                            intent.putExtra(Constants.IN_APP_TITLE, "Assignment")
                            intent.putExtra(Constants.BACK_TITLE, "Assignments")
                            intent.putExtra(Constants.OVERRIDE_TITLE, true)
                            intent.putExtra(Constants.PAGE_WARNING_HIDE, true)


                            startActivity(intent)
                        }
                    }catch (t:Throwable){}

            }

        })


        assignmentsListView.adapter = assignmentsListAdapter
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

        ConnectivityReceiver.connectivityReceiverListener = this@AssignmentsActivity
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


    private fun callAssignmentsApi(courseId: String)
    {

        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val authHeader = NetworkHandler.getAuthorizationHeader()
        val hubbleHeaders = NetworkHandler.getHeadersForHubbleRequest(loginBean?.authData?.token,
            HubbleNetworkConstants.getAcceptHeaderValue(HubbleNetworkConstants.ASSIGNMENTS_LIST))

        val finalHeaders = NetworkHandler.meargeHeaders(authHeader, hubbleHeaders)

        val params = HashMap<String, Any>()

        //val stickyHeader : java.util.HashMap<String, Any?>? = NetworkHandler.getStickySessionHeader(
        //   Preferences.getValue(PreferenceKeys.MULESOFT_ACCESS_TOKEN))

        var assignmentsUrl = HubbleNetworkConstants.getUrl(
            HubbleNetworkConstants.ASSIGNMENTS_LIST, "{{employeeId}}",
            "" + loginBean?.authData?.employeeId!!.value
        )

        val assignmentsListUrl =
            HubbleNetworkConstants.getUrl(assignmentsUrl, "{{courseId}}", "" + courseID)
        Util.trace("Assignments URL  :"  + assignmentsUrl)


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


    /**
     * Callback method of Network task which executes when communication completed with server
     *
     * @param response : API response received from server.
     * @see Pair
     */

    override fun onNetworkResponse(response: Pair<String, Any>) {


        Util.trace("Assignments : " + response.second )

        if(response.first == NetworkConstants.SUCCESS)
        {
            val gson = Gson()
            var assignmentsBean = gson.fromJson<AssignmentsBean>(response.second.toString(), AssignmentsBean::class.java)
            updateAssignmentsList(assignmentsBean)

        }else
        {
            DialogUtils.showGenericErrorDialog(this)
        }
    }


    private fun updateAssignmentsList(assignmentsBean: AssignmentsBean?) {

        try {
            OmnitureTrack.trackAction("course:assignment-detail")
            Util.trace("Update Assignments List method started")

            val assignmentsListBean = assignmentsBean




            if ((assignmentsBean?.courseAssignment == null) ||
                (assignmentsBean?.courseAssignment!!.isEmpty())
            ) {
                noAssignmentsLayout.visibility = View.VISIBLE
                return
            }

            var listNew = assignmentsListBean?.courseAssignment as ArrayList<AssignmentsBean.CourseAssignment>


            var newSortedList = listNew.sortedWith(compareBy(AssignmentsBean.CourseAssignment::title, AssignmentsBean.CourseAssignment::title))

            Util.trace("List size is ${listNew?.size}")
            assignmentsList.clear()
             assignmentsList.addAll(newSortedList)
           // assignmentsList.addAll(listNew!!)



            if (assignmentsList.size > 0) {

                assignmentsListLayout.visibility = View.VISIBLE

                if (assignmentsListAdapter != null) {
                    assignmentsListAdapter!!.notifyDataSetChanged()
                    assignmentsListView.adapter = assignmentsListAdapter
                     // noInfoTxt.visibility = View.GONE
                }
            } else {
                noAssignmentsLayout.visibility = View.VISIBLE
            }
        }catch (t: Throwable)
        {
            Util.trace("Assignments error : $t")
            t.printStackTrace()
        }
    }



}
