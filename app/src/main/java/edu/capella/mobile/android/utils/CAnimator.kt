package edu.capella.mobile.android.utils

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation


/**
 * Class Name.kt : class description goes here
 *
 * @author  :  jayesh.lahare
 * @version :  1.0
 * @since   :  5/25/2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
object CAnimator
{

    fun expandOrCollapseView(v: View, expand: Boolean)
    {
        // v.animation = null
        Util.trace("Interpolate expand = $expand")
        if(expand)
        {
            expand(v)
        }else
        {
            collapse(v )
        }
    }

    fun expand(v: View) {
        //val matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY )
        val matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.UNSPECIFIED )
        val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0,  View.MeasureSpec.UNSPECIFIED )
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            protected override fun applyTransformation(  interpolatedTime: Float,  t: Transformation?  ) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }

        }



        // Expansion speed of 1dp/ms
        /* a.setDuration(
             (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
         )*/

        a.setDuration((( (targetHeight / v.getContext().getResources().getDisplayMetrics().density)) * 8).toLong())
        // a.duration = 500
        v.startAnimation(a)
    }

    fun collapse(v: View) {
        v.invalidate()
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation()
        {
            protected override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation?
            ) {

                if (interpolatedTime >= 1f) {
                    v.visibility = View.GONE
                    v.animation = null
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        a.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                v.visibility = View.GONE
                v.animation = null

            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
        // Collapse speed of 1dp/ms
        /* a.setDuration(
             (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
         )*/

        var duration = (( (initialHeight / v.getContext().getResources().getDisplayMetrics().density)) * 8).toLong()

        // duration = 500
        Util.trace("Interpolate duration $duration")

        a.setDuration(duration)
        v.startAnimation(a)
    }
}