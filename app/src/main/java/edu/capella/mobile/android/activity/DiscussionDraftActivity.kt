package edu.capella.mobile.android.activity
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Pair
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.utils.Constants.ACTIVITY_REQUEST
import edu.capella.mobile.android.adapters.DiscussionDraftAdapter
import edu.capella.mobile.android.bean.CourseListBean
import edu.capella.mobile.android.bean.DiscussionDraftBean
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import kotlinx.android.synthetic.main.activity_announcements.networkLayout
import kotlinx.android.synthetic.main.activity_announcements.noInfoTxt
import kotlinx.android.synthetic.main.activity_discussion_draft.*
import kotlinx.android.synthetic.main.toolbar_announcement.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set


/**
 * DiscussionDraftActivity.kt :  Screen responsible for showing the list of DiscussionDraft
 *
 * @author  :  Kush.pandya
 * @version :  1.0
 * @since   :  03-02-2020
 *
 *
 */
class DiscussionDraftActivity : MenuActivity() /* BaseActivity()*/, NetworkListener,
    ConnectivityReceiver.ConnectivityReceiverListener {

    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null

    private var checkSaveDraft: Boolean? = false

    private var message: String? = ""

    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    public  var isInternetConnection: Boolean=false

    /**
     * List of discussion draft received from server.
     */
    var discussionDraftList = ArrayList<DiscussionDraftBean.Draft?>()   //ArrayList<Any>()

    /**
     * Adapter for handling list behaviour.
     * @see RecyclerView.Adapter
     */
    var discussionDraftAdapter: DiscussionDraftAdapter? = null

    private var courseData: CourseListBean.NewCourseroomData.CurrentCourseEnrollment? = null

    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_discussion_draft)
        setContentChildView(R.layout.activity_discussion_draft  , true)
