package edu.capella.mobile.android.interfaces

import android.util.Pair

/**
 * NetworkListener.kt : A network interface used as a callback or listener for different network activities.
 *
 * Created by Jayesh Lahare on 03/02/20.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 03/02/20.
 */
interface NetworkListener {
    /**
     * A callback method, executes when Network Handler class completes its network operations
     * used to inform activity about server response, whether its error or json data receievd from
     * server.
     *
     * @see edu.capella.mobile.android.network.NetworkHandler
     *
     *
     * @param response the response, an object of Pair class.
     * @see Pair
     */
    fun onNetworkResponse(response: Pair<String, Any>)

}
