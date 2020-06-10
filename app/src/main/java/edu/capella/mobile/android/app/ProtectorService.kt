package edu.capella.mobile.android.app

import android.app.Service
import android.content.Intent
import android.os.IBinder
import edu.capella.mobile.android.activity.LoginActivity
import edu.capella.mobile.android.utils.PreferenceKeys
import edu.capella.mobile.android.utils.Preferences
import edu.capella.mobile.android.utils.Util

class ProtectorService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
         return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        var needToHandleForeceStop = true

        /**
         *  check NoAccessMobileActivity is active or not
         */
        if(LoginActivity.noAccessMobileActive)
        {
            needToHandleForeceStop=false
        }

       /* if(Preferences.getValue(PreferenceKeys.STARTING_BACKGROUND_TIME) !="")
        {
            try
            {
                val lastTime = Preferences.getValue(PreferenceKeys.STARTING_BACKGROUND_TIME)!!.toLong()
                val diff = Util.getDifferentInMinutes(lastTime)
                if (diff != null && diff!! >= Constants.APP_TIMEOUT_MINS)
                {
                    needToHandleForeceStop = false;
                }
            }catch (t:Throwable)
            {
                Util.trace("Kill Timeout check failed $t")
                t.printStackTrace()
            }

        }*/
        if(needToHandleForeceStop)
            Preferences.addBoolean(PreferenceKeys.APP_KILLED_FROM_RECENT , true)

        Util.trace("App removed from recent")
        this.stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        Util.trace("App destroyed")
    }



}
