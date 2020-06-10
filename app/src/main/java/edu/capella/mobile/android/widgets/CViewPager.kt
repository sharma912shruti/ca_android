package edu.capella.mobile.android.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


/**
 * Class Name.kt : class description goes here
 *
 * @author  :  jayesh.lahare
 * @version :  1.0
 * @since   :  4/27/2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
class CViewPager (context: Context?, attrs: AttributeSet?) : ViewPager(context!!, attrs)
{
    var swipeEnabled = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (swipeEnabled) super.onTouchEvent(event) else false
        //return   false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean
    {
        return if (swipeEnabled) super.onInterceptTouchEvent(event) else false

       // return   false
    }


    fun setSwipe(isEnabled: Boolean)
    {
        swipeEnabled = isEnabled
    }

}
