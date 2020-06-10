package edu.capella.mobile.android.activity

import android.content.*
import android.graphics.Bitmap
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Pair
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.adapters.DiscussionForumListAdapter
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.app.CapellaApplication
import edu.capella.mobile.android.base.BaseActivity
import edu.capella.mobile.android.bean.DiscussionForumBean
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.bean.MuleSoftSession
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import edu.capella.mobile.android.network.NetworkService
import edu.capella.mobile.android.task.StickyInfoGrabber
import edu.capella.mobile.android.widgets.CPTextView
import edu.capella.mobile.android.widgets.CWebView
import kotlinx.android.synthetic.main.activity_common_webview.*
import kotlinx.android.synthetic.main.activity_common_webview.networkLayout
import kotlinx.android.synthetic.main.activity_common_webview.toolbar
import kotlinx.android.synthetic.main.activity_discussion_forum.*
import kotlinx.android.synthetic.main.toolbar_financial.*
import kotlinx.android.synthetic.main.toolbar_financial.view.*
import java.io.File

/**
 * Class Name.kt : class description goes here
 *
 * @author  :  SSHARMA45
 * @version :  1.0
 * @since   :  5/13/2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
class GP2InAppBrowser : BaseActivity(), View.OnClickListener,ConnectivityReceiver.ConnectivityReceiverListener, View.OnTouchListener  {


    /**
     * URL which needs to open in WebView or in-app browser
     */
    private var link: String? = ""

    private var isFirstTimeLoading:Boolean = true

    private var currentUrlLoading: String? = "";

    private var isScoringGuide: Boolean = false
    var tilteText = ""
    private var isWebViewCLick = false

    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null



    var filter:ArrayList<String> = ArrayList<String>()

    //Any urls which doesn't contains following strings:
    var supressfilter:ArrayList<String> = ArrayList<String>()



    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_common_webview)

        attachListener()
        initialiseToolbar()
        initFilterCriteria()

        OmnitureTrack.trackState("common-in-app")
        initWebView()
    }
    fun initWebView()
    {

        commonDetailWebView.webViewClient = InAppWebView()
        val webSettings: WebSettings = commonDetailWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true

        commonDetailWebView.settings.builtInZoomControls = true
        commonDetailWebView.settings.setSupportZoom(true)
        commonDetailWebView.settings.displayZoomControls = true
        commonDetailWebView.setOnTouchListener(this)
        commonDetailWebView.settings.useWideViewPort = true

        webSettings.allowContentAccess = true
        webSettings.allowFileAccess = true
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.allowUniversalAccessFromFileURLs = true




        isScoringGuide  = intent.extras!!.getBoolean(Constants.FOR_SCORING_GUIDE , false)


        callIrisUrl()




    }

    private fun initFilterCriteria()
    {
        filter.add("campus.capella.edu/web/dcp/modify?")
        filter.add("campus.capella.edu/web/dcp/transfer-credit-evaluation")

        supressfilter.add("campus.capella.edu")
        supressfilter.add("openam/UI/Login")
        supressfilter.add("fls.doubleclick.net")
        supressfilter.add("capella.demdex.net")
        supressfilter.add("mobile-feed/auth")
    }

    /**
     * Method used to set title of screen and to bind the click events with certain widgets
     * used inside screen
     *
     */
    private fun initialiseToolbar() {

        val toolbarTile = toolbar.findViewById<CPTextView>(R.id.headerTxt)
        if (intent.extras != null) {

            link = intent.extras!!.getString(Constants.URL_FOR_IN_APP)


            currentUrlLoading = link

            tilteText=intent.extras!!.getString(Constants.IN_APP_TITLE)!!

            if(tilteText!!.equals("My Academic Plan Home"))
            {
                backButton.contentDescription=resources.getString(R.string.ada_finance_academic_close_button)
            }else if (tilteText!!.contains("Finances" , true))
            {
                backButton.contentDescription=resources.getString(R.string.ada_finance_dashboard_close_button)
            }else {
                backButton.contentDescription = getString(R.string.ada_close) +" "+ tilteText
            }




            toolbarTile.text = intent.extras!!.getString(Constants.IN_APP_TITLE)



            //genericTitleTxt.text = intent.extras!!.getString(Constants.IN_APP_TITLE)
            backButton.setImageResource(R.drawable.ic_close_black)
            toolbarTile.gravity = Gravity.CENTER
            // headerLineView.visibility = View.GONE
            dotImage.visibility = View.VISIBLE


        }



        backButton.setOnClickListener { finish() }
        // toolbarTile.setOnClickListener { finish() }
        toolbar.headerTxt.setOnClickListener {
            if(popupLayout.visibility == View.VISIBLE)
            {
                popupLayout.visibility = View.GONE
            }
        }
    }

    override fun onBackPressed()
    {
        if( popupLayout.visibility == View.VISIBLE)
        {
            popupLayout.visibility = View.GONE
            return
        }

        try {
            if (commonDetailWebView.copyBackForwardList().getItemAtIndex(commonDetailWebView.copyBackForwardList().currentIndex - 1).url.contains(BuildConfig.OPENAM_HOST, true)) {
                destroyWebView()
                finish()
            }
        }catch (t:Throwable){}

        if (this.commonDetailWebView.canGoBack())
        {
            this.commonDetailWebView.goBack()
            return
        }
        destroyWebView()
        //overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up )
        super.onBackPressed()
    }

    fun destroyWebView() {

        try {
            commonDetailWebView.clearHistory()
            commonDetailWebView.clearCache(true)
            commonDetailWebView.loadUrl("about:blank")
            commonDetailWebView.onPause()
            commonDetailWebView.removeAllViews()
            commonDetailWebView.destroyDrawingCache()
            commonDetailWebView.pauseTimers()
            commonDetailWebView.destroy()
        }catch (t:Throwable){}

    }

    /*  override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
          if (keyCode == KeyEvent.KEYCODE_BACK && this.commonDetailWebView.canGoBack()) {
              this.commonDetailWebView.goBack()
              return true
          }
          return super.onKeyDown(keyCode, event)
      }*/
    /* override fun onTouch(v: View?, event: MotionEvent?): Boolean {

       Util.trace("Inside TOuch...")

       popupLayout.visibility = View.GONE
         when (v!!.id) {
             R.id.commonDetailWebView ->
             {
                // popupLayout.visibility = View.GONE

                 return false

             }
         }

         return true
     }*/

    /**
     * Method read title, date and other details from passed parameters and show open passed url
     * in WebView.
     *
     */
    private fun callIrisUrl()
    {
        commonWebProgressBar.visibility  = View.VISIBLE
        val stickyWork = StickyInfoGrabber(this)

        if(isScoringGuide == false) {
            stickyWork.generateReturnMuleSoftStickySessionForTargetUrl(link.toString(),
                object : NetworkListener {
                    override fun onNetworkResponse(response: Pair<String, Any>) {

                        if (response.first == NetworkConstants.SUCCESS) {
                            Util.trace("Token Is : " + response.second)

                            val gson = Gson()
                            val muleSoftSession = gson.fromJson<MuleSoftSession>(
                                response.second.toString(),
                                MuleSoftSession::class.java
                            )

                            var finalUrlToOpen =
                                BuildConfig.STICKY_FORWARD_URL + File.separator + muleSoftSession.token

                            Util.trace("Final Url : " + finalUrlToOpen)
                            currentUrlLoading = finalUrlToOpen
                            commonWebProgressBar.visibility  = View.GONE
                            commonDetailWebView.loadUrl(finalUrlToOpen)
                        }else{
                            commonWebProgressBar.visibility  = View.GONE
                            commonDetailWebView.loadUrl(currentUrlLoading)
                        }


                    }

                })
        }else
        {
            stickyWork.generateScoringGuideUrl(link.toString(),
                object : NetworkListener {
                    override fun onNetworkResponse(response: Pair<String, Any>) {
                        try{
                        if (response.first == NetworkConstants.SUCCESS) {
                            Util.trace("Token Is : " + response.second)

                            val gson = Gson()
                            val muleSoftSession = gson.fromJson<MuleSoftSession>(
                                response.second.toString(),
                                MuleSoftSession::class.java
                            )

                            var finalUrlToOpen =
                                BuildConfig.STICKY_FORWARD_URL + File.separator + muleSoftSession.token

                            Util.trace("Final Url : " + finalUrlToOpen)
                            currentUrlLoading = finalUrlToOpen
                            commonDetailWebView.loadUrl(finalUrlToOpen)
                        }else{
                            commonDetailWebView.loadUrl(currentUrlLoading)
                        }
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }

                })
        }
        Util.trace("Opening : " + link)


    }

    private fun refreshPage() {
        //  initWebView()
        isWebViewCLick = false
        commonDetailWebView.reload()
    }

    private fun attachListener() {


        forwordImg.setOnClickListener(this)
        refreshImg.setOnClickListener(this)
        dotImage.setOnClickListener(this)
        // commonDetailWebView.setOnTouchListener(this)
        openBrowserTxt.setOnClickListener(this)
        copyLintTxt.setOnClickListener(this)
        shareTxt.setOnClickListener(this)

        commonDetailWebView.setTouchWatcher( object: CWebView.CWebTouch{
            override fun isTouch(touch: Boolean) {
                popupLayout.visibility = View.GONE
            }

        })

    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.refreshImg -> {
                OmnitureTrack.trackAction("common-in-app:refreshPage")
                refreshPage()
                popupLayout.visibility = View.GONE
            }
            R.id.forwordImg -> {

                if(commonDetailWebView.canGoForward()) {

                    commonDetailWebView.goForward()

                    popupLayout.visibility = View.GONE
                    OmnitureTrack.trackAction("common-in-app:forward")
                }
            }
            R.id.dotImage -> {
                OmnitureTrack.trackAction("common-in-app:menuOpen")
                popupLayout.visibility = View.VISIBLE
            }
            R.id.openBrowserTxt -> {
                OmnitureTrack.trackAction("common-in-app:openInBrowser")
                /*  val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrlLoading))
                  startActivity(browserIntent)*/

                openLinkWithSSOAuthentication(currentUrlLoading!!)
                popupLayout.visibility = View.GONE
            }
            R.id.copyLintTxt -> {
                OmnitureTrack.trackAction("common-in-app:linkCopy")
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip =
                    ClipData.newPlainText(resources.getString(R.string.copied_url), currentUrlLoading)
                clipboard.setPrimaryClip(clip)

//                Util.showSnakeBar(resources.getString(R.string.copied_url) , v , Snackbar.LENGTH_LONG)

                Util.showCustomSnakeBar(this@GP2InAppBrowser, resources.getString(R.string.copied_url) , v , Snackbar.LENGTH_LONG)
                popupLayout.visibility = View.GONE

            }
            R.id.shareTxt -> {
                OmnitureTrack.trackAction("common-in-app:shareLink")
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, currentUrlLoading)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
                popupLayout.visibility = View.GONE

            }
        }
    }

    private inner class InAppWebView : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {
            Util.trace("Link Clicked : ", url)
//            Toast.makeText(this@GP2InAppBrowser,url,Toast.LENGTH_SHORT).show()
            if (url.contains("/webapps/discussionboard/do/forum?") ||
                url.contains("&nav=discussion_board")
            ) {
                // Go to discussion board button clicked on webview


                val forumId = url.subSequence(url.indexOf("forum_id") + 9, url.indexOf("&nav"))


                callDiscussionForumApi(
                    intent.extras?.getString(Constants.COURSE_ID).toString(),
                    forumId.toString()
                )
            } else if (isWebViewCLick && shouldOpenInExternalBrowser(url)){
                DialogUtils.screenNamePrefix = "gp2:detail-linkout"
                val stickyWork  = StickyInfoGrabber(this@GP2InAppBrowser)
                stickyWork.generateMuleSoftStickySessionForTargetUrl(url , BuildConfig.STICKY_FORWARD_URL)
            }else {
                currentUrlLoading = url
                view.loadUrl(url)
            }
            return true
        }

        override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
            Util.trace("History url : " + url)
            currentUrlLoading = url
            super.doUpdateVisitedHistory(view, url, isReload)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            isFirstTimeLoading = false
            commonWebProgressBar.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            commonWebProgressBar.visibility = View.GONE

            try {
                if (view?.title != null) {
                    /* if (view!!.copyBackForwardList().size <= 2)
                         toolbar.headerTxt.text = tilteText
                     else*/
                    var newTitle = getTitle(view.title)
                    if (newTitle.contains(tilteText, true)) {
                        toolbar.headerTxt.text = tilteText
                    } else {
                        toolbar.headerTxt.text = newTitle
                    }
                }

                if (!commonDetailWebView.canGoForward()) {
                    forwordImg.contentDescription = getString(R.string.ada_forward_not_available)
                    forwordImg.setColorFilter(
                        ContextCompat.getColor(
                            this@GP2InAppBrowser,
                            R.color.disabledGray
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    );

                } else {
                    forwordImg.contentDescription =
                        getString(R.string.ada_finance_dashboard_forword_button)
                    forwordImg.setColorFilter(
                        ContextCompat.getColor(
                            this@GP2InAppBrowser,
                            R.color.text_grey_900
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    );
                }
            } catch (t: Throwable) {
            }

            try {

                // code for enable zooming in javascript
                val javascript =
                    "javascript:document.getElementsByName('viewport')[0].setAttribute('content', 'initial-scale=1.0,maximum-scale=5.0');"
                view!!.loadUrl(javascript)
            } catch (t: Throwable) {
            }

            try {

                // code to remove header from the top
                view!!.loadUrl("javascript:document.getElementById(\"globalNavPageNavArea\").setAttribute(\"style\",\"display:none;\");");
                view.loadUrl(
                    "javascript:(function() { " +
                            "document.getElementsByClassName('global-nav-bar-wrap')[0].style.display='none'; })()"
                );
                view.loadUrl("javascript:document.getElementById(\"site-header\").setAttribute(\"style\",\"display:none;\");");
                view.loadUrl("javascript:document.getElementById(\"puller\").setAttribute(\"style\",\"display:none;\");");
                view.loadUrl("javascript:document.getElementById(\"fac-app\").setAttribute(\"style\",\"display:none;\");");
                view.loadUrl("javascript:document.getElementById(\"direct-path-banner\").setAttribute(\"style\",\"display:none;\");");
                view.loadUrl("javascript:document.getElementById(\"site-footer\").setAttribute(\"style\",\"display:none;\");");
                view.loadUrl("javascript:document.getElementById(\"contentListItem:_8069074_1\").setAttribute(\"style\",\"display:none;\");");
                view.loadUrl("javascript:document.getElementById(\"menuPuller\").setAttribute(\"style\",\"display:none;\");");
                view.loadUrl("javascript:document.getElementById(\"direct-path-banner\").setAttribute(\"style\",\"display:none;\");");
                view.loadUrl("javascript:document.getElementById(\"breadcrumbs\").setAttribute(\"style\",\"display:none;\");");
                view.loadUrl("javascript:document.getElementById(\"pageTitleDiv\").setAttribute(\"style\",\"display:none;\");");
                view.loadUrl(
                    "javascript:(function() { " +
                            "document.getElementsByClassName('printpage')[0].style.display='none'; })()"
                );
                view.loadUrl(
                    "javascript:(function() { " +
                            "document.getElementsByClassName('site-container header-top-section')[0].style.display='none'; })()"
                );

                view.loadUrl(
                    "javascript:(function() { " +
                            "document.getElementsByClassName('modeSwitchWrap')[0].style.display='none'; })()"
                );


            } catch (t: Throwable) {
            }
//            updateDocuementId(view)
            Handler().postDelayed(Runnable {
                try {
                    view?.loadUrl("javascript:document.getElementById(\"fac-app\").setAttribute(\"style\",\"display:none;\");");
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 6000)

        }
    }


    private fun shouldOpenInExternalBrowser(url:String):Boolean {
        var shouldOpen: Boolean = false
        if (url.contains("/webapps/blackboard/content/launchLink.jsp?course_id")) {
            shouldOpen = false
        } else if (url.contains("http:", true) ||
            url.contains("campus.capella.edu/web/dcp/modify?") ||
            url.contains("campus.capella.edu/web/dcp/transfer-credit-evaluation")
        ) {
            shouldOpen = true
        } else if(url.contains("https:") || url.contains("campustools.capella.edu") || url.contains("courserooma.capella.edu") || url.contains("campus.capella.edu") || url.contains("openam/UI/Login") || url.contains("fls.doubleclick.net")
            || url.contains("capella.demdex.net") || url.contains("mobile-feed/auth")){
            shouldOpen = false
        } else {
            shouldOpen = true
        }
        return shouldOpen
    }

    private fun executeJS(js:String , view: WebView?)
    {
        try {
            view?.loadUrl("javascript:$js")
        }catch (t:Throwable){}
    }

    private fun isUrlNeedsToOpenOutside(url: String?): Boolean
    {
        if(url==null)
            return false

        if(url.startsWith("http:" , true))
            return true



        for(u in filter)
        {
            if(url.contains(u , true))
                return true
        }



        var isFound = false

        for(s in supressfilter)
        {
            if(url.contains(s , true))
            {
                isFound = true
                break
            }
        }

        if(isFound == false)
        {
            return true // true means Link if not good and open it in browser
        }


        return false
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_up)
    }

    override fun onDestroy()
    {
        Preferences.addBoolean(PreferenceKeys.IS_APP_GONE_OUTSIDE , true)
        (applicationContext as CapellaApplication).validateOpenAMLogoutCase()
        super.onDestroy()

        //overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up )

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

    fun getTitle(pageTitle:String):String
    {

        var maxLength =  25;

        //trim the string to the maximum length
        var trimmedPageTitle = pageTitle.substring(0, maxLength);
        //re-trim if we are in the middle of a word
        trimmedPageTitle = trimmedPageTitle.substring(0, Math.min(trimmedPageTitle.length, trimmedPageTitle.lastIndexOf(" ")));

        //var actionBarTitle = (trimmedPageTitle == "" || pageTitle.length < maxLength) ? args.pageTitle : trimmedPageTitle + "...";
        var actionBarTitle = ""

        if(trimmedPageTitle =="" || pageTitle.length < maxLength)
        {
            actionBarTitle = pageTitle
        }else
        {
            actionBarTitle = "$trimmedPageTitle..."
        }

        actionBarTitle = actionBarTitle.replace(" –...", "...");

        return actionBarTitle
    }

    fun openLinkWithSSOAuthentication(url:String)
    {
        val stickyWork  = StickyInfoGrabber(this@GP2InAppBrowser)

        stickyWork.generateReturnMuleSoftStickySessionForTargetUrl(url , openInBrowserListener)
    }

    private val openInBrowserListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>)
        {

            if(response.first == NetworkConstants.SUCCESS) {
                val gson = Gson()
                val muleSoftSession = gson.fromJson<MuleSoftSession>(response.second.toString(), MuleSoftSession::class.java)

                //Util.openBrowserWithConfirmationPopup(globalContext, irisUrlToOpen + File.separator + muleSoftSession.token )

                var newUrl = BuildConfig.STICKY_FORWARD_URL + File.separator + muleSoftSession.token
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(newUrl))
                this@GP2InAppBrowser.startActivity(browserIntent)

            }else
            {
                DialogUtils.onShowDialog(this@GP2InAppBrowser , this@GP2InAppBrowser.getString(
                    R.string.error), this@GP2InAppBrowser.getString(R.string.url_opening_error))

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
        commonDetailWebView.setOnTouchListener(this)
        initNetworkBroadcastReceiver()
    }
    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun initNetworkBroadcastReceiver() {

        ConnectivityReceiver.connectivityReceiverListener = this@GP2InAppBrowser
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
            if(this.getCurrentNetworkQueueSize()==0)
            {
                DialogUtils.releaseGenericDialog()
                if(isFirstTimeLoading)
                {
                    callIrisUrl()
                }else
                    refreshPage()
            }else
            {
                Util.trace("Can not reload")
            }

        }else
        {
            isNetworkFailedDueToConnectivity = true
        }
    }

    /**
     * Method call LearnerProfile api to show details in PROFILE and IDCARD tabs.
     *
     */
    var fourumId :String = ""
    /**
     * Method call LearnerProfile api to show details in PROFILE and IDCARD tabs.
     *
     */
    private fun callDiscussionForumApi(courseId: String, forumId: String) {
        this.fourumId = forumId
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val params = HashMap<String, Any>()
        params.put(NetworkConstants.TOKEN, loginBean?.authData?.token!!)


        //POST /mobile-feed/json/newdiscussions.feed?action=getAllforums&courseidentifier=TESTSUB1331_006292_1_1201_1_03&formattype=text&courseid=7001 HTTP/1.1

        val qStringParams = HashMap<String, Any>()
        qStringParams.put(NetworkConstants.COURSE_IDENTIFIER, courseId)
        qStringParams.put(NetworkConstants.COURSE_NUMBER_ID,
            intent.extras?.getString(Constants.COURSE_NUMBER_ID)!!
        )
        qStringParams.put(NetworkConstants.ACTION, "getAllforums")
        qStringParams.put(NetworkConstants.FORMAT_TYPE, "text")


        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.COURSE_DISCUSSION_FORUM_API + NetworkService.getQueryString(
                qStringParams
            ),
            params,
            NetworkHandler.METHOD_POST,
            gp2BrowserListener,
            null
        )

        networkHandler.execute()


    }

    private val gp2BrowserListener: NetworkListener = object : NetworkListener{
        override fun onNetworkResponse(response: Pair<String, Any>) {
            if(response.first == NetworkConstants.SUCCESS)
            {
//                val gson = Gson()
//                var discussionTopicListBean = gson.fromJson<DiscussionTopicBean>(response.second.toString(), DiscussionTopicBean::class.java)

                val gson = Gson()
                var discussionListBean = gson.fromJson<DiscussionForumBean>(
                    response.second.toString(),
                    DiscussionForumBean::class.java
                )
                extractInformationAndOpenDiscussionBoard(discussionListBean)

            }else
            {
//                DialogUtils.showGenericErrorDialog(this)
            }
        }

    }

    private fun extractInformationAndOpenDiscussionBoard(discussionListBean: DiscussionForumBean) {

        if (discussionListBean?.errorData != null) {

            DialogUtils.showGenericErrorDialog(this)
            return
        }

        // if(matesListBean?.classmatesData?.courseMembers!![0]!!.member!!.size!! <= 0)
        //var f : DiscussionForumBean.NewDiscussionData.GroupDiscussion.Forum
        var grouplist: List<DiscussionForumBean.NewDiscussionData.GroupDiscussion?>? =
            discussionListBean.newDiscussionData?.groupDiscussions

        if ((grouplist == null) ||
            (grouplist!!.size!! <= 0)
        ) {

        }else {

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

            var information: DiscussionForumListAdapter.DiscussionForumCollector? = null

            for (index in 0 until commonList.size){
                var id = commonList[index]?.forumBean?.id?.replace("_","")
                if(id?.contains(fourumId)!!){
                    information = commonList[index]
                    break
                }
            }

            val discussionTitle =
                information?.forumBean?.title
            Util.extractHrefForViewDescription(information?.forumBean?.descriptionText)
            var topicIntent = Intent(this@GP2InAppBrowser, DiscussionTopicActivity::class.java)
            topicIntent.putExtra(Constants.FORUM_ID, fourumId)
            topicIntent.putExtra(
                Constants.COURSE_ID,
                intent.extras?.getString(Constants.COURSE_ID)
            )
            topicIntent.putExtra(
                Constants.DISCUSSION_TITLE,
                discussionTitle
            )

            var inAppTitle  = ""
            if(intent.extras?.getString(Constants.IN_APP_TITLE)?.contains("on Full Site" ,true)!!){
                inAppTitle = Util.getTrucatedString(intent.extras?.getString(Constants.IN_APP_TITLE)!!, 4)
            }else{
                inAppTitle = intent.extras?.getString(Constants.IN_APP_TITLE)!!
            }

            topicIntent.putExtra(
                Constants.BACK_TITLE,
                inAppTitle
            )
            startActivity(topicIntent)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
//            handler?.sendEmptyMessageDelayed(CLICK_ON_WEBVIEW, 500);
            isWebViewCLick = true
        }
        return false;
    }

}
