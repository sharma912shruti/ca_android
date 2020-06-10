package edu.capella.mobile.android.activity

import android.content.Intent
import android.os.Bundle
import android.telecom.Call
import android.view.View

import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.Constants
import edu.capella.mobile.android.utils.OmnitureTrack
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.base.BaseActivity

import kotlinx.android.synthetic.main.activity_no_access_to_mobile.*



/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Kush Pandya
 * @version :  1.0
 * @since   :  08-04-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */

class NoAccessMobileActivity : BaseActivity(), View.OnClickListener {

    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_access_to_mobile)
        attachListener()
        OmnitureTrack.trackAction("login:no-access")

    }




    /**
     * Method used to set click and change listeners for different widgets used over NoAccessMobileActivity screen
     */
    private fun attachListener() {
        backButtonLL.contentDescription=resources.getString(R.string.ada_back_button)+getString(R.string.back)
        propectiveInfoTxt.setOnClickListener(this)
        applyAdmissionTxt.setOnClickListener(this)
        trialCourseTxt.setOnClickListener(this)
        backButtonLL.setOnClickListener(this)

    }



    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.propectiveInfoTxt -> {
                Util.openUrlBrowser(this, Constants.PRPOSPECTIVE_STUDENT)
                OmnitureTrack.trackAction("login:no-access:prospective-linkout")


            }

            R.id.applyAdmissionTxt -> {
                Util.openUrlBrowser(this, Constants.APPLY_ADMISSION)
                OmnitureTrack.trackAction("login:no-access:application-linkout")

            }

            R.id.trialCourseTxt -> {
                Util.openUrlBrowser(this, Constants.TRIAL_COURSE)
                OmnitureTrack.trackAction("login:no-access:trial-course-linkout")

            }

            R.id.backButtonLL -> {
                onBackPressed()
            }


        }

    }

    override fun onBackPressed() {
        finish()
        this@NoAccessMobileActivity.overridePendingTransition(R.anim.slide_right_to_left,R.anim.no_anim);
    }

//    override fun finish() {
//        super.finish()
//        overridePendingTransition(R.anim.no_anim, R.anim.slide_right_to_left)
//    }





}
