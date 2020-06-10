package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.*
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class EventMessageTest {

   var eventMessage  =   EventMessage()

    @Before
    fun setFakeData()
    {
        eventMessage  =   EventMessage()
    }

    @Test
    fun check_for_getter_and_setter()
    {
        eventMessage.messageCategory = null
        eventMessage.messageId = null
        eventMessage.messageSequenceNumber = null
        eventMessage.messageStatus = null
        eventMessage.messageText = null
        eventMessage.messageType = null
        eventMessage.suppressable = null

        Assert.assertNull( eventMessage.messageCategory)
        Assert.assertNull( eventMessage.messageId)
        Assert.assertNull( eventMessage.messageSequenceNumber)
        Assert.assertNull( eventMessage.messageStatus)
        Assert.assertNull( eventMessage.messageText)
        Assert.assertNull( eventMessage.messageType)
        Assert.assertNull( eventMessage.suppressable)
    }

    @Test
    fun check_for_toString()
    {
        Assert.assertNotNull(eventMessage.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        Assert.assertNotNull(eventMessage.hashCode())
    }

    @Test
    fun check_whether_bean_is_gson_supported()
    {
        eventMessage.messageText = "Fake message text"
        var gson = Gson( )
        var json = gson.toJson(eventMessage)

        var newBean = gson.fromJson(json , EventMessage::class.java )

        assertEquals(eventMessage,newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        eventMessage.messageText = "Fake message text"
        var gson = Gson( )
        var json = gson.toJson(eventMessage)
        var newBean = gson.fromJson(json , EventMessage::class.java )
        newBean.messageText   = "New Fake message"
        assertNotEquals(eventMessage,newBean)
    }




}
