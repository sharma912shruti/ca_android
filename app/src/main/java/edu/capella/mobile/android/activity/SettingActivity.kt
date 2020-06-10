package edu.capella.mobile.android.activity

import android.os.Bundle

import android.widget.CompoundButton
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.OmnitureTrack
import edu.capella.mobile.android.utils.PreferenceKeys
import edu.capella.mobile.android.utils.Preferences
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.toolbar_drawer.*


/**
 * SettingActivity.kt :

 * @author  :  Kush Pandya
 * @version :  1.0
 * @since   :  24-02-2020
 *
 */
class SettingActivity : MenuActivity(){

    /*
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentChildView(R.layout.activity_setting)
        toolbarTitle.text = getString(R.string.settings)
        init()
        setVersionValue()
        attachListener()
        OmnitureTrack.trackState("settings")

        scaleSwitchForTablet()

    }

    private fun scaleSwitchForTablet()
    {
        try {
            if (resources.getBoolean(R.bool.portrait_only) == false) {
                toggleKeepMe.scaleX = 2f
                toggleKeepMe.scaleY = 2f

                togglePushNotification.scaleX = 2f
                togglePushNotification.scaleY = 2f
            }
        }catch (t:Throwable){}
    }

    /**
     * init() method used to initialize value of variables
     */
    private fun init()
    {



        if(Preferences.getBoolean(PreferenceKeys.KEEP_ME_SIGNED_IN))
        {
            toggleKeepMe.isChecked=true
            keepMeSigneTxt.contentDescription=resources.getString(R.string.ada_keep_me_sign_in_setting)+","+resources.getString(R.string.ada_on)
            OmnitureTrack.trackAction("autologin:true:EMPID")
        }
        else
        {
            toggleKeepMe.isChecked=false
            keepMeSigneTxt.contentDescription=resources.getString(R.string.ada_keep_me_sign_in_setting)+","+resources.getString(R.string.ada_off)
            OmnitureTrack.trackAction("autologin:false:EMPID")

        }

        if(Preferences.getBoolean(PreferenceKeys.PUSH_NOTIFICATION))
        {
            togglePushNotification.isChecked=true
            pushNotificationTxt.contentDescription=resources.getString(R.string.ada_push_notification)+","+resources.getString(R.string.ada_on)
        }
        else
        {
            togglePushNotification.isChecked=false
            pushNotificationTxt.contentDescription=resources.getString(R.string.ada_push_notification)+","+resources.getString(R.string.ada_off)

        }

    }

    /**
     *  Method set capella application version detail over screen.
     *
     */
    private fun setVersionValue(){


       // capellaVersion.text = getString(R.string.capella_mobile_version) + " "+BuildConfig.VERSION_PREFIX+""+ BuildConfig.VERSION_ID+" ("+BuildConfig.VERSION_CODE+")"
        versionTxt.text =
            resources.getString(R.string.capella_mobile) + " " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")"

    }

    private  fun attachListener()
    {
        toggleKeepMe.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
              val  user=Preferences.getValue(PreferenceKeys.SECRET_USER)!!
               val psw=Preferences.getValue(PreferenceKeys.SECRET)!!

                Preferences.addValue(
                    PreferenceKeys.EMAIL, user

                )
                Preferences.addValue(
                    PreferenceKeys.SECRET,
                    psw
                )

                Preferences.addBoolean(PreferenceKeys.KEEP_ME_SIGNED_IN,true)
                keepMeSigneTxt.contentDescription=resources.getString(R.string.ada_keep_me_sign_in_setting)+","+resources.getString(R.string.ada_on)
                OmnitureTrack.trackAction("autologin:true:EMPID")

            } else {
                Preferences.addBoolean(PreferenceKeys.KEEP_ME_SIGNED_IN,false)
                keepMeSigneTxt.contentDescription=resources.getString(R.string.ada_keep_me_sign_in_setting)+","+resources.getString(R.string.ada_off)
                OmnitureTrack.trackAction("autologin:false:EMPID")

            }
        })

        togglePushNotification.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Preferences.addBoolean(PreferenceKeys.PUSH_NOTIFICATION,true)
                pushNotificationTxt.contentDescription=resources.getString(R.string.ada_push_notification)+","+resources.getString(R.string.ada_on)
            } else {
                Preferences.addBoolean(PreferenceKeys.PUSH_NOTIFICATION,false)
                pushNotificationTxt.contentDescription=resources.getString(R.string.ada_push_notification)+","+resources.getString(R.string.ada_off)

            }
        })
    }




}
