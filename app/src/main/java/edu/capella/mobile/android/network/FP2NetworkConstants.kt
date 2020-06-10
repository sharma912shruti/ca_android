package edu.capella.mobile.android.network

import edu.capella.mobile.android.BuildConfig

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  27-04-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */

object FP2NetworkConstants {

    val GET_LEFT_NAVIGATION_AUTH_2 =  /*BuildConfig.FP_2_0_API_HEADER+*/"/webapps/sei-bbDataFramework-BBLEARN/getleftnavigation/{{courseId}}/{{employeeId}}"


    fun getUrl(url: String, regToReplace: String, regValue: String): String {
        return url.replace(regToReplace, regValue)
    }
}