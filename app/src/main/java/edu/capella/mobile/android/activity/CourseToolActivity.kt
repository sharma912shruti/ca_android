package edu.capella.mobile.android.activity

import android.content.Intent
import android.os.Bundle
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.Constants
import edu.capella.mobile.android.adapters.CourseToolAdapter
import edu.capella.mobile.android.bean.GP2Bean
import edu.capella.mobile.android.layout_manager.NonScrollableLayoutManager
import kotlinx.android.synthetic.main.activity_course_tool.*
import kotlinx.android.synthetic.main.page_warning_layout.view.*
import kotlinx.android.synthetic.main.toolbar_generic.*

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  24-04-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */

class CourseToolActivity : MenuActivity()/*BaseActivity()*/
{

    var courseToolAdapter:CourseToolAdapter? = null
    var list : ArrayList<GP2Bean.LeftNavigationItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentChildView(R.layout.activity_course_tool, true)
        initialiseValue()

    }

    fun getCourseToolListSize(): Int {
        try {
            return list?.size!!
        } catch (t: Throwable) {
            return 0
        }
    }

    private fun initialiseValue(){
        genericTitleTxt.text = getString(R.string.course_tool)
        backButtonLayout.contentDescription = getString(R.string.ada_back_button) + ", " + getString(R.string.back)
        list  = intent.extras?.getSerializable(Constants.COURSE_TOOL_LIST)  as ArrayList<GP2Bean.LeftNavigationItem>
        waringLayout.alertTxt.text  = resources.getString(R.string.some_item_not_work_on_mobile)
        initialiseAdapter()
    }

    private fun initialiseAdapter(){
//        list = ArrayList()
        courseToolAdapter = CourseToolAdapter(this,list!!,object: CourseToolAdapter.CourseToolEventListener{
            override fun onCourseToolClick(leftNavigationItem: GP2Bean.LeftNavigationItem) {
                openUrlInAppBrowser(leftNavigationItem)
            }

        })
        course_tool_list.layoutManager = NonScrollableLayoutManager(this)
        course_tool_list.adapter = courseToolAdapter
    }


    private fun openUrlInAppBrowser(item: GP2Bean.LeftNavigationItem) {
        // todo open in appBrowser
        val finalLink = intent.extras?.getString(Constants.HEADER) + item.url
        val intent = Intent(this@CourseToolActivity, GP2InAppBrowser::class.java)
        intent.putExtra(Constants.URL_FOR_IN_APP, finalLink)
        intent.putExtra(Constants.IN_APP_TITLE,item.name)
        startActivity(intent)
        overridePendingTransition( R.anim.slide_in_up, R.anim.no_anim)
    }

}
