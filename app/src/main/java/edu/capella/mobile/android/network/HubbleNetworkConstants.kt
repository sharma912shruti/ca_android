package edu.capella.mobile.android.network

import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.utils.PreferenceKeys
import edu.capella.mobile.android.utils.Preferences
import javax.security.auth.Subject

/**
 * HubbleNetworkConstants.kt : A network constant file, keeping record of url and constants to make
 * any Hubble api call.
 *
 * Created by Jayesh Lahare on 03/02/20.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 03/02/20.
 */
object HubbleNetworkConstants {


    val ID: String = "Id"
    val TARGET_DATE = "targetDate"
    val TARGET_NEW_DATE = "targetNewDate"

    val ASSIGNMENTS: String= "assignments"
    val LONG_COURSE_ID: String = "longCourseId"
    val USER_ID: String = "userId"
    val EMPLOYEE_ID: String ="employeeId"
    val COURSE_ID: String = "courseId"
    val PCCP_ID: String = "pccpId"
    /**
     * Campus Alert API path
     */
    val COURSE_BASIC_INFO_API =
        BuildConfig.HUBBLE_HOST + "/hubble-new/services/rest/CourseroomService/BasicCourseInfo/{{employeeId}}/{{courseId}}"


    /**=======CourseSyllabus==============*/

    val COURSES_SYLLABUS_LIST = BuildConfig.HUBBLE_HOST + "/hubble-new/services/rest/CourseroomService/CourseSyllabus/{{employeeId}}"

    /**
     *
     *course Basic info api
     * **/
    val CAMPUS_ALERT =
        BuildConfig.HUBBLE_HOST + "/hubble-new/services/rest/PersonInformationService/EventMessage/{{employeeId}}"

    /**
     *
     * course menu last login date
     *
     * **/
    val COURSE_LAST_LOGIN_DATE =
        BuildConfig.HUBBLE_HOST + "/hubble-new/services/rest/CourseroomService/LastLoginDate/{employeeId}/{courseId}/{lastLoginDateTime}"


    /***
     *  Course email count api
     *
     * ***/
    val COURSE_EMAIL_COUNT =
        BuildConfig.HUBBLE_HOST + "/hubble-new/services/rest/UserProfileService/Email/{{employeeId}}"

    /***
     *  Assignments List API
     *
     * ***/
    val ASSIGNMENTS_LIST =
        BuildConfig.HUBBLE_HOST + "/hubble-new/services/rest/CourseroomService/CourseAssignment/{{employeeId}}/{{courseId}}/true"


    /***
     *  ASSESSMENT_FACULTY List API
     *
     * ***/
    val ASSESSMENT_FACULTY_LIST =
        BuildConfig.HUBBLE_HOST + "/hubble-new/services/rest/CourseroomService/CourseAssignment/{{employeeId}}/{{courseId}}"

    /***
     *  pccp detail api
     *
     * **/
    val PCCP_DETAIL_API = BuildConfig.HUBBLE_HOST+ "/hubble-new/services/rest/FlexpathService/PccpDetails/{courseId}/{employeeId}"

    /****
     * Submit pccp detail
     *
     * ***/
    val SUBMIT_PCCP_DETAIL_API = BuildConfig.HUBBLE_HOST + "/hubble-new/services/rest/FlexpathService/SubmitPccpDetails/{employeeId}"

//    val SUBMIT_PCCP_DETAIL_API=  BuildConfig.HUBBLE_HOST +"/hubble-new/services/rest/FlexpathService/SubmitPccpDetails/{{employeeId}}"


    val FACULTY_COURSE_ASSIGNMENTS =
        BuildConfig.HUBBLE_HOST + "/hubble-new/services/rest/CourseroomService/CourseAssignment/{{employeeId}}/{{courseId}}"

    val STUDENT_COURSE_GRADE =
        BuildConfig.HUBBLE_HOST + "/hubble-new/services/rest/CourseroomService/CourseActivityGrades/{{employeeId}}/{{courseId}}"

    /***
     * course assessment detail list
     *
     * ****/
    val COURSE_ASESSMENT = BuildConfig.HUBBLE_HOST + "/hubble-new/services/rest/CourseroomService/CourseUnits/{{employeeId}}/{{courseId}}"

