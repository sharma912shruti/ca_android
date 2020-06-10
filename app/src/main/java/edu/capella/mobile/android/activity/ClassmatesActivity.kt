package edu.capella.mobile.android.activity

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Pair
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.adapters.ClassMatesListAdapter
import edu.capella.mobile.android.bean.ClassmatesListBean
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import edu.capella.mobile.android.network.NetworkService
import kotlinx.android.synthetic.main.activity_classmates.*
import kotlinx.android.synthetic.main.toolbar_generic.*
import kotlinx.android.synthetic.main.toolbar_generic.view.*

import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ClassmatesActivity : MenuActivity() /*BaseActivity()*/ ,  ConnectivityReceiver.ConnectivityReceiverListener,
    NetworkListener {


    var mateList:   ArrayList<ClassmatesListBean.ClassmatesData.CourseMember.Member?> = ArrayList()
    var mateListAdapter: ClassMatesListAdapter? = null

    var courseId = ""
    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    private  var isInternetConnection: Boolean=false

    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null


    fun getClassMatesListSize(): Int{
        try{
            return mateList.size
        }catch (t: Throwable)
        {
            return 0
        }
    }

    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*setContentView(R.layout.activity_classmates)*/
        setContentChildView(R.layout.activity_classmates, true)

        classMateToolbar.genericTitleTxt.text= getString(R.string.classmates)
        backButtonImg.setOnClickListener { finish() }
        classMateToolbar.setOnClickListener { finish() }
        initUi()

        backButtonLayout.contentDescription = getString(R.string.ada_back_button) + getString(R.string.back)

        OmnitureTrack.trackState("course:classmates:classmate-list")

    }

    /**
     * Method initializes tabs with container data and Look and fill.
     *
     */
    private fun initUi()
    {
        initializeAdapter()


        //callClassmatesApi("TEST-FPX7001_007535_2_1201_OEE_03")
        courseId = intent.getStringExtra(Constants.COURSE_ID)
        classMateListSwipeToRefresh.setColorSchemeColors( getColor(R.color.checkBoxColor))
//        callClassmatesApi(courseId)

        classMateListSwipeToRefresh.setOnRefreshListener {
            callClassmatesApi(courseId)
            classMateListSwipeToRefresh.isRefreshing = false
        }
    }

    private fun initializeAdapter() {
        classmatesListView.layoutManager = LinearLayoutManager(this)


        mateListAdapter = ClassMatesListAdapter(this, mateList , object : ClassMatesListAdapter.MatesItemListener{
            override fun onItemClicked(value: ClassmatesListBean.ClassmatesData.CourseMember.Member?)
            {

                //OmnitureTrack.trackAction("Classmates:opening-classmate-details")
                showMatesDetail(value?.employeeId!! , value?.profileImage!!)

            }

        })
        classmatesListView.adapter = mateListAdapter
    }

    private fun showMatesDetail(empId: String, profileUrl:String)
    {

        var matesDetailIntent = Intent(this, ClassmateDetailActivity::class.java)
        matesDetailIntent.putExtra(NetworkConstants.EMP_ID , empId )
        matesDetailIntent.putExtra(NetworkConstants.PROFILE_IMAGE_URL , profileUrl )
        matesDetailIntent.putExtra(Constants.TITLE,resources.getString(R.string.classmates))
        startActivity(matesDetailIntent)
    }
    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun initNetworkBroadcastReceiver()
    {

        ConnectivityReceiver.connectivityReceiverListener = this@ClassmatesActivity
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
        callClassmatesApi(courseId)

    }

    /**
     * Factory method of activity, executes when activity goes in paused state.
     *
     */
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
                callClassmatesApi(courseId)
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
    private fun callClassmatesApi(courseId: String)
    {

        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val params = HashMap<String, Any>()
        params.put(NetworkConstants.TOKEN, loginBean?.authData?.token!!)

        val qStringParams = HashMap<String, Any>()
        qStringParams.put(NetworkConstants.COURSE_ID, courseId)
        qStringParams.put(NetworkConstants.NO_BASE_64, true)

        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.CLASSMATES_API + NetworkService.getQueryString(qStringParams),
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

        Util.trace("ClassMates : " + response.second )

        if(response.first == NetworkConstants.SUCCESS)
        {
            val gson = Gson()
            var classMatesListBean = gson.fromJson<ClassmatesListBean>(response.second.toString(), ClassmatesListBean::class.java)
            updateClassMatesList(classMatesListBean)



        }else
        {
            DialogUtils.showGenericErrorDialog(this)
        }
    }


    private fun updateClassMatesList(classMatesListBean: ClassmatesListBean)
    {
        try {

            val matesListBean = classMatesListBean

            if (matesListBean?.errorData != null) {

                DialogUtils.showGenericErrorDialog(this)
                return
            }

            // if(matesListBean?.classmatesData?.courseMembers!![0]!!.member!!.size!! <= 0)
            if ((matesListBean?.classmatesData?.courseMembers!![0]!!.member == null) ||
                (matesListBean?.classmatesData?.courseMembers!![0]!!.member!!.size!! <= 0)
            ) {
                noClassmatesLayout.visibility = View.VISIBLE
                return
            }


            var listNew: ArrayList<ClassmatesListBean.ClassmatesData.CourseMember.Member?> =
                matesListBean?.classmatesData?.courseMembers!![0]!!.member as ArrayList<ClassmatesListBean.ClassmatesData.CourseMember.Member?>
            var userIdIndex:Int?=null
            for (i in 0 ..listNew.size)
            {
               if( listNew[i]!!.employeeId.equals(Preferences.getValue(PreferenceKeys.USER_ID)))
               {
                   userIdIndex= i;
                   break
               }

            }
            if(userIdIndex!=null)
            {
                listNew.removeAt(userIdIndex)
            }

          val  newSortedList=  listNew
                .sortedWith(compareBy<ClassmatesListBean.ClassmatesData.CourseMember.Member?> { it!!.firstName+""+it!!.lastName }
                    .thenBy{ it!!.firstName+""+" "+it!!.lastName })
            mateList.clear()
            mateList.addAll(newSortedList)





            if (mateList.size > 0) {
                if (mateListAdapter != null) {
                    mateListAdapter!!.notifyDataSetChanged()
                    // noInfoTxt.visibility = View.GONE
                }
            } else {
                noClassmatesLayout.visibility = View.VISIBLE
            }
        }catch (t: Throwable)
        {
            Util.trace("Classmate data error : $t")
            t.printStackTrace()
        }



    }


}
