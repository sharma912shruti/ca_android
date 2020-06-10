package edu.capella.mobile.android.activity.tabs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig

import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.activity.CourseRoomMenuActivity
import edu.capella.mobile.android.adapters.CourseListAdapter
import edu.capella.mobile.android.bean.*
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import edu.capella.mobile.android.network.NetworkService
import edu.capella.mobile.android.task.StickyInfoGrabber

import kotlinx.android.synthetic.main.fragment_current_course_tab.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class CurrentCourseTab : Fragment(), NetworkListener {


    lateinit var activityContext: Context

    var courseList: ArrayList<CourseListBean.NewCourseroomData.CurrentCourseEnrollment?> =
        ArrayList()
    var courseListAdapter: CourseListAdapter? = null
    var isApiCalled:Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    fun getCurrentCourseAdapter() : CourseListAdapter?
    {
        return courseListAdapter
    }

    fun getCurrentCourseSize(): Int {
        return courseList.size
    }

    fun getFpCourseIndex():Int{
        var fpIndex = 0
        for (index in 0 until courseList.size){
            if (courseList[index]?.flexpathCourse!!){
                fpIndex = index
                break
            }
        }

        return fpIndex
    }

    fun getGpCOurseIndex():Int {
        var gpIndex = 0
        for (index in 0 until courseList.size){
            if (!courseList[index]?.flexpathCourse!!){
                gpIndex = index
                break
            }
        }

        return gpIndex
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_current_course_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

    }

    private fun initViews() {

        initializeAdapter()
        callCurrentCourseApi()
        initialListenerSetUp()

    }

    /**
     *  Method responsible to refresh Announcement API
     */
    private fun initialListenerSetUp() {
        val swipeRefreshColor =  ContextCompat.getColor(activity!!,R.color.checkBoxColor)

        this.currentCourseRefreshView.setColorSchemeColors(swipeRefreshColor)
        this.currentCourseRefreshView.setOnRefreshListener {
            callCurrentCourseApi()
            currentCourseRefreshView.isRefreshing = false
        }

    }

    private fun initializeAdapter() {
        currentCourseListView.layoutManager = LinearLayoutManager(activityContext)
        courseListAdapter =
            CourseListAdapter(
                activityContext,
                courseList,
                object : CourseListAdapter.CourseItemListener {
                    override fun onItemClicked(newsitem: CourseListBean.NewCourseroomData.CurrentCourseEnrollment?) {
                        openCourseRoomMenuScreen(newsitem)
                    }
                })

        currentCourseListView.adapter = courseListAdapter

    }

    fun openCourseRoomMenuScreen(newsItem: CourseListBean.NewCourseroomData.CurrentCourseEnrollment?) {
        if(newsItem?.courseSection?.course?.subject.equals(Constants.FPO_COURSE)){
            checkCourseLinkAndCallApi(newsItem)
        } else {
            val intent = Intent(context, CourseRoomMenuActivity::class.java)
            intent.putExtra(Constants.COURSE_DATA, newsItem)
            intent.putExtra(Constants.COURSE_TYPE, Constants.CURRENT)
            startActivity(intent)
        }
    }

    private fun checkCourseLinkAndCallApi(newsItem: CourseListBean.NewCourseroomData.CurrentCourseEnrollment?) {
        if(newsItem?.courseLink != null){
            openCourseLink(newsItem.courseLink.toString())
        }else{
            callApiToGetCourseDetail(newsItem)
        }
    }

    private fun openCourseLink(courseLink:String){
        val stickyWork = StickyInfoGrabber(this.context!!)
        stickyWork.generateMuleSoftStickySessionForTargetUrl(
            courseLink,
            BuildConfig.STICKY_FORWARD_URL
        )
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activityContext = context


    }

    override fun onDetach() {
        super.onDetach()

    }

     fun callCurrentCourseApi() {
        isApiCalled = true
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)


        val queryString = HashMap<String, Any>()
        queryString.put(NetworkConstants.ACTION, "currentCourses")
        queryString.put(NetworkConstants.NO_BASE_64, true)
        queryString.put(NetworkConstants.INCLUDE_INSTRUCTOR_PROFILE, true)


        val params = HashMap<String, Any>()
        params.put(NetworkConstants.TOKEN, loginBean?.authData?.token!!)

        val networkHandler = NetworkHandler(
            this.activityContext,
            NetworkConstants.COURSES_API + NetworkService.getQueryString(queryString),
            params,
            NetworkHandler.METHOD_POST,
            this,
            null
        )

        networkHandler.execute()
    }


    override fun onNetworkResponse(response: Pair<String, Any>) {
        try {
            Util.trace("CURRENT COURSE first" + response.first)
            if (response.first == NetworkConstants.SUCCESS) {
                val gson = Gson()
                val coursesListBean = gson.fromJson<CourseListBean>(
                    response.second.toString(),
                    CourseListBean::class.java
                )


                if (coursesListBean.errorData != null) {
                    DialogUtils.showGenericErrorDialog(activityContext)
                    return
                }


                if (coursesListBean.newCourseroomData?.currentCourseEnrollments == null) {
                    currentNoCourseLayout.visibility = View.VISIBLE
                    return
                }


                var listNew: List<CourseListBean.NewCourseroomData.CurrentCourseEnrollment> =
                    coursesListBean.newCourseroomData?.currentCourseEnrollments as List<CourseListBean.NewCourseroomData.CurrentCourseEnrollment>

                order(listNew)
//                var newSortedList = listNew.sortedWith(compareBy(CourseListBean.NewCourseroomData.CurrentCourseEnrollment.CourseSection::startDate, CourseListBean.NewCourseroomData.CurrentCourseEnrollment.CourseSection.Course::title))

                courseList.clear()
                courseList.addAll(listNew)


                if (courseList.size > 0) {
                    OmnitureTrack.trackAction("courses:courses-current")
                    if (courseListAdapter != null) {
                        courseListAdapter!!.notifyDataSetChanged()
                        // noInfoTxt.visibility = View.GONE
                    }
                } else {
                    OmnitureTrack.trackAction("courses:courses-no-active-current")
                    currentNoCourseLayout.visibility = View.VISIBLE
                }


            } else {
                DialogUtils.showGenericErrorDialog(
                    activityContext
                )
            }
            isApiCalled = false
        } catch (t: Throwable) {
            Util.trace("Serious error : " + t.toString())

        }

    }

    private fun order(courseList: List<CourseListBean.NewCourseroomData.CurrentCourseEnrollment>) {

        Collections.sort(courseList,object :Comparator<CourseListBean.NewCourseroomData.CurrentCourseEnrollment>{

            override fun compare(courseListBean: CourseListBean.NewCourseroomData.CurrentCourseEnrollment?,
                                 secondCourseBean: CourseListBean.NewCourseroomData.CurrentCourseEnrollment?): Int {

//                val x1: Long = courseListBean?.courseSection?.startDate!!
//                val x2: Long = secondCourseBean?.courseSection?.startDate!!
//
//                val sComp =  x2.compareTo(x1)
//                if (sComp != 0) {
//                    return sComp
//                }

                val list1: String = courseListBean?.courseSection?.course?.title!!
                val list2: String = secondCourseBean?.courseSection?.course?.title!!
                return list1.compareTo(list2)
            }

        })
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if(menuVisible){
               if(!isApiCalled){
                   callCurrentCourseApi()
               }
        }
    }


    /**
     * callApiToGetCourseDetail(): method to call course detail api
     *
     * */
    private fun callApiToGetCourseDetail(newsItem: CourseListBean.NewCourseroomData.CurrentCourseEnrollment?) {
        val loginInfo = Preferences.getValue(PreferenceKeys.LOGIN_INFO)
        val gson = Gson()
        var loginBean = gson.fromJson<LoginBean>(loginInfo, LoginBean::class.java)

        val params = HashMap<String, Any>()
        params[NetworkConstants.TOKEN] = loginBean?.authData?.token!!
        val userEmployeeId = loginBean?.authData?.employeeId!!.value!!

        val qStringParams = HashMap<String, Any>()
        qStringParams[NetworkConstants.COURSE_ID] = newsItem?.courseIdentifier!!
        qStringParams[NetworkConstants.ACTION] = NetworkConstants.ACTION_GET_COURSE_DETAIL
        qStringParams[NetworkConstants.IS_FACULTY] = newsItem.faculty!!
        qStringParams[NetworkConstants.FACULTY_ROLE] = newsItem.facultyRole ?: "null"
        qStringParams[NetworkConstants.IS_FLEX_PATH_COURSE] = newsItem.flexpathCourse!!

        val networkHandler = NetworkHandler(
            this.context!!,
            NetworkConstants.COURSE_DETAIL_API + NetworkService.getQueryString(qStringParams),
            params,
            NetworkHandler.METHOD_POST,
            courseDetailListener,
            null
        )
        networkHandler.isPostTypeSubmitting = true
        networkHandler.submitMessage = ""
        networkHandler.execute()
    }


    private val courseDetailListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>) {
            try {

                Util.trace("course room first :  " + response.first)
                Util.trace("course room second :  " + response.second)
                // TODO handle course detail api responseset
                if (response.first == NetworkConstants.SUCCESS) {

                    val gson = Gson()
                    val courseDetailBean = gson.fromJson<CourseDetailBean>(
                        response.second.toString(),
                        CourseDetailBean::class.java
                    )

                    openCourseLink(courseDetailBean!!.newCourseroomData!!.courseDetails!!.courseLink.toString())

                } else {
                    DialogUtils.showGenericErrorDialog(activityContext)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}

