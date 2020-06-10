package edu.capella.mobile.android.activity

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Pair
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.adapters.DiscussionForumListAdapter
import edu.capella.mobile.android.bean.DiscussionForumBean
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import edu.capella.mobile.android.network.NetworkService
import kotlinx.android.synthetic.main.activity_discussion_forum.*
import kotlinx.android.synthetic.main.toolbar_generic.*
import kotlinx.android.synthetic.main.toolbar_generic.view.*


class DiscussionForumActivity : MenuActivity()/*BaseActivity() */,
    ConnectivityReceiver.ConnectivityReceiverListener,
    NetworkListener {


    var discussionList: ArrayList<DiscussionForumListAdapter.DiscussionForumCollector?> =
        ArrayList()
    var discussionListAdapter: DiscussionForumListAdapter? = null

    var courseId = ""
    var courseNumberId: Int? = null
    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    private var isInternetConnection: Boolean = false

    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null


    fun getDiscussionForumListSize(): Int {
        try {
            return discussionList.size
        } catch (t: Throwable) {
            return 0
        }
    }


    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_discussion_forum)
        setContentChildView(R.layout.activity_discussion_forum, true)

        discussionForumToolbar.genericTitleTxt.text = getString(R.string.discussion)

        discussionForumToolbar.backButtonLayout.contentDescription =   getString(R.string.ada_back_button) + getString(R.string.back)

        backButtonImg.setOnClickListener { finish() }

        initUi()
        OmnitureTrack.trackState("course:discussions:forums")
    }


    /**
     * Method initializes tabs with container data and Look and fill.
     *
     */
    private fun initUi() {
        initializeAdapter()

        courseId = intent.getStringExtra(Constants.COURSE_ID)
        courseNumberId = intent.getIntExtra(Constants.COURSE_NUMBER_ID, 0)

        discussionForumListSwipeToRefresh.setColorSchemeColors(getColor(R.color.checkBoxColor))
        // callDiscussionForumApi(courseId , courseNumberId!!)

        /*go_to_course_menu.setOnClickListener {
            finish()
        }*/
        discussionForumListSwipeToRefresh.setOnRefreshListener {
            callDiscussionForumApi(courseId, courseNumberId!!)
            discussionForumListSwipeToRefresh.isRefreshing = false

        }
    }

    private fun initializeAdapter() {

        discussionForumListView.layoutManager = LinearLayoutManager(this)


        discussionListAdapter = DiscussionForumListAdapter(
            this,
            discussionList,
            object : DiscussionForumListAdapter.DiscussioForumListItemListener {
                override fun onItemClicked(value: DiscussionForumListAdapter.DiscussionForumCollector?) {

                    if(value?.isFooter == true)
                    {
                            setResult(Activity.RESULT_OK)
                            this@DiscussionForumActivity.finish()
                    }else {
                        showTopicScreen(value)
                    }
                }

            })
        discussionForumListView.adapter = discussionListAdapter
    }

    private fun showTopicScreen(value: DiscussionForumListAdapter.DiscussionForumCollector?) {
        //extractHref(value?.forumBean?.descriptionText)
        Util.extractHrefForViewDescription(value?.forumBean?.descriptionText)
        var topicIntent = Intent(this, DiscussionTopicActivity::class.java)
        topicIntent.putExtra(Constants.FORUM_ID, value?.forumBean?.id)
        topicIntent.putExtra(Constants.COURSE_ID, courseId)
        topicIntent.putExtra(Constants.DISCUSSION_TITLE, value?.forumBean!!.title)
        topicIntent.putExtra(Constants.BACK_TITLE, getString(R.string.discussion))

        startActivity(topicIntent)
    }



    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun initNetworkBroadcastReceiver() {

        ConnectivityReceiver.connectivityReceiverListener = this@DiscussionForumActivity
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
        callDiscussionForumApi(courseId, courseNumberId!!)
    }

    /**
     * Factory method of activity, executes when activity goes in paused state.
     *
     */
    override fun onPause() {
        super.onPause()
        releaseConnectivityReceiver()
    }


    private fun releaseConnectivityReceiver() {
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver)
            ConnectivityReceiver.connectivityReceiverListener = null
        }
    }

    /**
     * Method triggered by broadcast receiver which is listening for Network state.
     *
     * @param isConnected : Tells whether internet is available or not
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        isInternetConnection = isConnected

        if(isConnected == true && isNetworkFailedDueToConnectivity!=null && isNetworkFailedDueToConnectivity==true)
        {
            isNetworkFailedDueToConnectivity = null
            if(this.getCurrentNetworkQueueSize()==0) {
                DialogUtils.releaseGenericDialog()
                callDiscussionForumApi(courseId, courseNumberId!!)
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
     * Method call LearnerProfile api to show details in PROFILE and IDCARD tabs.
     *
     */
    private fun callDiscussionForumApi(courseId: String, coursNumId: Int) {

        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val params = HashMap<String, Any>()
        params.put(NetworkConstants.TOKEN, loginBean?.authData?.token!!)


        //POST /mobile-feed/json/newdiscussions.feed?action=getAllforums&courseidentifier=TESTSUB1331_006292_1_1201_1_03&formattype=text&courseid=7001 HTTP/1.1

        val qStringParams = HashMap<String, Any>()
        qStringParams.put(NetworkConstants.COURSE_IDENTIFIER, courseId)
        qStringParams.put(NetworkConstants.COURSE_NUMBER_ID, courseNumberId!!)
        qStringParams.put(NetworkConstants.ACTION, "getAllforums")
        qStringParams.put(NetworkConstants.FORMAT_TYPE, "text")


        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.COURSE_DISCUSSION_FORUM_API + NetworkService.getQueryString(
                qStringParams
            ),
            params,
            NetworkHandler.METHOD_POST,
            this,
            null
        )

        networkHandler.execute()


    }

    /**
     * Its network listener methods invoked after network service complete its task.
     *
     *  @param response : response of network service
     *
     *  @see Pair
     *  @see NetworkHandler
     *
     */
    override fun onNetworkResponse(response: android.util.Pair<String, Any>) {
        Util.trace("DiscussionForumBean : " + response.second)
        try {
            if (response.first == NetworkConstants.SUCCESS) {
                /*try {
                    if (intent.extras?.getString(Constants.IS_GP_2_COURSE) != null)
                    {
                        yellodig_layout.visibility = View.VISIBLE
                    } else {
                        yellodig_layout.visibility = View.GONE
                    }
                } catch (t: Throwable) {
                }*/
                val gson = Gson()
                var discussionListBean = gson.fromJson<DiscussionForumBean>(
                    response.second.toString(),
                    DiscussionForumBean::class.java
                )
                updateDiscussionGroupList(discussionListBean)
            } else {
                DialogUtils.showGenericErrorDialog(this)
            }
        }catch (t:Throwable){
            DialogUtils.showGenericErrorDialog(this)
        }
        }

    private fun updateDiscussionGroupList(discussionForumBeanData: DiscussionForumBean) {
        try {

            val discussionForumBean = discussionForumBeanData

            if (discussionForumBean?.errorData != null) {

                DialogUtils.showGenericErrorDialog(this)
                return
            }

            // if(matesListBean?.classmatesData?.courseMembers!![0]!!.member!!.size!! <= 0)
            //var f : DiscussionForumBean.NewDiscussionData.GroupDiscussion.Forum
            var grouplist: List<DiscussionForumBean.NewDiscussionData.GroupDiscussion?>? =
                discussionForumBean.newDiscussionData?.groupDiscussions

            if ((grouplist == null) ||
                (grouplist!!.size!! <= 0)
            ) {
                noDiscussionForumLayout.visibility = View.VISIBLE
                return
            }
            var commonList = ArrayList<DiscussionForumListAdapter.DiscussionForumCollector>()

            for (item in grouplist) {
                var group = item
                var groupTitle = group?.groupTitle
                var groupId = group?.groupId
                var forum = group?.forums

                if (!groupTitle.equals("defaultForums", false)) {
                    var tmpCollector = DiscussionForumListAdapter.DiscussionForumCollector()
                    tmpCollector.groupTitle = groupTitle
                    tmpCollector.isTitleType = true

                    try {
                        var itm = commonList[commonList.size - 1]
                        itm.showSeparator = false
                        commonList[commonList.size - 1] = itm
                    } catch (t: Throwable) {
                    }

                    commonList.add(tmpCollector)
                }

                for (f in forum!!) {
                    var fCollector = DiscussionForumListAdapter.DiscussionForumCollector()
                    fCollector.groupTitle = groupTitle
                    fCollector.isTitleType = false
                    fCollector.forumBean = f
                    commonList.add(fCollector)
                }
            }

            discussionList.clear()
            discussionList.addAll(commonList)

            if (commonList.size > 0) {

                if (discussionListAdapter != null)
                {
                    if (intent.extras?.getString(Constants.IS_GP_2_COURSE) != null)
                    {
                        //YELLOW DIG
                        var fCollector = DiscussionForumListAdapter.DiscussionForumCollector()
                        fCollector.groupTitle = ""
                        fCollector.isFooter = true
                        discussionList.add(fCollector)
                    }

                    discussionListAdapter!!.notifyDataSetChanged()
                    discussionForumListView.adapter = discussionListAdapter
                    // noInfoTxt.visibility = View.GONE
                }
            } else {
                noDiscussionForumLayout.visibility = View.VISIBLE
            }
        } catch (t: Throwable) {
            Util.trace("DiscussionForumLayout data error : $t")
            t.printStackTrace()
        }


    }


}
