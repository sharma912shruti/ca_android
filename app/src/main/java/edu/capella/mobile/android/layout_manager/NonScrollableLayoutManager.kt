package edu.capella.mobile.android.layout_manager

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * Class Name.kt : class description goes here
 *
 * @author  :  SSHARMA45
 * @version :  1.0
 * @since   :  4/28/2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
class NonScrollableLayoutManager(context:Context):LinearLayoutManager(context) {

    private var isScrollEnabled = true

    fun setScrollEnabled(flag: Boolean) {
        isScrollEnabled = flag
    }

    override fun canScrollVertically(): Boolean { //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically()
    }

}

