package edu.capella.mobile.android.activity

import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.ConnectivityReceiver
import edu.capella.mobile.android.utils.OmnitureTrack
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.task.StickyInfoGrabber
import edu.capella.mobile.android.utils.DialogUtils
import kotlinx.android.synthetic.main.activity_library.*
import kotlinx.android.synthetic.main.toolbar_drawer.*

/**
 * LibraryActivity.kt : Screen shows details about capella university such as Library staff
 * availability, toll free number, contact us option.
 *
 * This screen contains following feature<br>
 *  1.) Summon (opens mobile native browser).<br>
 *  2.) Database A-Z (opens mobile native browser).<br>
 *  3.) Toll free (open mobile native dialer).<br>
 *  4.) International(open mobile native dialer).<br>
 *
 * @author  :   Kush.pandya
 * @version :  1.0
 * @since   :  2/24/2020
 *
 *
 */
class LibraryActivity : MenuActivity(), View.OnClickListener , ConnectivityReceiver.ConnectivityReceiverListener {


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
        setContentChildView(R.layout.activity_library)
        toolbarTitle.text = getString(R.string.library)
        attachListeners()

        OmnitureTrack.trackState("library:home")
    }

    /**
     *  Method used to bind click and change listeners for different widgets used over.
     */
    private fun attachListeners() {
        summonTxt.contentDescription = getString(R.string.summon) + getString(R.string.link_will_open)
        databaseTxt.contentDescription = getString(R.string.database) + getString(R.string.link_will_open)
        summonTxt.setOnClickListener(this)
        databaseTxt.setOnClickListener(this)
        tollFreeTxt.setOnClickListener(this)
        internationalText.setOnClickListener(this)
    }

    /**
     * Method triggered by broadcast receiver which is listening for Network state.
     *
     * @param isConnected : Tells whether internet is available or not
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
       /* if (!isConnected) {
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
        ConnectivityReceiver.connectivityReceiverListener = this@LibraryActivity
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
     * Factory method provided by click listener, executes when binded widgets performs
     * click event.
     *
     * @param v : Reference of clicked button or widgets.
     * @see View
     */
    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.summonTxt -> {
                OmnitureTrack.trackAction("library:search:summon")
                DialogUtils.screenNamePrefix = "library:search:summon-linkout"
                val stickyWork = StickyInfoGrabber(this)
                stickyWork.generateMuleSoftStickySessionForTargetUrl(
                    NetworkConstants.SUMMON_URL,
                    BuildConfig.STICKY_FORWARD_URL
                )
            }

            R.id.databaseTxt -> {
                OmnitureTrack.trackAction("library:search:database")
                DialogUtils.screenNamePrefix = "library:search:database-linkout"
                val stickyWork = StickyInfoGrabber(this)
                stickyWork.generateMuleSoftStickySessionForTargetUrl(
                    NetworkConstants.DATABASE_URL,
                    BuildConfig.STICKY_FORWARD_URL
                )
            }

            R.id.tollFreeTxt -> {
                OmnitureTrack.trackAction("library:phone-linkout")
                Util.dialNumber(this, tollFreeTxt.text.toString())
            }
            R.id.internationalText -> {
                OmnitureTrack.trackAction("library:phone-linkout")
                Util.dialNumber(this, internationalText.text.toString())
            }

        }
    }
}
