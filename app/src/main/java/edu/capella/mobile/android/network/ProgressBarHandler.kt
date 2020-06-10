package edu.capella.mobile.android.network

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import edu.capella.mobile.android.R

import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.base.BaseActivity
import kotlin.random.Random


/**
 * ProgressBarHandler.kt : Class responsible for handling and showing progressbar
 *
 * @author  :  jayesh.lahare
 * @version :  1.0
 * @since   :  3/21/2020
 *
 *
 */
object ProgressBarHandler
{
    var sypherChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
     var applicationContext: Context? = null
     var latestScreenId: String? = null
     var dialog: Dialog? = null


    private fun initializeProgressDialog(_activityContext: Context,postType : Boolean , submitMsg:String?)
    {
        try
        {
            if (dialog != null) {
                dialog!!.dismiss()
                dialog = null
            }

            applicationContext = _activityContext


            dialog = Dialog(_activityContext);
            // dialog = Dialog(wekRef?.get()!!);
            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog!!.setContentView(R.layout.progresss_bar_layout);
            if (postType) {
                dialog!!.findViewById<TextView>(R.id.submitMessageTxt).visibility = View.VISIBLE
                dialog!!.findViewById<TextView>(R.id.submitMessageTxt).text = submitMsg
                dialog!!.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                dialog!!.findViewById<ProgressBar>(R.id.progressBarWhite).visibility = View.VISIBLE
            } else {
                dialog!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }

            dialog!!.setCancelable(false)

            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


            dialog!!.setOnKeyListener { arg0, keyCode, event ->

                //if ( keyCode == KEYCODE_BACK)
                if ( keyCode == KEYCODE_BACK && event.action == KeyEvent.ACTION_UP && !event.isCanceled())
                {

                    dialog!!.dismiss()
                    killCurrentAcitivity()
                }
                true
            }

            /* try {
                            dialog!!.setOnDismissListener {
                                    applicationContext = null
                            }
                        }catch (t:Throwable){}*/

            //}
        }catch (t:Throwable)
        {
            Util.trace("ProgressBar Dialog Init error : $t" )
            t.printStackTrace()
        }
    }
    private fun killCurrentAcitivity()
    {
        try
        {
                if(applicationContext!=null)
                {
                    if( applicationContext is BaseActivity)
                    {
                        Util.trace("intrruptin....")
                        (applicationContext as BaseActivity).kill()

                    }else
                    {
                        Util.trace("intrruptin Cannot kill non-activity context")
                    }
                }
        }catch (t:Throwable)
        {
            Util.trace("Activity Kill issue $t")
            t.printStackTrace()
        }
    }

    fun hideProgressBar(screenId: String)
    {
        /*if(screenId == this.latestScreenId) {
            dialog?.dismiss()
            if(dialog != null) {
                dialog = null
            }
        }*/


            if(dialog != null) {
                dialog?.dismiss()

            }
        dialog = null

    }

    fun showProgressBar(context: Context, screenId: String, postType : Boolean, submitMsg: String?)
    {
        /*if(dialog != null)
        {
            if(dialog?.isShowing == true)
            {
                dialog?.dismiss()
                dialog == null
            }
        }*/


        initializeProgressDialog(context , postType,submitMsg)

        this.latestScreenId = screenId

        /*if(dialog?.isShowing == false)
        {
            dialog?.show()
        }*/
        if(dialog!=null)
            dialog?.show()
    }

    fun forceHide()
    {
        try {
            if (dialog != null) {
                dialog?.dismiss()
            }
            dialog = null
        }catch (t:Throwable){}
    }

    fun generateScreenId(): String
    {
        var screenId = ""+System.currentTimeMillis() + sypherChars[Random.nextInt(0, sypherChars.length)]
        return screenId
    }

}
