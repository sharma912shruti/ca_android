package edu.capella.mobile.android.network

import edu.capella.mobile.android.BuildConfig

/**
 * OpenamNetworkConstants.kt :  A network constant file, keeping record of url and constants to make
 * any OpenAM api call.
 *
 * @author  :  jayesh.lahare
 * @version :  1.0
 * @since   :  2/29/2020
 *
 *
 */
object OpenamNetworkConstants
{

    val OPENAM_AUTHENTICATION = BuildConfig.OPENAM_HOST + "/openam/json/authenticate"

    val OPENAM_TOKEN_VALIDATE = BuildConfig.OPENAM_HOST + "/openam/json/sessions/"

    val OPENAM_LOGOUT = BuildConfig.OPENAM_HOST + "/openam/json/sessions/"

}
