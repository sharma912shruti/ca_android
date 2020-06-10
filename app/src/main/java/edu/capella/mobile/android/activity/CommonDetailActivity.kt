package edu.capella.mobile.android.activity

import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Pair
import android.view.MotionEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebView.*
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.bean.MuleSoftSession
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.task.StickyInfoGrabber
import edu.capella.mobile.android.utils.*
import kotlinx.android.synthetic.main.activity_common_detail.*
import kotlinx.android.synthetic.main.page_warning_layout.*
import kotlinx.android.synthetic.main.toolbar_generic.*
import java.io.File


/**
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  26-03-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
class CommonDetailActivity: MenuActivity(), View.OnTouchListener,Handler.Callback,ConnectivityReceiver.ConnectivityReceiverListener  {

    /**
     * URL which needs to open in WebView or in-app browser
     */
    private var link: String? = ""
    private var isFirstTimeLoading:Boolean = true

    private var currentUrlLoading: String? = ""
    private var isFirstUrl = true
    private var isWebViewCLick = false

    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null

    var loadingFinished = true
    var redirect = false
    private var handler: Handler? = null
    private val CLICK_ON_WEBVIEW = 1
    private val CLICK_ON_URL = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_common_detail)
        setContentChildView(R.layout.activity_common_detail,true)
        initSwipeToRefresh()
        initValue()
        initWebView()
        omnitureActionStatus()
    }

    private fun initSwipeToRefresh(){
        commonPullToRefresh.setColorSchemeColors( getColor(R.color.checkBoxColor))
        commonPullToRefresh.setOnRefreshListener {

            commonPullToRefresh.isRefreshing = false
            isWebViewCLick = false
            loadingFinished = true
            redirect = false
            isFirstUrl = true
            callIrisUrl()

        }
    }

    private fun initValue(){
        genericTitleTxt.text = HtmlCompat.fromHtml(intent.extras?.getString(Constants.TITLE)!!,
            HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        link = getFinalUrlTOLoad()
        studyTitle.text = HtmlCompat.fromHtml(intent.extras?.getString(Constants.CONTENT_TITLE)!!,
            HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        if(intent?.extras?.getString(Constants.TITLE).equals(resources.getString(R.string.study))){
            backHeaderTxt.text = resources.getString(R.string.studies)
        }

        backButtonLayout.contentDescription = getString(R.string.ada_back_button) +  backHeaderTxt.text.toString()
        if(intent.extras?.getBoolean(Constants.SHOW_WARING)!!){
            warningLayout.visibility = View.VISIBLE
            backHeaderTxt.visibility = View.GONE
            alertTxt.text = resources.getString(R.string.mobile_view_notice)
        }else{
            backHeaderTxt.visibility = View.VISIBLE
            warningLayout.visibility = View.GONE
        }
    }

    private fun getFinalUrlTOLoad():String{
        var substringContent = intent.extras?.getString(Constants.CONTENT)

        Util.trace("sharePref",Preferences.getValue(PreferenceKeys.COURSE_ROOM_DISCUSSION_LINK))
        val headerLink = Preferences.getValue(PreferenceKeys.COURSE_ROOM_DISCUSSION_LINK)
        var urlWithoutDomainName = headerLink?.substring(8, headerLink.length);
        var schoolName =urlWithoutDomainName?.substring(0,  urlWithoutDomainName.indexOf("/"))

        var finalContent = ""
        if(substringContent?.contains("@")!!) {
            finalContent  = "https://" + schoolName + "/" + substringContent?.subSequence(
                getFirstIndex(substringContent), substringContent?.length
            ).toString()
        }else{

            finalContent = "https://" + schoolName + "/" + substringContent?.subSequence(
                substringContent?.lastIndexOf(".edu/")+5, substringContent?.length
            ).toString()
        }
        Util.trace("common content substring",finalContent.subSequence(0,finalContent.indexOf("\"")).toString())
        return finalContent.subSequence(0,finalContent.indexOf("\"")).toString()
    }

    private fun getFirstIndex(subString:String):Int{
        val stringChar = subString.toCharArray()
        var count = 0
        var charIndex = 0
        for(index in 0 until stringChar.size){
            if(stringChar[index] == '@'){
                count +=  1
            }
            if(count == 4){
                charIndex = index+1
                break
            }
        }
            return charIndex
    }

     fun initWebView() {
         handler =  Handler(this)
        studyWebView.webViewClient = InAppWebView()
        studyWebView.isVerticalScrollBarEnabled = false
         studyWebView.setOnTouchListener(this);
        val webSettings: WebSettings = studyWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true


         webSettings.setAppCacheEnabled(true)
         webSettings.loadsImagesAutomatically = true
         webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;

        webSettings.allowContentAccess = true
        webSettings.allowFileAccess = true
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.allowUniversalAccessFromFileURLs = true
        callIrisUrl()
    }

    @Override
    override fun handleMessage(msg: Message): Boolean {
        if (msg.what === CLICK_ON_URL) {
            handler!!.removeMessages(CLICK_ON_WEBVIEW)
            studyWebView.loadUrl(currentUrlLoading)
            return true
        }
        return false
    }

    /**
     * Inner class used for tracking the url being opened inside WebView once user tap over it,
     * after recognizing url tapped, it will request to generate MuleSoft token with tapped
     * url and open the IRIS url in external browser.
     *
     */
    private inner class InAppWebView : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {



            if (!loadingFinished) {
                redirect = true;
            }

            if(isWebViewCLick){
                    omnitureLinkOutStatus()
                    val stickyWork  = StickyInfoGrabber(this@CommonDetailActivity)
                    stickyWork.generateMuleSoftStickySessionForTargetUrl(currentUrlLoading.toString(), BuildConfig.STICKY_FORWARD_URL)
            }else{
                handler?.sendEmptyMessage(CLICK_ON_URL);
            }

            currentUrlLoading = url

            loadingFinished = false;


            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            commonWebProgressBar.visibility  = View.VISIBLE
        }
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if(!redirect){
                loadingFinished = true;
            }

            if(loadingFinished && !redirect){
                //HIDE LOADING IT HAS FINISHED
                commonWebProgressBar.visibility  = View.GONE
                Handler().postDelayed({
                    isFirstUrl = false
                },3000)

            } else{
                redirect = false
            }


        }
    }

    private fun omnitureLinkOutStatus(){
        if(intent.extras?.getString(Constants.TITLE)?.contains(resources.getString(R.string.study))!!){
            OmnitureTrack.trackAction("study:detail-linkout")
        }else if(intent.extras?.getString(Constants.TITLE)?.contains(resources.getString(R.string.syllabus))!!){
            OmnitureTrack.trackAction("course:syllabus-detail-linkout")
        }
    }

    private fun omnitureActionStatus(){
        if(intent.extras?.getString(Constants.TITLE)?.contains(resources.getString(R.string.study))!!){
            OmnitureTrack.trackAction("course:study-detail")
        }else if(intent.extras?.getString(Constants.TITLE)?.contains(resources.getString(R.string.syllabus))!!){
            OmnitureTrack.trackAction("course:syllabus-detail")
        }
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
                    try{
                    if (response.first == NetworkConstants.SUCCESS)
                    {
                        Util.trace("Token Is : " + response.second)

                        val gson = Gson()
                        val muleSoftSession = gson.fromJson<MuleSoftSession>(response.second.toString(), MuleSoftSession::class.java)

                        var finalUrlToOpen  = BuildConfig.STICKY_FORWARD_URL + File.separator + muleSoftSession.token
                        currentUrlLoading = finalUrlToOpen
                        commonWebProgressBar.visibility  = View.GONE
                        studyWebView.loadUrl(finalUrlToOpen)
                    }else
                    {
                        commonWebProgressBar.visibility  = View.GONE
                    }
                    }catch (e:Exception){
                        e.printStackTrace()
                        commonWebProgressBar.visibility  = View.GONE
                    }

                }

            })

        Util.trace("Opening : " + link)


    }


    fun get_returned_url():String?{
        return currentUrlLoading
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
//            handler?.sendEmptyMessageDelayed(CLICK_ON_WEBVIEW, 500);
            isWebViewCLick = true
        }
        return false;
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

        ConnectivityReceiver.connectivityReceiverListener = this@CommonDetailActivity
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
                isWebViewCLick = false
                loadingFinished = true
                redirect = false
                isFirstUrl = true
                callIrisUrl()
            }else
            {
                Util.trace("Can not reload")
            }

        }else
        {
            //commonWebProgressBar.visibility  = View.GONE
            //NETWORK GONE
            isNetworkFailedDueToConnectivity = true
        }
    }
}
