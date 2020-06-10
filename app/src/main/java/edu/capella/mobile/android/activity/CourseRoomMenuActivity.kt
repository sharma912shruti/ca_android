package edu.capella.mobile.android.activity

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Pair
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnScrollChangedListener
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.adapters.WeekAdapter
import edu.capella.mobile.android.bean.*
import edu.capella.mobile.android.interfaces.GradeBlueDotListener
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.layout_manager.NonScrollableLayoutManager
import edu.capella.mobile.android.network.*
import edu.capella.mobile.android.task.FacultyGradeBlueDotHandler
import edu.capella.mobile.android.task.StickyInfoGrabber
import edu.capella.mobile.android.task.StickyInfoToGetMuleSoftToken
import edu.capella.mobile.android.task.StudentGradeBlueDotHandler
import edu.capella.mobile.android.utils.Util.isAdaEnabled
import kotlinx.android.synthetic.main.activity_course_room_menu.*
import kotlinx.android.synthetic.main.activity_course_room_menu.toolbar
import kotlinx.android.synthetic.main.row_item_course_room_menu.view.*
import kotlinx.android.synthetic.main.toolbar_course_menu.*
import kotlinx.android.synthetic.main.toolbar_course_menu.view.headerTxt
import kotlinx.android.synthetic.main.toolbar_course_menu.view.syllabusTxt
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


/**
 * Class CourseRoomMenuActivity.kt : class description goes here
 *
 * @author  :  SSHARMA45
 * @version :  1.0
 * @since   :  3/9/2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
@Suppress("DEPRECATION")
class CourseRoomMenuActivity : MenuActivity(),
    ConnectivityReceiver.ConnectivityReceiverListener  {

    private var courseData: CourseListBean.NewCourseroomData.CurrentCourseEnrollment? = null
    private var courseDetailBean: CourseDetailBean? = null
    private lateinit var picLoader: PictureLoader
    var assessmentFacultyList: ArrayList<AssesstmentFacultyBean.CourseAssignment> = ArrayList()
    var assessmentLearnerList: ArrayList<AssessmentLearnerBean.FlexpathAssessmentsAndStatus>? =
        ArrayList()
    private var checkFpFlex: String = ""
    private var userEmployeeId: String = ""
    //    private var courseBaicInfoBean:CourseBasicInfoBean? = null
    private var connectivityReceiver: ConnectivityReceiver? = null
    private var isFpFlexPath: Boolean = false
    private var weekAdapter: WeekAdapter? = null
    private var headerAdapter: WeekAdapter? = null
    private var leftNavigationItem: GP2Bean? = null
    private var gp2ItemList: ArrayList<String>? = null
    private var isGp2: Boolean = false
    private var GP2_BROWSER_RESULT = 90

    private var  previousBlueDotState:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_course_room_menu)
        setContentChildView(R.layout.activity_course_room_menu , true)
        initialiseValues()
        initialListenerSetUp()
        handleScrollView()
        OmnitureTrack.trackState("course-menu:home")
    }

    private fun initialiseValues() {

        try
        {
                    courseData =
                        intent.getSerializableExtra(Constants.COURSE_DATA) as CourseListBean.NewCourseroomData.CurrentCourseEnrollment
                    val courseID = courseData!!.courseIdentifier!!
                    val courseType = intent.getStringExtra(Constants.COURSE_TYPE)
                    if (courseType == Constants.CURRENT) {
                        toolbar.headerTxt.text = getString(R.string.current)
                    } else {
                        toolbar.headerTxt.text = getString(R.string.previous)
                    }

                    this.picLoader = PictureLoader(this)
                    backButton.contentDescription = getString(R.string.ada_back_button) + toolbar.headerTxt.text.toString()
                    backButton.setOnClickListener {
                        finish()
                    }
            //        toolbar.headerTxt.setOnClickListener {
            //            finish()
            //        }

                    toolbar.syllabusTxt.text = getString(R.string.course_menu)
                    OmnitureTrack.trackAction("course-menu:home")
                    checkCoursePath()
        }catch (t:Throwable)
        {
            Util.trace("Course error $t")
            t.printStackTrace()
        }
    }

    /**
     *  Method responsible to refresh Announcement API
     */
    private fun initialListenerSetUp() {
        OmnitureTrack.trackAction("course-menu:refresh")
        val swipeRefreshColor = ContextCompat.getColor(this, R.color.checkBoxColor)
        this.courseMenuRefresh.setColorSchemeColors(swipeRefreshColor)
        courseMenuRefresh.setOnRefreshListener {
            courseMenuRefresh.isRefreshing = false
            callApiToGetCourseDetail()

            OmnitureTrack.trackState("course-menu:refresh")
        }


    }

    private fun handleScrollView()
    {
        val listener: OnScrollChangedListener

        listener = OnScrollChangedListener {
            val scrollY: Int = courseRoomScrollView.getScrollY()
            if (scrollY < 50)
            { //threshold
                courseMenuRefresh.setEnabled( true)
            } else {
                courseMenuRefresh.setEnabled(false)

            }
        }

        courseRoomScrollView.getViewTreeObserver().addOnScrollChangedListener(listener)
    }

    private fun checkCoursePath() {
        if (courseData?.flexpath2!! && courseData?.flexpathCourse!!) {
            // TODO set UI for flex path 2.0
            checkFpFlex = Constants.FP2

        } else if (!courseData?.flexpath2!! && courseData?.flexpathCourse!!) {
            // TODO set UI for flex path 1.0
            checkFpFlex = Constants.FP1

        } else if (!courseData?.flexpath2!! && !courseData?.flexpathCourse!!) {
            // TODO set Ui for guided path 1.0 and 2.0 (need to find out how to detect GP1.0 and GP2.0)
            checkFpFlex = Constants.GP1
        } else if (courseData?.westworld != null) {
            checkFpFlex = Constants.GP2
        }
    }

    private fun setScreenView() {


        if (courseData?.flexpath2!! && courseData?.flexpathCourse!!) {
            // this is for flex path 2.0
            header_message_layout.background =
                ContextCompat.getDrawable(this, R.drawable.fp_blue_gradient)
        } else {
            // this is for flex path 1.0 or guided paths
            header_message_layout.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.headerLineColor
                )
            )
        }

        if (courseData?.flexpath2!! && courseData?.flexpathCourse!!) {
            // TODO set UI for flex path 2.0
            isFpFlexPath = true
            setUiForFlexPath()
            attachClickListenerForFlexPath()
        } else if (!courseData?.flexpath2!! && courseData?.flexpathCourse!!) {
            // TODO set UI for flex path 1.0
            isFpFlexPath = true
            setUiForFlexPath()
            attachClickListenerForFlexPath()
        } else if (!courseData?.flexpath2!! && !courseData?.flexpathCourse!!) {
            // TODO set Ui for guided path 1.0 and 2.0 (need to find out how to detect GP1.0 and GP2.0)
            isFpFlexPath = false
            if (shouldCallLeftNavigation()) {
                checkAndCallGp2APi()
            } else {
                leftNavigationItem?.let { setGpUi(it, false) }
            }

        }

        setAlertValue()
    }

    private fun setUiForFlexPath() {

        courseRoomScrollView.visibility = View.VISIBLE
        mainLayout.visibility = View.VISIBLE
        // icon setting

//        picLoader.displayImage(
//            courseData?.courseSection?.instructor?.profileImage!!,
//            profileLayout.course_item_icon
//        )
//        profileLayout.course_item_icon.visibility = View.VISIBLE

        if(courseData?.courseSection?.course?.subject.equals(Constants.UOS_COURSE)){
            profileLayout.visibility = View.GONE
        }else {
            profileLayout.visibility = View.VISIBLE
            profileLayout.course_item_icon.visibility = View.VISIBLE
            profileLayout.header.text = courseData?.courseSection?.instructor?.fullName
            profileLayout.description.text = getString(R.string.instructor)
            profileLayout.description.visibility = View.VISIBLE
            // icon setting
            profileLayout.course_item_icon.visibility = View.VISIBLE
            courseData?.courseSection?.instructor?.profileImage?.let {
                picLoader.displayImage(
                    it,
                    profileLayout.course_item_icon
                )
            }
        }

        gettingStartedLayout.course_item_icon.visibility = View.VISIBLE
        gettingStartedLayout.course_item_icon.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_donut_chart
            )
        )

        syllabusLayout.course_item_icon.visibility = View.VISIBLE
        syllabusLayout.bottomLine.visibility = View.INVISIBLE
        syllabusLayout.course_item_icon.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_format_list_bulleted
            )
        )



        path_header.text = courseData?.courseSection?.course?.title
        path_description.text = courseData?.courseSection?.section
        // header setting
//        profileLayout.header.text = courseData?.courseSection?.instructor?.fullName
//        profileLayout.description.visibility = View.VISIBLE
//        profileLayout.description.text = getString(R.string.instructor)


        gettingStartedLayout.header.text = getString(R.string.competency_map)
        syllabusLayout.header.text = getString(R.string.syllabus)

        messageLayout.header.text = getString(R.string.messages) //message
        messageLayout.header.minLines = 1

        announcementLayout.header.text = getString(R.string.announcements) // announcement

        gradeAndStatusLayout.header.text =
            getString(R.string.assessment_and_status) // assessment and grades
        assignmentsLayout.header.text = getString(R.string.discussion) // discussion
        discussionLayout.header.text = getString(R.string.discussion_drafts) // discussion drafts

        if (isStudent()) {
            discussionDraftLayout.visibility = View.VISIBLE
        } else {
            discussionDraftLayout.visibility = View.GONE
        }

        discussionDraftLayout.header.text = getString(R.string.set_target_dates) // set target dates
        studiesLayout.header.text = getString(R.string.complete_this_course) //complete this course
        studiesLayout.contentDescription=getString(R.string.complete_this_course)+ getString(R.string.link_will_open)
        classmatesLayout.header.text = getString(R.string.classmates) // classmates
        viewOnFullSiteLayout.header.text = getString(
            R.string.view_course_on_fill_site,
            courseData?.courseSection?.course?.number
        ) //view course on full site
        viewOnFullSiteLayout.contentDescription = getString(
            R.string.view_course_on_fill_site,
            courseData?.courseSection?.course?.number
        ) + getString(R.string.link_will_open)
        setBlueDotForFP()

    }

    fun setBlueDotForFP() {

        try{
        messageLayout.description.visibility = View.VISIBLE
        if (courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount != null && courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount?.toInt()!! > 0) {

            messageLayout.course_item_icon.visibility = View.GONE
            messageLayout.description.visibility = View.VISIBLE


            messageLayout.dotContainer.visibility = View.GONE
            messageLayout.header.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_blue_dot,
                0,
                0,
                0
            )
            messageLayout.header.compoundDrawablePadding =
                resources.getDimension(R.dimen._10dp).toInt()
            messageLayout.description.setPadding(
                resources.getDimension(R.dimen._20dp).toInt(),
                0,
                0,
                0
            )

            if (courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount?.toInt() == 1) {
                messageLayout.description.text = resources.getString(
                    R.string.you_have_unread_message_count,
                    courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount
                )
                messageLayout.contentDescription = getString(R.string.messages)+", "+resources.getString(
                    R.string.you_have_unread_message_count,
                    courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount
                )
            } else {
                messageLayout.description.text = resources.getString(
                    R.string.you_have_unread_messages_count,
                    courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount
                )
                messageLayout.contentDescription = getString(R.string.messages)+", "+resources.getString(
                    R.string.you_have_unread_messages_count,
                    courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount
                )
            }
        } else {
            messageLayout.course_item_icon.visibility = View.INVISIBLE

            messageLayout.header.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            messageLayout.header.compoundDrawablePadding = 0
            messageLayout.description.setPadding(0, 0, 0, 0)
            messageLayout.dotContainer.visibility = View.VISIBLE
            messageLayout.contentDescription =  getString(R.string.messages)+", "+resources.getString(
                R.string.you_have_unread_messages_count,
                resources.getString(R.string.no)
            )
            messageLayout.description.text =
                resources.getString(
                    R.string.you_have_unread_messages_count,
                    resources.getString(R.string.no)
                )
        }

        // for announcements blue dot
//        if (courseDetailBean?.newCourseroomData?.courseDetails?.courseAnnouncements?.size ?: 0 > 0) {
        if (shouldShowBlueDotOnAnnouncement()) {
            announcementLayout.course_item_icon.visibility = View.GONE

            announcementLayout.dotContainer.visibility = View.GONE
            announcementLayout.header.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_blue_dot,
                0,
                0,
                0
            )
            announcementLayout.contentDescription = getString(R.string.ada_announcement_click_message)
            announcementLayout.header.compoundDrawablePadding =
                resources.getDimension(R.dimen._10dp).toInt()
            announcementLayout.description.setPadding(
                resources.getDimension(R.dimen._20dp).toInt(),
                0,
                0,
                0
            )
        } else {
            announcementLayout.header.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            announcementLayout.header.compoundDrawablePadding = 0
            announcementLayout.contentDescription = getString(R.string.announcements)
            announcementLayout.description.setPadding(0, 0, 0, 0)
            announcementLayout.dotContainer.visibility = View.VISIBLE
            announcementLayout.course_item_icon.visibility = View.INVISIBLE
        }

        //for assessments and status
        if (courseDetailBean?.newCourseroomData?.courseDetails?.pccpAssignment?.pccpAssignments != null) {

            gradeAndStatusLayout.course_item_icon.visibility = View.INVISIBLE
            gradeAndStatusLayout.header.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            gradeAndStatusLayout.header.compoundDrawablePadding = 0
            gradeAndStatusLayout.description.setPadding(0, 0, 0, 0)
            gradeAndStatusLayout.dotContainer.visibility = View.VISIBLE

            // TODO need to check this with real data and then implement
//            gradeAndStatusLayout.course_item_icon.visibility = View.GONE
//
//            gradeAndStatusLayout.dotContainer.visibility = View.GONE
//            gradeAndStatusLayout.header.setCompoundDrawablesWithIntrinsicBounds(
//                R.drawable.ic_blue_dot,
//                0,
//                0,
//                0
//            )
//            gradeAndStatusLayout.header.compoundDrawablePadding =
//                resources.getDimension(R.dimen._10dp).toInt()
//            gradeAndStatusLayout.description.setPadding(
//                resources.getDimension(R.dimen._20dp).toInt(),
//                0,
//                0,
//                0
//            )
        } else {
            gradeAndStatusLayout.course_item_icon.visibility = View.INVISIBLE
            gradeAndStatusLayout.header.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            gradeAndStatusLayout.header.compoundDrawablePadding = 0
            gradeAndStatusLayout.description.setPadding(0, 0, 0, 0)
            gradeAndStatusLayout.dotContainer.visibility = View.VISIBLE
        }

        //for Discussion
        if (courseDetailBean?.newCourseroomData?.courseDetails?.discussionsCountByCourseIdResponse?.replies?.toString()?.toInt()!! > 0) {
            assignmentsLayout.course_item_icon.visibility = View.GONE
            assignmentsLayout.description.visibility = View.VISIBLE
            assignmentsLayout.dotContainer.visibility = View.GONE
            assignmentsLayout.header.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_blue_dot,
                0,
                0,
                0
            )
            assignmentsLayout.header.compoundDrawablePadding =
                resources.getDimension(R.dimen._10dp).toInt()
            assignmentsLayout.description.setPadding(
                resources.getDimension(R.dimen._20dp).toInt(),
                0,
                0,
                0
            )
