package edu.capella.mobile.android.activity

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.Constants
import edu.capella.mobile.android.utils.DialogUtils
import edu.capella.mobile.android.utils.OmnitureTrack
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.task.StickyInfoGrabber
import edu.capella.mobile.android.widgets.CPTextView
import kotlinx.android.synthetic.main.activity_campus_detail.*
import kotlinx.android.synthetic.main.activity_campus_detail.toolbar
import kotlinx.android.synthetic.main.toolbar.*


/**
 * CampusDetailActivity.kt :  Screen responsible for showing detailed Campus news.
 *
 * @author  :  Kush.pandya
 * @version :  1.0
 * @since   :  03-02-2020
 *
 *
 */
class CampusDetailActivity : MenuActivity()/*BaseActivity()*/
{

    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_campus_detail)
        setContentChildView(R.layout.activity_campus_detail,true)
        initialiseToolbar()
        setWebVIew()
        OmnitureTrack.trackAction("news:details")
    }

    /**
     * Method used to set title of scree and to bind the click events with certain widgets
     * used inside screen
     *
     */
    private fun initialiseToolbar() {
        val toolbarTile = toolbar.findViewById<CPTextView>(R.id.headerTxt)
        toolbarTile.text = getString(R.string.news)

        backButtonLl.setOnClickListener { finish() }
        backButtonLl.contentDescription = getString(R.string.ada_back_button) + ", "+getString(R.string.news)


    }

    /**
     * Method read title, date and other details from passed parameters and show open passed url
     * in WebView.
     *
     */
    private fun setWebVIew() {
        val titleText = intent.extras?.getString(Constants.ITEM_TITLE)
        val date = intent.extras?.getString(Constants.DATE)

        titleTxtView.text = titleText
        dateTxtView.text = Util.getDate(date?.toLong()!!, Constants.DATE_FORMAT)


        val itemDescription = intent.extras?.getString(Constants.ITEM_DESCRIPTION)
        val mimeType = "text/html"
        val encoding = "utf-8"
        campusDetailWebView.webViewClient = MyWebViewClient()
        campusDetailWebView.settings.domStorageEnabled = true
        campusDetailWebView.settings.setAppCacheEnabled(true)
        campusDetailWebView.settings.setAppCachePath(applicationContext.filesDir.absolutePath + "/cache")
        campusDetailWebView.settings.databaseEnabled = true
        campusDetailWebView.isVerticalScrollBarEnabled = false
        campusDetailWebView.loadDataWithBaseURL(null, itemDescription, mimeType, encoding, null)
    }

    /**
     * Inner class used for tracking the url being opened inside WebView once user tap over it,
     * after recognizing url tapped, it will request to generate MuleSoft token with tapped
     * url and open the IRIS url in external browser.
     *
     */
    private inner class MyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            DialogUtils.screenNamePrefix="campus:link-out"
            val stickyWork = StickyInfoGrabber(this@CampusDetailActivity)
            stickyWork.generateMuleSoftStickySessionForTargetUrl(
                url!!,
                BuildConfig.STICKY_FORWARD_URL
            )
//                stickyWork.validateAndOpenUrl(url!!, BuildConfig.STICKY_FORWARD_URL)
            Util.trace("open_url", url)
            return true
        }

    }

}

