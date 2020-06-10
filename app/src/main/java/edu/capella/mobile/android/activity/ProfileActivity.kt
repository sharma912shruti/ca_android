package edu.capella.mobile.android.activity

import android.content.IntentFilter
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Pair
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.activity.tabs.IdCardTab
import edu.capella.mobile.android.activity.tabs.ProfileTab
import edu.capella.mobile.android.bean.LearnerInformation
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.toolbar_drawer.*


/**
 * ProfileActivity.kt : Screen responsible for showing Profile and ID Card tabs detail over screen.
 *
 * Created by Jayesh Lahare on 03/02/20.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 03/02/20.
 *
 */
class ProfileActivity : MenuActivity() ,  ConnectivityReceiver.ConnectivityReceiverListener,
    NetworkListener {


    /**
     * ProfileTab container object.
     */
    var profileTab = ProfileTab()

    /**
     * IDCard container object.
     */
    var idCardTab = IdCardTab()

    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    private  var isInternetConnection: Boolean=false

    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null



    fun enableSwiping(isEnable : Boolean)
    {

        pagerForContainer.setSwipe(isEnable)
    }

    fun enablePullToRefresh(flag: Boolean)
    {
        profileSwipeToRefresh.setEnabled(flag)
    }

    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentChildView(R.layout.activity_profile)
        toolbarTitle.text = getString(R.string.profile)

        try {
            if (savedInstanceState != null) {
                profileTab = supportFragmentManager.getFragment(
                    savedInstanceState,
                    "profileFragment"
                ) as ProfileTab

                idCardTab = supportFragmentManager.getFragment(
                    savedInstanceState,
                    "idcardFragment"
                ) as IdCardTab
            }
        }catch (t: Throwable)
        {
            Util.trace("Restore fragment error $t")
        }


        initTabs()

        OmnitureTrack.trackState("profile")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        try {
            supportFragmentManager.putFragment(outState, "profileFragment", profileTab);
            supportFragmentManager.putFragment(outState, "idcardFragment", idCardTab);
        }catch (t: Throwable)
        {
            Util.trace("Saving fragment error $t")
        }

    }


    /**
     * Method initializes tabs with container data and Look and fill.
     *
     */
    private fun initTabs()
    {
        setupViewPager(pagerForContainer)
        profileHostTab.setupWithViewPager(pagerForContainer)

        val root: View = profileHostTab.getChildAt(0)
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
        val swipeRefreshColor =  ContextCompat.getColor(this@ProfileActivity,R.color.checkBoxColor)
        this.profileSwipeToRefresh.setColorSchemeColors(swipeRefreshColor)
        profileSwipeToRefresh.setOnRefreshListener {
            callProfileDetailApi()
            reloadLogo()
            profileSwipeToRefresh.isRefreshing = false;
        }
    }

    /**
     * Method sets pager with data responsible for switching Profile and IDCard tabs.
     *
     * @param viewPager : ViewPager handling tabs.
     */
    private fun setupViewPager(viewPager: ViewPager)
    {

        val adapter = ProfileViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(profileTab, getString(R.string.profile))
        adapter.addFragment(idCardTab, getString(R.string.id_card))
        viewPager.adapter = adapter
    }


    /**
     * Internal class for handling adapter of tabs container using fragments of Profile and IDCard
     * tab.
     *
     * @param manager
     */
    internal class ProfileViewPagerAdapter(manager: FragmentManager) :
        FragmentPagerAdapter(manager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
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

    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun init()
    {

        ConnectivityReceiver.connectivityReceiverListener = this@ProfileActivity
        connectivityReceiver = ConnectivityReceiver(networkLayout)
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(connectivityReceiver, intentFilter)
    }

    /**
     *  Factory method of activity, executes when application comes in resume state from pause state.
     *
     */
    override fun onResume() {
        super.onResume()
        init()
        callProfileDetailApi()
        reloadLogo()
    }

    /**
     * Factory method of activity, executes when activity goes in paused state.
     *
     */
    override fun onPause() {
        super.onPause()
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver)
            ConnectivityReceiver.connectivityReceiverListener=null
        }
    }

    /**
     * Method triggered by broadcast receiver which is listening for Network state.
     *
     * @param isConnected : Tells whether internet is available or not
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        isInternetConnection=isConnected

        if(isConnected == true && isNetworkFailedDueToConnectivity!=null && isNetworkFailedDueToConnectivity==true)
        {
            isNetworkFailedDueToConnectivity = null
            if(this.getCurrentNetworkQueueSize()==0) {
                DialogUtils.releaseGenericDialog()
                callProfileDetailApi()
                reloadLogo()
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

    /**
     * Method call LearnerProfile api to show details in PROFILE and IDCARD tabs.
     *
     */
    private fun callProfileDetailApi()
    {

        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val params = HashMap<String, Any>()
        params.put(NetworkConstants.TOKEN, loginBean?.authData?.token!!)

        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.LEARNER_PROFILE_API,
            params,
            NetworkHandler.METHOD_POST,
            this,
            null
        )

        networkHandler.execute()


    }

    /**
     * Its network listener methods invoked after network service complete its task.
     *
     *  @param response : response of network service
     *
     *  @see Pair
     *  @see NetworkHandler
     *
     */
    override fun onNetworkResponse(response: android.util.Pair<String, Any>)
    {
        try {


                if(response.first == NetworkConstants.SUCCESS)
                {
                    val gson = Gson()
                    val learnerBean = gson.fromJson<LearnerInformation>(response.second.toString(), LearnerInformation::class.java)
                   // Util.trace("learnerBean sting  :  " + learnerBean.toString())

                    if (learnerBean.errorData == null)
                    {
                                Preferences.addValue(PreferenceKeys.LEARNER_PROFILE , response.second.toString())
                                profileTab.updateProfileInfo()
                                idCardTab.updateProfileInfo()
                    } else
                    {
                        DialogUtils.showGenericErrorDialog(this)
                    }
                }

        }catch (t: Throwable){
            profileSwipeToRefresh.isRefreshing = false;
        }
    }

    fun getProfileFirstName():String
    {
        return profileTab.isProfileHaveFirstName()
    }
    fun getIDCardFirstName():String
    {
        return idCardTab.isIDCardHaveFirstName()
    }
    private fun reloadLogo()
    {
        super.loadProfileImage(logoImageListener)
    }
    private val logoImageListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>)
        {
            try {
                if (response.first == NetworkConstants.SUCCESS) {
                    Util.trace("New Logo Path is : " + response.second.toString())
                    Preferences.addValue(
                        PreferenceKeys.CACHED_LOGO_PATH,
                        response.second.toString()
                    )
                    cachedLogoImagePath = response.second.toString()
                    setProfileLogoImage(cachedLogoImagePath)
                    profileTab.updateLogo()
                    idCardTab.updateLogo()

                } else {
                    Util.trace("New Profile Logo error" + response.second.toString())
                }
            }catch (t:Throwable){}
        }
    }

}
