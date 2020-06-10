package edu.capella.mobile.android.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Pair
import android.view.Gravity
import android.content.IntentFilter
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.app.CapellaApplication
import edu.capella.mobile.android.base.BaseActivity
import edu.capella.mobile.android.bean.MuleSoftSession
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.task.StickyInfoGrabber
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.widgets.CPTextView
import edu.capella.mobile.android.widgets.CWebView
import kotlinx.android.synthetic.main.activity_common_webview.*
import kotlinx.android.synthetic.main.activity_common_webview.toolbar
import kotlinx.android.synthetic.main.toolbar_financial.*
import kotlinx.android.synthetic.main.toolbar_financial.view.*
import java.io.File


/**
 * CommonWebViewActivity.kt :  Screen working as in-app browser, opens given url in WebView
 *
 * @author  :  Jayesh.Lahare
 * @version :  1.0
 * @since   :  04-04-2020
 *
 * @see WebView
 */
class CommonWebViewActivity : BaseActivity(), View.OnClickListener ,ConnectivityReceiver.ConnectivityReceiverListener {


    /**
     * URL which needs to open in WebView or in-app browser
     */
    private var link: String? = ""

    private var isFirstTimeLoading:Boolean = true

    private var currentUrlLoading: String? = "";

    private var isScoringGuide: Boolean = false
    private var linkToReload = ""
    var tilteText = ""

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
        commonWebProgressBar.visibility  = View.VISIBLE
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

    /*    commonDetailWebView.settings.builtInZoomControls = true
        commonDetailWebView.settings.setSupportZoom(true)
        commonDetailWebView.settings.displayZoomControls = true*/
//        commonDetailWebView.settings.displayZoomControls = true
        commonDetailWebView.getSettings().setBuiltInZoomControls(true);
        commonDetailWebView.getSettings().setSupportZoom(true);

        commonDetailWebView.getSettings().setUseWideViewPort(true);

        webSettings.allowContentAccess = true;
        webSettings.allowFileAccess = true;
        webSettings.allowFileAccessFromFileURLs = true;
        webSettings.allowUniversalAccessFromFileURLs = true;


