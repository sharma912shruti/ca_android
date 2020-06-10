package edu.capella.mobile.android.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Bundle
import android.util.Pair
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.gson.Gson
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.base.BaseActivity
import edu.capella.mobile.android.bean.CreateThreadBean

import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import edu.capella.mobile.android.network.NetworkService

import kotlinx.android.synthetic.main.activity_new_thread.*
import kotlinx.android.synthetic.main.post_failure_layout.*
import kotlinx.android.synthetic.main.post_failure_layout.view.*
import kotlinx.android.synthetic.main.toolbar_generic.*
import kotlinx.android.synthetic.main.toolbar_generic.view.*


class CreatePostActivity : BaseActivity() ,  ConnectivityReceiver.ConnectivityReceiverListener,
    NetworkListener, View.OnTouchListener  {

    var isReplyScreen: Boolean = false
    var courseId = ""
    var forumId  = ""
    var subjectLine  = ""
    var parentMessageId =""
    var discussionTitle = ""

    var successMsg = ""
    var failureMsg = ""
    var networkFailMsg = ""
    var snakeDuration = 3000


    var POST_TYPE = 0
    var POST_THREAD_CREATE = 0
    var POST_THREAD_DRAFT = 1
    var POST_REPLY_THREAD = 2
    var POST_REPLY_DRAFT = 3

    var isRestoreCase: Boolean = false

    var oldSubjectText = ""
    var oldBodyText = ""


    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    private  var isInternetConnection: Boolean=false

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

        setContentView(R.layout.activity_new_thread)
        //setContentChildView(R.layout.activity_new_thread , true)

        newThreadToolbar.genericTitleTxt.text= getString(R.string.new_thread)

        if(intent.getStringExtra(Constants.BACK_TITLE) != null )
        {
            newThreadToolbar.backHeaderTxt.text=intent.getStringExtra(Constants.BACK_TITLE).toString()
            newThreadToolbar.backButtonLayout.contentDescription = getString(R.string.ada_back_button) + intent.getStringExtra(Constants.BACK_TITLE).toString()
        }

       discussionTitle =   intent.getStringExtra(Constants.POSTS_TITLE)

        backButtonImg.setOnClickListener {
            if(isReplyScreen == false)
                OmnitureTrack.trackState("course:discussions:new-thread:cancel")
            else
                OmnitureTrack.trackState("course:discussions:post:reply:cancel")

            finish()
        }

       if(intent.getBooleanExtra(Constants.IS_REPLY_SCREEN,false))
       {
           isReplyScreen = true
           initReplyUi()
       }else {
           isReplyScreen = false
           initNormalUi()
       }


        viewDescriptionTxt.setOnClickListener{

            openViewDescriptionLink()
        }

        createPostParent.setOnTouchListener(this)


    }

    private fun openViewDescriptionLink()
    {

        val url = Preferences.getValue(PreferenceKeys.VIEW_DESCRIPTION_LINK_PATH)

            val intent = Intent(this, UnitWebViewActivity::class.java)
            intent.putExtra(Constants.URL_FOR_IN_APP, url)
            intent.putExtra(Constants.ORANGE_TITLE, discussionTitle)
            intent.putExtra(Constants.IN_APP_TITLE, "")
            startActivity(intent)
           // overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
           //   overridePendingTransition( R.anim.slide_in_up, R.anim.no_anim)

        if(isReplyScreen == false)
            OmnitureTrack.trackAction("course:discussions:new-thread:view-description")
        else
            OmnitureTrack.trackAction("course:discussions:post:reply:view-description")

    }
    /**
     * Method initializes tabs with container data and Look and fill.
     *
     */
    private fun initNormalUi()
    {
        OmnitureTrack.trackState("course:discussions:new-thread:edit")
        courseId = intent.getStringExtra(Constants.COURSE_ID)
        forumId = intent.getStringExtra(Constants.FORUM_ID)

        postNameTxt.text = discussionTitle

        submitPostTxt.setOnClickListener{
            submitPost()
            OmnitureTrack.trackAction("course:discussions:new-thread:submit")

        }

        saveAsDraftTxt.setOnClickListener{
            submitPostAsDraft()
            OmnitureTrack.trackAction("course:discussions:new-thread:save")
        }
    }

    private fun initReplyUi()
    {
        OmnitureTrack.trackAction("course:discussions:post:reply")
        courseId = intent.getStringExtra(Constants.COURSE_ID)
        parentMessageId = intent.getStringExtra(Constants.PARENT_MESSAGE_ID)
        subjectLine = intent.getStringExtra(Constants.SUBJECT)

        oldSubjectText = subjectLine

        threadSubjectEditTxt.setText(subjectLine)

        var userGivenName = getString(R.string.response_to) + intent.getStringExtra(Constants.USER_GIVEN_NAME)
        responseNameTxt.visibility  = View.VISIBLE
        responseNameTxt.text = userGivenName
        val font = Typeface.createFromAsset(assets, "fonts/roboto_medium_italic.ttf")
        responseNameTxt.typeface = font

        newThreadToolbar.genericTitleTxt.text= getString(R.string.reply)

        postNameTxt.text = discussionTitle

        submitPostTxt.setOnClickListener{
            OmnitureTrack.trackState("course:discussions:post:reply:submit")
            submitPostReply()
        }

        saveAsDraftTxt.setOnClickListener{
            OmnitureTrack.trackState("course:discussions:post:reply:save")
            submitPostAsReplyDraft()
        }
    }
    private fun submitPostReply()
    {

        var subTxt = threadSubjectEditTxt.text.toString().trim()
        var bodyTxt = threadBodyEditTxt.text.toString().trim()

        if(Validator.isBlank(subTxt) && Validator.isBlank(bodyTxt)) // BOTH BLANK
        {
//            DialogUtils.onShowDialog(this , getString(R.string.not_saved) , getString(R.string.you_must_have_content))
            DialogUtils.onLoginErrorDialog(this , getString(R.string.not_saved) , getString(R.string.you_must_have_content))
            return
        }
        else if(Validator.isBlank(bodyTxt)) // BOTH BLANK
        {
//            DialogUtils.onShowDialog(this , getString(R.string.not_saved) , getString(R.string.you_must_have_content))

            DialogUtils.onLoginErrorDialog(this , getString(R.string.not_saved) , getString(R.string.you_must_have_content))
            return
        }
        else if(Validator.isBlank(subTxt) ) // SUBJECT BLANK
        {
//            DialogUtils.onShowDialog(this , getString(R.string.not_saved) , getString(R.string.you_must_have_suject))
            DialogUtils.onLoginErrorDialog(this , getString(R.string.not_saved) , getString(R.string.you_must_have_suject))
            return
        }

        snakeDuration = 3000
        POST_TYPE = POST_REPLY_THREAD
        successMsg = getString(R.string.reply_saved_successfully)
        failureMsg = getString(R.string.reply_saved_failure)

        networkFailMsg= getString(R.string.thread_submit_internet_failure)


        callReplySubmitPostApi(courseId , parentMessageId , false)
    }
    private fun submitPostAsReplyDraft()
    {

        var subTxt = threadSubjectEditTxt.text.toString().trim()
        var bodyTxt = threadBodyEditTxt.text.toString().trim()

//        if(Validator.isBlank(subTxt) && Validator.isBlank(bodyTxt)) // BOTH BLANK
//        {
//            DialogUtils.onShowDialog(this , getString(R.string.error) , getString(R.string.you_must_have_content))
//            return
//        }
        if(Validator.isBlank(subTxt) ) // SUBJECT BLANK
        {
//            DialogUtils.onShowDialog(this , getString(R.string.not_saved) , getString(R.string.you_must_have_suject))
            DialogUtils.onLoginErrorDialog(this , getString(R.string.not_saved) , getString(R.string.you_must_have_suject))
            return
        }

        snakeDuration = 5000
        POST_TYPE = POST_REPLY_DRAFT
        successMsg = getString(R.string.reply_draft_saved_successfully)
        failureMsg = getString(R.string.reply_draft_saved_failure)

        networkFailMsg= getString(R.string.thread_submit_internet_failure)

        callReplySubmitPostApi(courseId , parentMessageId , true)
    }

    private fun submitPost()
    {

        var subTxt = threadSubjectEditTxt.text.toString().trim()
        var bodyTxt = threadBodyEditTxt.text.toString().trim()

        if(Validator.isBlank(subTxt) && Validator.isBlank(bodyTxt)) // BOTH BLANK
        {
//            DialogUtils.onShowDialog(this , getString(R.string.not_saved) , getString(R.string.you_must_have_content))
            DialogUtils.onLoginErrorDialog(this , getString(R.string.not_saved) , getString(R.string.you_must_have_content))
            return
        }
        else if(Validator.isBlank(bodyTxt)) // BOTH BLANK
        {
//            DialogUtils.onShowDialog(this , getString(R.string.not_saved) , getString(R.string.you_must_have_content))
            DialogUtils.onLoginErrorDialog(this , getString(R.string.not_saved) , getString(R.string.you_must_have_content))
            return
        }
        else if(Validator.isBlank(subTxt) ) // SUBJECT BLANK
        {
//            DialogUtils.onShowDialog(this , getString(R.string.not_saved) , getString(R.string.you_must_have_suject))
            DialogUtils.onLoginErrorDialog(this , getString(R.string.not_saved) , getString(R.string.you_must_have_suject))
            return
        }

        snakeDuration = 3000
        POST_TYPE = POST_THREAD_CREATE
        successMsg = getString(R.string.thread_submitted_successfully)
        failureMsg = getString(R.string.thread_submit_failure)
        networkFailMsg= getString(R.string.thread_submit_internet_failure)

        callSubmitPostApi(courseId , forumId , false)
    }
    private fun  submitPostAsDraft()
    {

        var subTxt = threadSubjectEditTxt.text.toString().trim()
        var bodyTxt = threadBodyEditTxt.text.toString().trim()

//        if(Validator.isBlank(subTxt) && Validator.isBlank(bodyTxt)) // BOTH BLANK
//        {
//            DialogUtils.onShowDialog(this , getString(R.string.error) , getString(R.string.draft_blank_error_for_subject_body))
//            return
//        }
        if(Validator.isBlank(subTxt) ) // SUBJECT BLANK
        {
//            DialogUtils.onShowDialog(this , getString(R.string.not_saved) , getString(R.string.you_must_have_suject))
            DialogUtils.onLoginErrorDialog(this , getString(R.string.not_saved) , getString(R.string.you_must_have_suject))
            return
        }

        snakeDuration = 5000
        POST_TYPE = POST_THREAD_DRAFT
        successMsg = getString(R.string.draft_saved_successfully)
        failureMsg = getString(R.string.draft_saved_failure)
        networkFailMsg= getString(R.string.thread_submit_internet_failure)

        callSubmitPostApi(courseId , forumId , true)
    }




    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun initNetworkBroadcastReceiver()
    {

        ConnectivityReceiver.connectivityReceiverListener = this@CreatePostActivity
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

    /**
     * Factory method of activity, executes when activity goes in paused state.
     *
     */
    override fun onPause() {
        checkState()
        super.onPause()
        releaseConnectivityReceiver()


    }
    private fun checkState()
    {
        try {
            var isChange: Boolean = true
            //if ((threadSubjectEditTxt.text.toString().length == oldBodyText.length) && (threadBodyEditTxt.text.toString().length == oldBodyText.length)) {
            if ((threadSubjectEditTxt.text.toString() == oldSubjectText) && (threadBodyEditTxt.text.toString()  == oldBodyText)) {
                isChange = false
            }

            oldSubjectText = threadSubjectEditTxt.text.toString()
            oldBodyText = threadBodyEditTxt.text.toString()

            /*if(isChange)
            {
                oldSubjectText = threadSubjectEditTxt.text.toString()
                oldBodyText = threadBodyEditTxt.text.toString()
            }*/
            Preferences.addBoolean(PreferenceKeys.IS_EDITED, isChange)

            Util.trace("changes done  : $isChange")
        }catch (t: Throwable){
            Util.trace("errro state change : $t")
            t.printStackTrace()
        }

    }






    private fun releaseConnectivityReceiver()
    {
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver)
            ConnectivityReceiver.connectivityReceiverListener=null
        }
    }

    /**
     * Method triggered by broadcast receiver which is listening for Network state.
     *
     * @param isConnected : Tells whether internet is available or not
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        isInternetConnection=isConnected
        /* if (!isConnected) {
             networkLayout.visibility = View.VISIBLE
         } else {
             networkLayout.visibility = View.GONE
         }*/
       /* if(isConnected)
        {
            submitPostTxt.isEnabled = true
            saveAsDraftTxt.isEnabled = true
        }else
        {
            submitPostTxt.isEnabled = false
            saveAsDraftTxt.isEnabled = false
        }*/
    }
    private fun callReplySubmitPostApi(courseId: String , parentMsgId: String , isDraft: Boolean)
    {
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val params = HashMap<String, Any>()
        params.put(NetworkConstants.COURSE_IDENTIFIER, courseId)
        params.put(NetworkConstants.PARENT_MESSAGE_ID, parentMsgId!!)
        params.put(NetworkConstants.TOKEN, loginBean?.authData?.token!!)
        params.put(NetworkConstants.SUBJECT_TEXT,  threadSubjectEditTxt.text)
        params.put(NetworkConstants.BODY_TEXT, threadBodyEditTxt.text)
        params.put(NetworkConstants.IS_DRAFT, isDraft)

        val qStringParams = HashMap<String, Any>()
        qStringParams.put(NetworkConstants.ACTION, NetworkConstants.POST_REPLY)

        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.REPLY_POST_API + NetworkService.getQueryString(qStringParams),
            params,
            NetworkHandler.METHOD_POST,
            this,
            null
        )

        networkHandler.isPostTypeSubmitting = true
        if(isDraft)
        {
            networkHandler.submitMessage = getString(R.string.saving_draft)
        }
        else {
            networkHandler.submitMessage = getString(R.string.submitting_reply)
        }
        networkHandler.execute()

    }

    private fun callSubmitPostApi(courseId: String , forumId: String , isDraft: Boolean)
    {
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val params = HashMap<String, Any>()
        params.put(NetworkConstants.COURSE_IDENTIFIER, courseId)
        params.put(NetworkConstants.DISCUSSION_FORUM_ID, forumId!!)
        params.put(NetworkConstants.TOKEN, loginBean?.authData?.token!!)
        params.put(NetworkConstants.SUBJECT_TEXT,  threadSubjectEditTxt.text)
        params.put(NetworkConstants.BODY_TEXT, threadBodyEditTxt.text)
        params.put(NetworkConstants.IS_DRAFT, isDraft)

        val qStringParams = HashMap<String, Any>()
        qStringParams.put(NetworkConstants.ACTION, NetworkConstants.POST_NEW_TOPIC)

        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.CREATE_POST_API + NetworkService.getQueryString(qStringParams),
            params,
            NetworkHandler.METHOD_POST,
            this,
            null
        )

        networkHandler.isPostTypeSubmitting = true
        if(isDraft)
        {
            networkHandler.submitMessage = getString(R.string.saving_draft)
        }
        else {
            networkHandler.submitMessage = getString(R.string.submitting_thread)
        }
        networkHandler.execute()

    }

    /**
     * Its network listener methods invoked after network service complete its task.
     *
     *  @param response : response of network service
     *
     *  @see Pair
     *  @see NetworkHandler
     *
     */
    override fun onNetworkResponse(response: android.util.Pair<String, Any>)
    {
        Util.trace("Create Post Repsonse : " + response.second )

        if(response.first == NetworkConstants.SUCCESS)
        {
            handleThreadAndDraftResponse(response)

        }else
        {

            if(response.first == NetworkConstants.NETWORK_INTERNET_ERROR)
            {
                // Show 2 layouts , No Internet and
            }else
            {
                // Show only 1 layout i.e. failure msg
            }

            //DialogUtils.showGenericErrorDialog(this)
            failsLayout.visibility  = View.VISIBLE
            failsLayout.failureMsg.text = failureMsg
            Util.announceAda(failureMsg  + getString(R.string.ada_please_try_again), this@CreatePostActivity)
            /*if(networkFailMsg.contentEquals(getString(R.string.thread_submit_internet_failure)))
            {
                pleaseTryAgainTxt.visibility = View.GONE
            }else {
                pleaseTryAgainTxt.visibility = View.VISIBLE
            }*/
        }
    }

    /** For Draft / New Thread / Reply / Reply Draft Response is same*/
    private fun handleThreadAndDraftResponse(response: android.util.Pair<String, Any>)
    {
        val gson = Gson()
        val threadBean = gson.fromJson<CreateThreadBean>(response.second.toString(), CreateThreadBean::class.java)

        if(threadBean.errorData != null)
        {
            failsLayout.visibility  = View.VISIBLE
            failsLayout.failureMsg.text = failureMsg
            pleaseTryAgainTxt.visibility = View.VISIBLE
        }else
        {
            failsLayout.visibility = View.GONE
           // Util.showSnakeBar(successMsg ,  submitPostTxt, snakeDuration)

            val resultIntent = Intent()
            resultIntent.putExtra(Constants.SNAKEBAR_MESSAGE, successMsg)
            resultIntent.putExtra(Constants.SNAKEBAR_MESSAGE_DURATION, snakeDuration)

            setResult(Activity.RESULT_OK, resultIntent)
            finish()

        }

    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean
    {
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
        return true
    }


   /* private fun storeUnsavedData()
    {
        if( Preferences.getBoolean(PreferenceKeys.KEEP_ME_SIGNED_IN))
        {
            // STORE EVERYTHING
            var createThreadStorage = CreateThreadStorage()

            createThreadStorage.is_reply_screen = isReplyScreen
            createThreadStorage.back_title = "Campus..."
            createThreadStorage.post_title = discussionTitle
            createThreadStorage.courseId = courseId
            createThreadStorage.forumId = forumId

            createThreadStorage.parentMessageId = parentMessageId

            createThreadStorage.subjectLine = threadSubjectEditTxt.text.toString()

            if(intent.getStringExtra(Constants.USER_GIVEN_NAME)!=null)
                createThreadStorage.user_given_name = intent.getStringExtra(Constants.USER_GIVEN_NAME)

            val gson = Gson()
           var createThreadStorageGson = gson.toJson(createThreadStorage)

            Preferences.addValue(PreferenceKeys.CREATE_THREAD_RESTORE , createThreadStorageGson)

            Util.trace("JSON is " + createThreadStorageGson)

        }
    }*/

}
