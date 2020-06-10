package edu.capella.mobile.android.activity

import android.os.Bundle
import android.view.View
import android.view.View.AccessibilityDelegate
import android.webkit.*
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.app.CapellaApplication
import edu.capella.mobile.android.base.BaseActivity
import edu.capella.mobile.android.task.StickyInfoGrabber
import edu.capella.mobile.android.utils.*
import kotlinx.android.synthetic.main.activity_formatted_webview.*
import kotlinx.android.synthetic.main.toolbar_generic.*


/**
 * CommonWebViewActivity.kt :  Screen working as in-app browser, opens given url in WebView
 *
 * @author  :  Jayesh.lahare
 * @version :  1.0
 * @since   :  03-02-2020
 *
 * @see WebView
 */
class FormatedWebViewActivity : BaseActivity()  {


    /**
     * URL which needs to open in WebView or in-app browser
     */
    private var link: String? = ""

    private var currentUrlLoading: String? = "";
  //  private var executeUrl: String? = ""

   var globaFormattedBody = ""
    // private var urlToOpen  = ""

    var isInit = true;

    var message_id = ""
    var forum_id = ""

    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formatted_webview)


        initWebView()
        initialiseToolbar()
        testBtn.setOnClickListener{
        setScreenData()
        }
        OmnitureTrack.trackState("discussion:post:formatted-view")

    }
    fun initWebView()
    {

        formatedWebView.settings.javaScriptEnabled = true
        formatedWebView.settings.domStorageEnabled = true;
        formatedWebView.webChromeClient = DirectionWebViewCromeClient()

       formatedWebView.webViewClient = DirectionWebViewClient()

        formatedWebView.addJavascriptInterface(TalkerInterface(), "CapellaTalker")
       // formatedWebView.settings.pluginState = WebSettings.PluginState.ON
       // formatedWebView.setAccessibilityDelegate(AccessibilityDelegate())
        formatedWebView.loadUrl("file:///android_asset/formatted_view.html");

    }
    fun setScreenData()
    {
        try{
            var formattedBody = intent.getStringExtra(Constants.FORMATTED_BODY)
            var fileName = intent.getStringExtra(Constants.FILE_NAME)
            var fileSize = intent.getStringExtra(Constants.FILE_SIZE)
            var fileLink = intent.getStringExtra(Constants.FILE_LINK)


            forum_id =  intent.getStringExtra(Constants.FORUM_ID)
            message_id =  intent.getStringExtra(Constants.PARENT_MESSAGE_ID)


             globaFormattedBody = formattedBody
            if(fileLink == null || fileLink.toString().trim().length<0 || fileLink =="null")
            {
                val hideElement = "javascript:hideElement('formattedAttachment')"
                formatedWebView.loadUrl(hideElement)
            }else
            {
                val buf = StringBuilder("javascript:setAttachmentDetail(")
                buf.append("'")
                buf.append(fileName)
                buf.append("','")
                buf.append(fileLink)
                buf.append("','")
                buf.append(fileSize)
                buf.append("')");

                formatedWebView.loadUrl(buf.toString())
            }

            if(formattedBody == null || formattedBody.toString().trim().length<0 || formattedBody == "null")
            {
                val hideElement = "javascript:hideElement('formattedText')"
                formatedWebView.loadUrl(hideElement)
            }else
            {

/*                formattedBody = formattedBody.replace("(","",true)
                formattedBody = formattedBody.replace("'","",true)
                formattedBody = formattedBody.replace(")","",true)
                formattedBody = Html.fromHtml( formattedBody , Html.FROM_HTML_MODE_LEGACY).toString().trim()*/
                Util.trace("formattedBody : $formattedBody")
                val buf = StringBuilder("javascript:setFormattedText('")
                buf.append("LoadFromAndroid")
                buf.append("')");

                formatedWebView.loadUrl(buf.toString())
            }

        }catch(t: Throwable){}

    }

    /**
     * Method used to set title of screen and to bind the click events with certain widgets
     * used inside screen
     *
     */
    private fun initialiseToolbar()
    {

        genericTitleTxt.text = getString(R.string.formatted_view)
        backButtonImg.setImageResource(R.drawable.ic_close_black)
//        backButtonLayout.contentDescription = getString(R.string.ada_back_button) + getString(R.string.close)
        backButtonLayout.contentDescription = getString(R.string.ada_formated_view_close_button) + getString(R.string.ada_button)

        backHeaderTxt.visibility = View.GONE

        backButtonLayout.setOnClickListener{ finish() }
        headerLineView.setBackgroundColor( getColor(R.color.headerColor))

    }

    override fun onBackPressed()
    {
        if (this.formatedWebView.canGoBack())
        {
            this.formatedWebView.goBack()
            return
        }

        overridePendingTransition( R.anim.no_anim, R.anim.slide_out_up )
        super.onBackPressed()
    }

    override fun onDestroy()
    {
        Preferences.addBoolean(PreferenceKeys.IS_APP_GONE_OUTSIDE , true)
        (applicationContext as CapellaApplication).validateOpenAMLogoutCase()
        super.onDestroy()
    }


    inner class TalkerInterface
    {
        @JavascriptInterface
        fun getFormatedData() : String {
           return globaFormattedBody
        }


        fun trace(msg: String) {
            Util.trace("Java Script", "" + msg)
        }

        @JavascriptInterface
        fun openAttachment()
        {
            try {


                var discussionRoomLink =
                    Preferences.getValue(PreferenceKeys.COURSE_ROOM_DISCUSSION_LINK)

                //https://courserooma.capella.edu/webapps/BBC-000-DeepLinks-BBLEARN/deeplink?landingpage=discussionboardforums&course_id=_244315_1

                var urlWithoutDomainName = discussionRoomLink?.substring(8, discussionRoomLink.length);
                var schoolName = urlWithoutDomainName?.substring(0, urlWithoutDomainName.indexOf("/"))

                var parts = urlWithoutDomainName?.split("&")
                var cours_id_value = parts!![parts.size - 1]

                var discussionBoardCourseRoomLink =
                    "https://" + schoolName + "/webapps/discussionboard/do/message?action=list_messages&forum_id=" + forum_id + "&nav=discussion_board_entry&" + cours_id_value + "&message_id=" + message_id + "#msg__" + message_id + "Id";


                Util.trace("URL IS  : " + discussionBoardCourseRoomLink)

                val stickyWork = StickyInfoGrabber(this@FormatedWebViewActivity)

                val customMsg = getString(R.string.leave_app_for_attachment_message)

                DialogUtils.screenNamePrefix = "course:discussions:post:attachment-linkout-to-post"
                stickyWork.generateMuleSoftStickySessionForTargetUrl(
                    discussionBoardCourseRoomLink,
                    BuildConfig.STICKY_FORWARD_URL,
                    customMsg
                )

               // OmnitureTrack.trackAction("DiscussionPost:open-attachment-link")
            }catch(t:Throwable){
                Util.trace("Attachment issue : $t")
                t.printStackTrace()
            }
        }

    }


    inner class DirectionWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if(isInit)
            {
                setScreenData()
                isInit = false;
            }
            Util.trace("Page Load", "Finished...")
        }


        override fun shouldOverrideUrlLoading(    view: WebView,url: String  ): Boolean
        {
            val result = formatedWebView.hitTestResult
            Util.trace("Extra is " + result.extra)
            Util.trace("Url is $url")

            try
            {
                if(Util.isAdaEnabled(this@FormatedWebViewActivity)) // ADA ENABLED , Consider UNKNOWN TYPE
                {
                    //if (result.type == WebView.HitTestResult.SRC_ANCHOR_TYPE && result.extra!!.contains( "EmbeddedFile" , true))
                    if ((result.type == WebView.HitTestResult.SRC_ANCHOR_TYPE || result.type == WebView.HitTestResult.UNKNOWN_TYPE )  && url.contains( "EmbeddedFile" , true))
                    {
                        //This is inline attachment, just tream like normal attachment as par STAN 12-May-20 7:30 PM
                        TalkerInterface().openAttachment()
                        return true
                    }

                    if ((result.type == WebView.HitTestResult.SRC_ANCHOR_TYPE || result.type == WebView.HitTestResult.UNKNOWN_TYPE)  && url.contains("https://courserooma.capella"
                        )
                    )
                    {
                        /* val stickyWork  = StickyInfoGrabber(this@FormatedWebViewActivity)
                    stickyWork.generateMuleSoftStickySessionForTargetUrl(url , BuildConfig.STICKY_FORWARD_URL)*/
                        return true
                    }

                    if((result.type == WebView.HitTestResult.SRC_ANCHOR_TYPE || result.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE || result.type == WebView.HitTestResult.UNKNOWN_TYPE) && !url.contains("kaltura",true))
                    {

                        if(url.startsWith("http:" , true) || url.startsWith("https:" , true) ) {
                            Util.openBrowserWithConfirmationPopup(this@FormatedWebViewActivity, url)
                        }

                        return true
                    }
                    if(url.contains("kaltura",true))
                        return false
                }else
                {
                    if (result.type == WebView.HitTestResult.SRC_ANCHOR_TYPE && result.extra!!.contains( "EmbeddedFile" , true))
                    //if ((result.type == WebView.HitTestResult.SRC_ANCHOR_TYPE || result.type == WebView.HitTestResult.UNKNOWN_TYPE )  && url.contains( "EmbeddedFile" , true))
                    {
                        //This is inline attachment, just tream like normal attachment as par STAN 12-May-20 7:30 PM
                        TalkerInterface().openAttachment()
                        return true
                    }

                    if (result.type == WebView.HitTestResult.SRC_ANCHOR_TYPE    && url.contains("https://courserooma.capella"
                        )
                    )
                    {
                        /* val stickyWork  = StickyInfoGrabber(this@FormatedWebViewActivity)
                    stickyWork.generateMuleSoftStickySessionForTargetUrl(url , BuildConfig.STICKY_FORWARD_URL)*/
                        return true
                    }

                    if (result.type == WebView.HitTestResult.SRC_ANCHOR_TYPE || result.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE )
                    {
                        if(url.startsWith("http:" , true) || url.startsWith("https:" , true) ) {
                            Util.openBrowserWithConfirmationPopup(this@FormatedWebViewActivity, url)
                        }

                        return true
                    }
                    if(url.contains("kaltura",true))
                        return false
                }



            }catch (t:Throwable){
                Util.trace("Error opening $t")
                t.printStackTrace()
                return true
            }

            view.loadUrl(url)
            currentUrlLoading = url;
            return true
        }

        override fun onReceivedError(
            view: WebView,
            errorCode: Int,
            description: String,
            failingUrl: String
        ) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            Util.trace("Page Error", "Error...")
        }
    }

    override fun onResume()
    {
        super.onResume()
         setScreenData()
    }

    class DirectionWebViewCromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            Util.trace("Progress ", " = $newProgress")
        }

        override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
            Util.trace("Console Message ", " = " + consoleMessage.message())
            return super.onConsoleMessage(consoleMessage)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_up)
    }


    //private fun openAttachment(linkToOpen: String , forum_id:  String, message_id: String)

}
