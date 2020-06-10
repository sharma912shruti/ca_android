package edu.capella.mobile.android.activity

import android.content.IntentFilter
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.ConnectivityReceiver
import edu.capella.mobile.android.utils.OmnitureTrack
import edu.capella.mobile.android.activity.tabs.CurrentCourseTab
import edu.capella.mobile.android.activity.tabs.PreviousCourseTab
import edu.capella.mobile.android.adapters.CourseListAdapter
import edu.capella.mobile.android.utils.DialogUtils
import edu.capella.mobile.android.utils.Util
import kotlinx.android.synthetic.main.activity_courses.*
import kotlinx.android.synthetic.main.toolbar_drawer.*


/**
 * CoursesActivity.kt :  Shows tabs for CURRENT and PREVIOUS courses taken by lerner
 *
 *  This screen contains following information <br>
 *  1.) List of current courses taken by lerner <br>
 *  2.) List of previous courses taken by lerner <br>
 *
 * @author  :  Jayesh Lahare
 * @version :  1.0
 * @since   :  24-02-2020
 *
 */
class CoursesActivity : MenuActivity(),  ConnectivityReceiver.ConnectivityReceiverListener {


    var currentCourseTab = CurrentCourseTab()
    var previousCourseTab = PreviousCourseTab()

    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    private  var isInternetConnection: Boolean=false

    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null

    fun getCurrentCourseAdapter() : CourseListAdapter?
    {
            return currentCourseTab.getCurrentCourseAdapter()
    }

    fun getPreviousCourseAdapter() : CourseListAdapter?
    {
        return previousCourseTab.getPreviousCourseAdapter()
    }

    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentChildView(R.layout.activity_courses)
        toolbarTitle.text = getString(R.string.menu_courses)
        initTabs()
        OmnitureTrack.trackState("courses:screen")
    }


    /**
     * Method initialize tabs with details and sets their look and fill.
     *
     */
    private fun initTabs()
    {
        setupViewPager(coursePagerContainer)
        coursesHostTab.setupWithViewPager(coursePagerContainer)

        val root: View = coursesHostTab.getChildAt(0)
        if (root is LinearLayout)
        {

            root.setPadding(0,0,0,resources.getDimension(R.dimen._1dp).toInt())

            root.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE

            val drawable = GradientDrawable()
            drawable.setColor(resources.getColor(R.color.whiteColor, resources.newTheme()))
            drawable.setSize(resources.getDimension(R.dimen._1dp).toInt(), resources.getDimension(R.dimen._1dp).toInt())
            root.dividerPadding =  /*R.dimen._1dp*/0
            root.dividerDrawable = drawable
        }
    }

    /**
     * Method sets contents for Current and Previous course tabs.
     *
     * @param viewPager : Pager object responsible for showing tab details according to user
     * selection.
     *
     * @see ViewPager
     */
    private fun setupViewPager(viewPager: ViewPager)
    {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(currentCourseTab, getString(R.string.current))
        adapter.addFragment(previousCourseTab, getString(R.string.previous))
        viewPager.adapter = adapter
    }


    internal class ViewPagerAdapter(manager: FragmentManager) :
        FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    private fun init()
    {

        ConnectivityReceiver.connectivityReceiverListener = this@CoursesActivity
        connectivityReceiver = ConnectivityReceiver(networkLayout)
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(connectivityReceiver, intentFilter)
    }


    override fun onResume() {
        super.onResume()
        try{
            currentCourseTab.activityContext = this
            previousCourseTab.activityContext = this
        }catch (t:Throwable){}
        init()
    }
    override fun onPause() {
        super.onPause()
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver)
            ConnectivityReceiver.connectivityReceiverListener=null
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        isInternetConnection=isConnected

       if(isConnected == true && isNetworkFailedDueToConnectivity!=null && isNetworkFailedDueToConnectivity==true)
        {
            isNetworkFailedDueToConnectivity = null


            if(this.getCurrentNetworkQueueSize()==0) {
                DialogUtils.releaseGenericDialog()
                //NETWORK AVAILABLE
                //Relead API
                currentCourseTab.callCurrentCourseApi()
                previousCourseTab.callPreviousCourseApi()
            }else
            {
                Util.trace("Can not reload")
            }

        }else
        {
            //NETWORK GONE
            isNetworkFailedDueToConnectivity = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun getCurrentCourseListSize(): Int {
       return currentCourseTab.getCurrentCourseSize()
    }

    fun getFpCourseIndex():Int{
        return currentCourseTab.getFpCourseIndex()
    }

    fun getGpCourseIndex():Int{
        return currentCourseTab.getGpCOurseIndex()
    }
    fun getPreviousCourseListSize(): Int {
        return previousCourseTab.getPreviousCourseSize()
    }

}
