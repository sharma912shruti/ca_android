package edu.capella.mobile.android.activity

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Html
import android.util.Pair
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.adapters.GettingStartedListAdapter
import edu.capella.mobile.android.bean.GettingStartedBean
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.bean.MuleSoftSession
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.HubbleNetworkConstants
import edu.capella.mobile.android.network.MuleSoftNetworkConstants
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import edu.capella.mobile.android.task.StickyInfoGrabber
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.utils.Constants.COURSE_ID
import kotlinx.android.synthetic.main.activity_getting_started.*
import kotlinx.android.synthetic.main.activity_library.networkLayout
import kotlinx.android.synthetic.main.toolbar_generic.view.*
import java.io.File

/**
 * GettingStartedActivity.kt :  Screen responsible for showing the list of Getting Started list
 *
 * @author  :  Didarul.Khan
 * @version :  1.0
 * @since   :  01-27-2020
 * @created : 04-01-20
 *
 */
class GettingStartedActivity: MenuActivity()/*BaseActivity()*/, ConnectivityReceiver.ConnectivityReceiverListener, NetworkListener {


    var courseID = ""
    var courseLink = ""
    var messageLink = ""

    var gettingStartedList: ArrayList<GettingStartedBean.GettingStarted.GettingStartedContent?> =
        ArrayList()
    var gettingStartedListAdapter: GettingStartedListAdapter? = null

