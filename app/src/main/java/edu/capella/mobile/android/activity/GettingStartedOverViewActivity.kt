package edu.capella.mobile.android.activity


import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Pair
import android.view.MotionEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.app.CapellaApplication
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.bean.MuleSoftSession
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.HubbleNetworkConstants
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import edu.capella.mobile.android.task.StickyInfoGrabber
import kotlinx.android.synthetic.main.activity_getting_startedoverview_webview.*
import kotlinx.android.synthetic.main.page_warning_layout.view.*

import kotlinx.android.synthetic.main.toolbar_generic.*
import java.io.File


/**
 * CommonWebViewActivity.kt :  Screen working as in-app browser, opens given url in WebView
 *
 * @author  :  Jayesh.lahare
 * @version :  1.0
 * @since   :  03-02-2020
 *
 * @see WebView
 */
class GettingStartedOverViewActivity : MenuActivity() /*BaseActivity()*/, View.OnClickListener, View.OnTouchListener , ConnectivityReceiver.ConnectivityReceiverListener{


    /**
     * URL which needs to open in WebView or in-app browser
     */
    private var htmlContents  = ArrayList<String>()

    private var contents = ""

    private var currentUrlLoading: String? = "";

    private var msgLink = ""

    private var baseUrl = ""

    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null


