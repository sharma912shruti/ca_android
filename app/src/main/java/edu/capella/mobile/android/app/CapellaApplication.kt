package edu.capella.mobile.android.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Pair
import com.adobe.marketing.mobile.*
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import edu.capella.mobile.android.task.StickyInfoGrabber

import java.io.File
import java.util.*


/**

* CapellaApplication.kt :  Android application class, used to provide application context with
* certain customized featured for every activity working with this application
*
* This class provides handling of ideal timeout feature for activity. this class integrated in
* manifest file
*
* @author  :  Shruti.Sharma
* @version :  1.0
* @since   :  03-02-2020
*
*/

class CapellaApplication : Application(), LifeCycleDelegate {

    /**
     * public variable tell whether applications User Interface displaying or not.
     */
    var IS_APP_DISPLAYING: Boolean = false

    /*var checkTimeoutExplicitly = false*/

    /**
     * Tag variable for Printing Log
     */
    private val TAG = "Capella"

    /**
     * appTimer: used to keep track of idal timeout when app goes in background
     *
     */
    private var appTimer = Timer()

    /**
     * ApplifeCycleHandler gives different events of activity i.e. Whether app in foreground or in
     * background
     */
    lateinit var lifeCycleHandler: AppLifecycleHandler


    /**
     * Application class factory method where i initializes different listeners and callbacks.
     */
    override fun onCreate() {
        super.onCreate()
        lifeCycleHandler = AppLifecycleHandler(this)
        registerLifecycleHandler(lifeCycleHandler)
        Preferences.getInstance(this)

        initializeOmnitureSDK()
    }

    /**
     * method called when application goes in background
     * @see AppLifecycleHandler
     */
    override fun onAppBackgrounded() {

        IS_APP_DISPLAYING = false

        Util.trace("State - Background")
        if (lifeCycleHandler.currentActivity?.localClassName != Constants.LOGIN_ACTIVITY && lifeCycleHandler.currentActivity?.localClassName != Constants.SPLASH_ACTIVITY) {
            resetAppTimer()
            var backTime  = Util.getCurrentTime()
            if(backTime!=null)
            {
                Preferences.addValue(PreferenceKeys.STARTING_BACKGROUND_TIME , ""+backTime)
            }
            Util.trace("Timeout  back time $backTime")
        }

        /*****Omniture Pause Handling******/
       // MobileCore.lifecyclePause();
    }

    fun getCurrentRunningActivity() : Activity?
    {
        return lifeCycleHandler.currentActivity
    }

    /**
     * method called when application comes to foreground
     * @see AppLifecycleHandler
     */
    override fun onAppForegrounded() {

        Util.trace("State - Foreground")
        IS_APP_DISPLAYING = true

        if (lifeCycleHandler.currentActivity?.localClassName != Constants.LOGIN_ACTIVITY && lifeCycleHandler.currentActivity?.localClassName != Constants.SPLASH_ACTIVITY)
        //if (lifeCycleHandler.currentActivity?.localClassName != Constants.LOGIN_ACTIVITY && lifeCycleHandler.currentActivity?.localClassName != Constants.SPLASH_ACTIVITY)
        {


            if(Preferences.getValue(PreferenceKeys.STARTING_BACKGROUND_TIME) !="")
            {
                try
                {
                    val lastTime =   Preferences.getValue(PreferenceKeys.STARTING_BACKGROUND_TIME)!!.toLong()

                    val diff = Util.getDifferentInMinutes(lastTime)
                    Util.trace("Timeout Last Time different in mins : $diff" )
                    if (diff != null && diff!! >= Constants.APP_TIMEOUT_MINS)
                    {
                        // IGNORE THESE 2 SCREENS

                        var isIgnore : Boolean = ((lifeCycleHandler.currentActivity?.localClassName == Constants.NAME_CREATE_ACTIVITY ||
                                lifeCycleHandler.currentActivity?.localClassName == Constants.NAME_EDIT_DRAFT_ACTIVITY) && Preferences.getBoolean(PreferenceKeys.KEEP_ME_SIGNED_IN) )

                        //isIgnore = (isIgnore && Preferences.getBoolean(PreferenceKeys.IS_EDITED))
                        isIgnore = Preferences.getBoolean(PreferenceKeys.IS_EDITED) &&  Preferences.getBoolean(PreferenceKeys.KEEP_ME_SIGNED_IN)

                        Util.trace("Is Ignore $isIgnore")
                        if(!isIgnore)
                        {
                            this.lifeCycleHandler.openLoginScreen(this)
                        }
                        clearAppTimer()
                    }else
                    {
                        checkPasswordChange(Preferences.getValue(PreferenceKeys.SECRET_USER)!! ,
                            Preferences.getValue(PreferenceKeys.SECRET)!!)

                    }

                }catch (t:Throwable)
                {
                    Util.trace("Timeout check failed $t")
                    t.printStackTrace()
                }

            }

        }






        if (lifeCycleHandler.currentActivity?.localClassName != Constants.LOGIN_ACTIVITY && lifeCycleHandler.currentActivity?.localClassName != Constants.SPLASH_ACTIVITY) {
            Util.trace("App comes in fore ground")

            validateOpenAMLogoutCase()

        }

        /*********Omniture Resume Call*********/
       // MobileCore.lifecycleStart(null);
    }

