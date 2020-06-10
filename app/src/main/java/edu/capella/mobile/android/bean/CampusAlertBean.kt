package edu.capella.mobile.android.bean

/**
 * CampusAlertBean.kt : Bean / Pojo class to store response of JSON received from API.
 *
 * Created by Jayesh Lahare on 03/02/20.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 03/02/20.
 *
 */
data class CampusAlertBean(
    var eventMessage: ArrayList<EventMessage>? = null,
    var serverName: String? = null,
    var trackingId: String? = null
)
{
    override fun toString(): String {
        return "CampusAlertBean(eventMessage=$eventMessage, serverName=$serverName, trackingId=$trackingId)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CampusAlertBean

        if (eventMessage != other.eventMessage) return false
        if (serverName != other.serverName) return false
        if (trackingId != other.trackingId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = eventMessage?.hashCode() ?: 0
        result = 31 * result + (serverName?.hashCode() ?: 0)
        result = 31 * result + (trackingId?.hashCode() ?: 0)
        return result
    }


}