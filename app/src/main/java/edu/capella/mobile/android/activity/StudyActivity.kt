package edu.capella.mobile.android.activity

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Pair
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.adapters.StudiesAdapter
import edu.capella.mobile.android.bean.CourseBasicInfoBean
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.HubbleNetworkConstants
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import kotlinx.android.synthetic.main.activity_study.*
import kotlinx.android.synthetic.main.activity_study.networkLayout
import kotlinx.android.synthetic.main.toolbar_generic.*

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  24-03-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
class StudyActivity  : MenuActivity() /*BaseActivity() */ ,  ConnectivityReceiver.ConnectivityReceiverListener{

    var studyAdapter: StudiesAdapter? = null
    val studiesdataList :ArrayList<CourseBasicInfoBean.StudyInstruction>? = ArrayList()
    var courseBasicInfoBean:CourseBasicInfoBean? = null
    var courseId: String? = null
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
       // setContentView(R.layout.activity_study)
        setContentChildView(R.layout.activity_study , true)
        initialiseValue()
        setStudiesRecyclerView()
        setSwipeRefresh()
        callApiToGetBasicInfo()

        OmnitureTrack.trackState("course:studies")
    }

    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun initNetworkBroadcastReceiver()
    {

        ConnectivityReceiver.connectivityReceiverListener = this@StudyActivity
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

    fun getStudyList():Int{
        try {
            return studiesdataList?.size!!
        } catch (t: Throwable) {
            return 0
        }
    }

    private fun initialiseValue(){
        genericTitleTxt.text = resources.getString(R.string.studies)
        backButtonLayout.contentDescription = getString(R.string.ada_back_button) + resources.getString(R.string.back)
    }

    private fun setSwipeRefresh(){
        val swipeRefreshColor =  ContextCompat.getColor(this,R.color.checkBoxColor)
        studiesSwipeLayout.setColorSchemeColors(swipeRefreshColor)
        studiesSwipeLayout.setOnRefreshListener{
            callApiToGetBasicInfo()
            studiesSwipeLayout.isRefreshing = false
        }
    }

    private fun setStudiesRecyclerView(){
        studiesList.layoutManager = LinearLayoutManager(this)
        studyAdapter = StudiesAdapter(this,studiesdataList!!,object :StudiesAdapter.StudyEventListener{
            override fun onStudyItemClick(studyInstruction: CourseBasicInfoBean.StudyInstruction) {
                // TODO open study detail screen
                val studyDetailIntent = Intent(this@StudyActivity,CommonDetailActivity::class.java)
                studyDetailIntent.putExtra(Constants.CONTENT, studyInstruction.content)
                studyDetailIntent.putExtra(Constants.CONTENT_TITLE, studyInstruction.title)
                studyDetailIntent.putExtra(Constants.TITLE,resources.getString(R.string.study))
                studyDetailIntent.putExtra(Constants.SHOW_WARING,false)
                this@StudyActivity.startActivity(studyDetailIntent)
            }

        })
        studiesList.adapter = studyAdapter
    }

    //call api to get basic info
    private fun callApiToGetBasicInfo() {
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        courseId = intent.extras!!.getString(Constants.COURSE_ID).toString()
        val authHeader = NetworkHandler.getAuthorizationHeader()
        val hubbleHeaders = NetworkHandler.getHeadersForHubbleRequest(loginBean?.authData?.token,
            HubbleNetworkConstants.getAcceptHeaderValue(HubbleNetworkConstants.COURSE_BASIC_INFO_API))

        val finalHeaders = NetworkHandler.meargeHeaders(authHeader, hubbleHeaders)

        val params = java.util.HashMap<String, Any>()

        val url = HubbleNetworkConstants.getUrl(
            HubbleNetworkConstants.COURSE_BASIC_INFO_API,
            "{{employeeId}}",
            "" + loginBean?.authData?.employeeId!!.value
        )


        val basicInfoUrl =
            HubbleNetworkConstants.getUrl(url, "{{courseId}}", "" + courseId)

        Util.trace("basic info url", basicInfoUrl)
        val networkHandler = NetworkHandler(
            this,
            basicInfoUrl,
            params,
            NetworkHandler.METHOD_GET,
            courseBasicInfoListener,
            finalHeaders
        )
        networkHandler.setSilentMode(false)
        networkHandler.execute()
    }

    private val courseBasicInfoListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {
            try{
            Util.trace("course basic info first :  " + response.first)
            Util.trace("course basic info second :  " + response.second)
            // TODO handle course detail api response
            if (response.first == NetworkConstants.SUCCESS) {
                val gson = Gson()
                courseBasicInfoBean = gson.fromJson<CourseBasicInfoBean>(
                    response.second.toString(),
                    CourseBasicInfoBean::class.java
                )

                studiesdataList?.clear()
                for(index in 0 until courseBasicInfoBean?.basicCourseInfo?.size!!)
                {
                    if(courseBasicInfoBean?.basicCourseInfo!![index].courseId?.equals(courseId)!!){
                        studiesdataList?.addAll(courseBasicInfoBean?.basicCourseInfo!![index].studyInstructions?.studyInstruction!!)
                        studyAdapter?.notifyDataSetChanged()
                        break
                    }
                }

            } else {
                DialogUtils.showGenericErrorDialog(this@StudyActivity)
            }
            }catch (e:Exception){
                e.printStackTrace()
                DialogUtils.showGenericErrorDialog(this@StudyActivity)
            }
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        isInternetConnection=isConnected
        if(isConnected == true && isNetworkFailedDueToConnectivity!=null && isNetworkFailedDueToConnectivity==true)
        {
            isNetworkFailedDueToConnectivity = null
            if(this.getCurrentNetworkQueueSize()==0) {
                DialogUtils.releaseGenericDialog()
                callApiToGetBasicInfo()
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

}