    fun validateOpenAMLogoutCase()
    {
        if(Preferences.getBoolean(PreferenceKeys.IS_APP_GONE_OUTSIDE ))
        {
            Preferences.removeKey(PreferenceKeys.IS_APP_GONE_OUTSIDE.toString())
            val task = StickyInfoGrabber(applicationContext)
            task.validateOpenAM()
        }
    }


    /**
     * Register callbacks to know whether app is in foreground or in background
     *
     * @param lifeCycleHandler : Parameter required to register, will track life cycle of
     * activity.
     *
     * @see AppLifecycleHandler
     *
     */
    private fun registerLifecycleHandler(lifeCycleHandler: AppLifecycleHandler) {
        registerActivityLifecycleCallbacks(lifeCycleHandler)
        registerComponentCallbacks(lifeCycleHandler)
    }

    /**
     * local method to clear or stop current running timer and create new instance and run it
     * again.
     */
    private fun resetAppTimer()
    {
       /* if (appTimer != null) {
            appTimer.cancel()
        }
        appTimer = Timer()
        var task = Task()

        appTimer.schedule(task, Constants.APP_TIMEOUT_SECONDS)*/

        Preferences.removeKey(PreferenceKeys.STARTING_BACKGROUND_TIME.toString())

    }

    /**
     * local method used to stop the timer.
     */
     fun clearAppTimer() {
       /* if (appTimer != null) {
            appTimer.cancel()
        }
        */
        Util.trace("State Resetting from class "  +lifeCycleHandler.currentActivity?.localClassName)
        Preferences.removeKey(PreferenceKeys.STARTING_BACKGROUND_TIME.toString())
    }

    /**
     * Task class used as a TimerTask for the app ideal timeout.
     *
     *  @see TimerTask
     */

 /*   class Task : TimerTask() {
        

        override fun run() {

            Preferences.addBoolean(PreferenceKeys.IS_TIMEOUT,true)
        }

    }*/

    /**
     * Method clears whole cached which was created in previous running of application, this method
     * executed from splash screen only.
     *
     */
    fun clearOldData()
    {
        val thread  = Thread{
            kotlin.run { deleteCache(applicationContext) }
        }
        thread.start()
    }

    /**
     * method deletes cached files one by one.
     *
     * @param context
     */
    private fun deleteCache(context: Context) {
        try {
            var fileCache = FileCache()
            fileCache.init(context)
            val dir: File =  fileCache.getCacheDirectory()!!
            deleteDir(dir)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * Method to delete files recursively.
     *
     * @param dir : Cache file directory which needs to be deleted.
     * @return
     */
    private fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children: Array<String> = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }

    /**
     *  Method responsible for Initializing for initializing Omniture SDK with Omniture APP ID.
     */
    private fun initializeOmnitureSDK()
    {

        try {
            MobileCore.setApplication(this)
            MobileCore.setLogLevel(LoggingMode.DEBUG)
            MobileServices.registerExtension()
            Analytics.registerExtension()
            UserProfile.registerExtension()
            Identity.registerExtension()
            Lifecycle.registerExtension()
            Signal.registerExtension()
            MobileCore.start { MobileCore.configureWithAppID(BuildConfig.OMNITURE_APP_ID) }
        } catch (e: InvalidInitException) {
            Util.trace("Omniture Failed " + e)
            e.printStackTrace()
        }
    }

    override fun onTerminate() {
        Util.trace("app terminated")
        super.onTerminate()
    }

    private fun checkPasswordChange(userName: String, password: String) {
        val params = HashMap<String, Any>()
        params.put(NetworkConstants.USER_NAME, userName)
        params.put(NetworkConstants.PASSWORD, password)


        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.LOGIN_API,
            params,
            NetworkHandler.METHOD_POST,
            loginListener,
            null
        )

        networkHandler.setSilentMode(true)
        networkHandler.execute()


    }

    private val loginListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {

            try {
                Util.trace("Logout first :  " + response.first)
                Util.trace("Logout second :  " + response.second)

                val gson = Gson()
                val loginBean =
                    gson.fromJson<LoginBean>(response.second.toString(), LoginBean::class.java)

                if(loginBean.errorData!=null && loginBean.errorData!!.message!! !=null && loginBean.errorData?.message!!.contains("Unable to authenticate with the data provided" , true))
                {
                    lifeCycleHandler.sessionExpired(lifeCycleHandler.currentActivity!!)
                }

            }catch (t:Throwable){}

        }
    }



}
