package edu.capella.mobile.android.network

import edu.capella.mobile.android.BuildConfig


/**
 * MuleSoftNetworkConstants.kt : A network constant file, keeping record of url and constants to make
 * any MuleSoft api call.
 *
 * Created by Jayesh Lahare on 2/29/2020.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 2/29/2020.
 */
object MuleSoftNetworkConstants
{

    /**
     * OpenAM API path for Creating access token
     */
    val CREATE_ACCESS_TOKEN = BuildConfig.MULESOFT_HOST + "/iris/oauth2/jwt-ext/v1/public/access_token"

    /**
     * Sticky INFO API Path
     */
    val CREATE_STICKY_INFO_SESSION = BuildConfig.MULESOFT_HOST + "/iris/utility-ext/v1/sticky/info/"


    /**
     * Validate Mule Token API Path
     */
    val MULE_TOKEN_VALIDATE = BuildConfig.MULESOFT_HOST + "/validate"

    /**
     * Getting Started API
     */
    val MULE_GETTING_STARTED_CONTENT = BuildConfig.MULESOFT_HOST+ "/iris/mobileapp-ext/v1/long-course-id/{courseIdentifier}/navigation/getting-started"

  

}
