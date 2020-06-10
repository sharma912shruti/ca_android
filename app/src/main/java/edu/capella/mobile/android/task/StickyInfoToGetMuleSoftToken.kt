package edu.capella.mobile.android.task

import android.content.Context
import android.util.Pair
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.utils.PreferenceKeys
import edu.capella.mobile.android.utils.Preferences
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.bean.MuleSoftToken
import edu.capella.mobile.android.bean.OpenAMAuth
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.MuleSoftNetworkConstants
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import edu.capella.mobile.android.network.OpenamNetworkConstants
import java.util.*

/**
 * Class Name.kt : class description goes here
 *
 * @author  :  SSHARMA45
 * @version :  1.0
 * @since   :  4/27/2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
class StickyInfoToGetMuleSoftToken {

    val TAG = "StickyInfoToGetMuleSoftToken"

    lateinit var globalContext: Context
    var userName: String = ""
    var password: String = ""
    var irisUrlToOpen: String = ""
    var tokenListener: AccessTokenListener? = null

    /**
     * Constructor without user and password.
     */
    constructor(context: Context, listener: AccessTokenListener) {
        this.globalContext = context
        this.userName = Preferences.getValue(PreferenceKeys.USER_NAME)!!
        this.password = Preferences.getValue(PreferenceKeys.SECRET)!!
        this.tokenListener = listener
    }

    /**
     * Method creates OpenAM api calls to generate access token.
     *
     */
    fun startTask() {
        Preferences.removeKey(PreferenceKeys.OPENAM_TOKEN.toString())
        Preferences.removeKey(PreferenceKeys.MULESOFT_ACCESS_TOKEN.toString())

        val openAmHeader: HashMap<String, Any?>? =
            NetworkHandler.getOpenAMHeader(this.userName, this.password)
        val params = HashMap<String, Any>()

        val openAMAuth = NetworkHandler(
            globalContext,
            OpenamNetworkConstants.OPENAM_AUTHENTICATION,
            params,
            NetworkHandler.METHOD_POST,
            openAMAuthenticatorListener,
            openAmHeader
        )

        openAMAuth.isPostTypeSubmitting = true
        openAMAuth.submitMessage = ""
        openAMAuth.setSilentMode(false)
        openAMAuth.execute()
    }

    private val openAMAuthenticatorListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {
            Util.trace(TAG, response.first.toString())
            Util.trace(TAG, response.second.toString())
            if (response.first == NetworkConstants.SUCCESS) {
                val gson = Gson()
                val openAMAuth =
                    gson.fromJson<OpenAMAuth>(response.second.toString(), OpenAMAuth::class.java)
                Preferences.addValue(PreferenceKeys.OPENAM_TOKEN, openAMAuth.tokenId.toString())
                generateMuleSoftToken(openAMAuth)
            } else {
                Preferences.addBoolean(PreferenceKeys.IS_MULESOFT_ACCESS_TOKEN_AVAILABLE, false)
                Util.trace(TAG, response.second.toString())
            }
        }
    }

    private fun generateMuleSoftToken(openAMAuth: OpenAMAuth) {
        try {
            val params = HashMap<String, Any>()

            params["grant_type"] = "password"
            params["scope"] = "read write"
            params["client_id"] = BuildConfig.MULESOFT_CLIENT_ID
            params["username"] = BuildConfig.AUTH_USER
            params["iplanetcookie"] = openAMAuth.tokenId.toString()

            val muleSoftTokenGenerator = NetworkHandler(
                globalContext,
                MuleSoftNetworkConstants.CREATE_ACCESS_TOKEN,
                params,
                NetworkHandler.METHOD_POST,
                muleSoftTokenGenListener,
                null
            )

            muleSoftTokenGenerator.isPostTypeSubmitting = true
            muleSoftTokenGenerator.submitMessage = ""
            muleSoftTokenGenerator.execute()
        } catch (t: Throwable) {
            Util.trace("Mulesoft Access Token Issue " + t.toString())
            t.printStackTrace()
            Preferences.addBoolean(PreferenceKeys.IS_MULESOFT_ACCESS_TOKEN_AVAILABLE, false)
        }
    }

    private val muleSoftTokenGenListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {
            Util.trace("MULESOFT", response.first.toString())
            Util.trace("MULESOFT", response.second.toString())
            if (response.first == NetworkConstants.SUCCESS) {
                val gson = Gson()
                val muleSoftToken = gson.fromJson<MuleSoftToken>(
                    response.second.toString(),
                    MuleSoftToken::class.java
                )

                Preferences.addValue(
                    PreferenceKeys.MULESOFT_ACCESS_TOKEN,
                    muleSoftToken.access_token.toString()
                )
                Preferences.addBoolean(PreferenceKeys.IS_MULESOFT_ACCESS_TOKEN_AVAILABLE, true)
                tokenListener?.onTokenGetListener(muleSoftToken.access_token.toString())
            } else {
                Util.trace(TAG, response.second.toString())

                /**Something Wrong with Mulesoft Token so needs to generate OpenAM Token Again*/
                Preferences.addBoolean(PreferenceKeys.IS_MULESOFT_ACCESS_TOKEN_AVAILABLE, false)
            }
        }
    }


    interface AccessTokenListener {
        fun onTokenGetListener(token: String)
    }
}