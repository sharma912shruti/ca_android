package edu.capella.mobile.android.activity


import android.content.IntentFilter
import android.os.Bundle
import android.util.Pair
import android.view.View
import com.google.gson.Gson
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*

import edu.capella.mobile.android.bean.LearnerInformation
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler

import kotlinx.android.synthetic.main.classmate_detail.*
import kotlinx.android.synthetic.main.classmate_detail.networkLayout
import kotlinx.android.synthetic.main.toolbar_generic.view.*


class ClassmateDetailActivity : MenuActivity()/*BaseActivity()*/ , NetworkListener,  ConnectivityReceiver.ConnectivityReceiverListener {

    lateinit var picLoader : PictureLoader

    var empId = ""
    var profileUrl = ""


    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    private  var isInternetConnection: Boolean=false

    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        /*setContentView(R.layout.classmate_detail)*/
        setContentChildView(R.layout.classmate_detail,true)
        classMateToolbar.backHeaderTxt.setOnClickListener { finish() }
        if(intent.getStringExtra(Constants.TITLE) == resources.getString(R.string.classmates) ) {
            classMateToolbar.genericTitleTxt.visibility = View.GONE
            classMateToolbar.backHeaderTxt.text= getString(R.string.classmates)
            classMateToolbar.backButtonLayout.contentDescription = getString(R.string.ada_back_button) + getString(R.string.classmates)

            //classMateToolbar.backHeaderTxt.contentDescription = getString(R.string.ada_back_button) + getString(R.string.classmates)

            OmnitureTrack.trackState("course:classmates:classmate-profile")
        }else{
            classMateToolbar.genericTitleTxt.visibility = View.VISIBLE
            classMateToolbar.backHeaderTxt.text= getString(R.string.back)

            classMateToolbar.backButtonLayout.contentDescription = getString(R.string.ada_back_button) + getString(R.string.back)
            OmnitureTrack.trackState("course:instructor-profile")
            //classMateToolbar.backHeaderTxt.contentDescription = getString(R.string.ada_back_button) + getString(R.string.back)
        }



//        classMateToolbar.genericTitleTxt.text= getString(R.string.classmates)

        picLoader = PictureLoader(this)
        picLoader.clearCache()

        this.empId = intent.getStringExtra(NetworkConstants.EMP_ID)
        this.profileUrl = intent.getStringExtra(NetworkConstants.PROFILE_IMAGE_URL)
        classMateToolbar.genericTitleTxt.text = intent.getStringExtra(Constants.TITLE)


        classmateEmailTxt.setOnClickListener{

            Util.openEmail(this@ClassmateDetailActivity , classmateEmailTxt.text.toString())
        }


        mateDetailSwipeToRefresh.setColorSchemeColors( getColor(R.color.checkBoxColor))


        mateDetailSwipeToRefresh.setOnRefreshListener {
            picLoader.clearCache()
            callProfileDetailApi()
            mateDetailSwipeToRefresh.isRefreshing = false

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
            NetworkConstants.LEARNER_PROFILE_API + "&" + NetworkConstants.EMP_ID +"=" +this.empId ,
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
            if (response.first == NetworkConstants.SUCCESS) {
                val gson = Gson()
                val learnerBean = gson.fromJson<LearnerInformation>(
                    response.second.toString(),
                    LearnerInformation::class.java
                )
                // Util.trace("learnerBean sting  :  " + learnerBean.toString())

                if (learnerBean.errorData == null) {
                    // Preferences.addValue(PreferenceKeys.LEARNER_PROFILE , response.second.toString())
                    updateDetails(learnerBean)
                } else {
                    mateTroubleTxt.visibility = View.VISIBLE
                    DialogUtils.showGenericErrorDialog(this)

                }
            } else {
                mateTroubleTxt.visibility = View.VISIBLE
                DialogUtils.showGenericErrorDialog(this)

            }
        }catch (t: Throwable)
        {
             DialogUtils.showGenericErrorDialog(this)


        }
    }

    private fun updateDetails(learnerBean : LearnerInformation)
    {

        picLoader.displayImage(this.profileUrl , classmateSmallLogo)

        if(learnerBean?.profileLearnerData?.profile?.firstName == null ||  learnerBean?.profileLearnerData?.profile?.lastName== null)
        {
            mateTroubleTxt.visibility = View.VISIBLE
            return
        }
        mateTroubleTxt.visibility = View.GONE

        matesDetailParentLayout.visibility = View.VISIBLE

        classmateName.text =
            learnerBean?.profileLearnerData?.profile?.firstName + " " + learnerBean?.profileLearnerData?.profile?.lastName
        classmateAddress.text =
            "" + learnerBean?.profileLearnerData?.profile?.city + ", " + learnerBean?.profileLearnerData?.profile?.state

        classmateEmailTxt.text = "" + learnerBean?.profileLearnerData?.profile?.emailAddress!!.toLowerCase()


        if(learnerBean?.profileLearnerData?.profile?.aboutMe != null && learnerBean?.profileLearnerData?.profile?.aboutMe!!.isNotEmpty())
        {
            classmateAboutLayout.visibility =  View.VISIBLE
            classmateAboutText.text = learnerBean?.profileLearnerData?.profile?.aboutMe.toString()
        }else
        {
            classmateAboutLayout.visibility =  View.GONE
        }

      //  matesDetailLayout.visibility = View.VISIBLE
    }


    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun initNetworkBroadcastReceiver()
    {

        ConnectivityReceiver.connectivityReceiverListener = this@ClassmateDetailActivity
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
        initNetworkBroadcastReceiver()
        callProfileDetailApi( )
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
                picLoader.clearCache()
                callProfileDetailApi()
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

    override fun onPause() {
        super.onPause()
        releaseConnectivityReceiver()
    }



    private fun releaseConnectivityReceiver()
    {
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver)
            ConnectivityReceiver.connectivityReceiverListener=null
        }
    }
}