    /**
     * Static method to replace some string from given url and return it.
     *
     * @param url : url in which some string portion needs to replace.
     * @param regToReplace : String portion which will be replaced.
     * @param regValue : String which will be inserted on place of refToReplace
     * @return : Updated string
     */
    fun getUrl(url: String, regToReplace: String, regValue: String): String {
        return url.replace(regToReplace, regValue)
    }


    fun getAcceptHeaderValue(api: String): String? {
        var acceptHeaderValue: String? = null
        when (api) {
            COURSE_BASIC_INFO_API -> {
                acceptHeaderValue =
                    "application/vnd.capella.basic-course-info+json;version=15.08,application/vnd.capella.hubble-exception+json,application/vnd.capella.hubble-validation-exception+json"
            }

            CAMPUS_ALERT -> {
                acceptHeaderValue =
                    "application/vnd.capella.event-message+json;version=13.10,application/vnd.capella.hubble-exception+json,application/vnd.capella.hubble-validation-exception+json"
            }

            COURSE_EMAIL_COUNT -> {
                acceptHeaderValue =
                    "application/vnd.capella.email+json;version=15.08,application/vnd.capella.hubble-exception+json,application/vnd.capella.hubble-validation-exception+json"
            }

            COURSES_SYLLABUS_LIST -> {
                acceptHeaderValue =  "application/vnd.capella.course-syllabus+json;version=15.08,application/vnd.capella.hubble-exception+json,application/vnd.capella.hubble-validation-exception+json"
            }

            ASSIGNMENTS_LIST -> {
                acceptHeaderValue =
                    "application/vnd.capella.course-assignment+json;version=15.08,application/vnd.capella.hubble-exception+json,application/vnd.capella.hubble-validation-exception+json"
            }

//            http://{{env}}hubble-ext.capella.edu/hubble-new/services/rest/FlexpathService/PccpDetails/TEST-FPX7001_007535_2_1201_OEE_03/{{employeeId}}

            PCCP_DETAIL_API -> {
                acceptHeaderValue =
                    "application/vnd.capella.pccp-details+json;version=17.06,application/vnd.capella.hubble-exception+json,application/vnd.capella.hubble-validation-exception+json"
            }

            FACULTY_COURSE_ASSIGNMENTS -> {
                acceptHeaderValue =
                    "application/vnd.capella.course-assignment+json;version=15.08,application/vnd.capella.hubble-exception+json,application/vnd.capella.hubble-validation-exception+json"
            }
           STUDENT_COURSE_GRADE -> {
                acceptHeaderValue =
                    "application/vnd.capella.course-activity-grades+json;version=15.09,application/vnd.capella.hubble-exception+json,application/vnd.capella.hubble-validation-exception+json"
            }

            SUBMIT_PCCP_DETAIL_API -> {
                acceptHeaderValue = "application/vnd.capella.submit-pccp-details+json;version=17.06,application/vnd.capella.hubble-exception+json,application/vnd.capella.hubble-validation-exception+json"
            }

            COURSE_ASESSMENT ->{
                acceptHeaderValue = "application/vnd.capella.course-units+json;version=15.08,application/vnd.capella.hubble-exception+json,application/vnd.capella.hubble-validation-exception+json"
            }
        }

        return acceptHeaderValue
    }


    fun buildRequestHeaderForGettingStarted():HashMap<String, Any?>
    {
        val hubbleHeader: HashMap<String, Any?> = HashMap()
         hubbleHeader.put("shouldAuthenticate", true)
        hubbleHeader.put("hashForCurrentEnv", BuildConfig.HASHES)


        hubbleHeader.put("Content-Type", "application/json")
         hubbleHeader.put("Cache-Control", "no-cache")

         hubbleHeader.put("HubbleServiceClientId", "MOBILECLIENT")
        hubbleHeader.put("Accept", "application/vnd.capella.course-assignment+json;version=15.08,application/vnd.capella.hubble-exception+json,application/vnd.capella.hubble-validation-exception+json")
        hubbleHeader.put("Authorization", "Basic " + BuildConfig.HASHES)


        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

       hubbleHeader.put("CapellaAuthToken", loginBean?.authData?.token)

        return hubbleHeader
    }

}