    var overViewTitle = ""

    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    private var isInternetConnection: Boolean = false


    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null


    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_getting_started)
        setContentChildView(R.layout.activity_getting_started , true)
        gettingStartedToolbar.genericTitleTxt.text = getString(R.string.getting_started)

        gettingStartedToolbar.backButtonLayout.contentDescription =   getString(R.string.ada_back_button) + getString(R.string.back)

        this.courseID = intent.getStringExtra(COURSE_ID)

        if(intent.getStringExtra(Constants.COURSE_MESSAGE_LINK) != null)
        {
           this.messageLink = intent.getStringExtra(Constants.COURSE_MESSAGE_LINK)
        }

        if(intent.getStringExtra(Constants.COURSE_LINK) != null)
        {
            this.courseLink = intent.getStringExtra(Constants.COURSE_LINK)
        }

      //  this.courseID = "ED5346_006248_1_1197_1_01"
        initUi()

        OmnitureTrack.trackState("course:getting-started")
    }

    /**
     * Method initializes tabs with container data and Look and fill.
     *
     */
    private fun initUi() {

        initializeAdapter()
        //callSyllabusListApi("TEST-FP6001_007575_1_1201_OEE_03")
        callGettingStartedApi(this.courseID)


        this.gettingStartedListSwipeToRefresh.setColorSchemeColors( getColor(R.color.checkBoxColor))

        // according to flex path show learner activity

        /*val isFpPath = intent.getBooleanExtra(Constants.WHICH_FLEX_PATH,false)
        if(isFpPath){
            learnerActivitiesTitle.visibility = View.GONE
        } else{
            learnerActivitiesTitle.visibility = View.VISIBLE
        }*/

        gettingStartedListSwipeToRefresh.setOnRefreshListener {
            callGettingStartedApi(this.courseID)
            gettingStartedListSwipeToRefresh.isRefreshing = false
        }
    }


    private fun callGettingStartedApi(courseId: String)
    {

        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)
        val stickyHeader : java.util.HashMap<String, Any?>? = NetworkHandler.getStickySessionHeader(Preferences.getValue(PreferenceKeys.MULESOFT_ACCESS_TOKEN))

        var syllabusUrl = HubbleNetworkConstants.getUrl(
            MuleSoftNetworkConstants.MULE_GETTING_STARTED_CONTENT,
            "{courseIdentifier}",
            this.courseID
        )
        val params = HashMap<String, Any>()
        val networkHandler = NetworkHandler(
            this,
            syllabusUrl,
            params,
            NetworkHandler.METHOD_GET,
            this,
            stickyHeader
        )

        networkHandler.execute()


    }










    private fun initializeAdapter() {
        gettingStartedListView.layoutManager = LinearLayoutManager(this)

        gettingStartedListAdapter = GettingStartedListAdapter(this, gettingStartedList, object :
            GettingStartedListAdapter.GettingStartedItemListener {
            override fun onItemClicked(value: GettingStartedBean.GettingStarted.GettingStartedContent?) {

                if(value?.contentType!!.contains("REG")) {
                    openItem(value)
                }
                else
                {
                    if(value?.forumId!=null)
                    {
                        Util.extractHrefForViewDescription(value?.content)


                        var topicIntent = Intent(this@GettingStartedActivity, DiscussionTopicActivity::class.java)
                        topicIntent.putExtra(Constants.FORUM_ID, value?.forumId)
                        topicIntent.putExtra(Constants.COURSE_ID, courseID)
                        topicIntent.putExtra(Constants.DISCUSSION_TITLE, value?.title)
                        topicIntent.putExtra(Constants.BACK_TITLE, getString(R.string.back))

                        startActivity(topicIntent)
                    }
                }

            }

        })
        gettingStartedListView.adapter = gettingStartedListAdapter
    }


    private fun openItem(value: GettingStartedBean.GettingStarted.GettingStartedContent?)
    {
        var linkToOpen = Util.extractHref_recursive(value?.content)



        if(linkToOpen != null)
        {
            OmnitureTrack.trackAction("getting-started:open-detail")

            if(value?.title!!.contains("Course Overview" , true))
            {
                callOverViewDetail(messageLink,value.content!!, value.title!!)
              //  buildGettingStartedOverviewDetailURLs(messageLink,value.content!!, value.title!!)
            }else
            {
                buildAssignmentDetailURL(messageLink,value.content!!, value.title!!)
            }

           /* val intent = Intent(this@GettingStartedActivity, UnitWebViewActivity::class.java)


            intent.putExtra(Constants.URL_FOR_IN_APP, linkToOpen)
            intent.putExtra(Constants.ORANGE_TITLE, Html.fromHtml( Util.str(value?.title) , Html.FROM_HTML_MODE_LEGACY).toString().trim())
            intent.putExtra(Constants.IN_APP_TITLE, "Getting Started")
            intent.putExtra(Constants.BACK_TITLE, "Getting Started")
            intent.putExtra(Constants.OVERRIDE_TITLE, true)
            intent.putExtra(Constants.PAGE_WARNING_HIDE, true)


            startActivity(intent)
            OmnitureTrack.trackAction("course:getting-started:detail")*/
            // overridePendingTransition(R.anim.slide_in_up, R.anim.no_anim)
        }
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

        ConnectivityReceiver.connectivityReceiverListener = this@GettingStartedActivity
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

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

       if(isConnected == true && isNetworkFailedDueToConnectivity!=null && isNetworkFailedDueToConnectivity==true)
        {
            isNetworkFailedDueToConnectivity = null
            if(this.getCurrentNetworkQueueSize()==0) {
                DialogUtils.releaseGenericDialog()
                callGettingStartedApi(this.courseID)
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

    override fun onNetworkResponse(response: Pair<String, Any>) {
        Util.trace("Getting Started : " + response.second)

        if (response.first == NetworkConstants.SUCCESS) {
            val gson = Gson()
            var newResponse =response.second.toString().replace("getting-started", "gettingstarted")
            var gettingStartedBean = gson.fromJson<GettingStartedBean>(
                newResponse,
                GettingStartedBean::class.java
            )
            updateGettingStartedList(gettingStartedBean)

        } else {
            DialogUtils.showGenericErrorDialog(this)
        }
    }


    private fun updateGettingStartedList(gettingStartedBean: GettingStartedBean?) {

        try {
            Util.trace("Getting method started")
            val gettingStartedListBean = gettingStartedBean


            // if(matesListBean?.classmatesData?.courseMembers!![0]!!.member!!.size!! <= 0)
            /*if ((gettingStartedListBean?.gettingstarted?.gettingStartedContent == null) ||
                (gettingStartedListBean?.gettingstarted?.gettingStartedContent!!.isEmpty())
            ) {
                noGettingStartedLayout.visibility = View.VISIBLE

                return
            }*/

            /*var listNew: ArrayList<GettingStartedBean.GettingStarted.GettingStartedContent> =
                gettingStartedListBean?.gettingstarted?.gettingStartedContent as ArrayList<GettingStartedBean.GettingStarted.GettingStartedContent>*/
            var listNew  =   gettingStartedListBean?.gettingstarted?.gettingStartedContent
            /*var newSortedList = listNew.sortedWith(
                compareBy(
                    GettingStartedBean.GettingStarted.GettingStartedContent::title,
                    GettingStartedBean.GettingStarted.GettingStartedContent::content
                )
            )*/
            Util.trace("List size is ${listNew?.size}")
            gettingStartedList.clear()
            // syllabusList.addAll(listNew)

            if(listNew!= null )
            {
                    for(item in listNew)
                    {
                        if((item!!.title!!.contains("Getting Started", true) == false)
                            && (item!!.title!!.contains("Accordion Toolbar" , true) == false)
                            && (item!!.title!!.contains("Print" , true) == false))
                        {
                            gettingStartedList.add(item)
                        }
                    }
            }




            if (gettingStartedList.size > 0) {

                gettingStartedListLayout.visibility = View.VISIBLE
                noGettingStartedLayout.visibility = View.GONE
                if (gettingStartedListAdapter != null) {
                    gettingStartedListAdapter!!.notifyDataSetChanged()
                    // noInfoTxt.visibility = View.GONE
                }
            } else {
                noGettingStartedLayout.visibility = View.VISIBLE
            }
        } catch (t: Throwable) {
            Util.trace("Getting Started data error : $t")
            t.printStackTrace()
            noGettingStartedLayout.visibility = View.VISIBLE
        }

    }


  /*  fun buildGettingStartedOverviewDetailURLs(messageLink:String , content:String , title:String)
    {
        overViewTitle = title

        var capellaDomain = ".capella.edu/"

        var domain:String? = null

        if(messageLink.contains(capellaDomain , true))
        {
            domain = messageLink.substring(0, messageLink.indexOf(capellaDomain) + capellaDomain.length)
        }

        // Find the URI
       var list =  Util.findURL_list_inHREF(content)

        var finalUrls : ArrayList<String> = ArrayList<String>()
        if(list !=null)
        {
            if(list.size ==3 || list.size==4)
            {
                 for(url in list)
                 {
                     if (url.indexOf('@') > -1 && url.length > 10)
                     {
                         var newUrl = url.substring(url.lastIndexOf("@") + 1, url.length)
                         finalUrls.add(domain+newUrl)
                     }
                 }
            }
        }
        var overViewUrlLength = finalUrls.size


        var blackboardDomain = Util.findBlackboardDomain(finalUrls[0])


        val urlIndex  = if (overViewUrlLength == 2) 0 else 1
        var overviewUrl = finalUrls[urlIndex]
        overviewUrl = overviewUrl.replace("\"","");


       *//* if(finalUrls.size <= 3)
        {
             overviewUrl = finalUrls.get(finalUrls.size-1);
        }else
        {
             overviewUrl = finalUrls.get(2);
        }*//*

        finalList.clear()
        htmlContents.clear()
        //finalList.addAll(finalUrls)
        finalList.addAll(finalUrls.subList(urlIndex, finalUrls.size))
        processOverviewUrl(overviewUrl)


     *//*if(overviewUrl!="")
        {
           // processOverviewUrl(overviewUrl)

             val intent = Intent(this@GettingStartedActivity, GettingStartedDetailActivity::class.java)
            intent.putExtra(Constants.URL_FOR_IN_APP, overviewUrl)
            intent.putExtra(Constants.ORANGE_TITLE, Html.fromHtml(Util.str(title), Html.FROM_HTML_MODE_LEGACY).toString().trim() )
            intent.putExtra(Constants.IN_APP_TITLE, "Getting Started")
            intent.putExtra(Constants.BACK_TITLE, "Getting Started")
            intent.putExtra(Constants.OVERRIDE_TITLE, true)
            //intent.putExtra(Constants.PAGE_WARNING_HIDE, true)

            startActivity(intent)
            OmnitureTrack.trackAction("course:getting-started:detail")
        }
*//*

    }*/

    fun buildAssignmentDetailURL(messageLink:String , content:String , title:String)
    {
        var capellaDomain = ".capella.edu/"

        var domain:String? = null

        if(messageLink.contains(capellaDomain , true))
        {
            domain = messageLink.substring(0, messageLink.indexOf(capellaDomain) + capellaDomain.length)
        }

        // Find the URI
        var uriList =  Util.findURL_list_inHREF(content)

        var uri = uriList!!.get(0)

        if ( uri.indexOf('@') > -1 && uri.length > 10)
        {
            uri = uri.substring(uri.lastIndexOf('@') + 1, uri.length);
        } else
        {
            // Checking the below condition if datapull is giving different metadata url from href.
            if(uri.indexOf("bbcswebdav") > -1)
            {
                uri = uri.substring(uri.indexOf("bbcswebdav"), uri.length)
            }else
            {
                uri = ""
            }

        }
        if(domain!="" && uri!="")
        {
            var finUrl =  domain + uri
            if(finUrl!="") {
                val intent = Intent(this@GettingStartedActivity, GettingStartedDetailActivity::class.java)


                intent.putExtra(Constants.URL_FOR_IN_APP, finUrl)
                intent.putExtra(
                    Constants.ORANGE_TITLE,
                    Html.fromHtml(Util.str(title), Html.FROM_HTML_MODE_LEGACY).toString().trim()
                )
                intent.putExtra(Constants.IN_APP_TITLE, "Getting Started")
                intent.putExtra(Constants.BACK_TITLE, "Getting Started")
                intent.putExtra(Constants.OVERRIDE_TITLE, true)
                //intent.putExtra(Constants.PAGE_WARNING_HIDE, true)


                startActivity(intent)
                OmnitureTrack.trackAction("course:getting-started:detail")
            }
        }


    }

  /* var htmlContents  = ArrayList<String>()
    var finalList   = ArrayList<String>()

    fun processOverviewUrl(url:String )
    {

        if(finalList.size !=0 &&  finalList.contains(url))
        {
            finalList.remove(url)

            val stickyWork = StickyInfoGrabber(this)
            stickyWork.generateReturnMuleSoftStickySessionForTargetUrl(url,
                object : NetworkListener {
                    override fun onNetworkResponse(response: Pair<String, Any>)
                    {

                        if (response.first == NetworkConstants.SUCCESS)
                        {
                            Util.trace("Token Is : " + response.second)

                            val gson = Gson()
                            val muleSoftSession = gson.fromJson<MuleSoftSession>(response.second.toString(), MuleSoftSession::class.java)

                            var finalUrlToOpen  = BuildConfig.STICKY_FORWARD_URL + File.separator + muleSoftSession.token

                            callGetForOverView(finalUrlToOpen)
                        }


                    }

                })
        }else
        {
            //contents received
        }


    }*/
/*
    fun callGetForOverView(urlToOpen :String)
    {
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)


         val header  = HubbleNetworkConstants.buildRequestHeaderForGettingStarted()

        val params = HashMap<String, Any>()

        val hparams = HashMap<String, Any?>()

        var assignmentsUrl = urlToOpen

        Util.trace(" URL  :$assignmentsUrl")

        val networkHandler = NetworkHandler(
            this,
            assignmentsUrl,
            params,
            NetworkHandler.METHOD_GET,
            htmlNetworkListener,
            header
        )

        networkHandler.execute()
    }


    private val htmlNetworkListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: android.util.Pair<String, Any>)
        {
            Util.trace("Response htmlNetworkListener " + response.second.toString())
            try {
                htmlContents.add(response.second.toString())
                if(finalList.size>0)
                {
                    processOverviewUrl(finalList[0]  )
                }else
                {
                    callOverViewDetail()
                }

            }catch (t: Throwable){
                Util.trace("Heell $t")
                t.printStackTrace()
            }
        }
    }*/

    private fun callOverViewDetail(messageLink:String , content:String , title:String)
    {
        val intent = Intent(this@GettingStartedActivity, GettingStartedOverViewActivity::class.java)

        intent.putExtra(Constants.COURSE_MESSAGE_LINK, messageLink)
        intent.putExtra(Constants.HTML_CONTENTS, content)
        intent.putExtra(
            Constants.ORANGE_TITLE,
            Html.fromHtml(Util.str(title), Html.FROM_HTML_MODE_LEGACY).toString().trim()
        )
        intent.putExtra(Constants.IN_APP_TITLE, "Getting Started")
        intent.putExtra(Constants.BACK_TITLE, "Getting Started")
        intent.putExtra(Constants.OVERRIDE_TITLE, true)
        //intent.putExtra(Constants.PAGE_WARNING_HIDE, true)

        startActivity(intent)
        OmnitureTrack.trackAction("course:getting-started:detail")
    }

}

