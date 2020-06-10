package edu.capella.mobile.android.base


import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.bean.NetworkID
import edu.capella.mobile.android.network.NetworkHandler
import kotlinx.android.synthetic.main.toolbar_generic.*


/**
 * BaseActivity.kt :  BaseActivity , required to provide common configuration to all
 * activities i.e. Rotation support, Rotation is supported only when app running over tablets,
 * for phone orientation will be locked to portrait only.
 *
 *
 * @author  :  Kush.pandya
 * @version :  1.0
 * @since   :  03-02-2020
 *
 */
open class BaseActivity : AppCompatActivity()
{

    private var networkHandlers : ArrayList<NetworkID> = ArrayList()

    var isNetworkFailedDueToConnectivity:Boolean? = null

    /**
     * Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        if(resources.getBoolean(R.bool.portrait_only)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }


    }

    fun handleHeadersBackButton()
    {
        try{

            backButtonLayout.setOnClickListener {
                finish()
            }
        }catch (t: Throwable)
        {
            Util.trace("Back Header unavailable")

        }

    }



    override fun onResume() {
        super.onResume()
        handleHeadersBackButton()

    }


    open fun kill()
    {
      //  Util.trace("intrruptin inside kill " + networkHandlers.size)
        try
        {
            for((index,network) in networkHandlers.withIndex())
            {
                try
                {
                   // Util.trace("intrrupting  $index")
                    var n = network.network
                    n?.interruptCall()
                    n?.cancel(true)
                }catch (n:Throwable)
                {
                    Util.trace("intrrupting Network Kill Issue $n")
                    n.printStackTrace()
                }
            }
            finish()
        }catch (t:Throwable)
        {
            Util.trace("intrrupting Kill Issue $t")
            t.printStackTrace()
        }


    }


    @Synchronized fun registerNetworkProcess(id: String , networkHandler: NetworkHandler)
    {
       // Util.trace("intrrupting registering..........")
        var networkID = NetworkID(id , networkHandler)
        networkHandlers.add(networkID)
    }

    @Synchronized fun releaseNetworkProcess(id: String)
    {
      //  Util.trace("intrrupting releasing.........")

         synchronized(this)
         {
             /*for ((index, network) in networkHandlers.withIndex()) {
                 if (network.id == id) {
                     networkHandlers.removeAt(index)
                 }
             }*/
             var iterator = networkHandlers.iterator()
            Util.trace("Net Registration before size : " + networkHandlers.size)
            while (iterator.hasNext())
            {
                var network  = iterator.next()
                 if (network.id == id) {
                     iterator.remove()
                 }
             }
             Util.trace("Net Registration after size : " + networkHandlers.size)
         }


    }

    @Synchronized fun getCurrentNetworkQueueSize() :Int
    {
        //  Util.trace("intrrupting releasing.........")

        synchronized(this)
        {
            return networkHandlers.size
        }
    }

    @Synchronized fun forceClearNetworkQueue()
    {

        try
        {
            synchronized(this)
            {
                for ((index, network) in networkHandlers.withIndex()) {
                    try {
                        // Util.trace("intrrupting  $index")
                        var n = network.network
                        n?.interruptCall()
                        n?.cancel(true)
                    } catch (n: Throwable) {
                        Util.trace("cleaning Network Kill Issue $n")
                        n.printStackTrace()
                    }
                }
            }

        }catch (t:Throwable)
        {
            Util.trace("Cleaning Issue $t")
            t.printStackTrace()
        }


    }


}
