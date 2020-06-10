package edu.capella.mobile.android.activity

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Pair
import android.view.MotionEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.bean.MuleSoftSession
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.task.StickyInfoGrabber
import edu.capella.mobile.android.utils.LinkTouchMovementMethod
import kotlinx.android.synthetic.main.activity_assessment_detail.*
import kotlinx.android.synthetic.main.activity_assessment_detail.studyTitle
import kotlinx.android.synthetic.main.activity_assessment_detail.studyWebView
import kotlinx.android.synthetic.main.activity_assessment_detail.warningLayout
import kotlinx.android.synthetic.main.activity_common_webview.commonWebProgressBar
import kotlinx.android.synthetic.main.toolbar_common.*
import java.io.File


/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  15-04-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
class AssessmentDetailActivity : MenuActivity()/*BaseActivity()*/ ,  ConnectivityReceiver.ConnectivityReceiverListener, View.OnTouchListener,
    Handler.Callback{


    /**
     * URL which needs to open in WebView or in-app browser
     */
    private var link: String? = ""

    private var currentUrlLoading: String? = ""
    private var isFirstUrl = true
    private var isWebViewCLick = false

    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    private  var isInternetConnection: Boolean=false

    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null
    private var handler: Handler? = null
    private val CLICK_ON_WEBVIEW = 1
    private val CLICK_ON_URL = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_assessment_detail)
        setContentChildView(R.layout.activity_assessment_detail,true)
        initSwipeToRefresh()
        initValue()
        initWebView()
        OmnitureTrack.trackAction("course:assessment:content-detail")
    }

    private fun initSwipeToRefresh(){
        assessmentRefresh.setColorSchemeColors( getColor(R.color.checkBoxColor))
        assessmentRefresh.setOnRefreshListener {

            assessmentRefresh.isRefreshing = false
            isWebViewCLick = false
            isFirstUrl = true
            callIrisUrl()

        }
    }

    @SuppressLint("SetTextI18n")
    private fun initValue() {
        backButtonLayout.contentDescription  = getString(R.string.ada_back_button) +  HtmlCompat.fromHtml(
            intent.extras?.getString(Constants.CONTENT_TITLE)!!,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        ).toString()

         studyTitle.text = HtmlCompat.fromHtml(
            intent.extras?.getString(Constants.TITLE)!!,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        ).toString()

        link = getFinalUrlTOLoad()

        val title = HtmlCompat.fromHtml(
            intent.extras?.getString(Constants.CONTENT_TITLE)!!,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        ).toString()

        backHeaderTxt.text = title
        if (intent.extras?.getBoolean(Constants.SHOW_WARING)!!) {
            warningLayout.visibility = View.VISIBLE
//            backHeaderTxt.visibility = View.GONE

            val ss = SpannableString(resources.getString(R.string.mobile_view_notice))
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    textView.contentDescription = getString(R.string.visit_courseroom)  + getString(R.string.link_will_open)
                    DialogUtils.screenNamePrefix = "visit:courseroom:linkout"
                    val stickyWork = StickyInfoGrabber(this@AssessmentDetailActivity)
                    stickyWork.generateMuleSoftStickySessionForTargetUrl(
                        intent.extras?.getString(Constants.COURSE_MESSAGE_LINK)!!,
                        BuildConfig.STICKY_FORWARD_URL
                    )
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)

                    if (alertTxt.isPressed) {
                        ds.color = ContextCompat.getColor(
                            this@AssessmentDetailActivity,
                            R.color.blue_900
                        );
                    } else {
                        ds.color = ContextCompat.getColor(
                            this@AssessmentDetailActivity,
                            R.color.checkBoxColor
                        );
                    }
                    ds.isUnderlineText = false
                    alertTxt.invalidate();

//                    ds.color = ContextCompat.getColor(this@AssessmentDetailActivity,R.color.blue_600);
//                    ds.isUnderlineText = false
                }
            }
            ss.setSpan(
                clickableSpan,
                resources.getString(R.string.mobile_view_notice).indexOf("visit"),
                resources.getString(R.string.mobile_view_notice).indexOf("courseroom") + 10,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            alertTxt.text = ss
            alertTxt.setHighlightColor(Color.TRANSPARENT);
            alertTxt.movementMethod = LinkTouchMovementMethod()
            alertTxt.highlightColor = ContextCompat.getColor(this, R.color.border_grey_300);
            alertTxt.setOnClickListener {
                alertTxt.contentDescription = getString(R.string.visit_courseroom)  + getString(R.string.link_will_open)
                DialogUtils.screenNamePrefix = "visit:courseroom:linkout"
                val stickyWork = StickyInfoGrabber(this@AssessmentDetailActivity)
                stickyWork.generateMuleSoftStickySessionForTargetUrl(
                    intent.extras?.getString(Constants.COURSE_MESSAGE_LINK)!!,
                    BuildConfig.STICKY_FORWARD_URL
                )
            }
//            alertTxt.text = resources.getString(R.string.mobile_view_notice)
        } else {
            backHeaderTxt.visibility = View.VISIBLE
            warningLayout.visibility = View.GONE
        }
    }


    private fun getFinalUrlTOLoad(): String {
        var substringContent = intent.extras?.getString(Constants.CONTENT)

        Util.trace("sharePref", Preferences.getValue(PreferenceKeys.COURSE_ROOM_DISCUSSION_LINK))
        val headerLink = Preferences.getValue(PreferenceKeys.COURSE_ROOM_DISCUSSION_LINK)
        var urlWithoutDomainName = headerLink?.substring(8, headerLink.length);
        var schoolName = urlWithoutDomainName?.substring(0, urlWithoutDomainName.indexOf("/"))
        var finalContent = ""
        if (substringContent?.contains("@")!!) {
            finalContent = "https://" + schoolName + "/" + substringContent?.subSequence(
                substringContent?.lastIndexOf("@") + 1, substringContent?.length
            ).toString()
        } else {

            finalContent = "https://" + schoolName + "/" + substringContent?.subSequence(
                substringContent?.lastIndexOf(".edu/") + 5, substringContent?.length
            ).toString()
        }
        Util.trace(
            "common content substring",
            finalContent.subSequence(0, finalContent.indexOf("\"")).toString()
        )
        return finalContent.subSequence(0, finalContent.indexOf("\"")).toString()
    }

    fun initWebView() {
        handler =  Handler(this)
        studyWebView.webViewClient = InAppWebView()
        studyWebView.isVerticalScrollBarEnabled = false
        val webSettings: WebSettings = studyWebView.settings
        studyWebView.setOnTouchListener(this);
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true

        webSettings.allowContentAccess = true
        webSettings.allowFileAccess = true
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.allowUniversalAccessFromFileURLs = true

        callIrisUrl()
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
            if(isWebViewCLick){
                DialogUtils.screenNamePrefix = "assessment:detail-linkout"
                val stickyWork  = StickyInfoGrabber(this@AssessmentDetailActivity)
                stickyWork.generateMuleSoftStickySessionForTargetUrl(currentUrlLoading.toString(), BuildConfig.STICKY_FORWARD_URL)
            }else{
                handler?.sendEmptyMessage(CLICK_ON_URL);
            }


            currentUrlLoading = url

            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            commonWebProgressBar.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            commonWebProgressBar.visibility = View.GONE
        }
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

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            handler?.sendEmptyMessageDelayed(CLICK_ON_WEBVIEW, 500);
            isWebViewCLick = true
        }
        return false;
    }

    private fun omnitureStatus() {
        OmnitureTrack.trackAction("course:assessment:content-detail-linkout")
    }

    /**
     * Method read title, date and other details from passed parameters and show open passed url
     * in WebView.
     *
     */
    private fun callIrisUrl() {
        commonWebProgressBar.visibility = View.VISIBLE
        val stickyWork = StickyInfoGrabber(this)

        stickyWork.generateReturnMuleSoftStickySessionForTargetUrl(link.toString(),
            object : NetworkListener {
                override fun onNetworkResponse(response: Pair<String, Any>) {

                    try {
                        if (response.first == NetworkConstants.SUCCESS) {
                            Util.trace("Token Is : " + response.second)

                            val gson = Gson()
                            val muleSoftSession = gson.fromJson<MuleSoftSession>(
                                response.second.toString(),
                                MuleSoftSession::class.java
                            )

                            var finalUrlToOpen =
                                BuildConfig.STICKY_FORWARD_URL + File.separator + muleSoftSession.token
                            currentUrlLoading = finalUrlToOpen
                            studyWebView.loadUrl(finalUrlToOpen)
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }


                }

            })

        Util.trace("Opening : " + link)


    }

    fun get_returned_url(): String? {
        return currentUrlLoading
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        isInternetConnection = isConnected
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
        }
    }

    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun initNetworkBroadcastReceiver()
    {

        ConnectivityReceiver.connectivityReceiverListener = this@AssessmentDetailActivity
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

    }
}
