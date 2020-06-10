package edu.capella.mobile.android.activity

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.adapters.CourseSyllabusListAdapter
import edu.capella.mobile.android.bean.CourseSyllabusBean
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.HubbleNetworkConstants
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import kotlinx.android.synthetic.main.activity_course_syllabus.*
import kotlinx.android.synthetic.main.activity_library.networkLayout
import kotlinx.android.synthetic.main.toolbar_generic.view.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CourseRoomSyllabusActivity: MenuActivity() /*BaseActivity()*/, ConnectivityReceiver.ConnectivityReceiverListener, NetworkListener {


    var courseID = "";
    var syllabusList: ArrayList<CourseSyllabusBean.CourseSyllabus.CourseSyllabusContents.CourseSyllabusContent?> = ArrayList()
    var syllabusListAdapter: CourseSyllabusListAdapter? = null
    var isFpPath:Boolean = false
    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    private  var isInternetConnection: Boolean=false


    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null


    /**
     *  Factory method of activity called when activity is created.
     *
     * @param savedInstanceState : Bundle used to restore variable values when activity restarted
     * due to some reason
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_course_syllabus)
        setContentChildView(R.layout.activity_course_syllabus,true)
        syllabusToolbar.genericTitleTxt.text =  getString(R.string.syllabus)
        syllabusToolbar.backButtonLayout.contentDescription = getString(R.string.ada_back_button) + getString(R.string.back)

        this.courseID = intent.getStringExtra(Constants.COURSE_ID)

        initUi()
        OmnitureTrack.trackState("course:syllabus")
    }


    fun getSyllabusListSize(): Int {
        try {
            return syllabusList.size
        } catch (t: Throwable) {
            return 0
        }
    }

    /**
     * Method initializes tabs with container data and Look and fill.
     *
     */
    private fun initUi() {
        try {
            initializeAdapter()
        } catch (e : IndexOutOfBoundsException){
            Util.trace("Syllabus error, $e")
            e.printStackTrace()
        }
        //callSyllabusListApi("TEST-FP6001_007575_1_1201_OEE_03")
        callSyllabusListApi(this.courseID)


        this.syllabusListSwipeToRefresh.setColorSchemeColors( getColor(R.color.checkBoxColor))

        // according to flex path show learner activity

        isFpPath = intent.getBooleanExtra(Constants.WHICH_FLEX_PATH,false)
        if(isFpPath){
            learnerActivitiesTitle.visibility = View.GONE
        } else{
            learnerActivitiesTitle.visibility = View.VISIBLE
        }

        syllabusListSwipeToRefresh.setOnRefreshListener {
            syllabusListSwipeToRefresh.isRefreshing = false
            callSyllabusListApi(this.courseID)
        }
    }

    private fun initializeAdapter() {
        syllabusListView.layoutManager = LinearLayoutManager(this)

        syllabusListAdapter = CourseSyllabusListAdapter(this, syllabusList , object :
            CourseSyllabusListAdapter.SyllabusItemListener {
            override fun onItemClicked(value: CourseSyllabusBean.CourseSyllabus.CourseSyllabusContents.CourseSyllabusContent?) {
//                    Util.showToastForUnderDevelopment(this@CourseRoomSyllabusActivity)
                OmnitureTrack.trackState("course:syllabus-detail")
                val studyDetailIntent = Intent(this@CourseRoomSyllabusActivity,CommonDetailActivity::class.java)
                studyDetailIntent.putExtra(Constants.CONTENT,value?.content)
                studyDetailIntent.putExtra(Constants.CONTENT_TITLE,value?.title)
                studyDetailIntent.putExtra(Constants.TITLE,resources.getString(R.string.syllabus))
                studyDetailIntent.putExtra(Constants.SHOW_WARING,false)
                this@CourseRoomSyllabusActivity.startActivity(studyDetailIntent)
            }

        })
        syllabusListView.adapter = syllabusListAdapter
    }

   /* private fun showSyllabusDetails(contId: String)
    {

        var matesDetailIntent = Intent(this, ClassmateDetailActivity::class.java)
        matesDetailIntent.putExtra(NetworkConstants.EMP_ID , empId )
        matesDetailIntent.putExtra(NetworkConstants.PROFILE_IMAGE_URL , profileUrl )
        startActivity(matesDetailIntent)
    }*/

    private fun callSyllabusListApi(courseId: String)
    {

        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val authHeader = NetworkHandler.getAuthorizationHeader()
        val hubbleHeaders = NetworkHandler.getHeadersForHubbleRequest(loginBean?.authData?.token,
            HubbleNetworkConstants.getAcceptHeaderValue(HubbleNetworkConstants.COURSES_SYLLABUS_LIST))

        val finalHeaders = NetworkHandler.meargeHeaders(authHeader, hubbleHeaders)

        var syllabusUrl = HubbleNetworkConstants.getUrl(
            HubbleNetworkConstants.COURSES_SYLLABUS_LIST,
            "{{employeeId}}",
            "" + loginBean?.authData?.employeeId!!.value
        )

        syllabusUrl = syllabusUrl + "/" + this.courseID


        Util.trace("Syllabus URL  :"  + syllabusUrl)

        val params = HashMap<String, Any>()



        val networkHandler = NetworkHandler(
            this,
            syllabusUrl,
            params,
            NetworkHandler.METHOD_GET,
            this,
            finalHeaders
        )

        networkHandler.execute()


    }

    /**
     * Method Registers broadcast receiver used for tracking Internet connectivity.
     *
     */
    private fun initNetworkBroadcastReceiver()
    {

        ConnectivityReceiver.connectivityReceiverListener = this@CourseRoomSyllabusActivity
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
    }

    /**
     * Method triggered by broadcast receiver which is listening for Network state.
     *
     * @param isConnected : Tells whether internet is available or not
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {


      if(isConnected == true && isNetworkFailedDueToConnectivity!=null && isNetworkFailedDueToConnectivity==true)
        {
            isNetworkFailedDueToConnectivity = null
            if(this.getCurrentNetworkQueueSize()==0) {
                DialogUtils.releaseGenericDialog()
                callSyllabusListApi(this.courseID)
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


    override fun onNetworkResponse(response: android.util.Pair<String, Any>)
    {
        try{
        Util.trace("Syllabus : " + response.second )

        if(response.first == NetworkConstants.SUCCESS)
        {
            val gson = Gson()
            var courseSyllabusBean = gson.fromJson<CourseSyllabusBean>(response.second.toString(), CourseSyllabusBean::class.java)
            updateSyllabusList(courseSyllabusBean)



        }else
        {
            DialogUtils.showGenericErrorDialog(this)
        }
        }catch (e:Exception){
            e.printStackTrace()
            DialogUtils.showGenericErrorDialog(this)
        }
    }

    private fun updateSyllabusList(courseSyllabusBean: CourseSyllabusBean?) {

        try {

            val syllabusListBean = courseSyllabusBean
            // if(matesListBean?.classmatesData?.courseMembers!![0]!!.member!!.size!! <= 0)
            if ((syllabusListBean?.courseSyllabus?.courseSyllabusContents?.courseSyllabusContent == null) ||
                (syllabusListBean?.courseSyllabus?.courseSyllabusContents?.courseSyllabusContent!!.isEmpty())
            ) {
                noSyllabusLayout.visibility = View.VISIBLE
                return
            }

            var listNew: ArrayList<CourseSyllabusBean.CourseSyllabus.CourseSyllabusContents.CourseSyllabusContent> =
                syllabusListBean?.courseSyllabus?.courseSyllabusContents?.courseSyllabusContent as ArrayList<CourseSyllabusBean.CourseSyllabus.CourseSyllabusContents.CourseSyllabusContent>
            // todo remove ignore title
            var listWithIgnoredItem = finalListTOShow(listNew)

//           var newSortedList = listWithIgnoredItem.sortedWith(compareBy(CourseSyllabusBean.CourseSyllabus.CourseSyllabusContents.CourseSyllabusContent::title, CourseSyllabusBean.CourseSyllabus.CourseSyllabusContents.CourseSyllabusContent::content))

            syllabusList.clear()
           // syllabusList.addAll(listNew)
            syllabusList.addAll(listWithIgnoredItem)


            if (syllabusList.size > 0) {

                courseSyllabusListLayout.visibility = View.VISIBLE

                if (syllabusListAdapter != null) {
                    syllabusListAdapter!!.notifyDataSetChanged()
                    // noInfoTxt.visibility = View.GONE
                }
            } else {
                noSyllabusLayout.visibility = View.VISIBLE
            }
        }catch (t: Throwable)
        {
            Util.trace("Syllabus data error : $t")
            t.printStackTrace()
        }
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

    private fun finalListTOShow(listNew: ArrayList<CourseSyllabusBean.CourseSyllabus.CourseSyllabusContents.CourseSyllabusContent>):ArrayList<CourseSyllabusBean.CourseSyllabus.CourseSyllabusContents.CourseSyllabusContent>{
        val finalList : ArrayList<CourseSyllabusBean.CourseSyllabus.CourseSyllabusContents.CourseSyllabusContent> = ArrayList()
        val ignoreList = getTitleToAddInList()
        for (index in 0 until listNew.size){
            val title = listNew[index].title
            var isMatch = false
            for (position in 0 until ignoreList.size){
                if(title.equals(ignoreList[position],true)){
                    isMatch = true
                    break
                }
            }
            if(isMatch){
                finalList.add(listNew[index])
            }
        }

        return finalList
    }

    private fun getTitleToAddInList():ArrayList<String>{
        var listToAdd:ArrayList<String> = ArrayList()

        if(isFpPath){
            listToAdd.add("Course Overview")
            listToAdd.add("Course Competencies")
            listToAdd.add("Scoring Guides")
            listToAdd.add("Learner Expectations")
            listToAdd.add("Disability Services Statement")
            listToAdd.add("APA Style Central")
        }else{
            listToAdd.add("Course Overview")
            listToAdd.add("Prerequisites")
            listToAdd.add("Grading")
            listToAdd.add("Final Course Grade")
            listToAdd.add("Course Materials")
            listToAdd.add("APA Style Central")
        }

        return listToAdd
    }
}
