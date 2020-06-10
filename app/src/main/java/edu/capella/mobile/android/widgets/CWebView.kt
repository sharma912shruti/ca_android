package edu.capella.mobile.android.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView
import edu.capella.mobile.android.utils.Util

/**
 * Class Name.kt : class description goes here
 *
 * @author  :  jayesh.lahare
 * @version :  1.0
 * @since   :  4/28/2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
class CWebView (context: Context?, attrs: AttributeSet?) : WebView(context!!, attrs)
{

   private var cWebTouch: CWebTouch? = null

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        Util.trace("Dispatch traced")

           if(ev!!.action == MotionEvent.ACTION_DOWN)
           {
               if(cWebTouch!=null)
               {
                   cWebTouch!!.isTouch(true)
               }
           }

        return super.dispatchTouchEvent(ev)
    }

    fun setTouchWatcher(_cWebTouch : CWebTouch)
    {
        cWebTouch = _cWebTouch
    }




    interface CWebTouch
    {
        fun isTouch(touch: Boolean)

    }


};