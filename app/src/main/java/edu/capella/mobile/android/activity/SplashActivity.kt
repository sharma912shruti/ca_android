package edu.capella.mobile.android.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.Constants
import edu.capella.mobile.android.utils.PreferenceKeys
import edu.capella.mobile.android.utils.Preferences
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.app.CapellaApplication
import edu.capella.mobile.android.app.ProtectorService
import edu.capella.mobile.android.base.BaseActivity

/**
 * Splash Activity.kt :  Initial screen appear for 2 seconds, this screen does not appear
 * if the app is idle for a few minutes and app timeout occurs
 *
 * @author  :  Kush.pandya
 * @version :  1.0
 * @since   :  03-02-2020
 */

class SplashActivity : BaseActivity()
{

    /**
     * A Long value used as a timeout value for splash screen, default value is 2000 mili
     * seconds.
     */
     val splashTimeout: Long = 2000


    /**
     * A Handler object to which will execute LoginActivity after 2 seconds
     */
    private lateinit var splashHandler: Handler



    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?)
    {

        // Possible work around for market launches. See https://issuetracker.google.com/issues/36907463
// for more details. Essentially, the market launches the main activity on top of other activities.
// we never want this to happen. Instead, we check if we are the root and if not, we finish.
       /* if (!isTaskRoot())
        {
            var nam = (application as CapellaApplication).getCurrentRunningActivity()!!.localClassName.toString()

            Util.trace("Create Name is $nam")
            finish()

            return
            *//*if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                Util.trace("Main Activity is not the root.  Finishing Main Activity instead of launching.")
                finish();
                return;
            }*//*
        }*/


        super.onCreate(savedInstanceState)

        if (!isTaskRoot()
            && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
            && getIntent().getAction() != null
            && getIntent().getAction().equals(Intent.ACTION_MAIN)) {

            /*
             (application as CapellaApplication).checkTimeoutExplicitly = true*/
            finish();
            return;
        }
        //window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)
        (applicationContext as CapellaApplication).clearOldData()

        initProtectorService()
        init()
    }

    /**
     * method to invoke login screen after 2 seconds
     * @see splashTimeout
     */
    private fun init() {

        splashHandler = Handler()
        splashHandler.postDelayed({


            if((applicationContext as CapellaApplication).IS_APP_DISPLAYING)
            {
                val loginIntent = Intent(this@SplashActivity, LoginActivity::class.java)
               // val i = Intent(this@SplashActivity, TypographyActivity::class.java)

                val isAppWasWipedFromRecent =Preferences.getBoolean(PreferenceKeys.APP_KILLED_FROM_RECENT )
                var reloadWithOldCredentials = false
                if(isAppWasWipedFromRecent)
                {
                   var uId =  Preferences.getValue(PreferenceKeys.SECRET_USER)
                   var uPsw =  Preferences.getValue(PreferenceKeys.SECRET)
                    if(uId != "" && uPsw != "")
                    {
                        reloadWithOldCredentials = true
                        if(isTimeOverThenTimeout())
                        {
                            reloadWithOldCredentials = false
                        }
                    }
                }

                var isRemember = Preferences.getBoolean(PreferenceKeys.KEEP_ME_SIGNED_IN)
                if(isRemember ||reloadWithOldCredentials )
                {
                    loginIntent.putExtra(Constants.IS_AUTO_FORWARD , true)
                }

                var isTimeOver = isTimeOverThenTimeout()
                Util.trace("State - TimeOver $isTimeOver" )
                if(isTimeOver)
                {

                    loginIntent.putExtra(Constants.IS_TIME_ERROR, true)
                }


                startActivity(loginIntent)
                finish()
            }else
            {
                Util.trace("App not in foreground, Stopping splash")
                finish()
            }

        }, splashTimeout)

    }

    /**
     * Factory method of activity, executes when activity goes in paused state.
     *
     */
    override fun onPause() {
        super.onPause()
        //splashHandler.removeCallbacksAndMessages(null)
    }



    private fun initProtectorService()
    {
        try {
            val protectorServiceIntent =
                Intent(this, ProtectorService::class.java)
            this.startService(protectorServiceIntent)
        }catch (t:Throwable){}


    }

     fun isTimeOverThenTimeout():Boolean
    {
        if(Preferences.getValue(PreferenceKeys.STARTING_BACKGROUND_TIME) !="")
        {
            try
            {
                val lastTime = Preferences.getValue(PreferenceKeys.STARTING_BACKGROUND_TIME)!!.toLong()
                val diff = Util.getDifferentInMinutes(lastTime)
                if (diff != null && diff!! >= Constants.APP_TIMEOUT_MINS)
                {
                   return true
                }
            }catch (t:Throwable)
            {
                Util.trace("Kill Timeout check failed $t")
                t.printStackTrace()
            }

        }
        return false
    }
}