//            assignmentsLayout.dotContainer.setPadding(0,resources.getDimension(R.dimen._5dp).toInt(),0,0)
            assignmentsLayout.header.minLines = 1
            if (courseDetailBean?.newCourseroomData?.courseDetails?.discussionsCountByCourseIdResponse?.replies?.toString()?.toInt() == 1) {
                assignmentsLayout.description.text = getString(R.string.unread_reply_to_you)
            } else {
                assignmentsLayout.description.text = getString(R.string.unread_replies_to_you)
            }
        } else {
            assignmentsLayout.course_item_icon.visibility = View.INVISIBLE
            assignmentsLayout.description.visibility = View.GONE
            assignmentsLayout.header.minLines = 2

            assignmentsLayout.header.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            assignmentsLayout.header.compoundDrawablePadding = 0
            assignmentsLayout.description.setPadding(0, 0, 0, 0)
            assignmentsLayout.dotContainer.visibility = View.VISIBLE
        }

    }catch(t:Throwable){}

    }

    private fun attachClickListenerForFlexPath() {
        profileLayout.setOnClickListener(clickListenerForFP) // profile
        gettingStartedLayout.setOnClickListener(clickListenerForFP) // competency map
        syllabusLayout.setOnClickListener(clickListenerForFP) // syllabus
        messageLayout.setOnClickListener(clickListenerForFP) // message
        announcementLayout.setOnClickListener(clickListenerForFP) // announcement
        gradeAndStatusLayout.setOnClickListener(clickListenerForFP) // assessments and grades
        assignmentsLayout.setOnClickListener(clickListenerForFP) // discussion
        discussionLayout.setOnClickListener(clickListenerForFP) // discussion drafts
        discussionDraftLayout.setOnClickListener(clickListenerForFP) // set target dates
        studiesLayout.setOnClickListener(clickListenerForFP) // complete this course
        classmatesLayout.setOnClickListener(clickListenerForFP) // classmates
        viewOnFullSiteLayout.setOnClickListener(clickListenerForFP) // view course on full site
    }

    private val clickListenerForFP: View.OnClickListener = View.OnClickListener {
        when (it.id) {
            R.id.profileLayout -> {
                // profile
                openInstructorProfile()
            }
            R.id.gettingStartedLayout -> {
                // competency map click
                val stickyWork = StickyInfoGrabber(this)
                DialogUtils.screenNamePrefix = "course:competency-map:linkout"
                stickyWork.generateMuleSoftStickySessionForTargetUrl(
                    BuildConfig.COMPETENCY_MAP_URL,
                    BuildConfig.STICKY_FORWARD_URL
                )
                // Util.showToastForUnderDevelopment(this)
            }
            R.id.syllabusLayout -> {
                val syllabusIntent = Intent(this, CourseRoomSyllabusActivity::class.java)
                syllabusIntent.putExtra(Constants.COURSE_ID, courseData?.courseIdentifier!!)
                syllabusIntent.putExtra(Constants.WHICH_FLEX_PATH, isFpFlexPath)
                startActivity(syllabusIntent)
            }
            R.id.messageLayout -> {
                //message click

                val stickyWork = StickyInfoGrabber(this)
                DialogUtils.screenNamePrefix = "course:linkout:messages"
                courseDetailBean?.newCourseroomData?.courseDetails?.messageLink?.let { it1 ->
                    stickyWork.generateMuleSoftStickySessionForTargetUrl(
                        it1, BuildConfig.STICKY_FORWARD_URL
                    )
                }

            }
            R.id.announcementLayout -> {
                val announcementIntent =
                    Intent(this@CourseRoomMenuActivity, AnnouncementsActivity::class.java)
                announcementIntent.putExtra(Constants.COURSE_ID, courseData?.courseIdentifier)
//                jsjsj
                announcementIntent.putExtra(Constants.ANNOUCEMENT_LIST_DATA, courseDetailBean)
//                startActivity(announcementIntent)
                startActivityForResult(announcementIntent, Constants.ACTIVITY_REQUEST)
                // announcement click
            }
            R.id.gradeAndStatusLayout -> {
                // assessments and status
                if (!isStudent()) {
                    // open assessment and status screen for Faculty
                    val assessmentIntent = Intent(this, AssessmentForFacultyActivity::class.java)
                    assessmentIntent.putExtra(Constants.COURSE_ID, courseData?.courseIdentifier!!)
                    assessmentIntent.putExtra(Constants.USER_EMPLOYE_ID, userEmployeeId)
                    assessmentIntent.putExtra(
                        Constants.COURSE_MESSAGE_LINK,
                        courseDetailBean?.newCourseroomData?.courseDetails?.messageLink
                    )
//                    assessmentIntent.putExtra(Constants.ANNOUCEMENT_LIST_DATA, courseDetailBean)
                    startActivity(assessmentIntent)
                } else {
                    // assessment for learner

                    val assessmentIntent = Intent(this, AssessmentForLearnerActivity::class.java)
                    assessmentIntent.putExtra(Constants.COURSE_ID, courseData?.courseIdentifier!!)
                    assessmentIntent.putExtra(Constants.USER_EMPLOYE_ID, userEmployeeId)
                    assessmentIntent.putExtra(
                        Constants.COURSE_MESSAGE_LINK,
                        courseDetailBean?.newCourseroomData?.courseDetails?.courseLink
                    )
                    assessmentIntent.putExtra(
                        Constants.COURSE_NUMBER_ID,
                        courseData?.courseSection?.course?.number!!
                    )
                    var crId: String? = null
                    if (courseDetailBean?.newCourseroomData?.courseDetails?.messageLink != null && courseDetailBean?.newCourseroomData?.courseDetails?.messageLink?.contains(
                            "course_id="
                        )!!
                    ) {
                        crId =
                            courseDetailBean?.newCourseroomData?.courseDetails?.messageLink.toString()
                        crId = crId.split("=")[2]
                        assessmentIntent.putExtra(Constants.COURSE_PK, crId)
                    }
                    startActivity(assessmentIntent)
                }

//                Util.showToastForUnderDevelopment(this)
            }
            R.id.assignmentsLayout -> {
                //discussion
                val discussionIntent =
                    Intent(this@CourseRoomMenuActivity, DiscussionForumActivity::class.java)
                discussionIntent.putExtra(Constants.COURSE_ID, courseData?.courseIdentifier)
                discussionIntent.putExtra(Constants.IS_GP_2_COURSE,courseData?.westworld)
                discussionIntent.putExtra(
                    Constants.COURSE_NUMBER_ID,
                    courseData?.courseSection?.course?.catalogNumber!!
                )
                startActivityForResult(discussionIntent,Constants.YELLOW_DIG_SCROLLING)
                //Util.showToastForUnderDevelopment(this)
            }
            R.id.discussionLayout -> {
                // discussion  drafts
                val intent =
                    Intent(this@CourseRoomMenuActivity, DiscussionDraftActivity::class.java)
                intent.putExtra(Constants.COURSE_DATA, courseData)
                startActivity(intent)
//                Util.showToastForUnderDevelopment(this)
            }
            R.id.discussionDraftLayout -> {
                OmnitureTrack.trackAction("course:set-dates-menu:linkout")
                // set target dates
                val targetDateIntent =
                    Intent(this@CourseRoomMenuActivity, TargetDateActivity::class.java)
                targetDateIntent.putExtra(Constants.COURSE_ID, courseData?.courseIdentifier!!)
                startActivity(targetDateIntent)
                discussionDraftLayout.contentDescription = getString(R.string.set_target_dates)
//                Util.showToastForUnderDevelopment(this)
            }
            R.id.studiesLayout -> {
                // complete this course
                OmnitureTrack.trackAction("course:complete-course-menu:linkout")
                val coursID =
                    courseDetailBean!!.newCourseroomData!!.courseDetails!!.messageLink!!.split("=")[2]
                val preString = Constants.COURSE_PRE_STRING
                val postString = Constants.COURSE_POST_STRING
                val link = preString + coursID + postString
//                val link=courseDetailBean!!.newCourseroomData!!.courseDetails!!.courseLink.toString()
                DialogUtils.screenNamePrefix="course:complete-course-menu:linkout"
                val stickyWork = StickyInfoGrabber(this@CourseRoomMenuActivity)
                
                stickyWork.generateMuleSoftStickySessionForTargetUrl(
                    link,
                    BuildConfig.STICKY_FORWARD_URL
                )

//                Util.showToastForUnderDevelopment(this)
            }
            R.id.classmatesLayout -> {
                // classmates click
                OmnitureTrack.trackAction("course:classmates:classmate-list")
                val classMateIntent = Intent(this, ClassmatesActivity::class.java)
                classMateIntent.putExtra(Constants.COURSE_ID, courseData?.courseIdentifier!!)
                startActivity(classMateIntent)
            }
            R.id.viewOnFullSiteLayout -> {

                OmnitureTrack.trackAction("course:linkout:confirm")
                
                val link =
                    courseDetailBean!!.newCourseroomData!!.courseDetails!!.courseLink.toString()
                DialogUtils.screenNamePrefix="view-full-site:link-out"
                val stickyWork = StickyInfoGrabber(this@CourseRoomMenuActivity)
                stickyWork.generateMuleSoftStickySessionForTargetUrl(
                    link,
                    BuildConfig.STICKY_FORWARD_URL
                )

//                Util.showToastForUnderDevelopment(this)
            }

        }

    }

    private fun setUiForGuidedPath1_0() {
        courseRoomScrollView.visibility = View.VISIBLE
        mainLayout.visibility = View.VISIBLE
        // icon setting
//        if(courseData?.courseSection?.course?.subject.equals(Constants.UOS_COURSE)){
//            profileLayout.course_item_icon.visibility = View.GONE
//        }else {
//            profileLayout.course_item_icon.visibility = View.VISIBLE
//        }
//        courseData?.courseSection?.instructor?.profileImage?.let {
//            picLoader.displayImage(
//                it,
//                profileLayout.course_item_icon
//            )
//        }

        if(courseData?.courseSection?.course?.subject.equals(Constants.UOS_COURSE)){
            profileLayout.visibility = View.GONE
        }else {
            profileLayout.visibility = View.VISIBLE
            profileLayout.course_item_icon.visibility = View.VISIBLE
            profileLayout.header.text = courseData?.courseSection?.instructor?.fullName
            profileLayout.description.text = getString(R.string.instructor)
            // icon setting
            profileLayout.course_item_icon.visibility = View.VISIBLE
            profileLayout.description.visibility = View.VISIBLE
            courseData?.courseSection?.instructor?.profileImage?.let {
                picLoader.displayImage(
                    it,
                    profileLayout.course_item_icon
                )
            }
        }
//        profileLayout.course_item_icon.maxWidth = resources.getDimension(R.dimen._25dp).toInt()
//        profileLayout.course_item_icon.maxHeight = resources.getDimension(R.dimen._25dp).toInt()



        if(GettingStartedUtil.isInCache(courseData!!.courseIdentifier) == null  ) {
            checkGettingStartedVisibility()
            gettingStartedLayout.visibility = View.GONE // REMOVE THIS LINE ONCE HANDLING IS DONE FOR GETTING STARTED USING LEFT NAVIGATION
        }else
        {
           var gCache =  GettingStartedUtil.isInCache(courseData!!.courseIdentifier)
            if(gCache?.visibile == true)
            {
                gettingStartedLayout.visibility = View.VISIBLE
            }else
            {
                gettingStartedLayout.visibility = View.GONE
            }
        }

        gettingStartedLayout.course_item_icon.visibility = View.VISIBLE
        gettingStartedLayout.course_item_icon.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_info_with_circle
            )
        )



        syllabusLayout.course_item_icon.visibility = View.VISIBLE
        syllabusLayout.bottomLine.visibility = View.INVISIBLE
        syllabusLayout.course_item_icon.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_format_list_bulleted
            )
        )


        path_header.text = courseData?.courseSection?.course?.title
        path_description.text = courseData?.courseSection?.section
        // header setting
//        profileLayout.header.text = courseData?.courseSection?.instructor?.fullName
//        profileLayout.description.visibility = View.VISIBLE
//        profileLayout.description.text = getString(R.string.instructor)


        gettingStartedLayout.header.text = getString(R.string.getting_started) // getting started
        syllabusLayout.header.text = getString(R.string.syllabus) //syllabus

        messageLayout.header.text = getString(R.string.messages) //message
        messageLayout.header.minLines = 1
        announcementLayout.header.text = getString(R.string.announcements) // announcement


        gradeAndStatusLayout.header.text = getString(R.string.grade_and_status) // grades and grades
        assignmentsLayout.header.text = getString(R.string.assignments) // assignments
        discussionLayout.header.text = getString(R.string.discussion) // discussion
        discussionDraftLayout.header.text =
            getString(R.string.discussion_drafts) // discussion drafts
        studiesLayout.header.text = getString(R.string.studies) //studies
        classmatesLayout.header.text = getString(R.string.classmates) // classmates
        viewOnFullSiteLayout.header.text = getString(
            R.string.view_course_on_fill_site,
            courseData?.courseSection?.course?.number
        ) //view course on full site

        viewOnFullSiteLayout.contentDescription = getString(
            R.string.view_course_on_fill_site,
            courseData?.courseSection?.course?.number
        ) + getString(R.string.link_will_open)
        setBlueDotForGP1_0()
    }

    fun setBlueDotForGP1_0() {


        messageLayout.description.visibility = View.VISIBLE

        // for message blue dot
        if (courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount != null && courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount?.toInt()!! > 0) {
            messageLayout.course_item_icon.visibility = View.GONE

            messageLayout.dotContainer.visibility = View.GONE
            messageLayout.header.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_blue_dot,
                0,
                0,
                0
            )
            messageLayout.header.compoundDrawablePadding =
                resources.getDimension(R.dimen._10dp).toInt()
            messageLayout.description.setPadding(
                resources.getDimension(R.dimen._20dp).toInt(),
                0,
                0,
                0
            )

            if (courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount?.toInt() == 1) {
                messageLayout.description.text = resources.getString(
                    R.string.you_have_unread_message_count,
                    courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount
                )
                messageLayout.contentDescription = getString(R.string.messages)+", "+resources.getString(
                    R.string.you_have_unread_message_count,
                    courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount
                )
            } else {
                messageLayout.description.text = resources.getString(
                    R.string.you_have_unread_messages_count,
                    courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount
                )
                messageLayout.contentDescription  = getString(R.string.messages)+", "+resources.getString(
                    R.string.you_have_unread_messages_count,
                    courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount
                )
            }
        } else {
            messageLayout.course_item_icon.visibility = View.INVISIBLE

            messageLayout.header.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            messageLayout.header.compoundDrawablePadding = 0
            messageLayout.description.setPadding(0, 0, 0, 0)
            messageLayout.dotContainer.visibility = View.VISIBLE
            messageLayout.course_item_icon.visibility = View.INVISIBLE

            messageLayout.description.text =
                resources.getString(
                    R.string.you_have_unread_messages_count,
                    resources.getString(R.string.no)
                )
            messageLayout.contentDescription = getString(R.string.messages)+", "+ resources.getString(
                R.string.you_have_unread_messages_count,
                resources.getString(R.string.no)
            )
        }

        // for announcements blue dot
