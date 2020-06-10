package edu.capella.mobile.android.utils

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import edu.capella.mobile.android.utils.Constants.IS_TIME_ERROR
import edu.capella.mobile.android.activity.LoginActivity
import edu.capella.mobile.android.app.CapellaApplication
import edu.capella.mobile.android.interfaces.EventListener


/**
 *  AppLifecycleHandler.kt : A class provides callbacks for activity and application lifecycle events i.e. app in
 *  foreground, app in background, low memory etc.
 *
 * Created by Shruti Sharma on 03/02/20.
 *
 * @author Shruti Sharma
 * @version 1.0
 * @since 03/02/20.
 */
class AppLifecycleHandler(private val lifeCycleDelegate: LifeCycleDelegate) :
    Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {


    private var activityReferences = 0
    private var isActivityChangingConfigurations = false

    private var appInForeground = false

    var currentActivity: Activity? = null

    // show login activity method

    fun sessionExpired(context: Context)
    {
        Util.showSessionTimeout(context , object : EventListener {
            override fun confirm() {
                super.confirm()
                currentActivity?.finish()
                Preferences.removeKey(PreferenceKeys.KEEP_ME_SIGNED_IN.toString())
                Preferences.removeKey(PreferenceKeys.LEARNER_PROFILE.toString())
                Preferences.removeKey(PreferenceKeys.CACHED_LOGO_PATH.toString())
                normalLogin(currentActivity!!)
            }
            override fun cancel() {
            }
        })


    }
    fun normalLogin(context: Context)
    {
        val intent = Intent(context, LoginActivity::class.java)
       // intent.putExtra(IS_TIME_ERROR, true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun openLoginScreen(context: Context) {
        currentActivity?.finish()

       /* Preferences.removeKey(PreferenceKeys.SECRET.toString())
        Preferences.removeKey(PreferenceKeys.SECRET_USER.toString())*/

        if (Preferences.getBoolean(PreferenceKeys.KEEP_ME_SIGNED_IN))
        {
           // val intent = Intent(context, CampusActivity::class.java)
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra(Constants.IS_AUTO_FORWARD , true)
//            intent.putExtra(IS_TIME_ERROR , true)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } else {
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra(IS_TIME_ERROR, true)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

    }

    override fun onActivityPaused(activity: Activity?) {
        Util.trace("Activity Paused")

      //  currentActivity = activity
        Preferences.addValue(
            PreferenceKeys.CURRENT_ACTIVITY,
            currentActivity?.localClassName.toString()
        )


       /* if (appInForeground == true)
        {
            appInForeground = false
            Util.trace("Activity inBackground")
            lifeCycleDelegate.onAppBackgrounded()
        }*/


    }

    override fun onActivityResumed(activity: Activity?) {
       /* try {
            Util.trace("Activity Resume / Foreground Explicit = " + (activity!!.application as CapellaApplication).checkTimeoutExplicitly)
        }catch (t:Throwable){}*/
        currentActivity = activity
       /* if (appInForeground == false) {
            appInForeground = true
            lifeCycleDelegate.onAppForegrounded()
        }*/

    }

    override fun onActivityStarted(activity: Activity?) {

        currentActivity = activity
        if (++activityReferences == 1 && !isActivityChangingConfigurations)
        {
            // App enters foreground
            appInForeground = true
            Util.trace("Activity Foreground " +  currentActivity?.localClassName.toString())
            lifeCycleDelegate.onAppForegrounded()
        }
    }

    override fun onActivityDestroyed(p0: Activity?) {
        Util.trace("Activity destroyed")
    }

    override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {

        isActivityChangingConfigurations = activity!!.isChangingConfigurations
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            // App enters background
            currentActivity = activity
            appInForeground = false
            Util.trace("Activity Background " +  currentActivity?.localClassName.toString())
            lifeCycleDelegate.onAppBackgrounded()
        }
    }

    override fun onActivityCreated(p0: Activity?, p1: Bundle?) {
        Util.trace("Activity created")
    }

    override fun onLowMemory() {}

    override fun onConfigurationChanged(p0: Configuration?) {}

    override fun onTrimMemory(level: Int) {
        Util.trace("Activity onTrimMemory")

       /* if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            appInForeground = false
            Util.trace("Activity inBackground")
            lifeCycleDelegate.onAppBackgrounded()
        }*/
    }

}

interface LifeCycleDelegate {
    fun onAppBackgrounded()
    fun onAppForegrounded()
}
