package edu.capella.mobile.android.utils

import com.adobe.marketing.mobile.MobileCore
import edu.capella.mobile.android.BuildConfig


/**
 * OmnitureTrack.kt : Class Responsible for writing omniture action/state
 *
 * @author  :  jayesh.lahare
 * @version :  1.0
 * @since   :  3/17/2020
 *
 *
 */
object OmnitureTrack
{
    var profileId = ""
    var appVersion = ""
    var prefix : String =  ""

    /**
     * Method responsible for setting ProfileId and AppVersion in static variables, which are
     * passed with every tracking request over Omniture.
     */
    fun initBasic() {

        profileId = Preferences.getValue(PreferenceKeys.PROFILE_ID)!!
//        appVersion =    BuildConfig.VERSION_NAME+" ("+BuildConfig.VERSION_CODE+")" // Preferences.getValue(PreferenceKeys.APP_VERSION)!!
        appVersion = BuildConfig.VERSION_NAME_WITHOUT_SUFFIX
    }

    fun addPrefixForTest()
    {
        if (prefix == "")
        {
            var dummy = Util.getDummyDate()
            prefix = "Android-"+dummy +"-"
            Util.trace("Dummy is $dummy")
        }
        //Util.trace("Prefix $prefix")
    }

    /**
     * Method responsible for calling track request setting ProfileId, AppVersion, actionName then
     * passed over Omniture.
     */
    fun trackAction(actionName: String)
    {
        addPrefixForTest()



      /** Commenting for TDD Report, temporarly */
        val data: MutableMap<String, String> = HashMap()
        data["emplId"] = profileId
        data["appVersion"] = appVersion

        MobileCore.trackState(prefix+actionName, data)
    }


    /**
     * Method responsible for calling track request setting ProfileId, AppVersion, stateName then
     * passed over Omniture.
     */
    fun trackState(stateName: String)
    {
        addPrefixForTest()
        /** Commenting for TDD Report, temporarly */
        val data: MutableMap<String, String> = HashMap()
        data["emplId"] = profileId
        data["appVersion"] = appVersion

        MobileCore.trackState(prefix+stateName, data)
    }

}
