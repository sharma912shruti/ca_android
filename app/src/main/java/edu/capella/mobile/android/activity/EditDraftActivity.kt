package edu.capella.mobile.android.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.Pair
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.utils.Constants.COURSE_DATA
import edu.capella.mobile.android.utils.Constants.EDITS_SAVED
import edu.capella.mobile.android.utils.Constants.POST_SUBMITTED

import edu.capella.mobile.android.utils.Util.isHTMLTagFound
import edu.capella.mobile.android.utils.Validator.checkEmptyString
import edu.capella.mobile.android.base.BaseActivity
import edu.capella.mobile.android.bean.*
import edu.capella.mobile.android.interfaces.EventListener
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import edu.capella.mobile.android.network.NetworkService
import edu.capella.mobile.android.task.StickyInfoGrabber
import edu.capella.mobile.android.utils.DialogUtils.onLoginErrorDialog
import kotlinx.android.synthetic.main.activity_edit_draft.*
import kotlinx.android.synthetic.main.internet_connection_layout.*
import kotlinx.android.synthetic.main.toolbar_announcement.*


/**
 * EditDraftActivity.kt :  Screen responsible for showing the data  of EditDraft
 *
 * @author  :  Kush.pandya
 * @version :  1.0
 * @since   :  03-02-2020
 *
 *
 */


class EditDraftActivity : BaseActivity()/*BaseActivity()*/,View.OnClickListener, NetworkListener,
    ConnectivityReceiver.ConnectivityReceiverListener,View.OnTouchListener {

    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null

    private var draftData: DiscussionDraftBean.Draft? = null

    private var isDraft: Boolean? = null

    var htmlList = ArrayList<String>()   //ArrayList<String>()

    private var checkAPI: Boolean? = true

    var courseId = ""
    var courseNumberId: Int?= null

    var viewDiscriptionTxt:String? = ""

    private var warningMsg: String? = ""

    private var courseData: CourseListBean.NewCourseroomData.CurrentCourseEnrollment? = null

    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    public var isInternetConnection: Boolean = false


    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_draft)
