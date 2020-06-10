package edu.capella.mobile.android.task

import android.content.Context
import android.util.Pair
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.DialogUtils
import edu.capella.mobile.android.utils.PreferenceKeys
import edu.capella.mobile.android.utils.Preferences
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.bean.*
import edu.capella.mobile.android.interfaces.EventListener
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.*
import java.io.File
import java.net.URLEncoder
import java.util.*

/**
 * StickyInfoGrabber.kt  :  Class responsible for connecting OpenAM and handling Sticky session
 *
 * @author  :  jayesh.lahare
 * @version :  1.0
 * @since   :  2/29/2020
 *
 *
 */
class StickyInfoGrabber
{
    val TAG  = "StickyInfoGrabber"

    lateinit var  globalContext: Context
    var userName: String = ""
    var password: String = ""
    var irisUrlToOpen: String = ""

    /**
     * Constructor without user and password.
     */
    constructor(context: Context )
    {
        this.globalContext = context
        this.userName = Preferences.getValue(PreferenceKeys.USER_NAME)!!
        this.password = Preferences.getValue(PreferenceKeys.SECRET)!!

    }

    /**
     * Constructor with user and password.
     */
    constructor(context: Context , uName: String , uPass : String)
    {
        this.globalContext = context
        this.userName = uName
        this.password  = uPass
    }

    /**
     * Method creates OpenAM api calls to generate access token.
     *
     */
    fun startTask()
    {
        Preferences.removeKey(PreferenceKeys.OPENAM_TOKEN.toString())
        Preferences.removeKey(PreferenceKeys.MULESOFT_ACCESS_TOKEN.toString())

           val openAmHeader : HashMap<String, Any?>? = NetworkHandler.getOpenAMHeader(this.userName , this.password)
           val params = HashMap<String, Any>()

            val openAMAuth = NetworkHandler(globalContext,
                    OpenamNetworkConstants.OPENAM_AUTHENTICATION,
                params,
                    NetworkHandler.METHOD_POST,
                    openAMAuthenticatorListener,
                openAmHeader)

           openAMAuth.setSilentMode(true)
           openAMAuth.execute()
    }


