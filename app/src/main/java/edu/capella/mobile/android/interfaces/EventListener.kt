package edu.capella.mobile.android.interfaces

/**
 * EvenListener.kt : Interface Description :</b> This interface is used to provide callback for dialog used over
 * login screen, it trigger's different callback over confirm and cancel button.
 *
 * @author  :  Kush.pandya
 * @version :  1.0
 * @since   :  10-02-2020
 *
 */
interface EventListener {

    /**
     * called when cancel button is tabbed
     */
    fun cancel()
    {

    }

    /**
     * called when confirm button is tabbed
     */
    fun confirm()
    {

    }

    /**
     * called when update button is tabbed
     */
    fun update()
    {

    }

    /**
     * called when cancle update button is tabbed
     */
    fun cancleUpdate()
    {

    }
}