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
import edu.capella.mobile.android.bean.MuleSoftSession
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.task.StickyInfoGrabber
import kotlinx.android.synthetic.main.activity_getting_starteddetail_webview.*
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
class GettingStartedDetailActivity : MenuActivity() /*BaseActivity()*/, View.OnClickListener, View.OnTouchListener , ConnectivityReceiver.ConnectivityReceiverListener{


    /**
     * URL which needs to open in WebView or in-app browser
     */
    private var link: String? = ""

    private var currentUrlLoading: String? = "";

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
        //setContentView(R.layout.activity_unit_webview)
        setContentChildView(R.layout.activity_getting_starteddetail_webview , true)

        attachListener()
        initWebView()
        initialiseToolbar()

        OmnitureTrack.trackState("in-app-browser")

    }
    fun initWebView()
    {


        commonDetailWebView.webViewClient = InAppWebView()
        val webSettings: WebSettings = commonDetailWebView.settings
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

               link = intent.extras!!.getString(Constants.URL_FOR_IN_APP)

               currentUrlLoading = link

                pageWarningMsg.alertTxt.text = getString(R.string.mobile_view_notice)
/*                toolbarTile.text = intent.extras!!.getString(Constants.IN_APP_TITLE)
                //genericTitleTxt.text = intent.extras!!.getString(Constants.IN_APP_TITLE)
                backButton.setImageResource(R.drawable.ic_close_black)
                toolbarTile.gravity = Gravity.CENTER
               // headerLineView.visibility = View.GONE
                dotImage.visibility = View.VISIBLE*/

            if(intent.getStringExtra(Constants.ORANGE_TITLE) != null)
            {

              //  dotImage.visibility = View.GONE
             //   toolbarTile.setText(getString(R.string.back))
               /* toolbar.visibility = View.GONE
                toolbarGeneric.visibility = View.VISIBLE*/
                backButtonLayout.setOnClickListener{ finish() }

                backButtonLayout.contentDescription = getString(R.string.ada_back_button) + getString(R.string.back)

                if(link.isNullOrEmpty())
                {
                    noDescriptionText.visibility = View.VISIBLE
                    commonWebProgressBar.visibility = View.GONE
                    return
                }

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

        callIrisUrl()

        unitPullToRefresh.setColorSchemeColors( getColor(R.color.checkBoxColor))
        unitPullToRefresh.setOnRefreshListener {

            unitPullToRefresh.isRefreshing = false
            callIrisUrl()

        }

    }

    override fun onBackPressed()
    {
        if (this.commonDetailWebView.canGoBack())
        {
            this.commonDetailWebView.goBack()
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
    private fun callIrisUrl() {
        commonWebProgressBar.visibility  = View.VISIBLE
        val stickyWork = StickyInfoGrabber(this)

        stickyWork.generateReturnMuleSoftStickySessionForTargetUrl(link.toString(),
                object : NetworkListener {
                    override fun onNetworkResponse(response: Pair<String, Any>)
                    {

                        if (response.first == NetworkConstants.SUCCESS)
                        {
                            Util.trace("Token Is : " + response.second)

                            val gson = Gson()
                            val muleSoftSession = gson.fromJson<MuleSoftSession>(response.second.toString(), MuleSoftSession::class.java)

                            var finalUrlToOpen  = BuildConfig.STICKY_FORWARD_URL + File.separator + muleSoftSession.token


                             currentUrlLoading = finalUrlToOpen
                            commonDetailWebView.loadUrl(finalUrlToOpen)
                        }


                    }

                })

        Util.trace("Opening : " + link)


    }

    private fun refreshPage() {
      //  initWebView()
        commonDetailWebView.reload()
    }

    private fun attachListener() {

        commonDetailWebView.setOnTouchListener(this)



    }

    override fun onClick(v: View?) {


    }

    private inner class InAppWebView : WebViewClient() {
        override fun shouldOverrideUrlLoading(    view: WebView,url: String  ): Boolean
        {
            val result = commonDetailWebView.hitTestResult
            if(result.type == WebView.HitTestResult.SRC_ANCHOR_TYPE)
            {
                DialogUtils.screenNamePrefix = "in-app-browser:link-out"
                val stickyWork  = StickyInfoGrabber(this@GettingStartedDetailActivity)
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
                callIrisUrl()
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

        ConnectivityReceiver.connectivityReceiverListener = this@GettingStartedDetailActivity
        connectivityReceiver = ConnectivityReceiver(networkLayout)
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(connectivityReceiver, intentFilter)
    }

}