//        if (courseDetailBean?.newCourseroomData?.courseDetails?.courseAnnouncements?.size!! > 0) {
        if (shouldShowBlueDotOnAnnouncement()) {
            announcementLayout.course_item_icon.visibility = View.GONE
            announcementLayout.dotContainer.visibility = View.GONE
            announcementLayout.contentDescription = getString(R.string.ada_announcement_click_message)
            announcementLayout.header.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_blue_dot,
                0,
                0,
                0
            )
            announcementLayout.header.compoundDrawablePadding =
                resources.getDimension(R.dimen._10dp).toInt()
            announcementLayout.description.setPadding(
                resources.getDimension(R.dimen._20dp).toInt(),
                0,
                0,
                0
            )
        } else {
            announcementLayout.header.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            announcementLayout.header.compoundDrawablePadding = 0
            announcementLayout.contentDescription = getString(R.string.announcements)
            announcementLayout.description.setPadding(0, 0, 0, 0)
            announcementLayout.dotContainer.visibility = View.VISIBLE
            announcementLayout.course_item_icon.visibility = View.INVISIBLE
        }

        //for grades and status
        /* if (getGradesAndStatusUnReadCount() > 0)
         {
             // CASE WHEN BLUE DOT APPEARS
             // TODO need to check this with real data and then implement
             gradeAndStatusLayout.course_item_icon.visibility =View.GONE
             gradeAndStatusLayout.dotContainer.visibility = View.GONE

             gradeAndStatusLayout.header.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_blue_dot,0,0,0)
             gradeAndStatusLayout.header.compoundDrawablePadding = resources.getDimension(R.dimen._10dp).toInt()
             gradeAndStatusLayout.description.setPadding(resources.getDimension(R.dimen._20dp).toInt(),0,0,0)

         } else
         {*/
        // CASE WHEN BLUE DOT DOES NOT APPEARS
        gradeAndStatusLayout.course_item_icon.visibility = View.INVISIBLE

        gradeAndStatusLayout.header.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        gradeAndStatusLayout.header.compoundDrawablePadding = 0
        gradeAndStatusLayout.description.setPadding(0, 0, 0, 0)
        gradeAndStatusLayout.dotContainer.visibility = View.VISIBLE
        /* }*/

        showCachedBlueDotForGrade()
        if (isStudent()) {
            callGradesBlueDotHandlerForStudent()
        } else {
            callGradesBlueDotHandlerForFaculty()
        }


        //for assignment (as per the acceptance criteria we will not display blue dot on assignment for GP1.0)
        //for Discussion
        if (courseDetailBean?.newCourseroomData?.courseDetails?.discussionsCountByCourseIdResponse?.replies != null  && courseDetailBean?.newCourseroomData?.courseDetails?.discussionsCountByCourseIdResponse?.replies?.toString()?.toInt()!! > 0) {

            discussionLayout.course_item_icon.visibility = View.GONE
            discussionLayout.dotContainer.visibility = View.GONE
            discussionLayout.description.visibility = View.VISIBLE

            discussionLayout.header.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_blue_dot,
                0,
                0,
                0
            )
            discussionLayout.header.compoundDrawablePadding =
                resources.getDimension(R.dimen._10dp).toInt()
            discussionLayout.description.setPadding(
                resources.getDimension(R.dimen._20dp).toInt(),
                0,
                0,
                0
            )

            discussionLayout.header.minLines = 1
            if (courseDetailBean?.newCourseroomData?.courseDetails?.discussionsCountByCourseIdResponse?.replies?.toString()?.toInt() == 1) {
                discussionLayout.description.text = getString(R.string.unread_reply_to_you)
            } else {
                discussionLayout.description.text = getString(R.string.unread_replies_to_you)
            }

        } else {
            discussionLayout.course_item_icon.visibility = View.INVISIBLE
            discussionLayout.description.visibility = View.GONE
            discussionLayout.header.minLines = 2

            discussionLayout.header.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            discussionLayout.header.compoundDrawablePadding = 0
            discussionLayout.description.setPadding(0, 0, 0, 0)
            discussionLayout.dotContainer.visibility = View.VISIBLE
        }
    }

    fun getGradesAndStatusUnReadCount(): Int {
        var count = 0
        count += courseDetailBean?.newCourseroomData?.courseDetails?.courseGradeStatusNotifications?.assignmentGrades?.size
            ?: 0
        count += courseDetailBean?.newCourseroomData?.courseDetails?.courseGradeStatusNotifications?.blogsGrades?.size
            ?: 0
        count += courseDetailBean?.newCourseroomData?.courseDetails?.courseGradeStatusNotifications?.quizzesGrades?.size
            ?: 0
        count += courseDetailBean?.newCourseroomData?.courseDetails?.courseGradeStatusNotifications?.journalsGrades?.size
            ?: 0
        count += courseDetailBean?.newCourseroomData?.courseDetails?.courseGradeStatusNotifications?.discussionsGrades?.size
            ?: 0
        return count
    }

    private fun attachClickListenerForGP1_0() {
        profileLayout.setOnClickListener(clickListenerForGP1_0) // profile
        gettingStartedLayout.setOnClickListener(clickListenerForGP1_0) // getting started
        syllabusLayout.setOnClickListener(clickListenerForGP1_0) // syllabus
        messageLayout.setOnClickListener(clickListenerForGP1_0) // message
        announcementLayout.setOnClickListener(clickListenerForGP1_0) // announcement
        gradeAndStatusLayout.setOnClickListener(clickListenerForGP1_0) // grades and grades
        assignmentsLayout.setOnClickListener(clickListenerForGP1_0) // assignments
        discussionLayout.setOnClickListener(clickListenerForGP1_0) // discussion
        discussionDraftLayout.setOnClickListener(clickListenerForGP1_0) // discussion drafts
        studiesLayout.setOnClickListener(clickListenerForGP1_0) // studies
        classmatesLayout.setOnClickListener(clickListenerForGP1_0) // classmates
        viewOnFullSiteLayout.setOnClickListener(clickListenerForGP1_0) // view course on full site
    }


    private val clickListenerForGP1_0: View.OnClickListener = View.OnClickListener {
        when (it.id) {
            R.id.profileLayout -> {
                // profile
                openInstructorProfile()
            }
            R.id.gettingStartedLayout -> {
                // getting started click
                try {
                    val gettingStartedIntent = Intent(this, GettingStartedActivity::class.java)
                    gettingStartedIntent.putExtra(
                        Constants.COURSE_ID,
                        courseData?.courseIdentifier!!
                    )
                    gettingStartedIntent.putExtra(
                        Constants.COURSE_MESSAGE_LINK,
                        courseDetailBean!!.newCourseroomData!!.courseDetails!!.messageLink
                    )
                    gettingStartedIntent.putExtra(
                        Constants.COURSE_LINK,
                        courseDetailBean!!.newCourseroomData!!.courseDetails!!.courseLink
                    )
                    startActivity(gettingStartedIntent)
                }catch (t:Throwable){
                    Util.trace("Getting started opening error $t")
                    t.printStackTrace()
                }
            }
            R.id.syllabusLayout -> {
                val syllabusIntent = Intent(this, CourseRoomSyllabusActivity::class.java)
                syllabusIntent.putExtra(Constants.COURSE_ID, courseData?.courseIdentifier!!)
                syllabusIntent.putExtra(Constants.WHICH_FLEX_PATH, isFpFlexPath)
                startActivity(syllabusIntent)
            }
            R.id.messageLayout -> {
                //message click

                val stickyWork = StickyInfoGrabber(this)
                DialogUtils.screenNamePrefix = "course:linkout:messages"
                courseDetailBean?.newCourseroomData?.courseDetails?.messageLink?.let { it1 ->
                    stickyWork.generateMuleSoftStickySessionForTargetUrl(
                        it1, BuildConfig.STICKY_FORWARD_URL
                    )
                }

            }
            R.id.announcementLayout -> {
                val announcementIntent =
                    Intent(this@CourseRoomMenuActivity, AnnouncementsActivity::class.java)
                announcementIntent.putExtra(Constants.COURSE_ID, courseData?.courseIdentifier)
                announcementIntent.putExtra(Constants.ANNOUCEMENT_LIST_DATA, courseDetailBean)
                startActivityForResult(announcementIntent, Constants.ACTIVITY_REQUEST)
                // announcement click
            }
            R.id.gradeAndStatusLayout -> {
                // grades and status
                try {
                    val gradesStatusIntent = Intent(this@CourseRoomMenuActivity, GradesStatusActivity::class.java)

                    gradesStatusIntent.putExtra(Constants.COURSE_ID, courseData?.courseIdentifier)
                    gradesStatusIntent.putExtra(Constants.IS_STUDENT, isStudent())
                    // https://courserooma.capella.edu/webapps/BBC-000-DeepLinks-BBLEARN/deeplink?landingpage=coursemessage&course_id=_244315_1
                    var crId: String? = null
                    if (courseDetailBean?.newCourseroomData?.courseDetails?.messageLink != null && courseDetailBean?.newCourseroomData?.courseDetails?.messageLink?.contains("course_id=")!!) {
                        crId = courseDetailBean?.newCourseroomData?.courseDetails?.messageLink.toString()
                        crId = crId.split("=")[2]
                        gradesStatusIntent.putExtra(Constants.COURSE_PK, crId)

                    }

                    startActivity(gradesStatusIntent)


                }catch (t:Throwable){
                    Util.trace("Cannot open Grade $t")
                    t.printStackTrace()
                }
            }
            R.id.assignmentsLayout -> {
                //assignments click
                val assignmentsIntent =
                    Intent(this@CourseRoomMenuActivity, AssignmentsActivity::class.java)
                assignmentsIntent.putExtra(Constants.COURSE_ID, courseData?.courseIdentifier!!)

                startActivity(assignmentsIntent)

                //Util.showToastForUnderDevelopment(this)

            }
            R.id.discussionLayout -> {
                // discussion
                OmnitureTrack.trackAction("course:discussions:forums")
                val discussionIntent =
                    Intent(this@CourseRoomMenuActivity, DiscussionForumActivity::class.java)
                discussionIntent.putExtra(Constants.COURSE_ID, courseData?.courseIdentifier)
                discussionIntent.putExtra(Constants.IS_GP_2_COURSE,courseData?.westworld)
                discussionIntent.putExtra(
                    Constants.COURSE_NUMBER_ID,
                    courseData?.courseSection?.course?.catalogNumber!!
                )
                startActivityForResult(discussionIntent,Constants.YELLOW_DIG_SCROLLING)
                //Util.showToastForUnderDevelopment(this)
            }
            R.id.discussionDraftLayout -> {

                // discussion drafts
                OmnitureTrack.trackAction("course:discussions:drafts")
                val intent =
                    Intent(this@CourseRoomMenuActivity, DiscussionDraftActivity::class.java)
                intent.putExtra(Constants.COURSE_DATA, courseData)
                startActivity(intent)

//                Util.showToastForUnderDevelopment(this)
            }
            R.id.studiesLayout -> {
                OmnitureTrack.trackAction("course:study-detail")
                // studies click
                val studyIntent = Intent(this, StudyActivity::class.java)
                studyIntent.putExtra(Constants.COURSE_ID, courseData?.courseIdentifier!!)
                startActivity(studyIntent)
//                Util.showToastForUnderDevelopment(this)
            }
            R.id.classmatesLayout -> {
                // classmates click
                OmnitureTrack.trackAction("course:classmates:classmate-list")
                val classMateIntent = Intent(this, ClassmatesActivity::class.java)
                classMateIntent.putExtra(Constants.COURSE_ID, courseData?.courseIdentifier!!)
                startActivity(classMateIntent)
            }

            R.id.viewOnFullSiteLayout -> {
                // view campus
                OmnitureTrack.trackAction("course:linkout:confirm")
               
                val link =
                    courseDetailBean!!.newCourseroomData!!.courseDetails!!.courseLink.toString()
                DialogUtils.screenNamePrefix="view-full-site:link-out"
                val stickyWork = StickyInfoGrabber(this@CourseRoomMenuActivity)
                stickyWork.generateMuleSoftStickySessionForTargetUrl(
                    link,
                    BuildConfig.STICKY_FORWARD_URL
                )
//                Util.showToastForUnderDevelopment(this)
            }

        }

    }

