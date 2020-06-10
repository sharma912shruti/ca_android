package edu.capella.mobile.android.bean
import java.io.Serializable


/**
 * EventMessage.kt : Bean / Pojo class to store response of JSON received from API.
 *
 * Created by Jayesh Lahare on 03/02/20.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 03/02/20.
 *
 */

data class EventMessage(
    var messageCategory: String?=null,
    var messageId: String?=null,
    var messageSequenceNumber: String?=null,
    var messageStatus: String?=null,
    var messageText: String?=null,
    var messageType: String?=null,
    var suppressable: Boolean?=null
) : Serializable
{
    override fun toString(): String {
        return "EventMessage(messageCategory='$messageCategory', messageId='$messageId', messageSequenceNumber='$messageSequenceNumber', messageStatus='$messageStatus', messageText='$messageText', messageType='$messageType', suppressable=$suppressable)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventMessage

        if (messageCategory != other.messageCategory) return false
        if (messageId != other.messageId) return false
        if (messageSequenceNumber != other.messageSequenceNumber) return false
        if (messageStatus != other.messageStatus) return false
        if (messageText != other.messageText) return false
        if (messageType != other.messageType) return false
        if (suppressable != other.suppressable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = messageCategory.hashCode()
        result = 31 * result + messageId.hashCode()
        result = 31 * result + messageSequenceNumber.hashCode()
        result = 31 * result + messageStatus.hashCode()
        result = 31 * result + messageText.hashCode()
        result = 31 * result + messageType.hashCode()
        result = 31 * result + suppressable.hashCode()
        return result
    }


}
