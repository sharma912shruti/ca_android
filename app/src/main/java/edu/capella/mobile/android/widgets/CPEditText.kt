package edu.capella.mobile.android.widgets

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.EditText


/**
 * CPEditText.kt : Custom EditText class with specific font with normal and bold style.
 *
 * Created by Jayesh Lahare on 30/1/2020.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 30/1/2020.
 * @see android.widget.Button
 *
 */

class CPEditText : EditText {

    /**
     * Instantiates a new Biz edit text.
     *
     * @param context the context
     */
    constructor(context: Context) : super(context) {
        applyFont(context, -1)
    }


    /**
     * Instantiates a new Biz edit text.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        appySettings(context, attrs)
    }

    /**
     * Instantiates a new Biz edit text.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        appySettings(context, attrs)

    }

    /**
     * Internal method to read attributes passed from XML.
     */
    private fun appySettings(context: Context, attrs: AttributeSet) {

        val set = intArrayOf(
            android.R.attr.textStyle //, android.R.attr.text
        )

        val a = context.obtainStyledAttributes(attrs, set)
        val style = a.getInt(0, -1)
        applyFont(context, style)
    }

    /**
     * Internal method to appy font setting over EditText.
     */
    private fun applyFont(context: Context, style: Int) {
        try {
            var typeFace: Typeface? = null

            if (style == Typeface.BOLD) {
                typeFace = Typeface.createFromAsset(context.assets, "fonts/roboto_bold.ttf")
            }
            else if(style == Typeface.NORMAL) {
                typeFace = Typeface.createFromAsset(context.assets, "fonts/roboto_medium.ttf")
            }
            else
            {
                typeFace = Typeface.createFromAsset(context.assets, "fonts/roboto_regular.ttf")
            }
            this.typeface = typeFace
        } catch (t: Throwable) {
        }

    }

}