    var finalList   = ArrayList<String>()

    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_unit_webview)
        setContentChildView(R.layout.activity_getting_startedoverview_webview  , true)

        ///attachListener()
         initWebView()
        initialiseToolbar()

        OmnitureTrack.trackState("in-app-browser")

    }
    fun initWebView()
    {


        commonOverViewWebView.webViewClient = InAppWebView()
        val webSettings: WebSettings = commonOverViewWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true

        webSettings.allowContentAccess = true;
        webSettings.allowFileAccess = true;
        webSettings.allowFileAccessFromFileURLs = true;
        webSettings.allowUniversalAccessFromFileURLs = true;


      //  commonDetailWebView.accessibilityDelegate = View.AccessibilityDelegate()

    }

    /**
     * Method used to set title of screen and to bind the click events with certain widgets
     * used inside screen
     *
     */
    private fun initialiseToolbar() {

        //val toolbarTile = toolbar.findViewById<CPTextView>(R.id.genericTitleTxt)

        if (intent.extras != null) {

               contents = intent.getStringExtra(Constants.HTML_CONTENTS)
               msgLink = intent.getStringExtra(Constants.COURSE_MESSAGE_LINK)


                pageWarningMsg.alertTxt.text = getString(R.string.mobile_view_notice)


            if(intent.getStringExtra(Constants.ORANGE_TITLE) != null)
            {


                backButtonLayout.setOnClickListener{ finish() }

                backButtonLayout.contentDescription = getString(R.string.ada_back_button) + getString(R.string.back)

                boldOrangeTitle.visibility = View.VISIBLE
                boldOrangeTitle.text = intent.getStringExtra(Constants.ORANGE_TITLE)
                pageWarningMsg.visibility = View.VISIBLE

            }
            if(intent.getBooleanExtra(Constants.OVERRIDE_TITLE,false))
            {
                genericTitleTxt.text = intent.extras!!.getString(Constants.IN_APP_TITLE)

                if(intent.getStringExtra(Constants.BACK_TITLE) !=null)
                {
                    var back = intent.getStringExtra(Constants.BACK_TITLE)
                    if(back.length>6)
                    {
                        backHeaderTxt.text = back.substring(0,6) +"..."
                    }else
                          backHeaderTxt.text = intent.getStringExtra(Constants.BACK_TITLE)

                    backButtonLayout.contentDescription = getString(R.string.ada_back_button) +intent.getStringExtra(Constants.BACK_TITLE)
                }
            }

            if(intent.getBooleanExtra(Constants.PAGE_WARNING_HIDE,false))
            {
                pageWarningMsg.visibility  = View.GONE
            }

        }

      /*  val backButton = toolbar.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener { finish() }*/
       // toolbarTile.setOnClickListener { finish() }

        //callHtmlContents()
        buildGettingStartedOverviewDetailURLs(msgLink , contents)

        unitPullToRefresh.setColorSchemeColors( getColor(R.color.checkBoxColor))
        unitPullToRefresh.setOnRefreshListener {

            unitPullToRefresh.isRefreshing = false
            buildGettingStartedOverviewDetailURLs(msgLink , contents)

        }

    }

    override fun onBackPressed()
    {
        if (this.commonOverViewWebView.canGoBack())
        {
            this.commonOverViewWebView.goBack()
            return
        }

       // overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up )
        super.onBackPressed()
    }

  /*  override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && this.commonDetailWebView.canGoBack()) {
            this.commonDetailWebView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }*/
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {



        when (v!!.id) {
            R.id.commonDetailWebView ->
            {
                //popupLayout.visibility = View.GONE
                return false
            }
        }

        return true
    }

    /**
     * Method read title, date and other details from passed parameters and show open passed url
     * in WebView.
     *
     */
    private fun callHtmlContents() {
        commonWebProgressBar.visibility = View.VISIBLE

       // var header = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1, user-scalable=no\"><style> body,div,li,p,strong,td,ul{font-size: 16px; font-family: sans-serif;} a{color: #0079a2; text-decoration: underline;} p{margin: 0px; margin-top: 10px;} .changeAlert{font-size:14px; color: #333; padding:5px 0; width:90%; margin:15px auto 0 auto; border-top:1px solid #ddd; border-bottom:1px solid #ddd;} .resourcesNote{font-size:14px; font-style:italic; text-align: center; color: #666; padding:5px 0; width:90%; margin:15px auto 0 auto; border-top:1px solid #ddd; border-bottom:1px solid #ddd;} .ie8StandardsModeBugPatch{border:1px solid #369; background-color:#ddd; height:0px; width:0px; display:none;} h2{color: #800D1E; font-family: AvenirLTStd-Medium,sans-serif;font-weight:normal; font-size: 22px; padding: 10px; padding-top: 20px; text-align: center; text-transform: capitalize;} .contentWrap>h3{margin-top:20px;} ul{margin-left:15px; padding:18px 18px 18px 18px;} ol{margin:0; padding:0 0 0 24px;} ul>li>ul>li{list-style:square;} .emptyStudiesNote{font-size:18px; text-align: center; color: #000; width:90%;} .transcript-link>a{text-decoration: none;} img{display:none;} .floatboxright{margin:0 0 0 0; height:auto; clear:both;} .floatboxright ul.no-bullet {margin:0 0 20px 0; padding:0;} .floatboxright ul.no-bullet li{list-style:none; clear:both;display:block; margin-bottom:15px; color:#fff;} .floatboxright ul.no-bullet li>img{display:none;}"//block;float:left;margin-top:3px; margin-right:10px p, .positioning-graphic, .graphic-text, .graphic-content{margin:0 0 10px 0; clear:both;} .FlexPathCTA{padding:20px 20px 10px 20px;position:relative;overflow:hidden;clear:both;} .FlexPathCTA.CompMap{padding:20px 20px 20px 20px;text-align:center;background: #f0f0f0;color:#1a1712;margin-bottom:20px;margin-top:40px;position:relative;overflow:hidden;clear:both;}.FlexPathCTA h5{color:#1a1712;!important;font-size:23px;text-align:center;line-height:28px;margin: 10px 200px 10px 0px;font-weight:bold;} .FlexPathCTA a.cta_link{width: auto;width:auto;display: block;background: #0079a2;text-transform: titlecase;color: #fff;padding: 12px 20px 10px 20px;text-decoration: none;font-size: 14px;text-align: center;font-weight: normal;position:absolute;top:30px;} .FlexPathCTA a.cta_link:hover,.FlexPathCTA a.cta_link:focus{background:#0079a2;color:#fff !important;text-decoration:none;}.FlexPathCTA > span{font-size:17px;display:block;margin: 0px 200px 0px 0px;}.FlexPathCTA a.howTo_link{display:block;clear:both;float:right;font-weight:normal;font-size:15px;margin-top:10px;} .FlexPathCTA a.howTo_link:hover,.FlexPathCTA a.howTo_link:focus{text-decoration:underline;color:#0984d3 !important;}.FlexPathCTA {height:210px !important}.FlexPathCTA > span,.FlexPathCTA h5 {margin-right: 0;} .FlexPathCTA a.cta_link {top:auto;margin-bottom:25px;left:50%;margin-left:-85px;}.FlexPathCTA a.howTo_link {top:auto;bottom:10px;position:absolute;color:#3da2d2;text-decoration:none;}.FlexPathCTA.CompMap{height:190px;}.FlexPathCTA.CompMap a.cta_link {top:auto;bottom:20px;} </style></head><body>"
        var header = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1, user-scalable=no\"></head><body>"
        var body =""
        for (html in htmlContents)
        {
            body = body + html
        }
        var footer = "</body></html>";
        var finalHtml = header + body + footer
        val mimeType = "text/html"
        val encoding = "utf-8"
         commonOverViewWebView.loadData(finalHtml, "text/html", "UTF-16")

       // commonOverViewWebView.loadDataWithBaseURL  (baseUrl,finalHtml, mimeType, encoding , "")
        commonWebProgressBar.visibility = View.GONE

    }

    private fun refreshPage() {
      //  initWebView()
        commonOverViewWebView.reload()
    }

   /* private fun attachListener() {

        commonOverViewWebView.setOnTouchListener(this)



    }*/

    override fun onClick(v: View?) {


    }



    private inner class InAppWebView : WebViewClient() {
        override fun shouldOverrideUrlLoading(    view: WebView,url: String  ): Boolean
        {
            if(url.contains("about:blank" , true))
            {
                return true
            }
            if((url.contains("http:" , true) != true) && (url.contains("https:" , true) != true))
            {
                return true
            }

            val result = commonOverViewWebView.hitTestResult
            if(result.type == WebView.HitTestResult.SRC_ANCHOR_TYPE)
            {
                DialogUtils.screenNamePrefix = "in-app-browser:link-out"
                val stickyWork  = StickyInfoGrabber(this@GettingStartedOverViewActivity)
                stickyWork.generateMuleSoftStickySessionForTargetUrl(url , BuildConfig.STICKY_FORWARD_URL)
                return true
            }

            view.loadUrl(url)
            currentUrlLoading = url;
            return true
        }



        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            commonWebProgressBar.visibility  = View.VISIBLE
        }
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            commonWebProgressBar.visibility  = View.GONE
        }
    }

