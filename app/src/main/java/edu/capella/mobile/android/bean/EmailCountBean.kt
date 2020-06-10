package edu.capella.mobile.android.bean

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  19-03-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
data class EmailCountBean(
    var loginUrl: String? = null,
    var serverName: String? = null,
    var trackingId: String? = null,
    var unreadCount: Int? = null
)