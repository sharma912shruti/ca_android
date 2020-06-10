package edu.capella.mobile.android.activity


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Pair
import android.view.*
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.adapters.MenuAdapter
import edu.capella.mobile.android.base.BaseActivity
import edu.capella.mobile.android.bean.EmailCountBean
import edu.capella.mobile.android.bean.LearnerInformation
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.HubbleNetworkConstants
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import edu.capella.mobile.android.task.StickyInfoGrabber
import edu.capella.mobile.android.widgets.CPTextView
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.drawer_header_layout.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * MenuActivity.kt :  Parent activity of all the screens which going to show toggle menu on left
 * side. every activity extending this class shoud call setContentChild(layout) method to set its
 * layout.
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  24-02-2020
 *
 */
open class MenuActivity : BaseActivity() {

    /**
     * DrawerAdapter responsible to showing list of menus on left side.
     */
     var drawerAdapter: MenuAdapter? = null

    var isLearner = false
    /**
     * As left menu will be common for all activities, needs to maintain path of Profile image
     * with this variable.
     */
    protected var cachedLogoImagePath: String? = null


    /**
     * Arraylist object which store drawer item names.
     */
    private var drawerItem = ArrayList<String>()

    /**
     * Stores list of icon used for left menu.
     */
    private var drawerIcons = ArrayList<Int>()

    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

        //setDrawerItem(false)
        setDrawerItem(isLearner)
        setAdapter()

        initialiseToolBar()

