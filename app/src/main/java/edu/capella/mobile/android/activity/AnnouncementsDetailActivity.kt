package edu.capella.mobile.android.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.utils.Constants.ANNOUCEMENT_LIST_DATA
import edu.capella.mobile.android.utils.Constants.READ
import edu.capella.mobile.android.utils.Constants.UNREAD
import edu.capella.mobile.android.bean.CourseDetailBean
import edu.capella.mobile.android.task.StickyInfoGrabber
import edu.capella.mobile.android.widgets.CPTextView
import kotlinx.android.synthetic.main.activity_announcements_detail.*

import kotlinx.android.synthetic.main.toolbar.*

/**
 * AnnouncementsDetailActivity.kt :  Screen responsible for showing detailed Announcements Detail.
 *
 * @author  :  Kush.pandya
 * @version :  1.0
 * @since   :  03-02-2020
 *
 *
 */
class AnnouncementsDetailActivity :  MenuActivity()//BaseActivity()
{

    private var courseDetailData: CourseDetailBean? = null

    private var courseData: CourseDetailBean.CourseAnnouncement? = null

    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       //setContentView(R.layout.activity_announcements_detail)
        setContentChildView(R.layout.activity_announcements_detail , true)
        initialiseToolbar()
        setWebVIew()
        OmnitureTrack.trackState("course:announcements:detail")
//        val swipeRefreshColor =  ContextCompat.getColor(this@AnnouncementsDetailActivity,R.color.checkBoxColor)
//        this.webViewSwipeToRefresh.setColorSchemeColors(swipeRefreshColor)
//        webViewSwipeToRefresh.setOnRefreshListener {
//            setWebVIew()
//            webViewSwipeToRefresh.isRefreshing = false;
//        }
    }

    /**
     * Method used to set title of scree and to bind the click events with certain widgets
     * used inside screen
     *
     */
    private fun initialiseToolbar() {
        val toolbarTile = toolbarAnnouncement.findViewById<CPTextView>(R.id.headerTxt)
        toolbarTile.text = getString(R.string.announcements)

        val backButton = toolbarAnnouncement.findViewById<ImageView>(R.id.backButton)

//        toolbarTile.contentDescription=getString(R.string.ada_back_button)+" "+getString(R.string.announcements)
        backButtonLl.setOnClickListener { finishActivity()  }
        backButtonLl.contentDescription=resources.getString(R.string.ada_back_button)+" "+getString(R.string.announcements)
//        backButton.setOnClickListener {
//            onBackPressed() }
//        toolbarTile.setOnClickListener {
//            onBackPressed()  }

    }

    override fun onBackPressed() {
        finishActivity()
    }

    private fun finishActivity()
    {
        var responselist=courseDetailData?.newCourseroomData?.courseDetails?.courseAnnouncements!!

        var SHARED_COURSE_ID=responselist[0].courseIdentifier!!.toString().trim()
        val SHARRED_ANNOUNCEMENT_READ=SHARED_COURSE_ID+""+PreferenceKeys.FP_ANNOUNCEMENT_READ

//            var Readlist =
//                Util.stringToArrayList(Preferences.getArrayList(SHARRED_ANNOUNCEMENT_READ)!!)

        var Readlist =
            Util.stringToWords(Preferences.getArrayList(SHARRED_ANNOUNCEMENT_READ)!!)


        var fpAnnouncementReadlist: ArrayList<String>? = null

        if (Readlist.isEmpty()) {
            fpAnnouncementReadlist = ArrayList<String>()
        } else {
            fpAnnouncementReadlist = Readlist as ArrayList
        }

        val date: String = Util.getDate(courseData!!.startDate!!,Constants.DATE_FORMAT_SEC)!!
        for(i in responselist.indices)
        {
            if(Util.getDate(responselist[i].startDate!!,Constants.DATE_FORMAT_SEC)!!.toString().trim() == date.trim())
            {
                if(responselist[i].getRead().toString().trim() ==UNREAD.trim())
                {
                    responselist[i].setRead(READ)
                }

            }
            if(fpAnnouncementReadlist.size>0)
            {
                fpAnnouncementReadlist[i]=responselist[i].getRead().toString()
            }

        }
        Preferences.addArrayList(SHARRED_ANNOUNCEMENT_READ,fpAnnouncementReadlist.toString())

        val intent = Intent()

        intent.putExtra(ANNOUCEMENT_LIST_DATA, courseDetailData)
        setResult(Activity.RESULT_OK, intent)
        finish() //finishing activity

    }

    /**
     * Method read title, date and other details from passed parameters and show open passed url
     * in WebView.
     *
     */
    private fun setWebVIew() {
        OmnitureTrack.trackAction("course:announcements:detail")
        courseDetailData = intent.getSerializableExtra(Constants.ANNOUCEMENT_LIST_DATA) as CourseDetailBean
        courseData = intent.getSerializableExtra(Constants.ANNOUCEMENT_DATA) as CourseDetailBean.CourseAnnouncement
        val titleText = intent.extras?.getString(Constants.ITEM_TITLE)
        val date = intent.extras?.getString(Constants.DATE)

        if(courseData!!.getRead()!!.equals("read"))
        {
            blue_dot_image.visibility = View.GONE
            Txt.visibility = View.GONE
        }
        else
        {
            blue_dot_image.visibility = View.VISIBLE
            Txt.visibility = View.VISIBLE
        }
//        else if(Util.getDifferenceBetweenDateIsMoreThan72Hours(date!!.toLong())){
//            blue_dot_image.visibility = View.VISIBLE
//            Txt.visibility = View.VISIBLE
//        }else{
//            blue_dot_image.visibility = View.GONE
//            Txt.visibility = View.GONE
//        }

        titleTxtView.text = titleText
        if(date!=null)
            dateTxtView.text = Util.getDate(date?.toLong()!!, Constants.DATE_FORMAT)


        var itemDescription = intent.extras?.getString(Constants.ITEM_DESCRIPTION)

        var isHtmlPage = false
        if(itemDescription!!.contains("<html>" , true) && itemDescription!!.contains("</html>" , true))
        {
            isHtmlPage = true
        }else
        {
            isHtmlPage = false
        }

        if(isHtmlPage == false)
        {
            var header = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1,user-scalable=0\"><meta content=\"telephone=no\" name=\"format-detection\"><style>body,div,li,p,strong,td,ul{font-size: 16; font-family: Roboto-Regular;} a{color: #0079a2} p{margin: 0px; margin-top: 10px; text-indent: 0px;} .grey-line{border-top:1px solid #ccc; margin-top:1.3em;} .date-bullet{margin:2px 0 0 4px; color:#54bdf1;font-size:28; line-height:12px; width:8px; float:left;} .date-display{margin:2px 0 0 4px;float:left;font-weight: bold;font-size: 10; margin-left:3px;text-align: center;} .date-wrap{background:#fff; width: 90px; height:20px; position:relative; top:-0.5em; margin: 0 auto;} .date-new-item{position:relative; top:-1.2em; font-size:12; font-style:italic;color:#aaa; text-align:center; margin-bottom:1.3em;}</style></head><body>";

          //  var title = "<h2 style=\"color: #800D1E; font-family: AvenirLTStd-Medium, sans-serif; font-weight:normal; font-size: 24; padding: 10px; padding-top: 20px; text-align: center; text-transform: capitalize;\">" + title + "</h2>";

            var description = "<div style=\"padding-bottom: 30px; padding-left: 20px; padding-right: 20px; padding-top: 10px; word-wrap: break-word;\">" + itemDescription + "</div>";

            var footer = "</body></html>";
            var hrefHandler = "<script>var hyperlinks = document.getElementsByTagName('a');for(var i in hyperlinks) {var nowlink = hyperlinks[i];nowlink.onclick = function(e) {e.preventDefault();Ti.App.fireEvent('linklistener', {href:e.srcElement.attributes.href.textContent,page:'Study'});}}</script>";
           // itemDescription = header + title + description + hrefHandler+ footer
            itemDescription = header  + description + hrefHandler+ footer
        }


        val mimeType = "text/html"
        val encoding = "utf-8"
        announcementDetailWebView.webViewClient = MyWebViewClient()
        announcementDetailWebView.settings.domStorageEnabled = true
        announcementDetailWebView.settings.setAppCacheEnabled(true)
        announcementDetailWebView.settings.setAppCachePath(applicationContext.filesDir.absolutePath + "/cache")
        announcementDetailWebView.settings.databaseEnabled = true
        announcementDetailWebView.isVerticalScrollBarEnabled = false
        announcementDetailWebView.isHorizontalScrollBarEnabled = false
        announcementDetailWebView.loadDataWithBaseURL(null, itemDescription, mimeType, encoding, null)
        announcementDetailWebView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN;
    }

    /**
     * Inner class used for tracking the url being opened inside WebView once user tap over it,
     * after recognizing url tapped, it will request to generate MuleSoft token with tapped
     * url and open the IRIS url in external browser.
     *
     */
    private inner class MyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            val stickyWork = StickyInfoGrabber(this@AnnouncementsDetailActivity)
            stickyWork.generateMuleSoftStickySessionForTargetUrl(
                url!!,
                BuildConfig.STICKY_FORWARD_URL
            )
//                stickyWork.validateAndOpenUrl(url!!, BuildConfig.STICKY_FORWARD_URL)
            Util.trace("open_url", url)
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

           try {
               val javascript =
                   "javascript:document.getElementsByName('viewport')[0].setAttribute('content', 'initial-scale=1.0,maximum-scale=10.0');"
               view!!.loadUrl(javascript)
               view!!.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN;
           }catch (e:Exception)
           {
               e.printStackTrace()
           }
        }
    }
}
