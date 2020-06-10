package edu.capella.mobile.android.activity

import android.content.IntentFilter
import android.os.Bundle
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.ConnectivityReceiver
import edu.capella.mobile.android.utils.DialogUtils
import edu.capella.mobile.android.utils.OmnitureTrack
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.task.StickyInfoGrabber
import kotlinx.android.synthetic.main.activity_contact_us.*
import kotlinx.android.synthetic.main.toolbar_drawer.*

/**
 * ContactUsActivity.kt :  Show details about Campus and gives functionality to contact with
 * toll free number or open university website in external browser
 *
 *  This screen contains following information <br>
 *  1.) united states toll free numbers information <br>
 *  2.) international number information <br>
 *  3.) Technical support,academic support and capella campus information.<br>
 *  4.) Capella university address.<br>
 *
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  24-02-2020
 *
 */
class ContactUsActivity : MenuActivity(), ConnectivityReceiver.ConnectivityReceiverListener {


    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null


    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentChildView(R.layout.activity_contact_us)
        toolbarTitle.text = getString(R.string.contact_us)
        attachListeners()
        setVersionValue()
        OmnitureTrack.trackState("contact:home")
    }


    /**
     * Method triggered by broadcast receiver which is listening for Network state.
     *
     * @param isConnected : Tells whether internet is available or not
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        /*if (!isConnected) {
            networkLayout.visibility = View.VISIBLE
        } else {
            networkLayout.visibility = View.GONE
        }*/
    }

    /**
     *  Factory method of activity, executes when application comes in resume state from pause state.
     *
     */
    override fun onResume() {
        super.onResume()
        init()
    }

    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun init()
    {
        ConnectivityReceiver.connectivityReceiverListener = this@ContactUsActivity
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
            ConnectivityReceiver.connectivityReceiverListener=null
        }
    }

    /**
     *  Method set capella application version detail over screen.
     *
     */
    private fun setVersionValue(){
       // capellaVersion.text = getString(R.string.capella_mobile_version) + " "+BuildConfig.VERSION_PREFIX+""+ BuildConfig.VERSION_ID+" ("+BuildConfig.VERSION_CODE+")"
        capellaVersion.text= getString(R.string.capella_mobile_version) +  " "+BuildConfig.VERSION_NAME+" ("+BuildConfig.VERSION_CODE+")"
    }



    /**
     * Method used to  bind the click events with certain widgets
     * used inside screen
     *
     */
    private fun attachListeners() {
        tollFreeNoCapella.setOnClickListener {
            Util.dialNumber(this, getString(R.string.capella_contact))
            OmnitureTrack.trackAction("contact:phone-linkout")
        }

        tollFreeNoSecond.setOnClickListener {
            Util.dialNumber(this, tollFreeNoSecond.text.toString())
            OmnitureTrack.trackAction("contact:phone-linkout")
        }

        internationalNo.setOnClickListener {
            Util.dialNumber(this, internationalNo.text.toString())
            OmnitureTrack.trackAction("contact:phone-linkout")
        }

        technicalSupportButton.setOnClickListener {
            val stickyWork  = StickyInfoGrabber(this)
            DialogUtils.screenNamePrefix = "contact:tech-support"
            stickyWork.generateMuleSoftStickySessionForTargetUrl(NetworkConstants.TECHNICAL_SUPPORT , BuildConfig.STICKY_FORWARD_URL)

        }

//        test.setOnClickListener{
//            var stickyInfoGrabber  =  StickyInfoGrabber(applicationContext)
//            stickyInfoGrabber.validateOpenAM()
////            stickyInfoGrabber.startTask()
//        }
        academicAdvisingButton.setOnClickListener {
            val stickyWork  = StickyInfoGrabber(this)
            DialogUtils.screenNamePrefix = "contact:acad-advising"
            stickyWork.generateMuleSoftStickySessionForTargetUrl(NetworkConstants.ACADEMIC_ADVISING , BuildConfig.STICKY_FORWARD_URL)
        }

        capellaCampusButton.setOnClickListener {
            val stickyWork  = StickyInfoGrabber(this)
            DialogUtils.screenNamePrefix = "contact:iguide"
            stickyWork.generateMuleSoftStickySessionForTargetUrl(NetworkConstants.CAPELLA_CAMPUS , BuildConfig.STICKY_FORWARD_URL)
        }
    }

}