//    private fun saveFacultyLearner() {
//        try {
//            if(courseDetailBean!=null) {
//
//                var responselist =
//                    courseDetailBean?.newCourseroomData?.courseDetails?.courseAssignmentNotifications!!
//
//                if (responselist.isNotEmpty()) {
//
//                    var SHARED_COURSE_ID =courseDetailBean?.newCourseroomData?.courseDetails?.courseIdentifier!!.toString().trim()
//                    val SHARRED_LEARNER_DATE =
//                        SHARED_COURSE_ID + "" + PreferenceKeys.FP_LEARNER_FACULTY_DATE
//                    val SHARRED_LEARNER_READ =
//                        SHARED_COURSE_ID + "" + PreferenceKeys.FP_LEARNER_FACULTY_READ
//
////                var Datelist =
////                    stringToArrayList(Preferences.getArrayList(SHARRED_ANNOUNCEMENT_DATE)!!)
//
//                    var Datelist =
//                        stringToWords(Preferences.getArrayList(SHARRED_LEARNER_DATE)!!)
//
//                    var fpLearnerDatelist: ArrayList<String>? = null
//                    if (Datelist.isEmpty()) {
//                        fpLearnerDatelist = ArrayList<String>()
//                    } else {
//                        fpLearnerDatelist = Datelist as ArrayList
//                    }
//
//                    var Readlist =
//                        stringToWords(Preferences.getArrayList(SHARRED_LEARNER_READ)!!)
////                var Readlist =
////                    stringToArrayList(Preferences.getArrayList(SHARRED_ANNOUNCEMENT_READ)!!)
//
//                    var fpLearnerReadlist: ArrayList<String>? = null
//
//                    if (Readlist.isEmpty()) {
//                        fpLearnerReadlist = ArrayList<String>()
//                    } else {
//                        fpLearnerReadlist = Readlist as ArrayList
//                    }
//
//                    if (fpLearnerDatelist.size > 0) {
//
//                        var firstDate = fpLearnerDatelist[0]
//                        var lastDate = fpLearnerDatelist[fpLearnerDatelist.size - 1]
//
//                        firstDate = firstDate.replace("[", "")
//                        lastDate = lastDate.replace("]", "")
//                        lastDate = lastDate.replace("[", "")
//
//                        fpLearnerDatelist.set(0, firstDate)
//                        fpLearnerDatelist.set(fpLearnerDatelist.size - 1, lastDate)
//                    }
//
//                    if (fpLearnerReadlist!!.size > 0) {
//                        var firstRead = fpLearnerReadlist?.get(0)!!
//                        var lastRead = fpLearnerReadlist[fpLearnerReadlist.size - 1]
//
//
//                        firstRead = firstRead.replace("[", "")
//                        lastRead = lastRead.replace("]", "")
//                        lastRead = lastRead.replace("[", "")
//
//                        fpLearnerReadlist[0] = firstRead
//                        fpLearnerReadlist[fpLearnerReadlist.size - 1] = lastRead
//
//                    }
//
//                    val learnerlistDate = ArrayList<String>()
//                    val learnerlistRead = ArrayList<String>()
//
//                    var difference=responselist.size-fpLearnerDatelist.size
//
//                    for (i in responselist.indices) {
//                        var date: String=""
//
//                       if(responselist[i].notificationDate!=null)
//                       {
//                        date=   Util.getDate(responselist[i].notificationDate!!, Constants.DATE_FORMAT_SEC)!!
//                       }
//
//                        if(responselist.size>i+difference) {
//                            if (responselist[i + difference].notificationDate != null) {
//
//
//                                var checkdate =
//                                    Util.getDate(
//                                        responselist[i + difference].notificationDate!!,
//                                        Constants.DATE_FORMAT_SEC
//                                    )!!
//
//
//                                if (fpLearnerDatelist!!.size > 0) {
////
//                                    if (fpLearnerDatelist!!.size > i) {
//                                        if ((fpLearnerDatelist[i].trim()) == checkdate!!.trim()) {
//
//                                            if ((fpLearnerReadlist[i].trim()) == READ.trim()) {
//                                                responselist[i + difference].setRead(READ)
//                                            }
//
//                                            val SHARRED_ATTEMP_READ =SHARED_COURSE_ID+""+checkdate+""+ PreferenceKeys.FP_LEARNER_ATTEMP_READ
//                                            var attempListRead =
//                                                stringToWords(Preferences.getArrayList(SHARRED_ATTEMP_READ)!!)
//
//                                            var fpLearnerAttemplist: ArrayList<String>? = null
//                                            if (attempListRead.isEmpty()) {
//                                                fpLearnerAttemplist = ArrayList<String>()
//                                            } else {
//                                                fpLearnerAttemplist = attempListRead as ArrayList
//                                            }
//
//                                            if (fpLearnerAttemplist!!.size > 0) {
//                                                var firstRead = fpLearnerAttemplist?.get(0)!!
//                                                var lastRead = fpLearnerAttemplist[fpLearnerAttemplist.size - 1]
//
//                                                firstRead = firstRead.replace("[", "")
//                                                lastRead = lastRead.replace("]", "")
//                                                lastRead = lastRead.replace("[", "")
//
//                                                fpLearnerAttemplist[0] = firstRead
//                                                fpLearnerAttemplist[fpLearnerAttemplist.size - 1] = lastRead
//
//                                             val serverAttempsList=   responselist[i + difference].attempts!!.attempts
//
//                                                var attempsDifference=serverAttempsList!!.size-fpLearnerAttemplist.size
//
//                                                if(attempsDifference==0)
//                                                {
//                                                    for (i in serverAttempsList.indices)
//                                                    {
//                                                        if(fpLearnerAttemplist[i].trim() == READ.trim())
//                                                        {
//                                                            serverAttempsList[i].setRead(READ)
//                                                        }
//                                                        else{
//                                                            responselist[i + difference].setRead(UNREAD)
//                                                        }
//
//                                                    }
//                                                }
//                                                else{
//                                                    responselist[i + difference].setRead(UNREAD)
//                                                }
//
//                                            }
//
//
//                                        }
//                                    }
//
//                                }
//                            }
//                        }
//                        learnerlistDate.add(date)
//                        learnerlistRead.add(responselist[i].getRead()!!.toString().trim())
//                    }
//                    Preferences.addArrayList(
//                        SHARRED_LEARNER_DATE,
//                        learnerlistDate.toString()
//                    )
//                    Preferences.addArrayList(
//                        SHARRED_LEARNER_READ,
//                        learnerlistRead.toString()
//                    )
//                }
//            }
//
//        } catch (e: NullPointerException) {
//            e.printStackTrace()
//        }
//        catch (e: IndexOutOfBoundsException) {
//            e.printStackTrace()
//        }
//
//    }

    private fun saveAnnouncementLocal() {
        try {

            if (courseDetailBean != null) {

                var responselist =
                    courseDetailBean?.newCourseroomData?.courseDetails?.courseAnnouncements!!

                if (responselist.isNotEmpty()) {

                    var SHARED_COURSE_ID = responselist[0].courseIdentifier!!.toString().trim()
                    val SHARRED_ANNOUNCEMENT_DATE =
                        SHARED_COURSE_ID + "" + PreferenceKeys.FP_ANNOUNCEMENT_DATE
                    val SHARRED_ANNOUNCEMENT_READ =
                        SHARED_COURSE_ID + "" + PreferenceKeys.FP_ANNOUNCEMENT_READ

//                var Datelist =
//                    stringToArrayList(Preferences.getArrayList(SHARRED_ANNOUNCEMENT_DATE)!!)

                    var Datelist =
                        Util.stringToWords(Preferences.getArrayList(SHARRED_ANNOUNCEMENT_DATE)!!)

                    var fpAnnouncementDatelist: ArrayList<String>? = null
                    if (Datelist.isEmpty()) {
                        fpAnnouncementDatelist = ArrayList<String>()
                    } else {
                        fpAnnouncementDatelist = Datelist as ArrayList
                    }

                    var Readlist =
                        Util.stringToWords(Preferences.getArrayList(SHARRED_ANNOUNCEMENT_READ)!!)
//                var Readlist =
//                    stringToArrayList(Preferences.getArrayList(SHARRED_ANNOUNCEMENT_READ)!!)

                    var fpAnnouncementReadlist: ArrayList<String>? = null

                    if (Readlist.isEmpty()) {
                        fpAnnouncementReadlist = ArrayList<String>()
                    } else {
                        fpAnnouncementReadlist = Readlist as ArrayList
                    }

                    if (fpAnnouncementDatelist.size > 0) {

                        var firstDate = fpAnnouncementDatelist[0]
                        var lastDate = fpAnnouncementDatelist[fpAnnouncementDatelist.size - 1]

                        firstDate = firstDate.replace("[", "")
                        lastDate = lastDate.replace("]", "")
                        lastDate = lastDate.replace("[", "")

                        fpAnnouncementDatelist.set(0, firstDate)
                        fpAnnouncementDatelist.set(fpAnnouncementDatelist.size - 1, lastDate)
                    }

                    if (fpAnnouncementReadlist!!.size > 0) {
                        var firstRead = fpAnnouncementReadlist?.get(0)!!
                        var lastRead = fpAnnouncementReadlist[fpAnnouncementReadlist.size - 1]


                        firstRead = firstRead.replace("[", "")
                        lastRead = lastRead.replace("]", "")
                        lastRead = lastRead.replace("[", "")

                        fpAnnouncementReadlist[0] = firstRead
                        fpAnnouncementReadlist[fpAnnouncementReadlist.size - 1] = lastRead

                    }

                    val announcementslistDate = ArrayList<String>()
                    val announcementslistRead = ArrayList<String>()

                    var difference = responselist.size - fpAnnouncementDatelist.size

                    for (i in responselist.indices) {
                        var date: String =
                            Util.getDate(responselist[i].startDate!!, Constants.DATE_FORMAT_SEC)!!

                        if (responselist.size > i + difference) {
                            var checkdate =
                                Util.getDate(
                                    responselist[i + difference].startDate!!,
                                    Constants.DATE_FORMAT_SEC
                                )!!


                            if (fpAnnouncementDatelist.size > 0) {
//
                                if (fpAnnouncementDatelist.size > i) {
                                    if ((fpAnnouncementDatelist[i].trim()) == checkdate!!.trim()) {
                                        if ((fpAnnouncementReadlist[i].trim()) == Constants.READ.trim()) {
                                            responselist[i + difference].setRead(Constants.READ)
                                        }
                                    }
                                }
                            }
                        }

                        announcementslistDate.add(date)
                        announcementslistRead.add(responselist[i].getRead()!!.toString().trim())
                    }
                    Preferences.addArrayList(
                        SHARRED_ANNOUNCEMENT_DATE,
                        announcementslistDate.toString()
                    )
                    Preferences.addArrayList(
                        SHARRED_ANNOUNCEMENT_READ,
                        announcementslistRead.toString()
                    )
                }
            }

        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }

    }

    private fun setUiForGuidedPath2_0(
        leftNavigation: GP2Bean,
        errorView: Boolean
    ) {


        checkFpFlex = Constants.GP2
        courseRoomScrollView.visibility = View.VISIBLE
        mainLayout.visibility = View.VISIBLE
        path_header.text = courseData?.courseSection?.course?.title
        path_description.text = courseData?.courseSection?.section
        // header setting


        if(courseData?.courseSection?.course?.subject.equals(Constants.UOS_COURSE)){
            profileLayout.visibility = View.GONE
        }else {
            profileLayout.visibility = View.VISIBLE
            profileLayout.course_item_icon.visibility = View.VISIBLE
            profileLayout.header.text = courseData?.courseSection?.instructor?.fullName
            profileLayout.description.text = getString(R.string.instructor)
            // icon setting
            profileLayout.course_item_icon.visibility = View.VISIBLE
            profileLayout.description.visibility = View.VISIBLE
            courseData?.courseSection?.instructor?.profileImage?.let {
                picLoader.displayImage(
                    it,
                    profileLayout.course_item_icon
                )
            }
        }

//        profileLayout.description.visibility = View.VISIBLE


        // error message view
        if (errorView) {
            error_layout.visibility = View.VISIBLE
            setErrorMessageForGP2()



            gettingStartedLayout.header.text = resources.getString(R.string.classmates)
            syllabusLayout.header.text = getString(
                R.string.view_course_on_fill_site,
                courseData?.courseSection?.course?.number
            )
            gettingStartedLayout.setOnClickListener(clickListenerForGP2_0)
            syllabusLayout.setOnClickListener(clickListenerForGP2_0)

            header_seperator.visibility = View.GONE
            messageLayout.visibility = View.GONE
            announcementLayout.visibility = View.GONE
            gradeAndStatusLayout.visibility = View.GONE
            assignmentsLayout.visibility = View.GONE
            discussionLayout.visibility = View.GONE
            discussionDraftLayout.visibility = View.GONE
            studiesLayout.visibility = View.GONE
            classmatesLayout.visibility = View.GONE
            viewOnFullSiteLayout.visibility = View.GONE

        } else {
            storeGp2InCache(leftNavigation)

            // normal GP2 view
//            // icon setting
//            profileLayout.course_item_icon.visibility = View.VISIBLE
//            courseData?.courseSection?.instructor?.profileImage?.let {
//                picLoader.displayImage(
//                    it,
//                    profileLayout.course_item_icon
//                )
//            }

            // hide other course headers
            gettingStartedLayout.visibility = View.GONE // REMOVE THIS LINE ONCE HANDLING IS DONE FOR GETTING STARTED USING LEFT NAVIGATION
            syllabusLayout.visibility = View.GONE
            GP_2_item_3.visibility = View.GONE
            GP_2_item_4.visibility = View.GONE

            // visible recyclerview view for gp2
            gp2_header.visibility = View.VISIBLE


            //todo setHearderList
            assignmentRecyclerLayout.visibility = View.VISIBLE
            discussionDraftLayout.visibility = View.GONE
            studiesLayout.visibility = View.GONE

            gp2_classmate_Layout.header.text =
                resources.getString(R.string.classmates) // classmates
            gp2_classmate_Layout.bottomLine.visibility = View.INVISIBLE

            messageLayout.header.text = getString(R.string.messages) //message
            messageLayout.header.minLines = 1
            announcementLayout.header.text = getString(R.string.announcements) // announcement
            gradeAndStatusLayout.header.text =
                getString(R.string.grade_and_status) // grades and status
            assignmentsLayout.header.text = getString(R.string.discussion) // discussion
            discussionLayout.header.text = getString(R.string.discussion_drafts) // discussion draft
            discussionLayout.bottomLine.visibility = View.INVISIBLE


            classmatesLayout.header.text = getString(R.string.course_tool) // classmates
            viewOnFullSiteLayout.header.text = getString(
                R.string.view_course_on_fill_site,
                courseData?.courseSection?.course?.number
            ) //view course on full site
            viewOnFullSiteLayout.contentDescription = getString(
                R.string.view_course_on_fill_site,
                courseData?.courseSection?.course?.number
            ) + getString(R.string.link_will_open)
            gp2ItemList = ArrayList()
            gp2ItemList?.add(resources.getString(R.string.classmates))
            gp2ItemList?.add(resources.getString(R.string.messages))
            gp2ItemList?.add(resources.getString(R.string.announcements))
            gp2ItemList?.add(resources.getString(R.string.grade_and_status))
            gp2ItemList?.add(resources.getString(R.string.discussion))
            gp2ItemList?.add(resources.getString(R.string.discussion_drafts))
            gp2ItemList?.add(resources.getString(R.string.course_tool))
            leftNavigationItem = leftNavigation
            setHeaderAdapter(leftNavigation)
            setWeeksAdapter(leftNavigation)
            setBlueDotForGP2_0()
        }

        attachClickListenerForGuidedPath_2()
        if(shouldScrollToWeek){
            courseRoomScrollView.scrollTo(courseRoomScrollView.top,discussionLayout.bottom)
            shouldScrollToWeek = false
        }
    }


    fun setBlueDotForGP2_0() {


        messageLayout.description.visibility = View.VISIBLE

        // for message blue dot
        if (courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount != null && courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount?.toInt()!! > 0) {
            messageLayout.course_item_icon.visibility = View.GONE

            messageLayout.dotContainer.visibility = View.GONE
            messageLayout.header.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_blue_dot, 0, 0, 0)

            messageLayout.header.compoundDrawablePadding =
                resources.getDimension(R.dimen._10dp).toInt()
            messageLayout.description.setPadding(
                resources.getDimension(R.dimen._20dp).toInt(), 0, 0, 0)

            if (courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount?.toInt() == 1) {
                messageLayout.description.text = resources.getString(
                    R.string.you_have_unread_message_count,
                    courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount
                )

                messageLayout.contentDescription = getString(R.string.messages)+", "+ resources.getString(
                    R.string.you_have_unread_message_count,
                    courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount
                )
            } else {
                messageLayout.description.text = resources.getString(
                    R.string.you_have_unread_messages_count,
                    courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount
                )
                messageLayout.contentDescription = getString(R.string.messages)+", "+resources.getString(
                    R.string.you_have_unread_messages_count,
                    courseDetailBean?.newCourseroomData?.courseDetails?.courseMessageCount
                )
            }
        } else {
            messageLayout.course_item_icon.visibility = View.INVISIBLE

            messageLayout.header.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            messageLayout.header.compoundDrawablePadding = 0
            messageLayout.description.setPadding(0, 0, 0, 0)
            messageLayout.dotContainer.visibility = View.VISIBLE
            messageLayout.course_item_icon.visibility = View.INVISIBLE

            messageLayout.description.text =
                resources.getString(
                    R.string.you_have_unread_messages_count,
                    resources.getString(R.string.no)
                )
            messageLayout.contentDescription = getString(R.string.messages)+", "+  resources.getString(
                R.string.you_have_unread_messages_count,
                resources.getString(R.string.no)
            )
        }

        // for announcements blue dot
