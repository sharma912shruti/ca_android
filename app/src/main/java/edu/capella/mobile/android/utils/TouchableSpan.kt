package edu.capella.mobile.android.utils

import android.text.TextPaint

import android.text.style.ClickableSpan




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
//class TouchableSpan {
//}

abstract class TouchableSpan(
    private val mNormalTextColor: Int,
    private val mPressedTextColor: Int,
    private val mPressedBackgroundColor: Int
) :
    ClickableSpan() {
    private var mIsPressed = false
    fun setPressed(isSelected: Boolean) {
        mIsPressed = isSelected
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.color = if (mIsPressed) mPressedTextColor else mNormalTextColor
        ds.bgColor = if (mIsPressed) mPressedBackgroundColor else -0x111112
        ds.isUnderlineText = false
    }

}