    private val openAMAuthenticatorListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>)
        {
            Util.trace(TAG , response.first.toString())
            Util.trace(TAG , response.second.toString())
            if(response.first == NetworkConstants.SUCCESS) {
                val gson = Gson()
                val openAMAuth = gson.fromJson<OpenAMAuth>(response.second.toString(), OpenAMAuth::class.java)
                Preferences.addValue(PreferenceKeys.OPENAM_TOKEN , openAMAuth.tokenId.toString())
                generateMuleSoftToken(openAMAuth)
            }else
            {
                Preferences.addBoolean(PreferenceKeys.IS_MULESOFT_ACCESS_TOKEN_AVAILABLE , false)
                Util.trace(TAG , response.second.toString())
            }
        }
    }

    /**
     * Method generated MuleSoft Access Token based on OpenAM access token.
     *
     * @param openAMAuth
     */
   private fun generateMuleSoftToken(openAMAuth: OpenAMAuth)
   {
       try {
            val params = HashMap<String, Any>()

           params.put("grant_type", "password")
           params.put("scope", "read write")
           params.put("client_id", BuildConfig.MULESOFT_CLIENT_ID)
           params.put("username", BuildConfig.AUTH_USER)
           params.put("iplanetcookie", openAMAuth.tokenId.toString())

           val muleSoftTokenGenerator = NetworkHandler(
               globalContext,
               MuleSoftNetworkConstants.CREATE_ACCESS_TOKEN,
               params,
               NetworkHandler.METHOD_POST,
               muleSoftTokenGenListener,
               null
           )

           muleSoftTokenGenerator.setSilentMode(true)
           muleSoftTokenGenerator.execute()
       }catch (t: Throwable)
       {
           Util.trace("Mulesoft Access Token Issue " + t.toString())
           t.printStackTrace()
           Preferences.addBoolean(PreferenceKeys.IS_MULESOFT_ACCESS_TOKEN_AVAILABLE , false)
       }
   }

    private val muleSoftTokenGenListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>)
        {
            Util.trace("MULESOFT" , response.first.toString())
            Util.trace("MULESOFT" , response.second.toString())
            if(response.first == NetworkConstants.SUCCESS) {
                val gson = Gson()
                val muleSoftToken = gson.fromJson<MuleSoftToken>(response.second.toString(), MuleSoftToken::class.java)

                Preferences.addValue(PreferenceKeys.MULESOFT_ACCESS_TOKEN , muleSoftToken.access_token.toString())
                Preferences.addBoolean(PreferenceKeys.IS_MULESOFT_ACCESS_TOKEN_AVAILABLE , true)

            }else
            {
                Util.trace(TAG , response.second.toString())

                /**Something Wrong with Mulesoft Token so needs to generate OpenAM Token Again*/
                Preferences.addBoolean(PreferenceKeys.IS_MULESOFT_ACCESS_TOKEN_AVAILABLE , false)
            }
        }
    }


    /**
     * This method make sures that before opening any external browser link the MuleSoft token
     * should be valid
     *
     * @param targetUrl
     * @param irisUrl
     */
    fun validateAndOpenUrl(targetUrl:String , irisUrl: String)
    {
        validateMuleSoftToken(object : NetworkListener {
            override fun onNetworkResponse(response: Pair<String, Any>) {
                if(response.first == NetworkConstants.SUCCESS)
                {
                    val gson = Gson()
                    val muleTokenVerify = gson.fromJson<MuleTokenVerify>(response.second.toString(), MuleTokenVerify::class.java)

                    if(muleTokenVerify.capellaLearnerVerify == "y")
                    {
                        Util.trace("MuleSoft token is valid")
                        generateMuleSoftStickySessionForTargetUrl(targetUrl , irisUrl)
                    }else
                    {
                        Util.trace("MuleSoft token is non-valid")
                        // GENERATE new TOKEN
                    }

                }else
                {
                    Util.trace("MuleSoft token  Error :" , response.second.toString())
                }

            }
        })
    }

    /**
     * Method generates MuleSoft token for given URL then appends received token with IRIS url then
     * opens it in external browser.
     *
     * @param targetUrl
     * @param irisUrl
     */
    fun generateMuleSoftStickySessionForTargetUrl(targetUrl:String , irisUrl: String)
    {
        this.irisUrlToOpen = irisUrl


        Util.openBrowserWithConfirmationPopupNew(globalContext , object :EventListener{
            override fun confirm() {
                super.confirm()
                            val stickyHeader : HashMap<String, Any?>? = NetworkHandler.getStickySessionHeader(Preferences.getValue(PreferenceKeys.MULESOFT_ACCESS_TOKEN))

                            val params = NetworkHandler.getStickyRawJSON(BuildConfig.ENV_CAPS,
                                Preferences.getValue(PreferenceKeys.OPENAM_TOKEN)!!,
                                BuildConfig.MULE_ENV , targetUrl)

                            val muleSoftStickySessionGenerator = NetworkHandler(globalContext,
                                MuleSoftNetworkConstants.CREATE_STICKY_INFO_SESSION,
                                params,
                                muleSoftSessionListener,
                                stickyHeader)

                            muleSoftStickySessionGenerator.setSilentMode(true)
                            muleSoftStickySessionGenerator.execute()

            }

            override fun cancel() {
                super.cancel()
            }

        })
    }

    /**
     * Method generates MuleSoft token for given URL then appends received token with IRIS url then
     * opens it in external browser.
     *
     * @param targetUrl
     * @param irisUrl
     * @param customMessage
     */
    fun generateMuleSoftStickySessionForTargetUrl(targetUrl:String , irisUrl: String, customMessage: String)
    {
        this.irisUrlToOpen = irisUrl


        Util.openBrowserWithConfirmationPopupNew(globalContext , object :EventListener{
            override fun confirm() {
                super.confirm()
                val stickyHeader : HashMap<String, Any?>? = NetworkHandler.getStickySessionHeader(Preferences.getValue(PreferenceKeys.MULESOFT_ACCESS_TOKEN))

                val params = NetworkHandler.getStickyRawJSON(BuildConfig.ENV_CAPS,
                    Preferences.getValue(PreferenceKeys.OPENAM_TOKEN)!!,
                    BuildConfig.MULE_ENV , targetUrl)

                val muleSoftStickySessionGenerator = NetworkHandler(globalContext,
                    MuleSoftNetworkConstants.CREATE_STICKY_INFO_SESSION,
                    params,
                    muleSoftSessionListener,
                    stickyHeader)

                muleSoftStickySessionGenerator.setSilentMode(true)
                muleSoftStickySessionGenerator.execute()

            }

            override fun cancel() {
                super.cancel()
            }

        } , customMessage)
    }


    private val muleSoftSessionListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>)
        {
            Util.trace("MULESOFT SESSION" , response.first.toString())
            Util.trace("MULESOFT SESSION" , response.second.toString())
            if(response.first == NetworkConstants.SUCCESS) {
                val gson = Gson()
                val muleSoftSession = gson.fromJson<MuleSoftSession>(response.second.toString(), MuleSoftSession::class.java)

                //Util.openBrowserWithConfirmationPopup(globalContext, irisUrlToOpen + File.separator + muleSoftSession.token )
                Util.openExternalBrowser(globalContext, irisUrlToOpen + File.separator + muleSoftSession.token )

                Util.trace("final_browser_url",irisUrlToOpen + File.separator + muleSoftSession.token)
            }else
            {
                DialogUtils.onShowDialog(globalContext , globalContext.getString(R.string.error), globalContext.getString(R.string.url_opening_error))
                Util.trace("MULESOFT SESSION ERROR" , response.second.toString())
            }
        }
    }


   /***********Open AM Logout Code****************/
    fun logoutOpenAM()
    {
        if( Preferences.getValue(PreferenceKeys.OPENAM_TOKEN) != "" )
        {

            val params = HashMap<String, Any>()
            params.put("_action", "logout")

            val openAMLogout = NetworkHandler(globalContext,
                OpenamNetworkConstants.OPENAM_LOGOUT + Preferences.getValue(PreferenceKeys.OPENAM_TOKEN) ,
                params,
                NetworkHandler.METHOD_POST,
                openAMLogoutListener,
                null)

            openAMLogout.setSilentMode(true)
            openAMLogout.execute()

        }
    }
    private val openAMLogoutListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>)
        {

            if(response.first == NetworkConstants.SUCCESS) {
                Util.trace("OpenAM Logout Done : " , response.second.toString())

            }else
            {
                Util.trace("OpenAM Logout Error :" , response.second.toString())
            }
        }
    }

    /***********Open AM Validate Token Code****************/
    fun validateOpenAM()
    {
        if( Preferences.getValue(PreferenceKeys.OPENAM_TOKEN) != "" )
        {

            val blankParams = HashMap<String, Any>()
            val params = HashMap<String, Any>()
            params.put("_action", "validate")

            val headers = HashMap<String, Any?>()
            headers.put("Content-Type", "application/json;charset=UTF-8")


            val qString = NetworkService.getQueryString(params)

            val eToken = URLEncoder.encode(Preferences.getValue(PreferenceKeys.OPENAM_TOKEN), "UTF-8")


            val url = OpenamNetworkConstants.OPENAM_TOKEN_VALIDATE + java.net.URLEncoder.encode(eToken, "utf-8") + qString

            Util.trace("url is : $url")
            //  ,
            val openAMValidate = NetworkHandler(globalContext,

                url ,
                blankParams,
                NetworkHandler.METHOD_POST,
                openAMValidatetListener,
                headers)

            openAMValidate.setSilentMode(true)
            openAMValidate.execute()

        }
    }
    private val openAMValidatetListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>)
        {

            if(response.first == NetworkConstants.SUCCESS)
            {
                val gson = Gson()
                val openValidToken = gson.fromJson<OpenAMValidToken>(response.second.toString(), OpenAMValidToken::class.java)

                if(openValidToken.valid == true)
                {
                    Util.trace("OpenAM token is valid")
                }else
                {
                    Util.trace("OpenAM token is non-valid")
                    startTask() // GENERTING OPEN AM TOKEN AGAIN
                }

            }else
            {
                Util.trace("OpenAM token  Error :" , response.second.toString())
            }
        }
    }

    /***********MuleSoft Validate Token Code****************/
    fun validateMuleSoftToken(muleTokenValidateListener : NetworkListener )
    {
        if( Preferences.getValue(PreferenceKeys.MULESOFT_ACCESS_TOKEN) != "" )
        {

            val params = HashMap<String, Any>()

            var header = HashMap<String, Any?>()
            header.put("Authorization","Bearer " + Preferences.getValue(PreferenceKeys.MULESOFT_ACCESS_TOKEN))

            val muleValidate = NetworkHandler(globalContext,
                MuleSoftNetworkConstants.MULE_TOKEN_VALIDATE   ,
                params,
                NetworkHandler.METHOD_GET,
                muleTokenValidateListener,
                header)

            muleValidate.setSilentMode(true)
            muleValidate.execute()

        }else
        {
                Util.trace("Token is null, cannot do anything")
        }
    }

    fun generateReturnMuleSoftStickySessionForTargetUrl(targetUrl: String, returnNetworkListener: NetworkListener ) {


        //val gson = Gson()
        // val muleSoftToken = gson.fromJson<MuleSoftToken>(Preferences.getValue(PreferenceKeys.MULESOFT_TOKEN), MuleSoftToken::class.java)

        val stickyHeader : HashMap<String, Any?>? = NetworkHandler.getStickySessionHeader(Preferences.getValue(PreferenceKeys.MULESOFT_ACCESS_TOKEN))

        val params = NetworkHandler.getStickyRawJSON(BuildConfig.ENV_CAPS,
            Preferences.getValue(PreferenceKeys.OPENAM_TOKEN)!!,
            BuildConfig.MULE_ENV , targetUrl)

        val muleSoftStickySessionGenerator = NetworkHandler(globalContext,
            MuleSoftNetworkConstants.CREATE_STICKY_INFO_SESSION,
            params,
            returnNetworkListener,
            stickyHeader)

        muleSoftStickySessionGenerator.setSilentMode(true)
        muleSoftStickySessionGenerator.execute()


    }

    fun generateReturnMuleSoftStickySessionForTargetUrl(targetUrl: String,silentCall:Boolean, returnNetworkListener: NetworkListener ) {


        //val gson = Gson()
        // val muleSoftToken = gson.fromJson<MuleSoftToken>(Preferences.getValue(PreferenceKeys.MULESOFT_TOKEN), MuleSoftToken::class.java)

        val stickyHeader : HashMap<String, Any?>? = NetworkHandler.getStickySessionHeader(Preferences.getValue(PreferenceKeys.MULESOFT_ACCESS_TOKEN))

        val params = NetworkHandler.getStickyRawJSON(BuildConfig.ENV_CAPS,
            Preferences.getValue(PreferenceKeys.OPENAM_TOKEN)!!,
            BuildConfig.MULE_ENV , targetUrl)

        val muleSoftStickySessionGenerator = NetworkHandler(globalContext,
            MuleSoftNetworkConstants.CREATE_STICKY_INFO_SESSION,
            params,
            returnNetworkListener,
            stickyHeader)

        muleSoftStickySessionGenerator.setSilentMode(silentCall)
        muleSoftStickySessionGenerator.execute()


    }


