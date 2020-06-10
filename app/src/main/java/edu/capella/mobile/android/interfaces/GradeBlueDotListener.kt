package edu.capella.mobile.android.interfaces

/**
 * Class Name.kt : class description goes here
 *
 * @author  :  jayesh.lahare
 * @version :  1.0
 * @since   :  4/24/2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
interface GradeBlueDotListener
{
    fun onGradeBlueDot(isBlueDot: Boolean)
    fun onGradeBlueDotFail(msg : String)
}
