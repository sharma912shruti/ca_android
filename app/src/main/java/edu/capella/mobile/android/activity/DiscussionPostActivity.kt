package edu.capella.mobile.android.activity

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.bean.DiscussionPostBean
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import edu.capella.mobile.android.network.NetworkService
import edu.capella.mobile.android.task.StickyInfoGrabber
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.toolbar_generic.*
import kotlinx.android.synthetic.main.toolbar_generic.view.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class DiscussionPostActivity : MenuActivity() /*BaseActivity()*/ ,  ConnectivityReceiver.ConnectivityReceiverListener,
    NetworkListener {

    var courseId = ""
    var topicId  = ""
    var postingTitle = ""
    var courseIdNumber = ""
    var postedEmployeeRole = ""

    var messageIdArrayArray  = ArrayList<String>()

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
        //setContentView(R.layout.activity_post_detail)
        setContentChildView(R.layout.activity_post_detail, true)

        discussionPostToolbar.genericTitleTxt.text= getString(R.string.posts)

        if(intent.getStringExtra(Constants.BACK_TITLE) != null )
        {
            discussionPostToolbar.backHeaderTxt.text=intent.getStringExtra(Constants.BACK_TITLE).toString()
            discussionPostToolbar.backButtonLayout.contentDescription =   getString(R.string.ada_back_button) +intent.getStringExtra(Constants.BACK_TITLE).toString()
        }
        postingTitle =   intent.getStringExtra(Constants.POSTS_TITLE)




        postedEmployeeRole=   intent.getStringExtra(Constants.POSTED_EMPLOYEE_ROLE)

        backButtonImg.setOnClickListener { finish() }
        initUi()


        viewDescriptionTxt.setOnClickListener{
            openViewDescriptionLink()

        }

        OmnitureTrack.trackState("course:discussions:post")
    }

    private fun openFormatedView(topicWithReply: DiscussionPostBean.NewDiscussionData.TopicWithReplies)
    {


        val intent = Intent(this, FormatedWebViewActivity::class.java)
        intent.putExtra(Constants.FORMATTED_BODY , topicWithReply.bodyFormattedText)
        intent.putExtra(Constants.FILE_NAME , getFileName(topicWithReply.attachmentLink.toString()))
        intent.putExtra(Constants.FILE_SIZE , topicWithReply.attachmentFileSize.toString())
        intent.putExtra(Constants.FILE_LINK , topicWithReply.attachmentLink.toString())
        intent.putExtra(Constants.FORUM_ID , topicWithReply.forumId)
        intent.putExtra(Constants.PARENT_MESSAGE_ID , topicWithReply.id)


        startActivity(intent)
      //  overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
        overridePendingTransition( R.anim.slide_in_up, R.anim.no_anim)

        OmnitureTrack.trackState("course:discussions:post:HTML")
    }

    private fun openFormatedViewForReply(item: DiscussionPostBean.NewDiscussionData.TopicWithReplies.Responses.DiscussionMessage)
    {

        val intent = Intent(this, FormatedWebViewActivity::class.java)
        intent.putExtra(Constants.FORMATTED_BODY , item.bodyFormattedText)
        intent.putExtra(Constants.FILE_NAME , getFileName(item.attachmentLink.toString()))
        intent.putExtra(Constants.FILE_SIZE , item.attachmentFileSize.toString())
        intent.putExtra(Constants.FILE_LINK , item.attachmentLink.toString())
        intent.putExtra(Constants.FORUM_ID , item.forumId)
        intent.putExtra(Constants.PARENT_MESSAGE_ID , item.id)
        startActivity(intent)
        //  overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
        overridePendingTransition( R.anim.slide_in_up, R.anim.no_anim)
    }


    private fun openViewDescriptionLink()
    {

        val url = Preferences.getValue(PreferenceKeys.VIEW_DESCRIPTION_LINK_PATH)

            val intent = Intent(this, UnitWebViewActivity::class.java)

            intent.putExtra(Constants.URL_FOR_IN_APP, url)
            intent.putExtra(Constants.ORANGE_TITLE, postingTitle)
            intent.putExtra(Constants.IN_APP_TITLE, "")
            startActivity(intent)
           // overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            // overridePendingTransition( R.anim.slide_in_up, R.anim.no_anim)

    }


    /**
     * Method initializes tabs with container data and Look and fill.
     *
     */
    private fun initUi()
    {


        courseId = intent.getStringExtra(Constants.COURSE_ID)
        topicId = intent.getStringExtra(Constants.TOPIC_ID)

        postNameTxt.text = postingTitle

        postDetailListSwipeToRefresh.setColorSchemeColors( getColor(R.color.checkBoxColor))


        postDetailListSwipeToRefresh.setOnRefreshListener {
            callPostListApi(courseId , topicId!!)
            postDetailListSwipeToRefresh.isRefreshing = false

        }


    }



    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun initNetworkBroadcastReceiver()
    {

        ConnectivityReceiver.connectivityReceiverListener = this@DiscussionPostActivity
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
        callPostListApi(courseId , topicId!!)
    }

    /**
     * Factory method of activity, executes when activity goes in paused state.
     *
     */
    override fun onPause() {
        super.onPause()
        releaseConnectivityReceiver()
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

      if(isConnected == true && isNetworkFailedDueToConnectivity!=null && isNetworkFailedDueToConnectivity==true)
        {
            isNetworkFailedDueToConnectivity = null
            if(this.getCurrentNetworkQueueSize()==0) {
                DialogUtils.releaseGenericDialog()
                callPostListApi(courseId , topicId!!)
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
     * Method call LearnerProfile api to show details in PROFILE and IDCARD tabs.
     *
     */
    private fun callPostListApi(courseId: String , forumId: String)
    {
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val params = HashMap<String, Any>()
        params.put(NetworkConstants.TOKEN, loginBean?.authData?.token!!)

        val qStringParams = HashMap<String, Any>()
        qStringParams.put(NetworkConstants.COURSE_IDENTIFIER, courseId)
        qStringParams.put(NetworkConstants.DISCUSSION_POST_TOPIC_ID, topicId!!)
        qStringParams.put(NetworkConstants.ACTION, "getallposts")
        qStringParams.put(NetworkConstants.FORMAT_TYPE, "inline")

        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.COURSE_DISCUSSION_POST_API + NetworkService.getQueryString(qStringParams),
            params,
            NetworkHandler.METHOD_POST,
            this,
            null
        )
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
        Util.trace("DiscussionPostBean : " + response.second )
        if(response.first == NetworkConstants.SUCCESS)
        {
            val gson = Gson()
            var discussionPostBean = gson.fromJson<DiscussionPostBean>(response.second.toString(), DiscussionPostBean::class.java)
            updateDiscussionPost(discussionPostBean)
        }else
        {
            DialogUtils.showGenericErrorDialog(this)
        }
    }


    private fun updateDiscussionPost(discussionPostBeanData: DiscussionPostBean )
    {
        try {

            val discussionPostBean = discussionPostBeanData

            if (discussionPostBean?.errorData != null) {

                DialogUtils.showGenericErrorDialog(this)
                return
            }

            setTopScreenData(discussionPostBean)
            setReplyData(discussionPostBean)
            callUpdateReadStatusAPI()

        }catch (t: Throwable)
        {
            Util.trace("Discussion Post data error : $t")
            t.printStackTrace()
        }

    }

    private fun setTopScreenData(discussionPostBeanData: DiscussionPostBean )
    {
        messageIdArrayArray.clear()

        postMainContainer.visibility = View.VISIBLE
        val discussionPostBean = discussionPostBeanData

        val topicWithReply =   discussionPostBean.newDiscussionData?.topicWithReplies
        postTitle.text =topicWithReply?.subject
        postDateTxt.text =  Util.getDate( topicWithReply?.editDate!! , Constants.DATE_FORMAT)
        postedNameTxt.text = topicWithReply?.postedName

       // postBodyTextTxt.text = Html.fromHtml( Util.str(topicWithReply?.bodyFormattedText.toString() ), Html.FROM_HTML_MODE_LEGACY).toString().trim()
      //  postBodyTextTxt.text = Html.fromHtml( Util.str(topicWithReply?.bodyText.toString() ), Html.FROM_HTML_MODE_LEGACY ).toString().trim()
      //  postBodyTextTxt.text = Html.fromHtml( Util.getNonParaBodyText(topicWithReply?.bodyText.toString()), Html.FROM_HTML_MODE_LEGACY).toString().trim()

        var actualText: String? =  ""

        if(topicWithReply.bodyFormattedText != null  && Util.isHTMLFoundForFormattedView(topicWithReply.bodyFormattedText))
        {
            actualText =Html.fromHtml( topicWithReply?.bodyFormattedText  , Html.FROM_HTML_MODE_LEGACY).toString().trim()
        }else if(topicWithReply?.bodyText !=null)
        {
            actualText =Html.fromHtml( topicWithReply?.bodyText  , Html.FROM_HTML_MODE_LEGACY).toString().trim()
        }
        postBodyTextTxt.text =  actualText
       // postBodyTextTxt.text =  Util.removeCorruptString(topicWithReply?.bodyText)


        if(topicWithReply.isUnread !=null && topicWithReply.isUnread== false)
        {
            postTitle.typeface = null
            postDateTxt.typeface = null
            postedNameTxt.typeface = null
            postBodyTextTxt.typeface = null
        }


        if(getRole(postedEmployeeRole).isNotEmpty()) {
            postedEmployeeRoleTxt.text = getRole(postedEmployeeRole.toString())
            postedEmployeeRoleLayout.visibility  = View.VISIBLE
        }else
        {
            postedEmployeeRoleLayout.visibility  = View.GONE
        }


        messageIdArrayArray.add(topicWithReply.id!!)

        if(topicWithReply.attachmentLink != null)
        {
            attachmentLayout.visibility = View.VISIBLE
           // postAttachmentNameTxt.visibility = View.VISIBLE
           // postAttachmentNameTxt.text = getFileName(topicWithReply.attachmentLink.toString())
            postAttachmentLinkTxt.text = getFileName(topicWithReply.attachmentLink.toString())
            postAttachmentSizeTxt.text = topicWithReply.attachmentFileSize.toString()

           // attachmentLayout.contentDescription = getFileName(topicWithReply.attachmentLink.toString()) + getString(R.string.ada_link_will_open_browser)
            attachmentLayout.contentDescription = getFileName(topicWithReply.attachmentLink.toString()) + topicWithReply.attachmentFileSize.toString() + getString(R.string.ada_link_will_open_browser)


            attachmentLayout.tag = topicWithReply.attachmentLink
            attachmentLayout.setOnClickListener{
                val linkToOpen = it.tag as String
                openAttachmentLink(linkToOpen , topicWithReply.forumId!! , topicWithReply.id!!)
            }

        }else
        {
            attachmentLayout.visibility = View.GONE
            //postAttachmentNameTxt.visibility = View.GONE
        }

        replyPostBtnTxt.text =   getString(R.string.reply_to ).toUpperCase() +   topicWithReply.userGivenName.toString()
        replyPostBtnTxt.contentDescription = getString(R.string.reply_to ).toUpperCase() +   topicWithReply.userGivenName.toString() +", "+ getString(R.string.ada_button)

         var customTag = CustomTag()
        customTag.parentId = topicWithReply?.id
        customTag.userGivenName = topicWithReply?.userGivenName
        customTag.subject = "RE: "+topicWithReply?.subject
        customTag.courseidentifier = courseId


        replyPostBtnTxt.tag = customTag
        replyPostBtnTxt.setOnClickListener{
           replyToPost(replyPostBtnTxt)
       }

        if(topicWithReply.bodyFormattedText != null  && Util.isHTMLFoundForFormattedView(topicWithReply.bodyFormattedText))
        {
            view_formatted_post.visibility = View.VISIBLE
        }else
        {
            view_formatted_post.visibility = View.GONE
        }

        view_formatted_post.setOnClickListener{
            openFormatedView(topicWithReply)
        }


    }

    private fun openAttachmentLink(linkToOpen: String , forum_id:  String, message_id: String) {

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

            val stickyWork = StickyInfoGrabber(this)

            DialogUtils.screenNamePrefix = "course:discussions:post:attachment-linkout-to-post"
            val customMsg = getString(R.string.leave_app_for_attachment_message)
            stickyWork.generateMuleSoftStickySessionForTargetUrl(
                discussionBoardCourseRoomLink,
                BuildConfig.STICKY_FORWARD_URL,
                customMsg
            )


        }catch (t:Throwable)
        {
            Util.trace("Attachment opening issue $t")
            t.printStackTrace()
        }

    }

    private fun getFileName(path:String) : String
    {
        return path.substring(path.lastIndexOf("/",path.length , true) +1, path.length)
    }

    private fun setReplyData(discussionPostBeanData: DiscussionPostBean )
    {
        val discussionPostBean = discussionPostBeanData

        val responses = discussionPostBean.newDiscussionData?.topicWithReplies?.responses
        var givenName = discussionPostBean.newDiscussionData?.topicWithReplies?.userGivenName


          replyListContainer.removeAllViews()
        repliesTitleHeadTxt.visibility = View.GONE
        if(responses!=null)
        {
            /*if(responses!!.discussionMessage!=null && responses!!.discussionMessage!!.size>0)
            {
                repliesTitleHeadTxt.visibility = View.VISIBLE
            }else
            {
                repliesTitleHeadTxt.visibility = View.GONE
            }*/

            gereateTree(responses!!, replyListContainer, givenName , 1)
        }


    }

   fun gereateTree(responses: DiscussionPostBean.NewDiscussionData.TopicWithReplies.Responses , subLayoutContainer : LinearLayout , responseNameTopper:String?, level: Int)
   {


       var sortedList = responses.discussionMessage!!
        orderPost(sortedList)

       //for((index,item)  in responses.discussionMessage!!.withIndex())
       for((index,item)  in sortedList!!.withIndex())
            {
                Util.trace("Level : " + level)

                repliesTitleHeadTxt.visibility = View.VISIBLE

                messageIdArrayArray.add(item?.id!!)

                val inflater = LayoutInflater.from(this)
                val replyView = inflater.inflate(R.layout.row_reply_post, null)

                if(level==1 && index!=0)
                {

                    try {
                        val postMainContainer = replyView.findViewById<LinearLayout>(R.id.postMainContainer)

                        var param: LinearLayout.LayoutParams  = postMainContainer.layoutParams as LinearLayout.LayoutParams

                        param.topMargin = resources.getDimension(R.dimen._10dp).toInt()
                    }catch (t:Throwable){
                        t.printStackTrace()
                    }
                }

                val replyTitle = replyView.findViewById<TextView>(R.id.replyTitle)
                val replyPostDateTxt= replyView.findViewById<TextView>(R.id.replyPostDateTxt)
                val replyPostedNameTxt= replyView.findViewById<TextView>(R.id.replyPostedNameTxt)
                val replyResponseNameTxt= replyView.findViewById<TextView>(R.id.replyResponseNameTxt)
                val replyTextTxt= replyView.findViewById<TextView>(R.id.replyTextTxt)
                val replyAttachmentLayout= replyView.findViewById<RelativeLayout>(R.id.replyAttachmentLayout)
                val replyAttachmentLinkTxt= replyView.findViewById<TextView>(R.id.replyAttachmentLinkTxt)
                val replyAttachmentSizeTxt= replyView.findViewById<TextView>(R.id.replyAttachmentSizeTxt)
                val replyRowPostBtnTxt= replyView.findViewById<TextView>(R.id.replyRowPostBtnTxt)
                val replySubListContainer= replyView.findViewById<LinearLayout>(R.id.replySubListContainer)
                val reply_view_formatted_post = replyView.findViewById<TextView>(R.id.reply_view_formatted_post)

                replyTitle.text = Util.str(item?.subject)
                replyPostDateTxt.text = Util.getDate(item!!.postDate!! , Constants.DATE_FORMAT)
                replyPostedNameTxt.text = item.postedName.toString()

                replyAttachmentLayout.tag = item.attachmentLink

                replyAttachmentLayout.setOnClickListener{
                   val linkToOpen = it.tag as String
                    openAttachmentLink(linkToOpen , item.forumId!! , item.id!!)
                }

                var topperName = responseNameTopper
                if(responseNameTopper!=null)
                {
                    topperName = responseNameTopper
                }
                else {
                    topperName =  item.userGivenName
                }

                replyResponseNameTxt.text = getString(R.string.response_to) +topperName
                val font = Typeface.createFromAsset(assets, "fonts/roboto_medium_italic.ttf")
                replyResponseNameTxt.typeface = font


                replyRowPostBtnTxt.text =  getString(R.string.reply_to) + item.userGivenName

                //replyTextTxt.text =    Html.fromHtml( Util.str(item.bodyFormattedText) , Html.FROM_HTML_MODE_LEGACY).toString().trim()

                var actualText: String? =  ""

                if(item.bodyFormattedText != null  && Util.isHTMLTagFound(item.bodyFormattedText))
                {
                    actualText =Html.fromHtml( item?.bodyFormattedText  , Html.FROM_HTML_MODE_LEGACY).toString().trim()
                }else if(item?.bodyText !=null)
                {
                    actualText =Html.fromHtml( item?.bodyText  , Html.FROM_HTML_MODE_LEGACY).toString().trim()
                }
                replyTextTxt.text =  actualText

                if(item.isUnread!! == false)
                {
                    replyTitle.typeface = null
                    replyPostDateTxt.typeface = null
                    replyPostedNameTxt.typeface = null
                    replyTextTxt.typeface = null
                    /*replyTitle.setTypeface(replyTitle.typeface , Typeface.BOLD)
                    replyPostDateTxt.setTypeface(replyTitle.typeface , Typeface.BOLD)
                    replyPostedNameTxt.setTypeface(replyTitle.typeface , Typeface.BOLD)
                    replyTextTxt.setTypeface(replyTitle.typeface , Typeface.BOLD)*/
                }

                if(item.attachmentLink != null)
                {
                    replyAttachmentLayout.visibility = View.VISIBLE
                    replyAttachmentLinkTxt.text = getFileName(item.attachmentLink.toString())
                    replyAttachmentSizeTxt.text = item.attachmentFileSize.toString()

                    replyAttachmentLayout.contentDescription = getFileName(item.attachmentLink.toString())+  item.attachmentFileSize.toString()+ getString(R.string.ada_link_will_open_browser)

                }else
                {
                    replyAttachmentLayout.visibility = View.GONE
                }

                if(item.bodyFormattedText != null  && Util.isHTMLFoundForFormattedView(item.bodyFormattedText))
                {
                    reply_view_formatted_post.visibility = View.VISIBLE
                }else
                {
                    reply_view_formatted_post.visibility = View.GONE
                }

                reply_view_formatted_post.setOnClickListener{
                    openFormatedViewForReply(item)
                }

                var customTag = CustomTag()
                customTag.parentId = item?.id
                customTag.userGivenName = item?.userGivenName
                customTag.subject = item?.subject
                customTag.courseidentifier = courseId


                replyRowPostBtnTxt.tag = customTag
                replyRowPostBtnTxt.setOnClickListener{
                    replyToPost(replyRowPostBtnTxt)
                }


                subLayoutContainer.addView(replyView)

                if(level<10)
                {
                    var params: LinearLayout.LayoutParams = replySubListContainer.layoutParams as LinearLayout.LayoutParams
                    params?.leftMargin = resources.getDimension(R.dimen._10dp).toInt()
                    replySubListContainer.layoutParams = params
                }

                if(item.responses != null)
                {
                    gereateTree(item.responses!!, replySubListContainer , item.userGivenName , level+1)
                }
            }
   }

    fun replyToPost(replyTxtView : View )
    {


        val tag = replyTxtView.tag as CustomTag



        var threadIntent = Intent(this, CreatePostActivity::class.java)
        threadIntent.putExtra(Constants.IS_REPLY_SCREEN , true)
        threadIntent.putExtra(Constants.COURSE_ID , tag.courseidentifier )
        threadIntent.putExtra(Constants.POSTS_TITLE ,  postingTitle)
        threadIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        threadIntent.putExtra(Constants.PARENT_MESSAGE_ID ,  tag.parentId)
        threadIntent.putExtra(Constants.SUBJECT ,  tag.subject)
        threadIntent.putExtra(Constants.USER_GIVEN_NAME ,  tag.userGivenName)

        threadIntent.putExtra(Constants.BACK_TITLE , getString(R.string.cancel))

        startActivityForResult(threadIntent, Constants.ACTIVITY_REQUEST_CODE)
        OmnitureTrack.trackAction("course:discussions:post:reply")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK)
        {
                if (requestCode == Constants.ACTIVITY_REQUEST_CODE)
                {
                    val snakeBarMessage = data!!.getStringExtra(Constants.SNAKEBAR_MESSAGE)
                    val snakeBarMessageDuration = data!!.getIntExtra(Constants.SNAKEBAR_MESSAGE_DURATION,3000)
//                    Util.showSnakeBar(snakeBarMessage, parentPostDetailContainer , snakeBarMessageDuration)
                    Util.showCustomSnakeBar(this@DiscussionPostActivity, snakeBarMessage, parentPostDetailContainer , snakeBarMessageDuration)
                }

        }

    }

   /* fun replyToTopPost(replyTxtView : View )
    {
        OmnitureTrack.trackAction("course:discussions:post:reply:view-description")
        val tag = replyTxtView.tag as CustomTag


        Util.trace("Detail is : " + tag.toString())

        var threadIntent = Intent(this, CreatePostActivity::class.java)
        threadIntent.putExtra(Constants.IS_REPLY_SCREEN , true)
        threadIntent.putExtra(Constants.COURSE_ID , tag.courseidentifier )
        threadIntent.putExtra(Constants.POSTS_TITLE ,  postingTitle)

        threadIntent.putExtra(Constants.PARENT_MESSAGE_ID ,  tag.parentId)
        threadIntent.putExtra(Constants.SUBJECT ,  tag.subject)
        threadIntent.putExtra(Constants.USER_GIVEN_NAME ,  tag.userGivenName)

        threadIntent.putExtra(Constants.BACK_TITLE , getString(R.string.cancel))

        startActivity(threadIntent)
    }*/

    inner class  CustomTag (

        var parentId: String? = null,
        var userGivenName : String? = null,
        var subject : String? = null,
        var courseidentifier: String? = null
        ){
        override fun toString(): String {
            return "CustomTag(parentId=$parentId, userGivenName=$userGivenName, subject=$subject, courseidentifier=$courseidentifier)"
        }
    }


    private fun callUpdateReadStatusAPI()
    {
       // OmnitureTrack.trackAction("DiscussionPost:mark-post-as-read")
        Util.trace("Message Array : $messageIdArrayArray")
        if(messageIdArrayArray.size > 0)
        {
            val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
            val gson = Gson()
            var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

            val params = HashMap<String, Any>()
            params.put(NetworkConstants.TOKEN, loginBean?.authData?.token!!)

            val qStringParams = HashMap<String, Any>()
            qStringParams.put(NetworkConstants.COURSE_IDENTIFIER, courseId)
            qStringParams.put(NetworkConstants.ACTION, "setmessagereadstatus")
            qStringParams.put(NetworkConstants.IS_READ, true)

            var msgArrayQuesryString =""
            for(msgId in messageIdArrayArray)
            {
                msgArrayQuesryString += "&"+NetworkConstants.MESSAGE_ID+"="+msgId
            }

            val networkHandler = NetworkHandler(
                this,
                NetworkConstants.UPDATE_READ_STATUS_API + NetworkService.getQueryString(qStringParams) + msgArrayQuesryString,
                params,
                NetworkHandler.METHOD_POST,
                UnreadMarkStatusListener,
                null
            )
            networkHandler.isSilentCall = true
            networkHandler.execute()
        }
    }

    private val UnreadMarkStatusListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {
            Util.trace("Read Status first :  " + response.first)
            Util.trace("Read Status second :  " + response.second)
        }
    }

    private fun getRole(role: String?):String
    {
        if(role!!.contains("Primary" , true))
        {
            return getString(R.string.primary_instructor)
        }
        else if(role!!.contains("Secondary" , true))
        {
            return getString(R.string.secondary_instructor)
        }
        else if(role!!.contains("Writing" , true) || role!!.contains("Coach" , true))
        {
            return getString(R.string.writing_coach)
        }

        return ""
    }

    private fun orderPost(list: List<DiscussionPostBean.NewDiscussionData.TopicWithReplies.Responses.DiscussionMessage?>)
    {

        Collections.sort(list,object :Comparator<DiscussionPostBean.NewDiscussionData.TopicWithReplies.Responses.DiscussionMessage?>{

            override fun compare(firstItem: DiscussionPostBean.NewDiscussionData.TopicWithReplies.Responses.DiscussionMessage?,
                                 secondItem: DiscussionPostBean.NewDiscussionData.TopicWithReplies.Responses.DiscussionMessage?): Int {

                var x1: Long =  1 //Long.MAX_VALUE
                var x2: Long =  1 //Long.MAX_VALUE



                var date1 = firstItem!!.postDate
                var date2 =  secondItem!!.postDate

                if(date1==null)
                    x1 = -1
                else
                    x1 =date1

                if(date2 == null)
                    x2 = -1
                else
                    x2 = date2


                //val sComp =  x2.compareTo(x1)
                val sComp =  x1.compareTo(x2)
                Util.trace("sComp $sComp")
                return sComp

            }

        })
    }

}