/*
    fun openScoringGuide(targetUrl:String , irisUrl: String)
    {
        this.irisUrlToOpen = irisUrl


        Util.openBrowserWithConfirmationPopupNew(globalContext , object :EventListener{
            override fun confirm() {
                super.confirm()
                val stickyHeader : HashMap<String, Any?>? = NetworkHandler.getStickySessionHeader(Preferences.getValue(PreferenceKeys.MULESOFT_ACCESS_TOKEN))


                val params = NetworkHandler.getStickyRawJSONForScoringGuide(Preferences.getValue(PreferenceKeys.MULESOFT_ACCESS_TOKEN)!!, BuildConfig.ENV_CAPS,
                    Preferences.getValue(PreferenceKeys.OPENAM_TOKEN)!!,
                    BuildConfig.MULE_ENV , targetUrl)

                val muleSoftStickySessionGenerator = NetworkHandler(globalContext,
                    MuleSoftNetworkConstants.CREATE_STICKY_INFO_SESSION,
                    params,
                    scoringGuideListener,
                    stickyHeader)

                muleSoftStickySessionGenerator.setSilentMode(true)
                muleSoftStickySessionGenerator.execute()

            }

            override fun cancel() {
                super.cancel()
            }

        })



    }

    private val scoringGuideListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>)
        {
            Util.trace("scoringGuideListener" , response.first.toString())
            Util.trace("scoringGuideListener" , response.second.toString())
            if(response.first == NetworkConstants.SUCCESS) {
                val gson = Gson()
                val muleSoftSession = gson.fromJson<MuleSoftSession>(response.second.toString(), MuleSoftSession::class.java)
                Util.trace("final_browser_url",irisUrlToOpen + File.separator + muleSoftSession.token)
                //Util.openBrowserWithConfirmationPopup(globalContext, irisUrlToOpen + File.separator + muleSoftSession.token )
                Util.openExternalBrowser(globalContext, irisUrlToOpen + File.separator + muleSoftSession.token )


            }else
            {
                DialogUtils.onShowDialog(globalContext , globalContext.getString(R.string.error), globalContext.getString(R.string.url_opening_error))
                Util.trace("MULESOFT SESSION ERROR" , response.second.toString())
            }
        }
    }*/

    fun generateScoringGuideUrl(targetUrl: String, returnNetworkListener: NetworkListener )
    {
        val stickyHeader : HashMap<String, Any?>? = NetworkHandler.getStickySessionHeader(Preferences.getValue(PreferenceKeys.MULESOFT_ACCESS_TOKEN))


        val params = NetworkHandler.getStickyRawJSONForScoringGuide(Preferences.getValue(PreferenceKeys.MULESOFT_ACCESS_TOKEN)!!, BuildConfig.ENV_CAPS,
            Preferences.getValue(PreferenceKeys.OPENAM_TOKEN)!!,
            BuildConfig.MULE_ENV , targetUrl)

        val muleSoftStickySessionGenerator = NetworkHandler(globalContext,
            MuleSoftNetworkConstants.CREATE_STICKY_INFO_SESSION,
            params,
            returnNetworkListener,
            stickyHeader)

        muleSoftStickySessionGenerator.setSilentMode(true)
        muleSoftStickySessionGenerator.execute()
    }



}
