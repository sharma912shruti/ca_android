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
import edu.capella.mobile.android.adapters.DiscussionTopicListAdapter
import edu.capella.mobile.android.bean.DiscussionTopicBean
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import edu.capella.mobile.android.network.NetworkService

import kotlinx.android.synthetic.main.activity_discussion_topic.*

import kotlinx.android.synthetic.main.toolbar_generic.*
import kotlinx.android.synthetic.main.toolbar_generic.view.*


class DiscussionTopicActivity : MenuActivity()/*BaseActivity()*/ ,  ConnectivityReceiver.ConnectivityReceiverListener,
    NetworkListener {


    var discussionTopicList:   ArrayList<DiscussionTopicBean.NewDiscussionData.Topic?> = ArrayList()
    var discussionTopicListAdapter: DiscussionTopicListAdapter? = null

    var courseId = ""
    var forumId  = ""
    var discussionTitle = ""
    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    private  var isInternetConnection: Boolean=false

    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null


    fun getTopicListSize(): Int{
        try{
            return discussionTopicList.size
        }catch (t: Throwable)
        {
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
        //setContentView(R.layout.activity_discussion_topic)
        setContentChildView(R.layout.activity_discussion_topic , true)

        discussionTopicToolbar.genericTitleTxt.text= getString(R.string.threads)

        if(intent.getStringExtra(Constants.BACK_TITLE) != null )
        {
            discussionTopicToolbar.backHeaderTxt.text=intent.getStringExtra(Constants.BACK_TITLE).toString()
            discussionTopicToolbar.backButtonLayout.contentDescription =   getString(R.string.ada_back_button) +intent.getStringExtra(Constants.BACK_TITLE).toString()
        }

       discussionTitle =   intent.getStringExtra(Constants.DISCUSSION_TITLE)



        discussionTopicToolbar.createThreadImg.visibility = View.VISIBLE

        backButtonImg.setOnClickListener { finish() }
        discussionTopicToolbar.createThreadImg.setOnClickListener{
            createNewThread()
        }
        createNewThreadTxt.setOnClickListener{
            createNewThread()
        }



//        discussionTopicToolbar.setOnClickListener { finish() }
        initUi()

        viewDescriptionTxt.setOnClickListener{
            openViewDescriptionLink()
        }

        OmnitureTrack.trackState("course:discussions:threads")
    }

    private fun openViewDescriptionLink()
    {

        val url = Preferences.getValue(PreferenceKeys.VIEW_DESCRIPTION_LINK_PATH)


            val intent = Intent(this, UnitWebViewActivity::class.java)

            intent.putExtra(Constants.URL_FOR_IN_APP, url)
            intent.putExtra(Constants.ORANGE_TITLE, discussionTitle)
            intent.putExtra(Constants.IN_APP_TITLE, "")
            startActivity(intent)
            //overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            // overridePendingTransition( R.anim.slide_in_up, R.anim.no_anim)

        OmnitureTrack.trackAction("course:discussions:threads:view-description")
    }

    private fun createNewThread()
    {
        var threadIntent = Intent(this, CreatePostActivity::class.java)
        threadIntent.putExtra(Constants.FORUM_ID , forumId)
        threadIntent.putExtra(Constants.COURSE_ID , courseId )
        threadIntent.putExtra(Constants.POSTS_TITLE ,  discussionTitle)
        threadIntent.putExtra(Constants.BACK_TITLE , getString(R.string.cancel))
        threadIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
         startActivityForResult(threadIntent, Constants.ACTIVITY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK)
        {
            if (requestCode == Constants.ACTIVITY_REQUEST_CODE)
            {
                val snakeBarMessage = data!!.getStringExtra(Constants.SNAKEBAR_MESSAGE)
                val snakeBarMessageDuration = data!!.getIntExtra(Constants.SNAKEBAR_MESSAGE_DURATION,3000)
//                Util.showSnakeBar(snakeBarMessage, disTopicParent , snakeBarMessageDuration)
                Util.showCustomSnakeBar(this@DiscussionTopicActivity, snakeBarMessage, disTopicParent , snakeBarMessageDuration)
            }

        }

    }

    /**
     * Method initializes tabs with container data and Look and fill.
     *
     */
    private fun initUi()
    {
        initializeAdapter()

        courseId = intent.getStringExtra(Constants.COURSE_ID)
        forumId = intent.getStringExtra(Constants.FORUM_ID)

        topicNameTxt.text = discussionTitle

        discussionTopicListSwipeToRefresh.setColorSchemeColors( getColor(R.color.checkBoxColor))
//        callDiscussionTopicApi(courseId , forumId!!)

        discussionTopicListSwipeToRefresh.setOnRefreshListener {
            callDiscussionTopicApi(courseId , forumId!!)
            discussionTopicListSwipeToRefresh.isRefreshing = false
        }
    }


    private fun initializeAdapter() {

        discussionTopicListView.layoutManager = LinearLayoutManager(this)


        discussionTopicListAdapter = DiscussionTopicListAdapter(this, discussionTopicList , object : DiscussionTopicListAdapter.DiscussioTopicListItemListener{
            override fun onItemClicked(value: DiscussionTopicBean.NewDiscussionData.Topic?)
            {

                showTopicDetail(value )

            }

        })
        discussionTopicListView.adapter = discussionTopicListAdapter
    }

    private fun showTopicDetail(value: DiscussionTopicBean.NewDiscussionData.Topic?)
    {

        var postIntent = Intent(this, DiscussionPostActivity::class.java)
        postIntent.putExtra(Constants.TOPIC_ID , value?.id )
        postIntent.putExtra(Constants.COURSE_ID , courseId )
        postIntent.putExtra(Constants.POSTS_TITLE ,  discussionTitle)
        postIntent.putExtra(Constants.BACK_TITLE , getString(R.string.threads))
        postIntent.putExtra(Constants.POSTS_TITLE ,  discussionTitle)
        postIntent.putExtra(Constants.POSTED_EMPLOYEE_ROLE ,  value?.postedEmployeeRole.toString())

        startActivity(postIntent)
    }
    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun initNetworkBroadcastReceiver()
    {

        ConnectivityReceiver.connectivityReceiverListener = this@DiscussionTopicActivity
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
        callDiscussionTopicApi(courseId , forumId!!)

    }

    /**
     * Factory method of activity, executes when activity goes in paused state.
     *
     */
    override fun onPause() {
        super.onPause()
        releaseConnectivityReceiver()
    }



    private fun releaseConnectivityReceiver()
    {
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver)
            ConnectivityReceiver.connectivityReceiverListener=null
        }
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
                callDiscussionTopicApi(courseId , forumId!!)
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
    private fun callDiscussionTopicApi(courseId: String , forumId: String)
    {
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val params = HashMap<String, Any>()
        params.put(NetworkConstants.TOKEN, loginBean?.authData?.token!!)

        val qStringParams = HashMap<String, Any>()
        qStringParams.put(NetworkConstants.COURSE_IDENTIFIER, courseId)
        qStringParams.put(NetworkConstants.DISCUSSION_FORUM_ID, forumId!!)
        qStringParams.put(NetworkConstants.ACTION, "gettopics")
        qStringParams.put(NetworkConstants.FORMAT_TYPE, "inline")

        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.COURSE_DISCUSSION_TOPIC_API + NetworkService.getQueryString(qStringParams),
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
    override fun onNetworkResponse(response: android.util.Pair<String, Any>)
    {
        Util.trace("DiscussionTopicBean : " + response.second )

        if(response.first == NetworkConstants.SUCCESS)
        {
            val gson = Gson()
            var discussionTopicListBean = gson.fromJson<DiscussionTopicBean>(response.second.toString(), DiscussionTopicBean::class.java)
            updateDiscussionTopicList(discussionTopicListBean)
        }else
        {
            DialogUtils.showGenericErrorDialog(this)
        }
    }


    private fun updateDiscussionTopicList(discussionTopicBeanData: DiscussionTopicBean )
    {
        try {

            val discussionTopicBean = discussionTopicBeanData

            if (discussionTopicBean?.errorData != null) {

                //DialogUtils.showGenericErrorDialog(this)
                noDiscussionTopicLayout.visibility = View.VISIBLE
                return
            }


            var topiclist: List<DiscussionTopicBean.NewDiscussionData.Topic?>? = discussionTopicBean.newDiscussionData?.topics

            if ((topiclist == null) ||
                (topiclist!!.size!! <= 0)
            ) {
                noDiscussionTopicLayout.visibility = View.VISIBLE
                return
            }

            discussionTopicList.clear()
            discussionTopicList.addAll(topiclist.reversed())

            if (topiclist.isNotEmpty())
            {
                if (discussionTopicListAdapter != null)
                {
                    noDiscussionTopicLayout.visibility = View.GONE
                    
                    //discussionTopicListAdapter!!.setItems(discussionTopicList)
                    discussionTopicListAdapter!!.notifyDataSetChanged()
                     discussionTopicListView.adapter = discussionTopicListAdapter
                    // initializeAdapter()
                    // noInfoTxt.visibility = View.GONE
                }
            } else {
                noDiscussionTopicLayout.visibility = View.VISIBLE
            }
        }catch (t: Throwable)
        {
            Util.trace("DiscussionTopicLayout data error : $t")
            t.printStackTrace()
        }

    }


}
