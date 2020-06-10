package edu.capella.mobile.android.activity


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Pair
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.adapters.AnnouncementsAdapter
import edu.capella.mobile.android.bean.AnnouncementsBean
import edu.capella.mobile.android.bean.CourseDetailBean

import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import kotlinx.android.synthetic.main.activity_announcements.*
import kotlinx.android.synthetic.main.toolbar_announcement.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

/**
 * AnnouncementsActivity.kt :  Screen responsible for showing the list of Announcements
 *
 * @author  :  Kush.pandya
 * @version :  1.0
 * @since   :  03-02-2020
 *
 *
 */
class  AnnouncementsActivity : MenuActivity() /*BaseActivity()*/, NetworkListener,
    ConnectivityReceiver.ConnectivityReceiverListener {

    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null

    private var courseDetailData: CourseDetailBean? = null

    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    public  var isInternetConnection: Boolean=false

    /**
     * List of announcement received from server.
     */
    var announcementsList = ArrayList<CourseDetailBean.CourseAnnouncement?>()   //ArrayList<Any>()

    /**
     * Adapter for handling list behaviour.
     * @see RecyclerView.Adapter
     */
    var announcementsAdapter: AnnouncementsAdapter? = null

    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_announcements)
        setContentChildView(R.layout.activity_announcements, true)
