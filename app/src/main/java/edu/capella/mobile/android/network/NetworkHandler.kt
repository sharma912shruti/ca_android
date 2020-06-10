package edu.capella.mobile.android.network

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.util.Base64
import android.util.Pair
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.base.BaseActivity
import edu.capella.mobile.android.interfaces.NetworkListener
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


/**
 * NetworkHandler.kt : Class responsible for running network operation in asynchronous way to
 * secure UI Thread, Http methods supported are GET, POST, PUT. synchronous call for network is made
 * **NetworkService**
 *
 * Created by Jayesh Lahare on 03/02/20.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @see NetworkService
 *
 * @since 03/02/20.
 */
class NetworkHandler : AsyncTask<String, String, Pair<String, Any>> {

    @SuppressLint("StaticFieldLeak")

    private var screenIdForProgressBar: String? = null
    private var context: Context
    private var url: String? = null
    private var headers: HashMap<String, Any?>? = null
    private var paramaters: HashMap<String, Any>? = null

    private var jsonData: String? = null
    private var methodType: Short = 0

    private var listener: NetworkListener? = null

    var isPostTypeSubmitting: Boolean = false
    var submitMessage: String? = null

    var isSilentCall: Boolean = false

    var isNetworkCallInterrupted: Boolean = false

    var networkHandlerId = ""

    /**
     * Instantiates a new Network handler, This constructor do not have Network Listener feature.
     * but supports GET, POST & PUT http Methods
     *
     * @param context    : Context of application of activity for which network operation is going to                     perform. batter use application context to avoid context leak isuse.
     * @param url        : A Url link for which network operation is going to perform.
     * @param paramaters : Input parameters for GET or POST method.
     * @param methodType : Method type GET or POST.
     */
    constructor(
        context: Context,
        url: String,
        headers: HashMap<String, Any?>?,
        paramaters: HashMap<String, Any>,
        methodType: Short
    ) {
        this.url = url
        this.paramaters = paramaters
        this.context = context
        this.methodType = methodType
        this.headers = headers

        require(!(methodType != METHOD_GET && methodType != METHOD_POST && methodType != METHOD_READ_FILE && methodType != METHOD_POST_JSON)) {
            context.resources.getString(
                R.string.method_required
            )
        }
    }

    /**
     * Will execute network service without showing progress bar over screen over UI
     *
     * @param showProgress : Flag will decide whether show alert dialog or not.
     */
    fun setSilentMode(showProgress: Boolean) {
        this.isSilentCall = showProgress
    }

    /**
     * Instantiates a new Network handler., This constructor have Network Listener feature.
     * and supports GET, POST & PUT http Methods
     *
     * @param context         : Context of application of activity for which network operation is going to                     perform. batter use application context to avoid context leak isuse.
     * @param url             : A Url link for which network operation is going to perform.
     * @param paramaters      : Input parameters for GET or POST method.
     * @param methodType      : Method type GET or POST.
     * @param networkListener : A network listener interface used a callback for network response.
     * @see NetworkListener
     */
    constructor(
        context: Context,
        url: String,
        paramaters: HashMap<String, Any>,
        methodType: Short,
        networkListener: NetworkListener,
        headers: HashMap<String, Any?>?
    ) {
        this.url = url
        this.paramaters = paramaters
        this.context = context
        this.listener = networkListener
        this.methodType = methodType
        this.headers = headers

        require(!(methodType != METHOD_GET && methodType != METHOD_POST && methodType != METHOD_READ_FILE && methodType != METHOD_POST_JSON)) {
            context.resources.getString(
                R.string.method_required
            )
        }
    }

    constructor(
        context: Context,
        url: String,
        jsonData: String,
        methodType: Short,
        networkListener: NetworkListener,
        headers: HashMap<String, Any?>?
    ) {
        this.url = url
        this.jsonData = jsonData
        this.context = context
        this.listener = networkListener
        this.methodType = methodType
        this.headers = headers

        require(!(methodType != METHOD_GET && methodType != METHOD_POST && methodType != METHOD_READ_FILE && methodType != METHOD_POST_JSON)) {
            context.resources.getString(
                R.string.method_required
            )
        }
    }