//
        initialiseToolbar()
        initialSetUp()
        initializeAdapter()
        updatDiscussionDraftData()
        OmnitureTrack.trackState("course:discussions:drafts")

    }

    /**
     *  Method creates a server request to load data from server for discussion draft
     */
    @SuppressLint("SetTextI18n")
    private fun updatDiscussionDraftData() {
        OmnitureTrack.trackAction("course:discussions:drafts")
        if (Util.isInternetOn(this)) {
            val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
            val gson = Gson()
            val loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

            callDiscussionDraftApi(
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
        headerTxt.text = getString(R.string.discussion_drafts)
//        backButton.setOnClickListener { finish() }
//        backTxt.setOnClickListener { finish() }

        backButtonLl.setOnClickListener { onBackPressed()  }
        backButtonLl.contentDescription=resources.getString(R.string.ada_back_button)+getString(R.string.back)

//        backButton.contentDescription=resources.getString(R.string.ada_back_button)+getString(R.string.back)
//        backTxt.contentDescription=resources.getString(R.string.ada_back_button)+getString(R.string.back)
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
                updatDiscussionDraftData()
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



    fun getDiscussionDraftListSize(): Int {
        try {
            return discussionDraftList.size
        } catch (t: Throwable) {
            return 0
        }
    }

    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun init() {
        ConnectivityReceiver.connectivityReceiverListener = this@DiscussionDraftActivity
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
        try {
            discussionDraftListView.layoutManager = LinearLayoutManager(this)
//        discussionDraftListView.layoutManager.
//        discussionDraftListView.layoutManager.setStackFromEnd(true);


//            discussionDraftList.reverse();
            discussionDraftAdapter =
                DiscussionDraftAdapter(
                    this,
                    discussionDraftList,
                    object : DiscussionDraftAdapter.OnItemClickListener {

                        override fun onItemClicked(draftData: DiscussionDraftBean.Draft) {
                            OmnitureTrack.trackAction("course:discussions:drafts-edit")
                            val intent =
                                Intent(this@DiscussionDraftActivity, EditDraftActivity::class.java)
                            intent.putExtra(Constants.DRAFT_DATA, draftData)
                            intent.putExtra(Constants.COURSE_DATA, courseData)
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
//                        startActivity(intent)
                            startActivityForResult(intent, ACTIVITY_REQUEST)


                        }

                    })
            discussionDraftListView.adapter = discussionDraftAdapter
        }catch (e : Exception)
        {

        }
    }

    /**
     *  Method responsible to refresh discussion draft  API
     */
    private fun initialSetUp() {
        courseData =
            intent.getSerializableExtra(Constants.COURSE_DATA) as CourseListBean.NewCourseroomData.CurrentCourseEnrollment

        this.discussionDrafSwipeRefresh.setColorSchemeColors(ContextCompat.getColor(this@DiscussionDraftActivity, R.color.checkBoxColor))
        this.discussionDrafSwipeRefresh.setOnRefreshListener {
            updatDiscussionDraftData()
            discussionDrafSwipeRefresh.isRefreshing = false
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
     * Method creates a server request to load data from server for Discussion Draft API.
     *
     * @param userName
     * @param password
     */
    private fun callDiscussionDraftApi(userName: String, password: String) {
        val params = HashMap<String, Any>()
        params[NetworkConstants.USER_NAME] = userName
        params[NetworkConstants.PASSWORD] = password
        params[NetworkConstants.ACTION] = Constants.GET_DRAFT
        params[NetworkConstants.COURSE_IDENTIFIER] = courseData!!.courseIdentifier!!
        params[NetworkConstants.FORMATTYPE]=Constants.HTML
        params[NetworkConstants.COURSEID]="_164007_1"

        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.DISCUSSION_DRAFT_API,
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
        try {

            Util.trace("discussion draft first :  " + response.first)
            // Util.trace("discussion draft second :  " + response.second)

            if (response.first == NetworkConstants.SUCCESS) {
                val gson = Gson()
                val discussionBean = gson.fromJson<DiscussionDraftBean>(
                    response.second.toString(),
                    DiscussionDraftBean::class.java
                )
                if (discussionBean != null && discussionBean?.newDiscussionData != null && discussionBean.newDiscussionData!!.drafts != null) {
                    var courseDrafts = discussionBean.newDiscussionData!!.drafts

                    if (discussionBean?.errorData == null) {

                        val newSortedList = courseDrafts
                            .sortedWith(compareBy<DiscussionDraftBean.Draft?> {
                                Util.getDateOject(
                                    Util.getDateNotPMAM(
                                        it!!.modifiedDate,
                                        Constants.DATE_FORMAT_AM_PM
                                    )!!
                                )
                            }
                                .thenBy {
                                    Util.getDateOject(
                                        Util.getDateNotPMAM(
                                            it!!.modifiedDate,
                                            Constants.DATE_FORMAT_AM_PM
                                        )!!
                                    )
                                })


//                        val newSortedList = courseDrafts
//                            .sortedByDescending {
//                                Util.getDateOject(
//                                    Util.getDateNotPMAM(
//                                        it!!.modifiedDate,
//                                        Constants.DATE_FORMAT_AM_PM
//                                    )!!
//                                )
//                            }




                        discussionDraftList.clear()
//                discussionDraftList.addAll(courseDrafts)
                        discussionDraftList.addAll(newSortedList)

//                discussionDraftList.sortBy{it!!.modifiedDate}

//                        discussionDraftList.reverse();
                        if (discussionDraftList.size > 0) {
                            if (discussionDraftAdapter != null) {
                                discussionDraftAdapter!!.notifyDataSetChanged()
                                noInfoTxt.visibility = View.GONE

                                if (checkSaveDraft!!) {
                                    checkSaveDraft = false

//                                    Util.showSnakeBar(message!!, parentRL, 3000)
                                    Util.showCustomSnakeBar(this@DiscussionDraftActivity, message!!, parentRL, 3000)

                                }
                            }
                        } else {
                            if (discussionDraftAdapter != null) {
                                discussionDraftAdapter!!.notifyDataSetChanged()
                            }
                            if (checkSaveDraft!!) {
                                checkSaveDraft = false

//                                Util.showSnakeBar(message!!, parentRL, 3000)

                                Util.showCustomSnakeBar(this@DiscussionDraftActivity, message!!, parentRL, 3000)

                            }
                            noInfoTxt.visibility = View.VISIBLE
                        }
                    } else {
//                noInfoTxt.visibility = View.VISIBLE
                    }

                    discussionBean?.errorData?.let {
                        DialogUtils.showGenericErrorDialog(this)
                    }
                } else {
                    DialogUtils.showGenericErrorDialog(this)
                }
            }else
            {
                DialogUtils.showGenericErrorDialog(this)
            }
        }catch (t: Throwable)
        {
            Util.trace("Issue found $t")
            t.printStackTrace()
            DialogUtils.showGenericErrorDialog(this)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ACTIVITY_REQUEST) {
            if(data!=null) {
                message = data!!.getStringExtra(Constants.EDITS_SAVED)
                checkSaveDraft=true

                updatDiscussionDraftData()
            }



        }

    }


}