        isScoringGuide  =intent.extras!!.getBoolean(Constants.FOR_SCORING_GUIDE , false)


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
            }else if (tilteText!!.contains(getString(R.string.scoring_guide) , true))
            {
                backButton.contentDescription=resources.getString(R.string.ada_scoring_guide_close_button)
            }

            toolbarTile.text = intent.extras!!.getString(Constants.IN_APP_TITLE)

                //genericTitleTxt.text = intent.extras!!.getString(Constants.IN_APP_TITLE)
                backButton.setImageResource(R.drawable.ic_close_black)
                toolbarTile.gravity = Gravity.CENTER
               // headerLineView.visibility = View.GONE
                dotImage.visibility = View.VISIBLE


        }



        val backButton = toolbar.findViewById<ImageView>(R.id.backButton)
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

       // Util.trace("back url  " + commonDetailWebView.copyBackForwardList().currentItem?.url)

        try {
            if (commonDetailWebView.copyBackForwardList().getItemAtIndex(commonDetailWebView.copyBackForwardList().currentIndex - 1).url.contains(BuildConfig.OPENAM_HOST, true)) {
                destroyWebView()
                finish()
            }
        }catch (t:Throwable){}


        if (this.commonDetailWebView.canGoBack() && this.commonDetailWebView.copyBackForwardList().size!=0)
        {
            this.commonDetailWebView.goBack()
            return
        }
         destroyWebView()

        //overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up )
        super.onBackPressed()
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
                            linkToReload = finalUrlToOpen
                            Util.trace("Final Url : " + finalUrlToOpen)
                            currentUrlLoading = finalUrlToOpen
                            commonDetailWebView.loadUrl(finalUrlToOpen)
                        }


                    }

                })
        }else
        {
            stickyWork.generateScoringGuideUrl(link.toString(),
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
                            linkToReload = finalUrlToOpen
                            Util.trace("Final Url : " + finalUrlToOpen)
                            currentUrlLoading = finalUrlToOpen
                            commonDetailWebView.loadUrl(finalUrlToOpen)
                        }
                    }

                })
        }
        Util.trace("Opening : " + link)


    }

    private fun refreshPage() {
      //  initWebView()
//        commonDetailWebView.clearHistory()
//        commonDetailWebView.loadUrl(linkToReload)
        commonDetailWebView.reload()
//        callIrisUrl()
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
//                refreshPage()
                callIrisUrl()
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
                Util.showCustomSnakeBar(this@CommonWebViewActivity ,resources.getString(R.string.copied_url) , v , Snackbar.LENGTH_LONG)


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
        ): Boolean
        {
            Util.trace("Link Clicked : "+  url)
            val result = commonDetailWebView.hitTestResult
            if(result.type == WebView.HitTestResult.SRC_ANCHOR_TYPE || result.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE )
            {


                /* if(url.contains("armedforces/benefitsportfolio/mbp.html" , true)) //MILITARY BENIFITS
                {

                    Util.openPlainUrl(this@CommonWebViewActivity , url)
                    return true
                }else if(url.contains("repayment/repaymentEstimator.action" , true)) //Repayment Estimator
                {
                    val stickyWork  = StickyInfoGrabber(this@CommonWebViewActivity)
                    stickyWork.generateMuleSoftStickySessionForTargetUrl(url , BuildConfig.STICKY_FORWARD_URL)
                    return true
                }*/

                if(isUrlNeedsToOpenOutside(url))
                {
                    val stickyWork  = StickyInfoGrabber(this@CommonWebViewActivity)
                    stickyWork.generateMuleSoftStickySessionForTargetUrl(url , BuildConfig.STICKY_FORWARD_URL)
                    return true
                }

            }

            currentUrlLoading = url;
            view.loadUrl(url)




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
            commonWebProgressBar.visibility  = View.VISIBLE

        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            commonWebProgressBar.visibility  = View.GONE

            try {
                if (view?.title != null) {
                   // if (view!!.copyBackForwardList().size <= 2)
                    if (view!!.copyBackForwardList().size <= 0 )
                        toolbar.headerTxt.text = tilteText
                    else
                        toolbar.headerTxt.text = getTitle(view.title)
                }

                    if(!commonDetailWebView.canGoForward())
                        {
                            forwordImg.contentDescription = getString(R.string.ada_forward_not_available)
                            forwordImg.setColorFilter(ContextCompat.getColor(this@CommonWebViewActivity, R.color.disabledGray), android.graphics.PorterDuff.Mode.SRC_IN);

                        }else
                    {
                        forwordImg.contentDescription = getString(R.string.ada_finance_dashboard_forword_button)
                        forwordImg.setColorFilter(ContextCompat.getColor(this@CommonWebViewActivity, R.color.text_grey_900), android.graphics.PorterDuff.Mode.SRC_IN);
                    }
            }catch (t:Throwable){}



            try {
                if(isScoringGuide)
                commonDetailWebView.loadUrl("javascript:document.getElementById(\"button--print-page\").setAttribute(\"style\",\"display:none;\");");
            }catch (t:Throwable){}

            removeHtmlElement("site-footer")

        }
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
        val stickyWork  = StickyInfoGrabber(this@CommonWebViewActivity)

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
                this@CommonWebViewActivity.startActivity(browserIntent)

            }else
            {
                DialogUtils.onShowDialog(this@CommonWebViewActivity , this@CommonWebViewActivity.getString(R.string.error), this@CommonWebViewActivity.getString(R.string.url_opening_error))

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
        initNetworkBroadcastReceiver()
    }
    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun initNetworkBroadcastReceiver() {

        ConnectivityReceiver.connectivityReceiverListener = this@CommonWebViewActivity
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
            if(this.getCurrentNetworkQueueSize()==0) {
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


    private fun removeHtmlElement(id:String)
    {
        try{

            commonDetailWebView.loadUrl("javascript:document.getElementById(\"$id\").setAttribute(\"style\",\"display:none;\");");

        }catch (t:Throwable){}
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


}