    /**
     * Instantiates a new Network handler, This constructor supports INPUT type as JSON and have
     * Network Listener feature, it supports only PUT http Method
     *
     * @param context         the context
     * @param url             the url
     * @param jsonData        the json data
     * @param networkListener the network listener
     */
    constructor(
        context: Context,
        url: String,
        jsonData: String,
        networkListener: NetworkListener,
        headers: HashMap<String, Any?>?
    ) {
        this.url = url
        this.jsonData = jsonData
        this.context = context
        this.listener = networkListener
        this.methodType = METHOD_PUT
        this.headers = headers


    }

    fun setMethodType(methodType: Short) {
        this.methodType = methodType
    }

    /**
     * Method initializes progress bar, which displays during Network call execution.
     *
     * @param context
     */
    private fun initializeProgressBar() {
        try {
            if (!this.isSilentCall) {

                this.screenIdForProgressBar = ProgressBarHandler.generateScreenId()

                ProgressBarHandler.showProgressBar(
                    this.context,
                    this.screenIdForProgressBar.toString(),
                    isPostTypeSubmitting,
                    submitMessage
                )
            }

        } catch (t: Throwable) {
            Util.trace("ProgressBar", "Progress Init Issue $t")
            ProgressBarHandler.forceHide()
        }

    }


    /**
     * Sets network listener.
     *
     * @param listener : A network listener interface used a callback for network response.
     * @see NetworkListener
     */
    fun setNetworkListener(listener: NetworkListener) {
        this.listener = listener
    }

    fun interruptCall()
    {
        isNetworkCallInterrupted = true
    }

    /**
     * On pre execution of network task, synchronous method.
     */
    override fun onPreExecute() {
        super.onPreExecute()
        initializeProgressBar()

        registerNetworkCall()

    }

    /**
     * Method performs work of calling different Http request with the help of **NetworkService**
     * , its asynchronous method.
     *
     * @param strings the strings
     * @return the pair as response.
     */
    override fun doInBackground(vararg strings: String): Pair<String, Any>? {
        var pair: Pair<String, Any>? = null

        if(isNetworkCallInterrupted)
                return null
//        try {
//            Thread.sleep(200)
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }

        this.trustAllCertificates()


        if (Util.isInternetOn(context)) {
            if (this.url!!.startsWith("http:", true)) {
                when {
                    this.methodType == METHOD_GET -> pair =
                        NetworkService.sendHttpGet(this.url!!, this.paramaters, this.headers)

                    methodType == METHOD_POST -> pair =
                        NetworkService.sendHttpPost(this.url!!, this.paramaters, this.headers)

                    methodType == METHOD_PUT -> pair =
                        NetworkService.sendHttpPutJSON(this.url!!, this.jsonData, this.headers)

                    methodType == METHOD_POST_JSON -> pair =
                        NetworkService.sendHttpPostJSON(this.url!!, this.jsonData, this.headers)
                }
            } else if (this.url!!.startsWith("https:", true)) {
                when {
                    this.methodType == METHOD_GET -> pair =
                        NetworkService.sendHttpsGet(this.url!!, this.paramaters, this.headers)
                    methodType == METHOD_POST -> pair =
                        NetworkService.sendHttpsPost(this.url!!, this.paramaters, this.headers)
                    methodType == METHOD_READ_FILE -> pair =
                        NetworkService.getFileHttpsPost(
                            this.context.applicationContext,
                            this.url!!,
                            this.paramaters,
                            this.headers
                        )

                    methodType == METHOD_PUT -> pair =
                        NetworkService.sendHttpsPutJSON(this.url!!, this.jsonData, this.headers)

                    methodType == METHOD_POST_JSON -> pair =
                        NetworkService.sendHttpsPostJSON(this.url!!, this.jsonData, this.headers)
                }
            }
        } else {
            pair = Pair.create(
                NetworkConstants.NETWORK_INTERNET_ERROR,
                "Please connect with internet first."
            )
        }