//        if (courseDetailBean?.newCourseroomData?.courseDetails?.courseAnnouncements?.size!! > 0) {
        if (shouldShowBlueDotOnAnnouncement()) {
            announcementLayout.course_item_icon.visibility = View.GONE
            announcementLayout.dotContainer.visibility = View.GONE
            announcementLayout.contentDescription = getString(R.string.ada_announcement_click_message)
            announcementLayout.header.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_blue_dot, 0, 0, 0)

            announcementLayout.header.compoundDrawablePadding =
                resources.getDimension(R.dimen._10dp).toInt()
            announcementLayout.description.setPadding(
                resources.getDimension(R.dimen._20dp).toInt(), 0, 0, 0)

        } else {
            announcementLayout.header.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            announcementLayout.header.compoundDrawablePadding = 0
            announcementLayout.contentDescription = getString(R.string.announcements)
            announcementLayout.description.setPadding(0, 0, 0, 0)
            announcementLayout.dotContainer.visibility = View.VISIBLE
            announcementLayout.course_item_icon.visibility = View.INVISIBLE
        }

        //for grades and status
        // CASE WHEN BLUE DOT DOES NOT APPEARS
        gradeAndStatusLayout.course_item_icon.visibility = View.INVISIBLE

        gradeAndStatusLayout.header.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        gradeAndStatusLayout.header.compoundDrawablePadding = 0
        gradeAndStatusLayout.description.setPadding(0, 0, 0, 0)
        gradeAndStatusLayout.dotContainer.visibility = View.VISIBLE
        /* }*/

        showCachedBlueDotForGrade()
        if (isStudent()) {
            callGradesBlueDotHandlerForStudent()
        } else {
            callGradesBlueDotHandlerForFaculty()
        }


        //for assignment (as per the acceptance criteria we will not display blue dot on assignment for GP1.0)
        //for Discussion
        if (courseDetailBean?.newCourseroomData?.courseDetails?.discussionsCountByCourseIdResponse?.replies?.toString()?.toInt()!! > 0) {

            assignmentsLayout.course_item_icon.visibility = View.GONE
            assignmentsLayout.dotContainer.visibility = View.GONE
            assignmentsLayout.description.visibility = View.VISIBLE

            assignmentsLayout.header.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_blue_dot,
                0,
                0,
                0
            )
            assignmentsLayout.header.compoundDrawablePadding =
                resources.getDimension(R.dimen._10dp).toInt()
            assignmentsLayout.description.setPadding(
                resources.getDimension(R.dimen._20dp).toInt(),
                0,
                0,
                0
            )

            assignmentsLayout.header.minLines = 1
            if (courseDetailBean?.newCourseroomData?.courseDetails?.discussionsCountByCourseIdResponse?.replies?.toString()?.toInt() == 1) {
                assignmentsLayout.description.text = getString(R.string.unread_reply_to_you)
            } else {
                assignmentsLayout.description.text = getString(R.string.unread_replies_to_you)
            }

        } else {
            assignmentsLayout.course_item_icon.visibility = View.INVISIBLE
            assignmentsLayout.description.visibility = View.GONE
            assignmentsLayout.header.minLines = 2

            assignmentsLayout.header.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            assignmentsLayout.header.compoundDrawablePadding = 0
            assignmentsLayout.description.setPadding(0, 0, 0, 0)
            assignmentsLayout.dotContainer.visibility = View.VISIBLE
        }
    }

    private fun setErrorMessageForGP2(){
        val ss = SpannableString(resources.getString(R.string.gp_2_error_message))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {

                // TODO handle click if require
                DialogUtils.screenNamePrefix = "course:room-linkout"
                val stickyWork = StickyInfoGrabber(this@CourseRoomMenuActivity)
                stickyWork.generateMuleSoftStickySessionForTargetUrl(
                    courseDetailBean?.newCourseroomData?.courseDetails?.courseLink?.toString()!!,
                    BuildConfig.STICKY_FORWARD_URL
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        ss.setSpan(
            clickableSpan,
            resources.getString(R.string.gp_2_error_message).indexOf(". "),
            resources.getString(R.string.gp_2_error_message).indexOf("courseroom") + 10,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        errorTxt.text = ss
        errorTxt.movementMethod = LinkMovementMethod.getInstance();
        errorTxt.highlightColor = ContextCompat.getColor(this, R.color.border_grey_300);
    }

    private fun attachClickListenerForGuidedPath_2() {
        profileLayout.setOnClickListener (clickListenerForGP2_0)
        gp2_classmate_Layout.setOnClickListener(clickListenerForGP2_0) // classmate
        messageLayout.setOnClickListener(clickListenerForGP2_0) // message
        announcementLayout.setOnClickListener(clickListenerForGP2_0) // announcement
        gradeAndStatusLayout.setOnClickListener(clickListenerForGP2_0) // grades and status
        assignmentsLayout.setOnClickListener(clickListenerForGP2_0) // discussion
        discussionLayout.setOnClickListener(clickListenerForGP2_0) // discussion drafts
        classmatesLayout.setOnClickListener(clickListenerForGP2_0) // classmates
        viewOnFullSiteLayout.setOnClickListener(clickListenerForGP2_0) // view course on full site
    }

    private val clickListenerForGP2_0: View.OnClickListener = View.OnClickListener {
        when (it.id) {
            R.id.profileLayout -> {
                // profile
                openInstructorProfile()
            }

            R.id.gp2_classmate_Layout -> {
                // classmates click
                OmnitureTrack.trackAction("course:classmates:classmate-list")
                val classMateIntent = Intent(this, ClassmatesActivity::class.java)
                classMateIntent.putExtra(Constants.COURSE_ID, courseData?.courseIdentifier!!)
                startActivity(classMateIntent)
            }

            R.id.messageLayout -> {
                //message click

                getMessageItemAndOpen()
//                val stickyWork = StickyInfoGrabber(this)
//                DialogUtils.screenNamePrefix = "course:linkout:messages"
//                courseDetailBean?.newCourseroomData?.courseDetails?.messageLink?.let { it1 ->
//                    stickyWork.generateMuleSoftStickySessionForTargetUrl(
//                        it1, BuildConfig.STICKY_FORWARD_URL
//                    )
//                }

            }

            R.id.announcementLayout -> {
                // announcement
                getAnnouncementItemAndOpen()

//                OmnitureTrack.trackAction("course:announcements")
//                val announcementIntent =
//                    Intent(this@CourseRoomMenuActivity, AnnouncementsActivity::class.java)
//                announcementIntent.putExtra(Constants.COURSE_ID, courseData?.courseIdentifier)
//                announcementIntent.putExtra(Constants.ANNOUCEMENT_LIST_DATA, courseDetailBean)
//                startActivityForResult(announcementIntent, Constants.ACTIVITY_REQUEST)
                // announcement click
            }

            R.id.gradeAndStatusLayout -> {
                // grades and status
                val gradesStatusIntent =
                    Intent(this@CourseRoomMenuActivity, GradesStatusActivity::class.java)

                gradesStatusIntent.putExtra(Constants.COURSE_ID, courseData?.courseIdentifier)

                // https://courserooma.capella.edu/webapps/BBC-000-DeepLinks-BBLEARN/deeplink?landingpage=coursemessage&course_id=_244315_1
                var crId: String? = null
                if (courseDetailBean?.newCourseroomData?.courseDetails?.messageLink != null && courseDetailBean?.newCourseroomData?.courseDetails?.messageLink?.contains(
                        "course_id="
                    )!!
                ) {
                    crId =
                        courseDetailBean?.newCourseroomData?.courseDetails?.messageLink.toString()
                    crId = crId.split("=")[2]
                    gradesStatusIntent.putExtra(Constants.COURSE_PK, crId)
                }


                gradesStatusIntent.putExtra(Constants.IS_STUDENT, isStudent())
                startActivity(gradesStatusIntent)
            }

            R.id.assignmentsLayout -> {
                //discussion click
                OmnitureTrack.trackAction("course:discussions:forums")
                val discussionIntent =
                    Intent(this@CourseRoomMenuActivity, DiscussionForumActivity::class.java)
                discussionIntent.putExtra(Constants.COURSE_ID, courseData?.courseIdentifier)
                discussionIntent.putExtra(Constants.IS_GP_2_COURSE,courseData?.westworld)
                discussionIntent.putExtra(
                    Constants.COURSE_NUMBER_ID,
                    courseData?.courseSection?.course?.catalogNumber!!
                )
                startActivityForResult(discussionIntent,Constants.YELLOW_DIG_SCROLLING)
            }

            R.id.discussionLayout -> {
                // discussion draft
                OmnitureTrack.trackAction("course:discussions:drafts")
                val intent =
                    Intent(this@CourseRoomMenuActivity, DiscussionDraftActivity::class.java)
                intent.putExtra(Constants.COURSE_DATA, courseData)
                startActivity(intent)
                //Util.showToastForUnderDevelopment(this)
            }

            R.id.classmatesLayout -> {
                // course tool click
                val courseIntent =
                    Intent(this@CourseRoomMenuActivity, CourseToolActivity::class.java)
                courseIntent.putExtra(Constants.COURSE_TOOL_LIST, getCourseToolList())
                courseIntent.putExtra(Constants.HEADER,getDomainForLink())
                startActivity(courseIntent)
            }

            R.id.viewOnFullSiteLayout -> {
                // view campus
                OmnitureTrack.trackAction("course:linkout:confirm")
               

                val link =
                    courseDetailBean!!.newCourseroomData!!.courseDetails!!.courseLink.toString()
//                val stickyWork = StickyInfoGrabber(this@CourseRoomMenuActivity)
//                stickyWork.generateMuleSoftStickySessionForTargetUrl(
//                    link,
//                    BuildConfig.STICKY_FORWARD_URL
//                )

                openCourseRoomUrlInAppBrowser(link,getString(
                    R.string.view_course_on_fill_site,
                    courseData?.courseSection?.course?.number
                ))
            }

            R.id.gettingStartedLayout -> {
                // classmate
                val classMateIntent = Intent(this, ClassmatesActivity::class.java)
                classMateIntent.putExtra(Constants.COURSE_ID, courseData?.courseIdentifier!!)
                startActivity(classMateIntent)
            }

            R.id.syllabusLayout -> {
                // view campus
                val link =
                    courseDetailBean!!.newCourseroomData!!.courseDetails!!.courseLink.toString()
                DialogUtils.screenNamePrefix = "view:campus:fullsite-linkout"
                val stickyWork = StickyInfoGrabber(this@CourseRoomMenuActivity)
                stickyWork.generateMuleSoftStickySessionForTargetUrl(
                    link,
                    BuildConfig.STICKY_FORWARD_URL
                )
            }

        }

    }

    private fun getAnnouncementItemAndOpen(){
        var announcementItem : GP2Bean.LeftNavigationItem? = null
        for (index in 0 until leftNavigationItem?.leftNavigationItems?.size!!){
            if(leftNavigationItem?.leftNavigationItems!![index].name.equals("Announcements")){
                announcementItem =  leftNavigationItem?.leftNavigationItems!![index]
            }
        }

        announcementItem?.let { openUrlInAppBrowser(it) }
    }

    private fun getMessageItemAndOpen(){
        var messageItem : GP2Bean.LeftNavigationItem? = null
        for (index in 0 until leftNavigationItem?.leftNavigationItems?.size!!){
            if(leftNavigationItem?.leftNavigationItems!![index].name.equals("Messages")){
                messageItem =  leftNavigationItem?.leftNavigationItems!![index]
            }
        }

        messageItem?.let { openUrlInAppBrowser(it) }
    }

    private fun setHeaderAdapter(leftNavigation: GP2Bean) {
        val layoutManager =
            NonScrollableLayoutManager(
                this
            )
        layoutManager.setScrollEnabled(false)
        gp2_header_recyclerView.layoutManager = layoutManager
        val headerList = getHeadersList(leftNavigation)

        headerAdapter = WeekAdapter(this, headerList, false, object : WeekAdapter.WeekEventListener {
            override fun onWeekItemClick(leftNavigationItem: GP2Bean.LeftNavigationItem) {
                openUrlInAppBrowser(leftNavigationItem)
            }

        })
        gp2_header_recyclerView.adapter = headerAdapter
    }

    private fun getDomainForLink(): String {
        var urlWithoutDomainName =
            courseDetailBean?.newCourseroomData?.courseDetails?.messageLink?.toString()
                ?.length?.let {
                courseDetailBean?.newCourseroomData?.courseDetails?.messageLink?.toString()
                    ?.substring(
                        8,
                        it
                    )
            };
        var schoolName = urlWithoutDomainName?.substring(0, urlWithoutDomainName.indexOf("/"))
        val finalDomain = "https://" + schoolName
        return finalDomain
    }

    private fun openUrlInAppBrowser(item: GP2Bean.LeftNavigationItem) {
        // todo open in appBrowser
        val finalLink = getDomainForLink() + item.url
        val intent = Intent(this@CourseRoomMenuActivity, GP2InAppBrowser::class.java)
        intent.putExtra(Constants.URL_FOR_IN_APP, finalLink)
        intent.putExtra(Constants.COURSE_ID , courseData?.courseIdentifier)
        intent.putExtra(
            Constants.COURSE_NUMBER_ID,
            courseData?.courseSection?.course?.catalogNumber.toString()!!
        )
        intent.putExtra(Constants.IN_APP_TITLE,item.name)
        startActivityForResult(intent,GP2_BROWSER_RESULT)
        overridePendingTransition( R.anim.slide_in_up, R.anim.no_anim)
    }

    private fun openCourseRoomUrlInAppBrowser(url: String,name:String) {
        // todo open in appBrowser
        val intent = Intent(this@CourseRoomMenuActivity, GP2InAppBrowser::class.java)
        intent.putExtra(Constants.URL_FOR_IN_APP, url)
        intent.putExtra(Constants.COURSE_ID , courseData?.courseIdentifier)
        intent.putExtra(
            Constants.COURSE_NUMBER_ID,
            courseData?.courseSection?.course?.catalogNumber.toString())
        intent.putExtra(Constants.IN_APP_TITLE,name)
        startActivityForResult(intent,GP2_BROWSER_RESULT)
        overridePendingTransition( R.anim.slide_in_up, R.anim.no_anim)
    }

    private fun getCourseToolList():ArrayList<GP2Bean.LeftNavigationItem> {

         val courToolList :ArrayList<GP2Bean.LeftNavigationItem> = ArrayList()
        var isCourseToolFound = false
         for (index in 0 until leftNavigationItem?.leftNavigationItems?.size!!){
            var isMatchFound = false
             if(!isCourseToolFound) {
                 if (leftNavigationItem?.leftNavigationItems!![index].name?.contains(
                         "Course Tools",true
                     )!!
                 ) {
                     isCourseToolFound = true
                 }
             }
             if(isCourseToolFound) {
                 val outerName = leftNavigationItem?.leftNavigationItems!![index].name
                 if( ! outerName?.contains("Course Tools",true)!!) {
                     for (position in 0 until gp2ItemList?.size!!) {
                         val innerName = gp2ItemList!!.get(position)
                         if (innerName.equals(outerName, true)) {
                             isMatchFound = true
                             break
                         }
                     }
                 }
                 if (!isMatchFound) {
                     courToolList.add(leftNavigationItem?.leftNavigationItems!![index])
                 }
             }
        }

        courToolList.removeAt(0)
        return  courToolList
    }

    private fun getHeadersList(leftNavigation: GP2Bean): ArrayList<GP2Bean.LeftNavigationItem> {
        var headerList: ArrayList<GP2Bean.LeftNavigationItem> = ArrayList()
        for (index in 0 until leftNavigation.leftNavigationItems?.size!!) {
            if ((leftNavigation.leftNavigationItems!![index].name?.contains("week", true)!!)
            ) {
                break
            } else {
                headerList.add(leftNavigation.leftNavigationItems!![index])
                gp2ItemList?.add(leftNavigation.leftNavigationItems!![index].name.toString())
            }
        }
        return headerList
    }

    private fun setWeeksAdapter(leftNavigation: GP2Bean) {
        val layoutManager =
            NonScrollableLayoutManager(
                this
            )
        layoutManager.setScrollEnabled(false)
        courseAssignmentList.layoutManager = layoutManager
        val weekList = getWeekList(leftNavigation)
        weekAdapter = WeekAdapter(this, weekList,true, object : WeekAdapter.WeekEventListener {
            override fun onWeekItemClick(leftNavigationItem: GP2Bean.LeftNavigationItem) {
                openUrlInAppBrowser(leftNavigationItem)
            }

        })
        courseAssignmentList.adapter = weekAdapter

    }


    private fun getWeekList(leftNavigation: GP2Bean): ArrayList<GP2Bean.LeftNavigationItem> {
        val weekList: ArrayList<GP2Bean.LeftNavigationItem> = ArrayList()
        var lastIndexOfWeek: Int = 0
        var isCourseMaterialFound = false
        for (index in 0 until leftNavigation.leftNavigationItems?.size!!) {
            if ((leftNavigation.leftNavigationItems!![index].name?.contains("week", true)!!)) {
                weekList.add(leftNavigation.leftNavigationItems!![index])
                gp2ItemList?.add(leftNavigation.leftNavigationItems!![index].name.toString())
            } else if (!isCourseMaterialFound && weekList.size > 0){
                if(leftNavigation.leftNavigationItems!![index].name?.contains("Course Materials", true)!!){
                    isCourseMaterialFound = true
                }

                weekList.add(leftNavigation.leftNavigationItems!![index])
                gp2ItemList?.add(leftNavigation.leftNavigationItems!![index].name.toString())
            }/*else if (weekList.size > 0) {
                lastIndexOfWeek = index
                break

            }*/

        }
        return weekList

    }
    // TODO write click listener for guided path 2.0


    /**
     * callApiToGetCourseDetail(): method to call course detail api
     *
     * */
    private fun callApiToGetCourseDetail() {
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val params = HashMap<String, Any>()
        params[NetworkConstants.TOKEN] = loginBean?.authData?.token!!
        userEmployeeId = loginBean?.authData?.employeeId!!.value!!

        val qStringParams = HashMap<String, Any>()
        qStringParams[NetworkConstants.COURSE_ID] = courseData?.courseIdentifier!!
        qStringParams[NetworkConstants.ACTION] = NetworkConstants.ACTION_GET_COURSE_DETAIL
        qStringParams[NetworkConstants.IS_FACULTY] = courseData?.faculty!!
        qStringParams[NetworkConstants.FACULTY_ROLE] = courseData?.facultyRole ?: "null"
        qStringParams[NetworkConstants.IS_FLEX_PATH_COURSE] = courseData?.flexpathCourse!!

        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.COURSE_DETAIL_API + NetworkService.getQueryString(qStringParams),
            params,
            NetworkHandler.METHOD_POST,
            courseDetailListener,
            null
        )
        if(isGp2){
            networkHandler.setSilentMode(true)
            blueDotSpinner.visibility = View.VISIBLE
        }else{
            networkHandler.setSilentMode(false)
        }
        networkHandler.isPostTypeSubmitting = true
        networkHandler.submitMessage = ""
        networkHandler.execute()

    }


    private val courseDetailListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {
            try {

                Util.trace("course room first :  " + response.first)
               // Util.trace("course room second :  " + response.second)
                // TODO handle course detail api responseset
                if (response.first == NetworkConstants.SUCCESS) {
                    
                    val gson = Gson()
                    courseDetailBean = gson.fromJson<CourseDetailBean>(
                        response.second.toString(),
                        CourseDetailBean::class.java
                    )

                    if(!isGp2) {
                        if (courseData != null) {
                            if ((checkFpFlex == Constants.FP2) || (checkFpFlex == Constants.FP1)) {
                                if (!isStudent()) {
                                    callAssessmentFacultyApi(courseData!!.courseIdentifier!!)
                                } else {
                                    callApiToGetAssessments()
                                }
                            }
                        }
                    }
//                    saveFacultyLearner()
                    try {
                        saveAnnouncementLocal()
                    }catch (tt:Throwable)
                    {
                        tt.printStackTrace()
                    }
                    try {
                        Preferences.addValue(PreferenceKeys.COURSE_ROOM_DISCUSSION_LINK, courseDetailBean?.newCourseroomData?.courseDetails?.discussionLink.toString())
                    }catch (tt:Throwable){
                        tt.printStackTrace()
                    }
                    // TODO: STORE ANNOUNCEMENT LIST IN SHARED PREFERENCE
                    try {
                        setScreenView()
                    }catch (tt:Throwable){
                        tt.printStackTrace()
                    }
                } else {
                    DialogUtils.showGenericErrorDialog(this@CourseRoomMenuActivity)
                }
            }catch (e: Exception) {
                e.printStackTrace()
                DialogUtils.showGenericErrorDialog(this@CourseRoomMenuActivity)
            }
        }
    }

    /**
     * callApiToGetLastLoginDate() : call last login date api to get last access date
     *
     *  **/

    private fun callApiToGetLastLoginDate() {

        // TODO need to confirm flow for this api and then implement remaining feature
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val authHeader = NetworkHandler.getAuthorizationHeader()
        val hubbleHeaders = NetworkHandler.getHeadersForHubbleRequest(
            loginBean?.authData?.token,
            HubbleNetworkConstants.COURSE_LAST_LOGIN_DATE
        )

        val finalHeaders = NetworkHandler.meargeHeaders(authHeader, hubbleHeaders)

        val params = java.util.HashMap<String, Any>()

        val url = HubbleNetworkConstants.getUrl(
            HubbleNetworkConstants.COURSE_LAST_LOGIN_DATE,
            "{{employeeId}}",
            "" + loginBean?.authData?.employeeId!!.value
        )


        val courseUrl =
            HubbleNetworkConstants.getUrl(url, "{{courseId}}", "" + courseData?.courseIdentifier!!)

        val lastLoginUrl =
            HubbleNetworkConstants.getUrl(courseUrl, "{{lastLoginDateTime}}", "" + "16-03-2020")
        val networkHandler = NetworkHandler(
            this,
            lastLoginUrl,
            params,
            NetworkHandler.METHOD_GET,
            lastLoginDateListener,
            finalHeaders
        )
        networkHandler.setSilentMode(true)
        networkHandler.execute()
    }

    private val lastLoginDateListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {
            Util.trace("last login first :  " + response.first)
           // Util.trace("course basic info second :  " + response.second)
            // TODO handle course detail api response

            if (response.first == NetworkConstants.SUCCESS) {
                val gson = Gson()
//                val courseBasicInfoBean = gson.fromJson<CourseBasicInfoBean>(
//                    response.second.toString(),
//                    CourseBasicInfoBean::class.java
//                )
            } else {
                DialogUtils.showGenericErrorDialog(this@CourseRoomMenuActivity)
            }
        }
    }


    fun getCourseDetail(): Int? {
        return if (courseDetailBean == null) {
            0
        } else {
            1
        }
    }


    fun getIsFp1_0(): Boolean {
        val isFp = !courseData?.flexpath2!! && courseData?.flexpathCourse!!
        return isFp
    }

    fun getIsGp1_0(): Boolean {
        val isGp = !courseData?.flexpath2!! && !courseData?.flexpathCourse!!
        return isGp
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
                callApiToGetCourseDetail()
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
    private fun init() {
        ConnectivityReceiver.connectivityReceiverListener = this
        connectivityReceiver = ConnectivityReceiver(networkLayout)
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(connectivityReceiver, intentFilter)
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
        init()
        callApiToGetCourseDetail()
    }

    private fun openInstructorProfile() {
        OmnitureTrack.trackAction("course:instructor-profile")
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        var matesDetailIntent = Intent(this, ClassmateDetailActivity::class.java)
        matesDetailIntent.putExtra(
            NetworkConstants.EMP_ID,
            courseData?.courseSection?.instructor?.employeeId
        )
        matesDetailIntent.putExtra(Constants.TITLE, resources.getString(R.string.instructor))
        matesDetailIntent.putExtra(
            NetworkConstants.PROFILE_IMAGE_URL,
            courseData?.courseSection?.instructor?.profileImage!!
        )
        startActivity(matesDetailIntent)
    }

    private fun shouldShowBlueDotOnAnnouncement(): Boolean {

        var shouldShow: Boolean = false
        try {
            if(courseDetailBean!=null && courseDetailBean?.newCourseroomData!=null && courseDetailBean?.newCourseroomData?.courseDetails?.courseAnnouncements!=null) {
                for (index in 0 until courseDetailBean?.newCourseroomData?.courseDetails?.courseAnnouncements?.size!!) {

                    val difference =
                        System.currentTimeMillis() - courseDetailBean?.newCourseroomData?.courseDetails?.courseAnnouncements!![index].startDate!!

                    val differenceInHours = TimeUnit.MILLISECONDS.toHours(difference)

                    if (Util.getDifferenceBetweenDateIsMoreThan72Hours(courseDetailBean?.newCourseroomData?.courseDetails?.courseAnnouncements!![index].startDate!!)) {
                        if (courseDetailBean?.newCourseroomData?.courseDetails?.courseAnnouncements!![index].getRead().toString().trim() == Constants.UNREAD.trim()) {
                            shouldShow = true
                            break
                        } else {
                            shouldShow = false
                        }
                    } else {
                        shouldShow = false
                    }
                }
            }
        }catch (t: Throwable)
        {
                Util.trace("issue with shouldShowBlueDotOnAnnouncement $t")
                t.printStackTrace()
        }
        return shouldShow

    }

    private fun shouldShowBlueDotOnAssessmentLearner(list: ArrayList<AssessmentLearnerBean.FlexpathAssessmentsAndStatus>): Boolean {

        var shouldShow: Boolean = false
//      var assesslist = courseDetailBean?.newCourseroomData?.courseDetails?.courseAssignmentNotifications!!
        for (index in 0 until list.size!!) {

            if (list[index].pccpEvaluatedDate != null) {


                var longg = Util.getDateToLong(
                    list[index].pccpEvaluatedDate!!
                )

                if (Util.getDifferenceBetweenDateIsMoreThan72Hours(longg!!)) {
                    if (list[index].getRead().toString().trim() == Constants.UNREAD.trim()) {
                        shouldShow = true
                        break
                    } else {
                        shouldShow = false
                    }
                } else {
                    shouldShow = false
                }
            }
             else if (list[index].pccpSubmittedDate != null) {


                var longg = Util.getDateToLong(
                    list[index].pccpSubmittedDate!!
                )

                if (Util.getDifferenceBetweenDateIsMoreThan72Hours(longg!!)) {
                    if (list[index].getRead().toString().trim() == Constants.UNREAD.trim()) {
                        shouldShow = true
                        break
                    } else {
                        shouldShow = false
                    }
                } else {
                    shouldShow = false
                }
            }

        }
        return shouldShow

    }

    private fun isStudent(): Boolean {
        try {
            var isFaculty: Boolean? = courseData?.faculty

            var isLearner: Boolean? = courseData?.enrolled;
            var student = false;

            if (isLearner!! && !isFaculty!!) {
                student = true;
            }

            return student
        } catch (t: Throwable) {
            Util.trace("student error= $t")
            t.printStackTrace()
        }
        return false
    }

    private var shouldScrollToWeek = false
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.ACTIVITY_REQUEST) {
            if (data != null) {
                courseDetailBean =
                    data.getSerializableExtra(Constants.ANNOUCEMENT_LIST_DATA) as CourseDetailBean
                setScreenView()

            }

        }else if(requestCode == Constants.YELLOW_DIG_SCROLLING && resultCode == Activity.RESULT_OK){
            shouldScrollToWeek = true
//            courseRoomScrollView.scrollTo(0, assignmentRecyclerLayout.top)
        }

    }




    private fun setAlertValue() {
        try {

        if (isStudent()) {

            if (courseDetailBean != null) {


                if (checkFpFlex == Constants.FP1) {
                    OmnitureTrack.trackAction("course:flexpath:engagement-needed")
                    val academicEngagement =
                        courseDetailBean!!.newCourseroomData!!.courseDetails!!.academicEngagementAlert

//                As we comment we reqired removed that last posting date checked condition
//                 if (academicEngagement.lastPostingDate != null && !academicEngagement.graded && !academicEngagement.hasPCCPActivity && !academicEngagement.hasPostingActivity) {

                    if (!academicEngagement!!.graded!! && !academicEngagement.hasPCCPActivity!! && !academicEngagement.hasPostingActivity!!) {
                        engagement_verification_layout.visibility = View.VISIBLE
                        val fontMedium =
                            Typeface.createFromAsset(assets, "fonts/roboto_medium.ttf")
                        var stringFirst =
                            resources.getString(R.string.your_last_recorded_activity) + " " + academicEngagement.lastPostingDate + ". " + resources.getString(
                                R.string.please_submit_an_assessment
                            )

//                        var stringFirst =
//                            resources.getString(R.string.your_last_recorded_activity) + " " + "7/23/2020" + ". " + resources.getString(
//                                R.string.please_submit_an_assessment
//                            )


                        var stringSecond = resources.getString(R.string.update_your_progress)

//                        val spannable = SpannableString(stringFirst + " " + stringSecond)

                        val spannable =  SpannableStringBuilder(stringFirst+" "+stringSecond)

                        val clickableSpan = object : ClickableSpan() {
                            override fun onClick(p0: View) {

//                                val link =
//                                    courseDetailBean!!.newCourseroomData!!.courseDetails!!.courseLink.toString()

                                val coursID =
                                    courseDetailBean!!.newCourseroomData!!.courseDetails!!.messageLink!!.split("=")[2]
                                val preString = Constants.EGAGEMENT_PRE_STRING
                                val postString = Constants.ENGAGEMENT_POST_STRING
                                val link = preString + coursID + postString

                                if (link != null) {
                                    DialogUtils.screenNamePrefix="engagement-alert:link-out"
                                    val stickyWork = StickyInfoGrabber(this@CourseRoomMenuActivity)
                                    stickyWork.generateMuleSoftStickySessionForTargetUrl(
                                        link,
                                        BuildConfig.STICKY_FORWARD_URL
                                    )

                                    OmnitureTrack.trackAction("course:flexpath:engagement-needed:progress-linkout")

                                }
//                         Util.openBrowserWithConfirmationPopup(this@CourseRoomMenuActivity, link!!)
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                super.updateDrawState(ds)

                                if (alertTxt.isPressed()) {
                                    ds.setColor(
                                        ContextCompat.getColor(
                                            this@CourseRoomMenuActivity,
                                            R.color.blue_900
                                        )
                                    );
                                } else {
                                    ds.setColor(
                                        ContextCompat.getColor(
                                            this@CourseRoomMenuActivity,
                                            R.color.checkBoxColor
                                        )
                                    );
                                }
                                alertTxt.invalidate();

//                                ds.setColor(
//                                    ContextCompat.getColor(
//                                        this@CourseRoomMenuActivity,
//                                        R.color.checkBoxColor
//                                    )
//                                );
                                ds.setUnderlineText(false);

                            }
                        }

                        alertTxt.setHighlightColor(Color.TRANSPARENT);

                        spannable.setSpan(
                            clickableSpan,
                            stringFirst!!.length,
                            stringFirst!!.length + stringSecond!!.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        var indexValue=0
                        if(academicEngagement.lastPostingDate==null)
                        {
                            indexValue=55
                        }else
                        {
                            indexValue=60
                        }
                        spannable.setSpan( CustomTypefaceSpan("fonts/roboto_medium.ttf", fontMedium), indexValue,stringFirst!!.length + stringSecond!!.length ,
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                        alertTxt.text = spannable
                        alertTxt.setMovementMethod(LinkMovementMethod.getInstance());

                        ViewCompat.enableAccessibleClickableSpanSupport(alertTxt);

//                    alertHeaderTxt.contentDescription=resources.getString(R.string.ada_academic_engagement_needed)
                        OmnitureTrack.trackAction("course:flexpath:engagement-needed")

                        if(isAdaEnabled(this@CourseRoomMenuActivity))
                        {
                            alertTxt.setOnClickListener {


                                val coursID =
                                    courseDetailBean!!.newCourseroomData!!.courseDetails!!.messageLink!!.split("=")[2]
                                val preString = Constants.EGAGEMENT_PRE_STRING
                                val postString = Constants.ENGAGEMENT_POST_STRING
                                val link = preString + coursID + postString

                                if (link != null) {
                                    DialogUtils.screenNamePrefix="engagement-alert:link-out"
                                    val stickyWork = StickyInfoGrabber(this@CourseRoomMenuActivity)
                                    stickyWork.generateMuleSoftStickySessionForTargetUrl(
                                        link,
                                        BuildConfig.STICKY_FORWARD_URL
                                    )

                                    OmnitureTrack.trackAction("course:flexpath:engagement-needed:progress-linkout")

                                }
                            }
                        }
                        else
                        {
                            alertTxt.setOnClickListener(null)
                        }

                        val headerString=resources.getString(R.string.academic_engagement)
                        val adaText=headerString+" "+stringFirst+" "+stringSecond

                        Util.announceAda(adaText, this@CourseRoomMenuActivity)


                    }
                    else if (academicEngagement?.hasPCCPActivity!! && !academicEngagement?.hasPostingActivity!!) {
                        engagement_verification_layout.visibility = View.VISIBLE
                        alertHeaderTxt.text =
                            resources.getString(R.string.academic_engagement_verfication)
                        alertTxt.text = resources.getString(R.string.we_have_received)
                        alertTxt.setTextColor(
                            ContextCompat.getColor(
                                this@CourseRoomMenuActivity,
                                R.color.text_gray_900
                            )
                        )
                        OmnitureTrack.trackAction("course:flexpath:engagement-pending")

                        val adaText=resources.getString(R.string.academic_engagement_verfication)+" "+resources.getString(R.string.we_have_received)
                        Util.announceAda(adaText, this@CourseRoomMenuActivity)
//                    alertHeaderTxt.contentDescription=resources.getString(R.string.ada_academic_verification_pending)
                    }
                    else
                    {
                        engagement_verification_layout.visibility = View.GONE
                    }

                } else if (checkFpFlex == Constants.FP2) {

                    val academicEngagement =
                        courseDetailBean!!.newCourseroomData!!.courseDetails!!.courseActivity?.learnerActivity?.get(0)?.lastAcademicEngagement

                    if (academicEngagement != null) {
                        if (Util.getDateAgo(academicEngagement.toString()) > 14) {
                            engagement_verification_layout.visibility = View.VISIBLE
                            alertTxt.text = resources.getString(R.string.complete_assessment)
                            alertTxt.setTextColor(
                                ContextCompat.getColor(
                                    this@CourseRoomMenuActivity,
                                    R.color.text_gray_900
                                )
                            )
                            OmnitureTrack.trackAction("course:flexpath:engagement-needed")
                            val adaText=resources.getString(R.string.academic_engagement)+" "+resources.getString(R.string.complete_assessment)
                            Util.announceAda(adaText, this@CourseRoomMenuActivity)
//                        alertHeaderTxt.contentDescription=resources.getString(R.string.ada_academic_engagement_needed_please_complete)
                        }
                    }
                    else
                    {
                        engagement_verification_layout.visibility = View.GONE
                    }

                }
            }
            else
            {
                engagement_verification_layout.visibility = View.GONE
            }
        }
        }catch (e:Exception)
        {
            e.printStackTrace()
        }

    }







    private fun checkBlueDotAssessementFaculty(statusFacultyList: ArrayList<AssesstmentFacultyBean.CourseAssignment>): Boolean {

        for (valueList in statusFacultyList) {
            if (Util.getDateAgo(valueList.submittedDateTime!!) > 3) {
                return true
            }
            else
            {
              if(valueList.getRead()==Constants.READ)
              {
                  return true
              }
                else
              {
                  return false
                  break
              }

            }
        }
        return false
    }

    private fun callAssessmentFacultyApi(courseId: String) {
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val authHeader = NetworkHandler.getAuthorizationHeader()
        val hubbleHeaders = NetworkHandler.getHeadersForHubbleRequest(
            loginBean?.authData?.token,
            HubbleNetworkConstants.getAcceptHeaderValue(HubbleNetworkConstants.ASSESSMENT_FACULTY_LIST)
        )
        val finalHeaders = NetworkHandler.meargeHeaders(authHeader, hubbleHeaders)

        val params = HashMap<String, Any>()

        //val stickyHeader : java.util.HashMap<String, Any?>? = NetworkHandler.getStickySessionHeader(
        //   Preferences.getValue(PreferenceKeys.MULESOFT_ACCESS_TOKEN))

        var assignmentsUrl = HubbleNetworkConstants.getUrl(
            HubbleNetworkConstants.ASSESSMENT_FACULTY_LIST, "{{employeeId}}",
            "" + loginBean?.authData?.employeeId!!.value
        )

        val assignmentsListUrl =
            HubbleNetworkConstants.getUrl(assignmentsUrl, "{{courseId}}", "" + courseId)
        Util.trace("Assignments URL  :" + assignmentsUrl)


        Util.trace("Assignments Url", assignmentsListUrl)

        val networkHandler = NetworkHandler(
            this,
            assignmentsListUrl,
            params,
            NetworkHandler.METHOD_GET,
            assesstmentFacultyNetworkListener,
            finalHeaders
        )

        networkHandler.setSilentMode(true)

        networkHandler.execute()

    }

    private val assesstmentFacultyNetworkListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: android.util.Pair<String, Any>) {
            try {


           // Util.trace("Assignments : " + response.second)

            if (response.first == NetworkConstants.SUCCESS) {
                val gson = Gson()
                val assignmentsBean = gson.fromJson<AssesstmentFacultyBean>(
                    response.second.toString(),
                    AssesstmentFacultyBean::class.java
                )
                updateAssessmentList(assignmentsBean)

            } else {
                DialogUtils.showGenericErrorDialog(this@CourseRoomMenuActivity)
            }
            }catch (e:Exception)
            {
                e.printStackTrace()
            }
        }
    }

    /*override fun onNetworkResponse(response: Pair<String, Any>) {
        Util.trace("Assignments : " + response.second)

        if (response.first == NetworkConstants.SUCCESS) {
            val gson = Gson()
            val assignmentsBean = gson.fromJson<AssesstmentFacultyBean>(
                response.second.toString(),
                AssesstmentFacultyBean::class.java
            )
            updateAssessmentList(assignmentsBean)

        } else {
            DialogUtils.showGenericErrorDialog(this)
        }
    }*/

    private fun updateAssessmentList(assignmentsBean: AssesstmentFacultyBean?) {

        try {
            assignmentsBean

            var listNew =
                assignmentsBean?.courseAssignment as ArrayList<AssesstmentFacultyBean.CourseAssignment>
            assessmentFacultyList.clear()
            assessmentFacultyList.addAll(listNew)

            if (courseDetailBean != null) {

              val  responselist=assessmentFacultyList

                if (responselist.isNotEmpty()) {


                    var SHARED_COURSE_ID =
                        courseDetailBean?.newCourseroomData?.courseDetails?.courseIdentifier!!.toString() + "" + userEmployeeId

                    val SHARRED_INTRUCTOR_DATE =
                        SHARED_COURSE_ID + "" + PreferenceKeys.FP_INTRUCTOR_FACULTY_DATE
                    val SHARRED_INTRUCTOR_READ =
                        SHARED_COURSE_ID + "" + PreferenceKeys.FP_INTRUCTOR_FACULTY_READ


                    var Datelist =
                        Util.stringToWords(Preferences.getArrayList(SHARRED_INTRUCTOR_DATE)!!)

                    var fpLearnerDatelist: ArrayList<String>? = null
                    if (Datelist.isEmpty()) {
                        fpLearnerDatelist = ArrayList<String>()
                    } else {
                        fpLearnerDatelist = Datelist as ArrayList
                    }

                    var Readlist =
                        Util.stringToWords(Preferences.getArrayList(SHARRED_INTRUCTOR_READ)!!)


                    var fpLearnerReadlist: ArrayList<String>? = null

                    if (Readlist.isEmpty()) {
                        fpLearnerReadlist = ArrayList<String>()
                    } else {
                        fpLearnerReadlist = Readlist as ArrayList
                    }

                    if (fpLearnerDatelist.size > 0) {

                        var firstDate = fpLearnerDatelist[0]
                        var lastDate = fpLearnerDatelist[fpLearnerDatelist.size - 1]

                        firstDate = firstDate.replace("[", "")
                        lastDate = lastDate.replace("]", "")
                        lastDate = lastDate.replace("[", "")

                        fpLearnerDatelist.set(0, firstDate)
                        fpLearnerDatelist.set(fpLearnerDatelist.size - 1, lastDate)
                    }

                    if (fpLearnerReadlist!!.size > 0) {
                        var firstRead = fpLearnerReadlist?.get(0)!!
                        var lastRead = fpLearnerReadlist[fpLearnerReadlist.size - 1]


                        firstRead = firstRead.replace("[", "")
                        lastRead = lastRead.replace("]", "")
                        lastRead = lastRead.replace("[", "")

                        fpLearnerReadlist[0] = firstRead
                        fpLearnerReadlist[fpLearnerReadlist.size - 1] = lastRead

                    }

                    val learnerlistDate = ArrayList<String>()
                    val learnerlistRead = ArrayList<String>()

                    var difference = responselist.size - fpLearnerDatelist.size

                    for (i in responselist.indices) {
                        var date: String = ""

                        if (responselist[i].submittedDateTime != null) {
                            var longg = Util.getDateToLong(
                                responselist[i].submittedDateTime!!
                            )


                            date = Util.getDate(longg!!, Constants.DATE_FORMAT_SEC)!!
                        }
                        if (responselist.size > i + difference) {
                            if (responselist[i + difference].submittedDateTime != null) {

                                var longgDate = Util.getDateToLong(
                                    responselist[i + difference].submittedDateTime!!
                                )
                                var checkdate =
                                    Util.getDate(
                                        longgDate!!,
                                        Constants.DATE_FORMAT_SEC
                                    )!!

                                if (fpLearnerDatelist!!.size > 0) {
//
                                    if (fpLearnerDatelist!!.size > i) {
                                        if ((fpLearnerDatelist[i].trim()) == checkdate!!.trim()) {

                                            if ((fpLearnerReadlist[i].trim()) == Constants.READ.trim()) {
                                                responselist[i + difference].setRead(Constants.READ)
                                            }
                                        }
                                    }

                                }
                            }
                        }
                        learnerlistDate.add(date)
                        learnerlistRead.add(responselist[i].getRead()!!.toString().trim())
                    }
                    Preferences.addArrayList(
                        SHARRED_INTRUCTOR_DATE,
                        learnerlistDate.toString()
                    )
                    Preferences.addArrayList(
                        SHARRED_INTRUCTOR_READ,
                        learnerlistRead.toString()
                    )



                }
            }

            if (!isStudent()) {
                if(assessmentFacultyList.size>0) {
                    if (checkBlueDotAssessementFaculty(assessmentFacultyList)) {

                        gradeAndStatusLayout.contentDescription =
                            resources.getString(R.string.ada_sssessments_and_status)
                        gradeAndStatusLayout.course_item_icon.visibility = View.INVISIBLE
                        gradeAndStatusLayout.header.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            0,
                            0
                        )
                        gradeAndStatusLayout.header.compoundDrawablePadding = 0
                        gradeAndStatusLayout.description.setPadding(0, 0, 0, 0)
                        gradeAndStatusLayout.dotContainer.visibility = View.VISIBLE
                    } else {

                        gradeAndStatusLayout.course_item_icon.visibility = View.GONE

                        gradeAndStatusLayout.dotContainer.visibility = View.GONE
                        gradeAndStatusLayout.header.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_blue_dot,
                            0,
                            0,
                            0
                        )
                        gradeAndStatusLayout.header.compoundDrawablePadding =
                            resources.getDimension(R.dimen._10dp).toInt()
                        gradeAndStatusLayout.description.setPadding(
                            resources.getDimension(R.dimen._20dp).toInt(),
                            0,
                            0,
                            0
                        )

                        gradeAndStatusLayout.contentDescription =
                            resources.getString(R.string.ada_sssessments_and_status) + " " + resources.getString(
                                R.string.ada_new_updated_three_days
                            )


                    }
                }

            }

        } catch (t: Throwable) {
            Util.trace("Assignments error : $t")
            t.printStackTrace()
        }
    }


    private fun callApiToGetAssessments() {

        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val params = HashMap<String, Any>()
        params[NetworkConstants.TOKEN] = loginBean?.authData?.token!!

        val qStringParams = HashMap<String, Any>()
        qStringParams[NetworkConstants.COURSE_ID] =
            courseDetailBean?.newCourseroomData?.courseDetails?.courseIdentifier!!.toString().trim()
        qStringParams[NetworkConstants.ACTION] = NetworkConstants.ACTION_FLEX_PATH_ASSESSMENT

        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.FLEX_PATH_ASSESSMENT_LIST + NetworkService.getQueryString(qStringParams),
            params,
            NetworkHandler.METHOD_POST,
            assessmentListListener,
            null
        )

        networkHandler.setSilentMode(true)
        networkHandler.execute()
    }

    private val assessmentListListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {
            try {

                Util.trace("flex path assessment first :  " + response.first)
                //Util.trace("flex path assessment second :  " + response.second)
                // TODO handle course detail api responseset
                if (response.first == NetworkConstants.SUCCESS) {
                    val gson = Gson()
                    val assessmentLearnerBean = gson.fromJson<AssessmentLearnerBean>(
                        response.second.toString(),
                        AssessmentLearnerBean::class.java
                    )

                    assessmentLearnerList!!.clear()

                    assessmentLearnerBean?.flexpathAssessmentsAndStatusResponse?.flexpathAssessmentsAndStatuss?.let {
                        assessmentLearnerList?.addAll(
                            it
                        )
                    }
                    assessmentLearnerList?.sortedWith(compareBy(AssessmentLearnerBean.FlexpathAssessmentsAndStatus::assignmentTitle))

                    saveFacultyLearner(assessmentLearnerList!!)

                } else {
//                    DialogUtils.showGenericErrorDialog(this@AssessmentForLearnerActivity)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveFacultyLearner(responselist: ArrayList<AssessmentLearnerBean.FlexpathAssessmentsAndStatus>) {
        try {

            if (courseDetailBean != null) {

                if (responselist.isNotEmpty()) {

                    var SHARED_COURSE_ID =
                        courseDetailBean?.newCourseroomData?.courseDetails?.courseIdentifier!!.toString() + "" + userEmployeeId
                    val SHARRED_LEARNER_DATE =
                        SHARED_COURSE_ID + "" + PreferenceKeys.FP_LEARNER_FACULTY_DATE
                    val SHARRED_LEARNER_READ =
                        SHARED_COURSE_ID + "" + PreferenceKeys.FP_LEARNER_FACULTY_READ


                    var Datelist =
                        Util.stringToWords(Preferences.getArrayList(SHARRED_LEARNER_DATE)!!)

                    var fpLearnerDatelist: ArrayList<String>? = null
                    if (Datelist.isEmpty()) {
                        fpLearnerDatelist = ArrayList<String>()
                    } else {
                        fpLearnerDatelist = Datelist as ArrayList
                    }

                    var Readlist =
                        Util.stringToWords(Preferences.getArrayList(SHARRED_LEARNER_READ)!!)


                    var fpLearnerReadlist: ArrayList<String>? = null

                    if (Readlist.isEmpty()) {
                        fpLearnerReadlist = ArrayList<String>()
                    } else {
                        fpLearnerReadlist = Readlist as ArrayList
                    }

                    if (fpLearnerDatelist.size > 0) {

                        var firstDate = fpLearnerDatelist[0]
                        var lastDate = fpLearnerDatelist[fpLearnerDatelist.size - 1]

                        firstDate = firstDate.replace("[", "")
                        lastDate = lastDate.replace("]", "")
                        lastDate = lastDate.replace("[", "")

                        fpLearnerDatelist.set(0, firstDate)
                        fpLearnerDatelist.set(fpLearnerDatelist.size - 1, lastDate)
                    }

                    if (fpLearnerReadlist!!.size > 0) {
                        var firstRead = fpLearnerReadlist?.get(0)!!
                        var lastRead = fpLearnerReadlist[fpLearnerReadlist.size - 1]


                        firstRead = firstRead.replace("[", "")
                        lastRead = lastRead.replace("]", "")
                        lastRead = lastRead.replace("[", "")

                        fpLearnerReadlist[0] = firstRead
                        fpLearnerReadlist[fpLearnerReadlist.size - 1] = lastRead

                    }

                    val learnerlistDate = ArrayList<String>()
                    val learnerlistRead = ArrayList<String>()

                    var difference = responselist.size - fpLearnerDatelist.size

                    for (i in responselist.indices) {
                        var date: String = ""

                        if (responselist[i].pccpEvaluatedDate != null) {
                            var longg = Util.getDateToLong(
                                responselist[i].pccpEvaluatedDate!!
                            )


//                            date = Util.getDate(longg!!, Constants.DATE_FORMAT_SEC)!!
                            date=responselist[i].id.toString()
                        }
                        else if (responselist[i].pccpSubmittedDate != null)
                        {
                            var longg = Util.getDateToLong(
                                responselist[i].pccpSubmittedDate!!
                            )


//                            date = Util.getDate(longg!!, Constants.DATE_FORMAT_SEC)!!
                            date=responselist[i].id.toString()
                        }
                        if (responselist.size > i + difference) {

                            if (responselist[i + difference].pccpEvaluatedDate != null) {

//                                var longgDate = Util.getDateToLong(
//                                    responselist[i + difference].pccpEvaluatedDate!!
//                                )
//                                var checkdate =
//                                    Util.getDate(
//                                        longgDate!!,
//                                        Constants.DATE_FORMAT_SEC
//                                    )!!


                                if (fpLearnerDatelist!!.size > 0) {
//
                                    if (fpLearnerDatelist!!.size > i) {


                                        for( serverListValue in responselist)
                                        {

                                            if(serverListValue.pccpEvaluatedDate!=null)
                                            {

                                                var longgDate = Util.getDateToLong(
                                                    serverListValue.pccpEvaluatedDate!!
                                                )
                                                var checkdateServer =
                                                    Util.getDate(
                                                        longgDate!!,
                                                        Constants.DATE_FORMAT_SEC
                                                    )!!

                                                checkdateServer=serverListValue.id!!
                                                if ((fpLearnerDatelist[i].trim()) == checkdateServer.trim()) {

                                                    if ((fpLearnerReadlist[i].trim()) == Constants.READ.trim()) {
                                                        serverListValue.setRead(Constants.READ)
                                                    }
                                                }

                                                if(serverListValue.attempts!=null && serverListValue!!.attempts!!.attempts!=null)
                                                {



                                                    val localLearnerAttempsRead = ArrayList<String>()

                                                    val localLearnerAttempsDate = ArrayList<String>()

                                                    val SHARRED_ATTEMP_READ =
                                                        SHARED_COURSE_ID + "" + checkdateServer + "" + PreferenceKeys.FP_LEARNER_ATTEMP_READ

                                                    val SHARRED_ATTEMP_DATE =
                                                        SHARED_COURSE_ID + "" + checkdateServer + "" + PreferenceKeys.FP_LEARNER_ATTEMP_DATA

                                                    var attempListRead =
                                                        Util.stringToWords(
                                                            Preferences.getArrayList(
                                                                SHARRED_ATTEMP_READ
                                                            )!!
                                                        )


                                                    var fpLearnerAttemplist: ArrayList<String>? = null
                                                    if (attempListRead.isEmpty()) {
                                                        fpLearnerAttemplist = ArrayList<String>()
                                                    } else {
                                                        fpLearnerAttemplist = attempListRead as ArrayList
                                                    }

                                                    if (fpLearnerAttemplist!!.size > 0) {
                                                        var firstRead = fpLearnerAttemplist?.get(0)!!
                                                        var lastRead =
                                                            fpLearnerAttemplist[fpLearnerAttemplist.size - 1]

                                                        firstRead = firstRead.replace("[", "")
                                                        lastRead = lastRead.replace("]", "")
                                                        lastRead = lastRead.replace("[", "")

                                                        fpLearnerAttemplist[0] = firstRead
                                                        fpLearnerAttemplist[fpLearnerAttemplist.size - 1] =
                                                            lastRead
                                                    }

                                                    var attempListDate =
                                                        Util.stringToWords(
                                                            Preferences.getArrayList(
                                                                SHARRED_ATTEMP_DATE
                                                            )!!
                                                        )


                                                    var fpDateAttemplist: ArrayList<String>? = null
                                                    if (attempListDate.isEmpty()) {
                                                        fpDateAttemplist = ArrayList<String>()
                                                    } else {
                                                        fpDateAttemplist = attempListDate as ArrayList
                                                    }

                                                    if (fpDateAttemplist!!.size > 0) {
                                                        var firstDate = fpDateAttemplist?.get(0)!!
                                                        var lastDate =
                                                            fpDateAttemplist[fpDateAttemplist.size - 1]

                                                        firstDate = firstDate.replace("[", "")
                                                        lastDate = lastDate.replace("]", "")
                                                        lastDate = lastDate.replace("[", "")

                                                        fpDateAttemplist[0] = firstDate
                                                        fpDateAttemplist[fpDateAttemplist.size - 1] =
                                                            lastDate
                                                    }


                                                    val serverAttempsList =
                                                        serverListValue.attempts!!.attempts

                                                    var attempsDifference =
                                                        serverAttempsList!!.size - fpLearnerAttemplist.size

                                                    if(attempsDifference>=0) {
                                                        for (m in serverAttempsList.indices) {

                                                            var serverSatusDate=""


                                                            if(serverAttempsList[m].status.equals(Constants.GRADED))
                                                            {
                                                                var longgDate = Util.getDateToLong(
                                                                    serverAttempsList[m].gradedDate!!
                                                                )
                                                                serverSatusDate  =
                                                                    Util.getDate(
                                                                        longgDate!!,
                                                                        Constants.DATE_FORMAT_SEC
                                                                    )!!
                                                            }
                                                            else if(serverAttempsList[m].status.equals(Constants.SUBMITTED))
                                                            {
                                                                var longgDate = Util.getDateToLong(
                                                                    serverAttempsList[m].submittedDate!!
                                                                )
                                                                serverSatusDate  =
                                                                    Util.getDate(
                                                                        longgDate!!,
                                                                        Constants.DATE_FORMAT_SEC
                                                                    )!!

                                                            }

                                                            if (serverAttempsList.size > m + attempsDifference) {

                                                                if(serverSatusDate.toString().trim() == fpDateAttemplist[m].toString().trim()) {
                                                                    if (fpLearnerAttemplist[m].trim() == Constants.READ.trim()) {
                                                                        serverListValue.setRead(
                                                                            Constants.READ
                                                                        )
                                                                        serverAttempsList[m].setRead(
                                                                            Constants.READ
                                                                        )

                                                                    } else {
                                                                        serverListValue.setRead(
                                                                            Constants.UNREAD
                                                                        )


                                                                    }
                                                                }else {
                                                                    serverListValue.setRead(
                                                                        Constants.UNREAD
                                                                    )


                                                                }

                                                            }

                                                            localLearnerAttempsRead.add(
                                                                serverAttempsList[m].getRead()!!
                                                            )
                                                            localLearnerAttempsDate.add(serverSatusDate)


                                                        }
                                                    }


                                                    if (attempsDifference != 0) {
                                                        serverListValue.setRead(Constants.UNREAD)
                                                    }
                                                    if(localLearnerAttempsRead.contains(Constants.UNREAD))
                                                    {
                                                        serverListValue.setRead(
                                                            Constants.UNREAD
                                                        )
                                                    }

                                                    Preferences.addArrayList(
                                                        SHARRED_ATTEMP_READ,
                                                        localLearnerAttempsRead.toString()
                                                    )

                                                    Preferences.addArrayList(
                                                        SHARRED_ATTEMP_DATE,
                                                        localLearnerAttempsDate.toString()
                                                    )

                                                }
                                            }

                                        }
                                    }

                                }
                            }

                            else if (responselist[i + difference].pccpSubmittedDate != null) {

//                                var longgDate = Util.getDateToLong(
//                                    responselist[i + difference].pccpSubmittedDate!!
//                                )
//                                var checkdate =
//                                    Util.getDate(
//                                        longgDate!!,
//                                        Constants.DATE_FORMAT_SEC
//                                    )!!


                                if (fpLearnerDatelist!!.size > 0) {
//
                                    if (fpLearnerDatelist!!.size > i) {

                                        for( serverListValue in responselist) {

                                            if (serverListValue.pccpSubmittedDate != null) {

                                                var longgDate = Util.getDateToLong(
                                                    serverListValue.pccpSubmittedDate!!
                                                )
//                                                    var checkdateServer =
//                                                        Util.getDate(
//                                                            longgDate!!,
//                                                            Constants.DATE_FORMAT_SEC
//                                                        )!!

                                                var checkdateServer=serverListValue.id!!
                                                if ((fpLearnerDatelist[i].trim()) == checkdateServer.trim()) {

                                                    if ((fpLearnerReadlist[i].trim()) == Constants.READ.trim()) {
                                                        serverListValue.setRead(Constants.READ)
                                                    }
                                                }

                                                if(serverListValue.attempts!=null && serverListValue!!.attempts!!.attempts!=null) {


                                                    val localLearnerAttempsRead =
                                                        ArrayList<String>()

                                                    val localLearnerAttempsDate =
                                                        ArrayList<String>()

                                                    val SHARRED_ATTEMP_READ =
                                                        SHARED_COURSE_ID + "" + checkdateServer + "" + PreferenceKeys.FP_LEARNER_ATTEMP_READ

                                                    val SHARRED_ATTEMP_DATE =
                                                        SHARED_COURSE_ID + "" + checkdateServer + "" + PreferenceKeys.FP_LEARNER_ATTEMP_DATA



                                                    var attempListRead =
                                                        Util.stringToWords(
                                                            Preferences.getArrayList(
                                                                SHARRED_ATTEMP_READ
                                                            )!!
                                                        )


                                                    var fpLearnerAttemplist: ArrayList<String>? =
                                                        null
                                                    if (attempListRead.isEmpty()) {
                                                        fpLearnerAttemplist =
                                                            ArrayList<String>()
                                                    } else {
                                                        fpLearnerAttemplist =
                                                            attempListRead as ArrayList
                                                    }

                                                    if (fpLearnerAttemplist!!.size > 0) {
                                                        var firstRead =
                                                            fpLearnerAttemplist?.get(0)!!
                                                        var lastRead =
                                                            fpLearnerAttemplist[fpLearnerAttemplist.size - 1]

                                                        firstRead = firstRead.replace("[", "")
                                                        lastRead = lastRead.replace("]", "")
                                                        lastRead = lastRead.replace("[", "")

                                                        fpLearnerAttemplist[0] = firstRead
                                                        fpLearnerAttemplist[fpLearnerAttemplist.size - 1] =
                                                            lastRead
                                                    }

                                                    var attempListDate =
                                                        Util.stringToWords(
                                                            Preferences.getArrayList(
                                                                SHARRED_ATTEMP_DATE
                                                            )!!
                                                        )

                                                    var fpDateAttemplist: ArrayList<String>? = null
                                                    if (attempListDate.isEmpty()) {
                                                        fpDateAttemplist = ArrayList<String>()
                                                    } else {
                                                        fpDateAttemplist = attempListDate as ArrayList
                                                    }

                                                    if (fpDateAttemplist!!.size > 0) {
                                                        var firstDate = fpDateAttemplist?.get(0)!!
                                                        var lastDate =
                                                            fpDateAttemplist[fpDateAttemplist.size - 1]

                                                        firstDate = firstDate.replace("[", "")
                                                        lastDate = lastDate.replace("]", "")
                                                        lastDate = lastDate.replace("[", "")

                                                        fpDateAttemplist[0] = firstDate
                                                        fpDateAttemplist[fpDateAttemplist.size - 1] =
                                                            lastDate
                                                    }

                                                    val serverAttempsList =
                                                        serverListValue.attempts!!.attempts

                                                    var attempsDifference =
                                                        serverAttempsList!!.size - fpLearnerAttemplist.size

                                                    if(attempsDifference>=0) {

                                                        for (m in serverAttempsList.indices) {

                                                            var serverSatusDate=""


                                                            if(serverAttempsList[m].status.equals(Constants.GRADED))
                                                            {
                                                                var longgDate = Util.getDateToLong(
                                                                    serverAttempsList[m].gradedDate!!
                                                                )
                                                                serverSatusDate  =
                                                                    Util.getDate(
                                                                        longgDate!!,
                                                                        Constants.DATE_FORMAT_SEC
                                                                    )!!
                                                            }
                                                            else if(serverAttempsList[m].status.equals(Constants.SUBMITTED))
                                                            {
                                                                var longgDate = Util.getDateToLong(
                                                                    serverAttempsList[m].submittedDate!!
                                                                )
                                                                serverSatusDate  =
                                                                    Util.getDate(
                                                                        longgDate!!,
                                                                        Constants.DATE_FORMAT_SEC
                                                                    )!!

                                                            }

                                                            if (serverAttempsList.size > m + attempsDifference) {
                                                                if(serverSatusDate.toString().trim() == fpDateAttemplist[m].toString().trim()) {
                                                                    if (fpLearnerAttemplist[m].trim() == Constants.READ.trim()) {
                                                                        serverListValue.setRead(
                                                                            Constants.READ
                                                                        )
                                                                        serverAttempsList[m].setRead(
                                                                            Constants.READ
                                                                        )

                                                                    } else {
                                                                        serverListValue.setRead(
                                                                            Constants.UNREAD
                                                                        )

                                                                    }
                                                                }
                                                                else {
                                                                    serverListValue.setRead(
                                                                        Constants.UNREAD
                                                                    )

                                                                }

                                                            }

                                                            localLearnerAttempsRead.add(
                                                                serverAttempsList[m].getRead()!!
                                                            )

                                                            localLearnerAttempsDate.add(serverSatusDate)

                                                        }
                                                    }

                                                    if (attempsDifference != 0) {
                                                        serverListValue.setRead(Constants.UNREAD)
                                                    }
                                                    if(localLearnerAttempsRead.contains(Constants.UNREAD))
                                                    {
                                                        serverListValue.setRead(
                                                            Constants.UNREAD
                                                        )
                                                    }


                                                    Preferences.addArrayList(
                                                        SHARRED_ATTEMP_READ,
                                                        localLearnerAttempsRead.toString()
                                                    )

                                                    Preferences.addArrayList(
                                                        SHARRED_ATTEMP_DATE,
                                                        localLearnerAttempsDate.toString()
                                                    )
                                                }

                                            }
                                        }
                                    }

                                }
                            }
                        }
                        learnerlistDate.add(date)
                        learnerlistRead.add(responselist[i].getRead()!!.toString().trim())
                    }

                    Preferences.addArrayList(
                        SHARRED_LEARNER_DATE,
                        learnerlistDate.toString()
                    )
                    Preferences.addArrayList(
                        SHARRED_LEARNER_READ,
                        learnerlistRead.toString()
                    )

                    if (shouldShowBlueDotOnAssessmentLearner(responselist)) {
// TODO need to check this with real data and then implement
                        gradeAndStatusLayout.contentDescription =
                            resources.getString(R.string.ada_sssessments_and_status) + " " + resources.getString(
                                R.string.ada_new_updated_three_days
                            )
                        gradeAndStatusLayout.course_item_icon.visibility = View.GONE
                        gradeAndStatusLayout.dotContainer.visibility = View.GONE
                        gradeAndStatusLayout.header.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_blue_dot,
                            0,
                            0,
                            0
                        )
                        gradeAndStatusLayout.header.compoundDrawablePadding =
                            resources.getDimension(R.dimen._10dp).toInt()
                        gradeAndStatusLayout.description.setPadding(
                            resources.getDimension(R.dimen._20dp).toInt(),
                            0,
                            0,
                            0
                        )
                    } else {
                        gradeAndStatusLayout.contentDescription =
                            resources.getString(R.string.ada_sssessments_and_status)
                        gradeAndStatusLayout.course_item_icon.visibility = View.INVISIBLE
                        gradeAndStatusLayout.header.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            0,
                            0
                        )
                        gradeAndStatusLayout.header.compoundDrawablePadding = 0
                        gradeAndStatusLayout.description.setPadding(0, 0, 0, 0)
                        gradeAndStatusLayout.dotContainer.visibility = View.VISIBLE
                    }

                }
            }

        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }

    }

    fun callGradesBlueDotHandlerForFaculty() {
        blueDotSpinner.visibility = View.VISIBLE
        var gradeBlueDotHandler = FacultyGradeBlueDotHandler(
            this,
            courseData?.courseIdentifier!!,
            object : GradeBlueDotListener {
                override fun onGradeBlueDot(isBlueDot: Boolean) {

                    showBlueDotForGrade(isBlueDot)

                    blueDotSpinner.visibility = View.GONE
                }

                override fun onGradeBlueDotFail(msg: String) {
                    blueDotSpinner.visibility = View.GONE
                }

            })

        gradeBlueDotHandler.start()
    }

    fun callGradesBlueDotHandlerForStudent() {
        blueDotSpinner.visibility = View.VISIBLE
        var gradeBlueDotHandler = StudentGradeBlueDotHandler(
            this,
            courseData?.courseIdentifier!!,
            object : GradeBlueDotListener {
                override fun onGradeBlueDot(isBlueDot: Boolean) {

                    showBlueDotForGrade(isBlueDot)

                    blueDotSpinner.visibility = View.GONE
                }

                override fun onGradeBlueDotFail(msg: String) {
                    blueDotSpinner.visibility = View.GONE
                }

            })

        gradeBlueDotHandler.start()
    }

    private fun showBlueDotForGrade(isVisible: Boolean) {
        previousBlueDotState = isVisible
        if (isVisible) {
            gradeAndStatusLayout.contentDescription =
                resources.getString(R.string.ada_grades_status) + " " + resources.getString(R.string.ada_new_updated_three_days)

            gradeAndStatusLayout.course_item_icon.visibility = View.GONE
            gradeAndStatusLayout.dotContainer.visibility = View.GONE

            gradeAndStatusLayout.header.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_blue_dot,
                0,
                0,
                0
            )
            gradeAndStatusLayout.header.compoundDrawablePadding =
                resources.getDimension(R.dimen._10dp).toInt()
            gradeAndStatusLayout.description.setPadding(
                resources.getDimension(R.dimen._20dp).toInt(),
                0,
                0,
                0
            )
        } else {
            gradeAndStatusLayout.contentDescription =
                resources.getString(R.string.ada_grades_status)
            gradeAndStatusLayout.course_item_icon.visibility = View.INVISIBLE

            gradeAndStatusLayout.header.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            gradeAndStatusLayout.header.compoundDrawablePadding = 0
            gradeAndStatusLayout.description.setPadding(0, 0, 0, 0)
            gradeAndStatusLayout.dotContainer.visibility = View.VISIBLE
        }
    }


    private fun showCachedBlueDotForGrade() {

        if (previousBlueDotState) {
            gradeAndStatusLayout.contentDescription =
                resources.getString(R.string.ada_grades_status) + " " + resources.getString(R.string.ada_new_updated_three_days)

            gradeAndStatusLayout.course_item_icon.visibility = View.GONE
            gradeAndStatusLayout.dotContainer.visibility = View.GONE

            gradeAndStatusLayout.header.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_blue_dot,
                0,
                0,
                0
            )
            gradeAndStatusLayout.header.compoundDrawablePadding =
                resources.getDimension(R.dimen._10dp).toInt()
            gradeAndStatusLayout.description.setPadding(
                resources.getDimension(R.dimen._20dp).toInt(),
                0,
                0,
                0
            )
        } else {
            gradeAndStatusLayout.contentDescription =
                resources.getString(R.string.ada_grades_status)
            gradeAndStatusLayout.course_item_icon.visibility = View.INVISIBLE

            gradeAndStatusLayout.header.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            gradeAndStatusLayout.header.compoundDrawablePadding = 0
            gradeAndStatusLayout.description.setPadding(0, 0, 0, 0)
            gradeAndStatusLayout.dotContainer.visibility = View.VISIBLE
        }
    }

    private fun checkAndCallGp2APi() {
        val mulesoftToken = Preferences.getValue(PreferenceKeys.MULESOFT_ACCESS_TOKEN)
        if (mulesoftToken != "") {
                callLeftNavigationApi(mulesoftToken!!)
        } else {
            val stickyInfoToGetMuleSoftToken = StickyInfoToGetMuleSoftToken(this,
                object : StickyInfoToGetMuleSoftToken.AccessTokenListener {
                    override fun onTokenGetListener(token: String) {
                        if (token != "") {
                            callLeftNavigationApi(token)
                        } else {

                            // mulesoft token failed , on the basis of westworld param show UI
                            if(courseData?.westworld != null){
                                setGpUi(leftNavigationItem!!,true)
                            }else{
                                setUiForGuidedPath1_0()
                                attachClickListenerForGP1_0()
                            }
                        }
                    }

                })
            stickyInfoToGetMuleSoftToken.startTask()
        }
    }

    private fun shouldCallLeftNavigation():Boolean{
        var shouldCall = true
        val cacheData = Preferences.getValue(PreferenceKeys.CACHED_COURSE_ROOM_DATA)

        if(cacheData != ""){

            val gson = Gson()
            val json: String = cacheData.toString()
            val type =
                object : TypeToken<ArrayList<CacheCourseRoomBean?>?>() {}.type
            val list: ArrayList<CacheCourseRoomBean> =  gson.fromJson(json, type)

            for (index in 0 until list.size){
                if(list[index].courseId.equals(courseData?.courseIdentifier) && list[index].type.equals(Constants.GP2)){
                    shouldCall = false
                    leftNavigationItem = list[index].jsonData
                    break
                }
            }
        }else {
            shouldCall = true
        }

        return shouldCall
    }

    //call api to get basic info
    private fun callLeftNavigationApi(token: String) {
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)
        val fp2Headers = NetworkHandler.getHeadersForFP2Request(token)


        val params = java.util.HashMap<String, Any>()

        val leftNavigationUrl = FP2NetworkConstants.GET_LEFT_NAVIGATION_AUTH_2
        val url = FP2NetworkConstants.getUrl(
            leftNavigationUrl,
            "{{employeeId}}",
            "" + loginBean?.authData?.employeeId!!.value
        )


        val fp2Url = getDomainForLink() + FP2NetworkConstants.getUrl(
            url,
            "{{courseId}}",
            "" + courseData!!.courseIdentifier!!
        )

        Util.trace("basic info url", fp2Url)
        val networkHandler = NetworkHandler(
            this,
            fp2Url,
            params,
            NetworkHandler.METHOD_GET,
            leftNavigationListener,
            fp2Headers
        )
        networkHandler.isPostTypeSubmitting = true
        networkHandler.submitMessage = ""
        networkHandler.execute()
    }

    private val leftNavigationListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {
            try{
            Util.trace("get left navigation first :  " + response.first)
          //  Util.trace("get left navigation second :  " + response.second)
            // TODO handle course detail api response
            OmnitureTrack.trackAction("course:studies")
            if (response.first == NetworkConstants.SUCCESS) {
                val gson = Gson()
                val leftNavigation = gson.fromJson<GP2Bean>(
                    response.second.toString(),
                    GP2Bean::class.java
                )


                setGpUi(leftNavigation,false)

            } else {

                if(courseData?.westworld != null){
                    setGpUi(leftNavigationItem!!,true)
                    // todo handle error message for GP2
                }else{
                    setUiForGuidedPath1_0()
                    attachClickListenerForGP1_0()
                }
            }
        }catch (e:Exception){e.printStackTrace()
            DialogUtils.showGenericErrorDialog(this@CourseRoomMenuActivity)}
            }
    }

    private fun setGpUi(leftNavigation: GP2Bean, isErrorView:Boolean) {
        isGp2 = false
        for (index in 0 until leftNavigation.leftNavigationItems?.size!!) {
            if ((leftNavigation.leftNavigationItems!![index].name?.contains("week", true)!!)
            ) {
                isGp2 = true
                break
            }
        }

        if (isGp2) {
            setUiForGuidedPath2_0(leftNavigation,isErrorView)
        } else {
            setUiForGuidedPath1_0()
            attachClickListenerForGP1_0()
        }
    }

    private fun storeGp2InCache(leftNavigation: GP2Bean) {
        val data  = Preferences.getValue(PreferenceKeys.CACHED_COURSE_ROOM_DATA)
        var list :ArrayList<CacheCourseRoomBean> ? =  null
        list = if(data == ""){
            ArrayList()
        }else{
            val fetchGson = Gson()
            val storeddata : String = Preferences.getValue(PreferenceKeys.CACHED_COURSE_ROOM_DATA).toString()
            val type: Type = object : TypeToken<ArrayList<CacheCourseRoomBean?>?>() {}.type
            fetchGson.fromJson(storeddata, type)
        }


        val cacheBean = CacheCourseRoomBean()
        cacheBean.courseId = courseData?.courseIdentifier
        cacheBean.type = Constants.GP2
        cacheBean.jsonData =  leftNavigation
        list?.add(cacheBean)
        var gson = Gson()
        var json = gson.toJson(list)
        Preferences.addValue(PreferenceKeys.CACHED_COURSE_ROOM_DATA,json)
    }

    private fun checkGettingStartedVisibility()
    {

        try {

            val gson = Gson()
            var loginBean = gson.fromJson<LoginBean>(
                Preferences.getValue(PreferenceKeys.LOGIN_INFO),
                LoginBean::class.java
            )

            var courseId = courseData!!.courseIdentifier!!
            var employeeId = loginBean.authData!!.employeeId!!.value!!

            val link = courseDetailBean!!.newCourseroomData!!.courseDetails!!.courseLink.toString()
            var blackboardDomain = Util.findDomainByMessageLink(link)

            var endpoint = NetworkConstants.LEFT_NAVIGATION_1_0

            endpoint = endpoint.replace("{blackboardDomain}", blackboardDomain)
            endpoint = endpoint.replace("{courseId}", courseId)
            var finalUrl = endpoint.replace("{employeeId}", employeeId)

            var muleSoftToken  = Preferences.getValue(PreferenceKeys.MULESOFT_ACCESS_TOKEN)

            val header: java.util.HashMap<String, Any?> = java.util.HashMap()
            header.put("access_token", muleSoftToken)
            header.put("Content-Type", "application/json")

            val params = HashMap<String, Any>()

            val networkHandler = NetworkHandler(this,
                finalUrl,
                params,
                NetworkHandler.METHOD_GET,
                leftNavigationListener_gs,
                header)

            networkHandler.setSilentMode(true)
            networkHandler.execute()


        }catch (t: Throwable){
            Util.trace("Getting started error : $t")
            t.printStackTrace()
        }
    }
    private val leftNavigationListener_gs: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {
            try {
                Util.trace("GS left navigation first :  " + response.first)
                // Util.trace("GS left navigation second :  " + response.second)

                val gson = Gson()
                var leftNav = gson.fromJson<LeftNavigation_One_Zero>(
                    response.second.toString(),
                    LeftNavigation_One_Zero::class.java
                )
                var hasGettingStarted = false

                if (leftNav.leftNavigationItems != null) {
                    for (items in leftNav!!.leftNavigationItems!!) {
                        if (items?.name!!.contains("Getting Started", true)) {
                            hasGettingStarted = true
                            break
                        }
                    }
                }

                if (hasGettingStarted) {
                    gettingStartedLayout.visibility = View.VISIBLE
                } else {
                    gettingStartedLayout.visibility = View.GONE
                }

                GettingStartedUtil.markAsCached(courseData?.courseIdentifier!!, hasGettingStarted)
            }catch (e:Exception){
                e.printStackTrace()
                DialogUtils.showGenericErrorDialog(this@CourseRoomMenuActivity)
            }
        }
    }

}
