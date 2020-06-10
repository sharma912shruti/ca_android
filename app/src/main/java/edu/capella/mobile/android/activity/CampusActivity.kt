package edu.capella.mobile.android.activity


import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Pair
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.utils.Util.dayAgo
import edu.capella.mobile.android.utils.Util.openPlayStore
import edu.capella.mobile.android.adapters.CampusAdapter
import edu.capella.mobile.android.bean.CampusAlertBean
import edu.capella.mobile.android.bean.CampusNewsBean
import edu.capella.mobile.android.bean.EventMessage
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.EventListener
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.HubbleNetworkConstants
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import kotlinx.android.synthetic.main.activity_campus.*
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.coach_screen.*
import kotlinx.android.synthetic.main.toolbar_drawer.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set


/**
 * CampusActivity.kt :  Screen responsible for showing the list of Campus news as well as list
 * of campus alerts.
 *
 * @author  :  Kush.pandya
 * @author  :  Kush.pandya
 * @version :  1.0
 * @since   :  03-02-2020
 *
 *
 */
open class CampusActivity : MenuActivity(), NetworkListener,
    ConnectivityReceiver.ConnectivityReceiverListener {

    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null

    private var  checkNewResponse: Boolean? = true

    /**
     * List of campus news received from server.
     */
    var campusNewsList = ArrayList<CampusNewsBean.NewsItem>()
    var campusAlertList = ArrayList<EventMessage?>()

    inner class CommonClass() {
        var itemType: String = ""
        var itemData: Any? = null
    }

    var finalList: ArrayList<CommonClass> = ArrayList()

    fun setNewsData() {
        OmnitureTrack.trackAction("news:home")
        finalList.clear()
        for (item in campusNewsList) {
            var tmpObj = CommonClass()
            tmpObj.itemType = "News"
            tmpObj.itemData = item
            finalList!!.add(tmpObj)
        }
        if (campusNewsAdapter != null) {
            campusNewsAdapter!!.notifyDataSetChanged()
            noInfoTxt.visibility = View.GONE
        }

    }

    /**
     * Listener created for dialog box which appears when Keep me signed in checkbox is checked.
     */
    inner class ItemEventListener : EventListener {

        override fun update() {
            super.update()

            navigateForOldVersion()
            openPlayStore(this@CampusActivity)
        }

        override fun cancleUpdate() {
            super.cancleUpdate()
            navigateForOldVersion()
        }
    }

    private fun navigateForOldVersion()
    {
        try
        {
            Preferences.removeKey(PreferenceKeys.KEEP_ME_SIGNED_IN.toString())
            Preferences.removeKey(PreferenceKeys.SECRET_USER.toString())
            Preferences.removeKey(PreferenceKeys.SECRET.toString())
            //          clear profile data
            Preferences.removeKey(PreferenceKeys.LEARNER_PROFILE.toString())
            Preferences.removeKey(PreferenceKeys.CACHED_LOGO_PATH.toString())


            val intent = Intent(this@CampusActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        } catch (t:Throwable) {
        }
    }



    private fun openAppVersionCheckDialog() {
        DialogUtils.showDialogOnUpdateVersion(this, ItemEventListener())
    }

    fun setAlertData() {
        if(!checkNewResponse!!)
        {
            finalList.clear()
        }
        for (item in campusAlertList) {
            var tmpObj = CommonClass()
            tmpObj.itemType = "Alert"
            tmpObj.itemData = item
            finalList!!.add(tmpObj)
        }

        var newSortedList =
            finalList.sortedWith(compareBy(CommonClass::itemType, CommonClass::itemType))
        finalList.clear()
        finalList.addAll(newSortedList)

        if (campusNewsAdapter != null) {
            campusNewsAdapter!!.notifyDataSetChanged()
//            noInfoTxt.visibility = View.GONE
        }

    }

    /**
     * Adapter for handling list behaviour.
     * @see RecyclerView.Adapter
     */
    var campusNewsAdapter: CampusAdapter? = null

    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentChildView(R.layout.activity_campus)
        toolbarTitle.text = getString(R.string.menu_campus_news)
        initialSetUp()
        initialiseListener()
        initializeAdapter()
//        if (Preferences.getBoolean(PreferenceKeys.FIRST_TIME_LOGIN)) {
//            updateCampusData()
//        }
        updateCampusData()
        loadProfileInformation()

        OmnitureTrack.trackAction("news")

        try
        {

                var isUpdateCheckRequired =
                    intent.getBooleanExtra(Constants.IS_APP_UPDATE_CHECK_REQUIRED, false)

                if (isUpdateCheckRequired)
//                if(true)
                {
                    if(BuildConfig.VERSION_NAME.toString().contains(Constants.PROD))
                    {
                        callAppVersionVerifierAPIProd()
                    }
                    else
                    {
                        callAppVersionVerifierAPI()
                        //Call API for Update check
                    }

                }

        }catch (t:Throwable){

        }




    }

    /**
     *  Method creates a server request to load data from server for Campus New and Campus Alert.
     */
    @SuppressLint("SetTextI18n")
    private fun updateCampusData() {


        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        val loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        OmnitureTrack.trackAction("news:home")
        callCampusNewsApi(
            loginBean?.authData?.username!!,
            Preferences.getValue(PreferenceKeys.SECRET)!!
        )
        OmnitureTrack.trackAction("news:alert-detail")
        callCampusAlert(loginBean)
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
            //NETWORK AVAILABLE , Reload API, But Make sure any call is not in progress

            if(this.getCurrentNetworkQueueSize()==0) {
                DialogUtils.releaseGenericDialog()
                updateCampusData()
                loadProfileInformation()
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
        ConnectivityReceiver.connectivityReceiverListener = this@CampusActivity
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
     * Initializes adapter class responsible to rendering the list over screen.
     *
     */
    private fun initializeAdapter() {

        campusListView.layoutManager = LinearLayoutManager(this)
        campusNewsAdapter =
            CampusAdapter(
                this,
                campusNewsList,
                finalList!!,
                object : CampusAdapter.OnItemClickListener {

                    override fun onNewsItemClicked(newsitem: CampusNewsBean.NewsItem) {
                        OmnitureTrack.trackAction("news:detail")
                        openCampusDetailScreen(newsitem)
                    }

                    override fun onAlertItemClicked(message: EventMessage) {
                        OmnitureTrack.trackAction("news:alert-detail")
                        openAlertDetail(message)
                    }

                })
        campusListView.adapter = campusNewsAdapter



    }

    /**
     *  Method creates a server request to load data from server for Campus Alert.
     */
    private fun callCampusAlert(loginBean: LoginBean?) {
        val authHeader = NetworkHandler.getAuthorizationHeader()
        val hubbleHeaders = NetworkHandler.getHeadersForHubbleRequest(
            loginBean?.authData?.token,
            HubbleNetworkConstants.getAcceptHeaderValue(HubbleNetworkConstants.CAMPUS_ALERT)
        )

        val finalHeaders = NetworkHandler.meargeHeaders(authHeader, hubbleHeaders)

        val params = HashMap<String, Any>()
        params[NetworkConstants.MESSAGE_TYPE] = "MOBILE_ALERT"
        params[NetworkConstants.MESSAGE_STATUS] = "CURRENT"

        val alertUrl = HubbleNetworkConstants.getUrl(
            HubbleNetworkConstants.CAMPUS_ALERT,
            "{{employeeId}}",
            "" + loginBean?.authData?.employeeId!!.value
        )


        val networkHandler = NetworkHandler(
            this,
            alertUrl,
            params,
            NetworkHandler.METHOD_GET,
            campusAlertListener,
            finalHeaders
        )
        networkHandler.setSilentMode(true)
        networkHandler.execute()

        if (cachedLogoImagePath == null) {

            loadProfileImage(loginBean)
        }
        else {

            setProfileLogoImage(cachedLogoImagePath)
        }
    }

    /**
     * Factory method of activity.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /**
     *  Method executed once used press back key or return key.
     */
    override fun onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }else {
            goToBackGround()
        }
    }
    override fun kill()
    {
        Util.trace("Killeed")
          goToBackGround()
    }

    /**
     *  Method sends application to background.
     */
    fun goToBackGround() {
        val i = Intent()
        i.action = Intent.ACTION_MAIN
        i.addCategory(Intent.CATEGORY_HOME)
        this.startActivity(i)
    }

    /**
     *  Method responsible to showing coach screen.
     */
    private fun initialSetUp() {

        this.campusSwipeRefresh.setColorSchemeColors(ContextCompat.getColor(this@CampusActivity, R.color.checkBoxColor))
        this.campusSwipeRefresh.setOnRefreshListener {
            //            finalList.clear()
            updateCampusData()
            loadProfileInformation()
            campusSwipeRefresh.isRefreshing = false


        }
        coachView.setOnClickListener {
            OmnitureTrack.trackAction("coach-mark:home")
        }
        if (!Preferences.getBoolean(PreferenceKeys.FIRST_TIME_LOGIN)) {
            drawerImageView.isEnabled = false
            coachView.visibility = View.VISIBLE
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            drawerImageView.isEnabled = true
            coachView.visibility = View.GONE
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

    /**
     * Method setting click listener for OK button over coach screen.
     *
     */
    private fun initialiseListener() {
        coachOkButton.setOnClickListener {
            OmnitureTrack.trackAction("coach-mark:home")
            Preferences.addBoolean(PreferenceKeys.FIRST_TIME_LOGIN, true)
            initialSetUp()

        }

    }

    /**
     * Method executes when user taps over any campus news item available over screen, displays
     * Campus Detail Activity which shows more details about selected campus news.
     *
     * @param newsItem : Object of class NewsItem contains detail about Campus News i.e.
     * title, description.
     */
    private fun openCampusDetailScreen(newsItem: CampusNewsBean.NewsItem) {

        OmnitureTrack.trackAction("news:detail-linkout")

        val intent = Intent(this, CampusDetailActivity::class.java)
        intent.putExtra(Constants.ITEM_LINK, newsItem.itemLink)
        intent.putExtra(Constants.ITEM_DESCRIPTION, newsItem.itemDescription)
        intent.putExtra(Constants.ITEM_TITLE, newsItem.itemTitle)
        intent.putExtra(Constants.DATE, newsItem.itemPublishDate.toString())
        startActivity(intent)
    }

    /**
     * Method executes when user taps over any campus alert item available over screen, displays
     * Alert Detail Activity which shows more details about selected campus news.
     *
     * @param eventMessage : Object of class EventMessage contains detail about Campus Alert.
     *
     */
    private fun openAlertDetail(eventMessage: EventMessage) {
        val intent = Intent(this, AlertDetailActivity::class.java)
        intent.putExtra(Constants.ALERT_DATA, eventMessage)
        startActivity(intent)
    }

    /**
     *  Factory method of activity, executes when application comes in resume state from pause state.
     *
     */
    override fun onResume() {
        super.onResume()
        init()
        //updateCampusData() //No needs to write here , we are adding PullTORefresh
    }


    /**
     * Method creates a server request to load data from server for Campus News.
     *
     * @param userName
     * @param password
     */
    private fun callCampusNewsApi(userName: String, password: String) {
        val params = HashMap<String, Any>()
        params[NetworkConstants.USER_NAME] = userName
        params[NetworkConstants.PASSWORD] = password
        params[NetworkConstants.PARSERSS] = true

        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.CAMPUS_NEWS_API,
            params,
            NetworkHandler.METHOD_POST,
            this, null
        )
        networkHandler.setSilentMode(true)
        networkHandler.execute()
    }


    /**
     * Callback method of Network task which executes when communication completed with server
     *
     * @param response : API response received from server.
     * @see Pair
     */
    override fun onNetworkResponse(response: Pair<String, Any>) {
        try{
        Util.trace("campus news first :  " + response.first)
       // Util.trace("campus news second :  " + response.second)

        if (response.first == NetworkConstants.SUCCESS) {
            val gson = Gson()
            val campusNewsBean = gson.fromJson<CampusNewsBean>(
                response.second.toString(),
                CampusNewsBean::class.java
            )
            Util.trace("campus News size", campusNewsBean?.newsData?.newsItems?.size.toString())

            campusNewsList.clear()

            if (campusNewsBean?.errorData == null) {

                var localList=campusNewsBean?.newsData?.newsItems!!

                for(value in localList)
                {
                    if(dayAgo(Util.getDate(value.itemPublishDate, Constants.DATE_FORMAT_TIME)!!,this@CampusActivity)<=122)
                    {
                        campusNewsList.add(value)
                    }

                }
//                campusNewsList.addAll(campusNewsBean?.newsData?.newsItems!!)
                if (campusNewsList.size > 0) {
                    checkNewResponse=true
                    setNewsData()


                } else {
                    checkNewResponse=false
                    noInfoTxt.visibility = View.VISIBLE
                }
                //Util.trace("CampusNewsBean sting  :  $campusNewsBean")
            } else {
                checkNewResponse=false
                noInfoTxt.visibility = View.VISIBLE

                DialogUtils.showGenericErrorDialog(this)

            }

        } else {
            DialogUtils.showGenericErrorDialog(this)

        }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }


    /**
     * Method loads profile image of logged in user from server, and store that image in cache of
     * application.
     *
     * @param loginBean : parameter required to execure profile picture api which contains user
     * information.
     */
    private fun loadProfileImage(loginBean: LoginBean) {
        val params = HashMap<String, Any>()
        params["employeeId"] = "" + loginBean.authData?.employeeId?.value
        params["token"] = "" + loginBean.authData?.token

        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.PROFILE_IMAGE_API,
            params,
            NetworkHandler.METHOD_READ_FILE,
            logoImageListener,
            null
        )

        networkHandler.execute()
    }

    /**
     * Network listener which executes once User profile picture is received or failed from server.
     */
    private val logoImageListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {
            try {
                if (response.first == NetworkConstants.SUCCESS) {
                    Util.trace("Logo Path is : " + response.second.toString())
                    Preferences.addValue(
                        PreferenceKeys.CACHED_LOGO_PATH,
                        response.second.toString()
                    )
                    cachedLogoImagePath = response.second.toString()
                    setProfileLogoImage(cachedLogoImagePath)

                } else {
                    Util.trace("Profile Logo error" + response.second.toString())
                }
            }catch (e:Exception)
            {
                e.printStackTrace()
            }
        }
    }


    /**
     *  Network listener which executes once campus alert response is received or failed from server.
     */
    private val campusAlertListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {
            try {
                Util.trace("Alert first :  " + response.first)
               // Util.trace("Alert second :  " + response.second)

                if (response.first == NetworkConstants.SUCCESS) {
                    val gson = Gson()
                    val campusAlertBean = gson.fromJson<CampusAlertBean>(
                        response.second.toString(),
                        CampusAlertBean::class.java
                    )
                    initializeAlertView(campusAlertBean.eventMessage)
                    // TODO
                } else {

                    //TODO need to hadle this seneriao
//                DialogUtils.onShowDialog(
//                    this@CampusActivity,
//                    resources.getString(R.string.network_error),
//                    response.second.toString()
//                )
                }
            }catch (e:Exception)
            {
                e.printStackTrace()
            }


        }
    }

    /**
     * Method sets data inside Campus alert layout once its available.
     *
     * @param eventMessage : Message detail of Campus alert.
     */
    private fun initializeAlertView(eventMessage: ArrayList<EventMessage>?) {
        campusAlertList.clear()
        campusAlertList.addAll(eventMessage!!)
        setAlertData()


    }

    /**
     * Method returns the size of Campus news
     *
     * @return : size of array list containing campus news
     */
    fun isDataInCampusNewsList(): Boolean {
        return campusNewsList.size > 0
    }

    /**
     * Method executes to load basic information about logged in user i.e. first name, last name etc.
     *
     */
    fun loadProfileInformation() {


        super.callLearnerProfileApi(lernerProfileListener)
    }

    /**
     * Neetwork listener executed once api communication done with LearnerProfile.
     */
    private val lernerProfileListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {

           



            try {
                if (response.first == NetworkConstants.SUCCESS) {
                    // val gson = Gson()
                    // val learnerBean = gson.fromJson<LearnerInformation>(response.second.toString(), LearnerInformation::class.java)
                    Preferences.addValue(PreferenceKeys.LEARNER_PROFILE, response.second.toString())
                    this@CampusActivity.updateMenuUserTitle()

                }
            } catch (t: Throwable) {

            }


        }
    }


    /***************APP VERSION CHECK*********************/

    private fun callAppVersionVerifierAPIProd() {
        // checkAPI=true
        val params = HashMap<String, Any>()

        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.APP_VERSION_PROD,
            params,
            NetworkHandler.METHOD_POST,
            updateListener, null
        )

        networkHandler.setSilentMode(true)
        networkHandler.execute()
    }

    private fun callAppVersionVerifierAPI() {
        // checkAPI=true
        val params = HashMap<String, Any>()

        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.APP_VERSION,
            params,
            NetworkHandler.METHOD_POST,
            updateListener, null
        )

        networkHandler.setSilentMode(true)
        networkHandler.execute()
    }
    private val updateListener: NetworkListener = object : NetworkListener
    {
        override fun onNetworkResponse(response: Pair<String, Any>)
        {
            try {
                Util.trace("version first :  " + response.first)
              //  Util.trace("version second :  " + response.second)

                if (response.first == NetworkConstants.SUCCESS) {

                    val serverAppVersion = response.second.toString()
                    val versionCode = BuildConfig.VERSION_NAME.toString()
                    val splitValue = versionCode.split("\\s".toRegex())[0]
                    val appVersion = splitValue.replace(".", "")
                   if(serverAppVersion.toInt()>appVersion.toInt())
                    //if (true)
                    {
                        openAppVersionCheckDialog()
                    }


                }
            }catch (t:Throwable){}
        }
    }

}
