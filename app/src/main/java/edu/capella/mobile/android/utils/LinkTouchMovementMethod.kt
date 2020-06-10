package edu.capella.mobile.android.utils

import android.text.Selection
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.view.MotionEvent
import android.widget.TextView


/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  12-05-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
//class LinkTouchMovementMethod {
//}


class LinkTouchMovementMethod : LinkMovementMethod() {
    private var mPressedSpan: TouchableSpan? = null
    override fun onTouchEvent(
        textView: TextView,
        spannable: Spannable,
        event: MotionEvent
    ): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            mPressedSpan = getPressedSpan(textView, spannable, event)
            if (mPressedSpan != null) {
                mPressedSpan!!.setPressed(true)
                Selection.setSelection(
                    spannable, spannable.getSpanStart(mPressedSpan),
                    spannable.getSpanEnd(mPressedSpan)
                )
            }
        } else if (event.action == MotionEvent.ACTION_MOVE) {
            val touchedSpan: TouchableSpan? = getPressedSpan(textView, spannable, event)
            if (mPressedSpan != null && touchedSpan !== mPressedSpan) {
                mPressedSpan!!.setPressed(false)
                mPressedSpan = null
                Selection.removeSelection(spannable)
            }
        } else {
            if (mPressedSpan != null) {
                mPressedSpan!!.setPressed(false)
                super.onTouchEvent(textView, spannable, event)
            }
            mPressedSpan = null
            Selection.removeSelection(spannable)
        }
        return true
    }

    private fun getPressedSpan(
        textView: TextView,
        spannable: Spannable,
        event: MotionEvent
    ): TouchableSpan? {
        val x = event.x.toInt() - textView.totalPaddingLeft + textView.scrollX
        val y = event.y.toInt() - textView.totalPaddingTop + textView.scrollY
        val layout = textView.layout
        val position = layout.getOffsetForHorizontal(layout.getLineForVertical(y), x.toFloat())
        val link: Array<TouchableSpan> = spannable.getSpans<TouchableSpan>(
            position, position,
            TouchableSpan::class.java
        )
        var touchedSpan: TouchableSpan? = null
        if (link.size > 0 && positionWithinTag(position, spannable, link[0])) {
            touchedSpan = link[0]
        }
        return touchedSpan
    }

    private fun positionWithinTag(
        position: Int,
        spannable: Spannable,
        tag: Any
    ): Boolean {
        return position >= spannable.getSpanStart(tag) && position <= spannable.getSpanEnd(tag)
    }
}