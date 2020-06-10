package edu.capella.mobile.android.activity

import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.Constants
import edu.capella.mobile.android.utils.OmnitureTrack
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.bean.EventMessage
import edu.capella.mobile.android.task.StickyInfoGrabber
import edu.capella.mobile.android.widgets.CPTextView
import kotlinx.android.synthetic.main.activity_alert_detail.*
import kotlinx.android.synthetic.main.activity_campus_detail.toolbar
import kotlinx.android.synthetic.main.toolbar.*

/**
 * AlertDetailActivity.kt :  Alert detail screen responsible for showing alerts inside in-app browser
 * i.e. WebView, this activity requires url as input through intent class object.
 *
 * @author  :  Kush.pandya
 * @version :  1.0
 * @since   :  03-02-2020
 *
 * @see WebView
 *
 */
class AlertDetailActivity : MenuActivity()  //BaseActivity()
{

    /**
     * webViewUrl : stores value of url which is going to open inside in-app browser of this
     * activity/screen.
     */
    private var webViewUrl: String? = ""

    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_alert_detail)
        setContentChildView(R.layout.activity_alert_detail,true)
        initialiseToolbar()
        setWebVIew()
        OmnitureTrack.trackState("news:alert-open")
    }

    /**
     * Method used to set title of scree and to bind the click events with certain widgets
     * used inside screen
     *
     */
    private fun initialiseToolbar() {
        val toolbarTile = toolbar.findViewById<CPTextView>(R.id.headerTxt)
        toolbarTile.text = getString(R.string.alert)
        backButtonLl.setOnClickListener { finish() }
        backButtonLl.contentDescription = resources.getString(R.string.ada_back_button)+" "+getString(R.string.alert)
    }

    /**
     * Method responsible for opening the given url inside in-app browser i.e. WebView
     *
     * @see WebView
     *
     */
    private fun setWebVIew() {
        OmnitureTrack.trackAction("news:alert-detail-linkout")
        if (intent.hasExtra(Constants.ALERT_DATA)) {
            val eventMessage = intent.getSerializableExtra(Constants.ALERT_DATA) as EventMessage
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                webViewUrl =eventMessage.messageText
            }
        }
        val mimeType = "text/html"
        val encoding = "utf-8"
        alertDetailWebView.webViewClient = CustomWebViewClient()
        alertDetailWebView.settings.domStorageEnabled = true
        alertDetailWebView.settings.setAppCacheEnabled(true)
        alertDetailWebView.settings.databaseEnabled = true
        alertDetailWebView.isVerticalScrollBarEnabled = false
        alertDetailWebView.settings.setAppCachePath(applicationContext.filesDir.absolutePath + "/cache")
        alertDetailWebView.loadDataWithBaseURL(null, webViewUrl, mimeType, encoding, null)
    }

    /**
     * Inner class used for tracking the url being opened inside WebView once user tap over it,
     * after recognizing url tapped, it will request to generate MuleSoft token with tapped
     * url and open the IRIS url in external browser.
     *
     */
    private inner class CustomWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            val stickyWork  = StickyInfoGrabber(this@AlertDetailActivity)
            stickyWork.generateMuleSoftStickySessionForTargetUrl(url!!, BuildConfig.STICKY_FORWARD_URL)
            Util.trace("open_url",url)
            return true
        }


    }
}