//        updateAnnouncementData()
        initialiseToolbar()
        initialSetUp()
        initializeAdapter()
        getAnnouncementDataFromIntent()
        OmnitureTrack.trackAction("course:announcements")

    }

    /**
     *  Method creates a server request to load data from server for Announcement.
     */
    @SuppressLint("SetTextI18n")
    private fun updateAnnouncementData() {
        if (Util.isInternetOn(this)) {
            val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
            val gson = Gson()
            val loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

            callAnnouncementsApi(
                loginBean?.authData?.username!!,
                Preferences.getValue(PreferenceKeys.SECRET)!!
            )
        }
    }

    /**
     * Method used to set title of screen and to bind the click events with certain widgets
     * used inside screen
     *
     */
    private fun initialiseToolbar() {
//        backTxt.contentDescription=resources.getString(R.string.ada_back_button)+getString(R.string.back)
        headerTxt.text = getString(R.string.announcements)
        backButtonLl.setOnClickListener { onBackPressed()  }
        backButtonLl.contentDescription=resources.getString(R.string.ada_back_button)+getString(R.string.back)
    }

    /**
     * Method triggered by broadcast receiver which is listening for Network state.
     *
     * @param isConnected : Tells whether internet is available or not
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        isInternetConnection=isConnected

      /*  if(isConnected == true && isNetworkFailedDueToConnectivity!=null && isNetworkFailedDueToConnectivity==true)
        {
            isNetworkFailedDueToConnectivity = null


            refreshAPI(courseDetailData!!)
        }else
        {
            //NETWORK GONE
            isNetworkFailedDueToConnectivity = true
        }*/
    }

    fun getAnnouncementsListSize(): Int {
        try {
            return announcementsList.size
        } catch (t: Throwable) {
            return 0
        }
    }

    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun init() {
        ConnectivityReceiver.connectivityReceiverListener = this@AnnouncementsActivity
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
     * Initializes adapter class responsible to rendering the list over screen.
     *
     */
    private fun initializeAdapter() {
        announcementsListView.layoutManager = LinearLayoutManager(this)
        announcementsAdapter =
            AnnouncementsAdapter(
                this,
                announcementsList,
                object : AnnouncementsAdapter.OnItemClickListener {
                    override fun onItemClicked(announcementsitem: CourseDetailBean.CourseAnnouncement) {
                        OmnitureTrack.trackAction("course:announcements")
                        val intent = Intent(this@AnnouncementsActivity, AnnouncementsDetailActivity::class.java)
                        intent.putExtra(Constants.ITEM_LINK, "")
                        intent.putExtra(Constants.ANNOUCEMENT_DATA,announcementsitem )
                        intent.putExtra(Constants.ANNOUCEMENT_LIST_DATA, courseDetailData)
                        intent.putExtra(Constants.ITEM_DESCRIPTION, announcementsitem.content)
                        intent.putExtra(Constants.ITEM_TITLE, announcementsitem.title)
                        intent.putExtra(Constants.DATE, announcementsitem.startDate.toString())
                        startActivityForResult(intent, Constants.ACTIVITY_REQUEST)
                    }

                })
        announcementsListView.adapter = announcementsAdapter
    }

    /**
     *  Method responsible to refresh Announcement API
     */
    private fun initialSetUp() {
//        val swipeRefreshColor =
//            ContextCompat.getColor(this@AnnouncementsActivity, R.color.checkBoxColor)
//        this.announcementsSwipeRefresh.setColorSchemeColors(swipeRefreshColor)
//
//        this.announcementsSwipeRefresh.setOnRefreshListener {
////            updateAnnouncementData()
//            announcementsSwipeRefresh.isRefreshing = false
//        }

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

    private fun getAnnouncementDataFromIntent(){
        courseDetailData = intent.getSerializableExtra(Constants.ANNOUCEMENT_LIST_DATA) as CourseDetailBean

        refreshAPI(courseDetailData!!)
//        if(courseDetailData?.newCourseroomData?.courseDetails?.courseAnnouncements?.size!! > 0){
//            announcementsList.addAll(courseDetailData?.newCourseroomData?.courseDetails?.courseAnnouncements!!)
//            announcementsAdapter!!.notifyDataSetChanged()
//        }
//
//            if (announcementsList.size > 0) {
//                if (announcementsAdapter != null) {
//                    announcementsAdapter!!.notifyDataSetChanged()
//                    noInfoTxt.visibility = View.GONE
//                }
//            } else {
//                noInfoTxt.visibility = View.VISIBLE
//            }
    }

    fun refreshAPI(courseData: CourseDetailBean)
    {
        if(courseData?.newCourseroomData?.courseDetails?.courseAnnouncements?.size!! > 0){
            announcementsList.clear()
            announcementsList.addAll(courseData?.newCourseroomData?.courseDetails?.courseAnnouncements!!)
            announcementsAdapter!!.notifyDataSetChanged()
        }

        if (announcementsList.size > 0) {
            if (announcementsAdapter != null) {
                announcementsAdapter!!.notifyDataSetChanged()
                noInfoTxt.visibility = View.GONE
            }
        } else {
            noInfoTxt.visibility = View.VISIBLE
        }
    }

    /**
     * Method creates a server request to load data from server for Announcement API.
     *
     * @param userName
     * @param password
     */
    private fun callAnnouncementsApi(userName: String, password: String) {
        val params = HashMap<String, Any>()
        params[NetworkConstants.USER_NAME] = userName
        params[NetworkConstants.PASSWORD] = password
        params[NetworkConstants.ACTION] = Constants.FIND_CURRENT_COURSE_ENROOLMENT
        params[NetworkConstants.INCLUDE_INSTRUCTOR_PROFILE] = false


        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.Announcement_API,
            params,
            NetworkHandler.METHOD_POST,
            this, null
        )
        networkHandler.setSilentMode(false)
        networkHandler.execute()
    }


    /**
     * Callback method of Network task which executes when communication completed with server
     *
     * @param response : API response received from server.
     * @see Pair
     */
    override fun onNetworkResponse(response: Pair<String, Any>) {
        Util.trace("campus coursefeed first :  " + response.first)
        Util.trace("campus coursefeed second :  " + response.second)

        if (response.first == NetworkConstants.SUCCESS) {
            val gson = Gson()
            val announcementsBean = gson.fromJson<AnnouncementsBean>(
                response.second.toString(),
                AnnouncementsBean::class.java
            )

            var courseAnnouncements = ArrayList<CourseDetailBean.CourseAnnouncement?>()
            val courseId = intent.extras?.getString(Constants.COURSE_ID)

            // match selected course_id with list and map course_annoucement to show
            for (index in 0 until announcementsBean.courseroomData?.currentCourseEnrollments?.size!!) {
                if (courseId.equals(announcementsBean.courseroomData?.currentCourseEnrollments!![index]?.courseIdentifier)) {
                    Util.trace(
                        "announcements News size",
                        announcementsBean?.courseroomData!!.currentCourseEnrollments!![index]?.courseAnnouncements?.size.toString()
                    )
                    courseAnnouncements =
                        announcementsBean?.courseroomData!!.currentCourseEnrollments!![index]?.courseAnnouncements!!
                    break
                }
            }
            announcementsList.clear()
            if (announcementsBean?.errorData == null) {
                announcementsList.addAll(courseAnnouncements)
                if (announcementsList.size > 0) {
                    if (announcementsAdapter != null) {
                        announcementsAdapter!!.notifyDataSetChanged()
                        noInfoTxt.visibility = View.GONE
                    }
                } else {
                    noInfoTxt.visibility = View.VISIBLE
                }
                Util.trace("AnnouncementsBean sting  :  $announcementsBean")
            } else {
//                noInfoTxt.visibility = View.VISIBLE
            }

            announcementsBean?.errorData?.let {
                DialogUtils.showGenericErrorDialog(this)
            }
        } else {
            DialogUtils.showGenericErrorDialog(this)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.ACTIVITY_REQUEST) {
            if(data!=null) {
                courseDetailData = data.getSerializableExtra(Constants.ANNOUCEMENT_LIST_DATA) as CourseDetailBean
                refreshAPI(courseDetailData!!)
            }

        }

    }

    private  fun finishActivity()
    {
        val intent = Intent()
        intent.putExtra(Constants.ANNOUCEMENT_LIST_DATA, courseDetailData)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        finishActivity()
    }

}
