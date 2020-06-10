package edu.capella.mobile.android.network

import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.adapters.AnnouncementsAdapter


/**
 * NetworkConstants.kt : A class contains constant parameters for different webservices
 *
 * Created by Jayesh Lahare on 03/02/20.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @see NetworkService
 *
 * @since 03/02/20.
 */

object NetworkConstants {


    val ACTION_FLEX_PATH_ASSESSMENT: String = "flexpathAssessments"
    val POST_NEW_TOPIC: String = "postnewtopic"
    val POST_REPLY: String = "postreply"
//    val IS_DRAFT: String = "isdraft"
//    val BODY_TEXT: String = "bodytext"
//    val SUBJECT_TEXT: String = "subjecttext"
    val IS_FLEX_PATH_COURSE: String = "isFelxpathCourse"
    val FACULTY_ROLE: String = "facultyRole"
    val IS_FACULTY: String = "isFaculty"
    val ACTION_GET_COURSE_DETAIL: String = "getCourseDetails"
    val CAPELLA_AUTH_TOKEN: String = "CapellaAuthToken"
    val CAPELLA_PASSWORD: String = "CapellaPassword"
    val CAPELLA_OPR_ID: String = "CapellaOprId"
    val MOBILECLIENT: String = "MOBILECLIENT"
    val HUBBLE_SERVICE_CLIENT_ID: String = "HubbleServiceClientId"
    val PARSERSS: String = "parserss"
    val MESSAGE_TYPE: String = "messageType"
    val MESSAGE_STATUS: String = "messageStatus"
    val TOKEN:String  = "token"
    val COURSE_ID:String  = "courseId"

     
    val PARENT_MESSAGE_ID:String  = "parentmessageid"
    val COURSE_NUMBER_ID:String  = "courseid"
    val FORMAT_TYPE:String  = "formattype"

    val EMP_ID:String  = "empId"
    val PROFILE_IMAGE_URL:String  = "profileImage"




    val INCLUDE_INSTRUCTOR_PROFILE:String  = "includeinstructorprofile"
    val COURSE_IDENTIFIER: String = "courseidentifier"
    val FORMATTYPE: String = "formattype"
    val MESSAGE_ID: String = "messageid"
    val COURSEID: String = "courseid"
    val SUBJECT_TEXT: String = "subjecttext"
    val BODY_TEXT: String = "bodytext"
    val NO_BASE_64:String  = "noBase64"
    val ACTION:String  = "action"
    val IS_DRAFT:String  = "isdraft"
    val IS_READ:String  = "isread"


    val DISCUSSION_FORUM_ID:String  = "forumid"
    val DISCUSSION_POST_TOPIC_ID:String  = "topicid"



    /**=======Network Constants============== */
    val NETWORK_SUCCESS = "success"

    /**
     * The constant NETWORK_ERROR.
     */
    val NETWORK_ERROR = "error"


    /**
     * The constant NETWORK_INTERNET_ERROR.
     */
    val NETWORK_INTERNET_ERROR = "internet_error"



    /**
     * The constant SUCCESS.
     */
    val SUCCESS = "success"


    /**
     * The constant ERROR.
     */
    val ERROR = "error"

   /**========API Parameter Names=========*/

    val USER_NAME = "username"
    val PASSWORD = "password"

    /**=======MOBIL FEED API's==============*/

    val LOGIN_API = BuildConfig.HOST + "/mobile-feed/json/login.feed"

    val CAMPUS_NEWS_API = BuildConfig.HOST + "/mobile-feed/json/news.feed"

    val Announcement_API = BuildConfig.HOST + "/mobile-feed/json/courseroom.feed?"

    val DISCUSSION_DRAFT_API = BuildConfig.HOST + "/mobile-feed/json/newdiscussions.feed?"

    val PROFILE_IMAGE_API = BuildConfig.HOST + "/mobile-feed/profile/pic.run"

    val LOGOUT_API = BuildConfig.HOST + "/mobile-feed/json/logout.feed"

    val EDIT_DRAFT_API = BuildConfig.HOST + "/mobile-feed/json/discussions.feed?"

    val APP_VERSION_PROD = BuildConfig.HOST + "/mobile-feed/public/AppVersion.run"

    val APP_VERSION = BuildConfig.HOST + "/mobile-feed/public/AppVersionRewrite.run"