        initialiseListener()
        loadCachedProfileInfo()


    }
  /*  private fun ada()
    {
        try{
            var v = findViewById<ImageView>(R.id.ic_chevron_right)
            if(v!=null)
            {
                v.setOnClickListener{
                    //Noting to Do
                    Util.announceAda("click to open submenu" , this)
                }
            }

        }catch (t:Throwable){}
    }*/

    /**
     * Method used to restore cached information about logged in user over screen.
     *
     */
    private fun loadCachedProfileInfo() {
        if (Preferences.getValue(PreferenceKeys.CACHED_LOGO_PATH) != "") {
            setProfileLogoImage(Preferences.getValue(PreferenceKeys.CACHED_LOGO_PATH))
        }

        updateMenuUserTitle()

        profileBaseLayout.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    /**
     * Method sets basic information about user over menu i.e. First name, ID number etc.
     *
     */
    fun updateMenuUserTitle() {

        try {
            if (Preferences.getValue(PreferenceKeys.LEARNER_PROFILE) != "") {
                val gson = Gson()

                val learnerBean = gson.fromJson<LearnerInformation>(
                    Preferences.getValue(PreferenceKeys.LEARNER_PROFILE),
                    LearnerInformation::class.java
                )
                if (learnerBean.errorData == null) {



                    var demographicName = learnerBean?.profileLearnerData?.demographicInfo?.demographicInfo?.name

                    /*userNameTxt.text = learnerBean?.profileLearnerData?.profile?.firstName
                    userIdTxt.text =  getString(R.string.id) + " " + learnerBean?.profileLearnerData?.profile?.employeeId*/

                    userNameTxt.text = demographicName!![0]!!.firstName
                    userIdTxt.text =  getString(R.string.id) + " " + learnerBean?.authData?.employeeId?.value
//                    userNameTxt.contentDescription = demographicName!![0]!!.firstName  + getString(R.string.id) + " " + learnerBean?.authData?.employeeId?.value

                    var emails =
                        learnerBean?.profileLearnerData?.demographicInfo?.demographicInfo?.emailAddress

                    /*var isLearner = false*/

                    for (index in 0 until emails!!.size) {
                        if (emails[index]?.type == getString(R.string.learner)) {
                            isLearner = true
                            break
                        }
                    }
                    Util.trace("is Learner "+ isLearner)
                   // setDrawerItem(isLearner)

                    if(isLearner) {
                        callCourseEmailCountApi()
                    } else {
                        setDrawerItem(isLearner)
                        setAdapter()
                    }
//                    drawerAdapter?.notifyDataSetChanged()
                }
            }
            else
            {
                Util.trace("error can not update menu as profile information is not available")
            }
        } catch (t: Throwable) {
            Util.trace("Menu Update error : " + t.toString())

        }
    }

    /**
     * Method sets profile image over top-left in sliding menu, method is protected and can be
     * called by child classes.
     *
     * @param filePath
     */
    protected fun setProfileLogoImage(filePath: String?) {
        try {
            val bitmap = BitmapFactory.decodeFile(filePath)
            userLogoImg.setImageBitmap(bitmap)
        } catch (t: Throwable) {
            Util.trace("Logo error $t")
        }

    }

    /**
     * Method shows layout of child activity
     *
     * @param id : ID of layout which wants to show over screen example : R.layout.profileActivity
     */
    protected fun setContentChildView(id: Int) {

        val inflater = LayoutInflater.from(this)
        val contentView: View = inflater.inflate(id, null)

        mainContainer.addView(contentView, 0)
    }

    protected fun setContentChildView(id: Int , hideToolbar:Boolean) {

        val inflater = LayoutInflater.from(this)
        val contentView: View = inflater.inflate(id, null)

        mainContainer.addView(contentView, 0)

        if(hideToolbar)
            menuToolbarLayout.visibility = View.GONE
    }


    /**
     *  Method is set the drawer items in defined param@drawerItem
     */
    private fun setDrawerItem(gmailCount: Boolean) {
        //FOR STRING
        drawerItem.clear()
        drawerItem.add(resources.getString(R.string.menu_courses))
        drawerItem.add(resources.getString(R.string.menu_finances))
        drawerItem.add(resources.getString(R.string.menu_academic_plan))
        drawerItem.add(resources.getString(R.string.menu_library))
        drawerItem.add(resources.getString(R.string.menu_contact_us))
        drawerItem.add(resources.getString(R.string.menu_campus_news))

        if (gmailCount) {
            drawerItem.add(resources.getString(R.string.capella_email))
        }
        drawerItem.add(resources.getString(R.string.menu_view_campus_on_full_site))
        drawerItem.add(resources.getString(R.string.settings))
        drawerItem.add(resources.getString(R.string.menu_logout))


        // FOR ICONS
        drawerIcons.clear()
        drawerIcons.add(R.drawable.ic_bookmark)
        drawerIcons.add(R.drawable.ic_finances)
        drawerIcons.add(R.drawable.ic_graduate)
        drawerIcons.add(R.drawable.ic_books)
        drawerIcons.add(R.drawable.ic_contact_us)
        drawerIcons.add(R.drawable.ic_news)
        if (gmailCount) {
            drawerIcons.add(R.drawable.ic_menu_email)
        }
        drawerIcons.add(R.drawable.ic_logo)
        drawerIcons.add(R.drawable.ic_settings)
        drawerIcons.add(R.drawable.ic_logout)

    }

    /**
     * Method which initialise DrawerAdapter and handle drawer item clicks.
     *
     */
    private fun setAdapter() {
        if(drawerAdapter==null) {
            drawerAdapter = MenuAdapter(this, drawerItem, drawerIcons)
        }
        drawerRecylerView!!.adapter = drawerAdapter
        drawerRecylerView!!.layoutManager = LinearLayoutManager(this)

        drawerAdapter!!.setOnIemClickListener(object : MenuAdapter.OnItemClickListener {
            override fun onItemClicked(position: Int) {
                drawerLayout.closeDrawer(GravityCompat.START)
                clickDrawerList(position)
            }
        })
    }

    /**
     * Method initializes toolbar and sets its text.
     *
     */
    private fun initialiseToolBar() {
        val toolbarTitle = toolbarDrawer.findViewById<CPTextView>(R.id.toolbarTitle)
        toolbarTitle.text = resources.getString(R.string.menu_campus_news)
    }

    /**
     * Factory method required for certain permissions.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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

    override fun onBackPressed()
    {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        super.onBackPressed()
    }

    /**
     * Methods opens left menu once user taps over menu icon.
     *
     */
    private fun onDrawerIconClick() {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    /**
     * Method handles click of menu icons.
     *
     */
    private fun initialiseListener() {
        val dehazeView = toolbarDrawer.findViewById<ImageView>(R.id.drawerImageView)
        dehazeView.setOnClickListener {
            onDrawerIconClick()
        }
    }

    /**
     * Method performs click operation for selected menu item.
     *
     * @param position : tells method which menu item position clicked.
     */
    fun clickDrawerList(position: Int) {
        try {

            when (position) {
                R.drawable.ic_bookmark -> {
                    OmnitureTrack.trackAction("course:linkout:confirm")
                    openCoursesActivity()
                    //  Util.showToastForUnderDevelopment(this)
                }

                R.drawable.ic_finances -> {

//                    openCoursesActivity1()

//
                    OmnitureTrack.trackAction("finances:campus-finances")
                    val intent = Intent(this@MenuActivity, CommonWebViewActivity::class.java)
                    intent.putExtra(Constants.URL_FOR_IN_APP, NetworkConstants.FINANCE)
                    intent.putExtra(Constants.IN_APP_TITLE,"Finances - Capella University")
                    startActivity(intent)
                    //overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up )
                    overridePendingTransition( R.anim.slide_in_up, R.anim.no_anim)
                   // Util.showToastForUnderDevelopment(this)
                }

                R.drawable.ic_graduate -> {

                    val intent = Intent(this@MenuActivity, CommonWebViewActivity::class.java)
                    OmnitureTrack.trackAction("academic-plan:plan")
                    intent.putExtra(Constants.URL_FOR_IN_APP, NetworkConstants.ACADEMIC_PLAN)
                    intent.putExtra(Constants.IN_APP_TITLE,getString(R.string.academic_plan_dashboard))
                    startActivity(intent)
                    //overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up )
                    overridePendingTransition( R.anim.slide_in_up, R.anim.no_anim )
//                    Util.showToastForUnderDevelopment(this)
                }

                R.drawable.ic_books -> {
                    openLibraryScreen()
                }

                R.drawable.ic_contact_us -> {
                    openContactUsScreen()
                }

                R.drawable.ic_news -> {
                    openCampusActivity()
                }

                R.drawable.ic_menu_email -> {
                    val stickyWork  = StickyInfoGrabber(this)
                    DialogUtils.screenNamePrefix = "gmail:link-out"
                    stickyWork.generateMuleSoftStickySessionForTargetUrl(BuildConfig.EMAIL_URL , BuildConfig.STICKY_FORWARD_URL)

//                    Util.showToastForUnderDevelopment(this)
                }

                R.drawable.ic_logo -> {
                    val stickyWork  = StickyInfoGrabber(this)
                    DialogUtils.screenNamePrefix = "visit:campus:linkout"
                    stickyWork.generateMuleSoftStickySessionForTargetUrl("https://campus.capella.edu/home" , BuildConfig.STICKY_FORWARD_URL)
                   // Util.showToastForUnderDevelopment(this)
                }

                R.drawable.ic_settings -> {
                    openSettingScreen()
                }

                R.drawable.ic_logout -> {
                    showLogoutConfirmation(this)
                }

                /*else -> {

                }*/
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Open Courses Screen.
     *
     */
    private fun openCoursesActivity() {
        OmnitureTrack.trackAction("course:linkout:confirm")
        startActivity(Intent(this, CoursesActivity::class.java))
        //finish()
    }

    /**
     * Opens setting screen.
     *
     */
    private fun openSettingScreen() {
        startActivity(Intent(this, SettingActivity::class.java))
        // finish()
    }

    /**
     * Opens campus news screen.
     *
     */
    private fun openCampusActivity() {
        OmnitureTrack.trackAction("campus:linkout:confirm")
        startActivity(Intent(this, CampusActivity::class.java))
        //finish()
    }

    /**
     * Opens contact us screen.
     *
     */
    private fun openContactUsScreen() {
        startActivity(Intent(this, ContactUsActivity::class.java))
        // finish()
    }


    /**
     * Opens Library screen.
     *
     */
    private fun openLibraryScreen() {
        startActivity(Intent(this, LibraryActivity::class.java))
        // finish()
    }

    /**
     *  Factory method of activity, executes when application comes in resume state from pause state.
     *
     */
    override fun onResume() {
        super.onResume()
        if (drawerAdapter != null) {
            drawerAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * Method used to show login screen once Logout is done.
     *
     */
    private fun showLogin() {
        try {

            Preferences.removeKey(PreferenceKeys.KEEP_ME_SIGNED_IN.toString())
            Preferences.removeKey(PreferenceKeys.SECRET_USER.toString())
            Preferences.removeKey(PreferenceKeys.SECRET.toString())

            //          clear profile data
            Preferences.removeKey(PreferenceKeys.LEARNER_PROFILE.toString())
            Preferences.removeKey(PreferenceKeys.CACHED_LOGO_PATH.toString())

        } catch (e: java.lang.Exception) {
        }
        var intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    /**
     * Methods call Logout API and shows login screen.
     *
     */
    private fun performLogout() {

        OmnitureTrack.trackAction("menu:logout-clicked")

        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        val loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val params = HashMap<String, Any>()
        params.put(NetworkConstants.USER_NAME, loginBean?.authData!!.username!!)
        params.put(NetworkConstants.TOKEN, loginBean?.authData!!.token!!)

        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.LOGOUT_API,
            params,
            NetworkHandler.METHOD_POST,
            logoutListener,
            null
        )

        networkHandler.execute()


    }

    /**
     * Its network listener methods invoked after logout network service completes its task.
     *
     *  @param response : response of network service
     *
     *  @see Pair
     *  @see NetworkHandler
     *
     */
    private val logoutListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {
            Util.trace("Logout first :  " + response.first)
            Util.trace("Logout second :  " + response.second)

            if (response.first == NetworkConstants.SUCCESS) {

                OmnitureTrack.trackAction("menu:logout-successful")

                showLogin()
            } else {
                OmnitureTrack.trackAction("menu:logout-error")
                DialogUtils.showGenericErrorDialog(this@MenuActivity)
            }
        }
    }

    /**
     * Method shows Logout confirmation dialog.
     *
     * @param context : Activity context.
     */
    fun showLogoutConfirmation(context: Context) {
        try {
            val dialog = Dialog(context)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            // dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_logout_layout)
            val cancelBtnTxt = dialog.findViewById(R.id.cancelBtnTxt) as CPTextView
            val confirmBtnTxt = dialog.findViewById(R.id.confirmBtnTxt) as CPTextView

            confirmBtnTxt.setOnClickListener {
                dialog.dismiss()
                performLogout()
                OmnitureTrack.trackAction("logout:confirm")
            }
            cancelBtnTxt.setOnClickListener {
                dialog.dismiss()
                OmnitureTrack.trackAction("logout:cancel")
            }

            val window: Window = dialog.window!!
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialog.show()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


    /**
     * Methods loads lerner profile information from server and save it for further use.
     *
     * @param profileNetworkListener : NetworkListner listening for Network response.
     */
    fun callLearnerProfileApi(profileNetworkListener: NetworkListener) {
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
            profileNetworkListener,
            null
        )
        networkHandler.execute()
    }

    private fun callCourseEmailCountApi() {
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val authHeader = NetworkHandler.getAuthorizationHeader()
        val hubbleHeaders = NetworkHandler.getHeadersForHubbleRequest(
            loginBean?.authData?.token,
            HubbleNetworkConstants.getAcceptHeaderValue(HubbleNetworkConstants.COURSE_EMAIL_COUNT)
        )

        val finalHeaders = NetworkHandler.meargeHeaders(authHeader, hubbleHeaders)

        val params = HashMap<String, Any>()

        val finalUrl = HubbleNetworkConstants.getUrl(
            HubbleNetworkConstants.COURSE_EMAIL_COUNT,
            "{{employeeId}}",
            "" + loginBean?.authData?.employeeId!!.value
        )


        Util.trace("course email count url", finalUrl)
        val networkHandler = NetworkHandler(
            this,
            finalUrl,
            params,
            NetworkHandler.METHOD_GET,
            courseEmailCountListener,
            finalHeaders
        )

        networkHandler.setSilentMode(true)
        networkHandler.execute()
    }

    private val courseEmailCountListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {
            Util.trace("email count  first :  " + response.first)
            Util.trace("email count  second :  " + response.second)

            if (response.first == NetworkConstants.SUCCESS) {

                OmnitureTrack.trackAction("menu:course email")
                val gson = Gson()
                val emailCount = gson.fromJson<EmailCountBean>(
                    response.second.toString(),
                    EmailCountBean::class.java
                )
                emailCount.unreadCount?.let { setUnreadEmailCount(it) }
                setDrawerItem(isLearner)
                setAdapter()
                setDrawerItem(isLearner)

//                    updateEmailCount(emailCount)
            } else {
               /* OmnitureTrack.trackAction("menu:course email")
                DialogUtils.showGenericErrorDialog(this@MenuActivity)*/
            }
        }
    }

    private var unreadEmailCount = 0
    private fun setUnreadEmailCount(count: Int) {
        unreadEmailCount = count
    }

    fun getUnreadEmailCount():Int{
        return unreadEmailCount
    }
//    private fun updateEmailCount(emailCount: EmailCountBean) {
//        val isEmailExist = drawerItem.contains(resources.getString(R.string.capella_email))
//        if(isEmailExist){
//            drawerItem.removeAt(6)
//            drawerItem.add(6,resources.getString(R.string.capella_email) + " " + emailCount.unreadCount)
//            drawerAdapter?.notifyDataSetChanged()
//        }
//    }

    fun loadProfileImage(logoImageListener  :NetworkListener )
    {
        try {
            val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
            val gson = Gson()
            var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)
            val params = HashMap<String, Any>()
            params["employeeId"] = "" + loginBean.authData?.employeeId?.value
            params["token"] = "" + loginBean.authData?.token

            val networkHandler = NetworkHandler(
                this,
                NetworkConstants.PROFILE_IMAGE_API,
                params,
                NetworkHandler.METHOD_READ_FILE,
                logoImageListener,
                null
            )
            networkHandler.execute()
        }catch (t: Throwable){
            Util.trace("Logo load error : $t" )
            t.printStackTrace()
        }
    }


}
