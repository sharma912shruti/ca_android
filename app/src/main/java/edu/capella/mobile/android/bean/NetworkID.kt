package edu.capella.mobile.android.bean

import edu.capella.mobile.android.network.NetworkHandler

/**
 * OpenAMAuth.kt : Bean / Pojo class to store response of JSON received from API.
 *
 * Created by Jayesh Lahare on 03/02/20.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 03/02/20.
 *
 */

data class NetworkID(
    var id: String? = null,
    var network: NetworkHandler? = null
)
{



}