    /**=======PRofile=============================*/
    val LEARNER_PROFILE_API = BuildConfig.HOST + "/mobile-feed/json/LearnerProfileView.feed?action=learnerinfo"

    val EDIT_EMAIL_ON_CAMPUS= "https://" + BuildConfig.ENVIRONMENT +"cs.capella.edu/psc/cslr/EMPLOYEE/CS/c/CC_PORTFOLIO.SS_CC_EMAIL_ADDR.GBL?EOPP_SCLabel=Personal%20Information&EOPP_SCName=CU_CAMPUS_PERSONAL_INFO&EOPP_SCNode=IGUIDE&EOPP_SCPTcname=CU_SC_SP_CAMPUS_PERSONAL_INFO&EOPP_SCPortal=CAPELLA&FolderPath=PORTAL_ROOT_OBJECT.CU_IG_SELF_SERVICE.CU_IG_CAMPUS_INFO.CU_IG_PERSONAL_INFO_NAVCOLL.CU_IG_EMAIL_ADDRESSES&IsFolder=false&NoCrumbs=yes&PORTALPARAM_PTCNAV=CU_IG_EMAIL_ADDRESSES&PortalActualURL=https%3A//cs.capella.edu/psc/cslr/EMPLOYEE/CS/c/CC_PORTFOLIO.SS_CC_EMAIL_ADDR.GBL&PortalCRefLabel=Email%20Addresses&PortalContentProvider=CS&PortalContentURL=https%3A//cs.capella.edu/psc/cslr/EMPLOYEE/CS/c/CC_PORTFOLIO.SS_CC_EMAIL_ADDR.GBL&PortalHostNode=IGUIDE&PortalKeyStruct=yes&PortalRegistryName=CAPELLA&PortalServletURI=https%3A//portal.capella.edu/psp/portal/&PortalURI=https%3A//portal.capella.edu/psc/portal/"
    val VIEW_EDIT_PROFILE_ON_CAMPUS= "https://" + BuildConfig.ENVIRONMENT +"campus.capella.edu/account/private/edit"

    /**========Classmates=================*/

    val CLASSMATES_API = BuildConfig.HOST + "/mobile-feed/json/classmates.feed"

    /**========Courses=================*/

    val COURSES_API = BuildConfig.HOST + "/mobile-feed/json/newcourseroom.feed"



    val COURSE_DETAIL_API = BuildConfig.HOST + "/mobile-feed/json/courseDetails.feed"

    val COURSE_DISCUSSION_FORUM_API = BuildConfig.HOST + "/mobile-feed/json/newdiscussions.feed"

     /**** FORUM AND TOPIC API IS SAME BUT STILL USING IT SEPARATLY ****/
    val COURSE_DISCUSSION_TOPIC_API = BuildConfig.HOST + "/mobile-feed/json/newdiscussions.feed"


    val CREATE_POST_API = BuildConfig.HOST + "/mobile-feed/json/discussions.feed"

    val REPLY_POST_API = BuildConfig.HOST + "/mobile-feed/json/discussions.feed"

    val COURSE_DISCUSSION_POST_API = BuildConfig.HOST + "/mobile-feed/json/newdiscussions.feed"

    val UPDATE_READ_STATUS_API = BuildConfig.HOST + "/mobile-feed/json/discussions.feed"

    val FLEX_PATH_ASSESSMENT_LIST = BuildConfig.HOST + "/mobile-feed/json/flexpathassessments.feed"

    /**======WEB BROWSER URLS=========**/

    val TECHNICAL_SUPPORT = BuildConfig.WEB_URL + "/web/technical-support/home"

    val ACADEMIC_ADVISING = BuildConfig.WEB_URL + "/web/advising-and-program-planning/home"

    val FINANCE = BuildConfig.WEB_URL + "/my-capella/finances"

    val ACADEMIC_PLAN = BuildConfig.WEB_URL + "/dcp/home"

    val CAPELLA_CAMPUS = BuildConfig.WEB_URL + "/web/iguide/home"

    val SUMMON_URL = "https://capella.summon.serialssolutions.com/"

    val DATABASE_URL = "http://capellauniversity.libguides.com/az.php?t=14393"

    val LEFT_NAVIGATION_1_0 =  "{blackboardDomain}/webapps/sei-bbDataFramework-BBLEARN/getleftnavigation/{courseId}/{employeeId}"

}
