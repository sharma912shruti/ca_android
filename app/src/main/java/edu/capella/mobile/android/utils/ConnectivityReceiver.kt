package edu.capella.mobile.android.utils


import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.view.View

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import edu.capella.mobile.android.R
import edu.capella.mobile.android.base.BaseActivity


/**
 * ConnectivityReceiver.kt : Its broadcast receiver which is listening for internet connection
 * activity whether its TURNED OFF or ON, based on event it notifies to register the activity and
 * registered activity shows INTERNET status over screen.
 *
 * @author  :  KPANDYA1
 * @version :  1.0
 * @since   :  2/17/2020
 *
 */

class ConnectivityReceiver(): BroadcastReceiver()
{
    lateinit var networkLayout: View

    var firstCheck: Boolean? = null

    constructor( _networkLayout: View) :this()
    {
        this.networkLayout = _networkLayout
    }

    var height = 0

    /**
     * Factory method of Broadcast Receiver which is listening for internet connection status.
     *
     * @param context : Context of activity required for access system service which will identify
     * internet connection status.
     * @param intent  : not required for now but included as its factory method.
     */
    override fun onReceive(context: Context?, intent: Intent?) {

        if (connectivityReceiverListener != null)
        {
            CAnimator.expandOrCollapseView(this.networkLayout ,!isConnectedOrConnecting(context!!))
            // this.animateView(isConnectedOrConnecting(context!!) , context)
            connectivityReceiverListener!!.onNetworkConnectionChanged(isConnectedOrConnecting(context!!))

            if(!isConnectedOrConnecting(context!!)) {
                //  Util.trace("No Internet")
                Util.announceAda(context.getString(R.string.ada_no_internet_connection), context)
            }
        }

    }
    /*  private fun animateView(isAnimate : Boolean , context: Context)
      {
          try {
              if (!isAnimate)
              {
                  networkLayout.visibility = View.VISIBLE
                  var slide =
                      AnimationUtils.loadAnimation(context.applicationContext, R.anim.test_down);
                 // slide.fillAfter = false

                  networkLayout.startAnimation(slide)

                  Util.announceAda(context.getString(R.string.ada_no_internet_connection) , context)
              } else
              {

                  var slide =
                      AnimationUtils.loadAnimation(context.applicationContext, R.anim.test_up);
                  slide.setAnimationListener(object : Animation.AnimationListener {

                      override fun onAnimationRepeat(animation: Animation?) {
                      }

                      override fun onAnimationEnd(animation: Animation?) {

                          networkLayout.visibility = View.GONE
                          networkLayout.animation = null
                      }

                      override fun onAnimationStart(animation: Animation?) {
                      }

                  })

                 // netAvailable(context)
                 // slide.fillAfter = true
                  networkLayout.startAnimation(slide)
              }



          }catch (t: Throwable){
              Util.trace("Animation issue : $t")
              t.printStackTrace()
          }

      }*/



    /**
     * function checks whether internet is available or not
     *
     * @param context : context of activity required to check internet status.
     * @return boolean : true if internet is available, false if disconnected.
     */
    @SuppressLint("ServiceCast")
    private fun isConnectedOrConnecting(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }

    /**
     * Interface providing listener callback method for network change.
     *
     */
    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    /**
     * Static object of this class or singleton object
     */
    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }







}
