package edu.capella.mobile.android.activity

import android.os.Bundle
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.OmnitureTrack
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.base.BaseActivity
import kotlinx.android.synthetic.main.update_your_app_layout.*


class UpdateVersionActivity : BaseActivity() {

    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_your_app_layout)
        init()
        OmnitureTrack.trackAction("login:update-required-screen")

    }


    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_up)
    }

    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun init()
    {

        updateTxt.setOnClickListener {
            OmnitureTrack.trackAction("login:update-required-screen-get-update")
            Util.openPlayStore(this@UpdateVersionActivity)
            finish()
            OmnitureTrack.trackAction("login:update-required-screen")
        }
        backButton.setOnClickListener {
                Util.trace("apply animation" +"finish")
                finish()
            OmnitureTrack.trackAction("login:update-required-screen-close")

        }

    }

    override fun onBackPressed() {
            Util.trace("apply animation" +"finish")
            finish()
        OmnitureTrack.trackAction("login:update-required-screen-close")
    }





}