        return pair
    }

    /**
     * Method executed after task is completed, its asynchronous method.
     *
     * @param pair the pair as response.
     */
    override fun onPostExecute(pair: Pair<String, Any>) {
        try {
            if(isNetworkCallInterrupted) {
                Util.trace("Interrupted")
                return
            }

            if (!this.isSilentCall) {

                ProgressBarHandler.hideProgressBar(this.screenIdForProgressBar.toString())
            }
            //ProgressBarHandler.hideProgressBar(this.screenIdForProgressBar.toString())
        } catch (t: Throwable) {
            ProgressBarHandler.forceHide()
            Util.trace("ProgressBar", "Progress Hide Issue $t")
        }

        if (listener != null)
            listener!!.onNetworkResponse(pair)


        release()
    }


    /**
     * Method used to handle HTTPS ssl certificate issue during network call.
     *
     */
    fun trustAllCertificates() {
        try {
            val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOfNulls(0)
                    }

                    override fun checkClientTrusted(
                        certs: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun checkServerTrusted(
                        certs: Array<X509Certificate>,
                        authType: String
                    ) {
                    }
                }
            )
            val sc = SSLContext.getInstance("SSL")
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier { arg0, arg1 -> true }
        } catch (e: Exception) {
        }
    }


    companion object {

        /**
         * The constant METHOD_GET.
         */
        var METHOD_GET: Short = 1
        /**
         * The constant METHOD_POST.
         */
        var METHOD_POST: Short = 2
        /**
         * The constant METHOD_POST_JSON.
         */
        var METHOD_POST_JSON: Short = 3
        /**
         * The constant METHOD_READ_FILE.
         */
        var METHOD_READ_FILE: Short = 4

        /**
         * The constant METHOD_PUT.
         */
        var METHOD_PUT: Short = 5

        /**
         * Method returns desired header required for communication with server.
         *
         * @return : Header with required request properties.
         */
        fun getAuthorizationHeader(): HashMap<String, Any?> {
            val authUser = BuildConfig.AUTH_USER
            val authPassword = BuildConfig.AUTH_PASSWORD
            //val authString = "$authUser:$authPassword"

            val credentials = authUser + ":" + authPassword
            val AUTH = "Basic " + Base64.encodeToString(
                credentials.toByteArray(Charsets.UTF_8),
                Base64.DEFAULT
            ).replace("\n", "")
            val authHeader: HashMap<String, Any?> = HashMap()
            authHeader.put("Authorization", AUTH)

            return authHeader
        }

        /**
         * Method returns desired header required for hubble api communication with server.
         *
         * @return : Header with required request properties.
         */
        fun getHeadersForHubbleRequest(
            capellaAuthHeader: String?,
            acceptValue: String?
        ): HashMap<String, Any?> {
            val hubbleHeader: HashMap<String, Any?> = HashMap()
            hubbleHeader.put("CapellaAuthToken", capellaAuthHeader)
            hubbleHeader.put("HubbleServiceClientId", "MOBILECLIENT")
            hubbleHeader.put("Content-Type", "application/json")
            hubbleHeader.put("Cache-Control", "no-cache")
//            hubbleHeader.put("Accept","application/vnd.capella.basic-course-info+json;version=15.08,application/vnd.capella.hubble-exception+json,application/vnd.capella.hubble-validation-exception+json")
//            hubbleHeader.put(
//                "Accept",
//                "application/vnd.capella.event-message+json;version=13.10,application/vnd.capella.hubble-exception+json,application/vnd.capella.hubble-validation-exception+json"
//            )
            hubbleHeader.put("Accept", acceptValue)



            return hubbleHeader
        }

        fun getHeadersForCapella(capellaAuthHeader: String?): HashMap<String, Any?> {
            val hubbleHeader: HashMap<String, Any?> = HashMap()
            hubbleHeader.put("CapellaAuthToken", capellaAuthHeader)
             hubbleHeader.put("Cache-Control", "no-cache")

            return hubbleHeader
        }

        /**
         * Method returns desired header required for hubble api communication with server.
         *
         * @return : Header with required request properties.
         */
        fun getHeadersForFP2Request(muleSoftToken: String?): HashMap<String, Any?> {
            val hubbleHeader: HashMap<String, Any?> = HashMap()
            hubbleHeader.put("access_token", muleSoftToken)
            hubbleHeader.put("Content-Type", "application/json")
            hubbleHeader.put("Cache-Control", "no-cache")
            return hubbleHeader
        }


        /**
         * Method returns desired header required for openAM api communication with server.
         *
         * @return : Header with required request properties.
         */
        fun getOpenAMHeader(userName: String?, password: String?): HashMap<String, Any?>? {
            val openAMHeader: HashMap<String, Any?>? = HashMap()
            openAMHeader?.put("X-OpenAM-Username", userName.toString())
            openAMHeader?.put("X-OpenAM-Password", password.toString())

            return openAMHeader
        }

        /**
         * Method returns desired header required for StickySession api communication with server.
         *
         * @return : Header with required request properties.
         */
        fun getStickySessionHeader(stickyToken: String?): HashMap<String, Any?>? {
            val stickyHeader: HashMap<String, Any?>? = HashMap()
            stickyHeader?.put("client_id", BuildConfig.MULESOFT_CLIENT_ID)
            stickyHeader?.put("Authorization", "Bearer " + stickyToken)
            stickyHeader?.put("Content-Type", "application/json")

            return stickyHeader
        }


        /**
         * Method returns JSON object required by http PUT method, it adds Url in JSON object
         * and returns, this method is part of feature where needs to open urls in external
         * browser.
         *
         * @return : Header with required request properties.
         */
        fun getStickyRawJSON(
            env_caps: String,
            authToken: String,
            mule_env: String,
            targetUrl: String
        ): String {
            //val json = "{  \"iplanetCookie\": {    \"name\": \"iPlanet"+env_caps+"DirectoryPro\",    \"value\": \""+authToken+"\",    \"options\" : \"domain=.capella.edu;path=/;Secure; HttpOnly\"  },  \"targetUrl\": \"https://"+mule_env+"campus.capella.edu\",  \"headers\": [    {      \"name\": \"test\",      \"value\": \"test\"    }  ]}"
            val json =
                "{  \"iplanetCookie\": {    \"name\": \"iPlanet" + env_caps + "DirectoryPro\",    \"value\": \"" + authToken + "\",    \"options\" : \"domain=.capella.edu;path=/;Secure; HttpOnly\"  },  \"targetUrl\": \"" + targetUrl + "\",  \"headers\": [    {      \"name\": \"test\",      \"value\": \"test\"    }  ]}"
            return json
        }

        fun getStickyRawJSONForScoringGuide(
            accessToken: String,
            env_caps: String,
            authToken: String,
            mule_env: String,
            targetUrl: String
        ): String {
            // val json = "{ \"accessToken\": \"" + accessToken+  "\",   \"iplanetCookie\": {    \"name\": \"iPlanet"+env_caps+"DirectoryPro\",    \"value\": \""+authToken+"\",    \"options\" : \"domain=.capella.edu;path=/;Secure; HttpOnly\"  },  \"targetUrl\": \""+targetUrl+"\",  \"headers\": [    {      \"name\": \"test\",      \"value\": \"test\"    }  ]}"
            val json =
                "{ \"accessToken\": \"" + accessToken + "\",   \"iplanetCookie\": {    \"name\": \"iPlanet" + env_caps + "DirectoryPro\",    \"value\": \"" + authToken + "\",    \"options\" : \"domain=.capella.edu;path=/;Secure; HttpOnly\"  },  \"targetUrl\": \"" + targetUrl + "\"}"
            return json
        }

        /**
         * Method combines two HashMap objects and returns single one.
         *
         * @param firstGroup : Source 1
         * @param secondGroup : Source 2
         * @return : Combined HashMap
         */
        fun meargeHeaders(
            firstGroup: HashMap<String, Any?>,
            secondGroup: HashMap<String, Any?>
        ): HashMap<String, Any?> {
            val finalGroup: HashMap<String, Any?> = HashMap()
            finalGroup.putAll(firstGroup)
            finalGroup.putAll(secondGroup)
            return finalGroup
        }
    }

    private fun registerNetworkCall()
    {
        try
        {
            if(context!=null && (context is BaseActivity))
            {
                networkHandlerId = ProgressBarHandler.generateScreenId()
                (context as BaseActivity).registerNetworkProcess(networkHandlerId , this)
            }
        }catch (t: Throwable){
            Util.trace("Net Registration Issue $t")
            t.printStackTrace()
        }
    }

    private fun release()
    {
        try
        {
            if(context!=null && (context is BaseActivity))
            {

                (context as BaseActivity).releaseNetworkProcess(networkHandlerId )
            }
        }catch (t: Throwable){
            Util.trace("Net Registration Release Issue $t")
            t.printStackTrace()
        }
    }
}