//        setContentChildView(R.layout.activity_edit_draft , true)
//        addValueHtml()
        initialiseToolbar()
        initialSetUp()
        attachListener()
        OmnitureTrack.trackState("course:discussions:drafts-edit")
    }

    override fun onResume() {
        super.onResume()
        init()
    }



    private fun initialSetUp() {
        saveDraftTxt.contentDescription = getString(R.string.save_as_draft) + getString(R.string.ada_button)
        submitTxt.contentDescription = getString(R.string.submit) + getString(R.string.ada_button)

        descriptionTxt.setImeActionLabel("<br>", KeyEvent.KEYCODE_ENTER);

        if (intent.hasExtra(Constants.DRAFT_DATA) && intent.hasExtra(COURSE_DATA)) {
            courseData =
                intent.getSerializableExtra(COURSE_DATA) as CourseListBean.NewCourseroomData.CurrentCourseEnrollment
            courseId= courseData?.courseIdentifier!!
            courseNumberId= courseData?.courseSection?.course?.catalogNumber!!.toInt()
            draftData =
                intent.getSerializableExtra(Constants.DRAFT_DATA) as DiscussionDraftBean.Draft
            descriptionTxt.setText(draftData!!.bodyText)
            subjectTxt.setText(draftData!!.subject)
            courseTxt.text=draftData!!.forumTitle
            if (draftData!!.parentMessageAuthorName != null) {
                responseToTxt.text =
                    resources.getString(R.string.response_to) + "" + draftData!!.parentMessageAuthorName.toString().split(
                        "\\s".toRegex()
                    )[0];
                responseToTxt.visibility = View.VISIBLE
                val font =
                    Typeface.createFromAsset(assets, "fonts/roboto_medium_italic.ttf")
                responseToTxt.typeface = font
            }

            if(draftData!!.attachmentLink!=null)
            {
                attachmentLayout.visibility=View.VISIBLE
                draftAttachmentLinkTxt.text = getFileName(draftData!!.attachmentLink.toString())
                attachmentLayout.contentDescription=getFileName(draftData!!.attachmentLink.toString())+" ."+resources.getString(R.string.ada_edit_delete_file_attachment)
                attachmentLayout.tag = draftData!!.attachmentLink
                attachmentLayout.setOnClickListener {
                    val linkToOpen = it.tag as String
                    openAttachmentLink(linkToOpen , draftData!!.forumId!! , draftData!!.id!!)
                }
            }

        }

        descriptionTxt.setSelection(descriptionTxt.text.length)
        subjectTxt.setSelection(subjectTxt.text.length)
        callDiscussionForumApi(courseId , courseNumberId!!)


    }

    private fun checkHTMLTag()
    {

        if(isHTMLTagFound(draftData!!.bodyFormattedText))
        {
            DialogUtils.showDialogEditingdraft(this, object : EventListener {
                override fun confirm() {

                    super.confirm()
                    finish()
                    OmnitureTrack.trackAction("course:discussions:drafts-cancel")

                }
            })


        }

    }


    private fun openAttachmentLink(linkToOpen: String , forum_id:  String, message_id: String) {

        var  discussionRoomLink = Preferences.getValue(PreferenceKeys.COURSE_ROOM_DISCUSSION_LINK)

        //https://courserooma.capella.edu/webapps/BBC-000-DeepLinks-BBLEARN/deeplink?landingpage=discussionboardforums&course_id=_244315_1

        var urlWithoutDomainName = discussionRoomLink?.substring(8, discussionRoomLink.length);
        var schoolName =urlWithoutDomainName?.substring(0,  urlWithoutDomainName.indexOf("/"))

        var parts = urlWithoutDomainName?.split("&")
        var cours_id_value = parts!![parts.size -1]

        var discussionBoardCourseRoomLink  = "https://" + schoolName + "/webapps/discussionboard/do/message?action=list_messages&forum_id=" + forum_id + "&nav=discussion_board_entry&" + cours_id_value + "&message_id=" + message_id + "#msg__" +message_id + "Id";

        Util.trace("URL IS  : " + discussionBoardCourseRoomLink)

        val stickyWork  = StickyInfoGrabber(this)
        stickyWork.generateMuleSoftStickySessionForTargetUrl(discussionBoardCourseRoomLink  , BuildConfig.STICKY_FORWARD_URL)

    }

    /**
     * Method used to set title of screen and to bind the click events with certain widgets
     * used inside screen
     *
     */
    private fun initialiseToolbar() {
        discriptionNetworkTxt.text=resources.getString(R.string.please_check_setting)
        headerTxt.text = getString(R.string.editDraft)
        backTxt.text = getString(R.string.cancel)
//        backButton.setOnClickListener { finish() }
//        backTxt.setOnClickListener { finish() }

        backButtonLl.setOnClickListener { onBackPressed()  }
        backButtonLl.contentDescription=resources.getString(R.string.ada_back_button)+" "+getString(R.string.cancel)
    }

    /**
     * Method triggered by broadcast receiver which is listening for Network state.
     *
     * @param isConnected : Tells whether internet is available or not
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        isInternetConnection = isConnected
        /* if (!isConnected) {
             networkLayout.visibility = View.VISIBLE
         } else {
             networkLayout.visibility = View.GONE
         }*/
        /*if(isConnected)
        {
            submitTxt.isEnabled = true
            saveDraftTxt.isEnabled = true
        }else
        {
            submitTxt.isEnabled = false
            saveDraftTxt.isEnabled = false
        }*/
    }


    /**
     * Method used to set click and change listeners for different widgets used over login screen
     */
    private fun attachListener() {
        saveDraftTxt.setOnClickListener(this)
        submitTxt.setOnClickListener(this)
        parentll.setOnTouchListener(this)
        descriptionHeaderTxt.setOnClickListener(this)

    }


    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.saveDraftTxt -> {

                isDraft = true
                warningMsg=resources.getString(R.string.Your_draft_edits)
                doSaveDraft()
                OmnitureTrack.trackAction("course:discussions:drafts-submit")

            }
            R.id.submitTxt -> {

                isDraft = false
                warningMsg=resources.getString(R.string.Your_post_submitted)
                doSaveDraft()
                OmnitureTrack.trackAction("course:discussions:drafts-save")
            }

            R.id.descriptionHeaderTxt -> {
                openViewDescriptionLink()
                OmnitureTrack.trackAction("course:discussions:drafts-edit:view-description")
            }



        }
    }

    private fun getFileName(path:String) : String
    {
        return path.substring(path.lastIndexOf("/",path.length , true) +1, path.length)
    }


    @SuppressLint("SetTextI18n")
    private fun editDraft() {
//        if (Util.isInternetOn(this)) {
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        val loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        callEditDraftApi(
            loginBean?.authData?.username!!,
            Preferences.getValue(PreferenceKeys.SECRET)!!
        )
//        }
    }

    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private  fun init() {
        ConnectivityReceiver.connectivityReceiverListener = this@EditDraftActivity
        connectivityReceiver = ConnectivityReceiver(networkLayout)
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(connectivityReceiver, intentFilter)
    }


    /**
     * Method creates a server request to load data from server for Discussion Draft API.
     *
     *
     * @param userName
     * @param password
     */
    private fun callEditDraftApi(userName: String, password: String) {
        try {

            val discription = descriptionTxt.text.toString()
            val finalDiscription = discription.replace("\n", "<br>")
            val params = HashMap<String, Any>()
            params[NetworkConstants.USER_NAME] = userName
            params[NetworkConstants.PASSWORD] = password
            params[NetworkConstants.ACTION] = Constants.EDIT_MESSAGE
            params[NetworkConstants.COURSE_IDENTIFIER] = courseData!!.courseIdentifier!!
            params[NetworkConstants.MESSAGE_ID] = draftData!!.id
            params[NetworkConstants.SUBJECT_TEXT] = subjectTxt.text.toString()
            params[NetworkConstants.BODY_TEXT] = finalDiscription
            params[NetworkConstants.IS_DRAFT] = isDraft!!

            val networkHandler = NetworkHandler(
                this,
                NetworkConstants.EDIT_DRAFT_API,
                params,
                NetworkHandler.METHOD_POST,
                this@EditDraftActivity, null
            )

            networkHandler.setSilentMode(false)
            networkHandler.isPostTypeSubmitting = true
            if (isDraft!!) {
                networkHandler.submitMessage = getString(R.string.saving_draft)
            } else {
                networkHandler.submitMessage = getString(R.string.submiting)
            }

            networkHandler.execute()
        }catch (e:Exception)
        {
            e.printStackTrace()
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

        checkIsChangedOrNot()
    }

    /**
     * Callback method of Network task which executes when communication completed with server
     *
     * @param response : API response received from server.
     * @see Pair
     */
    override fun onNetworkResponse(response: android.util.Pair<String, Any>) {


        if(checkAPI!!) {
            try {
                Util.trace("discussion draft first :  " + response.first)
                Util.trace("discussion draft second :  " + response.second)

                if (response.first == NetworkConstants.SUCCESS) {
                    val gson = Gson()
                    val editDraftBean = gson.fromJson<EditDraftBean>(
                        response.second.toString(),
                        EditDraftBean::class.java
                    )

                    if (editDraftBean.errorData == null) {
                        noDataLayout.visibility = View.GONE

                        val intent = Intent()
                        if (isDraft!!) {
                            intent.putExtra(EDITS_SAVED, EDITS_SAVED)
                        } else {
                            intent.putExtra(EDITS_SAVED, POST_SUBMITTED)
                        }
                        setResult(Activity.RESULT_OK, intent)
                        finish() //finishing activity

                    } else {
                        showWarningLayout()
                    }
                    editDraftBean?.errorData?.let {
                        showWarningLayout()

                    }
                } else {
                    showWarningLayout()
                }

            }catch (e:Exception)
            {
                showWarningLayout()
                e.printStackTrace()
            }
        }
        else {

            try {

                Util.trace("DiscussionForumBean : " + response.second)

                if (response.first == NetworkConstants.SUCCESS) {
                    val gson = Gson()
                    var discussionListBean = gson.fromJson<DiscussionForumBean>(
                        response.second.toString(),
                        DiscussionForumBean::class.java
                    )


                    if (discussionListBean?.errorData != null) {

                        return
                    }

                    // if(matesListBean?.classmatesData?.courseMembers!![0]!!.member!!.size!! <= 0)
                    //var f : DiscussionForumBean.NewDiscussionData.GroupDiscussion.Forum
                    var grouplist: List<DiscussionForumBean.NewDiscussionData.GroupDiscussion?>? =
                        discussionListBean.newDiscussionData?.groupDiscussions

                    var item = grouplist!![0]
                    var group = item
                    var forum = group?.forums

                    var forumValue: DiscussionForumBean.NewDiscussionData.GroupDiscussion.Forum? =
                        null

                    for (value in forum!!) {
                        if (value!!.id.equals(draftData!!.forumId)) {
                            forumValue = value
                            break
                        }

                    }

                    if (forumValue != null) {
                        extractHref(forumValue!!.descriptionText)
                    }

                } else {
                    DialogUtils.showGenericErrorDialog(this)
                }
                checkHTMLTag()
            }catch (e:Exception)
            {
                checkHTMLTag()
            }

        }
    }

    private fun extractHref(string:String?)
    {

        try{
            var str = string
            var parts = str?.split(" ")

            if (parts != null) {
                for(p in parts) {
                    if(p.indexOf("href",0 , true) != -1)
                    {
                        Util.trace("HREF : " + p)

                        var  discussionRoomLink = Preferences.getValue(PreferenceKeys.COURSE_ROOM_DISCUSSION_LINK)
                        var urlWithoutDomainName = discussionRoomLink?.substring(8, discussionRoomLink.length);
                        var schoolName =urlWithoutDomainName?.substring(0,  urlWithoutDomainName.indexOf("/"))

                        var pr = p
                        if(pr.indexOf(">") != -1)
                        {
                            pr = pr.substring(0, pr.indexOf(">"))
                        }

                        var view_description_link = "https://" + schoolName + "/" + pr.substring(pr.lastIndexOf("@")+1 , pr.length-1 )

                        Util.trace("view_description_link : " + view_description_link)
                        viewDiscriptionTxt=view_description_link
                        break
                    }
                }
            }



        }catch (t:Throwable){
            Util.trace(" setting VIEW_DESCRIPTION_LINK_PATH error $t")
        }
    }


    private fun openViewDescriptionLink()
    {
        OmnitureTrack.trackAction("course:discussions:threads:view-description")
        if(viewDiscriptionTxt != "") {

            val intent = Intent(this, UnitWebViewActivity::class.java)
            intent.putExtra(Constants.URL_FOR_IN_APP, viewDiscriptionTxt)
            intent.putExtra(Constants.ORANGE_TITLE, draftData!!.forumTitle)
            intent.putExtra(Constants.IN_APP_TITLE, "")
            startActivity(intent)
//            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            // overridePendingTransition( R.anim.slide_in_up, R.anim.no_anim)
        }
    }

    private fun showWarningLayout()
    {
        noDataLayout.visibility=View.VISIBLE
        warningMsgTxt.text=warningMsg
        Util.announceAda(warningMsg + getString(R.string.ada_please_try_again), this@EditDraftActivity)
    }


    /**
     * Method call LearnerProfile api to show details in PROFILE and IDCARD tabs.
     *
     */
    private fun callDiscussionForumApi(courseId: String , coursNumId: Int)
    {

        checkAPI=false

        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val params = HashMap<String, Any>()
        params.put(NetworkConstants.TOKEN, loginBean?.authData?.token!!)


        //POST /mobile-feed/json/newdiscussions.feed?action=getAllforums&courseidentifier=TESTSUB1331_006292_1_1201_1_03&formattype=text&courseid=7001 HTTP/1.1

        val qStringParams = HashMap<String, Any>()
        qStringParams.put(NetworkConstants.COURSE_IDENTIFIER, courseId)
        qStringParams.put(NetworkConstants.COURSE_NUMBER_ID, courseNumberId!!)
        qStringParams.put(NetworkConstants.ACTION, "getAllforums")
        qStringParams.put(NetworkConstants.FORMAT_TYPE, "text")


        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.COURSE_DISCUSSION_FORUM_API + NetworkService.getQueryString(qStringParams),
            params,
            NetworkHandler.METHOD_POST,
            this,
            null
        )

        networkHandler.execute()


    }


    private fun doSaveDraft() {
        OmnitureTrack.trackAction("course:discussions:drafts-save")
       
        checkAPI=true
        if(isDraft!!)
        {
            if (!checkEmptyString(subjectTxt.text.toString())) {

                onLoginErrorDialog(
                    this,
                    resources.getString(R.string.not_saved),
                    resources.getString(R.string.you_must_have_suject)
                )
            }
            else
            {

                editDraft()
            }
        }
        else if(!isDraft!!)
        {
            if (!checkEmptyString(descriptionTxt.text.toString())) {
                OmnitureTrack.trackAction("course:discussions:drafts-edit:view-description")
                onLoginErrorDialog(
                    this,
                    resources.getString(R.string.not_saved),
                    resources.getString(R.string.you_must_have_content)
                )
            }
            else if (!checkEmptyString(subjectTxt.text.toString())) {

                onLoginErrorDialog(
                    this,
                    resources.getString(R.string.not_saved),
                    resources.getString(R.string.you_must_have_suject)
                )
            }
            else{

                editDraft()

            }

        }



    }



    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
        return true
    }

    fun checkIsChangedOrNot()
    {
        try {


            if(draftData!!.subject != subjectTxt.text.toString())
            {

                Preferences.addBoolean(PreferenceKeys.IS_EDITED, true)
            }
            else  if(draftData!!.bodyText != descriptionTxt.text.toString())
            {

                Preferences.addBoolean(PreferenceKeys.IS_EDITED, true)
            }
            else
            {

                Preferences.addBoolean(PreferenceKeys.IS_EDITED, false)
            }
        }catch (e:Exception)
        {
            e.printStackTrace()
        }

    }

}

