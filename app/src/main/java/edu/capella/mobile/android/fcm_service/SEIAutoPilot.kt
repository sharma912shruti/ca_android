package edu.capella.mobile.android.fcm_service

import android.content.Context
import android.util.Log
import com.urbanairship.AirshipConfigOptions
import com.urbanairship.Autopilot
import com.urbanairship.UAirship
import androidx.core.content.ContextCompat
import com.urbanairship.BuildConfig
import edu.capella.mobile.android.R


class SEIAutoPilot: Autopilot() {

    override fun onAirshipReady(airship: UAirship) {
        super.onAirshipReady(airship)
        airship.pushManager.userNotificationsEnabled = true
        Log.e("airship ready", "airship ready")
    }

    override fun createAirshipConfigOptions(context: Context): AirshipConfigOptions? {

        return AirshipConfigOptions.Builder()
            .setDevelopmentAppKey("z2xoX6teRmesW8by8TkOBQ")
            .setDevelopmentAppSecret("wm_DBy03ToqSGgP2GnNyZw")
            .setProductionAppKey("z2xoX6teRmesW8by8TkOBQ")
            .setProductionAppSecret("wm_DBy03ToqSGgP2GnNyZw")
            .setInProduction(!BuildConfig.DEBUG)
            .setNotificationIcon(R.drawable.ic_logo)
            .setNotificationAccentColor(ContextCompat.getColor(context,R.color.colorPrimary))
            .setNotificationChannel("customChannel")
            .build()


    }
}