//    override fun finish() {
//        super.finish()
//        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_up)
//    }

    override fun onDestroy()
    {
        Preferences.addBoolean(PreferenceKeys.IS_APP_GONE_OUTSIDE , true)
        (applicationContext as CapellaApplication).validateOpenAMLogoutCase()
        super.onDestroy()
    }



  /*  private inner class CommonWebViewClient : WebViewClient() {

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            executeUrl = url;
            return false
        }
    }*/

    /**
     *  Factory method of activity, executes when application comes in resume state from pause state.
     *
     */
    override fun onResume() {
        super.onResume()
        initNetworkBroadcastReceiver()
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
                callHtmlContents()
            }else
            {
                Util.trace("Can not reload")
            }
        }else
        {
            //NETWORK GONE
            isNetworkFailedDueToConnectivity = true
           // commonWebProgressBar.visibility  = View.GONE
        }

    }
    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun initNetworkBroadcastReceiver() {

        ConnectivityReceiver.connectivityReceiverListener = this@GettingStartedOverViewActivity
        connectivityReceiver = ConnectivityReceiver(networkLayout)
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(connectivityReceiver, intentFilter)
    }

    /*****************************************************/




    fun buildGettingStartedOverviewDetailURLs(messageLink:String , content:String  )
    {
        try {
            commonWebProgressBar.visibility = View.VISIBLE

            var capellaDomain = ".capella.edu/"

            var domain: String? = null

            if (messageLink.contains(capellaDomain, true)) {
                domain = messageLink.substring(0, messageLink.indexOf(capellaDomain) + capellaDomain.length)
            }

            // Find the URI
            var list = Util.findURL_list_inHREF(content)

            var finalUrls: ArrayList<String> = ArrayList<String>()
            if (list != null) {
                // if(list.size ==3 || list.size==4)
                //  {
                for (url in list) {
                    if (url.indexOf('@') > -1 && url.length > 10) {
                        var newUrl = url.substring(url.lastIndexOf("@") + 1, url.length)
                        finalUrls.add(domain + newUrl)
                    }
                }
                //}
            }
            var overViewUrlLength = finalUrls.size


            var blackboardDomain = Util.findBlackboardDomain(finalUrls[0])
            baseUrl = blackboardDomain

            val urlIndex = if (overViewUrlLength == 2) 0 else 1
            var overviewUrl = finalUrls[urlIndex]
            overviewUrl = overviewUrl.replace("\"", "");


            /* if(finalUrls.size <= 3)
                 {
                      overviewUrl = finalUrls.get(finalUrls.size-1);
                 }else
                 {
                      overviewUrl = finalUrls.get(2);
                 }*/

            finalList.clear()
            htmlContents.clear()
            //finalList.addAll(finalUrls)
            finalList.addAll(finalUrls.subList(urlIndex, finalUrls.size))
            processOverviewUrl(overviewUrl)
        }catch (t:Throwable)
        {
            commonWebProgressBar.visibility = View.GONE
        }

    }



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
                        }else
                        {
                            commonWebProgressBar.visibility = View.GONE
                        }


                    }

                })
        }else
        {
            commonWebProgressBar.visibility = View.GONE
            //contents received
        }


    }

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
        networkHandler.setSilentMode(true)

        networkHandler.execute()
    }


    private val htmlNetworkListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: android.util.Pair<String, Any>)
        {
            Util.trace("Response htmlNetworkListener " + response.second.toString())
            try
            {
                if (response.first == NetworkConstants.SUCCESS)
                {
                    htmlContents.add(response.second.toString())
                    if (finalList.size > 0) {
                        processOverviewUrl(finalList[0])
                    } else {
                        commonWebProgressBar.visibility = View.GONE
                        callHtmlContents()
                    }
                }else
                    {
                        commonWebProgressBar.visibility = View.GONE
                    }


            }catch (t: Throwable){
                Util.trace("Heell $t")
                t.printStackTrace()
                commonWebProgressBar.visibility = View.GONE
            }
        }
    }



}
